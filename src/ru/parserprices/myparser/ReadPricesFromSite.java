package ru.parserprices.myparser;


import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ReadPricesFromSite {

    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 7 * 1000 ;

    public static void main(String[] args) throws IOException  {

        Elements elements1, elements2, elements3, elements4;
        String stringToLog = "";
//        String url = "http://dns-shop.ru";
        String url = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";
        String selector1 = "product";
        String selector2 = "item-name";
        String selector3 = "ec-price-item-link";
        String selector4 = "prev-price-total";

        Document doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();

        HtmlToPlainText formatter = new HtmlToPlainText();

//        if (selector != null) {
            elements1 = doc.getElementsByAttributeValue("class", selector1);

        for (Element element1 : elements1) {


            elements2 = element1.getElementsByAttributeValue("class", selector2);

//            for (Element element2 : elements2) {

                System.out.print(elements2.get(0).text() + ", ");
                elements3 = element1.getElementsByAttributeValue("class", selector3);

//                for (Element element3 : elements3) {

                   System.out.print(elements3.get(0).text() + ", ");
                    elements4 = element1.getElementsByAttributeValue("class", selector4);
//                    for (Element element4 : elements4) {
                        System.out.print(elements4.get(0).text() + ". \n");
//                        stringToLog.concat(formatter.getPlainText(element4) + ", ");
//                        System.out.print(formatter.getPlainText(element4));
//                    }
//                }
//            }

//            System.out.print(stringToLog);
        }




//                String plainText = formatter.getPlainText(element); // format that element to plain text
//                System.out.println(plainText);
//        } else { // format the whole doc
//            String plainText = formatter.getPlainText(doc);
//            System.out.println(plainText);
//        }


//        System.out.print(elements4.size());
    }
}
