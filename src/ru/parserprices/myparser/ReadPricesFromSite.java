package ru.parserprices.myparser;


import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;

public class ReadPricesFromSite {

    public static void main(String[] args) throws IOException, InterruptedException {


        ReadUseHttpClient Class2 = new ReadUseHttpClient();
        Class2.ReadSite();


    }
}
