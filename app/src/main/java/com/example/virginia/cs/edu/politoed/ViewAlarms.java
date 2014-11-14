package com.example.virginia.cs.edu.politoed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Fragment;
public class ViewAlarms extends ListActivity implements DatePickerFragment.DateListener {

    TimePicker alarmTime;
    ArrayList<String> alarms = new ArrayList<String>();
    ArrayAdapter<String> alarmAdapter;
    ListView lv;
    DatePickerFragment newFragment = new DatePickerFragment();

    TextView datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alarms);

        //List set up
        alarmAdapter = new ArrayAdapter <String>(this, R.layout.row_layout_alarmlist, alarms);

        // assign the list adapter
        lv = (ListView) getListView();
        lv.setAdapter(alarmAdapter);

        datePicker = (TextView) findViewById(R.id.datePicker);

    }


    //Clicking the set date button
    public void submitDate(View view) {
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Clicking the Config RPi button
    public void clickConfigRPi(View v) {
        Intent myIntent = new Intent(ViewAlarms.this, LightUpRPi.class);
        startActivity(myIntent);
    }

    //Clicking the submit alarm button
    public void submitAlarm(View view) {
        alarmTime = (TimePicker) findViewById(R.id.alarmTime);
        int hour = alarmTime.getCurrentHour();
        int min = alarmTime.getCurrentMinute();

        //Populate the list view
        //convert time to 12 hr time
        String AM_PM = "AM";
        if (hour > 12) {
            hour = hour - 12;
            AM_PM = "PM";
        }

        //Add to list
        alarmAdapter.add(String.format("%d/%d/%d %d:%02d %s", newFragment.setMonth +1, newFragment.setDay, newFragment.setYear, hour, min, AM_PM));
        //Setting up alarm
        Long time = new GregorianCalendar(newFragment.setYear, newFragment.setMonth, newFragment.setDay, alarmTime.getCurrentHour(),min).getTimeInMillis();
        //Toast.makeText(getApplicationContext(), "Alarm set for " + newFragment.setYear + "/" + newFragment.setMonth +"/" + newFragment.setDay, Toast.LENGTH_LONG).show();
        //Long time = new GregorianCalendar().getTimeInMillis()+10000;
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void returnDate(String date) {

        datePicker.setText(date);
    }
}
