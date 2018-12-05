package com.example.android.abnd_project6_newsapp_picasso;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.NewsItemViewHolder> {

    NewsItemOnClickListener newsItemListener;
    private List<NewsItem> newsItemList;
    private NewsItem currentNewsItem;
    private String thumbnailPath;
    private Uri thumbnailUri;

    public NewsItemAdapter(List<NewsItem> newsItemList, NewsItemOnClickListener
            newsItemListener) {
        this.newsItemList = newsItemList;
        this.newsItemListener = newsItemListener;
    }

    @NonNull
    @Override
    public NewsItemAdapter.NewsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item,
                parent, false);
        final NewsItemViewHolder viewHolder = new NewsItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsItemListener.OnItemClick(v, viewHolder.getPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsItemAdapter.NewsItemViewHolder holder, int position) {
        // Code below based on GitHub example provided by TheBaileyBrew ABND Student Leader
        // https://github.com/TheBaileyBrew/Angry_Nerds_Demo_Recycler
        currentNewsItem = newsItemList.get(position);
        holder.titleTextView.setText(currentNewsItem.getWebTitle());
        holder.sectionTextView.setText(currentNewsItem.getSectionName());
        holder.authorTextView.setText(currentNewsItem.getAuthor());
        holder.datePublishedTextView.setText(formatDate(currentNewsItem.getWebPublicationDate()));
        Context context = holder.thumbnailImageView.getContext();
        // https://www.101apps.co.za/index.php/articles/android-recyclerview-and-picasso-tutorial.html
        thumbnailPath = currentNewsItem.getThumbnail();
        thumbnailUri = Uri.parse(thumbnailPath);
        Picasso.with(context).load(thumbnailUri).into(holder
                .thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
    }

    // Clear the list, add the newly generated news items, and notify the
    // RecyclerView Adapter that there is a change
    public void updateNewsItemsList(List<NewsItem> newsItemList) {
        this.newsItemList.clear();
        this.newsItemList.addAll(newsItemList);
        notifyDataSetChanged();
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String publishedDate) {
        // Code below based on:
        // https://stackoverflow.com/a/4216767
        // https://www.codota.com/code/java/methods/java.text.DateFormat/parse
        DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        iso8601.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        try {
            date = iso8601.parse(publishedDate);
        } catch (ParseException e) {
            Log.e("WEBPUB:", "Problem parsing publish date", e);
        }
        // End attribution
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(date);
    }

    // Code below based on:
    // https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
    // https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
    public class NewsItemViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView sectionTextView;
        private TextView authorTextView;
        private TextView datePublishedTextView;
        private ImageView thumbnailImageView;



        public NewsItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.news_item_title);
            sectionTextView = itemView.findViewById(R.id.news_item_section);
            authorTextView = itemView.findViewById(R.id.news_item_author);
            datePublishedTextView = itemView.findViewById(R.id.news_item_date);
            thumbnailImageView = itemView.findViewById(R.id.news_item_thumbnail);

        }

    }

}
