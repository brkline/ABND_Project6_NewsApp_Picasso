package com.example.android.abnd_project6_newsapp_picasso;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news items by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsItemLoader extends AsyncTaskLoader<List<NewsItem>> {

    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link NewsItemLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsItemLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsItem> loadInBackground() {
        if (url == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news items.
        List<NewsItem> newsItemList = NewsItemHelper.fetchNews(url);
        return newsItemList;
    }


}
