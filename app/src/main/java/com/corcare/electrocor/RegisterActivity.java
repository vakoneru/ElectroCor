package com.corcare.electrocor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by varunkoneru on 10/6/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class RegisterActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private TextView mButtonRegister;
    private TextView mButtonLogin;
    private ImageView mProfilePicture;


    private static Bitmap Image = null;
    private static Bitmap rotateImage = null;
    private static final int GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        //Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextLTProRegular.ttf");
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mUsername = (EditText) findViewById(R.id.registerUsername);
        //mUsername.setTypeface(myTypeface);
        mPassword = (EditText) findViewById(R.id.registerPassword);
        //mPassword.setTypeface(myTypeface);
        mEmail = (EditText) findViewById(R.id.registerEmail);
        //mEmail.setTypeface(myTypeface);
        mButtonRegister = (TextView) findViewById(R.id.imageRegister);
        mButtonLogin = (TextView) findViewById(R.id.buttonLoginReg);
        mProfilePicture = (ImageView) findViewById(R.id.imageProfilePicture);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Make sure everything has been entered properly.")
                            .setCancelable(false)
                            .setTitle("Error!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //dialog.dismiss();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    final ProgressDialog dlg = new ProgressDialog(RegisterActivity.this);
                    dlg.setTitle("Please wait.");
                    dlg.setMessage("Signing up. Please wait.");
                    dlg.show();

                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);


                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                dlg.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(e.toString())
                                        .setCancelable(false)
                                        .setTitle("You made a mistake.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //dialog.dismiss();
                                                dialog.cancel();
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

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                mProfilePicture.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        });


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode != 0) {
            Uri mImageUri = data.getData();
            try {
                Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                if (getOrientation(getApplicationContext(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getApplicationContext(), mImageUri));
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix,true);
                    mProfilePicture.setImageBitmap(rotateImage);
                } else
                    mProfilePicture.setImageBitmap(Image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
