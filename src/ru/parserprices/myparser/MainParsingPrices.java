package ru.parserprices.myparser;


import java.io.IOException;

public class MainParsingPrices {

    static OS currentOS;
    static String fileResult1 = "Result1_DNS1.txt";
    static String fileResult2 = "Result2_DNS1.txt";

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        }else currentOS = OS.Linux;

//        System.out.println(currentOS);

//        // Load MySQL class.
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        ReadWriteFile mPatternOfSite = new ReadWriteFile("Pattern_DNS1.xml");

        ReadSiteDNS readSite_DNS = new ReadSiteDNS();
        readSite_DNS.ReadSite(mPatternOfSite.getFullAddress());

        ReadWriteFile mResultOfParsing = new ReadWriteFile(fileResult1);
        mResultOfParsing.writeResultToFile(mResultOfParsing.getFullAddress(), readSite_DNS.getResultOfParsing(), false);
//        mResultOfParsing.writeResultToFile("D:/Projects/ParserPrices/out/artifacts/ParserPrices_jar/1.txt", readSite_DNS.getResultOfParsing());

    }

    enum OS{
        Windows, Linux
    }

}
