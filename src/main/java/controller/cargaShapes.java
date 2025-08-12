package controller;

import entity.archivoInfo;
import entity.bases;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.basecon;
import models.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class cargaShapes {
    @FXML
    private TableView<bases> tablaBases;
    @FXML
    private TableColumn<archivoInfo, String> colStatus;
    @FXML
    private TableColumn<archivoInfo, String> colNombrebd;
    @FXML
    private VBox contenidoCentral;
    @FXML
    private TableColumn<archivoInfo, Integer> colTablas;
    @FXML
    private TableColumn<archivoInfo, String> colRemesa;
    @FXML
    private TableColumn<archivoInfo, String> colDesc;

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
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = hoy.format(formato);
        ObservableList<bases> bases = FXCollections.observableArrayList();
        String sql = "select b.nom_base,b.tot_tablas,r.nom_remesa,st.nom_status,st.descrip \n" +
                "\tfrom admin.bases b\n" +
                "\tinner join admin.remesas r on b.remesa_id = r.id \n" +
                "\tinner join admin.estados st on b.status = st.id ;";
        try (Connection conn = conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nom_base");
                int total_tablas = rs.getInt("tot_tablas");
                String remesa = rs.getString("nom_remesa");
                String status = rs.getString("nom_status");
                String descrip = rs.getString("descrip");

               bases.add(new bases(nombre,total_tablas,remesa,status,descrip));
            }

            tablaBases.setItems(bases);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void cargaShapes() throws Exception {
        Node cargaShape = FXMLLoader.load(getClass().getResource("/view/shapes/cargarShape.fxml"));
        mostrarContenido1(cargaShape);
    }
    private void mostrarContenido1(Node contenido) {
        // Reemplazar el contenido del VBox central con la nueva vista
        contenidoCentral.getChildren().clear();
        contenidoCentral.getChildren().add(contenido);
    }
    @FXML
    private void home() throws Exception {
        Node casa = FXMLLoader.load(getClass().getResource("/view/admin.fxml"));
        mostrarContenido1(casa);
    }
    @FXML
    private void crearBD() throws Exception {
        Node createBase = FXMLLoader.load(getClass().getResource("/view/bases/crearBase.fxml"));
        mostrarContenido(createBase);
    }

    private void mostrarContenido(Node contenido) {
        contenidoCentral.getChildren().clear();

        if (contenido instanceof Region) {
            Region region = (Region) contenido;
            region.setMaxWidth(300);
            region.setMaxHeight(220);
        }

        StackPane wrapper = new StackPane(contenido);
        wrapper.setMaxSize(300, 220);

        contenidoCentral.getChildren().add(wrapper);

        VBox.setVgrow(wrapper, Priority.NEVER);
        contenidoCentral.setAlignment(Pos.TOP_CENTER);
    }


}
