package com.example.handin2;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import com.example.handin2.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by flero on 3/7/18.
 */

public class RssParser {
    private static InputStream feed = null;

    /*Atom container element*/
    // public final class feed;
    // public final class entry;        //RSS2:item
    // public final class content;

    /*RSS2 optional channel elements*/
    // public final String language
    // public final String copyright
    // public final String managingEditor
    // public final String webMaster
    // public final String pubDate
    // public final String lastBuildDate
    // public final String category
    // public final String generator
    // public final String docs
    // public final String cloud
    // public final String ttl
    // public final String image
    // public final String rating
    // public final String textInput
    // public final String skipHours
    // public final String skipDays


    List<FeedEntry> handler(URL url, int listLength) throws IOException, XmlPullParserException {

        if (feed == null){
            feed = downloadRss(url);
        }

        List<FeedEntry> content  = parse(listLength);

        return content;
    }

    private InputStream downloadRss(URL url) throws IOException{

        final int milliSecond = 1;
        final int second = 1000 * milliSecond;

        // Set up the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10 * second);
        conn.setConnectTimeout(15 * second);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();

        // Return the response
        return conn.getInputStream();
    }

    public List<FeedEntry> parse(int listLength) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(feed, null);
            parser.nextTag();
            return parseXml(parser, listLength);

        } finally {
            feed.close();
        }
    }

    private List<FeedEntry> parseXml(XmlPullParser parser, int listLength) throws XmlPullParserException, IOException {
        List<FeedEntry> entries = null;

        //parser.require(XmlPullParser.START_TAG, nameSpace, "rss");
        String name = parser.getName();

        // Starts by looking for rss or atom tag
        switch (name) {
            case "rss":
                entries = rssParse(parser, listLength);
                break;
            case "feed":
                entries = atomParse(parser, listLength);
                break;
            default:
                skip(parser);
                break;
        }

        return entries;
    }

    private List<FeedEntry> rssParse(XmlPullParser parser, int listLength) throws XmlPullParserException, IOException {
        // Check that xml is for rss
        parser.require(XmlPullParser.START_TAG, null, "rss");
        Log.i("Handin2", "detected rss");

        List<FeedEntry> entries = new ArrayList<>();

        // Check that it has channel element
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, "channel");

        int nrOfEntries = 0;
        // look for item element
        while ((nrOfEntries <= listLength) && (parser.next() != XmlPullParser.END_TAG)) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (Objects.equals(parser.getName(), "item")) {

                entries.add(readItem(parser));
                nrOfEntries++;

            } else {

                skip(parser);

            }

        }
        return entries;
    }
    private List<FeedEntry> atomParse(XmlPullParser parser, int listLength) throws XmlPullParserException, IOException {
        // Check that xml is for atom
        parser.require(XmlPullParser.START_TAG, null, "feed");
        Log.i("Handin2", "detected atom");

        List<FeedEntry> entries = new ArrayList<>();

        int nrOfEntries = 0;
        // look for item element
        while ((nrOfEntries < listLength) && (parser.next() != XmlPullParser.END_TAG)) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (Objects.equals(parser.getName(), "entry")) {

                entries.add(readEntry(parser));
                nrOfEntries++;

            } else {

                skip(parser);

            }

        }
        return entries;
    }

    private FeedEntry readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        String title = null;
        String link = null;
        String summary = null;


        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "title":

                    parser.require(XmlPullParser.START_TAG, null, "title");
                    title = readText(parser);
                    parser.require(XmlPullParser.END_TAG, null, "title");

                    break;
                case "description":   //RSS2 specific

                    parser.require(XmlPullParser.START_TAG, null, "description");
                    summary = readText(parser);
                    parser.require(XmlPullParser.END_TAG, null, "description");

                    break;
                case "link":

                    if (parser.next() == XmlPullParser.TEXT) {
                        link = parser.getText();
                        parser.nextTag();
                    }

                    break;
                default:
                    skip(parser);

                    break;
            }
        }
        return new FeedEntry(link, summary, title);
    }

    private FeedEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "entry");

        String title = null;
        String link = null;
        String summary = null;


        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "title":

                    parser.require(XmlPullParser.START_TAG, null, "title");
                    title = readText(parser);
                    parser.require(XmlPullParser.END_TAG, null, "title");

                    break;
                case "summary":   //RSS2 specific

                    parser.require(XmlPullParser.START_TAG, null, "summary");
                    summary = readText(parser);
                    parser.require(XmlPullParser.END_TAG, null, "summary");

                    break;
                case "link":
                    parser.require(XmlPullParser.START_TAG, null, "link");
                    link = parser.getAttributeValue(null, "href");
                    parser.nextTag();
                    break;
                default:
                    skip(parser);

                    break;
            }
        }
        return new FeedEntry(link, summary, title);
    }
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // goes to the next element with lower depth in the xml tree.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {

            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }

    public void update(URL url){

        try {
            feed = downloadRss(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
