package com.example.handin2;

/**
 * Created by flero on 3/7/18.
 */

public class FeedEntry {

    /*Atom element metadata*/
    // public final String author;      //RSS2: author
    // public final String category;    //RSS2: category
    // public final String contributor;
    // public final String generator;
    // public final String icon;
    // public final String id;
    public final String link;           //RSS2: link
    // public final String logo;
    // public final String published;   //RSS2: pubDate
    // public final String rights;
    // public final String source;      //RSS2: source
    // public final String subtitle;
    public final String summary;        //RSS2: description
    public final String title;          //RSS2: title
    // public final String updated;

    /*RSS2 elements of item*/
    // public final String enclosure
    // public final String guid         //Similar to Atom id;


    FeedEntry(String link, String summary, String title){

        this.link = link;
        this.summary = summary;
        this.title = title;
    }

    public String toString(){
        return title + "\n" +link;
    }
}
