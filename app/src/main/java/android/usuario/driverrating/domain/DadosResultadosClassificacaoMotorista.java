package android.usuario.driverrating.domain;

import java.util.Date;

/**
 * Created by NIELSON on 23/06/2017.
 */

public class DadosResultadosClassificacaoMotorista {
    private int id_janclasmot;
    private int id_log;

    private double nota_cons_comb;
    private String clas_cons_comb;

    private double nota_emis_co2;
    private String clas_emis_co2;

    private double nota_velocid;
    private String clas_velocid;

    private double nota_acel_long;
    private String clas_acel_long;

    private double nota_acel_trans;
    private String clas_acel_trans;

    public int getId_janclasmot() {
        return id_janclasmot;
    }

    public void setId_janclasmot(int id_janclasmot) {
        this.id_janclasmot = id_janclasmot;
    }

    public int getId_log() {
        return id_log;
    }

    public void setId_log(int id_log) {
        this.id_log = id_log;
    }

    public double getNota_cons_comb() {
        return nota_cons_comb;
    }

    public void setNota_cons_comb(double nota_cons_comb) {
        this.nota_cons_comb = nota_cons_comb;
    }

    public String getClas_cons_comb() {
        return clas_cons_comb;
    }

    public void setClas_cons_comb(String clas_cons_comb) {
        this.clas_cons_comb = clas_cons_comb;
    }

    public double getNota_emis_co2() {
        return nota_emis_co2;
    }

    public void setNota_emis_co2(double nota_emis_co2) {
        this.nota_emis_co2 = nota_emis_co2;
    }

    public String getClas_emis_co2() {
        return clas_emis_co2;
    }

    public void setClas_emis_co2(String clas_emis_co2) {
        this.clas_emis_co2 = clas_emis_co2;
    }

    public double getNota_velocid() {
        return nota_velocid;
    }

    public void setNota_velocid(double nota_velocid) {
        this.nota_velocid = nota_velocid;
    }

    public String getClas_velocid() {
        return clas_velocid;
    }

    public void setClas_velocid(String clas_velocid) {
        this.clas_velocid = clas_velocid;
    }

    public double getNota_acel_long() {
        return nota_acel_long;
    }

    public void setNota_acel_long(double nota_acel_long) {
        this.nota_acel_long = nota_acel_long;
    }

    public String getClas_acel_long() {
        return clas_acel_long;
    }

    public void setClas_acel_long(String clas_acel_long) {
        this.clas_acel_long = clas_acel_long;
    }

    public double getNota_acel_trans() {
        return nota_acel_trans;
    }

    public void setNota_acel_trans(double nota_acel_trans) {
        this.nota_acel_trans = nota_acel_trans;
    }

    public String getClas_acel_trans() {
        return clas_acel_trans;
    }

    public void setClas_acel_trans(String clas_acel_trans) {
        this.clas_acel_trans = clas_acel_trans;
    }

    @Override
    public String toString() {
        return "DadosResultadosClassificacaoMotorista{" +
                "id_janclasmot=" + id_janclasmot +
                ", id_log=" + id_log +
                ", nota_cons_comb=" + nota_cons_comb +
                ", clas_cons_comb='" + clas_cons_comb + '\'' +
                ", nota_emis_co2=" + nota_emis_co2 +
                ", clas_emis_co2='" + clas_emis_co2 + '\'' +
                ", nota_velocid=" + nota_velocid +
                ", clas_velocid='" + clas_velocid + '\'' +
                ", nota_acel_long=" + nota_acel_long +
                ", clas_acel_long='" + clas_acel_long + '\'' +
                ", nota_acel_trans=" + nota_acel_trans +
                ", clas_acel_trans='" + clas_acel_trans + '\'' +
                '}';
    }
}
