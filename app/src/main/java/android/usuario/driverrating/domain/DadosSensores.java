package android.usuario.driverrating.domain;

/**
 * Created by NIELSON on 06/12/2016.
 */

public class DadosSensores {
    private int id;
    private int id_user;
    private Double fluxoComb;
    private Double velocid;
    private Double consumoComb;
    //private Date dataOcorrencia;
    //private Date horaOcorrencia;
    //private Double velocidadeMedia;


    public DadosSensores() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public Double getFluxoComb() {
        return fluxoComb;
    }

    public void setFluxoComb(Double fluxoComb) {
        this.fluxoComb = fluxoComb;
    }

    public Double getVelocid() {
        return velocid;
    }

    public void setVelocid(Double velocid) {
        this.velocid = velocid;
    }

    public Double getConsumoComb() {
        return consumoComb;
    }

    public void setConsumoComb(Double consumoComb) {
        this.consumoComb = consumoComb;
    }

    @Override
    public String toString() {
        return "DadosSensores{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", fluxoComb=" + fluxoComb +
                ", velocid=" + velocid +
                ", consumoComb=" + consumoComb +
                /*", dataOcorrencia=" + dataOcorrencia +
                ", horaOcorrencia=" + horaOcorrencia +*/
                '}';
    }
}
