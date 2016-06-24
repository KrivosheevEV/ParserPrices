package ru.parserprices.myparser;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by KrivosheevEV on 07.06.2016.
 */
public class ReadWriteBase {

    // JDBC URL, username and password of MySQL server
    private static String url = "jdbc:mysql://107.170.234.5:3306/Frontime";
    private static final String user = "frontime";
    private static final String password = "Ahjynfqv2015"; /*Фронтайм2015*/

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public ReadWriteBase(){

        try {
            //Class.forName("com.mysql.jdbc.Driver");

            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

        } catch (SQLException sqlEx){
            System.out.println("Error open base.");
            sqlEx.printStackTrace();
            closeBase();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Error get class.");
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Error load class.");
//            e.printStackTrace();
        }
    }

    public Boolean dataExist(Statement statement, String queryText) {

//        new ReadSiteDNS().addToResultString("Query: ".concat(queryText), addTo.LogFileAndConsole);

        try {
            rs = statement.executeQuery(queryText);
            if (rs.next()) return true;
            else return false;
        } catch (SQLException sqlEx) {
            new ReadSiteDNS().addToResultString("Wrong query: ".concat(queryText), addTo.LogFileAndConsole);
            return false;
        }
    }

    public void writeData(Statement statement, String queryText) {

        //String sData = arrayData[5];

//        java.sql.Date dateOfPrice;
//        try {
//            dateOfPrice = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(sData).getTime());
//        }catch (Exception e){
//            dateOfPrice = new java.sql.Date(System.currentTimeMillis());
//        }

//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        Date dateOfPrice = format.parse(arrayData[5]);

//        String query = String.format("INSERT INTO Frontime.goods (good, item, shop, price, dateofprice)" +
//                " VALUES ('" + clearLetters(arrayData[1]) + "', '" + arrayData[2] + "', '" + arrayData[3] + "', '" + Integer.parseInt(arrayData[4]) + "', '" + dateOfPrice + "');");

        try {
            statement.executeUpdate(queryText);
        } catch (SQLException sqlEx) {
            new ReadSiteDNS().addToResultString("Wrong query: ".concat(queryText), addTo.LogFileAndConsole);
//            sqlEx.printStackTrace();
        }
    }

    public void updateData(Statement statement, String[] arrayData) {

        java.sql.Date dateOfPrice;
        try {
            dateOfPrice = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(arrayData[5]).getTime());
        }catch (Exception e){
            dateOfPrice = new java.sql.Date(System.currentTimeMillis());
        }

        String query = "UPDATE goods SET price = '".concat(clearLetters(arrayData[4])) + "', dateofprice = '" + dateOfPrice +
                "' WHERE item LIKE '" + arrayData[2] + "' AND shop LIKE '" + arrayData[3] + "' LIMIT 5;";

        try {
            statement.executeUpdate(query);
            //new ReadSiteDNS().addToResultString("Query: ".concat(query), addTo.Console);
        } catch (SQLException sqlEx) {
            new ReadSiteDNS().addToResultString("Wrong query: ".concat(query), addTo.LogFileAndConsole);
        }
    }

    public Statement getStatement(){

        return this.stmt;
    }

    public void closeBase(){

        try {if (con != null) con.close();} catch (SQLException se) { /*can't do anything */ }
        try {if (stmt != null) stmt.close();} catch (SQLException se) { /*can't do anything */ }
        try {if (rs != null) rs.close();} catch (SQLException se) { /*can't do anything */ }

    }

    public String clearLetters(String givenString){
        String resultOfFunction = "";
        resultOfFunction = givenString.replace("'", "");
        return resultOfFunction;
    }

}
