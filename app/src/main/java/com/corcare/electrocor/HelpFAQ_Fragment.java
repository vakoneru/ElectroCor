package com.corcare.electrocor;

import android.app.Fragment;
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
public class HelpFAQ_Fragment extends Fragment {

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaneState) {
        myView = inflater.inflate(R.layout.helpfaq_layout, container, false);
        return myView;
    }
}
