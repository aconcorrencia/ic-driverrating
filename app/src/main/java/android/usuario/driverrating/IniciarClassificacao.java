package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.usuario.driverrating.GPS.GPSInfo;
import android.usuario.driverrating.GPS.GPSReader;
import android.usuario.driverrating.GPS.IGPSReader;
import android.usuario.driverrating.OBD.IOBDBluetooth;
import android.usuario.driverrating.OBD.OBDInfo;
import android.usuario.driverrating.OBD.OBDReader;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.Veiculo;
import android.usuario.driverrating.extra.Calculate;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sillas on 24/04/2017.
 */

public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth,IGPSReader {

    private OBDReader obdReader;
    private Veiculo veiculo;
    private long idPerfil;
    private Float cilindrada;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;
    private BluetoothAdapter mBluetoothAdapter = null;
    private GPSReader gpsReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        idPerfil = sharedPreferences.getLong("ID", -1);
        String mac = sharedPreferences.getString("addressDevice", "");
        DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
        veiculo = dataBasePerfis.selectPerfilById(idPerfil);
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3));
        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this,this);
    }


    /***
     * Resposta da solicitação para ativar o bluetooth
     * Caso resultCode == Activity.RESULT_OK, inicia conexão
     * Caso contrário finaliza a acticity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            obdReader.connectDevice();
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        obdReader.start();
        gpsReader.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        obdReader.stop();
        gpsReader.stop();
    }

    @Override
    public void obdUpdate(OBDInfo obdinfo) {
        float fuel_consump = Calculate.getFuelConsump(obdinfo, cilindrada);
        Toast.makeText(IniciarClassificacao.this, "fuel_consump: "+fuel_consump, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorConnectBluetooth() {
        Toast.makeText(IniciarClassificacao.this, "Erro ao conectar!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void notSupportedBluetooth() {
        Toast.makeText(IniciarClassificacao.this, "Bluetooth não suportado!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void connectingBluetooth() {
        Toast.makeText(IniciarClassificacao.this, "Conectando...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bluetoothConnected() {
        Toast.makeText(IniciarClassificacao.this, "Conectado!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void GPSupdate(GPSInfo gpsinfo) {
        obdReader.read(OBDInfo.RPM | OBDInfo.SPEED | OBDInfo.INTAKE_PRESSURE | OBDInfo.INTAKE_TEMP);
    }
}
