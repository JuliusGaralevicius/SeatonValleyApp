package com.seatonvalleyccapp;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seatonvalleyccapp.News.NewsItem;
import com.seatonvalleyccapp.News.RecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<NewsItem> newsItemList;
    private boolean isConnected = true;

    // Jsoup
    private Document document;
    private String url;
    // Titles
    private Elements titleElements;
    private ArrayList<String> titleList;
    // Short Description
    private Elements shortDescriptionElements;
    private ArrayList<String> shortDescriptionList;
    // Thumbnail
    private Elements thumbnailElements;
    private ArrayList<String> thumbnailList;
    // URL (uses titleElements)
    private ArrayList<String> urlList;


    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        // Recycler View
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set thread policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // If newsItemList is already stored then retrieve it
        if (GlobalVariables.newsItemList == null || GlobalVariables.newsItemList.size() != 8) {
            // Jsoup
            url = "http://seatonvalleycommunitycouncil.gov.uk/news/";
            titleList = new ArrayList<>();
            newsItemList = new ArrayList<>();
            shortDescriptionList = new ArrayList<>();
            thumbnailList = new ArrayList<>();
            urlList = new ArrayList<>();

            // Jsoup thread
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
            jsoupAsyncTask.execute();
        } else {
            newsItemList = GlobalVariables.newsItemList;
            adapter = new RecyclerViewAdapter(getActivity(), newsItemList);
            recyclerView.setAdapter(adapter);
        }


        return view;
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Connect with the website and get all wanted elements
            try {
                document = Jsoup.connect(url).get();
                titleElements = document.select("h3.gdlr-blog-title");
                shortDescriptionElements = document.select("div.gdlr-blog-content");
                thumbnailElements = document.select("div.gdlr-blog-thumbnail");
                // Loop through 0-7 because the website shows 8 news articles at a time
                Drawable drawable = null;
                InputStream inputStream = null;
                for (int i = 0; i < 8; i++) {
                    titleList.add(titleElements.get(i).text());
                    shortDescriptionList.add(shortDescriptionElements.get(i).text().replace("Read More", ""));
                    thumbnailList.add(thumbnailElements.get(i).getElementsByTag("img").attr("src"));
                    urlList.add(titleElements.get(i).getElementsByTag("a").attr("href"));

                    inputStream = (InputStream) new URL(thumbnailList.get(i)).getContent();
                    drawable = Drawable.createFromStream(inputStream, "src name");
                    newsItemList.add(new NewsItem(titleList.get(i), shortDescriptionList.get(i),
                            drawable, urlList.get(i)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            // Add items in adapter after they have been created
            if (isConnected) {
                adapter = new RecyclerViewAdapter(getActivity(), newsItemList);
                recyclerView.setAdapter(adapter);
                // Pass the newsItemList in order to retrieve it later
                GlobalVariables.newsItemList = newsItemList;
            }

        }

    }
}