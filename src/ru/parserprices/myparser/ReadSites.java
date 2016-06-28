package ru.parserprices.myparser;


//import org.openqa.selenium.*;

import org.openqa.selenium.*;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.addToResultString;
import static ru.parserprices.myparser.MainParsingPrices.shopName;

public class ReadSites {

    private static String GENERAL_URL;
    private static int WAITING_FOR_EXPAND = 7;
    private static int MAX_COUNT_EXPAND = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_ELEMENTS = -1;
    private WebDriver driver;

    public void ReadSite(shopNames shopName) throws InterruptedException {

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

//        MainParsingPrices.cityShop = MainParsingPrices.cityShops.chapaevsk.name();

        startingWebDriver();
        setCoockie(shopName);

        // Set "GENERAL_URL".
        switch (shopName){
            case DNS:
                GENERAL_URL = "http://www.dns-shop.ru";
                readSiteDNS();
                break;
            case CITILINK:
                GENERAL_URL = "http://www.citilink.ru";
                readSiteCitilink();
                break;
            case DOMO:
                GENERAL_URL = "http://www.dns-shop.ru"; break;
            case CORPCENTRE:
                GENERAL_URL = "http://www.dns-shop.ru"; break;
        }

//        addToResultString("Goods found: " + String.valueOf(listDataToBase.size()));
//        readGoodDescription(driver, listLinkGoods, cssSelector_GoodTitle, cssSelector_GoodCode, cssSelector_GoodPricePrevious, cssSelector_GoodPrice);

        // Close browser.
        if (driver != null) driver.quit();

//        System.out.println(listPages.size());
        addToResultString("Finish parsing: ".concat(new Date().toString()), addTo.LogFileAndConsole);

    }

    // Disable warning in console.
    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
    }
    ///

    private void readSiteDNS(){

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
        String cssSelector_GoodPrice2 = "span[data-of='price-total']";
        String cssSelector_GoodLink2 = "div.item-name a[href]";

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver, cssSelector_Categories, listLinkPages);
//        listLinkPages = fillListPagesFromFile();
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        for (int countSites = 0; countSites < listLinkPages.size(); countSites++) {

            // Open page for parsing Goods.
            try {
                addToResultString("Trying open page: ".concat(listLinkPages.get(countSites)), addTo.LogFileAndConsole);
                if (driver == null) startingWebDriver();
                driver.navigate().to(listLinkPages.get(countSites));
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(listLinkPages.get(countSites)), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver.quit();} catch (Exception e1){/**/};
                return;
            }

            // Expand and read all pages with Goods.
            //expandAndReadLinkGoods(driver, listLinkGoods, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodLink);

            // Expand and read data in current page.
//            addToResultString("Open page: " + listLinkPages.get(countSites), addTo.Yes);
            expandAndReadDescription_DNS(driver, listDataToBase, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodItems, cssSelector_GoodTitle2, cssSelector_GoodItem2, cssSelector_GoodPrice2, cssSelector_GoodLink2);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
        }
    }

    private void readSiteCitilink(){

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        String cssSelector_Categories = "SPAN > a[class='subcategory-list-item__link']";
        String cssSelector_NextPage = "link[rel='next']";
        String cssSelector_GoodItems = "tbody[class='product_data__gtm-js']";
        String cssSelector_GoodItem = "link[rel='next']";
        String cssSelector_GoodTitle = "link[rel='next']";
        String cssSelector_GoodPrice = "link[rel='next']";
        String cssSelector_GoodLink = "link[rel='next']";

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver, cssSelector_Categories, listLinkPages);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        for (String linkPage : listLinkPages
                ) {
            // Open catalog page for parsing Goods.
            try {
                addToResultString("Trying open page: ".concat(linkPage), addTo.LogFileAndConsole);
                if (driver == null) startingWebDriver();
                driver.navigate().to(linkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(linkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver.quit();} catch (Exception e1){/**/};
                return;
            }

            // Flip pages and read data.
            flipAndReadDescription_CITILINK(driver, listDataToBase, cssSelector_GoodItems, cssSelector_GoodItem, cssSelector_GoodTitle, cssSelector_GoodPrice, cssSelector_GoodLink);

            WebElement nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
            while (nextPage != null) {
                String linkNextPage = nextPage.getAttribute("href");
                if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
            }

        }
    }

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

    private void expandAndReadDescription_DNS(WebDriver driver,
                                              ArrayList<String[]> listLinkGoods2,
                                              String cssSelector_ExpandPage_Block,
                                              String cssSelector_ExpandPage_Button,
                                              String cssSelector_GoodItems,
                                              String cssSelector_GoodTitle2,
                                              String cssSelector_GoodItem2,
                                              String cssSelector_GoodPrice2,
                                              String cssSelector_GoodLink2) {

        List<WebElement> listItems;
        String goodTitle = "";
        String goodItem = "";
        String goodPrice = "";
        String goodLink = "";

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

            addToResultString("All page is opened.", addTo.LogFileAndConsole);

            try {
                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                int countIteration = 0;
                addToResultString("Reading data in page..", addTo.LogFileAndConsole);

                for (WebElement elementGood : listItems) {
                    try {
                        goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle2)).getText();
                        goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem2)).getText();
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink2)).getText();
                        goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice2)).getText().replace(" ", "");

//                goodPrice = goodPrice.replace(" ", "");
                        //addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink};
                        listLinkGoods2.add(toList);

                        if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                        countIteration++;
                    } catch (Exception e) {
                        addToResultString("Element not found: ".concat(elementGood.getText()), addTo.LogFileAndConsole);
                    }
                }
                addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
            } catch (Exception e) {
                addToResultString("Error reading expand page.", addTo.LogFileAndConsole);
            }
        }
    }

    private void flipAndReadDescription_CITILINK(WebDriver driver,
                                                 ArrayList<String[]> listLinkGoods2,
                                                 String cssSelector_GoodItems,
                                                 String cssSelector_GoodItem,
                                                 String cssSelector_GoodTitle,
                                                 String cssSelector_GoodPrice,
                                                 String cssSelector_GoodLink) {

        List<WebElement> listItems;
        String goodItem = "";
        String goodTitle = "";
        String goodPrice = "";
        String goodLink = "";


        try {
            listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
            int countIteration = 0;
            addToResultString("Reading data in page[".concat(String.valueOf(countIteration)).concat("].."), addTo.LogFileAndConsole);

            for (WebElement elementGood : listItems) {
                try {
                    goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem)).getText();
                    goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle)).getText();
                    goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink)).getText();
                    goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice)).getText().replace(" ", "");

                    //                goodPrice = goodPrice.replace(" ", "");
                    //addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice);
                    String[] toList = {String.valueOf(countIteration),
                            goodTitle,
                            goodItem,
                            shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                            goodPrice,
                            new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                            goodLink};
                    listLinkGoods2.add(toList);

                    if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                    countIteration++;
                } catch (Exception e) {
                    addToResultString("Element not found: ".concat(elementGood.getText()), addTo.LogFileAndConsole);
                }
            }
            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading expand page.", addTo.LogFileAndConsole);
        }
    }

    // Read/write all categories.
    private void fillListPagesFromSite(WebDriver driver, String cssSelector_Categories, ArrayList<String> listPages) {

        if (shopName == shopNames.DNS) {
            driver = new HtmlUnitDriver();
        }
        driver.navigate().to(GENERAL_URL);

        try {

            List<WebElement> listItems = driver.findElements(By.cssSelector(cssSelector_Categories));

            for (WebElement hrefElement : listItems
                    ) {
                String linkCategory = hrefElement.getAttribute("href");
                listPages.add(linkCategory);
//                System.out.println(linkCategory);
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

//        int countIteration = 0;
        for (String linkGood : listLinkGoods) {
//            countIteration++;
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

    // Write data into base.
    private void writeDataIntoBase(ArrayList<String[]> listDataToBase) {

        ReadWriteBase writeDataToBase;
        Statement statement;

        addToResultString("Getting statement base start..", addTo.Console);
        try {
            writeDataToBase = new ReadWriteBase();
            statement = writeDataToBase.getStatement();
            addToResultString("Getting statement base finish.", addTo.Console);
        } catch (Exception e) {
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        int countOfRecords = 0;
        int countOfUpdate = 0;

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            java.sql.Date dateOfPriceToQuery;
            try {
                dateOfPriceToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateOfPriceToQuery = new java.sql.Date(System.currentTimeMillis());
            }

            String query_recordExist = "SELECT item FROM goods WHERE goods.item LIKE '" + stringToBase[2] + "' AND goods.shop LIKE '" + stringToBase[3] + "' LIMIT 5;";

            String query_needUpdate = "SELECT item FROM goods WHERE goods.item LIKE '" + stringToBase[2] + "' AND goods.shop LIKE '" + stringToBase[3] +
                    "' AND goods.price NOT LIKE '" + stringToBase[4] + "' LIMIT 5;";

            String query_updateRecord = "UPDATE goods SET price = '".concat(writeDataToBase.clearLetters(stringToBase[4])) + "', dateofprice = '" + dateOfPriceToQuery +
                    "' WHERE item LIKE '" + stringToBase[2] + "' AND shop LIKE '" + stringToBase[3] + "' LIMIT 5;";

            String query_writeNewRecord = "INSERT INTO Frontime.goods (good, item, shop, price, dateofprice, link)" +
                " VALUES ('" + writeDataToBase.clearLetters(stringToBase[1]) + "', '" + stringToBase[2] + "', '" + stringToBase[3] + "', '" + Integer.parseInt(stringToBase[4]) + "', '" + dateOfPriceToQuery + "', '" + stringToBase[6] + "');";

            if (writeDataToBase.dataExist(statement, query_recordExist)) {
                if (writeDataToBase.dataExist(statement, query_needUpdate)) {
                    addToResultString("Update record(" + countOfRecords + ") in base", addTo.Console);
//                writeDataToBase.updateData(statement, stringToBase);
                    writeDataToBase.writeData(statement, query_updateRecord);
                    countOfUpdate++;
                }else {
                    addToResultString("Not need update(" + countOfRecords + ") record", addTo.Console);
                }
            } else {
                addToResultString("Write new record(" + countOfRecords + ") in base", addTo.Console);
                writeDataToBase.writeData(statement, query_writeNewRecord);
            }

            //System.out.println(stringToBase[2]);

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }

        addToResultString("Added records:   " + (countOfRecords - countOfUpdate) + " in base.", addTo.LogFileAndConsole);
        addToResultString("Updated records: " + (countOfUpdate) + " in base.", addTo.LogFileAndConsole);

        addToResultString("Close base connections", addTo.Console);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    // Start new WebDriver.
    private void startingWebDriver() {

        FirefoxProfile profile = new FirefoxProfile();

        try {
            switch (shopName){
                case DNS:

                    addToResultString("Trying start new WebDriver(Firefox)", addTo.LogFileAndConsole);

                    profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
                    profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword,application/csv,text/csv,image/png ,image/jpeg");
                    profile.setPreference("browser.download.manager.showWhenStarting", false);
                    profile.setPreference("browser.download.manager.focusWhenStarting", false);
                    //profile.setPreference("browser.download.useDownloadDir",true);
                    profile.setPreference("browser.helperApps.alwaysAsk.force", false);
                    profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
                    profile.setPreference("browser.download.manager.closeWhenDone", false);
                    profile.setPreference("browser.download.manager.showAlertOnComplete", false);
                    profile.setPreference("browser.download.manager.useWindow", false);
                    profile.setPreference("browser.download.manager.showWhenStarting", false);
                    profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
                    profile.setPreference("pdfjs.disabled", true);

                    driver = new FirefoxDriver(profile);
                    break;

                case CITILINK:
                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver = new HtmlUnitDriver();
                    break;

                default: break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }

    // Set coockie.
    private void setCoockie(shopNames shopName){

        switch (shopName){
            case DNS:

                String cookieCityPath;
                String cookieCityGuid1C;
                driver.get(GENERAL_URL);
                driver.manage().deleteCookieNamed("city_path");
                driver.manage().deleteCookieNamed("city_guid_1c");

                if (MainParsingPrices.shopCity == shopCities.samara){
                    cookieCityPath = "samara";
                    cookieCityGuid1C = "55506b53-0565-11df-9cf0-00151716f9f5";
                }else if (MainParsingPrices.shopCity == shopCities.novokuybishevsk){
                    cookieCityPath = "novokuybishevsk";
                    cookieCityGuid1C = "5acb57ac-40a6-11e1-8064-001517c526f0";
                }else if (MainParsingPrices.shopCity == shopCities.chapaevsk){
                    cookieCityPath = "chapaevsk";
                    cookieCityGuid1C = "eaa9918b-bc8d-11e4-bd90-00155d03361b";
                }else {
                    cookieCityPath = "samara";
                    cookieCityGuid1C = "55506b53-0565-11df-9cf0-00151716f9f5";
                }

                driver.manage().addCookie(new Cookie("city_path", cookieCityPath));
                driver.manage().addCookie(new Cookie("city_guid_1c", cookieCityGuid1C));

                break;

            case CITILINK:

                break;
            case DOMO:

                break;
            case CORPCENTRE:

                break;
            default:
                break;
        }
    }

}
