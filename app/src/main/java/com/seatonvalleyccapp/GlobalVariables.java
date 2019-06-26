package com.seatonvalleyccapp;

import android.graphics.Bitmap;

import com.seatonvalleyccapp.News.NewsItem;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;

/**
 * Created by julius on 29/03/2018.
 */

public class GlobalVariables {
    public static Bitmap currentBitmap = null;
    public static UserTimeline userTimeline = null;
    public static TweetTimelineRecyclerViewAdapter adapter = null;
    public static ArrayList<NewsItem> newsItemList = null;

}
