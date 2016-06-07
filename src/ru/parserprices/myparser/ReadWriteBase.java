package ru.parserprices.myparser;

import java.sql.*;

/**
 * Created by KrivosheevEV on 07.06.2016.
 */
public class ReadWriteBase {

    // JDBC URL, username and password of MySQL server
    private static String url = "jdbc:mysql://107.170.234.5:3306/Goods";
    private static final String user = "root";
    private static final String password = "root";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public int readData(String queryText) {
//        String query = "select count(*) from books";

        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            rs = stmt.executeQuery(queryText);

            while (rs.next()) {
                int count = rs.getInt(1);
//                System.out.println("Total number of books in the table : " + count);
                return count;
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                rs.close();
            } catch (SQLException se) { /*can't do anything */ }
            return -1;
        }
    }

    public void setData(String[] arrayData) {


        String query = "INSERT INTO Goods.goods (num, good, item, shop, price) \n" +
                " VALUES (" + Integer.parseInt(arrayData[0]) + ", '" + arrayData[1] + "', '" + arrayData[2] + "', '" + arrayData[3] + "', " + Integer.parseInt(arrayData[4]) + ");";

        try {
            con = DriverManager.getConnection(url, user, password);
            // getting Statement object to execute query
            stmt = con.createStatement();
            // executing SELECT query
            stmt.executeUpdate(query);

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                rs.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

    }

}
