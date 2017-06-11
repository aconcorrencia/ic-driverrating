package android.usuario.driverrating;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import android.usuario.driverrating.database.DataBaseColetadosSensores;
import android.usuario.driverrating.database.DataBaseDriverRating;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.DadosColetadosSensores;
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
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.notaConsumoCombustivel;
import static android.usuario.driverrating.DriverRatingActivity.classificacaoConsumoCombustivel ;
import static android.usuario.driverrating.DriverRatingActivity.tipoCombustivel;
import static android.usuario.driverrating.DriverRatingActivity.menorValorCO2;
import static android.usuario.driverrating.extra.ClassificadorEntradasSaidas.EntradasParaVelocidade;

/**
 * Created by Sillas and Nielson on 24/04/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class IniciarClassificacao extends AppCompatActivity implements IOBDBluetooth, IGPSReader, IOverpassReader {

    private Veiculo veiculo;
    private DadosColetadosSensores dadosColetadosSensores;
    private long idPerfil;
    private Float cilindrada;
    private SharedPreferences sharedPreferences;

    //Leitura das classe responsáveis por acessar os dispositivos: OBD e GPS e também ler dados disponibilizados.
    private BluetoothAdapter mBluetoothAdapter = null;
    private OBDReader obdReader;
    private GPSReader gpsReader;
    private OverpassReader overpassReader;

    //Armazena dados coletados referentes aos sensores
    ArrayList<DadosColetadosSensores> dadosColetadosSensoresArray = new ArrayList<>();
    DataBaseColetadosSensores dataBaseColetadosSensores = new DataBaseColetadosSensores(IniciarClassificacao.this);

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
    private float acumulaNotaVelocidade = 0;
    private int contarClassVelocBom = 0;
    private int contarClassVelocMedio = 0;
    private int contarClassVelocRuim = 0;
    //**********

    //**********--> Atributos gerais:
    private int tempoAnterior = 0;
    private int tempoAtual = 0;
    private int tempoAnteriorAuxiliar = 0;
    private double guardaLogitudeInicial;
    private double guardaLatitudeInicial;
    private double guardaLogitudeFinal;
    private double guardaLatitudeFinal;
    private int janelaTempo;
    private int acumulaTempo = 0;

    private int controleClassificacao;
    private int controleCalcVeloc = 0;

    private float distanciaPercorrida = 0;
    private GPSInfo lastGPSInfo = null;
    private boolean start = true;
    //***********

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
        cilindrada = Float.parseFloat(veiculo.getMotor().substring(0, 3)) * 1000;

        obdReader = new OBDReader(this, this, mac);
        gpsReader = new GPSReader(this, this);
        overpassReader = new OverpassReader(this);

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
        }

        // Controlador de classificação, ou seja, informa a classificações corrente.
        controleClassificacao = 0;

        // Envia para o classificador, os dados de saída.
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        obdReader.stop();
        gpsReader.stop();
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

        /*tvLitros.setText("Litros de Combustível: "+new DecimalFormat("0.0000").format(litrosCombustivelConsumidos)+" ml");
        tvTotLitros.setText("Total de Litros: "+new DecimalFormat("0.0000").format(acumulaLitrosCombustivelConsumidos)+" ml");*/

        velocidadeMotorista = obdinfo.getSpeed();

        /*tvmaf.setText(//"maf Cálculo 1: "+new DecimalFormat("0.0000000000").format(valormaf1) +"\n" +
                      //"litros maf 1: "+new DecimalFormat("0.0000000000").format(trazmaf1) + "\n" +
                      //"Abs_Load: "+new DecimalFormat("0.0000000000").format(valormaf3) +"\n" +
                      //"maf Cálculo 2: "+new DecimalFormat("0.000").format(valormaf2)); //+"\n" + //para testar o maf 2
                      //"tempo Atual: "+new DecimalFormat("0.00").format(valordelta)+" segundos" +"\n" +
                      //"CO2: "+new DecimalFormat("0.0000000000").format(quilogramasPorSegundosCO2) + "\n " +   //para testar o maf 1
                      //"Acumulo CO2: "+new DecimalFormat("0.0000000000").format(acumulaQuilogramasPorSegundosCO2) + "\n " +
                      //"Acumulo de tempo: "+new DecimalFormat("0.00").format(acumulaTempo) + " segundos");*/

        //********** Final - Consumo Combustível **********
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

            //Guarda a longitude e latitude inicial
            guardaLogitudeInicial = gpsInfo.getLongitude();
            guardaLatitudeInicial = gpsInfo.getLatitude();
        } else {
            //Guarda o tempo atual.
            tempoAtual = (int) hora.getTime();
            //Os dois atributos abaixo são utilizados para medir a distância percorrida.
            Location lastLocation, currentLocation;
            lastLocation = lastGPSInfo.getLocation();
            currentLocation = gpsInfo.getLocation();

            //Far a leitura dos dados do dispositivo OBD. Ativa o método obdUpdate.
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
            atual do motorista e da velocidade máxima da via onde o veículo está trafegando naquele momento, portanto o método
            "ClassificarVeloidadeParcial()" tem essa função. Após a janela de tempo ser completada será executado o método
            "ClassificarVelocidade", onde se dá assim a classificação de acordo com a média das somas das notas parciais no
            quesito Velocidade.
           */
            overpassReader.read(gpsInfo.getLatitude(), gpsInfo.getLongitude(), 10);

            if (velocidadeMaximaDaVia != 0) {
                controleCalcVeloc += 1;
                ClassificarVelocidadeParcial();
            }

            // Monitora a janela de tempo:
            if ((tempoAtual - tempoAnterior) >= (janelaTempo * 1000)) {

                //send the tone to the "alarm" stream (classic beeps go there) with 200% volume
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500); // 500 is duration in ms

                //Controlador de classificação
                controleClassificacao += 1;

                //Guarda a longitude e latitude final
                guardaLogitudeFinal = gpsInfo.getLongitude();
                guardaLatitudeFinal = gpsInfo.getLatitude();

                // Persiste os dados colhidos do OBD-II, do GPS do SmartPhone e dos Acelerômetros a cada janela de tempo.
                dadosColetadosSensoresArray.clear();
                PersistenciaDosDadosColhidos();

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

                    //Classificação do motorista através da variável: "CONSUMO DE COMBUSTÍVEL"
                    //sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
                    //idPerfil = sharedPreferences.getLong("ID", -1);

                    //Posicionar no último registro.(ver com Sillas ou Prof. Jorge)

                    TratarVariaveisDimensoesClassificar.ClassificarConsumoDeCombustivel(dadosColetadosSensores, veiculo);
                    notaConsumoCombustivel = new DecimalFormat("0.00").format(ClassificadorFuzzy.nota);
                    classificacaoConsumoCombustivel = ClassificadorFuzzy.classe;

                    tvNotaCons.setText("Nota: "+new DecimalFormat("0.00").format(notaConsumoCombustivel));
                    tvClassCons.setText("Classificação: "+classificacaoConsumoCombustivel);

                    /*tvMediaMotorista.setText("Média do Motorista: "++" km/l");
                    tvNormalizCons.setText("Consumo Normalizado: "+new DecimalFormat("0.00").format(NC)+" .");*/

                    //Classificação do motorista através da variável: "EMISSÃO DE ÓXIDO DE CARBONO"
                     TratarVariaveisDimensoesClassificar.ClassificarEmissaoCO2(dadosColetadosSensores, veiculo);
                    //Classificação do motorista através da variável: "VELOCIDADE"
                }

                /** Abaixo faz-se uma verificação das velocidades da via e do motorista, caso as mesmas sejam diferentes de o, a classificação da varíavel
                 *  Velocidade será calculada.
                 */
                if (velocidadeMaximaDaVia != 0) {
                    TratarVariaveisDimensoesClassificar.ClassificarVelocidade(dadosColetadosSensores);
                }

                //Zera os atributos acumuladores dentro da janela.
                distanciaPercorrida = 0;
                acumulaLitrosCombustivelConsumidos = 0;
                tempoAnterior = 0;
                tempoAtual = 0;
                tempoAnteriorAuxiliar = 0;

                guardaLogitudeInicial = 0.0;
                guardaLatitudeInicial = 0.0;
                guardaLogitudeFinal = 0.0;
                guardaLatitudeFinal = 0.0;

                acumulaNotaVelocidade = 0;
                controleCalcVeloc = 0;

                contarClassVelocBom = 0;
                contarClassVelocMedio = 0;
                contarClassVelocRuim = 0;

                acumulaTempo = 0;
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

        if (ClassificadorFuzzy.classe.equals("Bom")) {
            contarClassVelocBom += 1;
        } else if (ClassificadorFuzzy.classe.equals("Médio")) {
            contarClassVelocMedio += 1;
        } else {
            contarClassVelocRuim += 1;
        }

        acumulaNotaVelocidade += ClassificadorFuzzy.nota;

    }

    private void PersistenciaDosDadosColhidos() {

        // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.
        DadosColetadosSensores dadosColetadosSensores = new DadosColetadosSensores();

        dadosColetadosSensores.setUsuario((int) idPerfil);
        dadosColetadosSensores.setDistanciaPercorrida(distanciaPercorrida);
        dadosColetadosSensores.setLitrosCombustivel(acumulaLitrosCombustivelConsumidos);
        dadosColetadosSensores.setNotaVelocidade(acumulaNotaVelocidade);
        dadosColetadosSensores.setControleCalcVelocidade(controleCalcVeloc);
        dadosColetadosSensores.setControleCalcVelocidade(controleCalcVeloc);
        dadosColetadosSensores.setContarClassificarVelocidadeBom(contarClassVelocBom);
        dadosColetadosSensores.setContarClassificarVelocidadeMedio(contarClassVelocMedio);
        dadosColetadosSensores.setContarClassificarVelocidadeRuim(contarClassVelocRuim);
        Date dataOcorrencia = new Date();
        dadosColetadosSensores.setData(dataOcorrencia);
        dadosColetadosSensores.setHora(dataOcorrencia.getTime());
        dadosColetadosSensores.setLongitudeInicial(guardaLogitudeInicial);
        dadosColetadosSensores.setLatidudeInicial(guardaLatitudeInicial);
        dadosColetadosSensores.setLongitudeFinal(guardaLogitudeFinal);
        dadosColetadosSensores.setLatidudeFinal(guardaLatitudeFinal);
        dadosColetadosSensores.setTipoCombustivel(tipoCombustivel);
        DataBaseColetadosSensores dataBaseColetadosSensores = new DataBaseColetadosSensores(IniciarClassificacao.this);
        dataBaseColetadosSensores.inserirDadosColetadosSensores(dadosColetadosSensores);
    }

    public void btnEncerrarColetar(View view) {
        // Encerra a coleta de dados nos dispositivos.
        start = false;
        Toast.makeText(IniciarClassificacao.this, "Classificação Geral", Toast.LENGTH_SHORT).show();

        // Retorna para a tela anterior.
        finish();
    }

}
