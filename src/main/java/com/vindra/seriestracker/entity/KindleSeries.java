package com.vindra.seriestracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "kindle_series")
public class KindleSeries {

    @Id
    @Column(name = "series_asin")
    private String seriesAsin;

    @Column(name = "series_title")
    private String seriesTitle;

    @Column(name = "number_of_book")
    private int numberOfBook;

    @OneToMany(mappedBy = "kindleSeries",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    @OrderBy("no") // sort kindle based on no fields when retrieving as POJO
    private List<Kindle> kindles;


    public KindleSeries() {

    }

    public String getSeriesAsin() {
        return seriesAsin;
    }

    public void setSeriesAsin(String seriesAsin) {
        this.seriesAsin = seriesAsin;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public int getNumberOfBook() {
        return numberOfBook;
    }

    public void setNumberOfBook(int numberOfBook) {
        this.numberOfBook = numberOfBook;
    }

    public List<Kindle> getKindles() {
        return kindles;
    }

    public void setKindles(List<Kindle> kindles) {
        this.kindles = kindles;
    }
}
