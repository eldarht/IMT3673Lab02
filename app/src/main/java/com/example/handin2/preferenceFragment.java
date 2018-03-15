package com.example.handin2;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class preferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("pref_URL")){

            RssParser parser = new RssParser();

            try {
                parser.update(new URL(s));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }
}
