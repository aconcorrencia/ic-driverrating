package android.usuario.driverrating.domain;


import android.graphics.Bitmap;

/**
 * Created by NIELSON on 26/11/2016.
 */

public class Veiculo {
    private int id;
    private String nomeUsuario;
    private Bitmap foto;
    private String marca;
    private String modelo;
    private String motor;
    private String versao;
    private Double nox;
    private Double co2;
    private Double etanolCidade;
    private Double etanolEstrada;
    private Double gasolinaCidade;
    private Double gasolinaEstrada;

    public Veiculo() {
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public Double getNox() {
        return nox;
    }

    public void setNox(Double nox) {
        this.nox = nox;
    }

    public Double getCo2() {
        return co2;
    }

    public void setCo2(Double co2) {
        this.co2 = co2;
    }

    public Double getEtanolCidade() {
        return etanolCidade;
    }

    public void setEtanolCidade(Double etanolCidade) {
        this.etanolCidade = etanolCidade;
    }

    public Double getEtanolEstrada() {
        return etanolEstrada;
    }

    public void setEtanolEstrada(Double etanolEstrada) {
        this.etanolEstrada = etanolEstrada;
    }

    public Double getGasolinaCidade() {
        return gasolinaCidade;
    }

    public void setGasolinaCidade(Double gasolinaCidade) {
        this.gasolinaCidade = gasolinaCidade;
    }

    public Double getGasolinaEstrada() {
        return gasolinaEstrada;
    }

    public void setGasolinaEstrada(Double gasolinaEstrada) {
        this.gasolinaEstrada = gasolinaEstrada;
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "id=" + id +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", motor='" + motor + '\'' +
                ", versao='" + versao + '\'' +
                ", nox=" + nox +
                ", co2=" + co2 +
                ", etanolCidade=" + etanolCidade +
                ", etanolEstrada=" + etanolEstrada +
                ", gasolinaCidade=" + gasolinaCidade +
                ", gasolinaEstrada=" + gasolinaEstrada +
                '}';
    }
}
