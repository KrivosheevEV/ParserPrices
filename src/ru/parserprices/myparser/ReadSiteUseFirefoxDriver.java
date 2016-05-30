package ru.parserprices.myparser;


//import org.openqa.selenium.*;
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

    private String resultOfPasring;

    public void ReadSite(String FullAddress) throws InterruptedException {

//        WebDriver driver = new HtmlUnitDriver();
//        WebDriver driver = new FirefoxDriver();
        FirefoxProfile profile = new FirefoxProfile();
        WebDriver driver = new FirefoxDriver(profile);

        String siteAdress = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/";
        String nextPage = "http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/#";

        driver.get(siteAdress);

        List<WebElement> listElements2 = driver.findElements(By.className("thumbnail"));
        resultOfPasring = resultOfPasring.concat("\n" + listElements2.size() + "\n");
//        System.out.print();

        List<WebElement> listElements1 = driver.findElements(By.className("catalog-category-more"));

        for (WebElement element1 : listElements1) {

            WebElement element2 = element1.findElement(By.className("btn-default"));
//            WebElement element2 = element1.findElement(By.className("glyphicon-triangle-bottom"));
            resultOfPasring = resultOfPasring.concat(element2.getText() + "\n");
//            System.out.print(element2.getText() + "\n");
            element2.click();

        }


        int maxWaitTime = 10;
        String className = "products-list-continue";
        try {
            (new WebDriverWait(driver, maxWaitTime))
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(By.className(className)));

        } catch (Throwable te) {
            System.out.print("Unable to find the element by classname: '"
                    + className + "' within " + maxWaitTime + " seconds.");
        }


        listElements2 = driver.findElements(By.className("thumbnail"));
        resultOfPasring = resultOfPasring.concat("\n" + listElements2.size());

        System.out.print(resultOfPasring);

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

    public void writeResultToFile(String fileFullAddress, String textForFile){

    }
}
