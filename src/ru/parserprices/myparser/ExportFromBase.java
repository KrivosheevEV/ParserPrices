package ru.parserprices.myparser;

import com.google.common.io.Files;
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

import static ru.parserprices.myparser.MainParsingPrices.addToResultString;
import static ru.parserprices.myparser.MainParsingPrices.currentOS;

class ExportFromBase {

    private ResultSet resultSet;

    public ExportFromBase(String[] givenArguments){

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

        String query_readRecords = "SELECT * FROM goods WHERE goods.shop LIKE '".concat(givenArguments[0]).concat("_").concat(givenArguments[1]).concat("' LIMIT 100000;");

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
            addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }///

        String dateToName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = givenArguments[0].concat("_").concat(givenArguments[1]).concat("_").concat(dateToName);
        String prefixDirectory = "export/";
        String notFullPath = prefixDirectory.concat(fileName).concat(".xml");

        ReadWriteFile newFile = new ReadWriteFile(notFullPath);
        File file = new File(newFile.getFullAddress());

        if (givenArguments[2].toUpperCase().equals("XML")) createXML(file);
        else if (givenArguments[2].toUpperCase().equals("XLS") || givenArguments[2].toUpperCase().equals("XLSX")) /*createXML(nameFileExport)*/;
        else addToResultString("Finish export: ".concat(new Date().toString()), addTo.LogFileAndConsole);

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

    private void copyToWebServer(File fileSource){

        if(!fileSource.exists()){
            addToResultString("File-source for copy to web not exists", addTo.LogFileAndConsole);
            return;
        }

        File fileDestination;

        if (currentOS == OS.Linux) {
            fileDestination = new File("/var/www/Parser/".concat(fileSource.getName()));

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
