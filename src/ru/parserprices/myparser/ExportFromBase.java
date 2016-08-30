package ru.parserprices.myparser;

import com.google.common.io.Files;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import static ru.parserprices.myparser.MainParsingPrices.*;


class ExportFromBase {

    private ResultSet resultSet;

    public ExportFromBase(String[] givenArguments){

        String argument_Export = getArgumentValue(givenArguments, "-export").toUpperCase();
        if (argument_Export.isEmpty()){
            addToResultString("Not set value of argument '-export'.",  addTo.LogFileAndConsole);
            addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);
            return;
        }

        ReadWriteBase writeDataToBase;
        Statement statement;

        addToResultString("Start export: " + new Date().toString(), addTo.LogFileAndConsole);

        addToResultString("Getting statement base start..", addTo.LogFileAndConsole);
        try {
            writeDataToBase = new ReadWriteBase();
            statement = writeDataToBase.getStatement();
            addToResultString("Getting statement base finish.", addTo.LogFileAndConsole);
        } catch (Exception e) {
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        if (statement == null) {
            addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);
            return;
        }

        String query_readRecords = "SELECT * FROM goods WHERE goods.shop LIKE '".concat(shopName.name()).concat(shopCityCode.name()).concat("' LIMIT 100000;");

        addToResultString("Query start..", addTo.LogFileAndConsole);
        resultSet = writeDataToBase.readData(statement, query_readRecords);
        addToResultString("Query finish.", addTo.LogFileAndConsole);

        // If result is empty.
        try {
            if (!resultSet.next()) {
                addToResultString("Query result is empty.", addTo.LogFileAndConsole);
                return;
            }
        } catch (SQLException e) {
            addToResultString("Error while run query.", addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }///

        String dateToName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = shopName.name().concat(shopCityCode.name()).concat("_").concat(dateToName);
        String prefixDirectory = "export/";
        String notFullPath;
//        = prefixDirectory.concat(fileName); //.concat(".xml");

        if (argument_Export.equals("XML")) notFullPath = prefixDirectory.concat(fileName).concat(".xml");
        else if (argument_Export.equals("XLS") || argument_Export.equals("XLSX")) notFullPath = prefixDirectory.concat(fileName).concat(".xls");
        else {
            addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);
            return;
        }

        ReadWriteFile newFile = new ReadWriteFile(notFullPath);
        File file = new File(newFile.getFullAddress());

        if (argument_Export.equals("XML")) createXML(file);
        else if (argument_Export.equals("XLS") || argument_Export.equals("XLSX")) createXLS(file);
        else {
            addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);
            return;
        }

        copyToWebServer(file);

        addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);

    }

    private void createXML(File file){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc;
        try {
            doc = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        addToResultString("Creating XML!", addTo.LogFileAndConsole);
        addToResultString("Create export file start..", addTo.LogFileAndConsole);

        Element goods = doc.createElement("Goods");
//        root.setAttribute("xmlns", "http://www.javacore.ru/schemas/");

        try {
            while (resultSet.next()){
                //ReadSites.addToResultString(resultSet.getString("good"), addTo.Console);
                Element record = doc.createElement("record");

                Element good = doc.createElement("good");
                good.setTextContent(resultSet.getString("good"));
                record.appendChild(good);

                Element item = doc.createElement("item");
                item.setTextContent(resultSet.getString("item"));
                record.appendChild(item);

                Element shop = doc.createElement("shop");
                shop.setTextContent(resultSet.getString("shop"));
                record.appendChild(shop);

                Element price = doc.createElement("price");
                price.setTextContent(resultSet.getString("price"));
                record.appendChild(price);

                goods.appendChild(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        doc.appendChild(goods);

        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(file));
            addToResultString("Create export file finish.", addTo.LogFileAndConsole);
        } catch (TransformerException e) {
            addToResultString("Error create export file.", addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
        }

    }

    private void createXLS(File file){

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Просто лист");

        addToResultString("Creating XLS!", addTo.LogFileAndConsole);
        addToResultString("Create export file start..", addTo.LogFileAndConsole);

        int rowNum = 0;

        // Header of table.
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("Good");
        row.createCell(1).setCellValue("Item");
        row.createCell(2).setCellValue("Price");
//        row.createCell(3).setCellValue("Shop");

        try {
            while (resultSet.next()){
                row = sheet.createRow(++rowNum);
                row.createCell(0).setCellValue(resultSet.getString("good"));
                row.createCell(1).setCellValue(resultSet.getString("item"));
                row.createCell(2).setCellValue(resultSet.getString("price"));
//                row.createCell(3).setCellValue(resultSet.getString("shop"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            workbook.write(new FileOutputStream(file));
            addToResultString("Create export file finish.", addTo.LogFileAndConsole);
        } catch (IOException e) {
            addToResultString("Error create export file.", addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
        }
    }

    private void copyToWebServer(File fileSource){

        if(!fileSource.exists()){
            addToResultString("File-source for copy to web not exists", addTo.LogFileAndConsole);
            return;
        }

        File fileDestination;

        if (currentOS == OS.Linux) {
            fileDestination = new File("/var/www/parserpro.ru/public_html/Parser/".concat(fileSource.getName()));

        }else {
            fileDestination = new File("C:/Temp/".concat(fileSource.getName()));
        }

        if (fileDestination.exists()) fileDestination.delete();

//        InputStream is = null;
//        OutputStream os = null;
        try {
            Files.copy(fileSource, fileDestination);
//            is = new FileInputStream(fileSource);
//            os = new FileOutputStream(fileDestination);
////            byte[] buffer = 1024];
//            byte[] buffer = new byte[is.read()+1];
//            int readed;
//            while ((readed = is.read(buffer)) > 0) {
//                os.write(buffer, 0, readed);
//            }
            addToResultString("File-source copied to web", addTo.LogFileAndConsole);
        }catch (Exception e){
//            e.printStackTrace();
            addToResultString("Error copying file-source copied to web", addTo.LogFileAndConsole);
            addToResultString(e.toString(), addTo.LogFileAndConsole);
        } finally {
//            try {
//                is.close();
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

}
