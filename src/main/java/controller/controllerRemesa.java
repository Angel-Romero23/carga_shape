package controller;

import entity.remesas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.basecon;
import models.conexion;

import java.sql.*;

public class controllerRemesa {
    @FXML
    private TextField campoRemesa;
    @FXML
    private TextField campoUsuario;
    @FXML
    private TextField campoPass;
    @FXML
    private TableView<remesas> tablaRemesas;

    @FXML
    private void initialize() {
        if (tablaRemesas != null) {
            // tablaBases.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            tablaRemesas.getColumns().removeIf(c -> c.getText() == null || c.getText().isEmpty());
            listaRemesas();
        }
    }
    @FXML
    private void guardarRem() {
        String bd = campoRemesa.getText().trim();
        String usu = campoUsuario.getText().trim();
        String pas = campoPass.getText().trim();
        String url = "jdbc:postgresql://localhost:5433/gestion_bases";
        basecon conexion = new basecon(url,usu,pas);
        String createrem = "insert into admin.remesas (nom_remesa,fecha) values(?,now());";
        try {
            Connection conn2 = conexion.conectar();
            PreparedStatement stmt = conn2.prepareStatement(createrem);
            stmt.setString(1,bd);
            stmt.executeUpdate();
            mostrarAlerta("Exito","Remesa creada Correctamente");
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos:");
            e.printStackTrace();
            mostrarAlerta("Error", "Error en la conexión.");
        } finally {
            conexion.desconectar();
        }
    }

    @FXML
    public void listaRemesas() {
        ObservableList<remesas> remesas = FXCollections.observableArrayList();
        String sql = "select * from admin.remesas r;";
        try (Connection conn = conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String fecha = rs.getString("fecha");
                int remesa_id = rs.getInt("id");
                String remesa = rs.getString("nom_remesa");

                remesas.add(new remesas(remesa_id,remesa,fecha));
            }

            tablaRemesas.setItems(remesas);

        } catch (SQLException e) {
            e.printStackTrace();
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
