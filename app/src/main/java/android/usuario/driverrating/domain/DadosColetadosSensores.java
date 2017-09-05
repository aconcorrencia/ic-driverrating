package android.usuario.driverrating.domain;

import java.util.Date;

/**
 * Created by NIELSON on 27/05/2017.
 */

public class DadosColetadosSensores {

    private int id;
    private int usuario;
    private float distanciaPercorrida;
    private float litrosCombustivel;
    private float notaVelocidade;
    private int tipoCombustivel;

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

    public float getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(float distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public float getLitrosCombustivel() {
        return litrosCombustivel;
    }

    public void setLitrosCombustivel(float litrosCombustivel) {
        this.litrosCombustivel = litrosCombustivel;
    }

    public float getNotaVelocidade() {
        return notaVelocidade;
    }

    public void setNotaVelocidade(float notaVelocidade) {
        this.notaVelocidade = notaVelocidade;
    }

    public int getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(int tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    @Override
    public String toString() {
        return "DadosColetadosSensores{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", distanciaPercorrida=" + distanciaPercorrida +
                ", litrosCombustivel=" + litrosCombustivel +
                ", notaVelocidade=" + notaVelocidade +
                ", tipoCombustivel='" + tipoCombustivel + '\'' +
                '}';
    }
}
