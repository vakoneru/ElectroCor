package com.corcare.electrocor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by varunkoneru on 10/15/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class SettingsActivity extends Activity {

    private TextView mChangePassword;
    private TextView mChangeEmail;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mRepeatPassword;
    private EditText mOldEmail;
    private EditText mNewEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        mChangePassword = (TextView) findViewById(R.id.buttonChangePassword);
        mChangeEmail = (TextView) findViewById(R.id.buttonChangeEmail);
        mOldPassword = (EditText) findViewById(R.id.editOldPassword);
        mNewPassword = (EditText) findViewById(R.id.editNewPassword);
        mRepeatPassword = (EditText) findViewById(R.id.editRepeatPassword);
        mOldEmail = (EditText) findViewById(R.id.editOldEmail);
        mNewEmail = (EditText) findViewById(R.id.editNewEmail);

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpassword = mOldPassword.getText().toString().trim();
                final String newpassword = mNewPassword.getText().toString().trim();
                final String repeatpassword = mRepeatPassword.getText().toString().trim();

                final ProgressDialog dlg = new ProgressDialog(SettingsActivity.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Changing password.  Please wait.");
                dlg.show();

                if (!oldpassword.isEmpty() || !newpassword.isEmpty() || !repeatpassword.isEmpty()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    String username = currentUser.getUsername();
                    ParseUser.logInInBackground(username, oldpassword, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // the password was correct
                                if (newpassword.equals(repeatpassword)) {
                                    ParseUser thisUser = ParseUser.getCurrentUser();
                                    thisUser.setPassword(newpassword);
                                    thisUser.saveInBackground();
                                    dlg.dismiss();
                                    finish();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                    builder.setMessage("New passwords do not match.")
                                            .setCancelable(false)
                                            .setTitle("Uh oh")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    Log.d("Settings Error", "passwords didn't match");
                                    dlg.dismiss();
                                }
                            } else {
                                // Signup failed. Look at the ParseException to see what happened.
                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                builder.setMessage("Incorrect password.")
                                        .setCancelable(false)
                                        .setTitle("Uh oh")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                dlg.dismiss();
                            }
                        }
                    });
                }
            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                String oldemail = mOldEmail.getText().toString().trim();
                String newemail = mNewEmail.getText().toString().trim();

                final ProgressDialog dlg = new ProgressDialog(SettingsActivity.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Changing email.  Please wait.");
                dlg.show();

                if (!oldemail.isEmpty() || !newemail.isEmpty()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    String currentemail = currentUser.getEmail().toString().trim();

                    if (oldemail.equals(currentemail)) {
                        ParseUser thisUser = ParseUser.getCurrentUser();
                        thisUser.setEmail(newemail);
                        thisUser.saveInBackground();
                        dlg.dismiss();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Emails do not match.")
                                .setCancelable(false)
                                .setTitle("Uh oh")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            }
        });

    }
}
