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
public class Read_GlobusTutBy {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_PAGES = -1; //309
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 15;
    private static int START_RECORDS_WITH = PROP_START_RECORD_IN;
    private static int FINISH_RECORDS_IN = PROP_FINISH_RECORD_IN;
    private static boolean USE_GUI = false;
//    private static boolean NEED_PHONENUMBER = true;

    public static class ReadGlobusTutBy {

        public ReadGlobusTutBy(String givenURL) {

            int countIteration = 0;

            startingWebDriver("");

//            if (driver_noGUI!=null) driver_noGUI.setJavascriptEnabled(false);

            ArrayList<String> listPages = new ArrayList<String>();
            ArrayList<String[]> dataToBase = new ArrayList<String[]>();

            if (!PROP_SUBCATEGORIES1.equalsIgnoreCase("NO")) {
                String[] listSubcategories = PROP_SUBCATEGORIES1.split("|");
                for (String link : listSubcategories) {
                    readAllItemLinks(listPages, link);
                }
            } else if (!PROP_CATEGORY1.equalsIgnoreCase("NO")) {
                readAllItemLinks(listPages, givenURL.concat("/").concat(PROP_CATEGORY1));
            } else readAllItemLinks(listPages, givenURL);

            if (listPages.size() != 0) {
                for (String linkOfItem : listPages) {

                    if (listPages.indexOf(linkOfItem) + 1 < START_RECORDS_WITH) continue;
                    countIteration++;

                    String countToLog = String.valueOf(listPages.indexOf(linkOfItem) + 1).concat("/").concat(String.valueOf(listPages.size()));
                    readItemDiscription(dataToBase, linkOfItem, countToLog);

                    if (countIteration % BLOCK_RECORDS_TO_BASE == 0) {
                        addToResultString("Writing data in base..", addTo.LogFileAndConsole);
//                        writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                        addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
                    }

                    if (FINISH_RECORDS_IN != -1 & countIteration > FINISH_RECORDS_IN) break;
//                    if (MAX_COUNT_ITEMS != -1 & countIteration >= MAX_COUNT_ITEMS) break;
                }
                if (countIteration % BLOCK_RECORDS_TO_BASE != 0) {
                    addToResultString("Writing data in base..", addTo.LogFileAndConsole);
//                    writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                    addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
                }
            }

            dataToBase = new ArrayList<String[]>();

            if (USE_GUI) driver.close();
            else driver_noGUI.close();
//            driver_noGUI.close();

        }
   }


    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink) {


        String cssSelector_Items = "table tbody tr[valign=top] td:last-child a";

        List<WebElement> listItems;

        try {
            int countIteration = 0;

            while (true) {

                if (MAX_COUNT_PAGES != -1 & countIteration++ >= MAX_COUNT_PAGES) break;

                try {
                    addToResultString("Trying open page: ".concat(givenLink), addTo.LogFileAndConsole);
                    if (USE_GUI) {
                        driver.navigate().to(givenLink);
                    }
                    else {
                        driver_noGUI.navigate().to(givenLink);
                    }
                } catch (Exception e) {
                    addToResultString("Can't open new page: ".concat(givenLink), addTo.LogFileAndConsole);
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
                        if(!listPages.contains(href)) listPages.add(href);
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
                if (MAX_COUNT_PAGES == -1) break;
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
            if (USE_GUI) driver.navigate().to(givenLink);
            else driver_noGUI.navigate().to(givenLink);
//            addToResultString("Page is opened[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);

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

        String cssSelector_Title = "table tbody tr td font.subhPage";
        String cssSelector_Items = "div[align=justify]";
        String cssSelector_Object = "div table tbody tr td[align=center] font";
        String cssSelector_Place = "font.subhPage";
        String cssSelector_Coords = "a";
        String cssSelector_Description = "div.psPlacePage i";
        String cssSelector_Creator = "td.imageAuthor";
        String cssSelector_Images = "";

        String globusTitle = "";
        String globusDistinct = "";
        String globusObject = "";
        String globusPlace = "";
        String globusCoords = "";
        String globusDescription = "";
        String globusCreator = "";
        String globusImages = "";
//        String firmQuery = PROP_DISCRIPTION;

        try {

            if (USE_GUI) {

            } else {
                try {
                    globusObject = driver_noGUI.findElement(By.cssSelector(cssSelector_Object)).getText();
                    List<WebElement> listElement = driver_noGUI.findElements(By.cssSelector(cssSelector_Items));
                    for (WebElement element : listElement) {
//                        try{globusTitle = element.findElement(By.cssSelector(cssSelector_Title)).getText();}catch(Exception e){/**/}
                        try{globusPlace = element.findElement(By.cssSelector(cssSelector_Place)).getText();}catch(Exception e){/**/}
                        try{globusCoords = element.findElement(By.cssSelector(cssSelector_Coords)).getAttribute("href");}catch(Exception e){/**/}
                        try{globusDescription = element.findElement(By.cssSelector(cssSelector_Description)).getText();}catch(Exception e){/**/}

                        String[] toList = {"0",
                                (globusTitle),           // 1
                                (globusObject),           // 2
                                (globusPlace),          // 3
                                (globusCoords),          // 4
                                (globusDescription),     // 5
//                    clearLetters(firmQuery),          // 13
                                (givenLink)};         // 15
                        if (!globusPlace.isEmpty()) dataToBase.add(toList);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (Exception e) {
            addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
        } finally {

/**/

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
        return new String(givenString.replace("'", "").replace("\"", "").replace("\\", "/").replace(",", "|"));
    }

}

