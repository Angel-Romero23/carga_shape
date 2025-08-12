package controller;

import funciones.funciones;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.basecon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class controllerBase {
    @FXML
    private TextField campoBD;
    @FXML
    private TextField campoUsuario;
    @FXML
    private TextField campoPass;

    @FXML
    private void guardarBD() throws Exception {
        String bd = campoBD.getText().trim();
        String usu = campoUsuario.getText().trim();
        String pas = campoPass.getText().trim();
        String url = "jdbc:postgresql://localhost:5433/postgres";
        basecon conexion = new basecon(url,usu,pas);
        String createbd = "create database "+bd;
        try {
            Connection conn = conexion.conectar();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createbd);
            System.out.println("Base de datos creada: " + bd);
            String url2 = "jdbc:postgresql://localhost:5433/"+bd;
            String createexten = "create extension postgis; create schema bged;";
            basecon conexion2 = new basecon(url2, usu, pas);
            try {

                Connection conn2 = conexion2.conectar2();
                Statement stmt2 = conn2.createStatement();
                stmt2.executeUpdate(createexten);
                System.out.println("se creo la extension y el schema");
                mostrarAlerta("Éxito", "La base de datos fue creada correctamente.");
                funciones fun = new funciones();
                fun.guardaBase(bd,0,usu,pas);

                campoBD.clear();
                campoUsuario.clear();
                campoPass.clear();
            }catch (SQLException e){
                System.err.println("error al crear la extension y schema");
                mostrarAlerta("Error", "No se pudo crear la base de datos.");
            }finally {
                conexion2.desconectar();
            }
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos:");
            e.printStackTrace();
            mostrarAlerta("Error", "Error en la conexión.");
        } finally {
            conexion.desconectar();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Sin encabezado
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
