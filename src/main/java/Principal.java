import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.geotools.data.*;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
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
import org.geotools.data.DataStore;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Principal extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxml = getClass().getResource("/view/admin.fxml");
        if (fxml == null) {
            System.err.println("ERROR: El archivo FXML no se encontró.");
            return;
        }
        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Control de shapes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public String cargaShape(String archivo) throws IOException, FactoryException, TransformException {
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
        postgisParams.put("database", "geoloc_prueba");
        postgisParams.put("user", "postgres");
        postgisParams.put("passwd", "Angel2397.");

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
        }

        // Cerrar DataStores
        shpDataStore.dispose();
        postgisDataStore.dispose();

        System.out.println("✔ Shapefile cargado correctamente en PostgreSQL/PostGIS con geometrías transformadas a EPSG:4326.");

        return "hola";
    }
}
