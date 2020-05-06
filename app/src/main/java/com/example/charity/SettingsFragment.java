package com.example.charity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Context mContext;
    private int MAX_SEARCH_SURFACE_VALUE = 576;
    private int MIN_SEARCH_SURFACE_VALUE = 64;
    private SharedPreferences mSharedPreferences;
    private String mNewValueToSaveStr;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
          setPreferencesFromResource(R.xml.preferences,rootKey);
        // Get reference to edit text preference
        // Set summary on startup of preference screen
         mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount() ; i++ ) {
            // Finding preferences by index inside preference screen
            PreferenceGroup preferenceGroup = (PreferenceGroup)getPreferenceScreen().getPreference(i);
            for( int j = 0 ; j < preferenceGroup.getPreferenceCount() ; j++ ) {
                Preference preference =  preferenceGroup.getPreference(j);

            // Check if the preference is an edit text preference
            // update summary
            if (preference instanceof EditTextPreference) {

                String savedPreferenceValue =
                        mSharedPreferences.getString(preference.getKey(), "");
                mNewValueToSaveStr = savedPreferenceValue;
                /*
                 * If the message length > 30 characters
                 * Extract substring from 30 first characters with ...
                 */
                if (preference.getKey().equals(getString(R.string.pref_key_message_request))) {

                    if (savedPreferenceValue.length() > 30) {
                        mNewValueToSaveStr = savedPreferenceValue.substring(0, 30).concat("...");
                    }
                }
                // Set summary text during preference screen appear
                preference.setSummary(mNewValueToSaveStr);
                // Attach listener to update summary value if changed by user
                preference.setOnPreferenceChangeListener((preference1, newValue) -> {
                    // Check if value entered empty, then return without changing shared preferences
                    if (TextUtils.isEmpty(newValue.toString())) {
                        Toast.makeText(mContext, getString(R.string.empty_value_error_message), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    // Assign the value to save to the new entered
                    mNewValueToSaveStr = newValue.toString();
                    if (preference1.getKey().equals(getString(R.string.pref_key_edittext_surface))) {
                        int newValueSurfaceToSave;
                        // Convert new entered value to int
                        int newEnteredValueSurface = Integer.parseInt(newValue.toString());
                        // Check values if exceeded or minimals put old saved values
                        if (newEnteredValueSurface < MIN_SEARCH_SURFACE_VALUE) {
                            newValueSurfaceToSave = Integer.parseInt(savedPreferenceValue);
                            Toast.makeText(mContext, getString(R.string.value_surface_minimal, String.valueOf(MIN_SEARCH_SURFACE_VALUE)), Toast.LENGTH_SHORT).show();
                        } else if (newEnteredValueSurface > MAX_SEARCH_SURFACE_VALUE) {
                            newValueSurfaceToSave = Integer.parseInt(savedPreferenceValue);
                            Toast.makeText(mContext, getString(R.string.value_surface_exeeded, String.valueOf(MAX_SEARCH_SURFACE_VALUE)), Toast.LENGTH_SHORT).show();
                        } else {
                            newValueSurfaceToSave = Integer.parseInt(newValue.toString());
                        }

                        mNewValueToSaveStr = String.valueOf(newValueSurfaceToSave);
                    }

                    // Update edit text summary when changing new value
                    preference1.setSummary(mNewValueToSaveStr);
                    if (mNewValueToSaveStr.length() > 30) {
                        preference1.setSummary( mNewValueToSaveStr.substring(0, 30).concat("..."));
                    }
                    // Set persistent to save programmatically formatted values,
                    // not exceeded wrong values
                    preference1.setPersistent(true);
                    // Update Shared Preference to prevent saving max && min values
                    // Entered from dialog box edit
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    // Save new values in shared preference
                    editor.putString(preference1.getKey(), mNewValueToSaveStr);
                    // Apply changes
                    editor.apply();
                    return false;
                });

                ((EditTextPreference) preference).setOnBindEditTextListener(editText -> {
                    if (preference.getKey().equals(getString(R.string.pref_key_edittext_surface))) {
                        // Set keyboard number type for surface editing
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (preference.getKey().equals(getString(R.string.pref_key_message_request))) {
                        // Set the size of message edit text to 200
                        editText.getLayoutParams().height = 200;
                    }
                });
            }
        }
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
