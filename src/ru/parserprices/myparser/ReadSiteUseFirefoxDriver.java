package ru.parserprices.myparser;


//import org.openqa.selenium.*;
import net.sourceforge.htmlunit.corejs.javascript.ast.WhileLoop;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReadSiteUseFirefoxDriver {

    private String resultOfPasring = "";
    long startTime;

    public void ReadSite(String FullAddress) throws InterruptedException {

        startTime = System.currentTimeMillis();
        addToResultString("Start parsing");

//        WebDriver driver = new HtmlUnitDriver();
//        WebDriver driver = new FirefoxDriver();
        FirefoxProfile profile = new FirefoxProfile();
        WebDriver driver = new FirefoxDriver(profile);

        driver.manage().window().maximize();

//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        String siteAdress = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";

        driver.get(siteAdress);

        try {

            List<WebElement> listItems = driver.findElements(By.className("thumbnail"));
            addToResultString("Count of items: " + Integer.toString(listItems.size()));

            if (listItems.size() > 0){
//                String cssSelector1 = "div.catalog-category-more, div[style$='block'] > a[class='btn btn-default']";
                String cssSelector1 = "div.catalog-category-more";
                String cssSelector2 = "div.catalog-category-more";
//                String cssSelectorDisabled = "div.catalog-category-more, div[style$='none']";
                WebElement but_NextPage = driver.findElement(By.cssSelector(cssSelector1));
                int countOfClick = 1;

                while (but_NextPage != null) {

                    listItems = driver.findElements(By.className("thumbnail"));
                    addToResultString("Count of items: " + Integer.toString(listItems.size()));

                    try {
                        (new WebDriverWait(driver, 5))
                                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector2)));

                        but_NextPage = null;

                    } catch (Exception ex) {

                        but_NextPage = but_NextPage.findElement(By.className("btn-default"));

                        but_NextPage.sendKeys(Keys.ESCAPE);
                        but_NextPage.click();

                        System.out.print(countOfClick + but_NextPage.getText() + "\n");
                        addToResultString(Integer.toString(countOfClick++));

                        but_NextPage = driver.findElement(By.cssSelector(cssSelector1));

                    }
                }

                listItems = driver.findElements(By.className("thumbnail"));
                addToResultString("Count of items: " + Integer.toString(listItems.size()));
            }

            //                    try {
//                        (new WebDriverWait(driver, 10))
//                                .until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(but_NextPage)));
//
//                    } catch (TimeoutException te){
            //System.out.print(te.getMessage());



//            List<WebElement> listElements1 = driver.findElements(By.className("catalog-category-more"));
//
//            for (WebElement element1 : listElements1) {
//
//                WebElement element_NexpPage = element1.findElement(By.className("btn-default"));
//                //addToResultString(element_NexpPage.getText());
//                int countOfNewPages = 0;
//               // if (elementCanFind(driver, By.cssSelector("div[stile=*none]"))) {
//                    while (element_NexpPage.findElement(By.cssSelector("div[style*=none]")) != null) {
//                        addToResultString(++countOfNewPages + ". Open new part of items" + "\n");
//                        element_NexpPage.click();
//                    }
//                //}
//
//            }



//            int countOfString = 0;
//            for (WebElement element2 : listItems) {
//                WebElement element_ItemName = element2.findElement(By.className("item-name"));
//                addToResultString(++countOfString + ") " + element_ItemName.getText() + "\n");

        } catch (Throwable te){
            addToResultString("Error parsing sites.");
            addToResultString(te.getMessage());
        }

        System.out.print(resultOfPasring);
        driver.close();
        driver.quit();
    }

    // Disable warning in console.
    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
    ///

    public String getResultOfParsing(){

        return this.resultOfPasring;
    }

    private void addToResultString(String addedString){
        resultOfPasring = resultOfPasring.concat(Long.toString(System.currentTimeMillis() - startTime) + " -> " + addedString + "\n");
    }

    private boolean elementCanFind(WebDriver givenDriver, By givenBy){

        boolean resultOfMetod;
        int maxWaitTime = 10;

        try {
            (new WebDriverWait(givenDriver, maxWaitTime))
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(givenBy));
            resultOfMetod = true;
        } catch (Throwable te) {
            addToResultString("Unable to find the element by classname: '"
                    + givenBy.toString() + "' within " + maxWaitTime + " seconds.");
            resultOfMetod = false;
        }

        return resultOfMetod;
    }

}
