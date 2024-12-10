package com.s339862.mattespill;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    // Runs when the settings fragment is created
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from the XML file
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Add event listeners that runs when the preference is changed
        // When number of questions is changed, save the value to shared preferences
        findPreference("number_of_questions").setOnPreferenceChangeListener((preference, newValue) -> true);
        // When language is changed, set the new locale and save the language to shared preferences
        findPreference("language").setOnPreferenceChangeListener((preference, newValue) -> {
                    String language = newValue.toString();
                    LocaleListCompat locale = LocaleListCompat.forLanguageTags(language);
                    AppCompatDelegate.setApplicationLocales(locale);
                    return true;
                });

    }
}
