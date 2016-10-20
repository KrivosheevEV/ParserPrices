package ru.parserprices.myparser;


//import org.openqa.selenium.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.*;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.security.Key;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.addToResultString;
import static ru.parserprices.myparser.MainParsingPrices.shopName;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

public class ReadSites {

    private static String GENERAL_URL;
    private static int WAITING_FOR_EXPAND = 7;
    private static int MAX_COUNT_EXPAND = -1;    // -1 == never
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_ELEMENTS = -1;
    private HtmlUnitDriver driver_noGUI;
    private WebDriver driver_GUI;

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

        setGeneralUrl(shopName);
        startingWebDriver();
        setCookie(shopName);

        // Start reading.
        switch (shopName){
            case DNS:
                readSiteDNS();
                break;
            case CITILINK:
                readSiteCitilink();
                break;
            case DOMO:
                readSiteDomo();
                break;
            case CORPCENTRE:
                readSiteCorpCentre();
                break;
            case FENIXCOMP:
                readSiteFenixComp();
                break;
        }

//        addToResultString("Goods found: " + String.valueOf(listDataToBase.size()));
//        readGoodDescription(driver, listLinkGoods, cssSelector_GoodTitle, cssSelector_GoodCode, cssSelector_GoodPricePrevious, cssSelector_GoodPrice);

        // Close browser.
        if (driver_GUI != null) driver_GUI.quit();
        if (driver_noGUI != null) driver_noGUI.quit();

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
//        String cssSelector_Categories = "div.menu-item-category-wrapper a[href]";
        String cssSelector_CategoriesLink = "div[class='item-wrap'] a[href]";
        String cssSelector_GoodLink = "div.item-name a[href]";
        String cssSelector_GoodTitle = "h1[class='page-title price-item-title']";
        String cssSelector_GoodCode = "div.price-item-code span";
        String cssSelector_GoodPricePrevious = "div.previous-price s.prev-price-total";
        String cssSelector_GoodPrice = "meta[itemprop=price]";

        String cssSelector_GoodCategory = "div.page-products h1";
        String cssSelector_GoodItems = "div.thumbnail";
        String cssSelector_GoodItem2 = "div.item-code";
        String cssSelector_GoodTitle2 = "div.item-name";
        String cssSelector_GoodPrice2 = "span[data-of='price-total']";
        String cssSelector_GoodLink2 = "div.item-name a[href]";

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver_GUI, driver_noGUI, cssSelector_CategoriesLink, listLinkPages);
//        listLinkPages = fillListPagesFromFile();
        if (listLinkPages.size() == 0) addToResultString("Category links not found!", addTo.LogFileAndConsole);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        int countOfCategories = 0;
        String listLinkPagesSize = String.valueOf(listLinkPages.size());

        for (String listLinkPage : listLinkPages) {

            // Open page for parsing Goods.
            try {
                addToResultString("Trying open page[".concat(String.valueOf(++countOfCategories)).concat("/").concat(listLinkPagesSize).concat("]: ").concat(listLinkPage), addTo.LogFileAndConsole);
                if (driver_GUI == null) startingWebDriver();
                driver_GUI.navigate().to(listLinkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(listLinkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {
                    driver_GUI.quit();
                } catch (Exception e1) {/**/}
                ;
                return;
            }

            // Expand and read all pages with Goods.
            //expandAndReadLinkGoods(driver, listLinkGoods, cssSelector_ExpandPage_Block, cssSelector_ExpandPage_Button, cssSelector_GoodLink);

            // Expand and read data in current page.
//            addToResultString("Open page: " + listLinkPages.get(countSites), addTo.Yes);
            expandAndReadDescription_DNS(driver_GUI,
                    listDataToBase,
                    cssSelector_ExpandPage_Block,
                    cssSelector_ExpandPage_Button,
                    cssSelector_GoodCategory,
                    cssSelector_GoodItems,
                    cssSelector_GoodTitle2,
                    cssSelector_GoodItem2,
                    cssSelector_GoodPrice2,
                    cssSelector_GoodLink2);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
        }
    }

    private void readSiteCitilink(){

        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        String cssSelector_CategoriesLink = "SPAN > a[class='subcategory-list-item__link']";
        String cssSelector_NextPage = "link[rel='next']";
        String cssSelector_GoodCategory = "div.cleared h1";
        String cssSelector_GoodItems = "tbody[class='product_data__gtm-js']";
        String cssSelector_GoodLink = "span[class*='h3'] > a[class='link_gtm-js']";

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver_GUI, driver_noGUI, cssSelector_CategoriesLink, listLinkPages);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        int countOfCategories = 0;
        String listLinkPagesSize = String.valueOf(listLinkPages.size());

        for (String linkPage : listLinkPages
                ) {
            // Open catalog page for parsing Goods.
            try {
                addToResultString("Trying open page[".concat(String.valueOf(++countOfCategories)).concat("/").concat(listLinkPagesSize).concat("]: ").concat(linkPage), addTo.LogFileAndConsole);
                if (driver_noGUI == null) startingWebDriver();
                driver_noGUI.navigate().to(linkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(linkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver_noGUI.quit();} catch (Exception e1){/**/};
                return;
            }

            flipAndReadDescription_CITILINK(driver_noGUI,
                    listDataToBase,
                    cssSelector_NextPage,
                    cssSelector_GoodCategory,
                    cssSelector_GoodItems,
                    cssSelector_GoodLink);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
        }
    }

    private void readSiteDomo(){

        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        String cssSelector_CategoriesLink = "li:not(.mainl) > a";
        String cssSelector_NextPage = "a.right_arrow";
        String cssSelector_NextPageDivider = "div.paging_controls > div.divider";
        String cssSelector_GoodItems = "div.product";

        String cssSelector_GoodCategory = "div.widget_title_container.categorySpot span";
        String cssSelector_GoodTitle = "div.product_name > a";
        String cssSelector_GoodPrice = "span.price";
        String cssSelector_GoodLink = "div.product_name > a[href]";

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver_GUI, driver_noGUI, cssSelector_CategoriesLink, listLinkPages);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        int countOfCategories = 0;
        String listLinkPagesSize = String.valueOf(listLinkPages.size());

        for (String linkPage : listLinkPages
                ) {
            // Open catalog page for parsing Goods.
            try {
                addToResultString("Trying open page[".concat(String.valueOf(++countOfCategories)).concat("/").concat(listLinkPagesSize).concat("]: ").concat(linkPage), addTo.LogFileAndConsole);
                if (driver_noGUI == null) startingWebDriver();
                driver_noGUI.navigate().to(linkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(linkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver_noGUI.quit();} catch (Exception e1){/**/};
                return;
            }

            flipAndReadDescription_DOMO(driver_noGUI,
                    listDataToBase,
                    cssSelector_NextPage,
                    cssSelector_NextPageDivider,
                    cssSelector_GoodCategory,
                    cssSelector_GoodItems,
                    cssSelector_GoodTitle,
                    cssSelector_GoodPrice,
                    cssSelector_GoodLink);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
        }
    }

    private void readSiteCorpCentre(){

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        String cssSelector_CategoriesLink = "ul.catalog div.column li > a";
        String cssSelector_NextPage = "a.next";
        String cssSelector_NavPanel = "div.catalog-pagenav.clearfix";

        String cssSelector_GoodCategory = "div.title h1";
        String cssSelector_GoodItems = "div.catalog-item";
        String cssSelector_GoodItem = "div.name > a";
        String cssSelector_GoodTitle = "div.name > a";
        String cssSelector_GoodPrice = "div.price div > strong > span";
        String cssSelector_GoodLink = "div.name > a[href]";

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver_GUI, driver_noGUI, cssSelector_CategoriesLink, listLinkPages);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        int countOfCategories = 0;
        String listLinkPagesSize = String.valueOf(listLinkPages.size());

        for (String linkPage : listLinkPages
                ) {
            // Open catalog page for parsing Goods.
            try {
                addToResultString("Trying open page[".concat(String.valueOf(++countOfCategories)).concat("/").concat(listLinkPagesSize).concat("]: ").concat(linkPage), addTo.LogFileAndConsole);
                if (driver_noGUI == null) startingWebDriver();
                driver_noGUI.navigate().to(linkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(linkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver_noGUI.quit();} catch (Exception e1){/**/};
                return;
            }

            flipAndReadDescription_CORPCENTRE(driver_noGUI,
                    listDataToBase,
                    cssSelector_NavPanel,
                    cssSelector_NextPage,
                    cssSelector_GoodItems,
                    cssSelector_GoodItem,
                    cssSelector_GoodCategory,
                    cssSelector_GoodTitle,
                    cssSelector_GoodPrice,
                    cssSelector_GoodLink);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
        }
    }

    private void readSiteFenixComp(){

        ArrayList<String> listLinkGoods = new ArrayList<String>();
        ArrayList<String[]> listDataToBase = new ArrayList<String[]>();
        ArrayList<String> listLinkPages = new ArrayList<String>();

        String cssSelector_CategoriesLink = "li > :not(span) a";
        String cssSelector_ReadPage = "td.pages_nums";
        String cssSelector_HaveNextPage = "td.pages_nums > a";

        String cssSelector_GoodItems = "table.catalogListItem";
        String cssSelector_GoodItem = "td.cliArt";
        String cssSelector_GoodCategory = "div.bmItem div.borderBox h1";
        String cssSelector_GoodTitle = "td.cliName > a";
        String cssSelector_GoodPrice = "span.priceNum";
        String cssSelector_GoodLink = "td.cliName > a[href]";

        addToResultString("Start getting category links", addTo.LogFileAndConsole);
        fillListPagesFromSite(driver_GUI, driver_noGUI, cssSelector_CategoriesLink, listLinkPages);
        addToResultString("Finish getting category links", addTo.LogFileAndConsole);

        int countOfCategories = 0;
        String listLinkPagesSize = String.valueOf(listLinkPages.size());

        for (String linkPage : listLinkPages
                ) {
            // Open catalog page for parsing Goods.
            try {
                addToResultString("Trying open page[".concat(String.valueOf(++countOfCategories)).concat("/").concat(listLinkPagesSize).concat("]: ").concat(linkPage), addTo.LogFileAndConsole);
                if (driver_noGUI == null) startingWebDriver();
                driver_noGUI.navigate().to(linkPage);
            } catch (Exception e) {
//                e.printStackTrace();
                addToResultString("Can't open new page: ".concat(linkPage), addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
                try {driver_noGUI.quit();} catch (Exception e1){/**/};
                return;
            }

            flipAndReadDescription_FENIXCOMP(driver_noGUI,
                    listDataToBase,
                    cssSelector_ReadPage,
                    cssSelector_HaveNextPage,
                    cssSelector_GoodItems,
                    cssSelector_GoodItem,
                    cssSelector_GoodCategory,
                    cssSelector_GoodTitle,
                    cssSelector_GoodPrice,
                    cssSelector_GoodLink);

            addToResultString("Writing data in base..", addTo.LogFileAndConsole);
            writeDataIntoBase(listDataToBase);
            addToResultString("Records(".concat(String.valueOf(listDataToBase.size())).concat(") added/updated in base."), addTo.LogFileAndConsole);

            listDataToBase = new ArrayList<String[]>();
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
                                              String cssSelector_GoodCategory,
                                              String cssSelector_GoodItems,
                                              String cssSelector_GoodTitle2,
                                              String cssSelector_GoodItem2,
                                              String cssSelector_GoodPrice2,
                                              String cssSelector_GoodLink2) {

        List<WebElement> listItems;
        String goodCategory = "";
        String goodTitle = "";
        String goodItem = "";
        String goodPrice = "";
        String goodLink = "";

        try {

            listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
            int countPages = 0;

            if (listItems.size() > 0) {

//                driver.setJavascriptEnabled(true);

                WebElement but_NextPageBlock = driver.findElement(By.cssSelector(cssSelector_ExpandPage_Block));
                WebElement but_NextPageButton = but_NextPageBlock.findElement(By.cssSelector(cssSelector_ExpandPage_Button));

//                driver.setJavascriptEnabled(false);

                while ((new WebDriverWait(driver, WAITING_FOR_EXPAND)).until(ExpectedConditions.not(
                        ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_ExpandPage_Block))))) {


//                    driver.navigate().to(but_NextPageButton.getAttribute("href"));
                    but_NextPageButton.sendKeys(Keys.ESCAPE);
                    but_NextPageButton.click();

                    if (MAX_COUNT_EXPAND != -1 && countPages++ >= MAX_COUNT_EXPAND) break;
                }

                addToResultString("All page[".concat(String.valueOf(countPages)).concat("] is opened."), addTo.LogFileAndConsole);
            }

        } catch (Throwable te) {

//            addToResultString(te.getMessage(), addTo.LogFileAndConsole);
//
//        } finally {


            try {
                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                goodCategory = driver.findElements(By.cssSelector(cssSelector_GoodCategory)).get(0).getText();
                int countIteration = 0;
                addToResultString("Reading data in page..", addTo.LogFileAndConsole);

                for (WebElement elementGood : listItems) {
                    try {
                        goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle2)).getText();
                        goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem2)).getText();
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink2)).getAttribute("href");
                        goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice2)).getText().replace(" ", "");

//                goodPrice = goodPrice.replace(" ", "");
                        //addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink,
                                goodCategory};
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
                                                 String cssSelector_NextPage,
                                                 String cssSelector_GoodCategory,
                                                 String cssSelector_GoodItems,
                                                 String cssSelector_GoodLink) {

        List<WebElement> listItems;
        String params = "";
        String goodCategory = "";
        String goodItem = "";
        String goodTitle = "";
        String goodPrice = "";
        String goodLink = "";

        int countIteration = 0;
        Boolean readPage = true;

        try {
            int countOfPage = 1;

            while (readPage) {
                //addToResultString("Reading data in page[".concat(String.valueOf(countOfPage++).concat("]..")), addTo.LogFileAndConsole);

                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                goodCategory = driver.findElements(By.cssSelector(cssSelector_GoodCategory)).get(0).getText();

                for (WebElement elementGood : listItems) {
                    try {
                        params = elementGood.getAttribute("data-params");
                        goodItem = new String(params.substring(params.indexOf("id") + 5, params.indexOf("\"", params.indexOf("id") + 5)));
                        goodTitle = new String(params.substring(params.indexOf("shortName") + 12, params.indexOf("\"", params.indexOf("shortName") + 12)));
                        goodPrice = new String(params.substring(params.indexOf("price") + 7, params.indexOf(",", params.indexOf("price") + 7)));
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink)).getAttribute("href");
                        params = null;
                        //                goodPrice = goodPrice.replace(" ", "");
                        //addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink,
                                goodCategory};
                        listLinkGoods2.add(toList);
                        //addToResultString("Size of array: ".concat(String.valueOf(listLinkGoods2.size()).concat(".")), addTo.LogFileAndConsole);
//                        addToResultString("Added string to array: ".concat(java.util.Arrays.toString(toList)), addTo.LogFileAndConsole);

                        toList = null;  goodItem = null; goodTitle = null; goodPrice = null; goodLink = null;

                        if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                        countIteration++;
                    } catch (Exception e) {
                        addToResultString("Element not found: ".concat(elementGood.getText()), addTo.logFile);
                    }
                }

                listItems.clear();

                try {
                    WebElement nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                    String linkNextPage = nextPage.getAttribute("href");
                    if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
//                    nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                }catch (Exception e){
                    readPage = false;
                }


            }

            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Reading (".concat(Integer.toString(countIteration)).concat(") elements."), addTo.LogFileAndConsole);
//            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }

    private void flipAndReadDescription_DOMO(WebDriver driver,
                                                 ArrayList<String[]> listLinkGoods2,
                                                 String cssSelector_NextPage,
                                                 String cssSelector_NextPageDivider,
                                             String cssSelector_GoodCategory,
                                                 String cssSelector_GoodItems,
                                                 String cssSelector_GoodTitle,
                                                 String cssSelector_GoodPrice,
                                                 String cssSelector_GoodLink) {

        List<WebElement> listItems;
        String params = "";
        String goodItem = "0";
        String goodCategory = "";
        String goodTitle = "";
        String goodPrice = "";
        String goodLink = "";

        try {
            int countOfPage = 1;
            int countIteration = 0;
            boolean readPage = true;
            boolean haveNextPage = false;
            int countOfDivider = driver.findElements(By.cssSelector(cssSelector_NextPageDivider)).size();
            if (countOfDivider > 2) haveNextPage = true;

            while (readPage) {
                //addToResultString("Reading data in page[".concat(String.valueOf(countOfPage++).concat("]..")), addTo.LogFileAndConsole);

                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                goodCategory = driver.findElements(By.cssSelector(cssSelector_GoodCategory)).get(0).getText();

                for (WebElement elementGood : listItems) {
                    try {
                        goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle)).getText();
                        goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice)).getText();
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink)).getAttribute("href");
                        goodItem = new String(goodLink.substring(goodLink.lastIndexOf("-") + 1));
                        goodPrice = new String(goodPrice.replace(" руб.", "").replace(" ", ""));
                        //
//                        addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice, addTo.LogFileAndConsole);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink,
                                goodCategory};
                        listLinkGoods2.add(toList);
                        //addToResultString("Size of array: ".concat(String.valueOf(listLinkGoods2.size()).concat(".")), addTo.LogFileAndConsole);
//                        addToResultString("Added string to array: ".concat(java.util.Arrays.toString(toList)), addTo.LogFileAndConsole);

                        toList = null;  goodItem = null; goodTitle = null; goodPrice = null; goodLink = null;

                        if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                        countIteration++;
                    } catch (Exception e) {
                        addToResultString("Element not found: ".concat(elementGood.getText()), addTo.logFile);
                    }
                }

                listItems.clear();
                listItems = null;

                if (haveNextPage){
                    WebElement nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                    String linkNextPage = nextPage.getAttribute("href");
                    if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
                    countOfDivider = driver.findElements(By.cssSelector(cssSelector_NextPageDivider)).size();
                    if (countOfDivider < 4) haveNextPage = false;
                }else readPage = false;
            }

            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }

    private void flipAndReadDescription_CORPCENTRE(WebDriver driver,
                                                   ArrayList<String[]> listLinkGoods2,
                                                   String cssSelector_NavPanel,
                                                   String cssSelector_NextPage,
                                                   String cssSelector_GoodItems,
                                                   String cssSelector_GoodItem,
                                                   String cssSelector_GoodCategory,
                                                   String cssSelector_GoodTitle,
                                                   String cssSelector_GoodPrice,
                                                   String cssSelector_GoodLink) {

        List<WebElement> listItems;
        String params = "";
        String goodItem = "0";
        String goodCategory = "";
        String goodTitle = "";
        String goodPrice = "";
        String goodLink = "";
        int countIteration = 0;

        try {
            int countOfPage = 1;
            boolean readPage = true;
            boolean haveNextPage;

            //int countOfDivider = driver.findElements(By.cssSelector(cssSelector_NextPageDivider)).size();
            // if (countOfDivider > 2) haveNextPage = true;


            while (readPage) {
                //addToResultString("Reading data in page[".concat(String.valueOf(countOfPage++).concat("]..")), addTo.LogFileAndConsole);

                haveNextPage = driver.findElements(By.cssSelector(cssSelector_NavPanel)).size() != 0;

                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                goodCategory = driver.findElements(By.cssSelector(cssSelector_GoodCategory)).get(0).getText();

                for (WebElement elementGood : listItems) {
                    try {
                        countIteration++;
                        goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem)).getAttribute("onclick");
                        goodItem = new String (goodItem.substring(goodItem.indexOf("'") + 1, goodItem.indexOf(",") - 1));
                        goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle)).getText();
                        goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice)).getText();
                        goodPrice = new String(goodPrice.replace(" ", ""));
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink)).getAttribute("href");
                        //
//                        addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice, addTo.LogFileAndConsole);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink,
                                goodCategory};
                        listLinkGoods2.add(toList);
                        //addToResultString("Size of array: ".concat(String.valueOf(listLinkGoods2.size()).concat(".")), addTo.LogFileAndConsole);
//                        addToResultString("Added string to array: ".concat(java.util.Arrays.toString(toList)), addTo.LogFileAndConsole);

                        toList = null;  goodItem = null; goodTitle = null; goodPrice = null; goodLink = null;

                        if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                    } catch (Exception e) {
                        addToResultString("Element not found: ".concat(elementGood.getText()), addTo.logFile);
                    }
                }

                listItems.clear();
                listItems = null;

                if (haveNextPage){
                    WebElement nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                    String linkNextPage = nextPage.getAttribute("href");
                    if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
                    //countOfDivider = driver.findElements(By.cssSelector(cssSelector_NextPageDivider)).size();
                    //if (countOfDivider < 4) haveNextPage = false;
                }else readPage = false;
            }

            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Was read (".concat(Integer.toString(countIteration)).concat(") items."), addTo.LogFileAndConsole);
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }


    private void flipAndReadDescription_FENIXCOMP(WebDriver driver,
                                                   ArrayList<String[]> listLinkGoods2,
                                                   String cssSelector_ReadPage,
                                                   String cssSelector_HaveNextPage,
                                                   String cssSelector_GoodItems,
                                                   String cssSelector_GoodItem,
                                                   String cssSelector_GoodCategory,
                                                   String cssSelector_GoodTitle,
                                                   String cssSelector_GoodPrice,
                                                   String cssSelector_GoodLink) {

        List<WebElement> listItems;
        String params = "";
        String goodItem = "0";
        String goodCategory = "";
        String goodTitle = "";
        String goodPrice = "";
        String goodLink = "";
        int countIteration = 0;

        try {
            int countOfPage = 0;
            boolean readPage;
            boolean haveNextPage;

            readPage = driver.findElements(By.cssSelector(cssSelector_ReadPage)).size() >= 2;

            while (readPage) {
                //addToResultString("Reading data in page[".concat(String.valueOf(countOfPage++).concat("]..")), addTo.LogFileAndConsole);

                int countOfPages = (driver.findElements(By.cssSelector(cssSelector_HaveNextPage)).size() / 2) + 1;
                countOfPage++;
                haveNextPage = countOfPage < countOfPages;

                listItems = driver.findElements(By.cssSelector(cssSelector_GoodItems));
                goodCategory = driver.findElements(By.cssSelector(cssSelector_GoodCategory)).get(0).getText();

                for (WebElement elementGood : listItems) {
                    try {
                        countIteration++;
                        goodItem = elementGood.findElement(By.cssSelector(cssSelector_GoodItem)).getText();
                        goodItem = new String(goodItem.replace("Артикул: ", ""));
                        goodTitle = elementGood.findElement(By.cssSelector(cssSelector_GoodTitle)).getText();
                        goodPrice = elementGood.findElement(By.cssSelector(cssSelector_GoodPrice)).getText();
                        goodPrice = new String(goodPrice.replace(" ", ""));
                        goodPrice = new String(goodPrice.replace(".", ""));
                        goodPrice = new String(goodPrice.replace("-", ""));
                        goodLink = elementGood.findElement(By.cssSelector(cssSelector_GoodLink)).getAttribute("href");
                        //
//                        addToResultString("Good: " + goodTitle + ", Item: " + goodItem + ", Price: " + goodPrice, addTo.LogFileAndConsole);
                        String[] toList = {String.valueOf(countIteration),
                                goodTitle,
                                goodItem,
                                shopName.name().concat(MainParsingPrices.shopCityCode.name()),
                                goodPrice,
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                                goodLink,
                                goodCategory};
                        listLinkGoods2.add(toList);
                        //addToResultString("Size of array: ".concat(String.valueOf(listLinkGoods2.size()).concat(".")), addTo.LogFileAndConsole);
//                        addToResultString("Added string to array: ".concat(java.util.Arrays.toString(toList)), addTo.LogFileAndConsole);

                        toList = null;  goodItem = null; goodTitle = null; goodPrice = null; goodLink = null;

                        if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                    } catch (Exception e) {
                        addToResultString("Element not found: ".concat(elementGood.getText()), addTo.logFile);
                    }
                }

                listItems.clear();
                listItems = null;

                if (haveNextPage){
                    String linkNextPage = driver.getCurrentUrl().concat("&scroll_page=").concat(String.valueOf(countOfPage + 1));
                    if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
                }else readPage = false;
            }

            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Was read (".concat(Integer.toString(countIteration)).concat(") items."), addTo.LogFileAndConsole);
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }


    // Read/write all categories.
    private void fillListPagesFromSite(WebDriver driverGUI, HtmlUnitDriver drivernoGUI, String cssSelector_CategoriesLink, ArrayList<String> listPages) {

        if (shopName == shopNames.DNS) {
            drivernoGUI = new HtmlUnitDriver();

            //driver.setJavascriptEnabled(true);
        }
        setCookie(shopName);

        if (shopName == shopNames.DOMO || shopName == shopNames.CORPCENTRE){
            drivernoGUI.navigate().to(GENERAL_URL + "/catalog");
        }else drivernoGUI.navigate().to(GENERAL_URL);

//        driver2.get("http://www.ya.ru");

        drivernoGUI.manage()
                .timeouts()
                .implicitlyWait(10, TimeUnit.SECONDS);

        try {

            drivernoGUI.setJavascriptEnabled(true);
            List<WebElement> listItems = drivernoGUI.findElements(By.cssSelector(cssSelector_CategoriesLink));
            drivernoGUI.setJavascriptEnabled(false);

            for (WebElement hrefElement : listItems)
            {
                String linkCategory = hrefElement.getAttribute("href");
                listPages.add(linkCategory);
//                System.out.println(linkCategory);
            }

        } catch (Throwable te) {
            addToResultString("Error parsing site (fill category).", addTo.LogFileAndConsole);
            addToResultString(te.getMessage(), addTo.LogFileAndConsole);
        }

        addToResultString("Reading ".concat(Integer.toString(listPages.size()).concat(" category links")), addTo.LogFileAndConsole);
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

        if (listDataToBase == null || listDataToBase.isEmpty()) {
            addToResultString("Not have data to write into base.", addTo.LogFileAndConsole);
            return;
        }

        ReadWriteBase writeDataToBase;
        Statement statement;

        addToResultString("Getting statement base start..", addTo.LogFileAndConsole);
        try {
            writeDataToBase = new ReadWriteBase();
            statement = writeDataToBase.getStatement();
            addToResultString("Getting statement base finish.", addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        int countOfRecords = 0;
        int countOfUpdate = 0;
        int countOfNewRecords = 0;
        int MAX_RECORDS_FOR_INSERT = 50;

        // vvv

        java.sql.Date dateOfPriceToQuery;

        String query_writeNewRecords_prefix = "INSERT INTO general.".concat(shopName.name()).concat(" (good, item, shop, price, dateofprice, link, category)").concat(" VALUES ");
        String query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE good=VALUES(good), price=VALUES(price), dateofprice=VALUES(dateofprice), link=VALUES(link), category=VALUES(category);";
        String query_writeNewRecords = query_writeNewRecords_prefix;

        addToResultString("Start record into base.", addTo.LogFileAndConsole);

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            try {
                dateOfPriceToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateOfPriceToQuery = new java.sql.Date(System.currentTimeMillis());
            }

            query_writeNewRecords = query_writeNewRecords.concat(" ('" + writeDataToBase.clearLetters(stringToBase[1]) + "', '" +
                                stringToBase[2] + "', '" +
                                stringToBase[3] + "', '" +
                                stringToBase[4] + "', '" +
                                dateOfPriceToQuery + "', '" +
                                stringToBase[6] + "', '" +
                                stringToBase[7] + "') ");

            if (countOfRecords % MAX_RECORDS_FOR_INSERT == 0){
                query_writeNewRecords = query_writeNewRecords.concat(query_writeNewRecords_suffix);
                if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords)) countOfNewRecords++;
                query_writeNewRecords = query_writeNewRecords_prefix;
            }else if (countOfRecords != listDataToBase.size()) query_writeNewRecords = query_writeNewRecords.concat(",");

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }

        if (countOfRecords % MAX_RECORDS_FOR_INSERT != 0){
            query_writeNewRecords = query_writeNewRecords.concat(query_writeNewRecords_suffix);
            if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords)) countOfNewRecords = (countOfNewRecords*MAX_RECORDS_FOR_INSERT) + (++countOfRecords - (countOfNewRecords*MAX_RECORDS_FOR_INSERT));
        }

        addToResultString("Finish record into base.", addTo.LogFileAndConsole);


        /*for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            java.sql.Date dateOfPriceToQuery;
            try {
                dateOfPriceToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateOfPriceToQuery = new java.sql.Date(System.currentTimeMillis());
            }

//            String query_recordExist = "SELECT item FROM goods t WHERE t.item LIKE '" + stringToBase[2] + "' AND t.shop LIKE '" + stringToBase[3] + "' LIMIT 5;";
            String query_recordExist = "SELECT item FROM ".concat(shopName.name()).concat(" t WHERE t.item LIKE '").concat(stringToBase[2]).concat("' AND t.shop LIKE '").concat(stringToBase[3]).concat("' LIMIT 5;");

            String query_needUpdate = "SELECT item FROM ".concat(shopName.name()).concat(" t WHERE t.item LIKE '").concat(stringToBase[]).concat("' AND t.shop LIKE '").concat(stringToBase[3]).concat("' AND t.price NOT LIKE '").concat(stringToBase[4]).concat("' LIMIT 5;");
2
            String query_updateRecord = "UPDATE ".concat(shopName.name()).concat(" SET price = '").concat(writeDataToBase.clearLetters(stringToBase[4])).concat("', dateofprice = '").concat(String.valueOf(dateOfPriceToQuery)).concat("' WHERE item LIKE '").concat(stringToBase[2]).concat("' AND shop LIKE '").concat(stringToBase[3]).concat("' LIMIT 5;");

            String query_writeNewRecord = "INSERT INTO general.".concat(shopName.name()).concat(" (good, item, shop, price, dateofprice, link, category)") +
                    " VALUES ('" + writeDataToBase.clearLetters(stringToBase[1]) + "', '" +
                    stringToBase[2] + "', '" +
                    stringToBase[3] + "', '" +
                    stringToBase[4] + "', '" +
                    dateOfPriceToQuery + "', '" +
                    stringToBase[6] + "', '" +
                    stringToBase[7] + "');";

            if (writeDataToBase.dataExist(statement, query_recordExist)) {
                if (writeDataToBase.dataExist(statement, query_needUpdate) & writeDataToBase.writeDataSuccessfully(statement, query_updateRecord)) countOfUpdate++;
            } else if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecord))countOfNewRecords++;

            //System.out.println(stringToBase[2]);

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }*/

        addToResultString("Reading records: " + countOfRecords + " in base.", addTo.LogFileAndConsole);
        addToResultString("Added records:   " + countOfNewRecords + " in base.", addTo.LogFileAndConsole);
        addToResultString("Updated records: " + countOfUpdate + " in base.", addTo.LogFileAndConsole);

        addToResultString("Close base connections..", addTo.Console);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    private void setGeneralUrl(shopNames givenShopName){

        switch (givenShopName){
            case DNS:
                GENERAL_URL = "http://www.dns-shop.ru"; break;
            case CITILINK:
                GENERAL_URL = "http://www.citilink.ru"; break;
            case DOMO:
                GENERAL_URL = "http://www.domo.ru"; break;
            case CORPCENTRE:
                GENERAL_URL = "http://www.corpcentre.ru"; break;
            case FENIXCOMP:
                GENERAL_URL = "http://www.fenixcomp.ru"; break;
            case AVITO:
                GENERAL_URL = "http://www.avito.ru"; break;
            default:
                GENERAL_URL = "http://www.dns-shop.ru";
        }
    }

    // Start new WebDriver.
    private void startingWebDriver() {

        FirefoxProfile profile = new FirefoxProfile();
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

        try {
            switch (shopName){

                case DNS:

                    addToResultString("Trying start new WebDriver(Firefox)", addTo.LogFileAndConsole);
                    driver_GUI = new FirefoxDriver(profile);
                    break;

                case CITILINK:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver_noGUI = new HtmlUnitDriver();
                    break;

                case DOMO:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
                    driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    break;

                case FENIXCOMP:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
                    driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    break;

                case AVITO:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
                    driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    break;

                default:
                    driver_noGUI = new HtmlUnitDriver();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }

    // Set coockie.
    private void setCookie(shopNames shopName){

        switch (shopName){

            case DNS:

                String cookieCityPath;
                String cookieCityGuid1C;
                driver_GUI.get(GENERAL_URL);
                driver_GUI.manage().deleteCookieNamed("city_path");
                driver_GUI.manage().deleteCookieNamed("city_guid_1c");

                if (MainParsingPrices.shopCity == shopCities.aznakaevo && false){
                    cookieCityPath = "";
                    cookieCityGuid1C = "";
                }else if (MainParsingPrices.shopCity == shopCities.bugulma){
                    cookieCityPath = "bugulma";
                    cookieCityGuid1C = "da27fa97-5b7d-11e2-aee1-00155d030b1f";
                }else if (MainParsingPrices.shopCity == shopCities.buzuluk){
                    cookieCityPath = "buzuluk";
                    cookieCityGuid1C = "aaee8b82-4fcc-11e2-aee1-00155d030b1f";
                }else if (MainParsingPrices.shopCity == shopCities.volsk){
                    cookieCityPath = "volsk";
                    cookieCityGuid1C = "3cb9a040-ad7c-11e3-80bd-00155d031202";
                }else if (MainParsingPrices.shopCity == shopCities.dimitrovgrad){
                    cookieCityPath = "dimitrovgrad";
                    cookieCityGuid1C = "51643c3d-8e76-11e1-979d-001517c526f0";
                }else if (MainParsingPrices.shopCity == shopCities.zainsk){
                    cookieCityPath = "zainsk";
                    cookieCityGuid1C = "8f454fe2-b4b4-11e3-abb8-00155d031202";
                }else if (MainParsingPrices.shopCity == shopCities.leninogorsk){
                    cookieCityPath = "leninogorsk";
                    cookieCityGuid1C = "da27fa99-5b7d-11e2-aee1-00155d030b1f";
                }else if (MainParsingPrices.shopCity == shopCities.novokuybishevsk){
                    cookieCityPath = "novokuybishevsk";
                    cookieCityGuid1C = "5acb57ac-40a6-11e1-8064-001517c526f0";
                }else if (MainParsingPrices.shopCity == shopCities.samara){
                    cookieCityPath = "samara";
                    cookieCityGuid1C = "55506b53-0565-11df-9cf0-00151716f9f5";
                }else if (MainParsingPrices.shopCity == shopCities.syzran){
                    cookieCityPath = "syzran";
                    cookieCityGuid1C = "5acb57ad-40a6-11e1-8064-001517c526f0";
                }else if (MainParsingPrices.shopCity == shopCities.chapaevsk){
                    cookieCityPath = "chapaevsk";
                    cookieCityGuid1C = "eaa9918b-bc8d-11e4-bd90-00155d03361b";
                }else if (MainParsingPrices.shopCity == shopCities.chistopol){
                    cookieCityPath = "chistopol";
                    cookieCityGuid1C = "673efaf9-aca3-11e2-a322-00155d030b1f";
                }else {
                    cookieCityPath = "samara";
                    cookieCityGuid1C = "55506b53-0565-11df-9cf0-00151716f9f5";
                }

                driver_GUI.manage().addCookie(new Cookie("city_path", cookieCityPath));
                driver_GUI.manage().addCookie(new Cookie("city_guid_1c", cookieCityGuid1C));

                break;

            case CITILINK:

                String cookie_space;
                driver_noGUI.get(GENERAL_URL);
                driver_noGUI.manage().deleteCookieNamed("_space");

                if (MainParsingPrices.shopCity == shopCities.samara){
                    cookie_space = "smr_cl%3A";
                }else if (MainParsingPrices.shopCity == shopCities.bugulma){
                    cookie_space = "kzn_cl%3Akzmbugul";
                }else if (MainParsingPrices.shopCity == shopCities.buzuluk){
                    cookie_space = "smr_cl%3Asmmbuzul";
                }else if (MainParsingPrices.shopCity == shopCities.volsk){
                    cookie_space = "srt_cl%3Asrtvolsk";
                }else if (MainParsingPrices.shopCity == shopCities.dimitrovgrad){
                    cookie_space = "smr_cl%3Asmmdimit";
                }else if (MainParsingPrices.shopCity == shopCities.leninogorsk){
                    cookie_space = "kzn_cl%3Akzmleninog";
                }else if (MainParsingPrices.shopCity == shopCities.novokuybishevsk){
                    cookie_space = "smr_cl%3Asmmnovok";
                }else if (MainParsingPrices.shopCity == shopCities.chapaevsk){
                    cookie_space = "smr_cl%3Asmmchap";
                }else if (MainParsingPrices.shopCity == shopCities.chistopol){
                    cookie_space = "kzn_cl%3Akzmgchist";
                }else {
                    cookie_space = "smr_cl%3A";
                }

                driver_noGUI.manage().addCookie(new Cookie("_space", cookie_space));

                break;

            case DOMO:

                String cookie_CUSTOMER_ESITE;
                driver_noGUI.get(GENERAL_URL);
                driver_noGUI.manage().deleteCookieNamed("CUSTOMER_ESITE");

                if (MainParsingPrices.shopCity == shopCities.syzran){
                    cookie_CUSTOMER_ESITE = "syzran";
                }else if (MainParsingPrices.shopCity == shopCities.tolyatti){
                    cookie_CUSTOMER_ESITE = "tolyatti";
                }else if (MainParsingPrices.shopCity == shopCities.chapaevsk){
                    cookie_CUSTOMER_ESITE = "chapaevsk";
                }else {
                    cookie_CUSTOMER_ESITE = "syzran";
                }

                driver_noGUI.manage().addCookie(new Cookie("CUSTOMER_ESITE", cookie_CUSTOMER_ESITE));


                break;

            case CORPCENTRE:

                String cookie_store_city;
                driver_noGUI.get(GENERAL_URL);
                driver_noGUI.manage().deleteCookieNamed("store[city]");

                if (MainParsingPrices.shopCity == shopCities.samara){
                    cookie_store_city = "%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D0%B0";
                }else if (MainParsingPrices.shopCity == shopCities.tolyatti){
                    cookie_store_city = "%D0%A2%D0%BE%D0%BB%D1%8C%D1%8F%D1%82%D1%82%D0%B8";
                }else if (MainParsingPrices.shopCity == shopCities.novokuybishevsk){
                    cookie_store_city = "%D0%9D%D0%BE%D0%B2%D0%BE%D0%BA%D1%83%D0%B9%D0%B1%D1%8B%D1%88%D0%B5%D0%B2%D1%81%D0%BA";
                }else {
                    cookie_store_city = "%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D0%B0";
                }

                driver_noGUI.manage().addCookie(new Cookie("store[city]", cookie_store_city));

                break;

            case FENIXCOMP:

                String cookie_city;
                driver_noGUI.get(GENERAL_URL);
                driver_noGUI.manage().deleteCookieNamed("city");

                if (MainParsingPrices.shopCity == shopCities.samara){
                    cookie_city = "101";
                }else if (MainParsingPrices.shopCity == shopCities.chapaevsk){
                    cookie_city = "141";
                }else {
                    cookie_city = "101";
                }

                driver_noGUI.manage().addCookie(new Cookie("city", cookie_city));

                break;

            case AVITO:

                if (MainParsingPrices.shopCity == shopCities.nikolsk){
                    GENERAL_URL = "https://www.avito.ru/penzenskaya_oblast_nikolsk";
                }else {
                    GENERAL_URL = "https://www.avito.ru/penzenskaya_oblast_nikolsk";
                }

                break;

            default:
                break;
        }
    }

}
