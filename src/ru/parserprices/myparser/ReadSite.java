package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
//import org.apache.xpath.operations.String;
import org.apache.xpath.operations.Bool;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static int MAX_COUNT_PAGES = 1;
    private static int MAX_COUNT_ITEMS = 10;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 3;

    public static class Read_Avito{

        public Read_Avito(String givenURL){

            int countIteration = 0;

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
                    addToResultString(String.valueOf(listPages.indexOf(linkOfItem)).concat("/").concat(String.valueOf(listPages.size())), addTo.LogFileAndConsole);
                    readItemDiscription(dataToBase, linkOfItem);
                    if (MAX_COUNT_ITEMS != -1 && countIteration++ >= MAX_COUNT_ITEMS) break;
                }
            }

            if (dataToBase.size() != 0) {
                for (String[] item : dataToBase
                     ) {
//                    addToResultString(Arrays.toString(item), addTo.LogFileAndConsole);
                    addToResultString(item[0].concat(" - ").concat(item[8]), addTo.LogFileAndConsole);
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

                if (MAX_COUNT_PAGES != -1 && countIteration >= MAX_COUNT_PAGES) break;
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

    private static void readItemDiscription(ArrayList<String[]> dataToBase, String givenLink) {

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

        String cssSelector_Item = "div.item-sku > span#item_id";
        String cssSelector_ItemName = "h1.h1";
        String cssSelector_ItemPrice = "span.p_i_price > span";
        String cssSelector_ItemOwner = "div#seller > strong";
        String cssSelector_ItemCity = "div#map";
        String cssSelector_ItemParams = "div.item-params";
        String cssSelector_ItemDecription = "div.description.description-text";
//        String cssSelector_ItemPhoneButton = "span.button.button-azure.description__phone-btn.js-phone-show__link";
        String cssSelector_ItemPhoneButton = "span.button-azure-text.description__phone-insert.js-phone-show__insert";
        String cssSelector_ItemPhoneNumberImage = "img.description__phone-img";

        String goodCategory = PROP_CATEGORY1;
        String itemPhoneNumber = "";

                WebElement but_ShowPhoneNumber = driver.findElement(By.cssSelector(cssSelector_ItemPhoneButton));
        int countPages = 0;

        try {
            while ((new WebDriverWait(driver, WAITING_FOR_EXPAND)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssSelector_ItemPhoneNumberImage)))) {

                but_ShowPhoneNumber.sendKeys(Keys.ESCAPE);
                but_ShowPhoneNumber.click();

                if (MAX_COUNT_EXPAND != -1 && countPages++ >= MAX_COUNT_EXPAND) break;
            }
        }catch (Throwable te) {

            java.lang.String phoneNumberImageAdressEncode64 = "";
            java.lang.String phoneNumberImageAdressDecode64 = "";

            String imagePath = "";

            try {
                phoneNumberImageAdressEncode64 = driver.findElement(By.cssSelector(cssSelector_ItemPhoneNumberImage)).getAttribute("src");

                phoneNumberImageAdressEncode64 = new String(phoneNumberImageAdressEncode64.getBytes("Cp1251"), "UTF-8");
//                addToResultString(phoneNumberImageAdressEncode64, addTo.LogFileAndConsole);
                java.lang.String FileURI = phoneNumberImageAdressEncode64.split(",")[1];

                byte[] decodedValue = Base64.getDecoder().decode(FileURI);
//                phoneNumberImageAdressDecode64 = new String(decodedValue, StandardCharsets.UTF_8);

                File tmpFile = File.createTempFile("image_", ".png");
                try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                    fos.write(decodedValue);
                } catch (IOException ioe) {
//                    ioe.printStackTrace();
                }

                imagePath = tmpFile.getAbsolutePath();

                //  data:image/png;base64,
                //   iVBORw0KGgoAAAANSUhEUgAAAGYAAAAQCAYAAADkpAq+AAAACXBIWXMAAA7EAAAOxAGVKw4bAAADKElEQVRYhe3Yy2tdVRQG8N8KIVxCCEFqqVidKKlIBh0K/hMKIpJBB05EER9YHAniWBxEcCRSRKUWSqEISikOihMVpYOKTyqKSEmlSJqGKj62g7Ov7Jycs++5TszgLtjknP34vrW/tfda9yRSSma2/2zu/3ZgZt02C8w+tVlg9qullOQ6cxAncQ2/4C3cMh4vGxbxep57BRtYLMZfws/YxikczP2po/3VxTGJp+YvlnA28/+a5x2o8FT3Pi1esW69kXhPf6c+ZStvzJs4h9twOy7gRE88X8Fl3IEjeTNvQEQ8gy3clXG+xzv5EETZ8CBe7eGo8kzw9yl8i1vz+AU8XeGZtPdp8UTEnXiso79Xn11WRHEHC8X7CNs9J2ETy60TdSU/f6M4AVjGTgfGPC6VOFPy9PqLj7BWjK3gfIWnuvf/gDeHD7GmdWOG6lPemE+xHhELETGPh/FZ12nIxH8W76PsrJTSkZTSVYiIQ3gRZzowHsX7KaXrPRxVngn+ruKHYt0NjUh9Nmnv0+K9gPdSSl+0BwbrU0RuVXPFxrl/G3f3nIgTOK4R6oCmDvzRmjPOyddwtAPjIlYn5Ohenpq/+L0Da0/f0L1Pg4f7cK5431NjBunTmvh8FmExR/1UD+iypkBu4Ts8pCPtaU78Bi62+tfafdPy1PxtH5IBganufSieJtV+jsOTAlPTpx2YbSy1RNmaJF4h9OWKuDdbfcfx8hDsPp6av5raVNaMBWyOhSrbkL334bWxNEX8kZbPvYHp0yel3TXmekrpRvH+N34zzO7FxxARP0bE4WJsHldb8+/XFNRp7V8edX+/1qS+sa3kPmnvL8NJWL14HVjrOBkRadxg/Dc/D9FnV2BOR8STEbESESM8gXfbCzL4pYg4lovlPXhccyXhbTwbEUsRsYjnNLWhtDV82YU9BU/N3/M4FhGjiFjQFPMPKlST9j4Irx2oceCLAzBUn12pbITXNFd0Mz+Peq7fUXyCm/gKD7Su+Ybmm+MnzcfUXGv9juKDtHLNazy9/uJQFnNLk6bOKlJVB09179Pi1VLZEH1SSiJPntk+s9n/yvap/QOKThvshKiepwAAAABJRU5ErkJggg==data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGYAAAAQCAYAAADkpAq+AAAACXBIWXMAAA7EAAAOxAGVKw4bAAADKElEQVRYhe3Yy2tdVRQG8N8KIVxCCEFqqVidKKlIBh0K/hMKIpJBB05EER9YHAniWBxEcCRSRKUWSqEISikOihMVpYOKTyqKSEmlSJqGKj62g7Ov7Jycs++5TszgLtjknP34vrW/tfda9yRSSma2/2zu/3ZgZt02C8w+tVlg9qullOQ6cxAncQ2/4C3cMh4vGxbxep57BRtYLMZfws/YxikczP2po/3VxTGJp+YvlnA28/+a5x2o8FT3Pi1esW69kXhPf6c+ZStvzJs4h9twOy7gRE88X8Fl3IEjeTNvQEQ8gy3clXG+xzv5EETZ8CBe7eGo8kzw9yl8i1vz+AU8XeGZtPdp8UTEnXiso79Xn11WRHEHC8X7CNs9J2ETy60TdSU/f6M4AVjGTgfGPC6VOFPy9PqLj7BWjK3gfIWnuvf/gDeHD7GmdWOG6lPemE+xHhELETGPh/FZ12nIxH8W76PsrJTSkZTSVYiIQ3gRZzowHsX7KaXrPRxVngn+ruKHYt0NjUh9Nmnv0+K9gPdSSl+0BwbrU0RuVXPFxrl/G3f3nIgTOK4R6oCmDvzRmjPOyddwtAPjIlYn5Ohenpq/+L0Da0/f0L1Pg4f7cK5431NjBunTmvh8FmExR/1UD+iypkBu4Ts8pCPtaU78Bi62+tfafdPy1PxtH5IBganufSieJtV+jsOTAlPTpx2YbSy1RNmaJF4h9OWKuDdbfcfx8hDsPp6av5raVNaMBWyOhSrbkL334bWxNEX8kZbPvYHp0yel3TXmekrpRvH+N34zzO7FxxARP0bE4WJsHldb8+/XFNRp7V8edX+/1qS+sa3kPmnvL8NJWL14HVjrOBkRadxg/Dc/D9FnV2BOR8STEbESESM8gXfbCzL4pYg4lovlPXhccyXhbTwbEUsRsYjnNLWhtDV82YU9BU/N3/M4FhGjiFjQFPMPKlST9j4Irx2oceCLAzBUn12pbITXNFd0Mz+Peq7fUXyCm/gKD7Su+Ybmm+MnzcfUXGv9juKDtHLNazy9/uJQFnNLk6bOKlJVB09179Pi1VLZEH1SSiJPntk+s9n/yvap/QOKThvshKiepwAAAABJRU5ErkJggg==

//                byte[] decodedBytes = Base64.getUrlDecoder().decode(FileURI);
//                phoneNumberImageAdressDecode64 = new String(decodedBytes);

                addToResultString(phoneNumberImageAdressDecode64, addTo.LogFileAndConsole);

            }catch (Exception e){
                addToResultString(e.getMessage(), addTo.LogFileAndConsole);
            }

            if (!imagePath.isEmpty()) {
                // Anticaptcha.
                int conutIteration = 0;
                try {
                    AntiCaptcha antiCaptcha = new AntiCaptcha(imagePath); // "D:\\Temp\\avito_phonenumber.png"
//                while (!antiCaptcha.getCaptchaStatus() & conutIteration++ <= 10) {
//                    //Thread.sleep(3000);
//                }
                    if (antiCaptcha.getCaptchaStatus()) {
                        itemPhoneNumber = antiCaptcha.getCaptchaText();
                    } else addToResultString("Error read captcha.", addTo.LogFileAndConsole);
                } catch (Exception e) {
                    itemPhoneNumber = e.toString();
                    addToResultString(e.toString(), addTo.LogFileAndConsole);
                    /*Some error*/
                }

                itemPhoneNumber = clearPhoneNumber(itemPhoneNumber);
            }
        }

        try {

            String item = driver.findElement(By.cssSelector(cssSelector_Item)).getText();
            String itemName = driver.findElement(By.cssSelector(cssSelector_ItemName)).getText();
            String itemPrice = driver.findElement(By.cssSelector(cssSelector_ItemPrice)).getText();
            String itemOwner = driver.findElement(By.cssSelector(cssSelector_ItemOwner)).getText();
            String itemCity = driver.findElement(By.cssSelector(cssSelector_ItemCity)).getText();
            String itemParams = driver.findElement(By.cssSelector(cssSelector_ItemParams)).getText();
            String itemDescription = driver.findElement(By.cssSelector(cssSelector_ItemDecription)).getText();
            String[] toList = {
                    item,
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                    itemName,
                    itemPrice,
                    itemOwner,
                    itemCity,
                    itemParams,
                    itemDescription,
                    itemPhoneNumber};
            dataToBase.add(toList);

        } catch (Exception e) {
            //addToResultString("Element not found: ".concat(e.toString()), addTo.LogFileAndConsole);
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

    private static String clearPhoneNumber(String givenPhoneNumber){

        givenPhoneNumber = new String(givenPhoneNumber.trim()
                .replace("-", "")
                .replace("=", "")
                .replace(" ", "")
                .replace(" ", ""));

        givenPhoneNumber = givenPhoneNumber.replace("+7", "8");

        return givenPhoneNumber;
    }


}
