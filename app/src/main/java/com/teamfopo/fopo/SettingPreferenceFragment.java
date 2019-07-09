package com.teamfopo.fopo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.support.annotation.Nullable;

/**
 * Created by giwon on 2019-07-01.
 */

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    ListPreference soundPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);
        soundPreference = (ListPreference)findPreference("key_push_soundsList");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!prefs.getString("key_push_soundsList", "").equals("")){
            soundPreference.setSummary(prefs.getString("key_push_soundsList", "포포왔숑"));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);

    }// onCreate

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if(key.equals("key_push_soundsList"))
                soundPreference.setSummary(prefs.getString("key_push_soundsList", "포포왔숑"));
        }
    };
}
