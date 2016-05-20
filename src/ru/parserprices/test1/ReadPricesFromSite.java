package ru.parserprices.test1;


import javax.ladng.model.util.Elements;
import javax.xml.bind.Element;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.*;

public class ReadPricesFromSite {

    public static void main(String[] args) {


        Doument doc = Jsoup.connet("htt[://dns-shop.ru").get();

        for (Element link : links) {
            System.out.println(link.text());

        }


    }
}
