package ru.parserprices.myparser;

import java.sql.*;

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
            Class.forName("com.mysql.jdbc.Driver");

            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

        } catch (SQLException sqlEx){
            System.out.println("Error open base.");
            System.out.println(sqlEx);
//        } catch (ClassNotFoundException e) {
//            System.out.println("Error get class.");
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error load class.");
            e.printStackTrace();
        }
    }

    public int readData(String queryText) {

        try {
            rs = stmt.executeQuery(queryText);

            while (rs.next()) {
                int count = rs.getInt(1);
                return count;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }finally {
            return -1;
        }
    }

    public void writeData(Statement statement, String[] arrayData) {

        String query = "INSERT INTO Frontime.goods (good, item, shop, price) \n" +
                " VALUES ('" + arrayData[1] + "', '" + arrayData[2] + "', '" + arrayData[3] + "', " + Integer.parseInt(arrayData[4]) + ");";

        try {
            statement.executeUpdate(query);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }

    public Statement getStatement(){

        return this.stmt;
    }

    public void closeBase(){

        try {con.close();} catch (SQLException se) { /*can't do anything */ }
        try {stmt.close();} catch (SQLException se) { /*can't do anything */ }
        try {rs.close();} catch (SQLException se) { /*can't do anything */ }

    }

}
