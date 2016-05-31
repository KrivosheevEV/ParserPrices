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

        String cssSelector_ExpandPage_Block = "div.catalog-category-more, div[style$='block']";
        String cssSelector_ExpandPage_Button = "a[class='btn btn-default']";
        String cssSelector1 = "div.thumbnail";

        driver.get(siteAdress);

        ExpandAllPages(driver, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector1);

        System.out.print(resultOfPasring);
        driver.close();
        driver.quit();
    }

    // Disable warning in console.
    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
    ///

    public void ExpandAllPages(WebDriver driver, String cssSelector_ExpandPage_Block, String cssSelector_ExpandPage_Button, String cssSelector1) {

        try {

            List<WebElement> listItems = driver.findElements(By.className("thumbnail"));

            if (listItems.size() > 0) {

                WebElement but_NextPageBlock = driver.findElement(By.cssSelector(cssSelector_ExpandPage_Block));
                int countOfClick = 1;

                while (but_NextPageBlock != null) {

                    listItems = driver.findElements(By.cssSelector(cssSelector1));
                    addToResultString("Count of item goods: " + Integer.toString(listItems.size()));

                    WebElement but_NextPageButton = but_NextPageBlock.findElement(By.cssSelector(cssSelector_ExpandPage_Button));

                    try {
                        while ((new WebDriverWait(driver, 30)).until(ExpectedConditions.not(
                                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_ExpandPage_Block))))) {

                            but_NextPageButton.sendKeys(Keys.ESCAPE);
                            but_NextPageButton.click();

                            System.out.print(countOfClick + but_NextPageButton.getText() + "\n");
                            addToResultString(Integer.toString(countOfClick++));
                        }

                    } catch (TimeoutException te) {

                        but_NextPageBlock = null;
                        addToResultString("Timeout exception");
                        //addToResultString(te.getMessage());

                    } finally {
                        addToResultString("Finished expand pages");
                    }
                }

                listItems = driver.findElements(By.cssSelector(cssSelector1));
                addToResultString("Count of item goods: " + Integer.toString(listItems.size()));
            }

        } catch (Throwable te) {
            addToResultString("Error parsing sites.");
            addToResultString(te.getMessage());
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
