package com.amazon.kindle;

import com.vindra.seriestracker.entity.Kindle;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KindleFactory {

    // pattern for asin and kobo ebook id
    final private Pattern ASINPattern = Pattern.compile("[A-Z0-9]{10}");
    final private Pattern KoboIdPattern = Pattern.compile("[^\\/]{22}");

    // parameter -> (book title, book title)
    final private String koboLightNovelSearchUrl =
            "https://www.kobo.com/jp/en/search?query=%s&id=dac63710-e136-4309-ac7f-d517e2202171&nd=true&ac=1&ac.title=%s&sort=PublicationDateAsc";

    // date formatter for time and date
    final DateFormat df = new SimpleDateFormat("yyyy/M/dd", Locale.ENGLISH);
    final DateFormat outputDate = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);

    // use tor or not
    private boolean tor;


    public KindleFactory(boolean tor) {
        this.tor = tor;
    }

    public Kindle createKindle(String asin, int vol) {
        // create empty kindle object
        Kindle kindle = new Kindle();

        // TODO check if asin valid
        //

        // retrieve the document using asin
        Document book = null;
        if (this.tor) {
            book = WebPage.getWebPageWithProxy("https://www.amazon.co.jp/dp/" + asin);
        } else {
            book = WebPage.getWebPage("https://www.amazon.co.jp/dp/" + asin);
        }


        // TODO if book null then throw error


        // extract product details
        Elements productDetails = getProductDetails(book);

        // fill all the kindle fields

        // fill using whole document
        String title = getKindleTitle(book);

        kindle.setNo(vol);
        kindle.setAsin(asin);
        kindle.setTitle(title);
        kindle.setKoboId(getKoboEbookId(title));
        kindle.setPrice(getKindlePrice(book));
        kindle.setKindleUnlimited(getKindleUnlimitedStatus(book));
        kindle.setPreOrder(getPreOrderStatus(book));


        // fill using product details
        kindle.setPublisher(getKindlePublisher(productDetails));
        kindle.setPublicationDate(getKindlePublicationDate(productDetails));
        kindle.setLanguage(getKindleLanguage(productDetails));


        // return filled kindle
        return  kindle;
    }

    private String getKindleLanguage(Elements productDetails) {
        // get the language
        return productDetails.get(3).
                getElementsByTag("span")
                .last().text(); // value is in the last elements
    }

    private Date getKindlePublicationDate(Elements productDetails) {
        // get the raw date from DOM
        String rawDate = productDetails.get(2).
                getElementsByTag("span")
                .last().text(); // value is in the last elements

        // convert raw string to date format
        Date date = null;

        try {
            date = df.parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private String getKindlePublisher(Elements productDetails) {
        // get the publisher
        return productDetails.get(1).
                getElementsByTag("span")
                .last().text() // value is in the last elements
                .replaceAll("\\(.*\\)", "").trim(); // remove publication date
    }

    private boolean getKindleUnlimitedStatus(Document book) {
        return (book.getElementsByClass("a-icon a-icon-kindle-unlimited a-icon-medium").size() > 0);
    }

    private boolean getPreOrderStatus (Document book) {
        return (book.getElementById("oneClick-preorder-button-announce") != null);
    }

    private int getKindlePrice(Document book) {
        String tempPrice = book.getElementsByClass("kindle-price").last().text();
        return Integer.parseInt(tempPrice.replaceAll("[^0-9]", "")); //keep numbers only
    }

    private String getKoboEbookId(String title) {
        // remove imprint from title
        String cleanedTitle = title.replaceAll("\\([^0-9].*\\)", "").trim();

        // convert japanese char to url encoded string
        String encodedTitle = null;
        try {
            encodedTitle = URLEncoder.encode(cleanedTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // get search result using encoded title
        Document koboEbook = null;
        if (tor) {
            koboEbook = WebPage.getWebPageWithProxy(String.format(this.koboLightNovelSearchUrl, encodedTitle, encodedTitle));
        } else {
            koboEbook = WebPage.getWebPage(String.format(this.koboLightNovelSearchUrl, encodedTitle, encodedTitle));
        }

        // if search result is empty return null
        if (isKoboSearchResultEmpty(koboEbook)) {
            return null;
        }

        //TODO implement title checking between kobo and kindle
        //

        // get kobo ebook id
        return extractKoboId(koboEbook);
    }

    private String getKindleTitle(Document book) {
        return book.getElementById("productTitle").text();
    }


    // helper function
    private Elements getProductDetails(Document book) {
        return book.getElementById("detailBullets_feature_div")
                .getElementsByTag("ul")
                .get(0)
                .getElementsByTag("li");
    }

    private String extractKoboId(Document koboEbook) {
        // retrieve kobo url with DOM
        String koboEbookUrl = koboEbook.getElementsByClass("title product-field")
                .first()
                .child(0)
                .attr("href");

        // extract the id (which is of length 22 char)
        Matcher result = KoboIdPattern.matcher(koboEbookUrl);

        // return matched regex is found
        if (result.find()) {
            return result.group(0);
        } else {
            return null;
        }
    }

    private boolean isKoboSearchResultEmpty(Document koboEbook) {
        return koboEbook.getElementsByClass("empty-search-result").size() > 0;
    }





}
