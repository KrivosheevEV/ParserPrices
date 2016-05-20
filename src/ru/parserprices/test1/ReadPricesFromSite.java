package ru.parserprices.test1;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ReadPricesFromSite {

    public static void main(String[] args) {


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
