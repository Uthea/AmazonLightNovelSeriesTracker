package com.vindra.seriestracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;;
import java.util.Date;

@Scope("prototype")
@Entity
@Table(name = "kindle")
public class Kindle  implements Comparable<Kindle> {

    @Id
    @Column(name = "asin")
    private String asin;

    @Column(name = "no")
    private int no;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private int price;

    @Column(name = "kindle_unlimited")
    private boolean kindleUnlimited;

    @Column(name = "pre_order")
    private boolean preOrder;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "language")
    private String language;

    @Column(name = "kobo_id")
    private String koboId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_asin")
    @JsonIgnore
    private KindleSeries kindleSeries;

    @Column(name = "series_asin", updatable=false, insertable=false)
    private String seriesAsin;


    public Kindle () {

    }

    public boolean isPreOrder() {
        return preOrder;
    }

    public void setPreOrder(boolean preOrder) {
        this.preOrder = preOrder;
    }

    public String getSeriesAsin() {
        return seriesAsin;
    }

    public void setSeriesAsin(String seriesAsin) {
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


    public KindleSeries getKindleSeries() {
        return kindleSeries;
    }

    public void setKindleSeries(KindleSeries kindleSeries) {
        this.kindleSeries = kindleSeries;
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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void updateAllFields(Kindle latestVersion) {
        this.asin = latestVersion.getAsin();
        this.title = latestVersion.getTitle();
        this.price = latestVersion.getPrice();
        this.kindleUnlimited = latestVersion.isKindleUnlimited();
        this.preOrder = latestVersion.isPreOrder();
        this.publisher = latestVersion.getPublisher();
        this.publicationDate = latestVersion.getPublicationDate();
        this.language = latestVersion.getLanguage();
        this.koboId = latestVersion.getKoboId();
        this.seriesAsin = latestVersion.getSeriesAsin();
    }

    @Override
    public int compareTo(Kindle o) {
        return Integer.compare(this.no, o.no);
    }

    public void updateExistingField(boolean kindleUnlimitedStatus, boolean kindlePreOrderStatus, int price) {
        this.kindleUnlimited = kindleUnlimitedStatus;
        this.preOrder = kindlePreOrderStatus;
        if (price != -1) this.price = price;
    }
}
