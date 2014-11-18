package com.example.virginia.cs.edu.politoed;

/**
 * Created by Elaine on 11/12/2014.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public int setYear, setMonth, setDay;
    TextView datePicker;
    DateListener listener;

    public interface DateListener{
        public void returnDate(String date);
    }

    public DatePickerFragment() {
        super();
        GregorianCalendar g = new GregorianCalendar();
        setYear = g.get(Calendar.YEAR);
        setMonth = g.get(Calendar.MONTH);
        setDay = g.get(Calendar.DAY_OF_MONTH);


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listener = (DateListener) getActivity();
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        dpd.getDatePicker().setCalendarViewShown(false);
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        setYear = year;
        setMonth = month;
        setDay = day;

        if (listener != null)
        {
            listener.returnDate(String.format("%d-%d-%d", month+1, day, year));

        }
    }
}