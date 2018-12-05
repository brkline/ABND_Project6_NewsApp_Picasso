package com.example.android.abnd_project6_newsapp_picasso;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class NewsItemHelper {

    private static final String LOG_TAG = NewsItemHelper.class.getSimpleName();
    private static final int URL_READ_TIMEOUT = 10000;
    private static final int URL_SET_CONNECTION_TIMEOUT = 15000;

    private NewsItemHelper() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNews(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link NewsItem}s
        List<NewsItem> newsItemList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsItem}s
        return newsItemList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(URL_READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(URL_SET_CONNECTION_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Guardian JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<NewsItem> extractFeatureFromJson(String newsItemJSON) {

        // Create an empty ArrayList that we can start adding news items to
        List<NewsItem> newsItemList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsItemJSON);

            // Extract JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results"
            // which represents a list of results from the query.
            JSONArray resultsArray = response.getJSONArray("results");

            // For each news item in the resultsArray, create an {@link NewsItem} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news item at position i within the list of news items
                JSONObject currentResult = resultsArray.getJSONObject(i);

                // The headline and author are contained in a JSON Object with
                // the key called "fields"
                JSONObject fieldsJsonObject = currentResult.getJSONObject("fields");

                // Extract the value for the key called "headline"
                String headline = fieldsJsonObject.getString("headline");

                // Extract the value for the key called "byline"
                String author = fieldsJsonObject.optString("byline");

                // Extract the path for the thumbnail image
                String thumbnail = fieldsJsonObject.optString("thumbnail");

                // Extract the value for the key called "sectionName"
                String sectionName = currentResult.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentResult.getString("webPublicationDate");

                // Extract the value for the key called "webUrl"
                String url = currentResult.getString("webUrl");

                // Create a new {@link NewsItem} object with the magnitude, location, time,
                // and url from the JSON response.
                NewsItem newsItem = new NewsItem(headline, sectionName, author,
                        webPublicationDate, url, thumbnail);

                // Add the new {@link NewsItem} to the list of newsItems.
                newsItemList.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the newsItem JSON results", e);
        }

        // Return the list of news items
        return newsItemList;
    }

}
