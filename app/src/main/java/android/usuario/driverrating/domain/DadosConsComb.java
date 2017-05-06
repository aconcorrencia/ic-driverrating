package android.usuario.driverrating.domain;

import java.sql.Time;
import java.util.Date;

/**
 * Created by NIELSON on 28/04/2017.
 */

public class DadosConsComb {
    private int id;
    private Double fuelconsump;
    private Double fuelflow;
    private Date data;
    private long hora;
    private double longitude;
    private double latidude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getFuelconsump() {
        return fuelconsump;
    }

    public void setFuelconsump(Double fuelconsump) {
        this.fuelconsump = fuelconsump;
    }

    public Double getFuelflow() {
        return fuelflow;
    }

    public void setFuelflow(Double fuelflow) {
        this.fuelflow = fuelflow;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatidude() {
        return latidude;
    }

    public void setLatidude(double latidude) {
        this.latidude = latidude;
    }

    @Override
    public String toString() {
        return "DadosConsComb{" +
                "id=" + id +
                ", fuelconsump=" + fuelconsump +
                ", fuelflow=" + fuelflow +
                ", data=" + data +
                ", hora=" + hora +
                ", longitude=" + longitude +
                ", latidude=" + latidude +
                '}';
    }
}
