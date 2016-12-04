package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 10/22/16.
 */
public class Find_Emails {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages, listEmails;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_PAGES = 5; //309
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 15;
    private static int START_RECORDS_WITH = PROP_START_RECORD_IN;
    private static int FINISH_RECORDS_IN = PROP_FINISH_RECORD_IN;
    private static boolean USE_GUI = false;
//    private static boolean NEED_PHONENUMBER = true;

    public static class FindEmails {

        public ArrayList<String> FindEmails(String givenURLs) {

            int countIteration = 0;

            ArrayList<String> listPages = new ArrayList<String>();
            ArrayList<String> listEmails = new ArrayList<String>();
            ArrayList<String[]> dataToBase = new ArrayList<String[]>();

            List<WebElement> listItems;
            String cssSelector_Items = "a";
            String sourceHTML = "";

            if (givenURLs.isEmpty()) return listEmails;

            startingWebDriver("");

            for (String givenURL:givenURLs.split(",")
                 ) {

                if (USE_GUI) driver.get(givenURL);
                else driver_noGUI.get(givenURL);

                if (USE_GUI) listItems = driver.findElements(By.cssSelector(cssSelector_Items));
                else listItems = driver_noGUI.findElements(By.cssSelector(cssSelector_Items));

                for (WebElement elementGood : listItems) {
                    try {
                        String href = elementGood.getAttribute("href");
                        if(!href.isEmpty() & !listPages.contains(href)) listPages.add(href);
                    } catch (Exception e) {
//                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                for(String link : listPages){
                    try {
                        if (USE_GUI) {
                            driver.get(link);
                            sourceHTML = driver.getPageSource();
                        }
                        else {
                            driver_noGUI.get(link);
                            sourceHTML = driver_noGUI.getPageSource();
                        }
                    }catch (Exception e){
                        continue;
                    }

                    int countEmails=sourceHTML.split("@").length-1;
                    if (sourceHTML.isEmpty() || countEmails==0) continue;

                    String prefixEmail = "", suffixEmail = "";
                    int firstIndexAT = 0;

                    for (int i=1; i<countEmails; i++){
                        firstIndexAT = sourceHTML.indexOf("@", firstIndexAT);

                        for (int leftIndex = firstIndexAT; leftIndex>(firstIndexAT-25); leftIndex--){
                            char letter = sourceHTML.charAt(leftIndex-1);
                            int code = letter;
                            if (code==95 || code==64 || code==46 || (code>=48 & code<=57) || (code>=65 & code<=90) || (code>=97 & code<=122)){
                                prefixEmail = letter + prefixEmail;
                            }else leftIndex = firstIndexAT-25;
                        }
                        for (int rightIndex = firstIndexAT; rightIndex<(firstIndexAT+25); rightIndex++){
                            char letter = sourceHTML.charAt(rightIndex+1);
                            int code = letter;
                            if (code==95 || code==64 || code==46 || (code>=48 & code<=57) || (code>=65 & code<=90) || (code>=97 & code<=122)){
                                suffixEmail = suffixEmail + letter;
                            }else rightIndex = firstIndexAT+25;
                        }

                        while (prefixEmail.startsWith(".")) prefixEmail = new String(prefixEmail.substring(1));
                        while (suffixEmail.endsWith(".")) suffixEmail = new String(suffixEmail.substring(0,suffixEmail.length()-1));
                        String email = !prefixEmail.isEmpty() & !suffixEmail.isEmpty() ? prefixEmail.concat("@").concat(suffixEmail).trim() : "";
                        prefixEmail = ""; suffixEmail = "";
                        if (email.length()>1 & email.contains("."))
                            if (!listEmails.contains(email) & !itsExclusionEmail(email)) listEmails.add(email);

//                    System.out.println(new String(sourceHTML.substring(firstIndexAT-15, firstIndexAT+15)));
                    }

                }
            }



            for (String email_:listEmails
                    ) {
                System.out.println(email_);
            }


            if (USE_GUI) driver.close();
            else driver_noGUI.close();
//            driver_noGUI.close();

            return listEmails;
        }


   }

    private static Boolean itsExclusionEmail(String givenEmail){

        ArrayList<String> exclusionArray = new ArrayList<String>();
        exclusionArray.add("Rating@Mail.ru".toUpperCase());

        return exclusionArray.contains(givenEmail.toUpperCase());
    }



    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink) {

//        driver_noGUI.setJavascriptEnabled(true);
        USE_GUI = true;
        startingWebDriver("");

        String cssSelector_Items = "div.ttl.fild a";

        List<WebElement> listItems;
        Boolean readPage = true;

        try {
            int countIteration = 0;

            while (readPage) {

                if (MAX_COUNT_PAGES != -1 & countIteration++ >= MAX_COUNT_PAGES) break;

                String linkOfPage = givenLink.concat("&page=").concat(String.valueOf(countIteration));

                try {
                    addToResultString("Trying open page: ".concat(linkOfPage), addTo.LogFileAndConsole);
                    if (USE_GUI) {
                        driver.get("http://ya.ru");
                        driver.navigate().to(linkOfPage);
                    }
                    else {
//                        driver_noGUI.quit();
//                        startingWebDriver(linkOfPage);
                        driver_noGUI.setJavascriptEnabled(true);
//                        driver_noGUI.get("http://www.ya.ru");
                        driver_noGUI.navigate().to(linkOfPage);
//                        driver_noGUI.get(linkOfPage);
                        driver_noGUI.setJavascriptEnabled(false);
                    }
                } catch (Exception e) {
                    addToResultString("Can't open new page: ".concat(linkOfPage), addTo.LogFileAndConsole);
                    addToResultString(e.toString(), addTo.LogFileAndConsole);
                    if (USE_GUI) try {
                        driver.quit();
                    } catch (Exception e1) {/**/}
                    else try {
                        driver_noGUI.quit();
                    } catch (Exception e1) {/**/}
                    return;
                }

                if (USE_GUI) listItems = driver.findElements(By.cssSelector(cssSelector_Items));
                else listItems = driver_noGUI.findElements(By.cssSelector(cssSelector_Items));

                for (WebElement elementGood : listItems) {
                    try {
                        String href = elementGood.getAttribute("href");
                        if(href.contains("product")) listPages.add(href);
                    } catch (Exception e) {
                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                listItems.clear();

                /*try {
                    if (driver!=null) {
                        driver.manage().timeouts().implicitlyWait(15000, TimeUnit.MILLISECONDS);
                    } else {
//                        driver_noGUI.setJavascriptEnabled(true);
//                        driver_noGUI.manage().timeouts().implicitlyWait(15000, TimeUnit.MILLISECONDS);
//                        driver_noGUI.setJavascriptEnabled(false);
                    }

                } catch (Exception e) {
                    readPage = false;
                }*/
            }

            addToResultString("All pages(".concat(Integer.toString(--countIteration)).concat(") was reading(").concat(String.valueOf(listPages.size())).concat(")"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
//        driver_noGUI.setJavascriptEnabled(false);

    }

    // Reading description.
    private static void readItemDiscription(ArrayList<String[]> dataToBase, String givenLink, String countToLog) {

        try {


            addToResultString("Trying open page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);

//            if (USE_GUI) {
//                if (driver == null) startingWebDriver(givenLink);
//            } else {
//                if (driver_noGUI == null) startingWebDriver(givenLink);
//            }

            if (USE_GUI) driver.navigate().to(givenLink);
            else driver_noGUI.navigate().to(givenLink);

            //if (avitoNeedCaptcha()) enterAvitoCaptcha(givenLink);

        } catch (Exception e) {
            addToResultString("Can't open new page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            if (USE_GUI) try {
                driver.quit();
            } catch (Exception e1) {/**/}
            else try {
                driver_noGUI.quit();
            } catch (Exception e1) {/**/}
            return;
        }

        String cssSelector_LabelValue = "div.field span";
        String cssSelector_Title = "h1.title";

        String ceramicTitle = "";
        String ceramicColection = "";
        String ceramicUsed = "";
        String ceramicStyle = "";
        String ceramicColor = "";
        String ceramicWidth = "";
        String ceramicHight = "";
        String ceramicPlace = "";
        String ceramicCategory = "";
        String ceramicSurface = "";
        String ceramicPriceFor = "";
        String ceramicTextured = "";
        String ceramicRectified = "";
        String firmQuery = PROP_DISCRIPTION;

        try {

            if (USE_GUI) {
//                try {
//                    ceramicLabel = driver.findElement(By.cssSelector(cssSelector_Label)).getText();
//                } catch (Exception e) {
//                    ceramicLabel = "";
//                }
//                try {
//                    ceramicValue = driver.findElement(By.cssSelector(cssSelector_Value)).getText();
//                } catch (Exception e) {
//                    ceramicValue = "";
//                }
            } else {
                try {
                    ceramicTitle = driver_noGUI.findElement(By.cssSelector(cssSelector_Title)).getText();
                    List<WebElement> listElement = driver_noGUI.findElements(By.cssSelector(cssSelector_LabelValue));
                    for (WebElement element : listElement) {
                        switch (element.getText()) {
                            case "Коллекция:":
                                ceramicColection = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Применение:":
                                ceramicUsed = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Стиль:":
                                ceramicStyle = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Цвет:":
                                ceramicColor = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Ширина:":
                                ceramicWidth = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Высота:":
                                ceramicHight = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Место в коллекции:":
                                ceramicPlace = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Категория:":
                                ceramicCategory = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Поверхность:":
                                ceramicSurface = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Цена за:":
                                ceramicPriceFor = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Фактура плитки:":
                                ceramicTextured = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;
                            case "Ректифицированная:":
                                ceramicRectified = listElement.get(listElement.indexOf(element) + 1).getText();
                                break;

                        }
                    }
                } catch (Exception e) {
//                    ceramicLabel = "";
                }

            }
        } catch (Exception e) {
            addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
        } finally {


            String[] toList = {"0",
                    clearLetters(ceramicTitle),           // 1
                    clearLetters(ceramicColection),           // 1
                    clearLetters(ceramicUsed),          // 2
                    clearLetters(ceramicStyle),          // 3
                    clearLetters(ceramicColor),          // 4
                    clearLetters(ceramicWidth),          // 5
                    clearLetters(ceramicHight),          // 6
                    clearLetters(ceramicPlace),          // 7
                    clearLetters(ceramicCategory),          // 8
                    clearLetters(ceramicSurface),          // 9
                    clearLetters(ceramicPriceFor),          // 10
                    clearLetters(ceramicTextured),          // 11
                    clearLetters(ceramicRectified),          // 12
                    clearLetters(firmQuery),          // 13
                    clearLetters(givenLink)};         // 15
            if (!ceramicTitle.isEmpty()) dataToBase.add(toList);
        }
    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

        String proxyString = "";//getRandomProxy();

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
        if (!proxyString.isEmpty()) {
            String[] proxy = proxyString.split(":");
            profile.setPreference("network.proxy.type", 1);
            profile.setPreference("network.proxy.http", proxy[0]);
            profile.setPreference("network.proxy.http_port", Integer.valueOf(proxy[1]));
            profile.setPreference("network.proxy.ssl", proxy[0]);
            profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxy[1]));
        }
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
        profile.setPreference("pdfjs.disabled", true);

//        userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

        try {

            if (USE_GUI) {
                if (driver_noGUI!=null) driver_noGUI.quit();
                addToResultString("Trying start new FireDriver", addTo.LogFileAndConsole);
                driver = new FirefoxDriver(profile);
                driver.manage().timeouts().implicitlyWait(60000, TimeUnit.MILLISECONDS);
            } else {
                if (driver!=null) driver.quit();
                addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                driver_noGUI.manage().timeouts().implicitlyWait(60000, TimeUnit.MILLISECONDS);
//                driver_noGUI.setJavascriptEnabled(true);
                if (!givenURL.isEmpty()) driver_noGUI.get(givenURL);

            }

        } catch (Exception e) {
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }

    private static void setCookie(String givenURL) {

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

    private static String clearPhoneNumber(String givenPhoneNumber) {

        givenPhoneNumber = new String(givenPhoneNumber.trim()
                .replace("-", "")
                .replace("=", "")
                .replace(" ", "")
                .replace(" ", ""));

//        givenPhoneNumber = givenPhoneNumber.replace("+7", "8");

        return givenPhoneNumber;
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

        java.sql.Date dateToQuery;

        String query_writeNewRecords_prefix = "INSERT INTO general.".concat(shopName.name()).concat(" (date, title, colection, used, style, color, width, hight, place, category, surface, pricefor, textured, rectified, query, link)").concat(" VALUES ");
        String query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE date=VALUES(date), colection=VALUES(colection), used=VALUES(used), style=VALUES(style), color=VALUES(color), width=VALUES(width), hight=VALUES(hight), place=VALUES(place), category=VALUES(category), surface=VALUES(surface), pricefor=VALUES(pricefor), textured=VALUES(textured), rectified=VALUES(rectified), query=VALUES(query), link=VALUES(link);";
        String query_writeNewRecords = query_writeNewRecords_prefix;

        addToResultString("Start record into base.", addTo.LogFileAndConsole);

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            try {
                dateToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateToQuery = new java.sql.Date(System.currentTimeMillis());
            }

            query_writeNewRecords = query_writeNewRecords.concat(" ('" +
                    dateToQuery + "', '" +
                    stringToBase[1] + "', '" +
                    stringToBase[2] + "', '" +
                    stringToBase[3] + "', '" +
                    stringToBase[4] + "', '" +
                    stringToBase[5] + "', '" +
                    stringToBase[6] + "', '" +
                    stringToBase[7] + "', '" +
                    stringToBase[8] + "', '" +
                    stringToBase[9] + "', '" +
                    stringToBase[10] + "', '" +
                    stringToBase[11] + "', '" +
                    stringToBase[12] + "', '" +
                    stringToBase[13] + "', '" +
                    stringToBase[14] + "', '" +
                    stringToBase[15] + "') ");

            if (countOfRecords % BLOCK_RECORDS_TO_BASE == 0) {
                query_writeNewRecords = query_writeNewRecords.concat(query_writeNewRecords_suffix);
                if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords)) countOfNewRecords++;
                query_writeNewRecords = query_writeNewRecords_prefix;
            } else if (countOfRecords != listDataToBase.size())
                query_writeNewRecords = query_writeNewRecords.concat(",");

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }

        if (countOfRecords % BLOCK_RECORDS_TO_BASE != 0) {
            query_writeNewRecords = query_writeNewRecords.concat(query_writeNewRecords_suffix);
            if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords))
                countOfNewRecords = (countOfNewRecords * BLOCK_RECORDS_TO_BASE) + (countOfRecords - (countOfNewRecords * BLOCK_RECORDS_TO_BASE));
        }

        addToResultString("Finish record into base.", addTo.LogFileAndConsole);


        addToResultString("Reading records: ".concat(String.valueOf(countOfRecords - startRecordFromPosition)).concat(" in base."), addTo.LogFileAndConsole);
        addToResultString("Added records:   ".concat(String.valueOf(countOfNewRecords)).concat(" in base."), addTo.LogFileAndConsole);
        addToResultString("Updated records: ".concat(String.valueOf(countOfUpdate)).concat(" in base."), addTo.LogFileAndConsole);

        addToResultString("Close base connections", addTo.Console);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    private static String clearPrice(String givenPrice) {
        givenPrice = new String(givenPrice.trim().replace("руб.", "").replace(" ", ""));
        return givenPrice;
    }

    public static String clearLetters(String givenString){
        return new String(givenString.replace("'", "").replace("\"", "").replace("\\", "/"));
    }

}

