package ru.parserprices.myparser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ReadPricesFromSite {

    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 5 * 1000;

    public static void main(String[] args) throws IOException  {


        Elements links = null;
        try {
            links = Jsoup.connect("http://dns-shop.ru").get().getElementsByTag("a");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Element link : links) {
            System.out.println(link.text());

        }


    }
}
