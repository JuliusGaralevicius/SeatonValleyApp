package com.seatonvalleyccapp;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwitterFragment extends Fragment {

    private TwitterConfig config;
    private UserTimeline userTimeline;
    private TweetTimelineRecyclerViewAdapter adapter;
    private RecyclerView twitterTimeline;
    private boolean isConnected = true;

    public TwitterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_twitter, container, false);
        twitterTimeline = (RecyclerView) fragmentView.findViewById(R.id.rc_twitter_timeline);
        twitterTimeline.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set thread policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // If adapter is already stored then retrieve it
        if (GlobalVariables.adapter == null) {
            TwitterAsyncTask twitterAsyncTask = new TwitterAsyncTask();
            twitterAsyncTask.execute();
        } else {
            userTimeline = GlobalVariables.userTimeline;
            adapter = GlobalVariables.adapter;
            twitterTimeline.setAdapter(adapter);
        }
        return fragmentView;
    }

    private class TwitterAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Configure twitter for debugging and usage of API key
                config = new TwitterConfig.Builder(getActivity())
                        .logger(new DefaultLogger(Log.DEBUG))
                        .twitterAuthConfig(new TwitterAuthConfig("V1HemTcSwrpruHipXfHwdTyyE", "Fy47ba3iHdhadxpwtlQqx2q9wMZdnhZ6sgpTSeIG7ZSS9KBKHy"))
                        .debug(true)
                        .build();
                Twitter.initialize(config);

                // Choose user twitter timeline to show
                userTimeline = new UserTimeline.Builder()
                        .screenName("SeatonValleyCC")
                        .maxItemsPerRequest(5)
                        .build();

                // If threre is no internet connection then don't even initialize an adapter
                if (isNetworkAvailable())
                    adapter = new TweetTimelineRecyclerViewAdapter.Builder(getActivity())
                            .setTimeline(userTimeline)
                            .build();

            } catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (isConnected) {
                twitterTimeline.setAdapter(adapter);
                // Pass parameters in order to retrieve them later
                GlobalVariables.userTimeline = userTimeline;
                GlobalVariables.adapter = adapter;
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
