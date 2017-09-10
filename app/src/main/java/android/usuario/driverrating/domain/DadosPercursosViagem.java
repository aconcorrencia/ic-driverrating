package android.usuario.driverrating.domain;

/**
 * Created by NIELSON on 22/06/2017.
 */

public class DadosPercursosViagem {
    private int id;
    private long id_janelaclassmot;
    private double longitude;
    private double latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId_janelaclassmot() {
        return id_janelaclassmot;
    }

    public void setId_janelaclassmot(long id_janelaclassmot) {
        this.id_janelaclassmot = id_janelaclassmot;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "DadosPercursosViagem{" +
                "id=" + id +
                ", id_janelaclassmot=" + id_janelaclassmot +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
