package com.example.virginia.cs.edu.politoed.adapters;

/**
 * Created by Elaine on 11/16/2014.
 */
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virginia.cs.edu.politoed.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> alarmCollections;
    private List<String> alarms;

    public ExpandableListAdapter(Activity context, List<String> alarms,
                                 Map<String, List<String>> alarmCollections) {
        this.context = context;
        this.alarmCollections = alarmCollections;
        this.alarms = alarms;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return alarmCollections.get(alarms.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String alarmInfo = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_alarm_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.alarmInfo);

        item.setText(alarmInfo);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return alarmCollections.get(alarms.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return alarms.get(groupPosition);
    }

    public int getGroupCount() {
        return alarms.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String alarmName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        ImageButton editAlarmInfo = (ImageButton) convertView.findViewById(R.id.editAlarmBtn);
        editAlarmInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("EDIT CLICKED", "edit button clicked");
            }
        });
        editAlarmInfo.setFocusable(false);
        TextView item = (TextView) convertView.findViewById(R.id.alarmItem);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(alarmName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}