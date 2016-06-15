package ru.parserprices.myparser;


//import org.openqa.selenium.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReadSiteDNS {

    private String resultOfPasring = "";
    private long startTime;

    private static String DNG_GENERAL_URL = "http://www.dns-shop.ru";
    private static int WAITING_FOR_EXPAND = 7;
    private static int MAX_COUNT_EXPAND = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_ELEMENTS = -1;

    public void ReadSite(String FullAddress) throws InterruptedException {

        startTime = System.currentTimeMillis();
        addToResultString("Start parsing: " + new Date().toString(), addTo.LogFileAndConsole);

//        // Proxy setting.
//        ProxyServers newProxyServer = new ProxyServers();
//        String proxyAddress = null;
//        try {
//            proxyAddress = newProxyServer.getProxyServer();
//            System.out.println(proxyAddress);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

//        FirefoxProfile profile = new FirefoxProfile();
//        String ProxyServerIP = newProxyServer.getIPFromProxyAddress(proxyAddress); // "91.221.233.82";
//        int ProxyServerPort = newProxyServer.getPortFromProxyAddress(proxyAddress); //8080;

//        profile.setPreference("network.proxy.type", 1);
//        profile.setPreference("network.proxy.http", ProxyServerIP);
////        profile.setPreference("network.proxy.ftp",serverIP);
//        profile.setPreference("network.proxy.socks", ProxyServerIP);
//        profile.setPreference("network.proxy.ssl", ProxyServerIP);
//        profile.setPreference("network.proxy.http_port", ProxyServerPort);
////        profile.setPreference("network.proxy.ftp_port",port);
//        profile.setPreference("network.proxy.socks_port", ProxyServerPort);
//        profile.setPreference("network.proxy.ssl_port", ProxyServerPort);

//        WebDriver driver = new FirefoxDriver(profile);
//        ///

        WebDriver driver;

        FirefoxProfile profile = new FirefoxProfile();

        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/msword,application/csv,text/csv,image/png ,image/jpeg");
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        //profile.setPreference("browser.download.useDownloadDir",true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", false);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("browser.download.manager.showWhenStarting",false);
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
        profile.setPreference("pdfjs.disabled", true);

        try {
            addToResultString("Trying take new WebDriver Firefox", addTo.LogFileAndConsole);
            driver = new FirefoxDriver(profile);
        }catch (Exception e){
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

//        try {
//            addToResultString("Trying open empty page", addTo.Yes);
//            driver.get("");
//        }catch (Exception e){
//            e.printStackTrace();
//            addToResultString(e.toString(), addTo.Yes);
//            return;
//        }

//        driver.manage().window().maximize();

//        driver.navigate().to("https://2ip.ru/");

//        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        String cssSelector_ExpandPage_Block = "div.catalog-category-more, div[style$='block']";
        String cssSelector_ExpandPage_Button = "a[class='btn btn-default']";
        String cssSelector_Categories = "div.menu-item-category-wrapper a[href]";
        String cssSelector_GoodLink = "div.item-name a[href]";
        String cssSelector_GoodTitle = "h1[class='page-title price-item-title']";
        String cssSelector_GoodCode = "div.price-item-code span";
        String cssSelector_GoodPricePrevious = "div.previous-price s.prev-price-total";
        String cssSelector_GoodPrice = "meta[itemprop=price]";

        String cssSelector_GoodItems = "div.thumbnail";
        String cssSelector_GoodItem2 = "div.item-code";
        String cssSelector_GoodTitle2 = "div.item-name";
        String cssSelector_GoodPrice2 = "span[data-of='price-total'";

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        //listLinkPages.add("http://www.dns-shop.ru/catalog/17a892f816404e77/noutbuki/");
        //listPages.add("http://www.dns-shop.ru/catalog/31f05737df7e4e77/ssd-25-sata-nakopiteli/");

//        fillListPagesFromSite(driver, cssSelector_Categories, listLinkPages);
        listLinkPages = fillListPagesFromFile();

        for (int countSites = 0; countSites < listLinkPages.size(); countSites++) {

            // Open page for parsing Goods.
            try {
                addToResultString("Trying open page: " + listLinkPages.get(countSites), addTo.LogFileAndConsole);
                driver.navigate().to(listLinkPages.get(countSites));
//                driver.get(listLinkPages.get(countSites));
            }catch (Exception e){
                e.printStackTrace();
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                return;
            }

            // Expand and read all pages with Goods.
            //expandAndReadLinkGoods(driver, listLinkGoods, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodLink);

            // Expand and read data in current page.
//            addToResultString("Open page: " + listLinkPages.get(countSites), addTo.Yes);
            expandAndReadDescription(driver, listDataToBase, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodItems, cssSelector_GoodTitle2, cssSelector_GoodItem2, cssSelector_GoodPrice2);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("String(" + String.valueOf(listDataToBase.size()) + ") in base writing.", addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();

        }

//        addToResultString("Goods found: " + String.valueOf(listDataToBase.size()));
//        readGoodDescription(driver, listLinkGoods, cssSelector_GoodTitle, cssSelector_GoodCode, cssSelector_GoodPricePrevious, cssSelector_GoodPrice);

        // Close browser.
//        driver.close();
        driver.quit();

//        System.out.println(listPages.size());
        addToResultString("Finish parsing: " + new Date().toString(), addTo.LogFileAndConsole);

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
                        addToResultString("Timeout exception", addTo.LogFileAndConsole);
                        //addToResultString(te.getMessage());

//                        addToResultString("Count of item goods: " + Integer.toString(listItems.size()));

                        listItems = driver.findElements(By.cssSelector(cssSelector1));
                        addToResultString("Count of item goods: " + Integer.toString(listItems.size()), addTo.LogFileAndConsole);

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
            addToResultString("Error parsing site (timeout expand)", addTo.LogFileAndConsole);
            //addToResultString(te.getMessage());

        }
    }

    public void expandAndReadDescription(WebDriver driver,
                                         ArrayList<String[]> listLinkGoods2,
                                         String cssSelector_ExpandPage_Block,
                                         String cssSelector_ExpandPage_Button,
                                         String cssSelector_GoodItems,
                                         String cssSelector_GoodTitle2,
                                         String cssSelector_GoodItem2,
                                         String cssSelector_GoodPrice2) {

        List<WebElement> listItems;
        String goodTitle = "";
        String goodItem = "";
        String goodPrice = "";


        try {

            listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
            int countPages = 0;

            if (listItems.size() > 0) {

                WebElement but_NextPageBlock = driver.findElement(By.cssSelector(cssSelector_ExpandPage_Block));
                WebElement but_NextPageButton = but_NextPageBlock.findElement(By.cssSelector(cssSelector_ExpandPage_Button));

                while ((new WebDriverWait(driver, WAITING_FOR_EXPAND)).until(ExpectedConditions.not(
                        ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_ExpandPage_Block))))) {

                    but_NextPageButton.sendKeys(Keys.ESCAPE);
                    but_NextPageButton.click();

                    if (MAX_COUNT_EXPAND != -1 && countPages++ >= MAX_COUNT_EXPAND) break;
                }
            }

        } catch (Throwable te) {
//            addToResultString("Error parsing site (timeout expand)");
            addToResultString("All page is opened.", addTo.LogFileAndConsole);
            listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));

            int countIteration = 0;

            addToResultString("Reading data in page..", addTo.LogFileAndConsole);
            for (WebElement elementGood : listItems) {

                goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle2)).getText();
                goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem2)).getText();
                goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice2)).getText().replace(" ", "");
//                goodPrice = goodPrice.replace(" ", "");
                //addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice);
                String[] toList = {String.valueOf(countIteration), goodTitle, goodItem, "DNS_Samara", goodPrice};
                listLinkGoods2.add(toList);

                if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;

                countIteration++;

            }

            addToResultString("All item(" + countIteration + ") was reading", addTo.LogFileAndConsole);
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
            addToResultString("Error parsing site (fill category).", addTo.LogFileAndConsole);
            addToResultString(te.getMessage(), addTo.LogFileAndConsole);
        }
    }

    private ArrayList<String> fillListPagesFromFile() {

//        ArrayList<String> listLinkPages;
        ReadWriteFile mFileWithCategories = new ReadWriteFile("Categories_DNS.txt");

        String mFile = mFileWithCategories.readFile();
        return new ArrayList<String>(Arrays.asList(mFile.split("\n")));
    }

    private void readGoodDescription(WebDriver driver, ArrayList<String> listLinkGoods, String cssSelector_GoodTitle, String cssSelector_GoodCode, String cssSelector_GoodPricePrevious, String cssSelector_GoodPrice) {

        int countIteration = 0;
        for (String linkGood : listLinkGoods) {
            countIteration++;
            driver.navigate().to(linkGood);
            try {
                addToResultString("Goods title:" + driver.findElement(By.cssSelector(cssSelector_GoodTitle)).getText(), addTo.Console);
                addToResultString("Goods code :" + driver.findElement(By.cssSelector(cssSelector_GoodCode)).getText(), addTo.Console);
                addToResultString("Goods price:" + driver.findElement(By.cssSelector(cssSelector_GoodPrice)).getAttribute("content"), addTo.Console);
                List<WebElement> listGoodPricePreviuos = driver.findElements(By.cssSelector(cssSelector_GoodPricePrevious));
                if (listGoodPricePreviuos.size() > 0)
                    addToResultString("Goods previous price:" + listGoodPricePreviuos.get(0).getText(), addTo.Console);
            } catch (Throwable te) {
                addToResultString("Error parsing site (get description)", addTo.LogFileAndConsole);
                addToResultString(te.getMessage(), addTo.LogFileAndConsole);
            }

//            if (countIteration == 5) break;
        }

    }

    public String getResultOfParsing() {

        return this.resultOfPasring;
    }

    public void addToResultString(String addedString, addTo writeIntoLogFile) {
        //if (addedString.isEmpty()) return;
        String timeForResult = Long.toString((System.currentTimeMillis() - startTime) / 1000) + "." + Long.toString((System.currentTimeMillis() - startTime) % 1000);
        String stringToLog = timeForResult + " -> " + addedString + System.getProperty("line.separator");
        resultOfPasring = resultOfPasring.concat(stringToLog);

        if (writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.logFile) {
            ReadWriteFile mResultOfParsing = new ReadWriteFile(MainParsingPrices.fileResult2);
            mResultOfParsing.writeResultToFile(mResultOfParsing.getFullAddress(), stringToLog, true);
        }

        if (writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.Console)
            System.out.println(addedString);
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
                    + givenBy.toString() + "' within " + maxWaitTime + " seconds.", addTo.LogFileAndConsole);
            resultOfMetod = false;
        }

        return resultOfMetod;
    }

    private void writeDataIntoBase(ArrayList<String[]> listDataToBase) {

        ReadWriteBase writeDataToBase;
        Statement statement;

        addToResultString("Getting statement base start..", addTo.LogFileAndConsole);
        try {
            writeDataToBase = new ReadWriteBase();
            statement = writeDataToBase.getStatement();
            addToResultString("Getting statement base finish.", addTo.LogFileAndConsole);
        }catch (Exception e){
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        int countOfRecords = 0;
        int countOfUpdate = 0;

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            if (writeDataToBase.findData(statement, "SELECT item FROM goods WHERE goods.item LIKE '" + stringToBase[2] + "';")){
                addToResultString("Update record(" + countOfRecords + ") in base", addTo.Console);
                writeDataToBase.updateData(statement, stringToBase);
                countOfUpdate++;
            }
            else{
                addToResultString("Write new record(" + countOfRecords + ") in base", addTo.Console);
                writeDataToBase.writeData(statement, stringToBase);
            }

            //System.out.println(stringToBase[2]);

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }

        addToResultString("Added records:   " + (countOfRecords - countOfUpdate) + " in base.", addTo.Console);
        addToResultString("Updated records: " + (countOfUpdate) + " in base.", addTo.Console);

        addToResultString("Close base connections", addTo.LogFileAndConsole);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    enum addTo {
        logFile, Console, LogFileAndConsole
    }
}
