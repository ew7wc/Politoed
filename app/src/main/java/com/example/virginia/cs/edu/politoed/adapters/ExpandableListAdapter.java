package com.example.virginia.cs.edu.politoed.adapters;

/**
 * Created by Elaine on 11/16/2014.
 */
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virginia.cs.edu.politoed.Alarm;
import com.example.virginia.cs.edu.politoed.AlarmReceiver;
import com.example.virginia.cs.edu.politoed.DatabaseHelper;
import com.example.virginia.cs.edu.politoed.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> alarmCollections;
    private List<String> alarms;
    private List<Integer> alarmIDs;
    private DatabaseHelper db;

    public ExpandableListAdapter(Activity context, List<String> alarms,
                                 Map<String, List<String>> alarmCollections) {
        this.context = context;
        this.alarmCollections = alarmCollections;
        this.alarms = alarms;
    }

    public ExpandableListAdapter(Activity context, List<String> alarms,
                                 Map<String, List<String>> alarmCollections, List<Integer> alarmIDs) {
        this.context = context;
        this.alarmCollections = alarmCollections;
        this.alarms = alarms;
        this.alarmIDs = alarmIDs;
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

    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String alarmName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,null);
            ImageButton deleteAlarmBtn = (ImageButton) convertView.findViewById(R.id.deleteAlarmBtn);

            deleteAlarmBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //Toast.makeText(v.getContext(), "delete clicked, groupIndex: " + alarmIDs.get(groupPosition), Toast.LENGTH_LONG).show();
                    final int alarmID = alarmIDs.get(groupPosition);

                    db = new DatabaseHelper(v.getContext());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Alarm?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Alarm a = db.getAlarm(alarmID);
                                    String title = a.getFormattedTime() + " " + a.getName();
                                    //Toast.makeText(v.getContext(), title, Toast.LENGTH_LONG).show();
                                    alarmIDs.remove(groupPosition);
                                    alarms.remove(groupPosition);
                                    alarmCollections.remove(title);
                                    db.deleteAlarm(alarmID);
                                    notifyDataSetChanged();

                                    //Cancel alarm
                                    Intent i = new Intent(v.getContext(), AlarmReceiver.class);
                                    i.putExtra("alarmID",alarmID);
                                    PendingIntent pi;
                                    pi = PendingIntent.getBroadcast(v.getContext(), alarmID, i, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager am = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
                                    am.cancel(pi);
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    //Toast.makeText(v.getContext(), "delete clicked, groupIndex: " + alarmIDs.get(groupPosition), Toast.LENGTH_LONG).show();
                }
            });
            deleteAlarmBtn.setFocusable(false);
        }
        TextView item = (TextView) convertView.findViewById(R.id.alarmItem);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(getGroup(groupPosition).toString());

       /* Button editAlarmInfo = (Button) convertView.findViewById(R.id.editAlarmBtn);
        editAlarmInfo.setFocusable(false);
        //editAlarmInfo.setClickable(true);
        editAlarmInfo.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("EDIT CLICKED", "edit button clicked");
                Toast.makeText(v.getContext(), "edit clicked", Toast.LENGTH_LONG);
            }
        }); */



        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void colorListItems() {

    }
}