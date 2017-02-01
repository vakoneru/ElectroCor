package com.corcare.electrocor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by varunkoneru on 10/12/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */

public class JournalEntryActivity extends Activity {

    private EditText mJournalEntry;
    private Button mSaveButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.journalentry_layout);

        mJournalEntry = (EditText) findViewById(R.id.journalEditText);
        mSaveButton = (Button) findViewById(R.id.journalSubmitButton);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entry = mJournalEntry.getText().toString().trim();
                if (!entry.isEmpty()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    //Toast.makeText(getActivity(), entry, Toast.LENGTH_LONG).show();
                    ParseObject journalEntry = new ParseObject("JournalEntries");
                    journalEntry.put("entry", entry);
                    journalEntry.put("user", currentUser.getObjectId());
                    final ProgressDialog dlg = new ProgressDialog(JournalEntryActivity.this);
                    dlg.setTitle("Please wait.");
                    dlg.setMessage("Saving entry to server.");
                    dlg.show();
                    journalEntry.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                dlg.dismiss();
                                mJournalEntry.setText("");
                            } else {
                                dlg.dismiss();
                                Toast.makeText(getApplicationContext(), "There was an error saving to the server, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finish();
                }
            }
        });
    }
}
