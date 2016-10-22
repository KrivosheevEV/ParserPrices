package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
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
import static ru.parserprices.myparser.MainParsingPrices.addToResultString;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 10/22/16.
 */
public class Read_2Gis {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_PAGES = 3;
    private static int MAX_COUNT_ITEMS = -1;
    //    private static int MAX_COUNT_TOBASE = 10;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 15;
    private static int START_RECORDS_WITH = 1;      // !!!!!
    private static boolean NEED_PHONE_NUMBER = true;
    private static int MAX_COUNT_REREADING_CAPTCHA = 3;
    private static boolean USE_GUI = true;

    public static class Read2Gis{

        public Read2Gis(String givenURL){

            int countIteration = 0;

//            if (shopCity == shopCities.sanktpeterburg) givenURL = "https://2gis.ru/spb";
//            else if (shopCity == shopCities.nikolsk) givenURL = "https://www.avito.ru/penzenskaya_oblast_nikolsk";
//            else if (shopCity == shopCities.novokuybishevsk) givenURL = "https://www.avito.ru/novokuybyshevsk";
//            else if (shopCity == shopCities.ershov) givenURL = "https://www.avito.ru/ershov";
//            else if (shopCity == shopCities.samara) givenURL = "https://www.avito.ru/samara";

            startingWebDriver(givenURL);

            ArrayList<String> listPages = new ArrayList<String>();
            ArrayList<String[]> dataToBase = new ArrayList<String[]>();

            if (!PROP_SUBCATEGORIES1.toUpperCase().equals("NO")){
                String[] listSubcategories = PROP_SUBCATEGORIES1.split("|");
                for (String link:listSubcategories) {
                    readAllItemLinks(listPages, link);
                }
            }else if (!PROP_CATEGORY1.toUpperCase().equals("NO")){
                readAllItemLinks(listPages, givenURL.concat("/").concat(PROP_CATEGORY1));
            }else readAllItemLinks(listPages, givenURL);

            if (listPages.size() != 0){
                for (String linkOfItem: listPages) {

                    if (listPages.indexOf(linkOfItem) + 1 < START_RECORDS_WITH) continue;
                    countIteration++;

                    String countToLog = String.valueOf(listPages.indexOf(linkOfItem)+1).concat("/").concat(String.valueOf(listPages.size()));
                    readItemDiscription(dataToBase, linkOfItem, countToLog);

                    if (countIteration % BLOCK_RECORDS_TO_BASE == 0){
                        addToResultString("Writing data in base..", addTo.LogFileAndConsole);
                        //writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                        addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
                    }

                    if (MAX_COUNT_ITEMS != -1 & countIteration >= MAX_COUNT_ITEMS) break;
                }
            }

//            if (dataToBase.size() != 0) {
//                for (String[] item : dataToBase
//                     ) {
////                    addToResultString(Arrays.toString(item), addTo.LogFileAndConsole);
//                    addToResultString(item[0].concat(" - ").concat(item[8]), addTo.LogFileAndConsole);
//                }
//            }



            dataToBase = new ArrayList<String[]>();

            if (USE_GUI) driver.close();
            else driver_noGUI.close();
//            driver_noGUI.close();
        }
    }

    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink){

        //?user=1 - частные
        //?user=2 - компании
        //givenLink = givenLink.concat("?user=1");

        String cssSelector_Items = "a.miniCard__headerTitleLink";
        String cssSelector_NextPage = "div.pagination__arrow._right";
        String cssSelector_NextPageDisabled = "div.pagination__arrow._right._disabled";

        try {
            addToResultString("Trying open page: ".concat(givenLink), addTo.LogFileAndConsole);
//            if (driver == null) startingWebDriver(givenLink);
            if (USE_GUI) driver.navigate().to(givenLink);
            else driver_noGUI.navigate().to(givenLink);
            //if (avitoNeedCaptcha()) enterAvitoCaptcha(givenLink);
            //addToResultString(driver.getCurrentUrl(), addTo.LogFileAndConsole);
        } catch (Exception e) {
//                e.printStackTrace();
            addToResultString("Can't open new page: ".concat(givenLink), addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            if (USE_GUI) try {driver.quit();} catch (Exception e1){/**/}
            else try {driver_noGUI.quit();} catch (Exception e1){/**/}
            return;
        }

        List<WebElement> listItems;
        Boolean readPage = true;

        try {
            int countIteration = 0;

            while (readPage) {

                if (MAX_COUNT_PAGES != -1 & countIteration++ >= MAX_COUNT_PAGES) break;

                if (USE_GUI) listItems = driver.findElements(By.cssSelector(cssSelector_Items));
                else listItems = driver_noGUI.findElements(By.cssSelector(cssSelector_Items));

                for (WebElement elementGood : listItems) {
                    try {
                        listPages.add(elementGood.getAttribute("href"));

                    } catch (Exception e) {
                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                listItems.clear();

                try {
//                    String linkNextPage = nextPage.getAttribute("href");
//                    if (USE_GUI) {
//                        if (!driver.getCurrentUrl().equals(linkNextPage)) driver.get(linkNextPage);
//                    } else {
//                        if (!driver_noGUI.getCurrentUrl().equals(linkNextPage)) driver_noGUI.get(linkNextPage);
//                    }
                    WebElement but_NextPage;
                    if (USE_GUI) but_NextPage = driver.findElement(By.cssSelector(cssSelector_NextPage));
                    else but_NextPage = driver_noGUI.findElement(By.cssSelector(cssSelector_NextPage));

                    if (USE_GUI) {
//                        while ((new WebDriverWait(driver, WAITING_FOR_EXPAND)).until(
//                                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_NextPage)))) {

                            but_NextPage.sendKeys(Keys.ESCAPE);
                            but_NextPage.click();
                            driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);

//                            if (MAX_COUNT_EXPAND != -1 & countPages++ >= MAX_COUNT_EXPAND) break;
//                        }
                    }else {
//                        while ((new WebDriverWait(driver_noGUI, WAITING_FOR_EXPAND)).until(
//                                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_NextPage)))) {

                            driver_noGUI.setJavascriptEnabled(true);
//                            driver_noGUI.wait(1000);

                            but_NextPage.sendKeys(Keys.ESCAPE);
                            but_NextPage.click();
                            driver_noGUI.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);

//                            driver_noGUI.wait(1000);
//                            if (MAX_COUNT_EXPAND != -1 & countPages++ >= MAX_COUNT_EXPAND) break;
//                        }
                    }

                    try {
                        WebElement nextPageDisabled;
                        if (USE_GUI) {
                            nextPageDisabled = driver.findElement(By.cssSelector(cssSelector_NextPageDisabled));
                            readPage = false;
                        }else{
                            nextPageDisabled = driver_noGUI.findElement(By.cssSelector(cssSelector_NextPageDisabled));
                            readPage = false;
                        }
                    }catch (Exception e){
                        /**/
                    }

                }catch (Exception e) {
                    readPage = false;
                }
            }

            addToResultString("All pages(".concat(Integer.toString(countIteration)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }

    // Reading description.
    private static void readItemDiscription(ArrayList<String[]> dataToBase, String givenLink, String countToLog) {

        try {
            addToResultString("Trying open page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);


            if (USE_GUI) {
                if (driver == null) startingWebDriver(givenLink);
            }
            else {
                if (driver_noGUI == null) startingWebDriver(givenLink);
            }

            if (USE_GUI) driver.navigate().to(givenLink);
            else driver_noGUI.navigate().to(givenLink);

            //if (avitoNeedCaptcha()) enterAvitoCaptcha(givenLink);

        } catch (Exception e) {
            addToResultString("Can't open new page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            if (USE_GUI) try {driver.quit();} catch (Exception e1) {/**/}
            else try {driver_noGUI.quit();} catch (Exception e1) {/**/}
            return;
        }

        String cssSelector_Name = "h1.cardHeader__headerNameText";
        String cssSelector_butOtherContact = "div.contact__other";
        String cssSelector_Phone = "a.contact__phonesItemLink"; //href
        String cssSelector_Site = "div.contact__link._type_website > a.link.contact__linkText"; // innertext or title
        String cssSelector_VKontakte = "div.contact__link._social._type_vkontakte > a.link.contact__linkText";//href
        String cssSelector_Facebook = "div.contact__link._social._type_facebook > a.link.contact__linkText";//href
        String cssSelector_Email = "div.contact__link._type_email > a.link.contact__linkText";//href

        String firmName = "";
        String firmPhone = "";
        String firmSite = "";
        String firmVkontakte = "";
        String firmFacebook = "";
        String firmEmail= "";

        try {

            try {
                WebElement butOtherContact = driver.findElement(By.cssSelector(cssSelector_butOtherContact));
                butOtherContact.sendKeys(Keys.ESCAPE);
                butOtherContact.click();
            }catch (Exception e){/**/}

            if (USE_GUI) {
                try {firmName = driver.findElement(By.cssSelector(cssSelector_Name)).getText();} catch (Exception e) {firmName = "";}
                try {
                    for (WebElement element : driver.findElements(By.cssSelector(cssSelector_Phone)))
                        firmPhone = firmPhone.concat(element.getAttribute("href")).concat(" ");
                } catch (Exception e) {firmPhone = "";}
                try {firmSite = driver.findElement(By.cssSelector(cssSelector_Site)).getAttribute("title");} catch (Exception e) {firmSite = "";}
                try {firmVkontakte = driver.findElement(By.cssSelector(cssSelector_VKontakte)).getAttribute("href");} catch (Exception e) {firmVkontakte = "";}
                try {firmFacebook = driver.findElement(By.cssSelector(cssSelector_Facebook)).getAttribute("href");} catch (Exception e) {firmFacebook = "";}
                try {firmEmail = driver.findElement(By.cssSelector(cssSelector_Email)).getAttribute("href");} catch (Exception e) {firmEmail = "";}
            }else {
                try {firmName = driver_noGUI.findElement(By.cssSelector(cssSelector_Name)).getText();} catch (Exception e) {firmName = "";}
                try {
                    for (WebElement element : driver_noGUI.findElements(By.cssSelector(cssSelector_Phone)))
                        firmPhone = firmPhone.concat(element.getAttribute("href")).concat(" ");
                } catch (Exception e) {firmPhone = "";}
                try {firmSite = driver_noGUI.findElement(By.cssSelector(cssSelector_Site)).getAttribute("title");} catch (Exception e) {firmSite = "";}
                try {firmVkontakte = driver_noGUI.findElement(By.cssSelector(cssSelector_VKontakte)).getAttribute("href");} catch (Exception e) {firmVkontakte = "";}
                try {firmFacebook = driver_noGUI.findElement(By.cssSelector(cssSelector_Facebook)).getAttribute("href");} catch (Exception e) {firmFacebook = "";}
                try {firmEmail = driver_noGUI.findElement(By.cssSelector(cssSelector_Email)).getAttribute("href");} catch (Exception e) {firmEmail = "";}
            }
        } catch (Exception e) {
            addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
        }finally {

            String[] toList = {"0",
                    shopName.name().concat(MainParsingPrices.shopCityCode.name()), // 1
                    PROP_CATEGORY1,         // 2
                    PROP_SUBCATEGORIES1,    // 3
                    firmName,               // 4
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()),  // 5
                    firmEmail,              // 6
                    firmPhone,              // 7
                    firmSite,               // 8
                    firmVkontakte,          // 9
                    firmFacebook,           // 10
                    givenLink};             // 11
            dataToBase.add(toList);
        }
    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

        String proxyString = getRandomProxy();

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

        //userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

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
                    driver_noGUI.getBrowserVersion().setUserAgent("Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                    break;

                case FENIXCOMP:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
                    driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    break;

                case AVITO:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    if (USE_GUI){
                        driver = new FirefoxDriver(profile);
                    }else{
                        driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
                        driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    }
                    break;

                case GIS:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    if (USE_GUI){
                        driver = new FirefoxDriver(profile);
                    }else{
                        driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
                        driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    }
                    break;

                default:
                    driver_noGUI = new HtmlUnitDriver();
                    break;
            }

            //driver.navigate().to(givenURL);

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

    private static String clearPhoneNumber(String givenPhoneNumber){

        givenPhoneNumber = new String(givenPhoneNumber.trim()
                .replace("-", "")
                .replace("=", "")
                .replace(" ", "")
                .replace(" ", ""));

        givenPhoneNumber = givenPhoneNumber.replace("+7", "8");

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

    private static String clearPrice(String givenPrice){
        givenPrice = new String(givenPrice.trim().replace("руб.", "").replace(" ", ""));
        return givenPrice;
    }

    private static String getRandomProxy(){

        String resultString;

        if (PROP_PROXY.equalsIgnoreCase("FOXTOOLS")){
            GetPost getHtmlData = new GetPost();
            try {
                resultString = getHtmlData.sendGet("http://api.foxtools.ru/v2/Proxy.txt?cp=UTF-8&lang=RU&type=HTTPS&anonymity=All&available=Yes&free=Yes&limit=100&uptime=2&country=ru");
            } catch (Exception e) {
                resultString = "";
//                e.printStackTrace();
            }

        }else resultString = "";

        if (!resultString.isEmpty()){
            String[] proxyList = resultString.split(";");
            Random r = new Random();
            resultString = proxyList[r.nextInt(proxyList.length - 2) + 1];
        }

        return resultString;
    }

