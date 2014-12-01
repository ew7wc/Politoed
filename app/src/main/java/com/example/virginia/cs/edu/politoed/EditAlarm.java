package com.example.virginia.cs.edu.politoed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.GregorianCalendar;


public class EditAlarm extends Activity {

    //UI elements
    private TimePicker editTime;
    private DatePicker editDate;
    private EditText editTaskName;
    private Spinner categories;
    private Spinner priorities;
    private EditText editNotes;
    private ToggleButton twitterToggle;

    private DatabaseHelper dbH;

    private Alarm a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);



        int alarmID = getIntent().getExtras().getInt("alarmID");
        dbH = new DatabaseHelper(getApplicationContext());

        a = dbH.getAlarm(alarmID);

        int hour = a.getTimeHour();
        String amPM = "";
        if (hour > 12) {
            hour=-12;
            amPM = "PM";
        }
        if (hour ==0) {
            hour = 12;
            amPM = "AM";
        }
        //Toast.makeText(getApplicationContext(), "#:" + alarmID, Toast.LENGTH_LONG).show();
        findViews();



        editTaskName.setText(a.getName());
        editTime.setCurrentHour(a.getTimeHour());
        editTime.setCurrentMinute(a.getTimeMinute());
        editDate.updateDate(a.getDateYear(), a.getDateMonth(), a.getDateDay());
        categories.setSelection(getCategorySpinnerIndex(a.getCategory()));
        priorities.setSelection(getPrioritySpinnerIndex(a.getPriority()));

        if (a.getTwitter()==1) {
            twitterToggle.setChecked(true);
        }
        else {
            twitterToggle.setChecked(false);
        }
        editNotes.setText(a.getNotes());
    }


    public void onClickUpdate(View v) {
        boolean isEmpty = editTaskName.getText().toString().matches("^\\s*$");

        int categoryPosition = categories.getSelectedItemPosition();

        int priorityPosition = priorities.getSelectedItemPosition();

        if (editTaskName.getText().toString().matches("^\\s*$") || categories.getSelectedItemPosition()==0 || priorities.getSelectedItemPosition()==0) {

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

        a.setName(editTaskName.getText().toString());
        a.setCategory(categories.getSelectedItem().toString());
        a.setPriority(priorities.getSelectedItem().toString());
        a.setTwitter((twitterToggle.isChecked()) ? 1:0);
        a.setNotes(editNotes.getText().toString());
        a.setDateDay(editDate.getDayOfMonth());
        a.setDateMonth(editDate.getMonth());
        a.setDateYear(editDate.getYear());
        a.setTimeHour(editTime.getCurrentHour());
        a.setTimeMinute(editTime.getCurrentMinute());

        dbH.updateAlarm(a);

        Long time = new GregorianCalendar(editDate.getYear(), editDate.getMonth(), editDate.getDayOfMonth(), editTime.getCurrentHour(), editTime.getCurrentMinute()).getTimeInMillis();

        //Cancel old alarm

        Intent i = new Intent(v.getContext(), AlarmReceiver.class);
        i.putExtra("alarmID",a.getId());
        PendingIntent pi;
        pi = PendingIntent.getBroadcast(v.getContext(), a.getId(), i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("alarmID", a.getId());
        am.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(getApplicationContext(), "Alarm Updated", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK);
        //End activity
        finish();


    }
    protected int getPrioritySpinnerIndex(String priority) {
        int index;
        if (priority.equals("Low")) {
            index = 1;
        }
        else if (priority.equals("Medium")) {
            index = 2;
        }
        else {
            index = 3;
        }
        return index;
    }

    protected int getCategorySpinnerIndex(String category) {
        int index;
        if (category.equals("Class")) {
            index = 2;
        }
        else if (category.equals("Errands")) {
            index = 3;
        }
        else if (category.equals("Fitness")) {
            index = 4;
        }
        else if (category.equals("Meeting")) {
            index = 5;
        }
        else if (category.equals("Social")) {
            index = 6;
        }
        else {
            index = 1;
        }
        return index;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_alarm, menu);
        return true;
    }


    public void findViews() {

        priorities = (Spinner) findViewById(R.id.editPriority);
        categories = (Spinner) findViewById(R.id.editCategory);


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

        editTime = (TimePicker) findViewById(R.id.editTime);
        editDate = (DatePicker) findViewById(R.id.editDate);
        editTaskName = (EditText) findViewById(R.id.editTaskName);
        editNotes = (EditText) findViewById(R.id.editNotes);
        twitterToggle = (ToggleButton) findViewById(R.id.editTweet);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                i = new Intent(EditAlarm.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_add_alarm:
                //go to add alarm activity
                i = new Intent(EditAlarm.this, SetAlarm.class);
                startActivityForResult(i, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
