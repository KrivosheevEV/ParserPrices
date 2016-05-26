package ru.parserprices.myparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ReadUseJsoup {

    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 7 * 1000;

    public static void main2(String[] args) throws IOException {

        Elements elements10, elements11, elements12, elements13;
        String stringToLog = "";
        int countOfString = 0;
//        String url = "http://dns-shop.ru";
        String url = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";
        String selector10 = "product";
        String selector11 = "item-name";
        String selector12 = "ec-price-item-link";
        String selector13 = "price-total";

        Document doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();

        //HtmlToPlainText formatter = new HtmlToPlainText();

        elements10 = doc.getElementsByAttributeValue("class", selector10);

        for (Element element10 : elements10) {

            stringToLog = countOfString++ + ") ";

            elements11 = element10.getElementsByAttributeValue("class", selector11);

            if (elements11.size() > 0) stringToLog = stringToLog.concat(elements11.get(0).text() + ", ");
            elements12 = element10.getElementsByAttributeValue("class", selector12);


            if (elements12.size() > 0) stringToLog = stringToLog.concat(elements12.get(0).text() + ", ");
            elements13 = element10.getElementsByAttributeValue("data-of", selector13);

            if (elements13.size() > 0) stringToLog = stringToLog.concat(elements13.get(0).text());

            stringToLog = stringToLog.concat("\n");

            System.out.print(stringToLog);

        }
    }
}
