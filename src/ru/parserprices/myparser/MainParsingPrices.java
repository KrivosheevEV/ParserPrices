package ru.parserprices.myparser;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MainParsingPrices {

    static String PROP_SHOP;
    static String PROP_CITY;
    static String PROP_EXPORT;
    static String PROP_ZIP;
    static String PROP_DELETE;
    static String PROP_EMAIL;
    static String PROP_URL;
    static String PROP_CATEGORY1;
    static String PROP_SUBCATEGORIES1;
    static boolean haveProperty;

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

       /* // Anticaptcha.
        int conutIteration = 0;
        try {
            AntiCaptcha antiCaptcha = new AntiCaptcha("D:\\Temp\\avito_phonenumber.png");
            while (!antiCaptcha.getCaptchaStatus() & conutIteration++ <= 10){
                //Thread.sleep(3000);
            }
            if (antiCaptcha.getCaptchaStatus()){
                System.out.printf(antiCaptcha.getCaptchaText().concat(""));
            }else System.out.println("Error read captcha.");
        }catch (Exception e){
            *//**//*
        }*/


        haveProperty = readProperty(getArgumentValue(args, "-property"));

        setShop(haveProperty ? PROP_SHOP : getArgumentValue(args, "-shop"));
        setCityShop(haveProperty ? PROP_CITY : getArgumentValue(args, "-city"));

        // Get string-date for file name.
        String dateToName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

//        fileResult1 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result1_DNS1_" + dateToName + ".txt";
//        fileResult2 = "log" + (currentOS == OS.Windows ? "\\" : "/") + "Result2_DNS1_" + dateToName + ".txt";
        fileName_Result1 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat("_console").concat(".txt");
        fileName_Result2 = "Result_".concat(shopName.name()).concat("_").concat(shopCity.name()).concat("_").concat(dateToName).concat(".txt");
        String fullPathForLogs2 = "logs".concat(currentOS == OS.Windows ? "\\" : "/").concat(fileName_Result2);

        resultToLog = new ReadWriteFile(fullPathForLogs2);

        if (shopName == shopNames.empty || shopCity == shopCities.empty || shopCityCode == shopCityCodes.empty){
            addToResultString("Error to reading arguments value.",  addTo.LogFileAndConsole);
            return;
        }

        // Read another sites.
        if (shopName == shopNames.AVITO) {
            new ReadSite.Read_Avito(PROP_URL);
        }else{
            ReadSites readSites = new ReadSites();
            readSites.ReadSite(shopName);
        }

        // Export file if it set in argument package.
        boolean needExport = haveProperty  ? !PROP_EXPORT.toUpperCase().equals("NO") : !getArgumentValue(args, "-export").isEmpty();
        if (args != null && needExport){
            new ExportFromBase(args);
            return;
        }

//        readPattern = new ReadWriteFile("Pattern_DNS1_.xml");


        // Send Email.
        String emailAdress = haveProperty ? PROP_EMAIL : getArgumentValue(args, "-email");
        boolean sendEmail = emailAdress.toUpperCase().equals("NO");
        if (sendEmail){
            String[] paramConnect = {emailAdress, "KrivosheevEV@frontime.ru", "owa.axus.name"};
            String mSubject = "Cron on \"parser\" for ".concat(shopName.name()).concat(shopCityCode.name());
            String mText = resultOfPasring;
            try {
                new Email(paramConnect, mSubject, mText);
            }catch (Exception e) {
                addToResultString(e.toString(), addTo.LogFileAndConsole);
            }
        }

        // Write logs.
        resultToLog.writeResultToFile(resultToLog.getFullAddress(), resultOfPasring, false);

        /*try(InputStream in = new URL("http://example.com/image.jpg").openStream()){
            Files.copy(in, Paths.get("C:/File/To/Save/To/image.jpg"));
        }*/
    }

    private static void setShop(String argumentValue){

        if (!argumentValue.isEmpty()){

            try {
                shopName = shopNames.valueOf(argumentValue.toUpperCase());
            }catch (Exception e) {
                shopName = shopNames.empty;
                addToResultString("Wrong value of argument '-shop' (".concat(argumentValue).concat(")."),  addTo.LogFileAndConsole);
            }
        }else {
            shopName = shopNames.empty;
            addToResultString("Not set argument '-shop'.",  addTo.LogFileAndConsole);
        }
    }

    private static void setCityShop(String argumentValue){

        if (!argumentValue.isEmpty()){

            if (argumentValue.equals("85592")) {
                shopCity = shopCities.aznakaevo;
                shopCityCode = shopCityCodes._85595;
            }
            else if (argumentValue.equals("85594")) {
                shopCity = shopCities.bugulma;
                shopCityCode = shopCityCodes._85594;
            }
            else if (argumentValue.equals("35342")) {
                shopCity = shopCities.buzuluk;
                shopCityCode = shopCityCodes._35342;
            }
            else if (argumentValue.equals("84593")) {
                shopCity = shopCities.volsk;
                shopCityCode = shopCityCodes._84593;
            }
            else if (argumentValue.equals("84235")) {
                shopCity = shopCities.dimitrovgrad;
                shopCityCode = shopCityCodes._84235;
            }
            else if (argumentValue.equals("85558")) {
                shopCity = shopCities.zainsk;
                shopCityCode = shopCityCodes._85558;
            }
            else if (argumentValue.equals("85595")) {
                shopCity = shopCities.leninogorsk;
                shopCityCode = shopCityCodes._85595;
            }
            else if (argumentValue.equals("846")) {
                shopCity = shopCities.samara;
                shopCityCode = shopCityCodes._846;
            }
            else if (argumentValue.equals("84635")) {
                shopCity = shopCities.novokuybishevsk;
                shopCityCode = shopCityCodes._84635;
            }
            else if (argumentValue.equals("8464")) {
                shopCity = shopCities.syzran;
                shopCityCode = shopCityCodes._8464;
            }
            else if (argumentValue.equals("84639")) {
                shopCity = shopCities.chapaevsk;
                shopCityCode = shopCityCodes._84639;
            }
            else if (argumentValue.equals("84342")) {
                shopCity = shopCities.chistopol;
                shopCityCode = shopCityCodes._84342;
            }
            else if (argumentValue.equals("8482")) {
                shopCity = shopCities.tolyatti;
                shopCityCode = shopCityCodes._8482;
            }
            else if (argumentValue.equals("84165")) {
                shopCity = shopCities.nikolsk;
                shopCityCode = shopCityCodes._84165;
            }
            else if (argumentValue.equals("84253")) {
                    shopCity = shopCities.barysh;
                    shopCityCode = shopCityCodes._84253;
                }
            else {
                shopCity = shopCities.empty;
                shopCityCode = shopCityCodes.empty;
                addToResultString("Wrong value of argument '-city' (".concat(argumentValue).concat(")."),  addTo.LogFileAndConsole);
            }

        }else {
            shopCity = shopCities.empty;
            shopCityCode = shopCityCodes.empty;
            addToResultString("Not set argument '-city'.",  addTo.LogFileAndConsole);
        }
    }

    public String getResultOfParsing() {

        return resultOfPasring;
    }

    public static void addToResultString(String addedString, addTo writeIntoLogFile) {
        //if (addedString.isEmpty()) return;
        Long currentMilliseconds = System.currentTimeMillis();
        Long elapsedTime = (currentMilliseconds - startTime) / 1000;
        Long secondsElapse = elapsedTime % 60;
        Long minutsElapse = elapsedTime / 60 % 60;
        Long hoursElapse = elapsedTime / 3600 % 24;
        String timeForResult = Long.toString(hoursElapse) + "." + Long.toString(minutsElapse) + "." + Long.toString(secondsElapse);
        String stringToLog = timeForResult + " -> " + addedString + System.getProperty("line.separator");
        resultOfPasring = resultOfPasring.concat(stringToLog);

        try {
            if (resultToLog != null && writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.logFile) {
                resultToLog.writeResultToFile(resultToLog.getFullAddress(), stringToLog, true);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }

        if (writeIntoLogFile == addTo.LogFileAndConsole || writeIntoLogFile == addTo.Console)
            System.out.println(addedString);
    }

    public static String getArgumentValue(String[] args, String argument){

        String value = "";

        for (int counter = 0; counter < args.length - 1; counter++) {
            if (args[counter].toUpperCase().equals(argument.toUpperCase())) {
                value = args[counter + 1];
                break;
            }
        }

        if (value.startsWith("-")) {
            addToResultString("Wrong value (".concat(value).concat(") of argument '").concat(argument).concat("'."),  addTo.LogFileAndConsole);
            value = "";
        }

        return value;

    }

    public static Boolean argumentExist(String[] args, String argument){

        Boolean findArgument = false;

        for (int counter = 0; counter < args.length - 1; counter++) {
            if (args[counter].toUpperCase().equals(argument.toUpperCase())) {
                findArgument = true;
                break;
            }
        }

        return findArgument;

    }

    private static Boolean readProperty(String nameFileProperty){

        File fileProperty = new File(new ReadWriteFile(nameFileProperty).getFullAddress());
        if (!fileProperty.exists() || fileProperty.isDirectory()) return false;

        Properties property = new Properties();

        try {
            FileInputStream fis = new FileInputStream(fileProperty);
            property.load(fis);

            PROP_SHOP           = property.getProperty("shop").trim();
            PROP_CITY           = property.getProperty("city").trim();
            PROP_EXPORT         = property.getProperty("export").trim();
            PROP_ZIP            = property.getProperty("zip").trim();
            PROP_DELETE         = property.getProperty("delete").trim();
            PROP_EMAIL          = property.getProperty("email").trim();
            PROP_URL            = property.getProperty("url").trim();
            PROP_CATEGORY1      = property.getProperty("category1").trim();
            PROP_SUBCATEGORIES1 = property.getProperty("subcategories1").trim();

            return !(PROP_SHOP==null || PROP_CITY==null || PROP_EXPORT==null || PROP_ZIP==null || PROP_DELETE==null || PROP_EMAIL==null || PROP_URL==null ||
                    PROP_SHOP.isEmpty() || PROP_CITY.isEmpty() || PROP_EXPORT.isEmpty() || PROP_ZIP.isEmpty() || PROP_DELETE.isEmpty() || PROP_EMAIL.isEmpty() || PROP_URL.isEmpty());

        } catch (IOException e) {
            addToResultString("Error reading property file.", addTo.LogFileAndConsole);
            e.printStackTrace();
            return false;
        }

    }

}
