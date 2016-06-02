package ru.parserprices.myparser;


//import org.openqa.selenium.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.*;

public class ReadSiteDNS {

    private String resultOfPasring = "";
    private long startTime;

    private static String DNG_GENERAL_URL = "http://www.dns-shop.ru";
    private static int WAITING_FOR_EXPAND = 10;

    public void ReadSite(String FullAddress) throws InterruptedException {

        startTime = System.currentTimeMillis();
        addToResultString("Start parsing");

        // Proxy setting.
        ProxyServers newProxyServer = new ProxyServers();
        String proxyAddress = null;
        try {
            proxyAddress = newProxyServer.getProxyServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress);
        proxy.setSslProxy(proxyAddress);

        System.out.println(proxyAddress);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);

//        FirefoxProfile profile = new FirefoxProfile();
        WebDriver driver = new FirefoxDriver();

//        driver.manage().window().maximize();

//        driver.navigate().to("https://2ip.ru/");
//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        String cssSelector_ExpandPage_Block = "div.catalog-category-more, div[style$='block']";
        String cssSelector_ExpandPage_Button = "a[class='btn btn-default']";
        String cssSelector_Categories = "div.menu-item-category-wrapper a[href]";
        String cssSelector_GoodLink = "div.item-name a[href]";
        String cssSelector_GoodTitle = "h1[class='page-title price-item-title']";
        String cssSelector_GoodCode = "div.price-item-code span";
        String cssSelector_GoodPricePrevious = "div.previous-price s.prev-price-total";
        String cssSelector_GoodPrice = "meta[itemprop=price]";

        ArrayList<String> listLinkPages = new ArrayList<String>();
        //listLinkPages.add("http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/");
        //listPages.add("http://www.dns-shop.ru/catalog/31f05737df7e4e77/ssd-25-sata-nakopiteli/");

        //fillListPagesFromSite(driver, cssSelector_Categories, listLinkPages);
        fillListPagesFromFile(listLinkPages);



        ArrayList<String> listLinkGoods = new ArrayList<String>();

        for (int countSites = 0; countSites < listLinkPages.size(); countSites++) {

//            addToResultString(Integer.toString(countSites + 1));

            // Open browser.
//            addToResultString("Open page: " + listLinkPages.get(countSites));
            driver.navigate().to(listLinkPages.get(countSites));

            // Expand all pages with goods.
//            addToResultString("Start expand pages");
            expandAndReadLinkGoods(driver, listLinkGoods, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodLink);

        }

        readGoodDescription(driver, listLinkGoods, cssSelector_GoodTitle, cssSelector_GoodCode, cssSelector_GoodPricePrevious, cssSelector_GoodPrice);

        // Close browser.
//        driver.close();
        driver.quit();

//        System.out.println(listPages.size());
        System.out.print(resultOfPasring);

    }

    // Disable warning in console.
    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
    ///

    public void expandAndReadLinkGoods(WebDriver driver, ArrayList<String> listLinkGoods, String cssSelector_ExpandPage_Block, String cssSelector_ExpandPage_Button, String cssSelector1) {

        try {

            List<WebElement> listItems = driver.findElements(By.cssSelector(cssSelector1));

            if (listItems.size() > 0) {

                WebElement but_NextPageBlock = driver.findElement(By.cssSelector(cssSelector_ExpandPage_Block));

                while (but_NextPageBlock != null) {

                    listItems = driver.findElements(By.cssSelector(cssSelector1));

                    WebElement but_NextPageButton = but_NextPageBlock.findElement(By.cssSelector(cssSelector_ExpandPage_Button));

                    try {
                        while ((new WebDriverWait(driver, WAITING_FOR_EXPAND)).until(ExpectedConditions.not(
                                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_ExpandPage_Block))))) {

                            but_NextPageButton.sendKeys(Keys.ESCAPE);
                            but_NextPageButton.click();

                        }

                    } catch (TimeoutException te) {

                        // Expand button is invisible.

                        but_NextPageBlock = null;
                        addToResultString("Timeout exception");
                        //addToResultString(te.getMessage());

//                        addToResultString("Count of item goods: " + Integer.toString(listItems.size()));

                        listItems = driver.findElements(By.cssSelector(cssSelector1));
                        addToResultString("Count of item goods: " + Integer.toString(listItems.size()));

                        for (WebElement hrefElement : listItems
                                ) {
                            String linkGood = hrefElement.getAttribute("href");
                            listLinkGoods.add(linkGood);
//                            addToResultString(linkGood);
                        }

                    } finally {
                        //addToResultString("Finished expand pages");
                    }
                }

            }

        } catch (Throwable te) {
            addToResultString("Error parsing site (timeout expand)");
            //addToResultString(te.getMessage());

        }
    }

    private void fillListPagesFromSite(WebDriver driver, String cssSelector_Categories, ArrayList<String> listPages) {

        driver.navigate().to(DNG_GENERAL_URL);

        try {

            List<WebElement> listItems = driver.findElements(By.cssSelector(cssSelector_Categories));

            for (WebElement hrefElement : listItems
                    ) {
                String linkCategory = hrefElement.getAttribute("href");
                listPages.add(linkCategory);
                System.out.println(linkCategory);
            }

        } catch (Throwable te) {
            addToResultString("Error parsing site (fill category).");
            addToResultString(te.getMessage());
        }
    }

    private void fillListPagesFromFile(ArrayList<String> listPages){

        ReadWriteFile mFileWithCategories = new ReadWriteFile("Categories_DNS.txt");

        String mFile = mFileWithCategories.readFile();
        listPages = new ArrayList<String>(Arrays.asList(mFile.split("\n")));

    }


    private void readGoodDescription(WebDriver driver, ArrayList<String> listLinkGoods, String cssSelector_GoodTitle, String cssSelector_GoodCode, String cssSelector_GoodPricePrevious, String cssSelector_GoodPrice) {

        int countIteration = 0;
        for (String linkGood : listLinkGoods) {
            countIteration++;
            driver.navigate().to(linkGood);
            try {
                addToResultString("Goods title:" + driver.findElement(By.cssSelector(cssSelector_GoodTitle)).getText());
                addToResultString("Goods code :" + driver.findElement(By.cssSelector(cssSelector_GoodCode)).getText());
                addToResultString("Goods price:" + driver.findElement(By.cssSelector(cssSelector_GoodPrice)).getAttribute("content"));
                List<WebElement> listGoodPricePreviuos = driver.findElements(By.cssSelector(cssSelector_GoodPricePrevious));
                if (listGoodPricePreviuos.size() > 0) addToResultString("Goods previous price:" + listGoodPricePreviuos.get(0).getText());
            } catch (Throwable te) {
                addToResultString("Error parsing site (get description)");
                addToResultString(te.getMessage());
            }

            if (countIteration == 5) break;
        }

    }

    public String getResultOfParsing() {

        return this.resultOfPasring;
    }

    private void addToResultString(String addedString) {
        //if (addedString.isEmpty()) return;
        String timeForResult = Long.toString((System.currentTimeMillis() - startTime) / 1000) + "." + Long.toString((System.currentTimeMillis() - startTime) % 1000);
        resultOfPasring = resultOfPasring.concat(timeForResult + " -> " + addedString + "\n");
    }

    private boolean elementCanFind(WebDriver givenDriver, By givenBy) {

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
