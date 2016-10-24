package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
    private static int MAX_COUNT_PAGES = -1;
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    //    private static int MAX_COUNT_TOBASE = 10;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = 1;      // !!!!!
    private static boolean NEED_PHONE_NUMBER = true;
    private static int MAX_COUNT_REREADING_CAPTCHA = 3;
    private static boolean USE_GUI = true;

    public static class Read2Gis {

        public Read2Gis(String givenURL) {

            int countIteration = 0;

//            if (shopCity == shopCities.sanktpeterburg) givenURL = "https://2gis.ru/spb";
//            else if (shopCity == shopCities.nikolsk) givenURL = "https://www.avito.ru/penzenskaya_oblast_nikolsk";
//            else if (shopCity == shopCities.novokuybishevsk) givenURL = "https://www.avito.ru/novokuybyshevsk";
//            else if (shopCity == shopCities.ershov) givenURL = "https://www.avito.ru/ershov";
//            else if (shopCity == shopCities.samara) givenURL = "https://www.avito.ru/samara";

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

    private static void readAllItemLinks(ArrayList<String> listPages, String givenLink) {

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
            if (USE_GUI) try {
                driver.quit();
            } catch (Exception e1) {/**/}
            else try {
                driver_noGUI.quit();
            } catch (Exception e1) {/**/}
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
                    } else {
//                        while ((new WebDriverWait(driver_noGUI, WAITING_FOR_EXPAND)).until(
//                                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_NextPage)))) {

                        driver_noGUI.setJavascriptEnabled(true);
//                            driver_noGUI.wait(1000);

                        but_NextPage.sendKeys(Keys.ESCAPE);
                        but_NextPage.click();
                        driver_noGUI.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);

                        driver_noGUI.setJavascriptEnabled(false);
//                            driver_noGUI.wait(1000);
//                            if (MAX_COUNT_EXPAND != -1 & countPages++ >= MAX_COUNT_EXPAND) break;
//                        }
                    }

                    try {
                        WebElement nextPageDisabled;
                        if (USE_GUI) {
                            nextPageDisabled = driver.findElement(By.cssSelector(cssSelector_NextPageDisabled));
                            readPage = false;
                        } else {
                            nextPageDisabled = driver_noGUI.findElement(By.cssSelector(cssSelector_NextPageDisabled));
                            readPage = false;
                        }
                    } catch (Exception e) {
                        /**/
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

        String cssSelector_Name = "h1.cardHeader__headerNameText";
        String cssSelector_butOtherContact = "div.contact__other";
        String cssSelector_Phone = "a.contact__phonesItemLink"; //href
        String cssSelector_Site = "div.contact__link._type_website > a.link.contact__linkText"; // innertext or title
        String cssSelector_VKontakte = "div.contact__link._social._type_vkontakte > a.link.contact__linkText";//href
        String cssSelector_Odnoklassniki = "div.contact__link._social._type_odnoklassniki > a.link.contact__linkText";//href
        String cssSelector_Facebook = "div.contact__link._social._type_facebook > a.link.contact__linkText";//href
        String cssSelector_Instagramm = "div.contact__link._social._type_instagram > a.link.contact__linkText";//href
        String cssSelector_Email = "div.contact__link._type_email > a.link.contact__linkText";//href
        String cssSelector_Address = "span.card__addressPart > a.card__addressLink._undashed";//gettext()
        String cssSelector_Worktime = "div.schedule__dropdown td.schedule__td > time.schedule__tableTime";// multi
        String cssSelector_Rubrics = "div.cardRubrics__rubrics a.link.cardRubrics__rubricLink";
        String cssSelector_Description = "a.cardAds__text";// gettext()
        String cssSelector_butSeeWorktime = "svg.icon.schedule__toggle";// gettext()

        String firmName = "";
        String firmPhone = "";
        String firmSite = "";
        String firmVkontakte = "";
        String firmOdnoklassniki = "";
        String firmFacebook = "";
        String firmInstagram = "";
        String firmEmail = "";
        String firmAddress = "";
        String firmWorktime = "";
        String firmRubrics = "";
        String firmDescrption = "";
        String firmQuery = "MRT";

        try {

            try {
                WebElement butOtherContact = driver.findElement(By.cssSelector(cssSelector_butOtherContact));
                butOtherContact.sendKeys(Keys.ESCAPE);
                butOtherContact.click();
            } catch (Exception e) {/**/}

            if (USE_GUI) {
                try {
                    firmName = driver.findElement(By.cssSelector(cssSelector_Name)).getText();
                } catch (Exception e) {
                    firmName = "";
                }
                try {
                    for (WebElement element : driver.findElements(By.cssSelector(cssSelector_Phone)))
                        firmPhone = firmPhone.concat(element.getAttribute("href")).concat(" ");
                } catch (Exception e) {
                    firmPhone = "";
                }
                try {
                    firmSite = driver.findElement(By.cssSelector(cssSelector_Site)).getAttribute("title");
                } catch (Exception e) {
                    firmSite = "";
                }
                try {
                    firmVkontakte = driver.findElement(By.cssSelector(cssSelector_VKontakte)).getAttribute("href");
                } catch (Exception e) {
                    firmVkontakte = "";
                }
                try {
                    firmOdnoklassniki = driver.findElement(By.cssSelector(cssSelector_Odnoklassniki)).getAttribute("href");
                } catch (Exception e) {
                    firmOdnoklassniki = "";
                }
                try {
                    firmFacebook = driver.findElement(By.cssSelector(cssSelector_Facebook)).getAttribute("href");
                } catch (Exception e) {
                    firmFacebook = "";
                }
                try {
                    firmInstagram = driver.findElement(By.cssSelector(cssSelector_Instagramm)).getAttribute("href");
                } catch (Exception e) {
                    firmInstagram = "";
                }
                try {
                    firmEmail = driver.findElement(By.cssSelector(cssSelector_Email)).getAttribute("href");
                } catch (Exception e) {
                    firmEmail = "";
                }
                try {
                    firmAddress = driver.findElement(By.cssSelector(cssSelector_Address)).getText();
                } catch (Exception e) {
                    firmAddress = "";
                }
//                try {
                    //WebElement but_SeeWorkTime = driver.findElement(By.cssSelector(cssSelector_butSeeWorktime));
                    //but_SeeWorkTime.click();
//                    for (WebElement element : driver.findElements(By.cssSelector(cssSelector_Worktime)))
//                        firmWorktime = firmWorktime.concat(element.getText()).concat(firmWorktime.endsWith("- ") ? ", " : " - ");
//                } catch (Exception e) {
                    firmWorktime = "";
//                }
                try {
                    for (WebElement element : driver.findElements(By.cssSelector(cssSelector_Rubrics)))
                        firmRubrics = firmRubrics.concat(element.getText()).concat(", ");
                    if (firmRubrics.endsWith(", ")) firmRubrics = firmRubrics.substring(0, firmRubrics.length() - 2);
                } catch (Exception e) {
                    firmRubrics = "";
                }
                try {
                    firmDescrption = driver.findElement(By.cssSelector(cssSelector_Description)).getText();
                } catch (Exception e) {
                    firmDescrption = "";
                }
            } else {
                try {
                    firmName = driver_noGUI.findElement(By.cssSelector(cssSelector_Name)).getText();
                } catch (Exception e) {
                    firmName = "";
                }
                try {
                    for (WebElement element : driver_noGUI.findElements(By.cssSelector(cssSelector_Phone)))
                        firmPhone = firmPhone.concat(element.getAttribute("href")).concat(" ");
                } catch (Exception e) {
                    firmPhone = "";
                }
                try {
                    firmSite = driver_noGUI.findElement(By.cssSelector(cssSelector_Site)).getAttribute("title");
                } catch (Exception e) {
                    firmSite = "";
                }
                try {
                    firmVkontakte = driver_noGUI.findElement(By.cssSelector(cssSelector_VKontakte)).getAttribute("href");
                } catch (Exception e) {
                    firmVkontakte = "";
                }
                try {
                    firmOdnoklassniki = driver_noGUI.findElement(By.cssSelector(cssSelector_Odnoklassniki)).getAttribute("href");
                } catch (Exception e) {
                    firmOdnoklassniki = "";
                }
                try {
                    firmFacebook = driver_noGUI.findElement(By.cssSelector(cssSelector_Facebook)).getAttribute("href");
                } catch (Exception e) {
                    firmFacebook = "";
                }
                try {
                    firmInstagram = driver_noGUI.findElement(By.cssSelector(cssSelector_Instagramm)).getAttribute("href");
                } catch (Exception e) {
                    firmInstagram = "";
                }
                try {
                    firmEmail = driver_noGUI.findElement(By.cssSelector(cssSelector_Email)).getAttribute("href");
                } catch (Exception e) {
                    firmEmail = "";
                }
                try {
                    firmAddress = driver_noGUI.findElement(By.cssSelector(cssSelector_Address)).getText();
                } catch (Exception e) {
                    firmAddress = "";
                }
                try {
                    for (WebElement element : driver_noGUI.findElements(By.cssSelector(cssSelector_Worktime)))
                        firmWorktime = firmPhone.concat(element.getText()).concat(firmWorktime.endsWith("- ") ? ", " : " - ");
                } catch (Exception e) {
                    firmWorktime = "";
                }
                try {
                    for (WebElement element : driver_noGUI.findElements(By.cssSelector(cssSelector_Rubrics)))
                        firmRubrics = firmPhone.concat(element.getText()).concat(", ");
                    if (firmRubrics.endsWith(", ")) firmRubrics = firmRubrics.substring(0, firmRubrics.length() - 2);
                } catch (Exception e) {
                    firmRubrics = "";
                }
                try {
                    firmDescrption = driver_noGUI.findElement(By.cssSelector(cssSelector_Description)).getText();
                } catch (Exception e) {
                    firmDescrption = "";
                }
            }
        } catch (Exception e) {
            addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
        } finally {

            String[] toList = {"0",
                    shopName.name().concat(MainParsingPrices.shopCityCode.name()), // 1
                    PROP_CATEGORY1,                                         // 2
                    PROP_SUBCATEGORIES1,                                    // 3
                    new String(firmName.replace(",", ";")),                                               // 4
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()),  // 5
                    new String(firmEmail.replace("mailto:","")),            // 6
                    new String(firmPhone.replace("tel:", "")),              // 7
                    firmSite,               // 8
                    firmVkontakte,          // 9
                    firmOdnoklassniki,      // 10
                    firmFacebook,           // 11
                    firmInstagram,         // 12
                    givenLink.contains("?queryState") ? givenLink.substring(0, givenLink.indexOf("?queryState")) : givenLink, // 13
                    new String(firmAddress.replace(",", "-")),  // 14
                    firmWorktime,           // 15
                    new String(firmRubrics.replace(",", ";")),            // 16
                    firmDescrption,         // 17
                    firmQuery};             // 18
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
            switch (shopName) {

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
                    if (USE_GUI) {
                        driver = new FirefoxDriver(profile);
                    } else {
                        driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
                        driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                    }
                    break;

                case GIS:

                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                    if (USE_GUI) {
                        driver = new FirefoxDriver(profile);
                    } else {
                        driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
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
//        int MAX_RECORDS_FOR_INSERT = 50;

        java.sql.Date dateToQuery;

        String query_writeNewRecords_prefix = "INSERT INTO general.".concat("2").concat(shopName.name()).concat(" (city, name, date, email, phone, site, vkontakte, odnoklassniki, facebook, instagram, link, address, worktime, rubrics, description, query)").concat(" VALUES ");
        String query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE city=VALUES(city), email=VALUES(email), phone=VALUES(phone), site=VALUES(site), vkontakte=VALUES(vkontakte), odnoklassniki=VALUES(odnoklassniki), facebook=VALUES(facebook), instagram=VALUES(instagram), link=VALUES(link), worktime=VALUES(worktime), rubrics=VALUES(rubrics), description=VALUES(description), query=VALUES(query);";
        String query_writeNewRecords = query_writeNewRecords_prefix;


        addToResultString("Start record into base.", addTo.LogFileAndConsole);

        for (String[] stringToBase : listDataToBase) {

            countOfRecords++;

            try {
                dateToQuery = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(stringToBase[5]).getTime());
            } catch (Exception e) {
                dateToQuery = new java.sql.Date(System.currentTimeMillis());
            }

            query_writeNewRecords = query_writeNewRecords.concat(" ('" + stringToBase[1] + "', '" +
                    writeDataToBase.clearLetters(stringToBase[4]) + "', '" +
                    dateToQuery + "', '" +
                    stringToBase[6] + "', '" +
                    stringToBase[7] + "', '" +
                    stringToBase[8] + "', '" +
                    stringToBase[9] + "', '" +
                    stringToBase[10] + "', '" +
                    stringToBase[11] + "', '" +
                    stringToBase[12] + "', '" +
                    stringToBase[13] + "', '" +
                    stringToBase[14] + "', '" +
                    stringToBase[15] + "', '" +
                    stringToBase[16] + "', '" +
                    stringToBase[17] + "', '" +
                    stringToBase[18] + "') ");

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

