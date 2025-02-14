/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;
import server.LoggerUtil;

public class DatabaseConnection {

    private final static String CONNECTION_PATH = "jdbc:oracle:thin:@localhost:1521:XE";
    private final static String USERNAME = "hr";
    private final static String PASSWORD = "hr";
    static private  Connection con;

    private DatabaseConnection() {
    }

    static public void establishConnection() {
        try {
            DriverManager.registerDriver(new OracleDriver());
            LoggerUtil.info("Oracle JDBC driver registered successfully.");
            con = DriverManager.getConnection(CONNECTION_PATH, USERNAME, PASSWORD);
            LoggerUtil.info("Database connection established successfully ");
        } catch (SQLException ex) {
            LoggerUtil.error("Failed to establish database connection. Error: " + ex.getMessage());
        }

    }

    static public Connection getConnection() {
        if (con == null) {
            establishConnection();
        }
        return con;
    }

    public static void close() throws SQLException {
        if (con != null) {
            con.close();
            con = null;
        }
    }
}
