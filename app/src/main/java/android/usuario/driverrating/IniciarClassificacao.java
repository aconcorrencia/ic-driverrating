package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
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
import java.util.Date;


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
    private int contarClassVelocMedio = 0;
    private int contarClassVelocRuim = 0;
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
    private int acumulaTempo = 0;

    private int numeroLog;
    private int numeroJanela;

    private float distanciaPercorrida = 0;
    private GPSInfo lastGPSInfo = null;
    private boolean start = true;
    //***********

    ArrayList<Double> dadosPercViag = new ArrayList<>();

    Button btnEncerrarColetar;

    TextView txtLeituras, txtVelocidadeVia,txtRpm;

    private int tipoCombustivel;
    private int leituras = 0;
    private ProgressiveGauge speedometer;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        btnEncerrarColetar = (Button) findViewById(R.id.btnEncerrarColetar);

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

        //Persiste na tabela de Logs da classificação. Gravando a Data e a Hora da Classificação.
        //PersistirLog();

        //Buscar o id do log atual.
        //DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(this);
        //ultimoLog = dataBaseLogClassificacao.selectUltimoLog(idPerfil);




        //Restaura as preferencias gravadas em fator de penalização.



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
  /*      gpsReader.start();
        accReader.start(ACCReader.NORMAL);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //********** Início - Consumo de Combustível **********:
        if (ler) {
            //Calcular litros de combustível
            litrosCombustivelConsumidos = Calculate.getFuelflow(obdinfo, cilindrada, tempoAtual - tempoAnteriorAuxiliar, TratarVariaveisDimensoesClassificar.getDensityFuel(tipoCombustivel));

            //Armazena a última hora atual que será deduzida da próxima hora atual.
            tempoAnteriorAuxiliar = tempoAtual;

            //Totaliza a média de litros de combustíveis dentro da janela estabelecida de 300s.
            acumulaLitrosCombustivelConsumidos += litrosCombustivelConsumidos;

            if (litrosCombustivelConsumidos > 0) {

                //tvLitros.setText("Litros de Combustível: " +litrosCombustivelConsumidos + " ml");
                //tvTotLitros.setText("Total de Litros: " +acumulaLitrosCombustivelConsumidos + " ml");
            }

            // float testerpm;

            //teste = obdinfo.getRpm();

            if (obdinfo.getSpeed() != 0) {
                velocidadeMotorista = obdinfo.getSpeed();
            }
        }else{
            speedometer.speedTo(obdinfo.getSpeed(), 2000);
            txtRpm.setText(""+obdinfo.getRpm());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obdReader.read(OBDInfo.RPM|OBDInfo.SPEED, false);
                }
            }, 2000);

        }
        //********** Final - Consumo Combustível **********
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
        } else {
            velocidadeMaximaDaVia = 0;
            txtVelocidadeVia.setText("X");
        }
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
        gpsReader.start();
        accReader.start(ACCReader.NORMAL);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.lyAnimation).setVisibility(View.GONE);
        findViewById(R.id.lyMain).setVisibility(View.VISIBLE);
        obdReader.read(OBDInfo.RPM, false);
    }

    private boolean flag = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void GPSupdate(GPSInfo gpsInfo) {

        marker.setVisible(true);
        LatLng myLocation;
        myLocation = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
        marker.setPosition(myLocation);
        if (flag) {
            flag = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.f));
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
            dadosPercViag.add(gpsInfo.getLongitude());
            dadosPercViag.add(gpsInfo.getLatitude());

            obdReader.read(OBDInfo.RPM | OBDInfo.SPEED | OBDInfo.INTAKE_PRESSURE | OBDInfo.INTAKE_TEMP | OBDInfo.ABS_LOAD, true); //Faz a leitura dos dados do dispositivo OBD. Ativa o método obdUpdate.

            distanciaPercorrida += lastLocation.distanceTo(currentLocation); //Acumula a distância percorrida entre a última leitura e a atual.
            lastGPSInfo = gpsInfo;
            overpassReader.read(gpsInfo.getLatitude(), gpsInfo.getLongitude(), 10);

            ACCUpdate();

            if ((tempoAtual - tempoAnterior) >= (janelaTempo)) {
                leituras++;
                txtLeituras.setText("" + leituras);
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); // 500 is duration in ms

                DadosColetadosSensores dadosColetadosSensores = populaDadosColetadosSensores();
                persistirDadosColhidos(dadosColetadosSensores);

                DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();


                if (dadosColetadosSensores.getLitrosCombustivel() != 0.0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarConsumoDeCombustivel(dadosColetadosSensores, veiculo);
                    dadosResultadosClassificacaoMotorista.setNota_cons_comb(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_cons_comb(classificadorFuzzy.getClasse());

                    //Classificação do motorista através da variável: "EMISSÃO DE ÓXIDO DE CARBONO"
                    classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarEmissaoCO2(dadosColetadosSensores, veiculo, percentualAlcool, fatorPenalizacaoCO2);
                    dadosResultadosClassificacaoMotorista.setNota_emis_co2(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_emis_co2(classificadorFuzzy.getClasse());
                }

                if (acclongitudinal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acclongitudinal);
                    dadosResultadosClassificacaoMotorista.setNota_acel_long(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_acel_long(classificadorFuzzy.getClasse());
                }

                if (acctransversal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acctransversal);
                    dadosResultadosClassificacaoMotorista.setNota_acel_trans(classificadorFuzzy.getNota());
                    dadosResultadosClassificacaoMotorista.setClas_acel_trans(classificadorFuzzy.getClasse());
                }

        /*        // Controlador de número de janela.
                DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
                //ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela(ultimoLog);
                ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela();

                if (ultimaJanela == 0) {
                    numeroJanela = 1;
                } else {
                    numeroJanela = ultimaJanela + 1;
                }
                dadosResultadosClassificacaoMotorista.setId_janclasmot(numeroJanela);
                dadosResultadosClassificacaoMotorista.setId_log(ultimoLog);
                Toast.makeText(this, "CONS: " + dadosResultadosClassificacaoMotorista.getClas_cons_comb(), Toast.LENGTH_SHORT).show();*/
                //Persiste os resultados das classificações a cada janela, com a identificação de percurso.
                //persistirClassificacoesMotorista(dadosResultadosClassificacaoMotorista);

                /** Persiste as coordenadas do percurso da janela atual. Deve ser feita após a persistencia
                 *  da classificação do motorista, pois, é necessário aguardar número atual da janela.
                 */

                //PersistirPercursosViagem();

                //Zera os atributos acumuladores dentro da janela.
                distanciaPercorrida = 0;
                acumulaLitrosCombustivelConsumidos = 0;
                tempoAnterior = 0;
                tempoAtual = 0;
                tempoAnteriorAuxiliar = 0;

                menorNotaVelocidade = 1000;

                classeVelocidade = "";
                contarClassVelocMedio = 0;
                contarClassVelocRuim = 0;

                acumulaTempo = 0;

                acclongitudinal = 0;
                acctransversal = 0;

                dadosPercViag.clear();

            }
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

    private void PersistirPercursosViagem() {
        for (int i = 0; i < dadosPercViag.size(); i++) {

            // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.
            DadosPercursosViagem dadosPercursosViagem = new DadosPercursosViagem();

            dadosPercursosViagem.setId_janelaclassmot(numeroJanela);
            dadosPercursosViagem.setLongitude(dadosPercViag.get(0));
            dadosPercursosViagem.setLatitude(dadosPercViag.get(1));

            DataBasePercursosViagem dataBasePercursosViagem = new DataBasePercursosViagem(IniciarClassificacao.this);
            dataBasePercursosViagem.inserirDadosPercursosViagem(dadosPercursosViagem);
        }
    }

    private void persistirClassificacoesMotorista(DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista) {

        // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.

        DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(IniciarClassificacao.this);
        dataBaseResultadosClassificacaoMotorista.inserirDadosResultClassMot(dadosResultadosClassificacaoMotorista);
    }

    public void btnEncerrarColetar(View view) {
        // Encerra a coleta de dados nos dispositivos.
        start = false;
        Toast.makeText(IniciarClassificacao.this, "Classificação Geral", Toast.LENGTH_SHORT).show();

        // Retorna para a tela anterior.
        finish();

    }

    // Será utilizado a base de dados para armazenamento dos logs de classificação.
    private void PersistirLog() {

        DadosLogClassificacao dadosLogClassificacao = new DadosLogClassificacao();

        dadosLogClassificacao.setUsuario((int) idPerfil);
        Date dataOcorrencia = new Date();
        dadosLogClassificacao.setData(dataOcorrencia);
        dadosLogClassificacao.setHora(dataOcorrencia.getTime());

        DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(IniciarClassificacao.this);
        dataBaseLogClassificacao.inserirDadosLogClassificacao(dadosLogClassificacao);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady", Toast.LENGTH_SHORT).show();
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
}
