package com.example.virginia.cs.edu.politoed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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


public class SetAlarm extends Activity {

    //UI elements
    private TimePicker alarmTime;
    private DatePicker alarmDate;
    private EditText editTaskName;
    private Spinner category;
    private Spinner priority;
    private EditText notes;
    private ToggleButton twitterToggle;

    private DatabaseHelper dbH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        findViewsById();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
    private void findViewsById() {
        //setAlarmDate = (TextView) findViewById(R.id.setDate);

        priority = (Spinner) findViewById(R.id.priorityPicker);
        category = (Spinner) findViewById(R.id.categoryList);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        priority.setAdapter(priorityAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(categoryAdapter);
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

        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                i = new Intent(SetAlarm.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_add:
                //go to add alarm activity
                i = new Intent(SetAlarm.this, SetAlarm.class);
                startActivityForResult(i, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickSubmit(View v) {
        dbH = new DatabaseHelper(getApplicationContext());

        alarmTime = (TimePicker) findViewById(R.id.timePicker);
        alarmDate = (DatePicker) findViewById(R.id.setDate);
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

        int year = alarmDate.getYear();
        int mon = alarmDate.getMonth();
        int day = alarmDate.getDayOfMonth();

        String notes_string = notes.getText().toString();

        boolean isEmpty = editTaskName.getText().toString().matches("^\\s*$");

        int categoryPosition = category.getSelectedItemPosition();

        int priorityPosition = priority.getSelectedItemPosition();

        if (editTaskName.getText().toString().matches("^\\s*$") || category.getSelectedItemPosition()==0 || priority.getSelectedItemPosition()==0) {

            String msg = "";

            if (isEmpty) {
                msg = "Please fill in a Task Name.\n";
            }
            if (categoryPosition==0) {
                msg = msg + "Please select a Category.\n";
            }
            if (priorityPosition==0) {
                msg = msg + "Please select a Priority.";
            }

            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(msg)
                    .setTitle("Incomplete form!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //DONT DO ANYTHING
                }
            });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        Alarm a = new Alarm(-1, editTaskName.getText().toString(), year, mon, day, alarmTime.getCurrentHour(), min, categoryName, priorityName, notes_string,(twitterToggle.isChecked()) ? 1:0);
        int id = dbH.addAlarm(a);

        Long time = new GregorianCalendar(year, mon, day, alarmTime.getCurrentHour(),min).getTimeInMillis();
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
