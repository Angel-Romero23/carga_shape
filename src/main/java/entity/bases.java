package entity;

public class bases {
    String nombrebase;
    Integer tottablas;
    String remesa;
    String status;
    String descrip;

    public bases(String nombrebase, Integer tottablas, String remesa, String status, String descrip) {
        this.nombrebase = nombrebase;
        this.tottablas = tottablas;
        this.remesa = remesa;
        this.status = status;
        this.descrip = descrip;
    }

    public String getNombrebase() {
        return nombrebase;
    }

    public void setNombrebase(String nombrebase) {
        this.nombrebase = nombrebase;
    }

    public Integer getTottablas() {
        return tottablas;
    }

    public void setTottablas(Integer tottablas) {
        this.tottablas = tottablas;
    }

    public String getRemesa() {
        return remesa;
    }

    public void setRemesa(String remesa) {
        this.remesa = remesa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
}
