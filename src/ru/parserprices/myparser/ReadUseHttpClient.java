package ru.parserprices.myparser;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

public class ReadUseHttpClient {

    WebDriver driver1, driver2;

    public void ReadSite() throws InterruptedException {

//        driver1 = new HtmlUnitDriver();
        driver1 = new FirefoxDriver();

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

        List<WebElement> listElements2 = driver1.findElements(By.className("thumbnail"));
        System.out.print("\n" + listElements2.size() + "\n");

        List<WebElement> listElements1 = driver1.findElements(By.className("catalog-category-more"));

        for (WebElement element1 : listElements1) {

            WebElement element2 = element1.findElement(By.className("btn-default"));
//            WebElement element2 = element1.findElement(By.className("glyphicon-triangle-bottom"));
            System.out.print(element2.getText() + "\n");
            element2.click();

//            Actions actions = new Actions(driver1);
//            actions.moveToElement(element2, 1, 1).click();

//            JavascriptExecutor executor = (JavascriptExecutor) driver1;
//            executor.executeScript("arguments[0].click();", element2);
        }

//        Thread.sleep(5000);

        int maxWaitTime = 5;
        String className = "products-list-continue";
        try {
            (new WebDriverWait(driver1, maxWaitTime))
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(By.className(className)));

        } catch (Throwable te) {
            System.out.print("Unable to find the element by classname: '"
                    + className + "' within " + maxWaitTime + " seconds.");
            //throw new TimeoutException(te);
        }



        listElements2 = driver1.findElements(By.className("thumbnail"));
        System.out.print("\n" + listElements2.size());

//        System.out.println("\n Page title is: " + driver.getCurrentUrl());

    }

    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
}
