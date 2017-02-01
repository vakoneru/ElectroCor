package com.corcare.electrocor;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by varunkoneru on 10/2/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class Dashboard_Fragment extends Fragment {

    View myView;

    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mbtAdapter = null;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private Button mButtonStart;
    private Button mButtonStop;
    private Button buttonSaveData;
    private TextView timerValue;
    private TextView mTextBTStatus;

    private int mCount = 0;
    private long startTime = 0L;
    long updatedTime = 0L;

    private Handler customHandler = new Handler();
    private Handler handler;
    public OutputStream mOutputStream;

    private boolean endTimer = false;

    private static final String TAG = "BluetoothTest";

    public OutputStream mOutStream;
    XYPlot plot;
    ArrayList<Integer> electrode1 = new ArrayList<Integer>();
    ArrayList<Integer> electrode2 = new ArrayList<Integer>();
    ArrayList<Integer> electrode3 = new ArrayList<Integer>();
    ArrayList<Integer> electrode1Test = new ArrayList<Integer>();

    int INITIAL_CAPACITY = 50;
    StringBuilder stringToSave = new StringBuilder(INITIAL_CAPACITY);

    private byte[] byteArray;
    protected File mFilePath;
    private ParseObject mSubmission;
    protected ParseFile mUpload;
    private boolean mThreadWorking = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.dashboard_layout, container, false);

        mTextBTStatus = (TextView) myView.findViewById(R.id.textBTStatus);
        timerValue = (TextView) myView.findViewById(R.id.timerValue);
        mButtonStart = (Button) myView.findViewById(R.id.buttonStart);
        mButtonStop = (Button) myView.findViewById(R.id.buttonStop);
        buttonSaveData = (Button) myView.findViewById(R.id.buttonConnectBTDevice);


        plot = (XYPlot) myView.findViewById(R.id.ecgPlot);
        plot.setRangeBoundaries(-1000, BoundaryMode.FIXED, 1000, BoundaryMode.FIXED);

        Handler handler;

        XYSeries electrode1Series = null;
        boolean stopRead = false;

        final Bluetooth.ReadThread readThread = new Bluetooth.ReadThread();

        /*-------------------------------------------------------------------------------*/
        //****BLUETOOTH STATUS
        checkBTStatus();

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*-------------------------------------------------------------------------------*/
                //****TIMER
                endTimer = false;
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                mCount++;
                if (mCount == 1) {
                    view.setEnabled(false);
                }
                /*-------------------------------------------------------------------------------*/
                //****BLUETOOTH
                //here we need to add the code that will start to save the data that is being received by the phone
                readThread.pauseThread = false;
                readThread.numStarted++;

                Toast toast = Toast.makeText(getActivity(), "Collection of data started", Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endTimer = true;
                readThread.pauseThread = true;

                //File mFile = readThread.getFile();


                //   byte[] ByteArray = new byte[(int) ]
                //readThread.numStarted = 0;
                //readThread.interrupt();
                Toast toast = Toast.makeText(getActivity(), "Collection of data stopped", Toast.LENGTH_SHORT);
                toast.show();
//                if (!mThreadWorking) {
//                    new SaveFileThread().execute("");
//                }

//                plot.clear();
//                plot.setRangeBoundaries(0, BoundaryMode.FIXED, 1023, BoundaryMode.FIXED);
//                XYSeries electrode1Series = new SimpleXYSeries(electrode1Test.subList(0,500), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"ECG");
//                LineAndPointFormatter formatter = new LineAndPointFormatter(null, Color.RED,null,null);
//                plot.addSeries(electrode1Series, formatter);
//                plot.redraw();
//                Log.d("Avi", electrode1.toString());
            }
        });

        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("String to Save", stringToSave.toString());
                //writeFile("Avi_test_1",stringToSave.toString());
                //textView.setText(stringToSave.toString());
                Toast toast = Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_SHORT);
                toast.show();

                parseString(stringToSave.toString());
            }
        });

        handler = new Handler() {
            //String filename = "ECG_Data5";
            //FileOutputStream outputStream;
            @Override
            public void handleMessage(Message msg) {
                int electrode1TestLength = 0;
                int counter = 0;
                if (msg.what == 4) {
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    //String writeMessage = (String) msg.obj;
                    stringToSave.append(writeMessage);
                }
                if(msg.what == 1) {
                    Log.d("Avi", "In handler: electrode 1");
                    electrode1Test.addAll((ArrayList<Integer>) msg.obj);
                    electrode1TestLength = electrode1Test.size();
                    Log.d("Value of counter",Integer.toString((int)counter));
                    Log.d("Electrode 1 Test length", Integer.toString(electrode1TestLength));}
            }
        };


        //Bluetooth code
        BluetoothAdapter mBluetoothAdapter = enable(); //enable Bluetooth and return the adapter
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("20:15:06:25:57:60");
        Log.d("Avi Message", "Past receiving device");
        Bluetooth.establishStreams(device);
        Bluetooth.connectThroughSocket();
        Bluetooth.setHandler(handler);
        readThread.start();
        readThread.pauseThread = true; //make sure thread doesn't start until user starts it
        //Bluetooth.ReadThread readThread = new Bluetooth.ReadThread();
        //readThread.start();

        //End Bluetooth code

        return myView;
    }

    /*-------------------------------------------------------------------------------*/
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (endTimer == false) {
                updatedTime = SystemClock.uptimeMillis() - startTime;
                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                int hrs = mins / 60;
                secs = secs % 60;
                int milliseconds = (int) (updatedTime % 1000);
                timerValue.setText(String.format("%02d", hrs) + ":"
                        + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%03d", milliseconds));
                customHandler.postDelayed(this, 0);
            }
            customHandler.removeCallbacksAndMessages(updateTimerThread);
        }
    };

    private void checkBTStatus() {
        mbtAdapter = BluetoothAdapter.getDefaultAdapter();
        //check the state of the bluetooth
        if (mbtAdapter == null) {
            Toast.makeText(getActivity(), "This phone does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        } else if (mbtAdapter.isEnabled()) {
            Log.d(TAG, "...Bluetooth is enabled...");

            Toast.makeText(getActivity(), "Bluetooth is enabled!", Toast.LENGTH_LONG).show();
        } else {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG, "...Bluetooth has now been enabled...");
            // wait for the bluetooth to be turned on and then proceed...

        }
    }

    //External Storage -------------------------------------------------------------------
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getDocStorageDir(String docName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), docName);
        if (!file.mkdirs()) {
            Log.e("External Storage Issue", "Directory not created");
        }
        return file;
    }

    public File getDocStorageDir(Context context, String docName) {
        // Get the directory for the app's private documents directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), docName);
        if (!file.mkdirs()) {
            Log.e("External Storage Issue", "Directory not created");
        }
        return file;
    }

    public File getExternalStorageDir(String docName) {
        // Get the directory for the user's public pictures directory.
        File path = new File(Environment.getExternalStorageDirectory(), "ElectroCorTest Data");
        /*if (!file.getParentFile().mkdirs()){
            Log.e("External Storage Issue", "Directory not created");
        }*/
        if (!path.mkdirs()) {
            Log.e("External Storage Issue", "Directory not created");
        }

        File file1 = new File(path, docName);

        return file1;
    }
    //End External Storage -------------------------------------------------------------------

    //enable bluetooth
    public BluetoothAdapter enable() {
        int REQUEST_ENABLE_BT = 1;
        int RESULT_CODE = getActivity().RESULT_OK;
        Intent data = new Intent();
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Context context = getActivity().getApplicationContext();
            CharSequence text = "Device does not support Bluetooth";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            onActivityResult(REQUEST_ENABLE_BT, RESULT_CODE, data);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        return mBluetoothAdapter;
    }

    //read a file given the name
    public String readFile(String filename) {

        String ret = "";
        try {
            InputStream inputStream = getActivity().openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;

    }

    //write to a file given the file name and string to write
    public void writeFile(String filename, String s) {
        FileOutputStream outputStream;
        try {
            //outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream = getContext().openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(s.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToExternalFile(String filename, String s) {
        if (!isExternalStorageWritable()) {
            Log.d("External storage issue", "CANNOT write to external storage");
        }
        File file = getExternalStorageDir(filename);
        FileOutputStream stream = null;
        //OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(s.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeCharToExternalFile(String filename, String s) {
        if (!isExternalStorageWritable()) {
            Log.d("External storage issue", "CANNOT write to external storage");
        }
        File file = getExternalStorageDir(filename);
        FileWriter writer = null;
        //OutputStream stream = null;
        try {
            writer = new FileWriter(file);
            writer.write(s);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseString(String s) {
        //String delims = "#+[0-9]+,[0-9]+,[0-9]+";
        //String delims = ",";
        //String[] tokens = s.split(delims);
        String temp;
        String[] tokens;

        String pattern = "#[0-9]+,[0-9]+,[0-9]+";
        //String pattern = "#[0-9]{1,4},[0-9]{1,4},[0,9]{1,4}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);

        while (m.find()) {
            temp = s.substring(m.start() + 1, m.end());    //use +1 b/c do not want the '#' symbol
            tokens = temp.split(",");

//            electrode1.add(Integer.parseInt(tokens[0]));
//            electrode2.add(Integer.parseInt(tokens[1]));
//            electrode3.add(Integer.parseInt(tokens[2]));
        }
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        checkBTStatus();
    }

    public void CreateSaveObject() {

        mFilePath = Bluetooth.filecurrentFileName;

        byteArray = new byte[(int) mFilePath.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(mFilePath);
            fileInputStream.read(byteArray);

            fileInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            Toast.makeText(getActivity(), "File Not Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            getActivity().finish();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            Toast.makeText(getActivity(), "Error Reading The File", Toast.LENGTH_LONG).show();
            e1.printStackTrace();
            getActivity().finish();
        }

        mSubmission = new ParseObject("ECGData");

        mUpload = new ParseFile(Bluetooth.filecurrentFileString, byteArray);
        final ParseUser currentUser = ParseUser.getCurrentUser();

        mUpload.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    mSubmission.put("File", mUpload);
                    mSubmission.put("User", currentUser.getObjectId());

                    new SaveObjectThread().execute("");

                } else {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void saveFile() {
        mSubmission.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private class SaveFileThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            CreateSaveObject();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            mThreadWorking = false;

        }

        @Override
        protected void onPreExecute() {
            mThreadWorking = true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class SaveObjectThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            saveFile();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}