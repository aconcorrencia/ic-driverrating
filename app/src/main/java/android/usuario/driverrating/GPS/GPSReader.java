package android.usuario.driverrating.GPS;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Created by Jorge on 09/03/2017.
 */

public class GPSReader implements LocationListener {
    private IGPSReader listener;            // escutador a ser notificado com a nova localização
    private LocationManager locManager;     // O Gerente de localização
    private LocationProvider locProvider;   // O Provedor de localização

    public GPSReader(Context context, IGPSReader listener) {
        this.listener=listener;
        locManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
        locProvider = locManager.getProvider(LocationManager.GPS_PROVIDER);
    }
    public void start() {
        if (locManager.isProviderEnabled(locProvider.getName())) {
            try {
                locManager.requestLocationUpdates(locProvider.getName(), 5000, 1, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop() {
        if (locManager.isProviderEnabled(locProvider.getName())) {
            try {
                locManager.removeUpdates(this);
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        listener.GPSupdate(new GPSInfo(location));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
