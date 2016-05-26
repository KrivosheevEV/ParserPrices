package ru.parserprices.myparser;


import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.*;

public class ReadUseHttpClient {

    WebDriver driver1, driver2;

    public void ReadSite() throws InterruptedException {

        driver1 = new HtmlUnitDriver();

        String siteAdress = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";
        String nextPage = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/#";

        driver1.get(siteAdress);


//        driver1.get(nextPage);
//        List<WebElement> elements1 = driver1.findElements(By.tagName("a"));

//        for (WebElement element1: elements1) {
//
//            if (element1.getAttribute("href") == "#"){
//                System.out.print("3 " + element1.getText() + "\n");
//                element1.click();
//            }

            //System.out.print("1 " + element1.getText() + "\n");

//            List<WebElement> elements2 = element1.findElements(By.className("btn-default"));

//            for (WebElement element2: elements2) {
//                System.out.print("2 " + element2.getTagName() + "\n");
//
//                if (element2.getAttribute("href") == "#"){
//                    System.out.print("3 " + element2.getText() + "\n");
//                    element2.click();
//                }
//            }

//        }
//        System.out.print(element1.getText() + "\n");
//        element1.click();

        List<WebElement> listElements1 = driver1.findElements(By.className("catalog-category-more"));

        for (WebElement element1 : listElements1) {

            WebElement element2 = element1.findElement(By.className("btn-default"));
            System.out.print(element2.getText());
            element2.click();
        }


        List<WebElement> listElements2 = driver1.findElements(By.className("product"));
        System.out.print("\n" + listElements2.size());



//        System.out.println("\n Page title is: " + driver.getCurrentUrl());

    }

    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
}
