package android.usuario.driverrating.GPS;

import android.location.Location;

/**
 * Created by Jorge on 09/03/2017.
 */

public class GPSInfo {
    private Location lastKnownLocation;

    public GPSInfo(Location lastKnownLocation) {
        this.lastKnownLocation=lastKnownLocation;
    }
    public void setLocation (Location lastKnownLocation) {
        this.lastKnownLocation=lastKnownLocation;
    }
    public Location getLocation () {
        return this.lastKnownLocation;
    }
    public long getTime() {
        return lastKnownLocation.getTime();
    }

    public int getSpeed() {
        return (int)lastKnownLocation.getSpeed();
    }

    public double getAltitude() {
        return lastKnownLocation.getAltitude();
    }

    public double getLatitude() {
        return lastKnownLocation.getLatitude();
    }

    public double getLongitude() {
        return lastKnownLocation.getLongitude();
    }

    public float getBearing() {
        return lastKnownLocation.getBearing();
    }

    public float getAccuracy() {
        return lastKnownLocation.getAccuracy();
    }
}
