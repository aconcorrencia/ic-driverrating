package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
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
import android.usuario.driverrating.extra.ClassificadorEntradasSaidas;
import android.usuario.driverrating.extra.ClassificadorFuzzy;
import android.usuario.driverrating.extra.TratarVariaveisDimensoesClassificar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;


import static android.usuario.driverrating.DriverRatingActivity.ultimoLog;
import static android.usuario.driverrating.DriverRatingActivity.ultimaJanela;
import static android.usuario.driverrating.extra.ClassificadorEntradasSaidas.EntradasParaVelocidade;
import static android.usuario.driverrating.extra.SharedPreferencesKeys.JANELA_TEMPO;

/**
 * Created by Sillas and Nielson on 24/04/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth, IGPSReader, IOverpassReader, OnMapReadyCallback {

    private Veiculo veiculo;
    private DadosColetadosSensores dadosColetadosSensores;
    private long idPerfil;
    private Float cilindrada;
    private SharedPreferences sharedPreferences;

    private ImageView tvImage;
    private GoogleMap mMap;
    private Marker marker = null;

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

    TextView tvTituloCons,
            tvNotaCons,
            tvClassCons,

    tvTituloCO2,
            tvNotaCO2,
            tvClassCO2,
            tvCO2gkm,

    tvTituloVeloc,
            tvNotaVeloc,
            tvClassVeloc,
            tvNomeVia,
            tvVelocVia,
            tvVelocVeic,

    tvTituloAcelLong,
            tvNotaAcelLong,
            tvClassAcelLong,

    tvTituloAcelTrans,
            tvNotaAcelTrans,
            tvClassAcelTrans,

    tvLitros,
            tvTotLitros,
            tvDistPercor,
            tvMediaMotorista,
            tvNormalizCons,
            tvmaf;

    private int tipoCombustivel;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        btnEncerrarColetar = (Button) findViewById(R.id.btnEncerrarColetar);

        sharedPreferences = getSharedPreferences(SharedPreferencesKeys.DATABASE, MODE_PRIVATE);
        idPerfil = sharedPreferences.getLong(SharedPreferencesKeys.ID_USER, -1);
        String mac = sharedPreferences.getString(SharedPreferencesKeys.ADDRESS_DEVICE, "");

        DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
        veiculo = dataBasePerfis.selectPerfilById(idPerfil);
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3)) * 1000;

        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this, this);
        overpassReader = new OverpassReader(this);
        accReader = new ACCReader(this);

        tvImage = (ImageView) findViewById(R.id.tvImage);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        tvImage.startAnimation(rotation);

        setupTextViews();

        //Em Configurações no menu principal do aplicativo, a opção Janela de Tempo deverá possuir um valor informado, caso contrário levará o valor padrão de 300s.
        //Restaura as preferencias gravadas em janela de tempo para coletar os dados dos dispositivos.
        janelaTempo = sharedPreferences.getInt(SharedPreferencesKeys.JANELA_TEMPO, 300);

        /*//Verifica se o usuário não informou o valor da janela de tempo em segundos, então o aplicativo assume o valor de 300 segundos.
        if (janelaTempo == 0) {
            janelaTempo = 300;
        }*/

        //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
        tipoCombustivel = sharedPreferences.getInt(SharedPreferencesKeys.TIPO_COMBUSTIVEL, Utils.GASOLINA);

        if (tipoCombustivel == Utils.FLEX) {
            //Caso o tipo de combustível seja FLEX, deduz da quantidade de CO2 emitido pelo motorista o percentual de etanol.
            //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
            percentualAlcool = sharedPreferences.getInt(SharedPreferencesKeys.PERCENTUAL_ALCOOL, Utils.PERCENTUAL_ALCOOL_DEFAUT);
        }

        //Persiste na tabela de Logs da classificação. Gravando a Data e a Hora da Classificação.
        PersistirLog();

        //Buscar o id do log atual.
        DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(this);
        ultimoLog = dataBaseLogClassificacao.selectUltimoLog(idPerfil);

        dadosPercViag.clear();

        //Envia para o classificador, os dados de saída.
        ClassificadorEntradasSaidas.SaidasParaClassificador();

        //Restaura as preferencias gravadas em fator de penalização.
        //SharedPreferences recuperaSharedPercFP = getSharedPreferences(FATORPENALIZACAO_NAME, 0);
        //fatorPenalizacaoCO2 = recuperaSharedPercFP.getString(FATORPENALIZACAO_KEY, "");


    }

    public void setupTextViews() {
        tvTituloCons = (TextView) findViewById(R.id.textViewTituloCons);
        tvNotaCons = (TextView) findViewById(R.id.textViewNotaCons);
        tvClassCons = (TextView) findViewById(R.id.textViewClassCons);

        tvTituloCO2 = (TextView) findViewById(R.id.textViewTituloCO2);
        tvNotaCO2 = (TextView) findViewById(R.id.textViewNotaCO2);
        tvClassCO2 = (TextView) findViewById(R.id.textViewClassCO2);
        tvCO2gkm = (TextView) findViewById(R.id.textViewCO2);

        tvTituloVeloc = (TextView) findViewById(R.id.textViewTituloVeloc);
        tvNotaVeloc = (TextView) findViewById(R.id.textViewNotaVeloc);
        tvClassVeloc = (TextView) findViewById(R.id.textViewClassVeloc);

        tvTituloAcelLong = (TextView) findViewById(R.id.textViewTituloAcelLong);
        tvNotaAcelLong = (TextView) findViewById(R.id.textViewNotaAcelLong);
        tvClassAcelLong = (TextView) findViewById(R.id.textViewClassAcelLong);

        tvTituloAcelTrans = (TextView) findViewById(R.id.textViewTituloAcelTrans);
        tvNotaAcelTrans = (TextView) findViewById(R.id.textViewNotaAcelTrans);
        tvClassAcelTrans = (TextView) findViewById(R.id.textViewClassAcelTrans);

        //tvDistPercor = (TextView) findViewById(R.id.textViewDistancia);
        //tvMediaMotorista = (TextView) findViewById(R.id.textViewMdMotorista);
        //tvNormalizCons = (TextView) findViewById(R.id.textViewNormalizCons);
        //tvLitros = (TextView) findViewById(R.id.textViewLitros);
        tvTotLitros = (TextView) findViewById(R.id.textViewTotLitros);
        tvVelocVia = (TextView) findViewById(R.id.textViewVelocVia);
        tvVelocVeic = (TextView) findViewById(R.id.textViewVelocVeic);
        tvNomeVia = (TextView) findViewById(R.id.textViewNomeVia);
        //tvmaf = (TextView) findViewById(R.id.textViewMaf);
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

        //ACCUpdate();
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
    public void obdUpdate(OBDInfo obdinfo) {

        //********** Início - Consumo de Combustível **********:

        //Calcular litros de combustível
        litrosCombustivelConsumidos = Calculate.getFuelflow(obdinfo, cilindrada, tempoAtual - tempoAnteriorAuxiliar);

        //Armazena a última hora atual que será deduzida da próxima hora atual.
        tempoAnteriorAuxiliar = tempoAtual;

        //Totaliza a média de litros de combustíveis dentro da janela estabelecida de 300s.
        acumulaLitrosCombustivelConsumidos += litrosCombustivelConsumidos;

        if (litrosCombustivelConsumidos > 0) {
            tvLitros.setText("Litros de Combustível: " + new DecimalFormat("0.0000").format(litrosCombustivelConsumidos) + " ml");
            tvTotLitros.setText("Total de Litros: " + new DecimalFormat("0.0000").format(acumulaLitrosCombustivelConsumidos) + " ml");
        }

        // float testerpm;

        //teste = obdinfo.getRpm();

        if (obdinfo.getSpeed() != 0) {
            velocidadeMotorista = obdinfo.getSpeed();
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
        }

        tvNomeVia.setText("Nome da Via: " + overpassinfo.getName());
        tvVelocVia.setText("Vel. da Via: " + overpassinfo.getMaxspeed());
        tvVelocVeic.setText("Vel. do Veículo: " + velocidadeMotorista);
    }

    @Override
    public void errorConnectBluetooth() {
        Toast.makeText(IniciarClassificacao.this, "Erro ao conectar!", Toast.LENGTH_SHORT).show();
        gpsReader.start();
        accReader.start(ACCReader.NORMAL);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.lyAnimation).setVisibility(View.GONE);
        findViewById(R.id.lyMain).setVisibility(View.VISIBLE);
        //finish();
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

    }

    private boolean flag = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void GPSupdate(GPSInfo gpsInfo) {
        Log.w("OBD", "GPS");
        Toast.makeText(this, "GPS: " + gpsInfo.getAltitude(), Toast.LENGTH_SHORT).show();
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
            Log.w("OBD", "TEMPO = 0");
            //Guarda o último tempo.
            tempoAnterior = (int) hora.getTime();
            tempoAnteriorAuxiliar = tempoAnterior;
            //Guarda a última leitura do GPS a cada ciclo de X segundos.
            lastGPSInfo = gpsInfo;

        } else {
            Log.w("OBD", "TEMPO != 0");
            //Guarda o tempo atual.
            tempoAtual = (int) hora.getTime();
            //Os dois atributos abaixo são utilizados para medir a distância percorrida.
            Location lastLocation, currentLocation;
            lastLocation = lastGPSInfo.getLocation();
            currentLocation = gpsInfo.getLocation();

            //Guarda a longitude e latitude no arraylist e persiste após o fechamento de cada janela zerando-o para a próxima jelala se houver.
            dadosPercViag.add(gpsInfo.getLongitude());
            dadosPercViag.add(gpsInfo.getLatitude());

            //Faz a leitura dos dados do dispositivo OBD. Ativa o método obdUpdate.
            obdReader.read(OBDInfo.RPM | OBDInfo.SPEED | OBDInfo.INTAKE_PRESSURE | OBDInfo.INTAKE_TEMP | OBDInfo.ABS_LOAD);

            //Acumula a distância percorrida entre a última leitura e a atual.
            distanciaPercorrida += lastLocation.distanceTo(currentLocation);
            //tvDistPercor.setText("Distância Percorrida: " + new DecimalFormat("0.00").format(distanciaPercorrida) + " metros");

            // A cada change do GPS, a última localizaçãopassa a ser a atual. Recomeçando o ciclo novamente.
            lastGPSInfo = gpsInfo;

          /*
            Recupera as coordenadas, em um raio de 10m.

            Nessa etapa de classificação dos motoristas através da variável (dimensão) Velocidade, será executada da seguinte
            maneira: a nota será calculada a cada atualização do GPS, pois existe aqui a dependência instantânea da velocidade
            atual do motorista e da velocidade máxima da via onde o veículo está trafegando naquele momento. A nota será atualizada
            toda vez que o Classificador Fuzzy enviar uma nota inferior. O propósito é obter a nota mais baixa do motorista naquela
            viagem (percurso), pois, existe a possibilidade do motorista por conta da velocidade excessíva cometer um acidente.
          */
            overpassReader.read(gpsInfo.getLatitude(), gpsInfo.getLongitude(), 10);

            if (velocidadeMaximaDaVia != 0) {
                ClassificarVelocidadeParcial();

                tvNotaVeloc.setText("Nota: " + new DecimalFormat("0.00").format(notaVelocidade));
                tvClassVeloc.setText("Classificação: " + classificacaoVelocidade);
            }

            //Acessa o método responsável por armazenar os valores das acelerações longitudinal e transversal.
            ACCUpdate();

            // Monitora a janela de tempo:
            if ((tempoAtual - tempoAnterior) >= (janelaTempo * 1000)) {

                //send the tone to the "alarm" stream (classic beeps go there) with 200% volume
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); // 500 is duration in ms

                //Controlador de classificação
                //controleClassificacao += 1;

                // Persiste os dados colhidos do OBD-II, do GPS do SmartPhone e dos Acelerômetros a cada janela de tempo.
                //dadosColetadosSensoresArray.clear();
                //Último Registro
                PersistirDadosColhidos();

                //Buscar dados do veículo referentes ao ID do perfil do motorista.
                DataBasePerfis dataBasePerfis = new DataBasePerfis(IniciarClassificacao.this);
                veiculo = dataBasePerfis.selectPerfilById(idPerfil);

                //Buscar dados coletados do veículo referentes ao ID do perfil do motorista.
                DataBaseColetadosSensores dataBaseColetadosSensores = new DataBaseColetadosSensores(IniciarClassificacao.this);
                dadosColetadosSensores = dataBaseColetadosSensores.selectPerfilById(idPerfil);

                /** Abaixo faz-se uma verificação do acumulo de combustível, caso o mesmo seja diferente de o, a classificação de duas varíaveis,
                 *  Consumo de Combustível e Emissão de CO2 serão calculados.
                 */
                if (acumulaLitrosCombustivelConsumidos != 0.0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarConsumoDeCombustivel(dadosColetadosSensores, veiculo);
                    Double notaConsumoCombustivel = classificadorFuzzy.getNota();
                    String classificacaoConsumoCombustivel = classificadorFuzzy.getClasse();

                    tvNotaCons.setText("Nota: " + new DecimalFormat("0.00").format(notaConsumoCombustivel));
                    tvClassCons.setText("Classificação: " + classificacaoConsumoCombustivel);

                    //Classificação do motorista através da variável: "EMISSÃO DE ÓXIDO DE CARBONO"
                    classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarEmissaoCO2(dadosColetadosSensores, veiculo, percentualAlcool, -1);
                    Double notaEmissaoCO2 = classificadorFuzzy.getNota();
                    String classificacaoEmissaoCO2 = classificadorFuzzy.getClasse();

                    tvNotaCO2.setText("Nota: " + new DecimalFormat("0.00").format(notaEmissaoCO2));
                    tvClassCO2.setText("Classificação: " + classificacaoEmissaoCO2);

                }

                if (acclongitudinal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acclongitudinal);
                    Double notaAceleracaoLongitudinal = classificadorFuzzy.getNota();
                    String classificacaoAceleracaoLongitudinal = classificadorFuzzy.getClasse();
                    tvNotaAcelLong.setText("Nota: " + new DecimalFormat("0.00").format(notaAceleracaoLongitudinal));
                    tvClassAcelLong.setText("Classificação: " + classificacaoAceleracaoLongitudinal);
                }

                if (acctransversal != 0) {
                    ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarAceleracaoLongitudinal(acctransversal);
                    Double notaAceleracaoTransversal = classificadorFuzzy.getNota();
                    String classificacaoAceleracaoTransversal = classificadorFuzzy.getClasse();
                    tvNotaAcelTrans.setText("Nota: " + new DecimalFormat("0.00").format(notaAceleracaoTransversal));
                    tvClassAcelTrans.setText("Classificação: " + classificacaoAceleracaoTransversal);
                }

                // Controlador de número de janela.
                DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
                //ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela(ultimoLog);
                ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela();

                if (ultimaJanela == 0) {
                    numeroJanela = 1;
                } else {
                    numeroJanela = ultimaJanela + 1;
                }

                //Persiste os resultados das classificações a cada janela, com a identificação de percurso.
                PersistirClassificacoesMotorista();

                /** Persiste as coordenadas do percurso da janela atual. Deve ser feita após a persistencia
                 *  da classificação do motorista, pois, é necessário aguardar número atual da janela.
                 */

                PersistirPercursosViagem();

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

    private void ClassificarVelocidadeParcial() {

        //Informa para a classe Entradas, a velocidade máxima da via atual.
        //Classificar o motorista segundo a variável "Velocidade"
        /* Acumula a nota trazida pelo cálculo fuzzy, este acumulo terá uma média, onde fará uma nova classificação no método
        *  "ClassificaçãoVelocidade" após a janela de tempo ser fechada.
        */
        ClassificadorFuzzy classificadorFuzzy = TratarVariaveisDimensoesClassificar.classificarVelocidadeParcial(velocidadeMotorista,velocidadeMaximaDaVia);
        if (classificadorFuzzy.getNota()< menorNotaVelocidade) {
            menorNotaVelocidade = classificadorFuzzy.getNota();
            classeVelocidade = classificadorFuzzy.getClasse();

            Double notaVelocidade = menorNotaVelocidade;
            String classificacaoVelocidade = classeVelocidade;
        }
    }

    private void PersistirDadosColhidos() {

        // Será utilizado a base de dados colhidos para armazenamento a cada valor da janela de tempo estabelecida pelo motorista.
        DadosColetadosSensores dadosColetadosSensores = new DadosColetadosSensores();

        dadosColetadosSensores.setUsuario((int) idPerfil);
        dadosColetadosSensores.setDistanciaPercorrida(distanciaPercorrida);
        dadosColetadosSensores.setLitrosCombustivel(acumulaLitrosCombustivelConsumidos);
        dadosColetadosSensores.setTipoCombustivel(tipoCombustivel);

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

    private void PersistirClassificacoesMotorista() {

        // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.
        DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();

        dadosResultadosClassificacaoMotorista.setId_janclasmot(numeroJanela);
        dadosResultadosClassificacaoMotorista.setId_log(ultimoLog);
        dadosResultadosClassificacaoMotorista.setNota_cons_comb(notaConsumoCombustivel);
        dadosResultadosClassificacaoMotorista.setClas_cons_comb(classificacaoConsumoCombustivel);
        dadosResultadosClassificacaoMotorista.setNota_emis_co2(notaEmissaoCO2);
        dadosResultadosClassificacaoMotorista.setClas_emis_co2(classificacaoEmissaoCO2);
        dadosResultadosClassificacaoMotorista.setNota_velocid(notaVelocidade);
        dadosResultadosClassificacaoMotorista.setClas_velocid(classificacaoVelocidade);
        dadosResultadosClassificacaoMotorista.setNota_acel_long(notaAceleracaoLongitudinal);
        dadosResultadosClassificacaoMotorista.setClas_acel_long(classificacaoAceleracaoLongitudinal);
        dadosResultadosClassificacaoMotorista.setNota_acel_trans(notaAceleracaoTransversal);
        dadosResultadosClassificacaoMotorista.setClas_acel_trans(classificacaoAceleracaoTransversal);

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
