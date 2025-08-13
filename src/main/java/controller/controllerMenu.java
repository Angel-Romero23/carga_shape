package controller;

import entity.bases;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.TableView;
import models.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controllerMenu {
    @FXML
    private TableView<bases> tablaBases;

    @FXML
    private void initialize() {
        if (tablaBases != null) {
            // tablaBases.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            tablaBases.getColumns().removeIf(c -> c.getText() == null || c.getText().isEmpty());
            listaBases();
        }
    }
    @FXML
    public void listaBases() {
        ObservableList<bases> bases = FXCollections.observableArrayList();
        String sql = "select b.nom_base,b.tot_tablas,r.nom_remesa,op.nom_operacion ,op.descrip_oper \n" +
                "\tfrom admin.bases b\n" +
                "\tinner join admin.remesas r on b.remesa_id = r.id \n" +
                "\tinner join admin.operaciones op on b.status = op.num_operacion ;";
        try (Connection conn = conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nom_base");
                int total_tablas = rs.getInt("tot_tablas");
                String remesa = rs.getString("nom_remesa");
                String status = rs.getString("nom_operacion");
                String descrip = rs.getString("descrip_oper");

                bases.add(new bases(nombre,total_tablas,remesa,status,descrip));
            }

            tablaBases.setItems(bases);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}
