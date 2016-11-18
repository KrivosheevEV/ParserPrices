package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by KrivosheevEV on 04.10.2016.
 */
public class Watch_Youtube {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listProxy;
    private static int MAX_COUNT_PAGES = 1;
    private static int MAX_COUNT_ITEMS = -1;
//    private static int MAX_COUNT_TOBASE = 10;
    private static int MAX_COUNT_EXPAND = 5;
    private static int WAITING_FOR_EXPAND = 10;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = 1;      // !!!!!
    private static boolean NEED_PHONE_NUMBER = true;
    private static int MAX_COUNT_REREADING_CAPTCHA = 3;
    private static boolean USE_GUI = true;

    public static class WatchMyYoutube{

        public WatchMyYoutube(){

            int countPages = 1;
            String givenURL = "http://www.youtube.ru";

            listProxy = new ArrayList<String>();
            startingWebDriver(givenURL);
            driver.navigate().to(givenURL);
            try{
                while ((new WebDriverWait(driver, WAITING_FOR_EXPAND, 1000)).until(
                        ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("input.style-scope.ytd-searchbox")))) {

                    System.out.println(countPages);
                    if (MAX_COUNT_EXPAND != -1 & countPages++ >= MAX_COUNT_EXPAND) break;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            if (USE_GUI) driver.close();
            else driver_noGUI.close();
        }
    }




    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

        String proxyAddress = getRandomProxy();

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
        if (!proxyAddress.isEmpty()) {
            String[] proxy = proxyAddress.split(":");
            profile.setPreference("network.proxy.type", 1);
            profile.setPreference("network.proxy.http", proxy[0]);
            profile.setPreference("network.proxy.http_port", Integer.valueOf(proxy[1]));
            profile.setPreference("network.proxy.ssl", proxy[0]);
            profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxy[1]));
        }
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
        profile.setPreference("pdfjs.disabled", true);

        //userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

        try {
            addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
            if (USE_GUI) {
                driver = new FirefoxDriver(profile);
            } else {
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);

            }


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

    // Write data into base.
    private static void writeDataIntoBase(ArrayList<String[]> listDataToBase, int startRecordFromPosition) {

        if (listDataToBase == null || listDataToBase.isEmpty()) {
            addToResultString("Not have data to write into base.", addTo.LogFileAndConsole);
            return;
        }

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
        int countOfNewRecords = 0;
        java.sql.Date dateOfItemToQuery;

        for (String[] stringToBase : listDataToBase) {

//            if (listDataToBase.indexOf(stringToBase) < startRecordFromPosition - 1) ;

            try {
                dateOfItemToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateOfItemToQuery = new java.sql.Date(System.currentTimeMillis());
            }

//            String query_recordExist = "SELECT item FROM goods t WHERE t.item LIKE '" + stringToBase[2] + "' AND t.shop LIKE '" + stringToBase[3] + "' LIMIT 5;";
            String query_recordExist = "SELECT item FROM ".concat(shopName.name()).concat(" t WHERE t.item LIKE '").concat(stringToBase[4]).concat("' LIMIT 5;");

            String query_needUpdate = "SELECT item FROM ".concat(shopName.name()).concat(" t WHERE t.item LIKE '").concat(stringToBase[4]).concat("' AND t.dateofitem <> '") + dateOfItemToQuery + ("' LIMIT 5;");

            String query_updateRecord = "UPDATE ".concat(shopName.name()).concat(" SET ").concat(
                    "city = '").concat(stringToBase[1]).concat("', ").concat(
                    "category = '").concat(stringToBase[2]).concat("', ").concat(
                    "subcategory = '").concat(stringToBase[3]).concat("', ").concat(
                    "item = '").concat(stringToBase[4]).concat("', ").concat(
                    "dateofitem = '").concat(String.valueOf(dateOfItemToQuery)).concat("', ").concat(
                    "itemname = '").concat(stringToBase[6]).concat("', ").concat(
                    "price = '").concat(stringToBase[7]).concat("', ").concat(
                    "owner = '").concat(stringToBase[8]).concat("', ").concat(
                    "phonenumber = '").concat(stringToBase[9]).concat("', ").concat(
                    "cityitem = '").concat(stringToBase[10]).concat("', ").concat(
                    "params = '").concat(stringToBase[11]).concat("', ").concat(
                    "description = '").concat(stringToBase[12]).concat("', ").concat(
                    "link = '").concat(stringToBase[13]).concat("', ").concat(
                    "phonenumber64 = '").concat(stringToBase[14]).concat("' WHERE item LIKE '").concat(stringToBase[3]).concat("' LIMIT 5;");
                                                                                                 //1,    2,        3,           4,    5,          6,        7,     8,     9,           10,      11,      12,           13,   14
            String query_writeNewRecord = "INSERT INTO general.".concat(shopName.name()).concat(" (city, category, subcategory, item, dateofitem, itemname, price, owner, phonenumber, cityitem, params, description,  link, phonenumber64)") +
                    " VALUES ('" +
                    stringToBase[1].concat("', '").concat(
                    stringToBase[2]).concat("', '").concat(
                    stringToBase[3]).concat("', '").concat(
                    stringToBase[4]).concat("', '") +
                    dateOfItemToQuery + ("', '").concat(
                    writeDataToBase.clearLetters(stringToBase[6])).concat("', '").concat(
                    stringToBase[7]).concat("', '").concat(
                    writeDataToBase.clearLetters(stringToBase[8])).concat("', '").concat(
                    stringToBase[9]).concat("', '").concat(
                    stringToBase[10]).concat("', '").concat(
                    writeDataToBase.clearLetters(stringToBase[11])).concat("', '").concat(
                    writeDataToBase.clearLetters(new String(stringToBase[12].replace(",", ";")))).concat("', '").concat(
                    stringToBase[13]).concat("', '").concat(
                    stringToBase[14]).concat("');");

//            query_recordExist = writeDataToBase.clearLetters(query_recordExist);
//            query_needUpdate = writeDataToBase.clearLetters(query_needUpdate);
//            query_updateRecord = writeDataToBase.clearLetters(query_updateRecord);
//            query_writeNewRecord = writeDataToBase.clearLetters(query_writeNewRecord);

            if (writeDataToBase.dataExist(statement, query_recordExist)) {
                if (writeDataToBase.dataExist(statement, query_needUpdate) & writeDataToBase.writeDataSuccessfully(statement, query_updateRecord)) countOfUpdate++;
            } else if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecord)) countOfNewRecords++;

            if (MAX_COUNT_ITEMS != -1 & countOfRecords >= MAX_COUNT_ITEMS) break;
            countOfRecords++;
        }

        addToResultString("Reading records: ".concat(String.valueOf(countOfRecords - startRecordFromPosition)).concat(" in base."), addTo.LogFileAndConsole);
        addToResultString("Added records:   ".concat(String.valueOf(countOfNewRecords)).concat(" in base."), addTo.LogFileAndConsole);
        addToResultString("Updated records: ".concat(String.valueOf(countOfUpdate)).concat(" in base."), addTo.LogFileAndConsole);

        addToResultString("Close base connections", addTo.Console);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    private static String getRandomProxy(){

        String resultString, stringOfProxies;

        if (!PROP_PROXY.equalsIgnoreCase("FOXTOOLS")) return "";
        if(listProxy.isEmpty()){
            GetPost getHtmlData = new GetPost();
            try {
                stringOfProxies = getHtmlData.sendGet("http://api.foxtools.ru/v2/Proxy.txt?cp=UTF-8&lang=RU&type=HTTPS&available=Yes&free=Yes&limit=10&uptime=2&country=RU");
            } catch (Exception e) {
                return  "";
            }
            if (!stringOfProxies.isEmpty()) {
                for (String proxyAddress:stringOfProxies.split(";")
                     ) {
                    listProxy.add(proxyAddress);
                }
                listProxy.remove(0);
            }
        }
        Random r = new Random();
        int i = r.nextInt(listProxy.size() - 1);
        resultString = listProxy.get(i);
        listProxy.remove(i);

        return resultString;
    }
}
