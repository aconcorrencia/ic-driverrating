package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_NAME;
import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_KEY;
import static android.usuario.driverrating.DriverRatingActivity.PERCENTUALALCOOL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.PERCENTUALALCOOL_NAME;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoAceleracaoLongitudinal;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoAceleracaoTransversal;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoEmissaoCO2;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoVelocidade;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoLongitudinal;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoTransversal;
import static android.usuario.driverrating.DriverRatingActivity.notaConsumoCombustivel;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoConsumoCombustivel;
import static android.usuario.driverrating.DriverRatingActivity.notaEmissaoCO2;
import static android.usuario.driverrating.DriverRatingActivity.notaVelocidade;
import static android.usuario.driverrating.DriverRatingActivity.tipoCombustivel;
import static android.usuario.driverrating.DriverRatingActivity.percentualAlcool;
import static android.usuario.driverrating.DriverRatingActivity.ultimoLog;
import static android.usuario.driverrating.DriverRatingActivity.ultimaJanela;
import static android.usuario.driverrating.extra.ClassificadorEntradasSaidas.EntradasParaVelocidade;

/**
 * Created by Sillas and Nielson on 24/04/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth, IGPSReader, IOverpassReader{

    private Veiculo veiculo;
    private DadosColetadosSensores dadosColetadosSensores;
    private long idPerfil;
    private Float cilindrada;
    private SharedPreferences sharedPreferences;

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
    private int velocidadeMotorista = 0;
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
    private  int numeroJanela;

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

    tvLitros,
            tvTotLitros,
            tvDistPercor,
            tvMediaMotorista,
            tvNormalizCons,
            tvmaf;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_classificacao);

        btnEncerrarColetar = (Button) findViewById(R.id.btnEncerrarColetar);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        idPerfil = sharedPreferences.getLong("ID", -1);
        String mac = sharedPreferences.getString("addressDevice", "");

        DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
        veiculo = dataBasePerfis.selectPerfilById(idPerfil);
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3)) * 1000;

        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this, this);
        overpassReader = new OverpassReader(this);
        accReader = new ACCReader(this);

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

        tvDistPercor = (TextView) findViewById(R.id.textViewDistancia);
        tvMediaMotorista = (TextView) findViewById(R.id.textViewMdMotorista);
        tvNormalizCons = (TextView) findViewById(R.id.textViewNormalizCons);
        tvLitros = (TextView) findViewById(R.id.textViewLitros);
        tvTotLitros = (TextView) findViewById(R.id.textViewTotLitros);
        tvVelocVia = (TextView) findViewById(R.id.textViewVelocVia);
        tvVelocVeic = (TextView) findViewById(R.id.textViewVelocVeic);
        tvNomeVia = (TextView) findViewById(R.id.textViewNomeVia);
        tvmaf = (TextView) findViewById(R.id.textViewMaf);

        //Em Configurações no menu principal do aplicativo, a opção Janela de Tempo deverá possuir um valor informado, caso contrário levará o valor padrão de 300s.
        //Restaura as preferencias gravadas em janela de tempo para coletar os dados dos dispositivos.
        SharedPreferences recuperaSharedJT = getSharedPreferences(JANELATEMPO_NAME, 0);
        janelaTempo = Integer.parseInt(recuperaSharedJT.getString(JANELATEMPO_KEY, ""));

        //Verifica se o usuário não informou o valor da janela de tempo em segundos, então o aplicativo assume o valor de 300 segundos.
        if (janelaTempo == 0) {
            janelaTempo = 300;
        }

        //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
        SharedPreferences recuperaSharedTC = getSharedPreferences(TIPOCOMBUSTIVEL_NAME, 0);
        String tipoCombustivelExtenso = recuperaSharedTC.getString(TIPOCOMBUSTIVEL_KEY, "").toUpperCase();

        if (tipoCombustivelExtenso.equals("GASOLINA")) {
            tipoCombustivel = "1";
        } else if (tipoCombustivelExtenso.equals("DIESEL")) {
            tipoCombustivel = "2";
        } else if (tipoCombustivelExtenso.equals("FLEX")) {
            tipoCombustivel = "3";
            //Caso o tipo de combustível seja FLEX, deduz da quantidade de CO2 emitido pelo motorista o percentual de etanol.

            //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
            SharedPreferences recuperaSharedPercAlc = getSharedPreferences(PERCENTUALALCOOL_NAME, 0);
            percentualAlcool = recuperaSharedPercAlc.getString(PERCENTUALALCOOL_KEY, "");
        }

        //Persiste na tabela de Logs da classificação. Gravando a Data e a Hora da Classificação.
        PersistirLog();

        //Buscar o id do log atual.
        DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(this);
        ultimoLog  = dataBaseLogClassificacao.selectUltimoLog(idPerfil);

        dadosPercViag.clear();

        //Envia para o classificador, os dados de saída.
        ClassificadorEntradasSaidas.SaidasParaClassificador();

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
        accReader.start(ACCReader.NORMAL);

        //ACCUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        obdReader.stop();
        gpsReader.stop();
        accReader.stop();
    }

    @Override
    public void obdUpdate(OBDInfo obdinfo) {

        //Através do presente método "obdUpdate", será colhido e classificados dados de diversos sensores do dispositivo OBD,
        //referentes às seguintes variáveis (dimensões): Consumo de combustível e a Emissão do gás Óxido de carbono (CO2).

        //********** Início - Consumo de Combustível **********:

        //Calcular litros de combustível
        litrosCombustivelConsumidos = Calculate.getFuelflow(obdinfo, cilindrada, tempoAtual - tempoAnteriorAuxiliar);

        //Armazena a última hora atual que será deduzida da próxima hora atual.
        tempoAnteriorAuxiliar = tempoAtual;

        //Totaliza a média de litros de combustíveis dentro da janela estabelecida de 300s.
        acumulaLitrosCombustivelConsumidos += litrosCombustivelConsumidos;

        tvLitros.setText("Litros de Combustível: "+new DecimalFormat("0.0000").format(litrosCombustivelConsumidos)+" ml");
        tvTotLitros.setText("Total de Litros: "+new DecimalFormat("0.0000").format(acumulaLitrosCombustivelConsumidos)+" ml");

       // float testerpm;

        //teste = obdinfo.getRpm();

        velocidadeMotorista = obdinfo.getSpeed();

        //********** Final - Consumo Combustível **********
    }
    public void ACCUpdate() {
        ACCInfo accInfo = accReader.read();

        float acclong = Math.abs(accInfo.getAccLongitudinal());
        float acctrans = Math.abs(accInfo.getAccTranversal());

        if (acclong > acclongitudinal) acclongitudinal = acclong;

        if(acctrans > acctransversal) acctransversal = acctrans;
    }

    @Override
    public void overpassupdate(OverpassInfo overpassinfo) {

        if (overpassinfo.getMaxspeed() != "unknown") {
            velocidadeMaximaDaVia = Integer.parseInt(overpassinfo.getMaxspeed());
        }

        tvNomeVia.setText("Nome da Via: " + overpassinfo.getName());
        tvVelocVia.setText("Vel. da Via: " + overpassinfo.getMaxspeed());
        tvVelocVeic.setText("Vel. do Veículo: " + velocidadeMotorista);
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
        Log.w("TESTE","GPS");



        if (!start) {
            finish();
        }
        //********** Início - Monitorar o tempo de 300s, para classificar e armazenar os dados adquiridos **********
        Date hora = new Date();
        if (tempoAnterior == 0.0) {
            //Guarda o último tempo.
            tempoAnterior = (int) hora.getTime();
            tempoAnteriorAuxiliar = tempoAnterior;
            //Guarda a última leitura do GPS a cada ciclo de X segundos.
            lastGPSInfo = gpsInfo;

        } else {
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
            tvDistPercor.setText("Distância Percorrida: " + new DecimalFormat("0.00").format(distanciaPercorrida) + " metros");

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

                tvNotaVeloc.setText("Nota: "+new DecimalFormat("0.00").format(notaVelocidade));
                tvClassVeloc.setText("Classificação: "+ classificacaoVelocidade);
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

                    TratarVariaveisDimensoesClassificar.ClassificarConsumoDeCombustivel(dadosColetadosSensores, veiculo);
                    notaConsumoCombustivel = ClassificadorFuzzy.nota;
                    classificacaoConsumoCombustivel = ClassificadorFuzzy.classe;

                    tvNotaCons.setText("Nota: "+new DecimalFormat("0.00").format(notaConsumoCombustivel));
                    tvClassCons.setText("Classificação: "+classificacaoConsumoCombustivel);

                    //Classificação do motorista através da variável: "EMISSÃO DE ÓXIDO DE CARBONO"
                    TratarVariaveisDimensoesClassificar.ClassificarEmissaoCO2(dadosColetadosSensores, veiculo);
                    notaEmissaoCO2 = ClassificadorFuzzy.nota;
                    classificacaoEmissaoCO2 = ClassificadorFuzzy.classe;

                    tvNotaCO2.setText("Nota: "+new DecimalFormat("0.00").format(notaEmissaoCO2));
                    tvClassCO2.setText("Classificação: "+classificacaoEmissaoCO2);

                }

                if (acclongitudinal != 0) {
                    TratarVariaveisDimensoesClassificar.ClassificarAceleracaoLongitudinal(acclongitudinal);
                    notaAceleracaoLongitudinal  = ClassificadorFuzzy.nota;
                    classificacaoAceleracaoLongitudinal = ClassificadorFuzzy.classe;
                }

                if (acctransversal != 0) {
                    TratarVariaveisDimensoesClassificar.ClassificarAceleracaoTransversal(acctransversal);
                    notaAceleracaoTransversal  = ClassificadorFuzzy.nota;
                    classificacaoAceleracaoTransversal = ClassificadorFuzzy.classe;
                }

                // Controlador de número de janela.
                DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
                //ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela(ultimoLog);
                ultimaJanela = dataBaseResultadosClassificacaoMotorista.selectUltimaJanela();

                if (ultimaJanela == 0) {
                  numeroJanela = 1;
                }
                else{
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
                //*********************
            }
        }
        //********** Final - Monitoramento de Janela de Tempo **********
    }

    private void ClassificarVelocidadeParcial() {

        //Informa para a classe Entradas, a velocidade máxima da via atual.
        EntradasParaVelocidade(velocidadeMaximaDaVia);
        //Classificar o motorista segundo a variável "Velocidade"
        ClassificadorFuzzy.calcularNotas("velocidade", velocidadeMotorista);
        /* Acumula a nota trazida pelo cálculo fuzzy, este acumulo terá uma média, onde fará uma nova classificação no método
        *  "ClassificaçãoVelocidade" após a janela de tempo ser fechada.
        */

        if (ClassificadorFuzzy.nota < menorNotaVelocidade) {
            menorNotaVelocidade = ClassificadorFuzzy.nota;
            classeVelocidade = ClassificadorFuzzy.classe;

            notaVelocidade = menorNotaVelocidade;
            classificacaoVelocidade = classeVelocidade;
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
        for (int i=0; i < dadosPercViag.size(); i++){

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
        /*Toast.makeText(IniciarClassificacao.this, "Classificação Geral", Toast.LENGTH_SHORT).show();

        // Retorna para a tela anterior.
        finish();*/
        obdReader.read(OBDInfo.RPM);
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

}
