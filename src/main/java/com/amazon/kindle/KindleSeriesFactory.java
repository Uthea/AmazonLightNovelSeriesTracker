package com.amazon.kindle;

import com.vindra.seriestracker.entity.Kindle;
import com.vindra.seriestracker.entity.KindleSeries;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope("prototype")
public class KindleSeriesFactory {

    // url to retrieve individual books
    final private String kindleAsinListAJAXUrl =
            "https://www.amazon.co.jp/kindle-dbs/productPage/ajax/seriesAsinList?asin=%s&pageNumber=1&pageSize=%s";

    // url to access series page
    final private String amazonSeriesUrl =
            "https://www.amazon.co.jp/-/en/gp/product/%s";

    // asin pattern
    final private Pattern ASINPattern = Pattern.compile("[A-Z0-9]{10}");

    // use tor or not
    @Value("${jsoup.tor:true}")
    private boolean tor;



    public KindleSeries createKindleSeries(String seriesAsin) {
        // create new kindle series object and set series asin
        KindleSeries kindleSeries = new KindleSeries();
        kindleSeries.setSeriesAsin(seriesAsin);

        // retrieve kindle series webpage
        System.out.println(seriesAsin);
        Document bookSeries = null;
        if (tor) {
            bookSeries = WebPage.getWebPageWithProxy(String.format(amazonSeriesUrl, seriesAsin));
        } else {
            bookSeries = WebPage.getWebPage(String.format(amazonSeriesUrl, seriesAsin));
        }

        //TODO make sure asin is correct from rest input

        // get number of book from the series webpage
        kindleSeries.setNumberOfBook(getKindleNumberOfBook(bookSeries));

        // get series title from webpage
        kindleSeries.setSeriesTitle(getKindleSeriesTitle(bookSeries));

        // populate series with individual books
        kindleSeries.setKindles(createSeriesBooks(seriesAsin, kindleSeries.getNumberOfBook(), kindleSeries));

        return kindleSeries;
    }

    // main populate function
    private List<Kindle> createSeriesBooks(String seriesASIN, int totalNumberOfBook, KindleSeries kindleSeries) {
        // initialize kindle books list
        List<Kindle> kindleBooks = new ArrayList<>();

        // make call to amazon ajax to get list of kindle books
        Document ajaxResult = null;
        if (tor) {
            ajaxResult = WebPage.getWebPageWithProxy(String.format(kindleAsinListAJAXUrl, seriesASIN, totalNumberOfBook));
        } else {
            ajaxResult = WebPage.getWebPage(String.format(kindleAsinListAJAXUrl, seriesASIN, totalNumberOfBook));
        }



        // loop through the ajax result and populate the list
        for (int i = 1; i <= totalNumberOfBook; i++) {

//            // access specific kindle entry using dom
//            Element bookPage = ajaxResult.getElementById("series-childAsin-item_" + i);
//
//            // create new kindle using kindle factory
//            Kindle kindle = new KindleFactory(tor).createKindle(getKindleAsinFromSeriesPage(bookPage));
//
//            // reference the series
//            kindle.setKindleSeries(kindleSeries);
//
//            // add kindle to the list
//            kindleBooks.add(kindle);

            addBookToKindleSeries(ajaxResult, kindleSeries, kindleBooks, i);
        }

        return kindleBooks;

    }


    public void checkForUpdate(KindleSeries kindleSeries) {
        // TODO also check for existing book entries (not only add new books)

        // retrieve kindle series webpage
        Document bookSeries = null;
        if (tor) {
            bookSeries = WebPage.getWebPageWithProxy(String.format(amazonSeriesUrl, kindleSeries.getSeriesAsin()));
        } else {
            bookSeries = WebPage.getWebPage(String.format(amazonSeriesUrl,  kindleSeries.getSeriesAsin()));
        }

        // retrieve latest number of book
        int latestNumberOfBook = getKindleNumberOfBook(bookSeries);

        // call amazon ajax to get all books listing
        Document ajaxResult = null;
        if (tor) {
            ajaxResult = WebPage.getWebPageWithProxy(String.format(kindleAsinListAJAXUrl,  kindleSeries.getSeriesAsin(), latestNumberOfBook ));
        } else {
            ajaxResult = WebPage.getWebPage(String.format(kindleAsinListAJAXUrl,  kindleSeries.getSeriesAsin(), latestNumberOfBook ));
        }

//        // sort list of kindles inside series
//        Collections.sort(kindleSeries.getKindles());

        // check all existing book and add new book to series (if available)
        for (int i = 1; i <= latestNumberOfBook; i++) {

            // if existing book then check for field update
            if (i <= kindleSeries.getNumberOfBook()) {
                updateKindleExistingFields(ajaxResult, kindleSeries.getKindles().get(i-1), i, "selective");
            } else { // if new book then add to series
                addBookToKindleSeries(ajaxResult, kindleSeries, i);
            }
        }

        // update series number of book
        kindleSeries.setNumberOfBook(latestNumberOfBook);
    }

    // helper function

    private void updateKindleExistingFields(Document ajaxResult, Kindle kindle, int index, String updateType) {
        // access specific kindle entry using dom
        Element bookPage = ajaxResult.getElementById("series-childAsin-item_" + index);

        if (updateType.equals("all")) {
            // create new kindle using kindle factory
            Kindle latestKindle = new KindleFactory(tor).createKindle(getKindleAsinFromSeriesPage(bookPage), index);

            //update kindle information using the latest version
            kindle.updateAllFields(latestKindle);
        } else if (updateType.equals("selective")) {
            boolean kindleUnlimitedStatus = getAjaxKindleUnlimitedStatus(bookPage);
            boolean kindlePreOrderStatus = getAjaxPreOrderStatus(bookPage);
            int price = getAjaxPrice(bookPage);

            kindle.updateExistingField(kindleUnlimitedStatus, kindlePreOrderStatus, price);
        }

    }

    // get info of a book (from ajax result)
    private Boolean getAjaxKindleUnlimitedStatus(Element book) {
        try {
            return (book.getElementsByClass("a-section a-spacing-none a-padding-none productBadgeDetails")
                    .last()
                    .getElementsByClass("a-icon a-icon-kindle-unlimited a-icon-medium")
                    .size() > 0);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    private Boolean getAjaxPreOrderStatus(Element book) {
        try {
            return (book.getElementsByClass("a-button-inner").last()
                    .getElementsByTag("input").last()
                    .attr("aria-label").equals("1-Click <sup>®</sup>で予約注文"));
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    private int getAjaxPrice(Element book) {
        try {
            String rawPrice = book.getElementsByClass("a-size-large a-color-price").text();
            return Integer.parseInt(rawPrice.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Price not found in ajax result");
            return -1;
        }

    }




    // for existing series
    private void addBookToKindleSeries(Document ajaxResult, KindleSeries kindleSeries, int index) {
        // access specific kindle entry using dom
        Element bookPage = ajaxResult.getElementById("series-childAsin-item_" + index);

        // create new kindle using kindle factory
        Kindle kindle = new KindleFactory(tor).createKindle(getKindleAsinFromSeriesPage(bookPage), index);

//        // set kindle vol number
//        kindle.setNo(index);

        // reference the series
        kindle.setKindleSeries(kindleSeries);

        // add kindle to existing series
        kindleSeries.getKindles().add(kindle);

    }

    // for newly created series
    private void addBookToKindleSeries(Document ajaxResult, KindleSeries kindleSeries, List<Kindle> kindleList, int index) {
        // access specific kindle entry using dom
        Element bookPage = ajaxResult.getElementById("series-childAsin-item_" + index);

        // create new kindle using kindle factory
        Kindle kindle = new KindleFactory(tor).createKindle(getKindleAsinFromSeriesPage(bookPage), index);

        // reference the series
        kindle.setKindleSeries(kindleSeries);

        // add kindle to the list
        kindleList.add(kindle);
    }



    private String getKindleAsinFromSeriesPage(Element bookPage) {
        // get the link
        String link = bookPage.
                getElementsByClass("a-size-base-plus a-link-normal itemBookTitle a-text-bold")
                .first().attr("href");

        // regex match the asin
        // if found return it
        Matcher result = ASINPattern.matcher(link);

        if (result.find()) {
            return result.group(0);
        } else {
            return null;
        }
    }

    private String getKindleSeriesTitle(Document bookSeries) {
        return bookSeries.getElementById("collection-title").text();
    }

    private int getKindleNumberOfBook(Document bookSeries) {
        String tempNumber = bookSeries.getElementById("collection-size").text().replaceAll("[^0-9]", "").trim();
        return Integer.parseInt(tempNumber);
    }




}
