package ru.parserprices.myparser;


//import org.openqa.selenium.*;
import net.sourceforge.htmlunit.corejs.javascript.ast.WhileLoop;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

public class ReadSiteUseFirefoxDriver {

    private String resultOfPasring = "";

    public void ReadSite(String FullAddress) throws InterruptedException {

//        WebDriver driver = new HtmlUnitDriver();
//        WebDriver driver = new FirefoxDriver();
        FirefoxProfile profile = new FirefoxProfile();
        WebDriver driver = new FirefoxDriver(profile);

        String siteAdress = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";
        String nextPage = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/#";

        driver.get(siteAdress);

        try {

            List<WebElement> listElements2 = driver.findElements(By.className("thumbnail"));
            addToResultString(Integer.toString(listElements2.size()));

            List<WebElement> listElements1 = driver.findElements(By.className("catalog-category-more"));

            for (WebElement element1 : listElements1) {

                WebElement element_NexpPage = element1.findElement(By.className("btn-default"));
                //addToResultString(element_NexpPage.getText());
                int countOfNewPages = 0;
               // if (elementCanFind(driver, By.cssSelector("div[stile=*none]"))) {
                    while (element_NexpPage.findElement(By.cssSelector("div[style*=none]")) != null) {
                        addToResultString(++countOfNewPages + ". Open new part of items" + "\n");
                        element_NexpPage.click();
                    }
                //}

            }

            listElements2 = driver.findElements(By.className("thumbnail"));

            int countOfString = 0;
            for (WebElement element2 : listElements2) {
                WebElement element_ItemName = element2.findElement(By.className("item-name"));
                addToResultString(++countOfString + ") " + element_ItemName.getText() + "\n");
            }

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
        resultOfPasring = resultOfPasring.concat(resultOfPasring + addedString + "\n");
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
