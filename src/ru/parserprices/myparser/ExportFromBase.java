package ru.parserprices.myparser;


//import org.w3c.dom.Document;

import org.w3c.dom.Document;

import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExportFromBase {

    ResultSet resultSet;


    public ExportFromBase(String[] givenArguments){

        ReadWriteBase writeDataToBase;
        Statement statement;

        ReadSiteDNS.addToResultString("Getting statement base start..", addTo.Console);
        try {
            writeDataToBase = new ReadWriteBase();
            statement = writeDataToBase.getStatement();
            ReadSiteDNS.addToResultString("Getting statement base finish.", addTo.Console);
        } catch (Exception e) {
            ReadSiteDNS.addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }

        String query_readRecords = "SELECT * FROM goods WHERE goods.shop LIKE '".concat(givenArguments[0]).concat("_").concat(givenArguments[1]).concat("' LIMIT 10;");

        resultSet = writeDataToBase.readData(statement, query_readRecords);

        // If result is empty.
        try {
            if (!resultSet.next()) return;
        } catch (SQLException e) {
            ReadSiteDNS.addToResultString(e.toString(), addTo.LogFileAndConsole);
            return;
        }///


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc;
        try {
            doc = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        Element goods = doc.createElement("Goods");
//        root.setAttribute("xmlns", "http://www.javacore.ru/schemas/");

        try {
            while (resultSet.next()){
                //ReadSiteDNS.addToResultString(resultSet.getString("good"), addTo.Console);
                Element record = doc.createElement("record");

                Element good = doc.createElement("good");
                good.setTextContent(resultSet.getString("good"));
                record.appendChild(good);

                Element item = doc.createElement("item");
                item.setTextContent(resultSet.getString("item"));
                record.appendChild(item);

//                record.setTextContent("Good").setTextContent(resultSet.getString("good"));
//                doc.createElement("Good").setTextContent(resultSet.getString("good"));
//                doc.createElement("Item").setTextContent(resultSet.getString("item"));
//                doc.createElement("Shop").setTextContent(resultSet.getString("good"));
//                doc.createElement("Price").setTextContent(String.valueOf(resultSet.getInt("price")));

                goods.appendChild(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        doc.appendChild(goods);


        File file = new File(new ReadWriteFile("myXML").getFullAddress());

        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
