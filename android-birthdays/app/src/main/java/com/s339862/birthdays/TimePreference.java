package com.s339862.birthdays;

import androidx.preference.EditTextPreference;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Locale;

public class TimePreference extends EditTextPreference {
    // Constructor
    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void showTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog with the current time
        // Lets you pick a time and sets the text and summary of the preference to the picked time
        TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hour, minute) -> {
            String time = String.format(Locale.getDefault(),"%02d:%02d", hour, minute);
            setText(time);
            setSummary(time);
        }, defaultHour, defaultMinute, true);

        // Show the TimePickerDialog
        dialog.show();
    }

    @Override
    protected void onClick() {
        // Show TimePickerDialog instead of the default text input dialog
        showTimePickerDialog();
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        // Set the default value of the preference
        String time = getPersistedString((String) defaultValue);
        setText(time);
        setSummary(time);
    }

}
