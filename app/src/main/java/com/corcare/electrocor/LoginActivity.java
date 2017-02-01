package com.corcare.electrocor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by varunkoneru on 10/7/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class LoginActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private TextView mButtonLogin;
    private TextView mButtonSignup;
    private TextView mLabelUsername;
    private TextView mLabelPassword;
    private TextView mForgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextLTProRegular.ttf");

        mUsername = (EditText) findViewById(R.id.loginUsername);
        mPassword = (EditText) findViewById(R.id.loginPassword);
        mLabelPassword = (TextView) findViewById(R.id.labelPassword);
        mLabelUsername = (TextView) findViewById(R.id.labelUsername);
        mButtonLogin = (TextView) findViewById(R.id.imageLogin);


        // Login button
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mUsername.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please enter a username.")
                            .setCancelable(false)
                            .setTitle("Uh oh")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //dialog.dismiss();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please enter a password.")
                            .setCancelable(false)
                            .setTitle("Uh oh")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //dialog.dismiss();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
                    dlg.setTitle("Please wait.");
                    dlg.setMessage("Logging in.  Please wait.");
                    dlg.show();
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            dlg.dismiss();
                            if (user != null) {
                                // Hooray! The user is logged in.
                                // Set up a progress dialog

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                // Signup failed. Look at the ParseException to see what happened.
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Recheck your login credentials.")
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
                    });
                }
            }
        });

        mButtonSignup = (TextView) findViewById(R.id.buttonLoginReg);
        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mForgotPassword = (TextView) findViewById(R.id.textForgotPassword);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
}

