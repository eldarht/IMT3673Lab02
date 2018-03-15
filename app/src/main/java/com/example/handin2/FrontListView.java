package com.example.handin2;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FrontListView extends AppCompatActivity{

    private int listLength;
    private List<FeedEntry> listContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPref.getString("pref_URL", getString(R.string.pref_URL_Title));
        listLength = Integer.parseInt(sharedPref.getString("pref_lineCount_Title",  getString(R.string.pref_lineCount_Default)));
        int updateFequence = Integer.parseInt(sharedPref.getString("pref_updateInterval_Title",  getString(R.string.pref_updateInterval_Default)));

        RssHandler rssHandler = new RssHandler();

        try {
            rssHandler.execute(new URL(url));
        } catch (MalformedURLException e) {

            Log.v("Handin2", "Not a solvable url");

            Toast toast = Toast.makeText(this, R.string.toast_error_URL, Toast.LENGTH_SHORT);
            toast.show();

        }

        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Log.i("Handin2", "Setting alarm to update every" + updateFequence + " minutes");

        assert alarmMgr != null;
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,(
                SystemClock.elapsedRealtime() + 1000 * 60 * updateFequence),1000 * 60 * updateFequence, alarmIntent);

    }


    public void startActivityPreference(View view){
        Intent intent = new Intent(this, preference.class);

        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int requestStatus, Intent intent) {

        if (requestCode == 1) { // fill list
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            RssParser parser = new RssParser();
            try {
                listContent = parser.parse(Integer.parseInt(sharedPref.getString("pref_lineCount_Title",  getString(R.string.pref_lineCount_Default))));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class RssHandler extends AsyncTask<URL, Void, List<FeedEntry> >{

        @Override
        protected List<FeedEntry> doInBackground(URL... url){

            RssParser parser = new RssParser();

            List<FeedEntry> content;
            try {

                content = parser.handler(url[0], listLength);

            } catch (IOException e) {

                Log.v("Handin2", "Failed to connect to url");

                return null;

            } catch (XmlPullParserException e) {

                Log.v("Handin2", "Url did not yield a valid XML file");

                return null;
            }

            return content;
        }

        @Override
        protected void onPostExecute(List<FeedEntry> result){

            if (result == null){
                Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_error_URL, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_sucsess_URL, Toast.LENGTH_SHORT);
                toast.show();
            }

            listContent = result;
            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activeContext, R.array.ui_listTest, android.R.layout.simple_list_item_1);
            ArrayAdapter<FeedEntry> adapter = new ArrayAdapter<FeedEntry>(getApplicationContext(), android.R.layout.simple_list_item_1, listContent);

            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Adapter adapter = adapterView.getAdapter();
                    FeedEntry entry = (FeedEntry) adapter.getItem(i);

                    if (entry.getClass() == FeedEntry.class){

                        Intent intent = new Intent(getBaseContext(), DisplayMainContent.class);

                        intent.putExtra("Content", entry.summary);

                        startActivityForResult(intent, 1);
                    }
                }

            });
        }

    }
}
