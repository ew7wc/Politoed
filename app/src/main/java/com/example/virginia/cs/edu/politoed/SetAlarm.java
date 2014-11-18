package com.example.virginia.cs.edu.politoed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


public class SetAlarm extends Activity implements DatePickerFragment.DateListener {

    //UI elements
    private TextView setAlarmDate;
    private TimePicker alarmTime;
    private TextView datePicker;
    private EditText editTaskName;
    private Spinner category;
    private Spinner priority;
    private EditText notes;
    private ToggleButton twitterToggle;

    private DatePickerFragment newFragment = new DatePickerFragment();
    private DatabaseHelper dbH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        findViewsById();

    }

    private void findViewsById() {
        setAlarmDate = (TextView) findViewById(R.id.alarmDate);

        Spinner priorities = (Spinner) findViewById(R.id.priorityPicker);
        Spinner categories = (Spinner) findViewById(R.id.categoryList);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        priorities.setAdapter(priorityAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categories.setAdapter(categoryAdapter);
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


    public void onClickSetDate(View view) {
        //Toast.makeText(getApplicationContext(), "Set date clicked", Toast.LENGTH_LONG).show();
        newFragment.show(getFragmentManager(), "datePicker"); //open the datepicker
    }

    @Override
    public void returnDate(String date) {
        setAlarmDate.setText(date);
    }

    public void onClickSubmit(View v) {
        dbH = new DatabaseHelper(getApplicationContext());

        alarmTime = (TimePicker) findViewById(R.id.timePicker);
        int hour = alarmTime.getCurrentHour();
        int min = alarmTime.getCurrentMinute();

        //Populate the list view
        //convert time to 12 hr time
        String AM_PM = "AM";
        if (hour > 12) {
            hour = hour - 12;
            AM_PM = "PM";
        }
        if (hour == 0) {
            hour = 12;
            AM_PM= "AM";
        }
        editTaskName = (EditText) findViewById(R.id.editTaskName);
        category = (Spinner) findViewById(R.id.categoryList);
        priority = (Spinner) findViewById(R.id.priorityPicker);
        notes = (EditText) findViewById(R.id.editNotes);
        twitterToggle = (ToggleButton) findViewById(R.id.twitterToggle);

        String categoryName = category.getSelectedItem().toString();
        String priorityName = priority.getSelectedItem().toString();

        int year = newFragment.setYear;
        int mon = newFragment.setMonth;
        int day = newFragment.setDay;

        String notes_string = notes.getText().toString();

        Alarm a = new Alarm(-1, editTaskName.getText().toString(), year, mon, day, alarmTime.getCurrentHour(), min, categoryName, priorityName, notes_string,(twitterToggle.isChecked()) ? 1:0);
        int id = dbH.addAlarm(a);

        Long time = new GregorianCalendar(newFragment.setYear, newFragment.setMonth, newFragment.setDay, alarmTime.getCurrentHour(),min).getTimeInMillis();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("alarmID", id);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK);
        //End activity
        finish();


    }
}
