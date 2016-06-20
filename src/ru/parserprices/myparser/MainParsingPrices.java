package ru.parserprices.myparser;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainParsingPrices {

    static OS currentOS;
    public static String cityShop;
    static String fileResult1;
    static String fileResult2;

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        }else currentOS = OS.Linux;

        String dateToName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

//        fileResult1 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result1_DNS1_" + dateToName + ".txt";
//        fileResult2 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result2_DNS1_" + dateToName + ".txt";
        fileResult1 = "Result_DNS1_".concat(dateToName).concat("_console").concat(".txt");
        fileResult2 = "Result_DNS1_".concat(dateToName).concat(".txt");

        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        System.out.println(format1);

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
        Windows, Linux;
    }

    enum cityShops{
        samara, novokuybishevsk, chapaevsk;
    }

}
