package ru.parserprices.myparser;


import java.io.IOException;

public class MainParsingPrices {

    public static OS currentOS;

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        }else currentOS = OS.Linux;

        // Load MySQL class.
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ReadWriteFile mPatternOfSite = new ReadWriteFile("Pattern_DNS1.xml");

        ReadSiteDNS readSite_DNS = new ReadSiteDNS();
        readSite_DNS.ReadSite(mPatternOfSite.getFullAddress());

        ReadWriteFile mResultOfParsing = new ReadWriteFile("Result_DNS1.txt");
//
        mResultOfParsing.writeResultToFile(mResultOfParsing.getFullAddress(), readSite_DNS.getResultOfParsing());
//        mResultOfParsing.writeResultToFile("D:/Projects/ParserPrices/out/artifacts/ParserPrices_jar/1.txt", readSite_DNS.getResultOfParsing());

    }

    public enum OS{
        Windows, Linux
    }

}
