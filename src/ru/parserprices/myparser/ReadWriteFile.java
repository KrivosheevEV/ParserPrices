package ru.parserprices.myparser;

import ru.parserprices.myparser.interfaces.ReadSites_Interface;

import java.io.*;

/**
 * Created by KrivosheevEV on 30.05.2016.
 */
public class ReadWriteFile implements ReadSites_Interface {

    private static String FileFullAddress;
    private static File mFile;


    public void main(String[] args){

       // this.mFile = new File(FileFullAddress);

    }

    public void setFullAddress(String FileAddress, String FileName){

        this.FileFullAddress = FileAddress + FileName;
        this.mFile = new File(FileFullAddress);

    }

    public String getFullAddress(){

        return this.FileFullAddress;

    }

    public String readFile(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        exists(fileName);

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(mFile.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
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

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()){
            System.out.print("File not exist: " + fileName);
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
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

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
