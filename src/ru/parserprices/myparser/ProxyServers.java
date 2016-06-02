package ru.parserprices.myparser;

import org.openqa.selenium.Proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

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

        listProxyServers_RU.add("109.195.131.207");
        listProxyServers_RU.add("110.8.253.100");
        listProxyServers_RU.add("119.70.40.100");
        listProxyServers_RU.add("46.101.129.227");
        listProxyServers_RU.add("46.101.36.66");
        listProxyServers_RU.add("85.198.103.35");
        listProxyServers_RU.add("185.12.94.236");
        listProxyServers_RU.add("212.120.163.170");
        listProxyServers_RU.add("46.48.133.250");
        listProxyServers_RU.add("120.52.72.21");
        listProxyServers_RU.add("120.52.72.53");
        listProxyServers_RU.add("123.56.28.108");
        listProxyServers_RU.add("121.22.252.248");
        listProxyServers_RU.add("120.131.128.211");

        String actualProxyServer = "nbr-02:8080";
        int timeOut = 1;

        for (int i = 0; i <= listProxyServers_RU.size(); i++) {

            if (i == listProxyServers_RU.size() || timeOut <= 500) {
                System.out.println(timeOut);
                i = 0;
                timeOut = timeOut + 100;
            }

            InetAddress inetAddress = InetAddress.getByName(listProxyServers_RU.get(i));
            if (inetAddress.isReachable(timeOut)) {
                actualProxyServer = listProxyServers_RU.get(i);
                break;
            }
        }

        System.out.println("Finish search quickly proxy");

        return actualProxyServer;

    }

    public boolean checkProxy(String proxyHost_, String proxyPort_, int timeOut) {
        Socket socket = null;
        try {
            socket = new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost,
                    proxyPort)));

            socket.connect(new InetSocketAddress(proxyHost_, proxyPort_), timeOut);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
