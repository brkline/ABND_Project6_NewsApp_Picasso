package com.example.android.abnd_project6_newsapp_picasso;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<NewsItem>> {

    /**
     * URL for news item data from the Guardian dataset
     */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/";


    // Constant value for the news items loader ID.
    private static final int NEWS_ITEM_LOADER_ID = 1;

    private NewsItemAdapter newsItemAdapter;
    private List<NewsItem> newsItemList;
    private String QUERY_FIELD_LIST = "headline,byline,thumbnail";
    // API Key removed.  Get yours here: https://open-platform.theguardian.com/access/
    private String API_KEY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        newsItemList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // Implement our custom OnClickListener so we can open the browser when
        // a news item is selected
        newsItemAdapter = new NewsItemAdapter(newsItemList, new
                NewsItemOnClickListener() {
                    @Override
                    public void OnItemClick(View v, int position) {

                        // Find the current news item that was clicked on
                        NewsItem currentNewsItem = newsItemList.get(position);

                        // Convert the String URL into a URI object (to pass into the Intent constructor)
                        Uri newsItemUri = Uri.parse(currentNewsItem.getWebURL());

                        // Create a new intent to view the news item URI
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                        // Send the intent to launch a new activity
                        startActivity(websiteIntent);
                    }
                });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NewsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(newsItemAdapter);
        // Code below based on:
        // https://www.grokkingandroid.com/first-glance-androids-recyclerview/
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_ITEM_LOADER_ID, null, this);
        } else {
            // Set empty state text to display "No Internet Connection."
            TextView emptyStateTextView = findViewById(R.id.empty_view);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sectionSelected = sharedPrefs.getString(
                getString(R.string.news_settings_section_key),
                getString(R.string.news_settings_section_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        // https://content.guardianapis
        // .com/technology?show-fields=headline%2Cbyline%2Cthumbnail&api-key=6ebdf018-f6e7-45f3-94bb-b41a4e4d1190
        uriBuilder.appendPath(sectionSelected);
        uriBuilder.appendQueryParameter("show-fields", QUERY_FIELD_LIST);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        // Create a new loader for the given URL
        return new NewsItemLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItemList) {

        // If there is a valid list of {@link NewsItem}s, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (newsItemList != null && !newsItemList.isEmpty()) {
            newsItemAdapter.updateNewsItemsList(newsItemList);
        } else {
            // Set empty state text to display "No news items found."
            TextView emptyStateTextView = findViewById(R.id.empty_view);
            emptyStateTextView.setText(R.string.no_news_items);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        loader.reset();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, NewsItemSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
