package ru.parserprices.myparser;

import org.apache.xpath.operations.Bool;
import org.openqa.selenium.Proxy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by KrivosheevEV on 02.06.2016.
 */
public class ProxyServers {

    public String getProxyServer() throws IOException {

        System.out.println("Start search quickly proxy");

        ArrayList<String> listProxyServers_RU = new ArrayList<String>();
//        listProxyServers_RU.add("109.195.131.207:1080");
//        listProxyServers_RU.add("110.8.253.100:80");
//        listProxyServers_RU.add("119.70.40.100:8080");
//        listProxyServers_RU.add("212.120.163710:80");
//        listProxyServers_RU.add("46.101.129.227:3128");
//        listProxyServers_RU.add("46.101.36.66:2015");
//        listProxyServers_RU.add("85.198.103.35:8080");
//        listProxyServers_RU.add("185.12.94.236:4444");
//        listProxyServers_RU.add("212.120.163.170:80");
//        listProxyServers_RU.add("46.48.133.250:3128");
//        listProxyServers_RU.add("120.52.72.21:80");
//        listProxyServers_RU.add("120.52.72.53:80");
//        listProxyServers_RU.add("123.56.28.108:8888");
//        listProxyServers_RU.add("121.22.252.248:8000");
//        listProxyServers_RU.add("120.131.128.211:80");

//        listProxyServers_RU.add("109.195.131.207");
//        listProxyServers_RU.add("110.8.253.100");
//        listProxyServers_RU.add("119.70.40.100");
//        listProxyServers_RU.add("46.101.129.227");
//        listProxyServers_RU.add("46.101.36.66");
//        listProxyServers_RU.add("85.198.103.35");
//        listProxyServers_RU.add("185.12.94.236");
//        listProxyServers_RU.add("212.120.163.170");
//        listProxyServers_RU.add("46.48.133.250");
//        listProxyServers_RU.add("120.52.72.21");
//        listProxyServers_RU.add("120.52.72.53");
//        listProxyServers_RU.add("123.56.28.108");
//        listProxyServers_RU.add("121.22.252.248");
//        listProxyServers_RU.add("120.131.128.211");

        listProxyServers_RU = fillListProxyServersFromFile("ProxyServers.txt", ProxyAddressWithPorts.No);

        String actualProxyServer = "nbr-02:8080";
        int timeOut = 1000;

        for (int i = 0; i < listProxyServers_RU.size(); i++) {

            if (timeOut == 5000) {
                System.out.println(timeOut);
                break;
            }

            if (i == listProxyServers_RU.size()) {
                System.out.println(timeOut);
                i = 0;
                timeOut = timeOut + 1000;
            }

            try {
                InetAddress inetAddress = InetAddress.getByName(deletePortFromProxyAddress(listProxyServers_RU.get(i)));
                if (inetAddress.isReachable(timeOut)) {
                    actualProxyServer = listProxyServers_RU.get(i);
                    break;
                }
            }catch (Exception e){

            }

        }

//        for (int i = 0; i < listProxyServers_RU.size(); i++) {
//
//            String newProxyServer = listProxyServers_RU.get(i);
////            String newProxyIP = newProxyServer.substring(0, newProxyServer.indexOf(":") - 1);
////            int newProxyPort = Integer.parseInt(newProxyServer.substring(newProxyServer.length() - 2));
//
//            if (proxyIsAlive2(newProxyServer)){
//                actualProxyServer = newProxyServer;
//                break;
//            }
//
//        }


        System.out.println("Finish search quickly proxy");

        return actualProxyServer;

    }


    public boolean proxyIsAlive(String proxyHost_, int proxyPort_, int timeOut) {


        Boolean resultChecking = false;

        Socket t = null;

        try {
            t = new Socket(proxyHost_, proxyPort_);
            DataInputStream dis = new DataInputStream(t.getInputStream());
            PrintStream ps = new PrintStream(t.getOutputStream());
            ps.println("Test");
            String str = dis.readUTF();
            if (str.equals("Test")) resultChecking = true;
            t.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return resultChecking;

    }

    public static boolean proxyIsAlive2(String url) throws IOException {

        boolean result = false;
        int code = 0;
        try {
            URL siteURL = new URL("http://" + url);
            HttpURLConnection connection = (HttpURLConnection) siteURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            code = connection.getResponseCode();
            if (code == 200) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }

        System.out.println(url + ", code: " + code);

        return result;
    }

    private ArrayList<String> fillListProxyServersFromFile(String filename, ProxyAddressWithPorts withPorts){

        ArrayList<String> listLinkPages;
        ReadWriteFile mFileWithCategories = new ReadWriteFile(filename);

        String mFile = mFileWithCategories.readFile();
        listLinkPages = new ArrayList<String>(Arrays.asList(mFile.split("\n")));

//        if (withPorts == ProxyAddressWithPorts.No) {
//            for (String strOfList: listLinkPages) {
//                String strOfListNew = strOfList.substring(0, strOfList.indexOf(":"));
//                listLinkPages.set(listLinkPages.indexOf(strOfList), strOfListNew);
//            }
//        }
        return listLinkPages;

    }

    private String deletePortFromProxyAddress(String fullProxyAddress){

        return fullProxyAddress.substring(0, fullProxyAddress.indexOf(":"));
    }

    enum ProxyAddressWithPorts{
        Yes, No
    }

}

