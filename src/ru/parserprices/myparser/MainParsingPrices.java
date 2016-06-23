package ru.parserprices.myparser;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainParsingPrices {

    static OS currentOS;
    static shopNames shopName;
    static shopCities shopCity;
    static shopCityCodes shopCityCode;
    static String fileResult1, fileResult2;

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        } else currentOS = OS.Linux;

        setShop(args);
        setCityShop(args);

        // Get string-date to file name.
        String dateToName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

//        fileResult1 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result1_DNS1_" + dateToName + ".txt";
//        fileResult2 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result2_DNS1_" + dateToName + ".txt";
        fileResult1 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat("_console").concat(".txt");
        fileResult2 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat(".txt");


        // Export file if it set in argument package.
        if (args != null && args.length >= 3){
            new ExportFromBase(args[2]);
        }

        ReadWriteFile mPatternOfSite = new ReadWriteFile("Pattern_DNS1.xml");

        // Read sites. DNS.
        ReadSiteDNS readSite_DNS = new ReadSiteDNS();
        readSite_DNS.ReadSite(mPatternOfSite.getFullAddress());

        // Write logs.
        ReadWriteFile mResultOfParsing = new ReadWriteFile(fileResult1);
        mResultOfParsing.writeResultToFile(mResultOfParsing.getFullAddress(), readSite_DNS.getResultOfParsing(), false);
//        mResultOfParsing.writeResultToFile("D:/Projects/ParserPrices/out/artifacts/ParserPrices_jar/1.txt", readSite_DNS.getResultOfParsing());

    }

    private static void setShop(String[] args){

        ReadSiteDNS readSiteDNS = new ReadSiteDNS();
        String argument = args[0];

        if (args != null && args.length >= 1){

            try {
                shopName = shopNames.valueOf(argument);
            }catch (Exception e) {
                readSiteDNS.addToResultString("Wrong argument #1 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
            }
        }else {
            readSiteDNS.addToResultString("Not set argument #1 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
        }
    }

    private static void setCityShop(String[] args){

        ReadSiteDNS readSiteDNS = new ReadSiteDNS();
        String argument = args[1];

        if (args != null && args.length >= 2){
//            try {
//                cityShop = cityShops.valueOf(argument);
//            }catch (Exception e) {
//                readSiteDNS.addToResultString("Wrong argument #2 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
//            }
            if (argument.equals("846")) {
                shopCity = shopCities.samara;
                shopCityCode = shopCityCodes._846;
            }
            else if (argument.equals("84635")) {
                shopCity = shopCities.novokuybishevsk;
                shopCityCode = shopCityCodes._84635;
            }
            else if (argument.equals("84639")) {
                shopCity = shopCities.chapaevsk;
                shopCityCode = shopCityCodes._84639;
            }
            else readSiteDNS.addToResultString("Wrong argument #2 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);

        }else {
            readSiteDNS.addToResultString("Not set argument #2 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
        }
    }




}
