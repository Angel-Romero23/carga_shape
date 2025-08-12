package controller;

import entity.archivoInfo;
import entity.baseInfo;
import funciones.funciones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.conexion;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class controllerShape {
    @FXML
    private TableView<archivoInfo> tablaArchivos;
    @FXML
    private TableColumn<archivoInfo, String> colStatus;
    @FXML
    private TableColumn<archivoInfo, String> colNombre;
    @FXML
    private ComboBox<baseInfo> comboBD;
    @FXML
    private TableColumn<archivoInfo, Long> colTamanio;
    @FXML
    private TableColumn<archivoInfo, Boolean> colSeleccionado;
    @FXML
    private TableColumn<archivoInfo, String> colRuta;

    @FXML
    private TextField campoBD;
    @FXML
    private TextField campoUsuario;
    @FXML
    private TextField campoPass;

    @FXML
    private void initialize() {
        // Enlazar columnas con propiedades del modelo
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTamanio.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
        colRuta.setCellValueFactory(new PropertyValueFactory<>("ruta"));
        colSeleccionado.setCellValueFactory(new PropertyValueFactory<>("seleccionado"));
        colSeleccionado.setCellFactory(tc -> new javafx.scene.control.cell.CheckBoxTableCell<>());
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<archivoInfo, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    if (status.equals("✔")) {
                        setTextFill(javafx.scene.paint.Color.GREEN);
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else if (status.equals("✘")) {
                        setTextFill(javafx.scene.paint.Color.RED);
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK);
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        CheckBox checkBoxHeader = new CheckBox();
        checkBoxHeader.setOnAction(e -> {
            boolean seleccionado = checkBoxHeader.isSelected();
            for (archivoInfo item : tablaArchivos.getItems()) {
                item.setSeleccionado(seleccionado);
            }
        });


        colSeleccionado.setGraphic(checkBoxHeader);
        colSeleccionado.setEditable(true);
        tablaArchivos.setEditable(true);
        cargarBases();
    }

    private void cargarBases() {
        ObservableList<baseInfo> listaBases = FXCollections.observableArrayList();
        String sql = "select * from admin.bases;";
        try (Connection conn = conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nom_base");
                listaBases.add(new baseInfo(id, nombre));
            }

            comboBD.setItems(listaBases);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void listaArchivos(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecciona una carpeta");

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File carpeta = directoryChooser.showDialog(stage);
        ObservableList<archivoInfo> lista = FXCollections.observableArrayList();

        if (carpeta != null && carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isFile() && archivo.getName().toLowerCase().endsWith(".shp")) {
                        lista.add(new archivoInfo(
                                archivo.getName(),
                                archivo.length(),
                                archivo.getAbsolutePath()
                        ));
                    }
                }
            }
            tablaArchivos.setItems(lista);
        } else {
            System.out.println("No se seleccionó una carpeta válida");
        }
    }

    @FXML
    private void cargarSeleccionados() {
        String usu = campoUsuario.getText().trim();
        String pas = campoPass.getText().trim();
        ObservableList<archivoInfo> archivos = tablaArchivos.getItems();
        baseInfo seleccion = comboBD.getSelectionModel().getSelectedItem();
        int idBase=0;
        String nombreBase="";

        if (seleccion != null) {
            idBase = seleccion.getId();
            nombreBase = seleccion.getNombre();
        for (archivoInfo archivo : archivos) {
            if (archivo.isSeleccionado()) {
                try {
                    cargaShape(archivo.getRuta(),nombreBase,usu,pas);
                    archivo.setStatus("✔️");
                } catch (Exception e) {
                    archivo.setStatus("❌");
                    System.err.println("❌ Error al cargar: " + archivo.getNombre());
                }
            }
        }
        System.out.println("✔ Archivos seleccionados cargados.");
        } else {
            System.out.println("No se ha seleccionado ninguna base de datos.");

        }
    }
    @FXML
    private void cargarTodos() {
        String usu = campoUsuario.getText().trim();
        String pas = campoPass.getText().trim();
        ObservableList<archivoInfo> archivos = tablaArchivos.getItems();
        baseInfo seleccion = comboBD.getSelectionModel().getSelectedItem();
        int idBase=0;
        String nombreBase="";

        if (seleccion != null) {
            idBase = seleccion.getId();
            nombreBase = seleccion.getNombre();
            if (archivos.isEmpty()) {
                System.out.println("No hay archivos para cargar.");
                return;
            }
            for (archivoInfo archivo : archivos) {
                try {
                    cargaShape(archivo.getRuta(),nombreBase,usu,pas);
                    archivo.setStatus("✔️");
                } catch (Exception e) {
                    archivo.setStatus("❌");
                    System.err.println("❌ Error al cargar: " + archivo.getNombre());
                }
            }
            funciones fun = new funciones();
            fun.actualizaEstado(idBase,nombreBase,usu,pas);

            System.out.println("✔ Todos los archivos SHP han sido cargados.");
        } else {
            System.out.println("No se ha seleccionado ninguna base de datos.");

        }
    }
    @FXML
    private void cargaShape(String archivo,String nombreBase,String usu,String pas) throws IOException, FactoryException, TransformException {
        baseInfo seleccion = comboBD.getSelectionModel().getSelectedItem();

        if (nombreBase.isEmpty() || usu.isEmpty() || pas.isEmpty()){
            mostrarAlerta("Error", "Completa las credenciales.");
        }else {
            File file = new File(archivo);
            // Crear DataStore para leer el SHP
            Map<String, Serializable> shpParams = new HashMap<>();
            shpParams.put("url", file.toURI().toURL());
            shpParams.put("charset", StandardCharsets.UTF_8.name());

            ShapefileDataStoreFactory shpFactory = new ShapefileDataStoreFactory();
            ShapefileDataStore shpDataStore = (ShapefileDataStore) shpFactory.createDataStore(shpParams);
            shpDataStore.setCharset(StandardCharsets.UTF_8);

            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);

            // Obtener CRS original y definir CRS destino EPSG:4326
            SimpleFeatureType schema = featureSource.getSchema();
            CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326", true);
            // Crear transformador de coordenadas
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            // Construir nuevo schema con CRS EPSG:4326
            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.init(schema);
            builder.setCRS(targetCRS);
            SimpleFeatureType schema4326 = builder.buildFeatureType();

            // Parámetros para conexión a PostGIS
            Map<String, Object> postgisParams = new HashMap<>();
            postgisParams.put("dbtype", "postgis");
            postgisParams.put("host", "localhost");
            postgisParams.put("port", 5433);
            postgisParams.put("schema", "bged");
            postgisParams.put("database", nombreBase);
            postgisParams.put("user", usu);
            postgisParams.put("passwd", pas);

            DataStore postgisDataStore = DataStoreFinder.getDataStore(postgisParams);
            if (postgisDataStore == null) {
                throw new RuntimeException("No se pudo conectar a la base de datos PostGIS.");
            }

            // Crear tabla con schema que tiene CRS EPSG:4326
            postgisDataStore.createSchema(schema4326);

            String tableName = schema4326.getTypeName();
            FeatureStore<SimpleFeatureType, SimpleFeature> targetStore =
                    (FeatureStore<SimpleFeatureType, SimpleFeature>) postgisDataStore.getFeatureSource(tableName);

            // Leer features del shapefile y transformarlas
            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         postgisDataStore.getFeatureWriterAppend(tableName, Transaction.AUTO_COMMIT)) {

                try (FeatureIterator<SimpleFeature> features = collection.features()) {
                    while (features.hasNext()) {
                        SimpleFeature feature = features.next();
                        SimpleFeature newFeature = writer.next();

                        for (int i = 0; i < feature.getAttributeCount(); i++) {
                            Object attr = feature.getAttribute(i);

                            if (attr instanceof org.locationtech.jts.geom.Geometry) {
                                org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) attr;
                                org.locationtech.jts.geom.Geometry geom4326 = JTS.transform(geom, transform);

                                newFeature.setAttribute(i, geom4326);
                            } else {
                                newFeature.setAttribute(i, attr);
                            }
                        }
                        writer.write();
                    }
                }
                // Cerrar DataStores
                shpDataStore.dispose();
                postgisDataStore.dispose();

                System.out.println("✔ Shapefile cargado correctamente en PostgreSQL/PostGIS con geometrías transformadas a EPSG:4326.");
            }
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
