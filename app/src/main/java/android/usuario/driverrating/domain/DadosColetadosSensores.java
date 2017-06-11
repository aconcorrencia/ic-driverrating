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
    private int controleCalcVelocidade;
    private int contarClassificarVelocidadeBom;
    private int contarClassificarVelocidadeMedio;
    private int contarClassificarVelocidadeRuim;
    private Date data;
    private long hora;
    private double longitudeInicial;
    private double latidudeInicial;
    private double longitudeFinal;
    private double latidudeFinal;
    private String tipoCombustivel;

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

    public int getControleCalcVelocidade() {
        return controleCalcVelocidade;
    }

    public void setControleCalcVelocidade(int controleCalcVelocidade) {
        this.controleCalcVelocidade = controleCalcVelocidade;
    }

    public int getContarClassificarVelocidadeBom() {
        return contarClassificarVelocidadeBom;
    }

    public void setContarClassificarVelocidadeBom(int contarClassificarVelocidadeBom) {
        this.contarClassificarVelocidadeBom = contarClassificarVelocidadeBom;
    }

    public int getContarClassificarVelocidadeMedio() {
        return contarClassificarVelocidadeMedio;
    }

    public void setContarClassificarVelocidadeMedio(int contarClassificarVelocidadeMedio) {
        this.contarClassificarVelocidadeMedio = contarClassificarVelocidadeMedio;
    }

    public int getContarClassificarVelocidadeRuim() {
        return contarClassificarVelocidadeRuim;
    }

    public void setContarClassificarVelocidadeRuim(int contarClassificarVelocidadeRuim) {
        this.contarClassificarVelocidadeRuim = contarClassificarVelocidadeRuim;
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

    public double getLongitudeInicial() {
        return longitudeInicial;
    }

    public void setLongitudeInicial(double longitudeInicial) {
        this.longitudeInicial = longitudeInicial;
    }

    public double getLatidudeInicial() {
        return latidudeInicial;
    }

    public void setLatidudeInicial(double latidudeInicial) {
        this.latidudeInicial = latidudeInicial;
    }

    public double getLongitudeFinal() {
        return longitudeFinal;
    }

    public void setLongitudeFinal(double longitudeFinal) {
        this.longitudeFinal = longitudeFinal;
    }

    public double getLatidudeFinal() {
        return latidudeFinal;
    }

    public void setLatidudeFinal(double latidudeFinal) {
        this.latidudeFinal = latidudeFinal;
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(String tipoCombustivel) {
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
                ", controleCalcVelocidade=" + controleCalcVelocidade +
                ", contarClassificarVelocidadeBom=" + contarClassificarVelocidadeBom +
                ", contarClassificarVelocidadeMedio=" + contarClassificarVelocidadeMedio +
                ", contarClassificarVelocidadeRuim=" + contarClassificarVelocidadeRuim +
                ", data=" + data +
                ", hora=" + hora +
                ", longitudeInicial=" + longitudeInicial +
                ", latidudeInicial=" + latidudeInicial +
                ", longitudeFinal=" + longitudeFinal +
                ", latidudeFinal=" + latidudeFinal +
                ", tipoCombustivel='" + tipoCombustivel + '\'' +
                '}';
    }
}
