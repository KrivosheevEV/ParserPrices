package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 10/22/16.
 */
public class Read_Cornkz {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_PAGES = 212;
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = PROP_START_RECORD_IN;
    private static int FINISH_RECORDS_IN = PROP_FINISH_RECORD_IN;
    private static boolean USE_GUI = false;
    private static boolean NEED_PHONENUMBER = true;

    public static class ReadCornkz {

        public ReadCornkz(String givenURL) {

            int countIteration = 0;

            /*startingWebDriver(givenURL);

            ArrayList<String> listPages = new ArrayList<String>();
            ArrayList<String[]> dataToBase = new ArrayList<String[]>();

            if (!PROP_SUBCATEGORIES1.toUpperCase().equals("NO")) {
                String[] listSubcategories = PROP_SUBCATEGORIES1.split("|");
                for (String link : listSubcategories) {
                    readAllItemLinks(listPages, link);
                }
            } else if (!PROP_CATEGORY1.toUpperCase().equals("NO")) {
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
                        writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                        addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
                    }

                    if (FINISH_RECORDS_IN != -1 & countIteration > FINISH_RECORDS_IN) break;
//                    if (MAX_COUNT_ITEMS != -1 & countIteration >= MAX_COUNT_ITEMS) break;
                }
                if (countIteration % BLOCK_RECORDS_TO_BASE != 0) {
                    addToResultString("Writing data in base..", addTo.LogFileAndConsole);
                    writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                    addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
                }
            }

            dataToBase = new ArrayList<String[]>();

            if (USE_GUI) driver.close();
            else driver_noGUI.close();
//            driver_noGUI.close();
        }*/
            startingWebDriver(givenURL);
            String link, number="";
            FileInputStream file;
            HSSFWorkbook workbook=null;
            HSSFSheet sheet;
            HSSFRow row;
            try {
                file = new FileInputStream(new File("CORNKZ_all_2.xls"));
                workbook = new HSSFWorkbook(file);
                sheet = workbook.getSheetAt(0);

                for (int i = 2512; i < 3930; i++ //3930
                        ) {
                    row = sheet.getRow(i);

                    link = row.getCell(2).getStringCellValue();

                    driver_noGUI.navigate().to(link);
                    try {
                        number = driver_noGUI.findElement(By.cssSelector("table.total img")).getAttribute("src");
                    } catch (Exception e) {
                        number = "";
                    }

                    File tmpFile;
                    String imagePath;
                    try {
                        tmpFile = File.createTempFile("image_", ".png");
                        BufferedImage image = null;
                        URL url = new URL(number);
                        image = ImageIO.read(url);
                        if (image != null) ImageIO.write(image, "png", tmpFile);
                        imagePath = tmpFile.getAbsolutePath();
                    } catch (Exception e) {
                        imagePath = "";
                    }

                    int MAX_COUNT_REREADING_CAPTCHA = 3;

                    if (NEED_PHONENUMBER & !imagePath.isEmpty()) {
                        int conutReReadingCaptcha = 0;
                        Boolean readCaptcha = true;
                        while (readCaptcha) {
                            try {
                                AntiCaptcha antiCaptcha = new AntiCaptcha(imagePath); // "D:\\Temp\\avito_phonenumber.png"
                                if (antiCaptcha.getCaptchaStatus()) {
                                    number = antiCaptcha.getCaptchaText();
                                    if (number.equals(RuCaptcha.Responses.ERROR_NO_SLOT_AVAILABLE.toString())) {
                                        if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA)
                                            readCaptcha = false;
                                    } else
                                        readCaptcha = false;
                                } else {
                                    addToResultString("Error read captcha.", addTo.LogFileAndConsole);
                                    readCaptcha = false;
                                }
                            } catch (Exception e) {
                                if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA)
                                    readCaptcha = false;
                            }
                        }
//                        number = clearPhoneNumber(number);
                    }

                    if (!number.isEmpty() & !number.contains("mid")) {
                        System.out.println(i);
                        Cell cell =row.getCell(3);
                        if (cell == null) cell = row.createCell(3);
//                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(number);
                        workbook.write(new File("CORNKZ_all_3.xls"));
                    }

                }
            } catch (Exception e) {System.out.println(e.getMessage());
            }finally {
                try{workbook.write(new File("CORNKZ_all_4.xls"));}
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }


        }
   }


    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink) {


        String cssSelector_Items = "table.total a";

        List<WebElement> listItems;
        Boolean readPage = true;

        try {
            int countIteration = 0;

            while (readPage) {

                if (MAX_COUNT_PAGES != -1 & countIteration++ >= MAX_COUNT_PAGES) break;

                String linkOfPage = givenLink.concat(String.valueOf(212 - countIteration));

                try {
//                    addToResultString("Trying open page: ".concat(linkOfPage), addTo.LogFileAndConsole);
                    if (USE_GUI) driver.navigate().to(linkOfPage);
                    else driver_noGUI.navigate().to(linkOfPage);
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
                        if (href.contains("mid")) listPages.add(href);
                    } catch (Exception e) {
                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                listItems.clear();

                try {
                    if (USE_GUI) {
                        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
                    } else {
//                        driver_noGUI.setJavascriptEnabled(true);
                        driver_noGUI.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
//                        driver_noGUI.setJavascriptEnabled(false);
                    }

                } catch (Exception e) {
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
            } else {
                if (driver_noGUI == null) startingWebDriver(givenLink);
            }

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

        String cssSelector_Phone = "table.total img";
        String cssSelector_Text = "table.total";

        String firmPhone = "";
        String firmName = "";
        String firmQuery = PROP_DISCRIPTION;

        try {

            if (USE_GUI) {
                try {
                    firmPhone = driver.findElement(By.cssSelector(cssSelector_Phone)).getAttribute("src");
                } catch (Exception e) {
                    firmPhone = "";
                }
                try {
                    firmName = driver.findElement(By.cssSelector(cssSelector_Text)).getText();
                } catch (Exception e) {
                    firmName = "";
                }
            } else {
                try {
                    firmPhone = driver_noGUI.findElement(By.cssSelector(cssSelector_Phone)).getAttribute("src");
                } catch (Exception e) {
                    firmPhone = "";
                }
                try {
                    firmName = driver_noGUI.findElement(By.cssSelector(cssSelector_Text)).getText();
                    firmName = new String(firmName.substring(firmName.lastIndexOf("контактное лицо:")+16)).trim();
                } catch (Exception e) {
                    firmName = "";
                }
            }
        } catch (Exception e) {
            addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
        } finally {

            File tmpFile;
            String imagePath;
            try {
                tmpFile = File.createTempFile("image_", ".png");
                BufferedImage image = null;
                URL url = new URL(firmPhone);
                image = ImageIO.read(url);
                if (image != null) {

                    ImageIO.write(image, "png", tmpFile);
                }
                imagePath = tmpFile.getAbsolutePath();
            } catch (Exception e) {imagePath="";}

            int MAX_COUNT_REREADING_CAPTCHA = 3;

            if (NEED_PHONENUMBER & !imagePath.isEmpty()) {
                int conutReReadingCaptcha = 0;
                Boolean readCaptcha = true;
                while (readCaptcha) {
                    try {
                        AntiCaptcha antiCaptcha = new AntiCaptcha(imagePath); // "D:\\Temp\\avito_phonenumber.png"
                        if (antiCaptcha.getCaptchaStatus()) {
                            firmPhone = antiCaptcha.getCaptchaText();
                            if (firmPhone.equals(RuCaptcha.Responses.ERROR_NO_SLOT_AVAILABLE.toString())) {
                                if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA) readCaptcha = false;
                            } else
                                readCaptcha = false;
                        } else {
                            addToResultString("Error read captcha.", addTo.LogFileAndConsole);
                            readCaptcha = false;
                        }
                    } catch (Exception e) {
                        if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA) readCaptcha = false;
                    }
                }
                firmPhone = clearPhoneNumber(firmPhone);
            }

            String[] toList = {"0",
                    firmName,           // 1
                    firmPhone,          // 2
                    givenLink,          // 3
                    firmQuery};         // 4
            if (!firmPhone.isEmpty()) dataToBase.add(toList);
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

            addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
            if (USE_GUI) {
                driver = new FirefoxDriver(profile);
            } else {
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
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
//        int MAX_RECORDS_FOR_INSERT = 50;

        java.sql.Date dateToQuery;

        String query_writeNewRecords_prefix = "INSERT INTO general.".concat(shopName.name()).concat(" (date, name, phone, link, query)").concat(" VALUES ");
        String query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE date=VALUES(date), name=VALUES(name), link=VALUES(link), query=VALUES(query);";
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
                    stringToBase[4] + "') ");

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

    private static String getRandomProxy() {

        String resultString;

        if (PROP_PROXY.equalsIgnoreCase("FOXTOOLS")) {
            GetPost getHtmlData = new GetPost();
            try {
                resultString = getHtmlData.sendGet("http://api.foxtools.ru/v2/Proxy.txt?cp=UTF-8&lang=RU&type=HTTPS&anonymity=All&available=Yes&free=Yes&limit=100&uptime=2&country=ru");
            } catch (Exception e) {
                resultString = "";
//                e.printStackTrace();
            }

        } else resultString = "";

        if (!resultString.isEmpty()) {
            String[] proxyList = resultString.split(";");
            Random r = new Random();
            resultString = proxyList[r.nextInt(proxyList.length - 2) + 1];
        }

        return resultString;
    }
}

