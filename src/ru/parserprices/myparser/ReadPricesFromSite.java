package ru.parserprices.myparser;


import java.io.File;
import java.io.IOException;

public class ReadPricesFromSite {

    public static void main(String[] args) throws IOException, InterruptedException {

        ReadWriteFile mPatternOfSite = new ReadWriteFile();
//        mPatternOfSite.setFullAddress("D:/Temp/", "Pattern_DNS1.txt");
        mPatternOfSite.setFullAddress(mPatternOfSite.getCurrentPath(), "Pattern_DNS1.xml");

        ReadSiteUseFirefoxDriver readSiteUseFirefoxDriver = new ReadSiteUseFirefoxDriver();
        readSiteUseFirefoxDriver.ReadSite(mPatternOfSite.getFullAddress());

        ReadWriteFile mResultOfParsing = new ReadWriteFile();
        mResultOfParsing.setFullAddress(mPatternOfSite.getCurrentPath(), "/Result_DNS1.txt");
//
        mResultOfParsing.writeResultToFile(mResultOfParsing.getFullAddress(), readSiteUseFirefoxDriver.getResultOfParsing());
//        mResultOfParsing.writeResultToFile("D:/Projects/ParserPrices/out/artifacts/ParserPrices_jar/1.txt", readSiteUseFirefoxDriver.getResultOfParsing());


    }
}
