package funciones;

import models.basecon;

import java.sql.*;

public class funciones {
    public int guardaBase(String base,int total,String usu, String pas){
        int estado = 0;
        String url = "jdbc:postgresql://localhost:5433/gestion_bases";
        String sql="insert into admin.bases(nom_base,tot_tablas,status,remesa_id,fecha_registro,fecha_update) values (?,?,1,(select max(id) from admin.remesas),now(),now())";
        basecon conexion2 = new basecon(url, usu, pas);
        try {
            Connection conn2 = conexion2.conectar();
            PreparedStatement stmt = conn2.prepareStatement(sql);
            stmt.setString(1,base);
            stmt.setInt(2,total);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return estado;
    }

    public int actualizaEstado(int idbase,String base,String usu,String pas){
        int estado = 0,totalTablas=0;

        String urltabla= "jdbc:postgresql://localhost:5433/"+base;
        String sqlcount = "SELECT COUNT(*)\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = 'bged'\n" +
                "  AND table_type ='BASE TABLE';";
        String url = "jdbc:postgresql://localhost:5433/gestion_bases";
        String sql="update admin.bases set tot_tablas = ?, status=2 where id = ?;";
        basecon conexion = new basecon(urltabla, usu, pas);
        try (Connection conn = conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlcount);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalTablas = rs.getInt(1);
                basecon conexion2 = new basecon(url, usu, pas);
                Connection conn2 = conexion2.conectar();
                PreparedStatement stmt2 = conn2.prepareStatement(sql);
                stmt2.setInt(1,totalTablas);
                stmt2.setInt(2,idbase);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return estado;
    }
}
