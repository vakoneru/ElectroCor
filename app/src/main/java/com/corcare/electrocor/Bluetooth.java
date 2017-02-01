package com.corcare.electrocor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Avinash on 10/12/2015.
 */
public class Bluetooth {
    static Handler handler;
    static BluetoothSocket socket;
    static BluetoothDevice mmDevice;
    public static InputStream mmInStream;
    public static OutputStream mmOutStream;

    static ArrayList<Integer> electrode1 = new ArrayList<Integer>();
    static ArrayList<Integer> electrode2 = new ArrayList<Integer>();
    static ArrayList<Integer> electrode3 = new ArrayList<Integer>();

    static File filecurrentFileName = null;
    static String filecurrentFileString;

    public static void parseString(String s)
    {

        String temp;
        String[] tokens;

        String pattern = "#[0-9]+,[0-9]+,[0-9]+";
        //String pattern = "#[0-9]{1,4},[0-9]{1,4},[0,9]{1,4}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);



        while(m.find()) {
            temp = s.substring(m.start()+1,m.end());    //takes each matched substring; use +1 b/c do not want the '#' symbol
            tokens = temp.split(","); //substring still has commas separating it, so split it into a bunch of toekns

            //add each token to the corresponding electrode
            electrode1.add(Integer.parseInt(tokens[0]));
            electrode2.add(Integer.parseInt(tokens[1]));
            electrode3.add(Integer.parseInt(tokens[2]));
        }
    }
    //External Storage -------------------------------------------------------------------
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static BufferedWriter getFileWriter(String docName) {
        // Get the directory for the user's public pictures directory.
        File path = new File(Environment.getExternalStorageDirectory(), "ElectroCorTest Data");

        if (!path.mkdirs()){
            Log.e("External Storage Issue", "Directory exists");
        }
        File file = new File(path,docName);
        //check to see if file w/ that name already exists
        int indexOfPeriod = docName.indexOf('.');
        if(file.exists())
        {
            for(int i = 1;i<1000;i++)
            {
                file = new File(path,docName.substring(0,indexOfPeriod)+Integer.toString(i)+".txt");
                if(!file.exists()){
                    filecurrentFileName = new File(path,docName.substring(0,indexOfPeriod)+Integer.toString(i)+".txt");
                    filecurrentFileString = filecurrentFileName.toString();
                    break;
                }
            }
        } else {
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file,true));
        }
        catch(IOException e){e.printStackTrace();}

        return writer;
    }


    //End External Storage -----------------------------------------------------------------------
    //inner class that works in the background
    static class ReadThread extends Thread{
        public static boolean pauseThread = false;
        public static int numStarted = 0;
        //BufferedWriter bw = getFileWriter("ECG_DirectWrite.txt");
        BufferedWriter bw;

        @Override
        public void run(){
            manageConnectedSocket(socket);
        }

        public void manageConnectedSocket(BluetoothSocket socket) {
            //BufferedWriter bw = getFileWriter("ECG_DirectWrite");
            if(bw == null)
            {
                bw = getFileWriter("ECG_DirectWrite.txt");
            }
            ArrayList<Integer> electrode1 = new ArrayList<Integer>();
            int MESSAGE_READ = 4;  //1-3 are reserved for electrodes
            int BUFFER_LENGTH = 10000;
            byte[] buffer = new byte[BUFFER_LENGTH];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                if(pauseThread == true && numStarted == 0) //initally stopped state
                {

                }
                else if(pauseThread == true && numStarted != 0) //started, then stopped
                {
                    try {
                        bw.close();
                        numStarted = 0;
                        bw = null;
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        if (true) {
                            //if (mmInStream.available() > (BUFFER_LENGTH / 2)) {
                            // Read from the InputStream
                            bytes = mmInStream.read(buffer);
                            String readMessage = new String(buffer, 0, bytes);
                            bw.write(readMessage);

                            // Send the obtained bytes to the UI activity
                            Message message = handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer);
                            message.sendToTarget();

                            parseString1(readMessage);
                            //averageECG1(readMessage);
                            Message msgElectrode1 = handler.obtainMessage(1, bytes, -1, Bluetooth.electrode1); //1 in parameter indicates "electrode 1"
                            msgElectrode1.sendToTarget();
                        }

                    } catch (IOException e) {
                        Log.d("Avi Message", "Received exception");
                        break;
                    }
                }
            }
        }
    }

    public static void parseString1(String s)
    {

        String temp;
        String[] tokens;

        tokens = s.split(",");

        for(int i=0;i<tokens.length;i++)
        {
            try {
                electrode1.add(Integer.parseInt(tokens[i]));
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void establishStreams(BluetoothDevice device)
    {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            Log.d("Avi", "After tmp");

        } catch (IOException e) {}
        socket = tmp;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public static void connectThroughSocket(){
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
            Log.d("Avi", "Device connected");


        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                socket.close();
            } catch (IOException closeException) {
            }
            return;
        }
    }

    public static void sendData(byte[] bytes){
        try {
            Log.d("Avi Message","Right Before Writing");
            mmOutStream.write(bytes);
            Log.d("Avi Message","Supposedly written");
        } catch (IOException e) { }
    }

    public static void setHandler(Handler handlerToSet){
        handler = handlerToSet;
    }

}

