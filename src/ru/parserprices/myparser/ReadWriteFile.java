package ru.parserprices.myparser;

import ru.parserprices.myparser.interfaces.ReadSites_Interface;

import java.io.*;

import static ru.parserprices.myparser.MainParsingPrices.addToResultString;

/**
 * Created by KrivosheevEV on 30.05.2016.
 */
public class ReadWriteFile {

    private String fileFullAddress;
    private static File mFile;

    public ReadWriteFile(String givenFileName){

        this.fileFullAddress = getCurrentPath() + givenFileName;
        this.mFile = new File(fileFullAddress);

        if(!mFile.exists()){
            File path = new File(mFile.getParent());
            if (!path.exists()) {
                if (path.mkdir()) addToResultString("Directory is creating.", addTo.LogFileAndConsole);
                else addToResultString("Error creating of directory.", addTo.LogFileAndConsole);
            }
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        System.out.println(fileFullAddress);
    }

    public void setFullAddress(String FileAddress, String FileName){

        this.fileFullAddress = FileAddress + FileName;

    }

    public String getFullAddress(){

        return this.fileFullAddress;

    }

    public String getCurrentPath(){

        String pathStartingApp = "";
        byte prefixException = 0;
        byte suffixException = 1;
//        return new File(ReadWriteFile.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

        try {
            if (MainParsingPrices.currentOS == OS.Windows){
//                pathStartingApp = ClassLoader.getSystemClassLoader().getResource(".").getPath();
                pathStartingApp = new File(".").getAbsolutePath();
            }else {
                pathStartingApp = "/" + new File(".").getAbsolutePath();
                prefixException = 0;
                suffixException = 2;
            }
        }catch (Exception e){
            System.out.println("Error getting path.");
            e.printStackTrace();
        }

        pathStartingApp = pathStartingApp.substring(prefixException, pathStartingApp.length() - suffixException);

        if (MainParsingPrices.currentOS != OS.Windows)pathStartingApp = pathStartingApp + "/";

//        System.out.println(" => " + pathStartingApp);

        return pathStartingApp;

    }

    public String readFile() {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        try {

            exists(mFile);

            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(mFile.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    if (!s.startsWith("//") & !s.isEmpty()){
                        sb.append(s);
                        sb.append("\n");
                    }
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();

    }

    private static void exists(File file) throws FileNotFoundException {
        if (!file.exists()){
            System.out.print("File not exist: " + file.getPath() + file.getName() + "\n");
            throw new FileNotFoundException(file.getName());
        }
    }

    public void writeResultToFile(String fileName, String text, Boolean appendFile) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                File path = new File(file.getParent());
                if (!path.exists()) {
                    if (path.mkdir()) addToResultString("Directory is creating.", addTo.LogFileAndConsole);
                    else addToResultString("Error creating of directory.", addTo.LogFileAndConsole);
                }
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            FileWriter out = new FileWriter(fileName, appendFile);

            try {
                //Записываем текст у файл
                out.write(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }



}