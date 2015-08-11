/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author swtf
 */
public class DBManager {

    String URL = "jdbc:mysql://localhost:3306/test";
    String Username = "root";
    String Password = "1234";
    String Driver = "com.mysql.jdbc.Driver";

    public DBManager() {
        try {
            Class.forName(Driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, Username, Password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Statement getStatement() {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            return stmt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getResultSet(String sql, boolean isQuery) {
        ResultSet rs = null;
        Statement stmt = null;
        stmt = getStatement();
        try {
            if (isQuery) {
                rs = stmt.executeQuery(sql);
            } else {
                stmt.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }
}
