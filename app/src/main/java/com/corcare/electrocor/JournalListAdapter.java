package com.corcare.electrocor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by varunkoneru on 10/13/15.
 * University of Texas at Austin; Biomedical Engineering
 * BME 370: Capstone Design
 */
public class JournalListAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mEntries;

    public JournalListAdapter(Context context, List<ParseObject> entries) {
        super(context, R.layout.journal_entry, entries);

        mContext = context;
        mEntries = entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder listItem;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.journal_entry, null);
            listItem = new ViewHolder();
        } else {
            //listItem = (ViewHolder) convertView.getTag();
            listItem = new ViewHolder();
        }

        ParseObject message = mEntries.get(position);

        listItem.mObject = message;


        listItem.mEntry = message.getString("entry");
        listItem.mEntryLabel = (TextView) convertView.findViewById(R.id.entry);
        if (listItem.mEntry == null) {
            listItem.mEntry = "None";
        }
            listItem.mEntryLabel.setText(listItem.mEntry);


        listItem.mCreatedAt = message.getCreatedAt().toString();
        listItem.mCreatedAtText = (TextView) convertView.findViewById(R.id.textCreatedAt);
        if (listItem.mCreatedAt == null) {
            listItem.mCreatedAt = "None";
        }
            listItem.mCreatedAtText.setText(listItem.mCreatedAt);


        return convertView;
    }

    private static class ViewHolder {
        TextView mCreatedAtText;
        String mCreatedAt;
        TextView mEntryLabel;
        String mEntry;
        ParseObject mObject;
    }
}
