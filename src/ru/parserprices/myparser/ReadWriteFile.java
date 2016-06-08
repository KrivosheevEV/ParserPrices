package ru.parserprices.myparser;

import ru.parserprices.myparser.interfaces.ReadSites_Interface;

import java.io.*;

/**
 * Created by KrivosheevEV on 30.05.2016.
 */
public class ReadWriteFile implements ReadSites_Interface {

    private String fileFullAddress;
    private static File mFile;

    public ReadWriteFile(String givenFileName){
        this.fileFullAddress = getCurrentPath() + "/" + givenFileName;
        this.mFile = new File(fileFullAddress);

        System.out.println(fileFullAddress);
    }

    public void setFullAddress(String FileAddress, String FileName){

        this.fileFullAddress = FileAddress + FileName;

    }

    public String getFullAddress(){

        return fileFullAddress;

    }

    public String getCurrentPath(){

        String pathStartingApp = "";
        byte suffixException = 1;
//        return new File(ReadWriteFile.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

        try {
            if (MainParsingPrices.currentOS == MainParsingPrices.OS.Windows){
                pathStartingApp = ClassLoader.getSystemClassLoader().getResource(".").getPath();
            }else {
                pathStartingApp = "/" + new File(".").getAbsolutePath();
                suffixException = 2;
            }
        }catch (Exception e){
        }

        pathStartingApp = pathStartingApp.substring(1, pathStartingApp.length() - suffixException);
        System.out.println(" => " + pathStartingApp);

        return pathStartingApp;

//        String path=System.getProperty("java.class.path");
//        String FileSeparator=(String)System.getProperty("file.separator");
//        return path.substring(0, path.lastIndexOf(FileSeparator)+1);

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

    @Override
    public void writeResultToFile(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(fileName);

            try {
                //Записываем текст у файл
                out.print(text);
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