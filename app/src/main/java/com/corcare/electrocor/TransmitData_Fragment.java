package com.corcare.electrocor;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by varunkoneru on 10/2/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class TransmitData_Fragment extends Fragment {

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaneState) {
        myView = inflater.inflate(R.layout.transmitdata_layout, container, false);



        return myView;
    }
}
