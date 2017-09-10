package android.usuario.driverrating;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.usuario.driverrating.database.DataBasePercursosViagem;
import android.usuario.driverrating.database.DataBaseResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class VerResultadoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<LatLng> mList;
    private Button btnMapa, btnResultado;

    private TextView
            tvNotaCons,
            tvNotaCO2,
            tvNotaVeloc,
            tvNotaAcelLong,
            tvNotaAcelTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_resultado);
        setupViews();
        int id = getIntent().getExtras().getInt("ID_LOG", -1);
        if (id == -1) {
            finish();
            return;
        }


        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.lyResultados).setVisibility(View.GONE);
                findViewById(R.id.map).setVisibility(View.VISIBLE);
            }
        });

        btnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.lyResultados).setVisibility(View.VISIBLE);
                findViewById(R.id.map).setVisibility(View.GONE);
            }
        });

        DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
        ArrayList<DadosResultadosClassificacaoMotorista> dadosResultadosClassificacaoMotorista = dataBaseResultadosClassificacaoMotorista.selectResultadosClassificacaoByIdLog(id);

        DadosResultadosClassificacaoMotorista resultadoMedia = dataBaseResultadosClassificacaoMotorista.selectMediaResultadosClassificacaoByIdLog(id);




        DataBasePercursosViagem dataBasePercursosViagem = new DataBasePercursosViagem(this);
        mList = dataBasePercursosViagem.selectPercusoByClassMot2(dadosResultadosClassificacaoMotorista);
        if (mList.size() == 0) {
            Toast.makeText(this, "Sem dados para mostrar!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        tvNotaCons.setText("Nota: " + resultadoMedia.getNota_cons_comb());
        tvNotaCO2.setText("Nota: " + resultadoMedia.getNota_emis_co2());
        tvNotaVeloc.setText("Nota: " + resultadoMedia.getNota_velocid());
        tvNotaAcelLong.setText("Nota: " + resultadoMedia.getNota_acel_long());
        tvNotaAcelTrans.setText("Nota: " + resultadoMedia.getNota_acel_trans());
    }

    public void setupViews() {
        tvNotaCons = (TextView) findViewById(R.id.textViewNotaCons);
        tvNotaCO2 = (TextView) findViewById(R.id.textViewNotaCO2);
        tvNotaVeloc = (TextView) findViewById(R.id.textViewNotaVeloc);
        tvNotaAcelLong = (TextView) findViewById(R.id.textViewNotaAcelLong);
        tvNotaAcelTrans = (TextView) findViewById(R.id.textViewNotaAcelTrans);
        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnResultado = (Button) findViewById(R.id.btnResultado);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // desabilita mapas indoor e 3D
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        // Configura elementos da interface gráfica
        UiSettings mapUI = mMap.getUiSettings();
        // habilita: pan, zoom, tilt, rotate
        mapUI.setAllGesturesEnabled(true);
        // habilita norte
        mapUI.setCompassEnabled(true);
        // habilita controle do zoom
        mapUI.setZoomControlsEnabled(true);

        for (LatLng l : mList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(l);
            markerOptions.title("Lat: " + l.latitude + " | Lng: " + l.longitude);
            mMap.addMarker(markerOptions);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mList.get(0), 17.f));
    }
}
