package ru.parserprices.myparser;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static File parentDirectory;
    private static String category;

    public static class ReadLuckShop {

        public ReadLuckShop() {

            dataToPrice = new ArrayList<String[]>();

            parentDirectory = new File("/home/clLuckShop/Prices");
            File fileResult = new File(parentDirectory.getAbsolutePath().concat("/ResultPrice.csv"));
            if (!parentDirectory.isDirectory()) return;
            for (File dir1 : parentDirectory.listFiles()){
                if (!dir1.isDirectory()) continue;
                for (File dir2 : dir1.listFiles()) {
                    if (!dir2.isDirectory()) continue;
                    for (File file : dir2.listFiles()) {
                        if (!file.isFile()) continue;
                        category = dir2.getName();
                        String fileName = file.getName();
                        if (fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == 0) continue;
                        String fileExtention = fileName.substring(fileName.lastIndexOf(".") + 1);
                        switch (fileExtention) {
                            case "xls":
                                readXLSToArray(file);
                                addDataToResultPrice(fileResult.getAbsolutePath());
                                break;
                            //case "xlsx": readXLSToArray(file); break;

                        }
                    }
                }
            }
        }
   }

    private static void readXLSToArray(File givenFile) {

        InputStream in = null;
        HSSFWorkbook wb = null;
        try {
            in = new FileInputStream(givenFile);
            wb = new HSSFWorkbook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] accordCells = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        //                              0row, 1col, 2uniqe text,     3name,         4row start, 5col image
        String[][] findParentPrice = {{"0", "3", "patskan-time.ru", "patskantime", "10", "1"}};
        int startRow = 0;
        int pictureColumn = 0;
        String[] imagesName;
        String companyAlias = "";

        for (String[] parentPrice : findParentPrice
                ) {
            if (wb.getSheetAt(0).getRow(Integer.valueOf(parentPrice[0])).getCell(Integer.valueOf(parentPrice[1])).getStringCellValue().contains(parentPrice[2])) {
                switch (parentPrice[3]) {
                    case "patskantime":
                        accordCells[1] = 2;
                        accordCells[2] = 3;
                        accordCells[11] = 4;
                        accordCells[17] = 1000;
                        startRow = Integer.valueOf(parentPrice[4]);
                        pictureColumn = Integer.valueOf(parentPrice[5]);
                        companyAlias = parentPrice[3];
                }
            }
        }

        int countOfRows = 0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            // Save images.
            HSSFSheet sheet_ = wb.getSheetAt(i);
            imagesName = new String[sheet_.getLastRowNum()+1];
            for (HSSFShape shape : sheet_.getDrawingPatriarch().getChildren()) {
                if (shape instanceof HSSFPicture) {
                    HSSFPicture picture = (HSSFPicture) shape;
                    HSSFClientAnchor anchor = (HSSFClientAnchor) picture.getAnchor();
                    if (anchor.getCol1() == pictureColumn) {
                        HSSFRow pictureRow = sheet_.getRow(anchor.getRow1());
                        if (pictureRow != null) {
                            int rowNum = pictureRow.getRowNum() + 1;
                            PictureData pic = picture.getPictureData();
                            String ext = pic.suggestFileExtension();
                            byte[] data = pic.getData();
                            String imageName = companyAlias.concat("_").concat(String.valueOf(rowNum)).concat(".").concat(ext);
                            try {
                                FileOutputStream out = new FileOutputStream(parentDirectory.getAbsolutePath().concat("/images/").concat(imageName));
                                out.write(data);
                                out.close();
                                imagesName[rowNum] = imageName;
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            }

            Sheet sheet = wb.getSheetAt(i);
            Iterator<Row> it = sheet.iterator();
            String[] lineToPrice;
            while (it.hasNext()) {
                Row row = it.next();
                if (countOfRows++ < startRow) continue;
                int countCols = 0;
                lineToPrice = new String[18];
                lineToPrice[0] = category;
                for (int col : accordCells
                        ) {
                    if (col == 0 & countCols != 0) {
                        lineToPrice[countCols] = "";
                        countCols++;
                        continue;
                    }else if (col == 1000){
                        lineToPrice[countCols] = imagesName[countOfRows];
                        countCols++;
                        continue;
                    }
                    Cell cell = row.getCell(col);
                    if(cell == null) continue;;
//                    int cellType = cell.getCellType();
//                    switch (cellType) {
//                        case Cell.CELL_TYPE_STRING:
//                            lineToPrice[countCols] = cell.getStringCellValue();
//                            break;
//                        case Cell.CELL_TYPE_NUMERIC:
//                            lineToPrice[countCols] = String.valueOf(cell.getNumericCellValue());
//                            break;
//                        case Cell.CELL_TYPE_FORMULA:
//                            lineToPrice[countCols] = String.valueOf(cell.getNumericCellValue());
//                            break;
//                        default:
//                            lineToPrice[countCols] = "";
//                            break;
//                    }
                    countCols++;
                }
                dataToPrice.add(lineToPrice);
            }
        }

    }

    private static void addDataToResultPrice(String givenResultPricePath){
        File resultFie = new File(givenResultPricePath);
        if (!resultFie.exists()) {
            try {
                if (!resultFie.createNewFile()) return;
            }catch (Exception e) {/**/}
        }else if (!resultFie.isFile()) return;

        for (String[] str: dataToPrice
             ) {
            try {
                FileWriter writer = new FileWriter(givenResultPricePath, true);
                BufferedWriter bufferWriter = new BufferedWriter(writer);
                bufferWriter.write(arrayToString(str, ";").concat("\n"));
                bufferWriter.close();
                dataToPrice = new ArrayList<String[]>();
            }catch (Exception e) {
                System.out.println("Error write result price. ".concat(e.getMessage()));
            }
        }

    }

    private static String arrayToString(String[] givenArray, String givenSeparator){

        String result = "";
        for (String element:givenArray
             ) {
            result = result.concat(element.concat(givenSeparator));
        }
        String result_c = "";
        try {
//            result_c = new String (result.getBytes("UTF-8"), "UTF-8"); //Cp1251 UTF-8
            Charset cset = Charset.forName("UTF8");
            ByteBuffer buf = cset.encode(result);
            byte[] b = buf.array();
            result_c = new String(b);
        }catch (Exception e){/**/}
        return result_c;
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

