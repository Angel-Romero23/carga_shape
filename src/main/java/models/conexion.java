package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    private static final String URL = "jdbc:postgresql://localhost:5433/gestion_bases";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Angel2397.";

    public static Connection conectar() throws SQLException, SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
