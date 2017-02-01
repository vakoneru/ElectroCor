package com.corcare.electrocor;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by varunkoneru on 10/2/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 **/

public class Journal_Fragment extends ListFragment {

    private JournalListAdapter mAdapter;

    protected List<String> mUserFavorites;

    protected ParseUser currentUser;

    protected ListView mListView;

    private EditText mJournalEntry;
    private TextView mNewJournalEntry;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View myView = inflater.inflate(R.layout.journal_fragment, container, false);

//        FloatingActionButton fab = (FloatingActionButton) myView.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    //    mListView = (ListView) myView.findViewById(android.R.id.list);

        mNewJournalEntry = (TextView) myView.findViewById(R.id.buttonJournalEntry);

        mNewJournalEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JournalEntryActivity.class);
                startActivity(intent);

            }
        });

        currentUser = ParseUser.getCurrentUser();

        runQuery();

        return myView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = this.getListView();
        mListView.setDivider(null);
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    public void runQuery() {

        ParseQuery<ParseObject> mQuery = ParseQuery.getQuery("JournalEntries");
        mQuery.whereEqualTo("user", currentUser.getObjectId());
        mQuery.setLimit(50);

        // hallOfFameQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> files, ParseException e) {
                if (e == null) { // if successful

                    mAdapter = new JournalListAdapter(mListView.getContext(), files);
                    setListAdapter(mAdapter);

                } else {
                    Log.e("HOF Error", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });


    }

    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
        //     mAdapter.pause();
        }
    }

    public void onResume() {
        super.onResume();

        runQuery();
    }
}
