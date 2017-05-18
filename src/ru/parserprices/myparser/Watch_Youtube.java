package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.remote.DesiredCapabilities;
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
    private static int MAX_COUNT_WIEVER = 1000;
    private static int MAX_COUNT_ITEMS = -1;
//    private static int MAX_COUNT_TOBASE = 10;
    private static int MAX_COUNT_EXPAND = 5;
    private static int WAITING_FOR_EXPAND = 10;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = 1;      // !!!!!
    private static boolean NEED_PHONE_NUMBER = true;
    private static int MAX_COUNT_REREADING_CAPTCHA = 3;
    private static boolean USE_GUI = true;

    private static WebDriver driver_GUI;
    private static usableWebDriver currentWebDriver;
    private static ArrayList<String> listProxy;
    private static int countForListProxy = 0;

    public static class WatchMyYoutube{

        public WatchMyYoutube(){

            currentWebDriver = usableWebDriver.Chromium;
//            USE_PROXY = false; //!!!

            String givenURL1 = "https://www.youtube.com/watch?v=yKFyoJqHoaI";
            String givenURL2 = "https://www.youtube.com/watch?v=N7pPf9kCJU8";
            String givenURL3 = "https://www.youtube.com/watch?v=Y39KgGQTNM4";
//            String givenURL = "https://www.iplocation.net";
//            String queryForSearchVideo = "A terrible accident, 2013 June (part 1).";
            String queryForSearchVideo = "Страшные аварии, 2013 июнь (часть 1).";
            listProxy = new ArrayList<String>();
            int delay = 0;
            Random r = new Random();

            for (int i = 0; i < MAX_COUNT_WIEVER; i++) {
                long startWatch;
                try {
                    startingWebDriver();
                    if (!findAndPlayVideo(queryForSearchVideo, givenURL1)) continue;
//                    driver_GUI.navigate().to(givenURL);
//                    delay = 300000;
//                    while (delay < 30000) delay = r.nextInt(60000);
                    try {
                        startWatch = System.currentTimeMillis();
                        int maxIteration = 900;

                        while (driver_GUI.getCurrentUrl().equals(givenURL1)
                                |driver_GUI.getCurrentUrl().equals(givenURL2)
                                |driver_GUI.getCurrentUrl().equals(givenURL3)){
                            Thread.sleep(1000);
                            if (--maxIteration<=0) break;
                        }
//                        TimeUnit.MILLISECONDS.sleep(delay);
                        delay = (int)(System.currentTimeMillis() - startWatch);

                    } catch (Exception e){
//                        addToResultString(e.getMessage(), addTo.LogFileAndConsole);
                    }
                } catch (Exception e){
                    addToResultString("Error watching video.", addTo.LogFileAndConsole);
                } finally {
                    addToResultString("Iteration:".concat(String.valueOf(i+1)).concat(", WhatchTime:").concat(String.valueOf(delay/1000)).concat(" sec."), addTo.LogFileAndConsole);
                    closeDriver(driver_GUI);
                }
            }
        }
    }

    // Start new WebDriver.
    private static void startingWebDriver() {

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

        String[] proxy = {"127.0.0.1:0"};
        String proxyIp = "127.0.0.1";
        String proxyPort = "0";
        if (USE_PROXY) {
//            String proxyString = "";
            if (countForListProxy >= listProxy.size()) fillProxyListFromBase(1);
            for (String proxyString : listProxy.get(countForListProxy++).split(",")
                    ) {
                if (!proxyString.isEmpty() | proxyString.split(":").length > 2
                        ) {
                    proxyIp = proxyString.split(":")[0];
                    proxyPort = proxyString.split(":")[1];
                    profile.setPreference("network.proxy.type", 1);
                    profile.setPreference("network.proxy.http", proxyIp);
                    profile.setPreference("network.proxy.http_port", Integer.valueOf(proxyPort));
                    profile.setPreference("network.proxy.ssl", proxyIp);
                    profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxyPort));
                }
            }
        }

        try {
            Random r = new Random(1000);
            int dimW = r.nextInt(1000);
            int dimH = r.nextInt(1000);
            while (dimH<200) dimH = r.nextInt(500);
            while (dimW<500) dimW = r.nextInt(1000);

            if (currentWebDriver == usableWebDriver.FireFox){
                addToResultString("Trying start new WebDriver(Firefox) - ".concat(proxyIp).concat(":").concat(proxyPort), addTo.LogFileAndConsole);
                driver_GUI = new FirefoxDriver(profile);
                driver_GUI.manage().window().setSize(new Dimension(dimH, dimW));
                driver_GUI.manage().window().setPosition(new Point(100, 100));
                driver_GUI.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                driver_GUI.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
                driver_GUI.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
            }else {
                addToResultString("Trying start new WebDriver(Chromium) - ".concat(proxyIp).concat(":").concat(proxyPort), addTo.LogFileAndConsole);

                System.setProperty("webdriver.chrome.driver", "/usr/develop/parserpro/chromedriver");
                System.setProperty("webdriver.chrome.silentOutput", "true");

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--window-size=".concat(String.valueOf(dimH)).concat(",").concat(String.valueOf(dimW)));
                options.addArguments("--window-position=100,100");
//                options.addArguments("--proxy-server=http://".concat(proxy[0]).concat(":").concat(proxy[1]));
                options.addArguments("--metrics-recording-only");
                options.addArguments("--ash-hide-notifications-for-factory");
//                options.addArguments("--ignore-certificate-errors");
                options.addArguments("--test-type");
//                options.addArguments("--ash-host-window-dounds=100+200-1024x768");

                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);

                if (USE_PROXY){
                    Proxy proxySettings = new Proxy();
                    proxySettings.setHttpProxy(proxyIp.concat(":").concat(proxyPort));
                    proxySettings.setSslProxy(proxyIp.concat(":").concat(proxyPort));
                    capabilities.setCapability("proxy", proxySettings);
                }

                driver_GUI = new ChromeDriver(capabilities);
                driver_GUI.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                driver_GUI.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
                driver_GUI.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
//                driver_GUI.manage().window().setSize(new Dimension(dimH, dimW));
//                driver_GUI.manage().window().setPosition(new Point(100, 100));

            }
//

            if (!proxyIsAvailable(driver_GUI)) {
                closeDriver(driver_GUI);
                startingWebDriver();
            }

//            setCookie(shopName);
        } catch( TimeoutException te){
            addToResultString("TimeOut exception start FirefoxWebDriver.", addTo.LogFileAndConsole);
            closeDriver(driver_GUI);
            startingWebDriver();
        } catch (Exception e) {
            addToResultString("Error start WebDriver.", addTo.LogFileAndConsole);
//            e.printStackTrace();
//            addToResultString(e.toString(), addTo.LogFileAndConsole);
            closeDriver(driver_GUI);
            startingWebDriver();
//            return;
        }finally{

        }
    }

    private static void fillProxyListFromBase(int countOfProxy){

        String resultString = "";
        listProxy = new ArrayList<String>();

        countForListProxy = 0;

        ReadWriteBase writeDataToBase;
        Statement statement;

        writeDataToBase = new ReadWriteBase();
        statement = writeDataToBase.getStatement();

        for (int counterDays = 0; counterDays < 10; counterDays++) {

            String dateOfProxyToQuery = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime() - (1000*60*60*24*counterDays));

            String queryText = "SELECT * FROM general.proxylist t WHERE t.dateofproxy >= '".concat(dateOfProxyToQuery).concat("' ORDER BY t.id DESC LIMIT 200;");

            ResultSet resultSet = writeDataToBase.readData(statement, queryText);

            try {
                while (resultSet.next()){
                    listProxy.add(resultSet.getString("address"));
                }
            } catch (SQLException e) {
                addToResultString("Error add proxy into proxylist.", addTo.LogFileAndConsole);
//                e.printStackTrace();
            }

            if (listProxy.size() > 0) {
                counterDays = 10;
                randomizeArrayList(listProxy);
            }
        }

    }

    private static boolean proxyIsAvailable(WebDriver driver) {

        String urlForTest = "http://www.youtube.com/";
//        String urlForTest = "https://www.iplocation.net";
//        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
//        driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
        try {
            if (!urlForTest.isEmpty()) driver.get(urlForTest);
        } catch(TimeoutException te){
            //addToResultString("!! Timeout 30 sec.", addTo.LogFileAndConsole);
            closeDriver(driver_GUI);
            return false;
        } catch(Exception e) {
            addToResultString("!! Error check proxy.", addTo.LogFileAndConsole);
//            e.printStackTrace();
            closeDriver(driver_GUI);
            return false;
        }

        return driver.getTitle().contains("YouTube") & (!driver.getTitle().contains("error") | !driver.getTitle().contains("not"));

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

    private static Boolean findAndPlayVideo(String givenQuery, String givenURLVideo){

        Boolean videoFounded = false;
        try {
            driver_GUI.get("https://www.youtube.com");
            WebElement weSearch = driver_GUI.findElement(By.cssSelector("input#masthead-search-term"));
            for (int j = 0; j < givenQuery.length(); j++) {
                weSearch.sendKeys(String.valueOf(givenQuery.charAt(j)));
            }
            weSearch.sendKeys(Keys.ENTER);
            Thread.sleep(new Random().nextInt(6000));

        } catch (Exception e){
            return videoFounded;
        }

        try {
            List<WebElement> listHrefs;
            for (int i = 0; i < 100; i++) {
                listHrefs = driver_GUI.findElements(By.cssSelector("div.yt-lockup-content a"));
                for (WebElement el: listHrefs
                     ) {
                    try {
                        ((JavascriptExecutor) driver_GUI).executeScript("arguments[0].scrollIntoView();", el);
                        Thread.sleep(new Random().nextInt(3000));
                        if (el.getAttribute("href").equals(givenURLVideo)){
                            addToResultString("Page: ".concat(String.valueOf(i)), addTo.LogFileAndConsole);
                            el.sendKeys(Keys.ENTER);
                            Thread.sleep(new Random().nextInt(10000));
                            videoFounded = true;
                            break;
                        }
                    } catch (Exception e){
                        videoFounded = false;
                    }
                }

                if (!videoFounded){
                    List<WebElement>  butsNext = driver_GUI.findElements(By.cssSelector("div.branded-page-box.search-pager.spf-link a"));
                    for (WebElement we: butsNext
                         ) {
                        try {
                            if (we.getText().contains("Next")) {
                                we.click();
                                Thread.sleep(new Random().nextInt(6000));
                            }
                        } catch (Exception e) {
                            videoFounded = false;
                        }

                    }
                }else {break;}
            }
            return videoFounded;

        } catch (Exception e) {
            return videoFounded;
        }

    }

    // Close webdriver.
    private static void closeDriver(WebDriver givenDriver){

        try {
            givenDriver.quit();
            ((JavascriptExecutor) givenDriver).executeScript("window.stop();");
        } catch (Exception e) {
            //Main.addToResultString("Error runnig closing script.");
        } finally {
            givenDriver = null;
        }

    }


    private static void randomizeArrayList(ArrayList givenArrayList){

        Collections.shuffle(givenArrayList, new Random(System.nanoTime()));
    }
}
