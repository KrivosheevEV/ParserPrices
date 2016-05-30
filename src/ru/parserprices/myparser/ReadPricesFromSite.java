package ru.parserprices.myparser;


import java.io.IOException;

public class ReadPricesFromSite {

    public static void main(String[] args) throws IOException, InterruptedException {

        ReadWriteFile mPatternOfSite = new ReadWriteFile();
        mPatternOfSite.setFullAddress("D:/Temp/", "Pattern_DNS1.txt");

        ReadSiteUseFirefoxDriver readSiteUseFirefoxDriver = new ReadSiteUseFirefoxDriver();
        readSiteUseFirefoxDriver.ReadSite(mPatternOfSite.getFullAddress());

//        ReadWriteFile mResultOfParsing = new ReadWriteFile();
//        mResultOfParsing.setFullAddress("D:/Temp/", "Result_DNS1.txt");
//
//        readSiteUseFirefoxDriver.writeResultToFile(mResultOfParsing.getFullAddress(), readSiteUseFirefoxDriver.getResultOfParsing());


    }
}
