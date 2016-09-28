package ru.parserprices.myparser;

import com.google.common.io.Files;
import org.apache.poi.ss.usermodel.Row;
//import org.apache.xpath.operations.String;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import static java.lang.Integer.parseInt;
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

//        String query_readRecords = "SELECT * FROM goods WHERE goods.shop LIKE '".concat(shopName.name()).concat(shopCityCode.name()).concat("' LIMIT 100000;");
        String query_readRecords = "SELECT * FROM ".concat(shopName.name()).concat(" t WHERE t.shop LIKE '").concat(shopName.name()).concat(shopCityCode.name()).concat("' ORDER BY t.category, t.good, t.shop LIMIT 200000;");

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

        // Export file to archive.
        Boolean needZip = argumentExist(givenArguments, "-zip");
        if (needZip){
            String newFullAdress = new String(file.getAbsolutePath().replace(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")), ".zip"));
            file = zipFile(file, newFullAdress);
        }

        // Delete oldest export files.
        String argument_DeleteOldest = getArgumentValue(givenArguments, "-delete");
        try {
            if (!argument_DeleteOldest.isEmpty()){
                int periodForDelete = Integer.valueOf(String.valueOf(argument_DeleteOldest));
                deleteOldest(file.getParent(), "/var/www/parserpro.ru/public_html/Parser/",  periodForDelete);
            }
        }catch (Exception e){
            addToResultString("Wrong value of argument '-delete' :".concat(argument_DeleteOldest),  addTo.LogFileAndConsole);
            e.printStackTrace();
            addToResultString(e.toString(),  addTo.LogFileAndConsole);
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

                String goodValue = resultSet.getString("good");
                Element good = doc.createElement("good");
                good.setTextContent(goodValue!=null ? goodValue : " ");
                record.appendChild(good);

                String itemValue = resultSet.getString("item");
                Element item = doc.createElement("item");
                item.setTextContent(itemValue!=null ? itemValue : " ");
                record.appendChild(item);

                String shopValue = resultSet.getString("shop");
                Element shop = doc.createElement("shop");
                shop.setTextContent(shopValue!=null ? shopValue : " ");
                record.appendChild(shop);

                String priceValue = resultSet.getString("price");
                Element price = doc.createElement("price");
                price.setTextContent(priceValue!=null ? priceValue : " ");
                record.appendChild(price);

                String categoryValue = resultSet.getString("category");
                Element category = doc.createElement("category");
                category.setTextContent(categoryValue!=null ? categoryValue : " ");
                record.appendChild(category);

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
        row.createCell(0).setCellValue("Category");
        row.createCell(1).setCellValue("Good");
        row.createCell(2).setCellValue("Item");
        row.createCell(3).setCellValue("Price");

        try {
            while (resultSet.next()){
                row = sheet.createRow(++rowNum);
                row.createCell(0).setCellValue(resultSet.getString("category"));
                row.createCell(1).setCellValue(resultSet.getString("good"));
                row.createCell(2).setCellValue(resultSet.getString("item"));
                row.createCell(3).setCellValue(resultSet.getString("price"));
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

    private void deleteOldest(String exportCatalog1, String exportCatalog2, int periodForDelete){

        if (currentOS != OS.Linux) return;


        File сatalogForExportFiles;
        сatalogForExportFiles = new File(exportCatalog1);

        File webCatalogForExportFiles;
        webCatalogForExportFiles = new File(exportCatalog2);

        try {
            addToResultString("Delete old files from :".concat(exportCatalog1), addTo.LogFileAndConsole);
            for (File fileInCatalog: сatalogForExportFiles.listFiles()
                    ) {
                if (fileInCatalog==null) continue;
                Boolean fileIsOld = new Date(fileInCatalog.lastModified()).before(new Date(new Date().getTime() - periodForDelete * 1000 * 60 * 60 * 24));
                Boolean fileForCurrentShop = fileInCatalog.getName().contains(shopName.name().concat(shopCityCode.name()));
                if (fileIsOld & fileForCurrentShop){
                    if (fileInCatalog.delete()) addToResultString("Old export file is delete: ".concat(fileInCatalog.getName()), addTo.LogFileAndConsole);
                    else addToResultString("Can't delete old export file: ".concat(fileInCatalog.getName()), addTo.LogFileAndConsole);
                }
            }

            addToResultString("Delete old files from :".concat(exportCatalog2), addTo.LogFileAndConsole);
            for (File fileInCatalog: webCatalogForExportFiles.listFiles()
                    ) {
                if (fileInCatalog==null) continue;
                Boolean fileIsOld = new Date(fileInCatalog.lastModified()).before(new Date(new Date().getTime() - periodForDelete * 1000 * 60 * 60 * 24));
                Boolean fileForCurrentShop = fileInCatalog.getName().contains(shopName.name().concat(shopCityCode.name()));
                if (fileIsOld & fileForCurrentShop){
                    if (fileInCatalog.delete()) addToResultString("Old export file is delete: ".concat(fileInCatalog.getName()), addTo.LogFileAndConsole);
                    else addToResultString("Can't delete old export file: ".concat(fileInCatalog.getName()), addTo.LogFileAndConsole);
                }
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
            addToResultString(npe.toString(), addTo.LogFileAndConsole);
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

    private File zipFile(File inputFile, String outputFile){

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            ZipEntry zipEntry = new ZipEntry(inputFile.getName());
            zipOutputStream.putNextEntry(zipEntry);

            FileInputStream fileInputStream = new FileInputStream(inputFile);
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, bytesRead);
            }

            zipOutputStream.closeEntry();

            zipOutputStream.close();
            fileOutputStream.close();
            addToResultString("Archive is creating.", addTo.LogFileAndConsole);

            if (inputFile.delete()) {
                addToResultString("File-export(not archive) is deleted.", addTo.LogFileAndConsole);
            }

            //System.out.println("Regular file :" + inputFile.getCanonicalPath()+" is zipped to archive :"+zipFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(outputFile);
    }

}
