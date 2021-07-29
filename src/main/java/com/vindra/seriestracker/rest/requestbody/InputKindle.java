package com.vindra.seriestracker.rest.requestbody;

import java.util.Date;

public class InputKindle {

    private String asin;
    private String title;
    private int price;
    private boolean kindleUnlimited;
    private String publisher;
    private Date publicationDate;
    private String language;
    private String koboId;
    private String seriesAsin;

    public InputKindle(String asin, String title, int price, boolean kindleUnlimited, String publisher, Date publicationDate, String language, String koboId, String seriesAsin) {
        this.asin = asin;
        this.title = title;
        this.price = price;
        this.kindleUnlimited = kindleUnlimited;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.language = language;
        this.koboId = koboId;
        this.seriesAsin = seriesAsin;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isKindleUnlimited() {
        return kindleUnlimited;
    }

    public void setKindleUnlimited(boolean kindleUnlimited) {
        this.kindleUnlimited = kindleUnlimited;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getKoboId() {
        return koboId;
    }

    public void setKoboId(String koboId) {
        this.koboId = koboId;
    }

    public String getSeriesAsin() {
        return seriesAsin;
    }

    public void setSeriesAsin(String seriesAsin) {
        this.seriesAsin = seriesAsin;
    }
}
