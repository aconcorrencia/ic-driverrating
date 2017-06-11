package android.usuario.driverrating.GPS;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.usuario.driverrating.R;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CollectActivity extends FragmentActivity implements OnMapReadyCallback, IGPSReader, IOverpassReader {

    private GoogleMap mMap;
    private GPSReader gpsreader;
    private OverpassReader overpassReader;
    TextView tvVia,tvVelVia,tvVelVeic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gpsreader=new GPSReader(this,this);
        //overpassReader=new OverpassReader(this);
        tvVia=(TextView)findViewById(R.id.textViewVia);
        tvVelVia=(TextView)findViewById(R.id.textViewVelVia);
        tvVelVeic=(TextView)findViewById(R.id.textViewVelVeic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gpsreader.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsreader.stop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-12.985, -38.450);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Mestrado em Sistemas e Computação"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void GPSupdate(GPSInfo gpsinfo) {
        overpassReader.read(gpsinfo.getLatitude(),gpsinfo.getLongitude(),50);
        if (mMap!=null) {
            String coordsbndbox="Coordenadas:"+ gpsinfo.getLatitude()+","+gpsinfo.getLongitude()+"\n";
            mMap.clear();
            LatLng location=new LatLng(gpsinfo.getLatitude(),gpsinfo.getLongitude());
            mMap.addMarker(new MarkerOptions().position(location).title("Coordenadas GPS").snippet(coordsbndbox));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( location, 15.0f ));
            tvVelVeic.setText("Vel. da veiculo:"+gpsinfo.getSpeed()+"km/h");
        }
    }

    @Override
    public void overpassupdate(OverpassInfo overpassinfo) {
        tvVia.setText("Via:"+overpassinfo.getName());
        tvVelVia.setText("Vel. da via:"+overpassinfo.getMaxspeed()+"km/h");

    }
}
