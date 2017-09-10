package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.usuario.driverrating.extra.SharedPreferencesKeys;
import android.usuario.driverrating.extra.Utils;
import android.util.Log;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.usuario.driverrating.ACC.ACCInfo;
import android.usuario.driverrating.ACC.ACCReader;
import android.usuario.driverrating.GPS.GPSInfo;
import android.usuario.driverrating.GPS.GPSReader;
import android.usuario.driverrating.GPS.IGPSReader;
import android.usuario.driverrating.GPS.IOverpassReader;
import android.usuario.driverrating.GPS.OverpassInfo;
import android.usuario.driverrating.GPS.OverpassReader;
import android.usuario.driverrating.OBD.IOBDBluetooth;
import android.usuario.driverrating.OBD.OBDInfo;
import android.usuario.driverrating.OBD.OBDReader;
import android.usuario.driverrating.database.DataBaseColetadosSensores;
import android.usuario.driverrating.database.DataBaseLogClassificacao;
import android.usuario.driverrating.database.DataBasePercursosViagem;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.database.DataBaseResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.DadosColetadosSensores;
import android.usuario.driverrating.domain.DadosLogClassificacao;
import android.usuario.driverrating.domain.DadosPercursosViagem;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.Veiculo;
import android.usuario.driverrating.extra.Calculate;
import android.usuario.driverrating.extra.ClassificadorFuzzy;
import android.usuario.driverrating.extra.TratarVariaveisDimensoesClassificar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.usuario.driverrating.extra.Utils.getDate;
import static android.usuario.driverrating.extra.Utils.getHour;


/**
 * Created by Sillas and Nielson on 24/04/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth, IGPSReader, IOverpassReader, OnMapReadyCallback {

    private Veiculo veiculo;
    private long idPerfil;
    private Float cilindrada;

    private ImageView tvImage;
    private GoogleMap mMap;
    private Marker marker = null;

    private float fatorPenalizacaoCO2;
    private int percentualAlcool = 0;

    //Leitura das classe responsáveis por acessar os dispositivos: OBD e GPS e também ler dados disponibilizados.
    private BluetoothAdapter mBluetoothAdapter = null;
    private OBDReader obdReader;  //Escutador do OBD
    private GPSReader gpsReader;  // Escutador do GPS
    private ACCReader accReader;  //Escutador dos acelerômetros
    private OverpassReader overpassReader; //Requisições da base à base de dados do Open Street Map (OSM)

    //Armazena dados coletados referentes aos sensores
    /*ArrayList<DadosColetadosSensores> dadosColetadosSensoresArray = new ArrayList<>();
    DataBaseColetadosSensores dataBaseColetadosSensores = new DataBaseColetadosSensores(IniciarClassificacao.this);*/

    //**********--> Atributos referentes à variável: consumo de combustíveis:
    private float litrosCombustivelConsumidos = 0;
    private float acumulaLitrosCombustivelConsumidos = 0;
    //**********

    //**********--> Atributos referentes à variável: Emissão de CO2:
    private float quilogramasPorSegundosCO2 = 0;
    private float acumulaQuilogramasPorSegundosCO2 = 0;

    //**********

    //**********--> Atributos referentes à variável: Velocidade:
    private int velocidadeMotorista = -1;
    private int velocidadeMaximaDaVia = 0;
    private double menorNotaVelocidade = 1000;
    private String classeVelocidade = "";
    //**********


    //**********--> Atributos referentes à variável: Accelerações:
    private float acclongitudinal = 0;
    private float acctransversal = 0;
    //**********

    //**********--> Atributos gerais:
    private int tempoAnterior = 0;
    private int tempoAtual = 0;
    private int tempoAnteriorAuxiliar = 0;
    private int janelaTempo;

    private float distanciaPercorrida = 0;
    private GPSInfo lastGPSInfo = null;
    private boolean start = true;
    //***********

    private TextView
            tvNotaCons,
            tvClassCons,

    tvNotaCO2,
            tvClassCO2,

    tvNotaVeloc,
            tvClassVeloc,

    tvNotaAcelLong,
            tvClassAcelLong,


    tvNotaAcelTrans,
            tvClassAcelTrans,
            tvTotLitros,
            tvCO2gkm,
            tvNomeVia,
            tvVelocVia,
            tvVelocVeic;


    private TextView txtLeituras, txtVelocidadeVia, txtRpm;
    private int tipoCombustivel;
    private int leituras = 0;
    private ProgressiveGauge speedometer;
    private long idLog;
    boolean map = true;
    boolean sound = true;
    private boolean flag = true;
    private MenuItem volume, earth_box,encerrar;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setSubtitle("t");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.DATABASE, MODE_PRIVATE);

        idPerfil = sharedPreferences.getLong(SharedPreferencesKeys.ID_USER, -1);
        tipoCombustivel = sharedPreferences.getInt(SharedPreferencesKeys.TIPO_COMBUSTIVEL, Utils.GASOLINA);
        fatorPenalizacaoCO2 = sharedPreferences.getFloat(SharedPreferencesKeys.FATOR_PENALIZACAO, -1);
        janelaTempo = sharedPreferences.getInt(SharedPreferencesKeys.JANELA_TEMPO, Utils.JANELA_DEFAUT);
        String mac = sharedPreferences.getString(SharedPreferencesKeys.ADDRESS_DEVICE, "");


        DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
        veiculo = dataBasePerfis.selectPerfilById(idPerfil);
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3)) * 1000;

        setupViews();

        if (tipoCombustivel == Utils.FLEX) {
            //Caso o tipo de combustível seja FLEX, deduz da quantidade de CO2 emitido pelo motorista o percentual de etanol.
            //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
            percentualAlcool = sharedPreferences.getInt(SharedPreferencesKeys.PERCENTUAL_ALCOOL, Utils.PERCENTUAL_ALCOOL_DEFAUT);
        }

        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this, this);
        overpassReader = new OverpassReader(this);
        accReader = new ACCReader(this);

    }

    public void setupViews() {
        txtLeituras = (TextView) findViewById(R.id.txtLeituras);
        txtVelocidadeVia = (TextView) findViewById(R.id.txtVelocidadeVia);
        txtRpm = (TextView) findViewById(R.id.txtRpm);
        speedometer = (ProgressiveGauge) findViewById(R.id.progressiveGauge);

        tvImage = (ImageView) findViewById(R.id.tvImage);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        tvImage.startAnimation(rotation);

        //TextViews Main 2
        tvNotaCons = (TextView) findViewById(R.id.textViewNotaCons);
        tvClassCons = (TextView) findViewById(R.id.textViewClassCons);

        tvNotaCO2 = (TextView) findViewById(R.id.textViewNotaCO2);
        tvClassCO2 = (TextView) findViewById(R.id.textViewClassCO2);

        tvNotaVeloc = (TextView) findViewById(R.id.textViewNotaVeloc);
        tvClassVeloc = (TextView) findViewById(R.id.textViewClassVeloc);

        tvNotaAcelLong = (TextView) findViewById(R.id.textViewNotaAcelLong);
        tvClassAcelLong = (TextView) findViewById(R.id.textViewClassAcelLong);

        tvNotaAcelTrans = (TextView) findViewById(R.id.textViewNotaAcelTrans);
        tvClassAcelTrans = (TextView) findViewById(R.id.textViewClassAcelTrans);

        tvTotLitros = (TextView) findViewById(R.id.textViewTotLitros);
        tvCO2gkm = (TextView) findViewById(R.id.textViewCO2);
        tvNomeVia = (TextView) findViewById(R.id.textViewNomeVia);
        tvVelocVia = (TextView) findViewById(R.id.textViewVelocVia);
        tvVelocVeic = (TextView) findViewById(R.id.textViewVelocVeic);

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
        /*gpsReader.start();
        accReader.start(ACCReader.NORMAL);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        persistirLog();
        findViewById(R.id.lyAnimation).setVisibility(View.GONE);
        findViewById(R.id.lyMain).setVisibility(View.VISIBLE);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        obdReader.stop();
        gpsReader.stop();
        accReader.stop();
    }

    /**
     * Através do presente método "obdUpdate", será colhido e classificados dados de diversos sensores do dispositivo OBD,
     * referentes às seguintes variáveis (dimensões): Consumo de combustível e a Emissão do gás Óxido de carbono (CO2).
     *
     * @param obdinfo
     */
    @Override
    public void obdUpdate(OBDInfo obdinfo, boolean ler) {


        if (ler) {
            //********** Início - Consumo de Combustível **********:
            //Calcular litros de combustível
            litrosCombustivelConsumidos = Calculate.getFuelflow(obdinfo, cilindrada, tempoAtual - tempoAnteriorAuxiliar, TratarVariaveisDimensoesClassificar.getDensityFuel(tipoCombustivel));

            //Armazena a última hora atual que será deduzida da próxima hora atual.
            tempoAnteriorAuxiliar = tempoAtual;

            //Totaliza a média de litros de combustíveis dentro da janela estabelecida de 300s.
            acumulaLitrosCombustivelConsumidos += litrosCombustivelConsumidos;

            if (litrosCombustivelConsumidos > 0) {
                //tvLitros.setText("Litros de Combustível: " +litrosCombustivelConsumidos + " ml");
                tvTotLitros.setText("Total de Litros: " + acumulaLitrosCombustivelConsumidos + " ml");
            }

            if (obdinfo.getSpeed() != 0) {
                velocidadeMotorista = obdinfo.getSpeed();
            }
            //********** Final - Consumo Combustível **********
        } else {
            speedometer.speedTo(obdinfo.getSpeed(), 500);
            txtRpm.setText("" + obdinfo.getRpm());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obdReader.read(OBDInfo.RPM | OBDInfo.SPEED, false);
                }
            }, 2000);
            tvVelocVeic.setText("Vel. do Veículo: " + obdinfo.getSpeed() + " Km/h");
        }

    }

    public void ACCUpdate() {
        ACCInfo accInfo = accReader.read();

        float acclong = Math.abs(accInfo.getAccLongitudinal());
        float acctrans = Math.abs(accInfo.getAccTranversal());

        if (acclong > acclongitudinal) acclongitudinal = acclong;

        if (acctrans > acctransversal) acctransversal = acctrans;
    }

    @Override
    public void overpassupdate(OverpassInfo overpassinfo) {
        if (!overpassinfo.getMaxspeed().equals("unknown")) {
            velocidadeMaximaDaVia = Integer.parseInt(overpassinfo.getMaxspeed());
            txtVelocidadeVia.setText(velocidadeMaximaDaVia + " Km/h");
            tvVelocVia.setText("Vel. da Via: " + overpassinfo.getMaxspeed());
        } else {
            velocidadeMaximaDaVia = 0;
            txtVelocidadeVia.setText("X");
            tvVelocVia.setText("Vel. da Via: Desconhecida");
        }

        if (!overpassinfo.getName().equals("unknown"))
            tvNomeVia.setText("Nome da Via: " + overpassinfo.getName());
        else
            tvNomeVia.setText("Nome da Via: Desconhecida");
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

    }

    @Override
    public void bluetoothConnected() {
        Toast.makeText(IniciarClassificacao.this, "Conectado!", Toast.LENGTH_SHORT).show();
        persistirLog();
        volume.setVisible(true);
        earth_box.setVisible(true);
        encerrar.setVisible(true);
        gpsReader.start();
        accReader.start(ACCReader.NORMAL);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.lyAnimation).setVisibility(View.GONE);
        findViewById(R.id.lyMain).setVisibility(View.VISIBLE);
        obdReader.read(OBDInfo.RPM, false);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void GPSupdate(GPSInfo gpsInfo) {

        marker.setVisible(true);
        LatLng myLocation;
        myLocation = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
        marker.setPosition(myLocation);
        if (flag) {
            flag = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.f));
        } else {
            CameraPosition cam = mMap.getCameraPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, cam.zoom));
        }

        if (!start) {
            finish();
        }

        Date hora = new Date();

        if (tempoAnterior == 0.0) {
            tempoAnterior = (int) hora.getTime();
            tempoAnteriorAuxiliar = tempoAnterior;
            lastGPSInfo = gpsInfo;

        } else {
            tempoAtual = (int) hora.getTime();
            Location lastLocation, currentLocation;
            lastLocation = lastGPSInfo.getLocation();
            currentLocation = gpsInfo.getLocation();

            obdReader.read(OBDInfo.RPM | OBDInfo.SPEED | OBDInfo.INTAKE_PRESSURE | OBDInfo.INTAKE_TEMP | OBDInfo.ABS_LOAD, true); //Faz a leitura dos dados do dispositivo OBD. Ativa o método obdUpdate.

            distanciaPercorrida += lastLocation.distanceTo(currentLocation); //Acumula a distância percorrida entre a última leitura e a atual.
            lastGPSInfo = gpsInfo;
            overpassReader.read(gpsInfo.getLatitude(), gpsInfo.getLongitude(), 10);

            if (velocidadeMaximaDaVia != 0) {
                ClassificadorFuzzy classificadorFuzzy = classificarVelocidadeParcial();
                populaTextView(tvNotaVeloc, tvClassVeloc, classificadorFuzzy);
            }

            ACCUpdate();

            if ((tempoAtual - tempoAnterior) >= (janelaTempo * 1000)) {
                leituras++;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude()));
                markerOptions.title("Leitura: "+leituras);

                mMap.addMarker(markerOptions);
                txtLeituras.setText("" + leituras);
                if (sound) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); // 500 is duration in ms
                }
                DadosColetadosSensores dadosColetadosSensores = populaDadosColetadosSensores();
                //persistirDadosColhidos(dadosColetadosSensores); Não irá persistir esses dados

                DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();
                dadosResultadosClassificacaoMotorista.setId_log(idLog);

                if (dadosColetadosSensores.getLitrosCombustivel() != 0.0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarConsumoDeCombustivel(dadosColetadosSensores, veiculo);
                    dadosResultadosClassificacaoMotorista.setNota_cons_comb(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_cons_comb(classificadorFuzzy.getClasse());
                    populaTextView(tvNotaCons, tvClassCons, classificadorFuzzy);

                    //Classificação do motorista através da variável: "EMISSÃO DE ÓXIDO DE CARBONO"
                    classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarEmissaoCO2(dadosColetadosSensores, veiculo, percentualAlcool, fatorPenalizacaoCO2);
                    dadosResultadosClassificacaoMotorista.setNota_emis_co2(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_emis_co2(classificadorFuzzy.getClasse());
                    populaTextView(tvNotaCO2, tvClassCO2, classificadorFuzzy);
                }
                if (acclongitudinal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acclongitudinal);
                    dadosResultadosClassificacaoMotorista.setNota_acel_long(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_acel_long(classificadorFuzzy.getClasse());
                    populaTextView(tvNotaAcelLong, tvClassAcelLong, classificadorFuzzy);
                }

                if (acctransversal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acctransversal);
                    dadosResultadosClassificacaoMotorista.setNota_acel_trans(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_acel_trans(classificadorFuzzy.getClasse());
                    populaTextView(tvNotaAcelTrans, tvClassAcelTrans, classificadorFuzzy);
                }

                long idClassifMot = persistirClassificacoesMotorista(dadosResultadosClassificacaoMotorista);

                DadosPercursosViagem dadosPercursosViagem = new DadosPercursosViagem();
                dadosPercursosViagem.setId_janelaclassmot(idClassifMot);
                dadosPercursosViagem.setLatitude(gpsInfo.getLatitude());
                dadosPercursosViagem.setLongitude(gpsInfo.getLongitude());
                persistirPercursosViagem(dadosPercursosViagem);
                //Zera os atributos acumuladores dentro da janela.
                distanciaPercorrida = 0;
                acumulaLitrosCombustivelConsumidos = 0;
                tempoAnterior = 0;
                tempoAtual = 0;
                tempoAnteriorAuxiliar = 0;
                menorNotaVelocidade = 1000;
                classeVelocidade = "";
                acclongitudinal = 0;
                acctransversal = 0;
            }
        }
    }

    private void populaTextView(TextView nota, TextView classe, ClassificadorFuzzy classificadorFuzzy) {
        if (classificadorFuzzy != null) {
            nota.setText("Nota: " + classificadorFuzzy.getNota());
            classe.setText("Classificação: " + classificadorFuzzy.getClasse());
        } else {
            nota.setText("Nota: X");
            classe.setText("Classificação: X");
        }
    }

    private ClassificadorFuzzy classificarVelocidadeParcial() {

        //Informa para a classe Entradas, a velocidade máxima da via atual.
        //Classificar o motorista segundo a variável "Velocidade"
        /* Acumula a nota trazida pelo cálculo fuzzy, este acumulo terá uma média, onde fará uma nova classificação no método
        *  "ClassificaçãoVelocidade" após a janela de tempo ser fechada.
        */
        ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarVelocidadeParcial(velocidadeMotorista, velocidadeMaximaDaVia);
        if (classificadorFuzzy.getNota() < menorNotaVelocidade) {
            menorNotaVelocidade = classificadorFuzzy.getNota();
            classeVelocidade = classificadorFuzzy.getClasse();

            classificadorFuzzy.setNota(menorNotaVelocidade);
            classificadorFuzzy.setClasse(classeVelocidade);
        } else {
            classificadorFuzzy = null;
        }
        return classificadorFuzzy;
    }

    private DadosColetadosSensores populaDadosColetadosSensores() {
        DadosColetadosSensores dadosColetadosSensores = new DadosColetadosSensores();
        dadosColetadosSensores.setUsuario((int) idPerfil);
        dadosColetadosSensores.setDistanciaPercorrida(distanciaPercorrida);
        dadosColetadosSensores.setLitrosCombustivel(acumulaLitrosCombustivelConsumidos);
        dadosColetadosSensores.setTipoCombustivel(tipoCombustivel);
        return dadosColetadosSensores;
    }

    private void persistirDadosColhidos(DadosColetadosSensores dadosColetadosSensores) {
        DataBaseColetadosSensores dataBaseColetadosSensores = new DataBaseColetadosSensores(IniciarClassificacao.this);
        dataBaseColetadosSensores.inserirDadosColetadosSensores(dadosColetadosSensores);
    }

    private void persistirPercursosViagem(DadosPercursosViagem dadosPercursosViagem) {
        DataBasePercursosViagem dataBasePercursosViagem = new DataBasePercursosViagem(IniciarClassificacao.this);
        dataBasePercursosViagem.inserirDadosPercursosViagem(dadosPercursosViagem);

    }

    private long persistirClassificacoesMotorista(DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista) {

        // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.

        DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(IniciarClassificacao.this);
        return dataBaseResultadosClassificacaoMotorista.inserirDadosResultClassMot(dadosResultadosClassificacaoMotorista);
    }

    public void btnEncerrarColetar(View view) {
        // Encerra a coleta de dados nos dispositivos.
        start = false;
        Toast.makeText(IniciarClassificacao.this, "Classificação Geral", Toast.LENGTH_SHORT).show();

        // Retorna para a tela anterior.
        finish();

    }

    // Será utilizado a base de dados para armazenamento dos logs de classificação.
    private void persistirLog() {
        DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(IniciarClassificacao.this);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        DadosLogClassificacao dadosLogClassificacao = new DadosLogClassificacao();
        dadosLogClassificacao.setIdPerfil(idPerfil);
        dadosLogClassificacao.setData(getDate(cal));
        dadosLogClassificacao.setHora(getHour(cal));
        idLog = dataBaseLogClassificacao.inserirDadosLogClassificacao(dadosLogClassificacao);
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

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(0, 0));
        markerOptions.title("Minha Localização");
        marker = mMap.addMarker(markerOptions);
        marker.setVisible(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_iniciar_class, menu);
        volume = menu.findItem(R.id.volume);
        earth_box= menu.findItem(R.id.earth_box);
        encerrar= menu.findItem(R.id.encerrar);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.earth_box:
                if (map) {
                    item.setIcon(R.drawable.earth_box_off);
                    findViewById(R.id.lyMain).setVisibility(View.GONE);
                    findViewById(R.id.lyMain2).setVisibility(View.VISIBLE);
                    map = false;
                } else {
                    item.setIcon(R.drawable.earth_box);
                    findViewById(R.id.lyMain).setVisibility(View.VISIBLE);
                    findViewById(R.id.lyMain2).setVisibility(View.GONE);
                    map = true;
                }
                break;
            case R.id.volume:
                if (sound) {
                    item.setIcon(R.drawable.volume_off);
                    sound = false;
                } else {
                    item.setIcon(R.drawable.volume_on);
                    sound = true;
                }
                break;
            case R.id.encerrar:
                encerrarClassificacao();
                break;

            case android.R.id.home:
                alertEncerrarClassificacao();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        alertEncerrarClassificacao();
    }

    public void alertEncerrarClassificacao() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("Deseja encerrar classificação?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        encerrarClassificacao();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void encerrarClassificacao() {
        start = false;
        Toast.makeText(IniciarClassificacao.this, "Classificação Encerrada.", Toast.LENGTH_SHORT).show();
        finish();
    }


}
