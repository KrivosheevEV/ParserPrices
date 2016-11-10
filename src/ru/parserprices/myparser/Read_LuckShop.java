package ru.parserprices.myparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.marketer.RuCaptcha;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class Read_LuckShop {

    private static ArrayList<String[]> dataToPrice;
    //1         2    3     4          5   6   7     8       9        10   11    12    13       14              15         16          17
    //category;good;price;adressPage;see;hit;brend;variant;priceOld;item;stock;title;keyWords;pageDescription;annotation;description;images
    private static int MAX_COUNT_PAGES = 212;
    private static int MAX_COUNT_ITEMS = -1;
    private static int MAX_COUNT_TOBASE = -1;
    private static int MAX_COUNT_EXPAND = 3;
    private static int WAITING_FOR_EXPAND = 5;
    private static int BLOCK_RECORDS_TO_BASE = 5;
    private static int START_RECORDS_WITH = PROP_START_RECORD_IN;
    private static int FINISH_RECORDS_IN = PROP_FINISH_RECORD_IN;

    public static class ReadLuckShop {

        public ReadLuckShop() {

            File dir = new File("/home/clLuckShop");
            if (!dir.isDirectory()) return;
            for (File file : dir.listFiles()){
                if (file.isDirectory()) continue;
                String fileName = file.getName();
                if (fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == 0) continue;
                String fileExtention = fileName.substring(fileName.lastIndexOf(".") + 1);
                switch (fileExtention){
                    case "xls": readXLSToArray(file); break;
                    case "xlsx": readXLSToArray(file); break;

                }
            }




        }
   }

    private static void readXLSToArray(File givenFile){

        InputStream in = null;
        HSSFWorkbook wb = null;
        try {
            in = new FileInputStream(givenFile);
            wb = new HSSFWorkbook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] accordCells = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        String[][] findParentPrice = {{"0", "3", "patskan-time.ru", "patskantime", "10"}};
        int startRow = 0;

        for (String[] parentPrice: findParentPrice
             ) {
            if (wb.getSheetAt(0).getRow(Integer.valueOf(parentPrice[0])).getCell(Integer.valueOf(parentPrice[1])).getStringCellValue().contains(parentPrice[2])){
                switch (parentPrice[3]){
                    case "patskantime":
                        accordCells[1] = 2;
                        accordCells[2] = 3;
                        accordCells[11] = 4;
                        //accordCells[17] = 1;
                        startRow = Integer.valueOf(parentPrice[4]);
                }
            }
        }

        int countOfRows = 0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            Iterator<Row> it = sheet.iterator();
            while (it.hasNext() & countOfRows++ >= startRow) {
                Row row = it.next();
                String[] lineToPrice = {""};
                for (int col : accordCells
                        ) {
                    Cell cell = row.getCell(col);
                    int cellType = cell.getCellType();
                    switch (cellType) {
                        case Cell.CELL_TYPE_STRING:
                            lineToPrice[col] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            lineToPrice[col] = String.valueOf(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            lineToPrice[col] = String.valueOf(cell.getNumericCellValue());
                            break;
                        default:
                            lineToPrice[col] = "";
                            break;
                    }
                }
                dataToPrice.add(lineToPrice);
            }
        }


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

}

