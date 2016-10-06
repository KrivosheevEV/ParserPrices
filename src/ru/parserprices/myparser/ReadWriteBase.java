package ru.parserprices.myparser;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.parserprices.myparser.MainParsingPrices.addToResultString;

/**
 * Created by KrivosheevEV on 07.06.2016.
 */
public class ReadWriteBase {

    // JDBC URL, username and password of MySQL server
//    private static String url = "jdbc:mysql://107.170.234.5:3306/Frontime";
//    private static String url = "jdbc:mysql://parserpro.ru:3306/Frontime";
    private static String url = "jdbc:mysql://parserpro.ru:3306/general";
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
            stmt = null;
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

    public ResultSet readData(Statement statement, String queryText) {
        try {
            rs = statement.executeQuery(queryText);
            return rs;
        } catch (SQLException sqlEx) {
            addToResultString("Wrong query: ".concat(queryText), addTo.LogFileAndConsole);
            return null;
        }
    }

    public Boolean dataExist(Statement statement, String queryText) {

//        new ReadSites().addToResultString("Query: ".concat(queryText), addTo.LogFileAndConsole);

        try {
            rs = statement.executeQuery(queryText);
            if (rs.next()) return true;
            else return false;
        } catch (SQLException sqlEx) {
            addToResultString("Wrong query: ".concat(queryText), addTo.LogFileAndConsole);
            addToResultString(sqlEx.getMessage(), addTo.LogFileAndConsole);
            return false;
        }
    }

    public Boolean writeDataSuccessfully(Statement statement, String queryText) {

        try {
            statement.executeUpdate(queryText);
            return true;
        } catch (SQLException sqlEx) {
            addToResultString("Wrong query: ".concat(queryText), addTo.LogFileAndConsole);
            addToResultString(sqlEx.getMessage(), addTo.LogFileAndConsole);
            return false;
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
        resultOfFunction = resultOfFunction.replace("\\", "/");
        return resultOfFunction;
    }

}
