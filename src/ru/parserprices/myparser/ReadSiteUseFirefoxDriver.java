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
//            addToResultString("Count of items: " + Integer.toString(listItems.size()));

            if (listItems.size() > 0) {
//                String cssSelector1 = "div.catalog-category-more, div[style$='block'], a[class='btn btn-default']";
                String cssSelector_NextPage_Block = "div.catalog-category-more, div[style$='block']";
                String cssSelector_NextPage_Button = "a[class='btn btn-default']";
                String cssSelector1 = "div.thumbnail";
                WebElement but_NextPageBlock = driver.findElement(By.cssSelector(cssSelector_NextPage_Block));
                int countOfClick = 1;

                OpenAllPages();



                listItems = driver.findElements(By.cssSelector(cssSelector1));
                addToResultString("Count of items: " + Integer.toString(listItems.size()));
            }

        } catch (Throwable te) {
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

    public void OpenAllPages(){

        while (but_NextPageBlock != null) {

            listItems = driver.findElements(By.cssSelector(cssSelector1));
            addToResultString("Count of items: " + Integer.toString(listItems.size()));

            WebElement but_NextPageButton = but_NextPageBlock.findElement(By.cssSelector(cssSelector_NextPage_Button));

            try {
                while ((new WebDriverWait(driver, 30)).until(ExpectedConditions.not(
                        ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_NextPage_Block))))) {

                    but_NextPageButton.sendKeys(Keys.ESCAPE);
                    but_NextPageButton.click();

                    System.out.print(countOfClick + but_NextPageButton.getText() + "\n");
                    addToResultString(Integer.toString(countOfClick++));
                }

            } catch (Exception ex) {

                but_NextPageBlock = null;
                addToResultString("Ошибка парсинга");
                addToResultString(ex.getMessage());

            } finally {
                System.out.println(countOfClick);
            }
        }
    }

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
