package com.example.virginia.cs.edu.politoed;

import com.example.virginia.cs.edu.politoed.adapters.ExpandableListAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class AllAlarms extends Activity {

    private List<Alarm> groupList;
    private Map<String, List<String>> alarmCollection;
    private ExpandableListView expListView;
    private DatabaseHelper db;
    private List<String> titleList;
    private ExpandableListAdapter expListAdapter;
    private List<Integer> alarmIDs;
    private ImageView noAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alarms);
        db = new DatabaseHelper(getApplicationContext());
        createGroupList();
    }

    protected void createGroupList() {
        alarmCollection = new LinkedHashMap<String, List<String>>();
        groupList = new ArrayList<Alarm>();
        titleList = new ArrayList<String>();
        groupList = db.getAllAlarms();
        alarmIDs = new ArrayList<Integer>();

        for (int i = 0; i < groupList.size(); i++) {
            String title = groupList.get(i).getFormattedTime() + " " + groupList.get(i).getName();
            titleList.add(title);
            alarmIDs.add(groupList.get(i).getId());
            createCollection(groupList.get(i), title);
        }

        expListView = (ExpandableListView) findViewById(R.id.alarm_list);
        expListAdapter = new ExpandableListAdapter(this, titleList, alarmCollection,alarmIDs);
        expListView.setAdapter(expListAdapter);
        noAlarms = (ImageView) findViewById(R.id.noAlarms);
        if (expListAdapter.getGroupCount()==0) {

            noAlarms.setVisibility(View.VISIBLE);
        }
        else {
            noAlarms.setVisibility(View.INVISIBLE);
        }


        /*expListView.setOnChildClickListener(new OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        }); */
        //Toast.makeText(getApplicationContext(), expListAdapter.getGroup(0).toString(), Toast.LENGTH_LONG);



    }

    protected void createCollection(Alarm a, String title) {
        String sendTweet;
        if (a.getTwitter()==1) {
            sendTweet = "Yes";
        }
        else {
            sendTweet = "No";
        }
        String[] alarm1 = {"Set for Date: " + a.getFormattedDate(), "Category: "  + a.getCategory(), "Priority: " + a.getPriority(), "Notes: " + a.getNotes(), "Send to Twitter? " + sendTweet};

        alarmCollection.put(title, loadChild(alarm1));
    }

    private ArrayList<String> loadChild(String[] alarmInfo) {
        ArrayList<String> cl = new ArrayList<String>();
        for (String info : alarmInfo)
            cl.add(info);
        return cl;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_alarms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                i = new Intent(AllAlarms.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_add_alarm:
                //go to add alarm activity
                i = new Intent(AllAlarms.this, SetAlarm.class);
                startActivityForResult(i, 1);
                return true;
            case R.id.action_help:
                i = new Intent(AllAlarms.this, HelpScreen.class);
                startActivityForResult(i, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        createGroupList();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        createGroupList();
    }

}
