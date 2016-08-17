package ru.parserprices.myparser;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainParsingPrices {

    private static String resultOfPasring = "";
    private static long startTime;

    static OS currentOS;
    static shopNames shopName;
    static shopCities shopCity;
    static shopCityCodes shopCityCode;
    static String fileName_Result1, fileName_Result2;

    private static ReadWriteFile resultToLog, readPattern;

    public static void main(String[] args) throws IOException, InterruptedException {

        startTime = System.currentTimeMillis();

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        } else currentOS = OS.Linux;

        setShop(args);
        setCityShop(args);

        // Get string-date for file name.
        String dateToName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

//        fileResult1 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result1_DNS1_" + dateToName + ".txt";
//        fileResult2 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result2_DNS1_" + dateToName + ".txt";
        fileName_Result1 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat("_console").concat(".txt");
        fileName_Result2 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat(".txt");
        String fullPathForLogs2 = "logs".concat(currentOS == OS.Windows ? "\\" : "/").concat(fileName_Result2);

        resultToLog = new ReadWriteFile(fullPathForLogs2);

        // Export file if it set in argument package.
        if (args != null && args.length >= 3){
            new ExportFromBase(args);
            return;
        }

//        readPattern = new ReadWriteFile("Pattern_DNS1_.xml");

        // Read sites. DNS.
        ReadSites readSites = new ReadSites();
        readSites.ReadSite(shopName);

        // Write logs.
        resultToLog.writeResultToFile(resultToLog.getFullAddress(), resultOfPasring, false);

    }

    private static void setShop(String[] args){

        String argument = args[0];

        if (args.length >= 1){

            try {
                shopName = shopNames.valueOf(argument.toUpperCase());
            }catch (Exception e) {
                addToResultString("Wrong argument #1 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
            }
        }else {
            addToResultString("Not set argument #1 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
        }
    }

    private static void setCityShop(String[] args){

        String argument = args[1];

        if (args.length >= 2){
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
            else addToResultString("Wrong argument #2 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);

        }else {
            addToResultString("Not set argument #2 (".concat(argument).concat(")."),  addTo.LogFileAndConsole);
        }
    }

    public String getResultOfParsing() {

        return this.resultOfPasring;
    }
    public static void addToResultString(String addedString, addTo writeIntoLogFile) {
        //if (addedString.isEmpty()) return;
        Long currentMilliseconds = System.currentTimeMillis();
        Long elapsedTime = (currentMilliseconds - startTime) / 1000;
        Long secondsElapse = elapsedTime % 60;
        Long minutsElapse = elapsedTime / 60;
        Long hoursElapse = elapsedTime / 3600;
        String timeForResult = Long.toString(hoursElapse) + "." + Long.toString(minutsElapse) + "." + Long.toString(secondsElapse);
        String stringToLog = timeForResult + " -> " + addedString + System.getProperty("line.separator");
        resultOfPasring = resultOfPasring.concat(stringToLog);

        if (writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.logFile) {
            resultToLog.writeResultToFile(resultToLog.getFullAddress(), stringToLog, true);
        }

        if (writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.Console)
            System.out.println(addedString);
    }
}
