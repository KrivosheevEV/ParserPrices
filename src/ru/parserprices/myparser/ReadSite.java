package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by KrivosheevEV on 04.10.2016.
 */
public class ReadSite {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_ELEMENTS = 2;

    public static class Read_Avito{

        public Read_Avito(String givenURL){

            startingWebDriver(givenURL);
//            setCookie(givenURL);

            if (driver.getCurrentUrl().equals("https://www.avito.ru/blocked")) {
                addToResultString(driver.getCurrentUrl(), addTo.LogFileAndConsole);
                return;
            }

            ArrayList<String> listPages = new ArrayList<String>();
            ArrayList<String[]> dataToBase = new ArrayList<String[]>();

            if (!PROP_SUBCATEGORIES1.toUpperCase().equals("NO")){
                String[] listSubcategories = PROP_SUBCATEGORIES1.split("|");
                for (String link:listSubcategories) {
                    readAllItemLinks(listPages, link);
                }
            }else if (!PROP_CATEGORY1.toUpperCase().equals("NO")){
                readAllItemLinks(listPages, givenURL.concat("/").concat(PROP_CATEGORY1));
            }

            if (listPages.size() != 0){
                for (String linkOfItem: listPages) {
                    readItemDiscription(linkOfItem);
                }
            }

            if (dataToBase.size() != 0) {
                for (String[] item : dataToBase
                     ) {
                    addToResultString(Arrays.toString(item), addTo.LogFileAndConsole);
                }
            }

            driver.close();
        }
    }

    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink){

        //?user=1 - частные
        //?user=2 - компании
        givenLink = givenLink.concat("?user=1");

        String cssSelector_Items = "a.item-description-title-link";
        String cssSelector_NextPage = "a.pagination-page.js-pagination-next";

        try {
            addToResultString("Trying open page: ".concat(givenLink), addTo.LogFileAndConsole);
            if (driver == null) startingWebDriver(givenLink);
            driver.navigate().to(givenLink);
            addToResultString(driver.getCurrentUrl(), addTo.LogFileAndConsole);
        } catch (Exception e) {
//                e.printStackTrace();
            addToResultString("Can't open new page: ".concat(givenLink), addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            try {driver.quit();} catch (Exception e1){/**/};
            return;
        }

        List<WebElement> listItems;
        Boolean readPage = true;
        WebElement nextPage;

        try {
            int countIteration = 0;

            while (readPage) {

                if (MAX_COUNT_ELEMENTS != -1 && countIteration >= MAX_COUNT_ELEMENTS) break;
                countIteration++;

                listItems = driver.findElements(By.cssSelector(cssSelector_Items));

                for (WebElement elementGood : listItems) {
                    try {
                        listPages.add(elementGood.getAttribute("href"));

                    } catch (Exception e) {
                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                listItems.clear();

                try {
                    nextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                    String linkNextPage = nextPage.getAttribute("href");
                    if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
                }catch (Exception e) {
                    readPage = false;
                }
            }

            addToResultString("All item(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }

    private static void readItemDiscription(String givenLink) {

        try {
            addToResultString("Trying open page: ".concat(givenLink), addTo.LogFileAndConsole);
            if (driver == null) startingWebDriver(givenLink);
            driver.navigate().to(givenLink);
        } catch (Exception e) {
            addToResultString("Can't open new page: ".concat(givenLink), addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            try {
                driver.quit();
            } catch (Exception e1) {/**/}
            return;
        }

        String cssSelector_Item             = "div.item-sku > span#item_id";
        String cssSelector_ItemName         = "h1.h1";
        String cssSelector_ItemPrice        = "span.p_i_price > span";
        String cssSelector_ItemOwner        = "div#seller > strong";
        String cssSelector_ItemCity         = "div#map";
        String cssSelector_ItemParams       = "div.item-params";
        String cssSelector_ItemDecription   = "div.description.description-text";
        String cssSelector_ItemPhoneButton  = "span.button.button-azure.description__phone-btn.js-phone-show__link ";

        String goodCategory = PROP_CATEGORY1;
        String goodTitle = "";
        String goodLink = "";

            int countIteration = 0;
            try {
                String item             = driver.findElement(By.cssSelector(cssSelector_Item)).getText();
                String itemName         = driver.findElement(By.cssSelector(cssSelector_ItemName)).getText();
                String itemPrice        = driver.findElement(By.cssSelector(cssSelector_ItemPrice)).getText();
                String itemOwner        = driver.findElement(By.cssSelector(cssSelector_ItemOwner)).getText();
                String itemCity         = driver.findElement(By.cssSelector(cssSelector_ItemCity)).getText();
                String itemParams       = driver.findElement(By.cssSelector(cssSelector_ItemParams)).getText();
                String itemDescription  = driver.findElement(By.cssSelector(cssSelector_ItemDecription)).getText();
                String[] toList = {
                        String.valueOf(countIteration),
                        item,
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                        itemName,
                        itemPrice,
                        itemOwner,
                        itemCity,
                        itemParams,
                        itemDescription};
                dataToBase.add(toList);

            } catch (Exception e) {
                addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
            }
    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

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
//                    driver_GUI = new FirefoxDriver(profile);
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
                    driver = new FirefoxDriver(profile);;
//                    driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
//                    driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    break;

                default:
                    driver_noGUI = new HtmlUnitDriver();
                    break;
            }

            driver.navigate().to(givenURL);

        } catch (Exception e) {
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }

    private static void setCookie(String givenURL){

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
        WebDriver driver_GUI = new FirefoxDriver(profile);
        driver_GUI.navigate().to(givenURL);
//        Set<Cookie> cookieAvito = driver_GUI.manage().getCookies();
//        driver_GUI.close();

//        driver_noGUI.manage().deleteAllCookies();
//        for (Cookie cookie:cookieAvito
//             ) {
//            driver_noGUI.manage().addCookie(cookie);
//        }
//        driver_noGUI.getCurrentUrl();

//        div.form-fieldset__context.js-captcha
//        img.form-captcha-image
//        button.button.button-origin // submit
    }
}
