package main.java.com.restaurante.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	private static final String URL = "jdbc:mysql://localhost:3306/sushiburrito_db?openTelemetry=DISABLED";
    private static final String USER = "sushiBurrito";
    private static final String PASSWORD = "SBDataBaseKey2025";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
