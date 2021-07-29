package com.amazon.kindle;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class WebPage {

    //tor proxy
    static Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050));

    private static int requestNumber = 0;

    private static final int timeout = 10;

    private static final int requestLimit = 30;

    public static Document getWebPage(String url) {
        Document doc = null;
        checkCount();


        try {
            doc = Jsoup.connect(url)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .referrer("http://www.google.com")
                    .get();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (doc == null) {
            throw new RuntimeException("Page not Found");
        }

        return doc;

    }

    public static Document getWebPageWithProxy(String url) {
        Document doc = null;
        checkCount();

        // use tor proxy
        try {
            doc = Jsoup.connect(url)
                    .proxy(proxy)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .referrer("http://www.google.com")
                    .get();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (doc == null) {
            throw new RuntimeException("Page not Found");
        }

        return doc;

    }

    private static void checkCount()
    {
        if (requestNumber >= requestLimit) {

            System.out.println(String.format("Rate limit request, sleep for %s minute(s)", timeout));

            try {
                TimeUnit.MINUTES.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            requestNumber = 0;
        } else {
            requestNumber++;
        }
    }
}
