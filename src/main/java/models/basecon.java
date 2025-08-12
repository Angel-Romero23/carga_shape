package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class basecon {

    private String url;
    private String usuario;
    private String contrasena;

    private Connection conexion;

    public basecon(String url, String usuario, String contrasena) {
        this.url = url;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public Connection conectar() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión exitosa a la base de datos");
        }
        return conexion;
    }

    public Connection conectarges() throws SQLException {
        String url = "jdbc:postgresql://localhost:5433/gestion_bases";
        String usuario = "postgres";
        String contrasena = "Angel2397.";
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión exitosa a la base de datos");
        }
        return conexion;
    }

    public Connection conectar2() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión exitosa a la base de datos");
        }
        return conexion;
    }


    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
