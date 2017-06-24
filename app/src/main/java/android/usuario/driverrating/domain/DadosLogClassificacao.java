package android.usuario.driverrating.domain;

import java.util.Date;

/**
 * Created by NIELSON on 22/06/2017.
 */

public class DadosLogClassificacao {
    private int id;
    private int usuario;
    private Date data;
    private long hora;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "DadosLogClassificacao{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", data=" + data +
                ", hora=" + hora +
                '}';
    }
}
