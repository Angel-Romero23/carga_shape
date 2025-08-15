package controller;

import entity.*;
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
import java.io.IOException;

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
        try {
            Node basesListas = FXMLLoader.load(getClass().getResource("/view/basesListas.fxml"));
            mostrarContenido2(basesListas);
        } catch (IOException e) {
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

    private void mostrarContenido2(Node contenido) {
        // Reemplazar el contenido del VBox central con la nueva vista
        contenidoCentral.getChildren().clear();
        contenidoCentral.getChildren().add(contenido);
    }
    @FXML
    private void home() throws Exception {
        Node casa = FXMLLoader.load(getClass().getResource("/view/basesListas.fxml"));
        mostrarContenido1(casa);
    }
    @FXML
    private void crearBD() throws Exception {
        Node createBase = FXMLLoader.load(getClass().getResource("/view/bases/crearBase.fxml"));
        mostrarContenido(createBase);
    }

    @FXML
    private void crearRemesa() throws Exception {
        Node createBase = FXMLLoader.load(getClass().getResource("/view/remesas/crearRemesa.fxml"));
        mostrarContenido(createBase);
    }

    @FXML
    private void listaRemesa() throws Exception {
        Node listaRemesa= FXMLLoader.load(getClass().getResource("/view/remesas/listaRemesas.fxml"));
        mostrarContenido1(listaRemesa);
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
