package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.openqa.selenium.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 10/22/16.
 */
public class Read_Shutterstock {

    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static ArrayList<String> listPages;
    private static ArrayList<String[]> dataToBase;
    private static int MAX_COUNT_PAGES = 50;
    private static int FIRST_PAGE = 200;
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = PROP_START_RECORD_IN;
    private static int FINISH_RECORDS_IN = PROP_FINISH_RECORD_IN;
    private static boolean USE_GUI = false;
    private static boolean NEED_PHONENUMBER = false;

    public static class ReadShutterstock {

        public ReadShutterstock(String givenURL) {

            int countIteration = 0;

            startingWebDriver(givenURL);

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
                        addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") found."), addTo.LogFileAndConsole);
                    }

                    if (FINISH_RECORDS_IN != -1 & countIteration > FINISH_RECORDS_IN) break;
//                    if (MAX_COUNT_ITEMS != -1 & countIteration >= MAX_COUNT_ITEMS) break;
                }
                if (countIteration % BLOCK_RECORDS_TO_BASE != 0) {
                    addToResultString("Writing data in base..", addTo.LogFileAndConsole);
                    writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
                    addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") found."), addTo.LogFileAndConsole);
                }
            }

            dataToBase = new ArrayList<String[]>();

            if (USE_GUI) driver.close();
            else driver_noGUI.close();
//            driver_noGUI.close();
            addToResultString("End working.", addTo.LogFileAndConsole);

        }


//            startingWebDriver(givenURL);
//            String link, number="";
//            FileInputStream file;
//            HSSFWorkbook workbook=null;
//            HSSFSheet sheet;
//            HSSFRow row, row_behind;
//            try {
//                file = new FileInputStream(new File("CORNKZ_all_sorted.xls"));
//                workbook = new HSSFWorkbook(file);
//                sheet = workbook.getSheetAt(0);
//
//                for (int i = 3915; i > 1 ; i-- //3930
//                        ) {
//                    row = sheet.getRow(i);
//                    row_behind = sheet.getRow(i-1);
//                    Cell cell =row.getCell(1);
//                    Cell cell_behind =row_behind.getCell(1);
//                        if (cell == null) cell = row.createCell(1);
////                        int type = cell.getCellType();
////                        int type_behind = cell_behind.getCellType();
////                        cell.setCellValue(number);
//                    /*if (type == cell.CELL_TYPE_STRING & type_behind == cell_behind.CELL_TYPE_STRING ){
//                        if (cell.getStringCellValue().equals(cell_behind.getStringCellValue())){
//                            cell.setCellValue("");
//                        }
//
//                    }else if (type == cell.CELL_TYPE_NUMERIC & type_behind == cell_behind.CELL_TYPE_NUMERIC){
//                        if (cell.getNumericCellValue() == cell_behind.getNumericCellValue()) cell.setCellValue(0);
//                    }*/
//                    System.out.println(i);
////                        workbook.write(new File("CORNKZ_all_3.xls"));
//
////                    link = row.getCell(2).getStringCellValue();
//
////                    driver_noGUI.navigate().to(link);
////                    try {
////                        number = driver_noGUI.findElement(By.cssSelector("table.total img")).getAttribute("src");
////                    } catch (Exception e) {
////                        number = "";
////                    }
//
//////                    File tmpFile;
//////                    String imagePath;
//////                    try {
//////                        tmpFile = File.createTempFile("image_", ".png");
//////                        BufferedImage image = null;
//////                        URL url = new URL(number);
//////                        image = ImageIO.read(url);
//////                        if (image != null) ImageIO.write(image, "png", tmpFile);
//////                        imagePath = tmpFile.getAbsolutePath();
//////                    } catch (Exception e) {
//////                        imagePath = "";
//////                    }
//////
//////                    int MAX_COUNT_REREADING_CAPTCHA = 3;
//////
//////                    if (NEED_PHONENUMBER & !imagePath.isEmpty()) {
//////                        int conutReReadingCaptcha = 0;
//////                        Boolean readCaptcha = true;
//////                        while (readCaptcha) {
//////                            try {
//////                                AntiCaptcha antiCaptcha = new AntiCaptcha(imagePath); // "D:\\Temp\\avito_phonenumber.png"
//////                                if (antiCaptcha.getCaptchaStatus()) {
//////                                    number = antiCaptcha.getCaptchaText();
//////                                    if (number.equals(RuCaptcha.Responses.ERROR_NO_SLOT_AVAILABLE.toString())) {
//////                                        if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA)
//////                                            readCaptcha = false;
//////                                    } else
//////                                        readCaptcha = false;
//////                                } else {
//////                                    addToResultString("Error read captcha.", addTo.LogFileAndConsole);
//////                                    readCaptcha = false;
//////                                }
//////                            } catch (Exception e) {
//////                                if (MAX_COUNT_REREADING_CAPTCHA != -1 & ++conutReReadingCaptcha > MAX_COUNT_REREADING_CAPTCHA)
//////                                    readCaptcha = false;
//////                            }
//////                        }
////////                        number = clearPhoneNumber(number);
//////                    }
////
////                    if (!number.isEmpty() & !number.contains("mid")) {
////                        System.out.println(i);
////                        Cell cell =row.getCell(1);
////                        if (cell == null) cell = row.createCell(1);
//////                        cell.setCellType(Cell.CELL_TYPE_STRING);
////                        cell.setCellValue(number);
////                        workbook.write(new File("CORNKZ_all_3.xls"));
////                    }
//
//                }
//            } catch (Exception e) {System.out.println(e.getMessage());
//            }finally {
//                try{workbook.write(new File("CORNKZ_all_4.xls"));}
//                catch (Exception e){
//                    System.out.println(e.getMessage());
//                }
//            }


//        }
   }


    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink) {

        String cssSelector_Items = "a.js_related-item.a";

        List<WebElement> listItems;
        Boolean readPage = true;

        try {
            int firstPage = FIRST_PAGE;
            int countIteration = 0;

            while (readPage) {

                if (MAX_COUNT_PAGES != -1 & countIteration++ >= MAX_COUNT_PAGES) break;

                String linkOfPage = givenLink.concat("?page=").concat(String.valueOf(firstPage++));

                try {
                    addToResultString("Trying open page: ".concat(linkOfPage), addTo.LogFileAndConsole);
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
                        listPages.add(href);
                    } catch (Exception e) {
                        addToResultString("Error reading href : ".concat(e.toString()), addTo.LogFileAndConsole);
                    }
                }

                listItems.clear();

            }

            addToResultString("All pages(".concat(Integer.toString(countIteration-1)).concat(") was reading"), addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString("Error reading flipping page.", addTo.LogFileAndConsole);
        }
    }

    // Reading description.
    private static void readItemDiscription(ArrayList<String[]> dataToBase, String givenLink, String countToLog) {

        try {
            addToResultString("Trying open page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);

            USE_GUI = true;

            if (USE_GUI) {
                if (driver == null) startingWebDriver(givenLink);
            } else {
                if (driver_noGUI == null) startingWebDriver(givenLink);
            }

            if (USE_GUI){
                try {
                    driver.navigate().to(givenLink);
                }catch (Exception e){
                    startingWebDriver(givenLink);
                }
            }
            else driver_noGUI.navigate().to(givenLink);

            try {
                WebElement we = driver.findElement(By.cssSelector("h3.inherit.inline a"));
                String autor = we.getText();
                String autorLink = we.getAttribute("href");
                driver_noGUI.get(autorLink);
                String textBio = driver_noGUI.findElement(By.cssSelector("div.bio")).getText();
                String email = findEmail(textBio);
                if (!email.isEmpty()) {
                    String[] toList = {"0",
                            "image",        // 1 type
                            "abstract",     // 2 category
                            autor,          // 3 autor
                            autorLink,      // 4 autorLink
                            email};         // 5 email
                    dataToBase.add(toList);
                }

            } catch (Exception e){
                addToResultString("Error reading page ".concat(givenLink), addTo.LogFileAndConsole);
//                addToResultString(e.toString(), addTo.LogFileAndConsole);
            }

        } catch (Exception e) {
            addToResultString("Can't open new page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);
//            addToResultString(e.toString(), addTo.LogFileAndConsole);
            if (USE_GUI) try {
                driver.quit();
            } catch (Exception e1) {/**/}
            else try {
                driver_noGUI.quit();
            } catch (Exception e1) {/**/}
//            return;
        }
    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

        try {

            if (USE_GUI) {

                addToResultString("Trying start new FirefoxDriver", addTo.LogFileAndConsole);

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

                driver = new FirefoxDriver(profile);
                driver.manage().window().setSize(new Dimension(500, 300));
                driver.manage().window().setPosition(new Point(100, 100));
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

            } else {
                addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);

//                userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.CHROME);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                driver_noGUI.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                driver_noGUI.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
                driver_noGUI.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
                java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
                java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
            }

        } catch (Exception e) {
            e.printStackTrace();
            addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
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

        String query_writeNewRecords_prefix;
        String query_writeNewRecords_suffix;
        String query_writeNewRecords;

        addToResultString("Start record into base.", addTo.LogFileAndConsole);

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            try {
                String query_writeCheckExist = "SELECT s.email FROM general.".concat(shopName.name().toLowerCase()).concat(" s WHERE email='").concat(stringToBase[5]).concat("';");
                if (writeDataToBase.dataExist(statement, query_writeCheckExist)) continue;
            } catch (Exception e) {
                continue;
            }

            try {
                dateToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateToQuery = new java.sql.Date(System.currentTimeMillis());
            }

            query_writeNewRecords_prefix = "INSERT INTO general.".concat(shopName.name().toLowerCase()).concat(" (date, type, category, autor, autorLink, email)").concat(" VALUES ");
            query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE id=VALUES(id), date=VALUES(date), type=VALUES(type), category=VALUES(category), autorLink=VALUES(autorLink), email=VALUES(email);";
            query_writeNewRecords = query_writeNewRecords_prefix;

            query_writeNewRecords = query_writeNewRecords.concat(" ('" +
                    dateToQuery + "', '" +
                    stringToBase[1] + "', '" +
                    stringToBase[2] + "', '" +
                    stringToBase[3] + "', '" +
                    stringToBase[4] + "', '" +
                    stringToBase[5] + "') ").concat(query_writeNewRecords_suffix);

            if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords)) countOfNewRecords++;

            if (MAX_COUNT_TOBASE != -1 && countOfRecords >= MAX_COUNT_TOBASE) break;
        }

//        if (countOfRecords % BLOCK_RECORDS_TO_BASE != 0) {
//            query_writeNewRecords = query_writeNewRecords.concat(query_writeNewRecords_suffix);
//            if (writeDataToBase.writeDataSuccessfully(statement, query_writeNewRecords))
//                countOfNewRecords = (countOfNewRecords * BLOCK_RECORDS_TO_BASE) + (countOfRecords - (countOfNewRecords * BLOCK_RECORDS_TO_BASE));
//        }

        addToResultString("Finish record into base.", addTo.LogFileAndConsole);


        addToResultString("Reading records: ".concat(String.valueOf(countOfRecords)).concat(" in base."), addTo.LogFileAndConsole);
        addToResultString("Added records:   ".concat(String.valueOf(countOfNewRecords)).concat(" in base."), addTo.LogFileAndConsole);
//        addToResultString("Updated records: ".concat(String.valueOf(countOfUpdate)).concat(" in base."), addTo.LogFileAndConsole);

        addToResultString("Close base connections", addTo.Console);
        writeDataToBase.closeBase();
        try {
            if (statement != null) statement.close();
        } catch (SQLException se) { /*can't do anything */ }
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

    private static String findEmail(String sourceText){

        int countEmails=sourceText.split("@").length-1;
        if (sourceText.isEmpty() || countEmails==0) return "";

        String prefixEmail = "", suffixEmail = "";
        int firstIndexAT = 0;

        firstIndexAT = sourceText.indexOf("@", firstIndexAT);

        for (int leftIndex = firstIndexAT; leftIndex > 0; leftIndex--) {
            char letter = sourceText.charAt(leftIndex - 1);
            int code = letter;
            if (code == 95 || code == 64 || code == 46 || (code >= 48 & code <= 57) || (code >= 65 & code <= 90) || (code >= 97 & code <= 122)) {
                prefixEmail = letter + prefixEmail;
            } else leftIndex = -1;
        }
        for (int rightIndex = firstIndexAT; rightIndex < sourceText.length()-1; rightIndex++) {
            char letter = sourceText.charAt(rightIndex + 1);
            int code = letter;
            if (code == 95 || code == 64 || code == 46 || (code >= 48 & code <= 57) || (code >= 65 & code <= 90) || (code >= 97 & code <= 122)) {
                suffixEmail = suffixEmail + letter;
            } else rightIndex = 10000;
        }

        while (prefixEmail.startsWith(".")) prefixEmail = new String(prefixEmail.substring(1));
        while (suffixEmail.endsWith(".")) suffixEmail = new String(suffixEmail.substring(0, suffixEmail.length() - 1));
        String email = !prefixEmail.isEmpty() & !suffixEmail.isEmpty() ? prefixEmail.concat("@").concat(suffixEmail).trim() : "";
        prefixEmail = "";
        suffixEmail = "";
        if (email.length() > 1 & email.contains("."))
            return email;
        else
            return "";

    }
}

