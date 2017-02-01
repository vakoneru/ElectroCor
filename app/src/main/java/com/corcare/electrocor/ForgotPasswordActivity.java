package com.corcare.electrocor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by varunkoneru on 10/19/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class ForgotPasswordActivity extends Activity {


    private TextView mButtonResetPassword;
    private EditText mTextEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgotpassword);

        mTextEmail = (EditText) findViewById(R.id.forgotEmail);
        mButtonResetPassword = (TextView) findViewById(R.id.imageChangePassword);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage("Check your email for a link to reset your password.")
                .setCancelable(false)
                .setTitle("Success!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });

        mButtonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mTextEmail.getText().toString().trim();

                ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });
    }
}