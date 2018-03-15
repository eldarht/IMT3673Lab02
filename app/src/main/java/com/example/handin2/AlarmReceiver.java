package com.example.handin2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by flero on 3/14/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sharedPref.getString("pref_URL", context.getString(R.string.pref_URL_Title));

        RssParser parser = new RssParser();
        try {
            parser.update(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
