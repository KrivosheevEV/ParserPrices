package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static ru.parserprices.myparser.MainParsingPrices.*;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 10/22/16.
 */
public class Read_GlobusTutBy {

    private static HtmlUnitDriver driver_noGUI, driver_noGUI2;
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

//            int countIteration = 0;

//            startingWebDriver("");

//            downloadPhotoFromSite();

            createExportFile();

//            driver_noGUI2 = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
//            driver_noGUI2.getBrowserVersion().setUserAgent(userAgent);
//            driver_noGUI2.manage().timeouts().implicitlyWait(7000, TimeUnit.MILLISECONDS);
//
////            if (driver_noGUI!=null) driver_noGUI.setJavascriptEnabled(false);
//
//            ArrayList<String> listPages = new ArrayList<String>();
//            ArrayList<String[]> dataToBase = new ArrayList<String[]>();
//
//            if (!PROP_SUBCATEGORIES1.equalsIgnoreCase("NO")) {
//                String[] listSubcategories = PROP_SUBCATEGORIES1.split("|");
//                for (String link : listSubcategories) {
//                    readAllItemLinks(listPages, link);
//                }
//            } else if (!PROP_CATEGORY1.equalsIgnoreCase("NO")) {
//                readAllItemLinks(listPages, givenURL.concat("/").concat(PROP_CATEGORY1));
//            } else readAllItemLinks(listPages, givenURL);
//
//            if (listPages.size() != 0) {
//                for (String linkOfItem : listPages) {
//
//                    if (listPages.indexOf(linkOfItem) + 1 < START_RECORDS_WITH) continue;
//                    countIteration++;
//
//                    String countToLog = String.valueOf(listPages.indexOf(linkOfItem) + 1).concat("/").concat(String.valueOf(listPages.size()));
//                    readItemDiscription(dataToBase, linkOfItem, countToLog);
//
//                    if (countIteration % BLOCK_RECORDS_TO_BASE == 0) {
//                        addToResultString("Writing data in base..", addTo.LogFileAndConsole);
//                        writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
//                        addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
//                    }
//
//                    if (FINISH_RECORDS_IN != -1 & countIteration > FINISH_RECORDS_IN) break;
////                    if (MAX_COUNT_ITEMS != -1 & countIteration >= MAX_COUNT_ITEMS) break;
//                }
//                if (countIteration % BLOCK_RECORDS_TO_BASE != 0) {
//                    addToResultString("Writing data in base..", addTo.LogFileAndConsole);
//                    writeDataIntoBase(dataToBase, countIteration - BLOCK_RECORDS_TO_BASE);
//                    addToResultString("Sum records (".concat(String.valueOf(dataToBase.size())).concat(") added into base."), addTo.LogFileAndConsole);
//                }
//            }
//
//            dataToBase = new ArrayList<String[]>();
//
//            if (USE_GUI) driver.close();
//            else driver_noGUI.close();
////            driver_noGUI.close();

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
            addToResultString(Calendar.getInstance().getTime().toString()+" Trying open page[".concat(countToLog).concat("]: ").concat(givenLink), addTo.LogFileAndConsole);

            if (USE_GUI) driver.navigate().to(givenLink);
            else {
//                driver_noGUI.setJavascriptEnabled(true);
                driver_noGUI.navigate().to(givenLink);
//                driver_noGUI.setJavascriptEnabled(false);
            }
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

        String cssSelector_pathElementsPrefix = "html body table tbody tr td table tbody tr td";
        String cssSelector_Items = "div[id^=\"GATag_WholeObj\"]";
        String cssSelector_Title = "font.hPage";
        String xpath_Object = "//html//body//table[1]//tbody//tr[2]//td//div[2]";
        String cssSelector_Place = "font.subhPage";
        String cssSelector_Coords = "a[href*=\"http://orda.of.by/.map\"]";
        String cssSelector_Description = "div.psPlacePage";

        String globusTitle = "";
        String globusObject = "";
        String globusPlace = "";
        String globusCoords = "";
        String globusDescription = "";
        ArrayList<String[]> globusImages = new ArrayList<String[]>();
        String firmQuery = PROP_DISCRIPTION;

        try {

            if (USE_GUI) {

            } else {
                try {
//                    List<WebElement> listElements;
                    List<WebElement> listElement = driver_noGUI.findElements(By.cssSelector(cssSelector_Items));
//                    System.out.println("Find elements: "+listElement.size());
                    for (WebElement aListElement : listElement) {
                        String cssDivSubstring = aListElement.getAttribute("id");
                        if (cssDivSubstring.isEmpty() || cssDivSubstring.contains("_index")) continue;
//                        System.out.println(Calendar.getInstance().getTime().toString()+"> "+cssDivSubstring);
                        String namePlace= new String(cssDivSubstring.substring(15));
                        String nameObject = new String(givenLink.substring(21, givenLink.lastIndexOf("/")));
                        cssDivSubstring = "div[id^=\"".concat(cssDivSubstring).concat("\"]");
                        try{globusObject = driver_noGUI.findElement(By.xpath(xpath_Object)).getText();
                            globusObject = globusObject.split("\n")[1];
                        }catch(Exception e){globusObject="";}
                        try{globusTitle = driver_noGUI.findElement(By.cssSelector(cssSelector_Title)).getText();}catch(Exception e){globusTitle="";}
                        try{globusPlace = aListElement.findElement(By.cssSelector(cssSelector_pathElementsPrefix.concat(" ").concat(cssDivSubstring).concat(" ").concat(cssSelector_Place))).getText();}catch(Exception e){globusPlace="";}
                        try{
                            if (((HtmlUnitWebElement) aListElement).findElementsByTagName("nobr").size() > 1){
                                globusCoords = aListElement.findElement(By.cssSelector(cssSelector_pathElementsPrefix.concat(" ").concat(cssDivSubstring).concat(" ").concat(cssSelector_Coords))).getText();
                            }else{globusCoords="";}
                        } catch (Exception e) {globusCoords="";}
                        try{
                            if (((HtmlUnitWebElement) aListElement).findElementsByTagName("br").size() > 5){
                                for (WebElement element:aListElement.findElements(By.cssSelector(cssSelector_Description))
                                        ) {
                                    globusDescription = globusDescription.concat(" ").concat(element.getText());
                                }
                            }else{globusDescription="";}
                        } catch (Exception e) {globusDescription="";}

                        try{
                            globusImages = getGlobusImages("http://orda.of.by/.add/gallery.php?".concat(nameObject).concat("/").concat(namePlace));
                        } catch (Exception e) {/**/}
//                        try{globusObject = driver_noGUI.findElement(By.cssSelector(cssSelector_Object)).getText();}catch(Exception e){/**/}

                        for (String[] elementOfArray:globusImages
                             ) {
                            String[] toList = {"0",
                                    clearLetters(globusTitle),           // 1
                                    clearLetters(globusObject),           // 2
                                    clearLetters(globusPlace),          // 3
                                    clearLetters(globusCoords),          // 4
                                    clearLetters(globusDescription),     // 5
                                    clearLetters(elementOfArray[0]),    // 6 link  image
                                    clearLetters(elementOfArray[1]),    // 7 creator image
                                    clearLetters(firmQuery),          // 8
                                    clearLetters(givenLink)};         // 9
                            if (!globusPlace.isEmpty()) dataToBase.add(toList);
//                            System.out.println(Calendar.getInstance().getTime().toString()+"=> "+globusPlace+";"+globusCoords);

                        }

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

    private static ArrayList<String[]> getGlobusImages(String givenLink){

        ArrayList<String[]> result = new ArrayList<String[]>();

        if (!givenLink.isEmpty()) driver_noGUI2.get(givenLink);

        for (WebElement el: driver_noGUI2.findElements(By.cssSelector("table[align=center]"))
             ) {
            String imageName = "";
            String imageCreator = "";
            try{imageName = el.findElement(By.cssSelector("img")).getAttribute("src");}catch(Exception e){/**/};
            try{imageCreator = el.findElement(By.cssSelector("td.imageAuthor")).getText();}catch(Exception e){/**/};
            if (!imageName.isEmpty() & !imageCreator.isEmpty())
                result.add(new String[]{imageName, imageCreator});
            imageName=""; imageCreator="";
        }

        return result;
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
                addToResultString("Trying start new FirefoxDriver", addTo.LogFileAndConsole);
                driver = new FirefoxDriver(profile);
                driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
            } else {
                if (driver!=null) driver.quit();
                addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                driver_noGUI.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
//                driver_noGUI.setJavascriptEnabled(true);
                if (!givenURL.isEmpty()) driver_noGUI.get(givenURL);

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

        java.sql.Date dateToQuery;

        String query_writeNewRecords_prefix = "INSERT INTO general.".concat(shopName.name().toLowerCase()).concat(" (date, title, object, place, coords, description, imageLink, imageCreator, query, link)").concat(" VALUES ");
        String query_writeNewRecords_suffix = " ON DUPLICATE KEY UPDATE date=VALUES(date), title=VALUES(title), object=VALUES(object), place=VALUES(place), coords=VALUES(coords), description=VALUES(description), imageCreator=VALUES(imageCreator), query=VALUES(query), link=VALUES(link);";
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
                    stringToBase[9] + "') ");

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

    public static String clearLetters(String givenString) {
        return new String(givenString.replace("'", "").replace("\"", "").replace("\\", "/").replace(",", ","));
    }

    private static void downloadPhotoFromSite(){

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

        String query_readData = "SELECT DISTINCT t.imageLink FROM globustutby as t;";
        ResultSet resultSet = writeDataToBase.readData(statement, query_readData);
        int countIteration=1;
        try {
            while (resultSet.next()) {
                URL imageLink = resultSet.getURL("imageLink");
                System.out.println(countIteration++ + " " + imageLink.getFile());
//                if (countIteration==10) break;
                try {
                    FileUtils.copyURLToFile(imageLink, new File("/home/vnc/Public/GlobusTutBy/images/".concat(imageLink.getFile())));

                }catch (Exception e) {continue;}

            }

        }catch (Exception e){return;}

    }

    private static void createExportFile(){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc;
        try {
            doc = factory.newDocumentBuilder().newDocument();
//            doc.setXmlStandalone(false);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

//        addToResultString("Creating XML!", addTo.LogFileAndConsole);
//        addToResultString("Create export file start..", addTo.LogFileAndConsole);

//        Element goods = doc.createElement("Goods");

        ReadWriteBase writeDataToBase1, writeDataToBase2;
        Statement statement1, statement2;

        addToResultString("Getting statement base start..", addTo.Console);
        try {
            writeDataToBase1 = new ReadWriteBase();
            statement1 = writeDataToBase1.getStatement();
            writeDataToBase2 = new ReadWriteBase();
            statement2 = writeDataToBase2.getStatement();
            addToResultString("Getting statement base finish.", addTo.Console);
        } catch (Exception e) {
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        String query_readData = "SELECT distinct g.title, g.object, g.place, g.coords, g.description FROM globustutby as g;";
        ResultSet resultSet = writeDataToBase1.readData(statement1, query_readData);
        int countIteration=0;
        try {

            Element rss = doc.createElement("rss");
            rss.setAttribute("version", "2.0");
            rss.setAttribute("xmlns:excerpt","http://wordpress.org/export/1.2/excerpt/");
            rss.setAttribute("xmlns:content","http://purl.org/rss/1.0/modules/content/");
            rss.setAttribute("xmlns:wfw","http://wellformedweb.org/CommentAPI/");
            rss.setAttribute("xmlns:dc","http://purl.org/dc/elements/1.1/");
            rss.setAttribute("xmlns:wp","http://wordpress.org/export/1.2/");
            Element channel = doc.createElement("channel");

            channel.appendChild(doc.createElement("title")).setTextContent("Way");
            channel.appendChild(doc.createElement("link")).setTextContent("http://way.by");
            channel.appendChild(doc.createElement("description")).setTextContent("Ещё один сайт на WordPress");
            channel.appendChild(doc.createElement("pubDate")).setTextContent("Wed, 02 Nov 2016 11:25:25 +0000");
            channel.appendChild(doc.createElement("language")).setTextContent("ru-RU");
            channel.appendChild(doc.createElement("wp:wxr_version")).setTextContent("1.2");
            channel.appendChild(doc.createElement("wp:base_site_url")).setTextContent("http://way.by/");
            channel.appendChild(doc.createElement("wp:base_blog_url")).setTextContent("http://way.by");
            Element autor = doc.createElement("wp:autor");
            autor.appendChild(doc.createElement("wp:author_id")).setTextContent("4");
            autor.appendChild(doc.createElement("wp:author_login")).appendChild(doc.createCDATASection("alexey"));
            autor.appendChild(doc.createElement("wp:author_email")).appendChild(doc.createCDATASection("alexey.sheshko@gmail.com"));
            autor.appendChild(doc.createElement("wp:author_display_name")).appendChild(doc.createCDATASection("alexey"));
            autor.appendChild(doc.createElement("wp:author_first_name")).appendChild(doc.createCDATASection("Алексей"));
            autor.appendChild(doc.createElement("wp:author_last_name")).appendChild(doc.createCDATASection("Шешко"));
            channel.appendChild(autor);

            channel.appendChild(doc.createElement("generator")).setTextContent("https://wordpress.org/?v=4.6.1");

            int countOfItems=0;
            while (resultSet.next()) {
//              String imageLink = resultSet.getURL("imageLink").getFile();
                countOfItems++;

                Element item = doc.createElement("item");

                String titleValue = resultSet.getString("title");
                String placeValue = resultSet.getString("place");
                Element title = doc.createElement("title");
                title.setTextContent(titleValue!=null?titleValue:"_noTitleValue_".concat("; ").concat(placeValue!=null?placeValue:"_noPlaceValue_"));
                item.appendChild(title);

                item.appendChild(doc.createElement("link")).setTextContent("http://way.by");
                item.appendChild(doc.createElement("pubDate")).setTextContent("Wed, 02 Nov 2016 11:25:25 +0000");
                item.appendChild(doc.createElement("dc:creator")).appendChild(doc.createCDATASection("alexey"));
                Element guid = doc.createElement("guid");
                guid.setAttribute("isPermaLink", "false");
                item.appendChild(guid).setTextContent("http://wb.magworks.ru/listing/".concat(String.valueOf(countOfItems)));
                item.appendChild(doc.createElement("description")). setTextContent(" ");

                String descriptionValue = resultSet.getString("description");
                Element contentEncoded = doc.createElement("content:encoded");
                contentEncoded.appendChild(doc.createCDATASection(descriptionValue!=null&!descriptionValue.isEmpty()?descriptionValue:" "));
                item.appendChild(contentEncoded);

                item.appendChild(doc.createElement("excerpt:encoded")).appendChild(doc.createCDATASection(" "));
                item.appendChild(doc.createElement("wp:post_id")).appendChild(doc.createCDATASection(String.valueOf(6000+countOfItems)));
                item.appendChild(doc.createElement("wp:post_date")).appendChild(doc.createCDATASection("2015-11-12 07:19:21"));
                item.appendChild(doc.createElement("wp:post_date_gmt")).appendChild(doc.createCDATASection("2015-11-12 07:19:21"));
                item.appendChild(doc.createElement("wp:comment_status")).appendChild(doc.createCDATASection("closed"));
                item.appendChild(doc.createElement("wp:ping_status")).appendChild(doc.createCDATASection("closed"));
                item.appendChild(doc.createElement("wp:post_name")).appendChild(doc.createCDATASection("item_".concat(String.valueOf(countOfItems))));
                item.appendChild(doc.createElement("wp:status")).appendChild(doc.createCDATASection("publish"));
                item.appendChild(doc.createElement("wp:post_parent")).setTextContent("0");
                item.appendChild(doc.createElement("wp:menu_order")).setTextContent("0");
                item.appendChild(doc.createElement("wp:post_type")).appendChild(doc.createCDATASection("lv_listing"));
                item.appendChild(doc.createElement("wp:post_password")).appendChild(doc.createCDATASection(" "));
                item.appendChild(doc.createElement("wp:is_sticky")).setTextContent("0");


                String objectValue = resultSet.getString("object");
                if (objectValue!=null){
                    String[] ar = objectValue.split(",");

                    Element categoryLocation1 = doc.createElement("category");
                    categoryLocation1.setAttribute("domain", "listing_location");
                    categoryLocation1.setAttribute("nicename", "location1_".concat(String.valueOf(countOfItems)));
                    categoryLocation1.appendChild(doc.createCDATASection(ar[1].trim()));
                    item.appendChild(categoryLocation1);

                    Element category1 = doc.createElement("category");
                    category1.setAttribute("domain", "listing_category");
                    category1.setAttribute("nicename", "dostoprimechatelnosti");
                    category1.appendChild(doc.createCDATASection("Достопримечательности"));
                    item.appendChild(category1);

                    Element category2 = doc.createElement("category");
                    category2.setAttribute("domain", "listing_category");
                    category2.setAttribute("nicename", "zamki-i-dvortsy");
                    category2.appendChild(doc.createCDATASection("Замки и дворцы"));
                    item.appendChild(category2);

                    Element categoryKeyword = doc.createElement("category");
                    categoryKeyword.setAttribute("domain", "listing_keyword");
                    categoryKeyword.setAttribute("nicename", "zamok");
                    categoryKeyword.appendChild(doc.createCDATASection("замок"));
                    item.appendChild(categoryKeyword);

                    Element categoryLocation2 = doc.createElement("category");
                    categoryLocation2.setAttribute("domain", "listing_location");
                    categoryLocation2.setAttribute("nicename", "location2_".concat(String.valueOf(countOfItems)));
                    categoryLocation2.appendChild(doc.createCDATASection(ar[0].trim()));
                    item.appendChild(categoryLocation2);

                    Element categoryAmnities = doc.createElement("category");
                    categoryAmnities.setAttribute("domain", "listing_amenities");
                    categoryAmnities.setAttribute("nicename", "amenities_".concat(String.valueOf(countOfItems)));
                    categoryAmnities.appendChild(doc.createCDATASection("Стоянка"));
                    item.appendChild(categoryAmnities);

                }

                Element wpPostMeta = doc.createElement("wp:postmeta");

                String coordsValue = resultSet.getString("coords");
                if (coordsValue!=null & coordsValue.contains(",")){

                    String[] coordsArray = coordsValue.split(",");

                    Element wpMetaKey1 = doc.createElement("wp:meta_key");
                    wpMetaKey1.setTextContent("lv_listing_lat");
                    wpPostMeta.appendChild(wpMetaKey1);
                    Element wpMetaValue1 = doc.createElement("wp:meta_value");
                    wpMetaValue1.setTextContent(coordsArray[0].trim());
                    wpPostMeta.appendChild(wpMetaValue1);

                    Element wpMetaKey2 = doc.createElement("wp:meta_key");
                    wpMetaKey2.setTextContent("lv_listing_lng");
                    wpPostMeta.appendChild(wpMetaKey2);
                    Element wpMetaValue2 = doc.createElement("wp:meta_value");
                    wpMetaValue2.setTextContent(coordsArray[1].trim());
                    wpPostMeta.appendChild(wpMetaValue2);
                }
                item.appendChild(wpPostMeta);

                String query_readImage = "SELECT distinct g.imageLink, g.imageCreator FROM globustutby as g WHERE place =\'"+placeValue+"\' AND title LIKE \'%"+titleValue+"%\' AND object LIKE \'%"+objectValue+"%\';";
                ResultSet resultSetImage = writeDataToBase2.readData(statement2, query_readImage);
                while (resultSetImage.next()){
                    String imageLinkValue = resultSetImage.getString("imageLink");
                    String imageCreatorValue = resultSetImage.getString("imageCreator");
                    if (imageLinkValue!=null){

                        Element wpMetaKey1 = doc.createElement("wp:meta_key");
                        wpMetaKey1.setTextContent("detail_images");
                        wpPostMeta.appendChild(wpMetaKey1);
                        Element wpMetaValue1 = doc.createElement("wp:meta_value");
                        wpMetaValue1.setTextContent("way.magworks.ru/upload".concat(new URL(imageLinkValue.trim()).getFile()));
                        wpPostMeta.appendChild(wpMetaValue1);

                        Element wpMetaKey2 = doc.createElement("wp:meta_key");
                        wpMetaKey2.setTextContent("image_creator");
                        wpPostMeta.appendChild(wpMetaKey2);
                        Element wpMetaValue2 = doc.createElement("wp:meta_value");
                        wpMetaValue2.setTextContent(imageCreatorValue!=null?imageCreatorValue:" ");
                        wpPostMeta.appendChild(wpMetaValue2);
                    }
                    item.appendChild(wpPostMeta);
                }

                channel.appendChild(item);
                System.out.println(countIteration++);

                if (countIteration>=100) break;

            }

            rss.appendChild(channel);
            doc.appendChild(rss);

            Transformer transformer = null;
            try {
                transformer = TransformerFactory.newInstance().newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            try {
                File f = new File("/home/vnc/Public/GlobusTutBy/way.wordpress.2016-12-05.xml");
                transformer.transform(new DOMSource(doc), new StreamResult(f));
                StringEscapeUtils.unescapeXml(f.toString());
//                for (String s:f.list()
//                     ) {
//                    StringEscapeUtils.unescapeXml(s);
//                }
                addToResultString("Create export file finish.", addTo.LogFileAndConsole);
            } catch (TransformerException e) {
                addToResultString("Error create export file.", addTo.LogFileAndConsole);
                addToResultString(e.toString(), addTo.LogFileAndConsole);
            }

        }catch (Exception e){e.printStackTrace();}

    }

}

