package android.usuario.driverrating.domain;

import java.util.Date;

/**
 * Created by NIELSON on 22/06/2017.
 */

public class DadosLogClassificacao {

    private int id;
    private long idPerfil;
    private String data;
    private String hora;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(long idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "DadosLogClassificacao{" +
                "id=" + id +
                ", idPerfil=" + idPerfil +
                ", data='" + data + '\'' +
                ", hora='" + hora + '\'' +
                '}';
    }
}
