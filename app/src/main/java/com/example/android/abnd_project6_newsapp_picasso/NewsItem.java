package com.example.android.abnd_project6_newsapp_picasso;

public class NewsItem {
    private String webTitle;
    private String sectionName;
    private String author;
    private String webPublicationDate;
    private String webURL;
    private String thumbnail;


    /**
     * @param webTitle           is the title of the news item
     * @param sectionName        is the section the news item belongs to
     * @param author             is the author of the news item
     * @param webPublicationDate is the date the article was published
     * @param webURL             is the URL to the news item
     * @param thumbnail          is the path to the thumbnail image
     */
    public NewsItem(String webTitle, String sectionName, String author, String webPublicationDate,
                    String webURL, String thumbnail) {
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.author = author;
        this.webPublicationDate = webPublicationDate;
        this.webURL = webURL;
        this.thumbnail = thumbnail;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthor() {
        return author;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebURL() {
        return webURL;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
