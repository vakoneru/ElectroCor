package com.corcare.electrocor;

/**
 * Created by varunkoneru on 10/6/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */

import java.lang.reflect.Field;
import android.app.Application;
import android.view.ViewConfiguration;

import com.parse.Parse;


public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "PpEnmhKtiWjYm7YsHJ9xTsAKxPx6eTUwoh8ktv6m", "n2sTgLSSzmiG4tdGggSngth9usGJRKjrjZAmiv5g");

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore

        }
    }
}
