package com.example.virginia.cs.edu.politoed;

import com.example.virginia.cs.edu.politoed.adapters.ExpandableListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class AllAlarms extends Activity {

    private List<Alarm> groupList;
    private Map<String, List<String>> alarmCollection;
    private ExpandableListView expListView;
    private DatabaseHelper db;
    private ImageButton editAlarmBtn;
    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alarms);

        db = new DatabaseHelper(getApplicationContext());

        createGroupList();

        //createCollection();


        //Code to get the IP address out from settings
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String IP = preferences.getString("example_text", "NA");
        Toast.makeText(getBaseContext(), IP, Toast.LENGTH_LONG)
                .show();*/
    }


    private void createGroupList() {
        alarmCollection = new LinkedHashMap<String, List<String>>();
        //editAlarmBtn = (ImageButton) findViewById(R.id.editAlarmBtn);
        //editAlarmBtn.setFocusable(false);

        groupList = new ArrayList<Alarm>();
        titleList = new ArrayList<String>();
        //TODO fetch from DB....
        groupList = db.getAllAlarms();


            //Currently are placeholders
            for (int i = 0; i < groupList.size(); i++) {
                String title = groupList.get(i).getFormattedTime() + " " + groupList.get(i).getName();
                titleList.add(title);
                createCollection(groupList.get(i), title);

            }


            expListView = (ExpandableListView) findViewById(R.id.alarm_list);
            final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                    this, titleList, alarmCollection);
            expListView.setAdapter(expListAdapter);


            expListView.setOnChildClickListener(new OnChildClickListener() {

                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    final String selected = (String) expListAdapter.getChild(
                            groupPosition, childPosition);
                    Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                            .show();

                    return true;
                }
            });


    }

    private void createCollection(Alarm a, String title) {
        // preparing laptops collection(child)
        String[] alarm1 = {"Date: " + a.getFormattedDate(), "Notes: " + a.getNotes(), "Category: "  + a.getCategory()};
        alarmCollection.put(title, loadChild(alarm1));

    }

    private ArrayList<String> loadChild(String[] alarmInfo) {
        ArrayList<String> cl = new ArrayList<String>();

        for (String info : alarmInfo)
            //childList.add(info);
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
            case R.id.action_add:
                //go to add alarm activity
                i = new Intent(AllAlarms.this, SetAlarm.class);
                startActivityForResult(i, 1);
                return true;
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
