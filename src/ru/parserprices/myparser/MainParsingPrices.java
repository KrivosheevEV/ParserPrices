package ru.parserprices.myparser;


import com.googlecode.fannj.ActivationFunction;
import com.googlecode.fannj.Fann;
import com.googlecode.fannj.Layer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

        /*String path = "D:/Temp/FANN-2.2.0-Source/bin";
        System.setProperty("jna.library.path", path);

        System.out.println(path); //maybe the path is malformed
        File file = new File(path + "/fannfloat.dll");
        System.out.println("Is the dll file there:" + file.exists());
        System.load(file.getAbsolutePath());

        Fann fann = new Fann("D:/Temp/avito_phonenumber.png" );
        float[] inputs = new float[]{};

//        List<Layer> layers = new ArrayList<Layer>();
//        layers.add(Layer.create(2));
//        layers.add(Layer.create(3, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
//        layers.add(Layer.create(2, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
//        layers.add(Layer.create(1, ActivationFunction.FANN_SIGMOID_SYMMETRIC));


        float[] outputs = fann.run( inputs );
        fann.close();

        for (float f : outputs) {
            System.out.print(f + ",");
        }
*/

        // Get current OS.
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        } else currentOS = OS.Linux;

        setShop(getArgumentValue(args, "-shop"));
        setCityShop(getArgumentValue(args, "-city"));

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

        // Export file if it set in argument package.
        if (args != null && !getArgumentValue(args, "-export").isEmpty()){
            new ExportFromBase(args);
            return;
        }

//        readPattern = new ReadWriteFile("Pattern_DNS1_.xml");

        // Read sites. DNS.
        ReadSites readSites = new ReadSites();
        readSites.ReadSite(shopName);

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

            if (argumentValue.equals("846")) {
                shopCity = shopCities.samara;
                shopCityCode = shopCityCodes._846;
            }
            else if (argumentValue.equals("84635")) {
                shopCity = shopCities.novokuybishevsk;
                shopCityCode = shopCityCodes._84635;
            }
            else if (argumentValue.equals("84639")) {
                shopCity = shopCities.chapaevsk;
                shopCityCode = shopCityCodes._84639;
            }
            else if (argumentValue.equals("8464")) {
                shopCity = shopCities.syzran;
                shopCityCode = shopCityCodes._8464;
            }
            else if (argumentValue.equals("8482")) {
                shopCity = shopCities.tolyatti;
                shopCityCode = shopCityCodes._8482;
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

        return this.resultOfPasring;
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
}
