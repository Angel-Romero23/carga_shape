package entity;

public class remesas {
    private int id;
    private String nomRemesa;
    private String fechaReg;

    public remesas(int id, String nomRemesa, String fechaReg) {
        this.id = id;
        this.nomRemesa = nomRemesa;
        this.fechaReg = fechaReg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomRemesa() {
        return nomRemesa;
    }

    public void setNomRemesa(String nomRemesa) {
        this.nomRemesa = nomRemesa;
    }

    public String getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(String fechaReg) {
        this.fechaReg = fechaReg;
    }
}
