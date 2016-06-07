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

        //System.out.println("Start search quickly proxy");

        ArrayList<String> listProxyServers_RU = new ArrayList<String>();
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

            String newProxyServer = listProxyServers_RU.get(i);

//            if (proxyIsAlive(getIPFromProxyAddress(newProxyServer), getPortFromProxyAddress(newProxyServer), timeOut))
////            if (proxyIsAlive2(newProxyServer))
// {
//                System.out.println(newProxyServer + " +");
//                actualProxyServer = newProxyServer;
//
//                break;
//            }else System.out.println(newProxyServer + " -");

//            try {
//                InetAddress inetAddress = InetAddress.getByName(getIPFromProxyAddress(listProxyServers_RU.get(i)));
//                if (inetAddress.isReachable(timeOut)) {
//                    actualProxyServer = listProxyServers_RU.get(i);
//                    System.out.println(newProxyServer + " +");
////                    break;
//                }else System.out.println(newProxyServer + " -");
//            }catch (Exception e){
//                System.out.println(newProxyServer + " - (timeout)");
//            }

        }



//        for (int i = 0; i < listProxyServers_RU.size(); i++) {
//
//            String newProxyServer = listProxyServers_RU.get(i);
////            String newProxyIP = newProxyServer.substring(0, newProxyServer.indexOf(":") - 1);
////            int newProxyPort = Integer.parseInt(newProxyServer.substring(newProxyServer.length() - 2));
//

//
//        }


        //System.out.println("Finish search quickly proxy");

        return actualProxyServer;

    }


    public boolean proxyIsAlive(String proxyHost_, int proxyPort_, int timeOut) {


        Boolean resultChecking = false;

//        Socket t = null;

        try {
            Socket t = new Socket(proxyHost_, proxyPort_);
            DataInputStream dis = new DataInputStream(t.getInputStream());
            PrintStream ps = new PrintStream(t.getOutputStream());
            ps.println("Test");
            String str = dis.readUTF();
            if (str.equals("Test")) resultChecking = true;
            resultChecking = true;
            t.close();
        } catch (IOException e) {
            //e.printStackTrace();
            //System.out.println("Error open socket: " + proxyHost_ + ":" + proxyPort_);
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

    public String getIPFromProxyAddress(String fullProxyAddress){

        return fullProxyAddress.substring(0, fullProxyAddress.indexOf(":"));
    }

    public int getPortFromProxyAddress(String fullProxyAddress){

        return Integer.parseInt(fullProxyAddress.substring(fullProxyAddress.indexOf(":") + 1));
    }

    enum ProxyAddressWithPorts{
        Yes, No
    }

}

