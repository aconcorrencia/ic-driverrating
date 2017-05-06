package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.usuario.driverrating.GPS.GPSInfo;
import android.usuario.driverrating.GPS.GPSReader;
import android.usuario.driverrating.GPS.IGPSReader;
import android.usuario.driverrating.GPS.IOverpassReader;
import android.usuario.driverrating.GPS.OverpassInfo;
import android.usuario.driverrating.GPS.OverpassReader;
import android.usuario.driverrating.OBD.IOBDBluetooth;
import android.usuario.driverrating.OBD.OBDInfo;
import android.usuario.driverrating.OBD.OBDReader;
import android.usuario.driverrating.database.DataBaseConsComb;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.DadosConsComb;
import android.usuario.driverrating.domain.DadosConsCombBuilder;
import android.usuario.driverrating.domain.Veiculo;
import android.usuario.driverrating.extra.Calculate;
import android.usuario.driverrating.extra.ClassificadorEntradasSaidas;
import android.usuario.driverrating.extra.ClassificadorFuzzy;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_NAME;
import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_KEY;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;

import static java.lang.String.valueOf;

/**
 * Created by Sillas and Nielson on 24/04/2017.
 */

public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth,IGPSReader,IOverpassReader {

    private Veiculo veiculo;
    private long idPerfil;
    private Float cilindrada;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;
    private SharedPreferences sharedJanelaTempo;

    //Leitura das classe responsáveis por acessar os dispositivos: OBD e GPS e também ler dados disponibilizados.
    private BluetoothAdapter mBluetoothAdapter = null;
    private OBDReader obdReader;
    private GPSReader gpsReader;
    private OverpassReader overpassReader;

    //Armazena dados coletados referentes ao consumo de combustível
    ArrayList<DadosConsComb> dadosConsCombArray = new ArrayList<>();
    DataBaseConsComb dataBaseConsComb = new DataBaseConsComb(IniciarClassificacao.this);

    //**********--> Atributos referentes à variável: consumo de combustíveis:
    private float tempoAnterior = 0;
    private float tempoAtual = 0;
    private float litroHoraCombustivel = 0;
    private float acumulaLitroHoraCombustivel = 0;
    private double acumulaConsumoCombustivel = 0;
    //**********

    //**********--> Atributos gerais:
    private double guardarLogitude;
    private double guardarLatitude;
    private int janelaTempo;
    private String tipoCombustivel;

    private float distanciaPercorrida = 0;
    private GPSInfo lastGPSInfo = null;
    private boolean start = true;
    //***********

    Button btnEncerrarColetar;

    TextView tvNotaCons, tvClassCons, tvLitros, tvNotaVeloc, tvNomeVia, tvClassVeloc, tvVelocVia, tvVelocVeic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        btnEncerrarColetar = (Button) findViewById(R.id.btnEncerrarColetar);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        idPerfil = sharedPreferences.getLong("ID", -1);
        String mac = sharedPreferences.getString("addressDevice", "");

        DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
        veiculo = dataBasePerfis.selectPerfilById(idPerfil);
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3));

        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this,this);
        overpassReader = new OverpassReader(this);

        tvNotaCons=(TextView)findViewById(R.id.textViewNotaCons);
        tvClassCons=(TextView)findViewById(R.id.textViewClassCons);
        tvNotaVeloc=(TextView)findViewById(R.id.textViewNotaVeloc);
        tvClassVeloc=(TextView)findViewById(R.id.textViewClassCons);
        tvLitros=(TextView)findViewById(R.id.textViewLitros);
        tvVelocVia=(TextView)findViewById(R.id.textViewVelocVia);
        tvVelocVeic=(TextView)findViewById(R.id.textViewVelocVeic);
        tvNomeVia=(TextView)findViewById(R.id.textViewNomeVia);

        //Em Configurações no menu principal do aplicativo, a opção Janela de Tempo deverá possuir um valor informado, caso contrário levará o valor padrão de 300s.

        //Restaura as preferencias gravadas em janela de tempo para coletar os dados dos dispositivos.
        SharedPreferences recuperaSharedJT = getSharedPreferences(JANELATEMPO_NAME, 0);
        janelaTempo = Integer.parseInt(recuperaSharedJT.getString(JANELATEMPO_KEY, ""));

        if (janelaTempo == 0) {
          janelaTempo = 300;
        }

        //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
        SharedPreferences recuperaSharedTC = getSharedPreferences(TIPOCOMBUSTIVEL_NAME, 0);
        String tipoCombustivelExtenso = recuperaSharedTC.getString(TIPOCOMBUSTIVEL_KEY, "").toUpperCase();

        if (tipoCombustivelExtenso.equals("GASOLINA") ) {
            tipoCombustivel = "1";
        }else if (tipoCombustivelExtenso.equals("ETANOL")) {
            tipoCombustivel = "2";
        }

        // Envia para o classificador, os dados de saída.
        ClassificadorEntradasSaidas.SaidasParaClassificador();
        // Envia para o classificador, os dados de Entrada da variável consumo de combustível.
        ClassificadorEntradasSaidas.EntradasParaConsumoCombustivel();
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

        //********** Consumo de Combustível:
        Toast.makeText(IniciarClassificacao.this, "Testando OBDUpdate...", Toast.LENGTH_SHORT).show();
        //float fuel_consump = Calculate.getFuelConsump(obdinfo, cilindrada); //Calcula o consumo de combustível em litro
        litroHoraCombustivel = Calculate.getFuelflow(obdinfo, cilindrada); //Calcular litros de combustível
        acumulaLitroHoraCombustivel+= litroHoraCombustivel; //Totaliza a média de litros de combustíveis dentro da janela estabelecida de 300s.
        tvLitros.setText("Litros de Combustível: "+new DecimalFormat("0.00").format(litroHoraCombustivel)+" l/h");
        //acumulaConsumoCombustivel+= fuel_consump;
        //********** Final - Consumo Combustível **********
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void GPSupdate(GPSInfo gpsInfo) {

        if (!start) {
            finish();
        }

        //********** Início - Monitorar o tempo de 300s, para classificar e armazenar os dados adquiridos **********
        Date hora = new Date();
        if (tempoAnterior == 0.0) {
            tempoAnterior = hora.getTime();
            lastGPSInfo=gpsInfo;
        }
        else {
            //Far a leitura dos dados do dispositivo OBD.
            obdReader.read(OBDInfo.RPM | OBDInfo.SPEED | OBDInfo.INTAKE_PRESSURE | OBDInfo.INTAKE_TEMP);
            tempoAtual = hora.getTime();
            Location lastLocation,currentLocation;
            lastLocation=lastGPSInfo.getLocation();
            currentLocation=gpsInfo.getLocation();
            distanciaPercorrida+=lastLocation.distanceTo(currentLocation);
            lastGPSInfo=gpsInfo;

            // Monitora a janela de tempo:
            if ((tempoAtual - tempoAnterior) >= (janelaTempo*1000)) {

                //Classificação do motorista através da variável: "CONSUMO DE COMBUSTÍVEL"
                if (acumulaLitroHoraCombustivel != 0.0) {
                    ClassificarConsComb();
                }

                //Zerar os atributos acumuladores dentro da janela
                distanciaPercorrida=0;
                acumulaConsumoCombustivel = 0;
                tempoAnterior = 0;
                tempoAtual = 0;
            }
        }
        //********** Final - Monitoramento **********
    }

    @Override
    public void overpassupdate(OverpassInfo overpassinfo) {
        //float maximaVelocidade = Calculate.getVelocidade(overpassinfo);

        //********** Início - Velocidade **********

        //maxVelocVia = Calculate.getVelocidade(overpassinfo);

        //tvNomeVia.setText("Nome da Via: "+overpassinfo.getName());
        //tvVelocVia.setText("Vel. da via: "+maxVelocVia+"km/h");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ClassificarConsComb(){
        //Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.++++++
        DadosConsComb dadosConsComb = new DadosConsCombBuilder().createDadosConsComb();

        // Analisa qual o tipo de combustível informado pelo perfil do motorista a ser classificado.
        Double mediaCombustivelFabricante = 0.0;
        if (tipoCombustivel == "1"){
            mediaCombustivelFabricante = veiculo.getGasolinaCidade();
        }else if (tipoCombustivel == "2"){
            mediaCombustivelFabricante = veiculo.getEtanolCidade();
        }

        //Calcular o consumo médio de combustível = distanciapercorrida/quantidade de litros consumidos.
        float mediaConsumoCombustivelMotorista = distanciaPercorrida / acumulaLitroHoraCombustivel;
        //Calcular o CONSUMO NORMALIZADO = consumo médio do motorista/consumo médio indicado pelo fabricante do veículo.
        Double NC = mediaConsumoCombustivelMotorista / mediaCombustivelFabricante;
        //Envia os dois parâmetros para a classe classificadorFuzzy. Parâmetro1: indica a variável: "CONSUMO DE COMBUSTÍVEL", Paâmetro 2: indica o consumo normalizado.
        ClassificadorFuzzy.calcularNotas("consumo", NC);

        //tvNotaCons.setText(ClassificadorFuzzy.resultado[0]);
        //tvClassCons.setText(ClassificadorFuzzy.resultado[1]);

        tvNotaCons.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCons.setText("Classificação: "+ClassificadorFuzzy.classe);

        dadosConsComb.setFuelconsump(NC);
        dadosConsComb.setFuelflow(Double.parseDouble(valueOf(mediaConsumoCombustivelMotorista)));
        Date dataOcorrencia = new Date();
        dadosConsComb.setData(dataOcorrencia);
        dadosConsComb.setHora(dataOcorrencia.getTime());
        //dadosConsComb.setLongitude(guardarLogitude);
        //dadosConsComb.setLongitude(guardarLatitude);

        dadosConsCombArray.add(dadosConsComb);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ClassificarVelocidade(){
        //ClassificadorEntradasSaidas.EntradasParaVelocidade(maxVelocVia);
        //ClassificadorFuzzy.calcularNotas("velocidade", velocMotorista); //Classificar o motorista segundo a variável "consumo de combustível"
    }

    public void btnEncerrarColetar(View view) {
        start = false; //Encerra a coleta de dados nos dispositivos.
        Toast.makeText(IniciarClassificacao.this,"Classificação Geral", Toast.LENGTH_SHORT).show();
        enviarDadosSensores();
        finish(); //Retorna para a tela anterior.
    }

    public void enviarDadosSensores() {
        //Persiste os dados de consumo de combustível:
        DataBaseConsComb dataBaseDadosSensores = new DataBaseConsComb(IniciarClassificacao.this);
        for (DadosConsComb d: dadosConsCombArray) {
            dataBaseDadosSensores.inserirDadosConsComb(d);
        }
    }
}
