package com.s339862.birthdays;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private void startPeriodicService() {
        Log.d("MainActivity", "Starting PeriodicService");
        Intent intent = new Intent(requireContext(), PeriodicService.class);
        requireContext().startService(intent);
    }

    private void stopPeriodicService() {
        Log.d("MainActivity", "Stopping PeriodicService");
        Intent intent = new Intent(requireContext(), PeriodicService.class);
        requireContext().stopService(intent);
    }

    // Check if the app has SMS permission
    private boolean checkSmsPermission() {
        final int PERMISSION_GRANTED = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS);
        return PERMISSION_GRANTED == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permission
    private void getSmsPermission() {
        FragmentActivity activity = requireActivity();
        String[] permissions = new String[]{Manifest.permission.SEND_SMS};
        int requestCode = SettingsActivity.SMS_PERMISSION_REQUEST_CODE;
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from the XML file
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Add event listeners that runs when the preference is changed
        // When Enable SMS switch is clicked, start or stop the periodic service
        // If SMS permission is not granted, request the permission first
        findPreference("sms_enabled").setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enableSmsService = (boolean) newValue;
            if (enableSmsService) {
                if (!checkSmsPermission()) {
                    getSmsPermission();
                    return false;
                } else {
                    startPeriodicService();
                    return true;
                }
            } else {
                stopPeriodicService();
                return true;
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Sets the padding of the view equal to the system bars
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }
}
