package entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class archivoInfo {
    private final String nombre;
    private final long tamanio;
    private final String ruta;
    private final StringProperty status;
    private final BooleanProperty seleccionado;

    public archivoInfo(String nombre, long tamanio, String ruta) {
        this.nombre = nombre;
        this.tamanio = tamanio;
        this.ruta = ruta;
        this.seleccionado = new SimpleBooleanProperty(false);
        this.status = new SimpleStringProperty("");
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public String getNombre() {
        return nombre;
    }

    public long getTamanio() {
        return tamanio;
    }

    public String getRuta() {
        return ruta;
    }

    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public StringProperty statusProperty() {
        return status;
    }
}
