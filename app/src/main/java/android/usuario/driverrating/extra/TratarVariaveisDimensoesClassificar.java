package android.usuario.driverrating.extra;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.usuario.driverrating.domain.DadosColetadosSensores;
import android.usuario.driverrating.domain.Veiculo;

import static android.usuario.driverrating.DriverRatingActivity.densityFuel;
import static android.usuario.driverrating.DriverRatingActivity.fatorPenalizacaoCO2;
import static android.usuario.driverrating.DriverRatingActivity.percentualAlcool;

/**
 * Created by NIELSON on 27/05/2017.
 */

public class TratarVariaveisDimensoesClassificar{

    private static int somaCarbonoOxigenio = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void ClassificarConsumoDeCombustivel(DadosColetadosSensores dadosColetadosSensores, Veiculo veiculo){

        // Envia para o classificador, os dados de Entrada da variável consumo de combustível.
        ClassificadorEntradasSaidas.EntradasParaConsumoCombustivel();

        // Analisa qual o tipo de combustível informado pelo perfil do motorista a ser classificado.
        Double mediaCombustivelFabricante = 0.0;
        if ((dadosColetadosSensores.getTipoCombustivel().equals("1")) || (dadosColetadosSensores.getTipoCombustivel().equals("3"))){
            mediaCombustivelFabricante = veiculo.getGasolinaCidade();
        }else if (dadosColetadosSensores.getTipoCombustivel().equals("2")){
            mediaCombustivelFabricante = veiculo.getEtanolCidade();
        }

        if ((dadosColetadosSensores.getTipoCombustivel().equals("1")) || (dadosColetadosSensores.getTipoCombustivel().equals("3"))){
            densityFuel = 750; //Gasolina
        }else if (dadosColetadosSensores.getTipoCombustivel().equals("2")){
            densityFuel = 820; //Diesel
        }

        // Calcular o consumo médio de combustível.
        //double mediaConsCombustMotorista = (distanciaPercorrida / acumulaLitrosCombustivelConsumidos);
        double mediaConsCombustMotorista = (dadosColetadosSensores.getDistanciaPercorrida() / dadosColetadosSensores.getLitrosCombustivel());
        // Calcular o CONSUMO NORMALIZADO = consumo médio indicado pelo fabricante do veículo / consumo médio do motorista.
        double NC = mediaCombustivelFabricante / mediaConsCombustMotorista;
        // Envia os dois parâmetros para a classe classificadorFuzzy. Parâmetro1: indica a variável: "CONSUMO DE COMBUSTÍVEL", Paâmetro 2: indica o consumo normalizado.
        ClassificadorFuzzy.calcularNotas("consumo", NC);

       /* tvNotaCons.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCons.setText("Classificação: "+ClassificadorFuzzy.classe);

        tvMediaMotorista.setText("Média do Motorista: "+new DecimalFormat("0.00").format(mediaConsCombustMotorista)+" km/l");
        tvNormalizCons.setText("Consumo Normalizado: "+new DecimalFormat("0.00").format(NC)+" .");

        tvTituloCons.setText("COMSUMO DE COMBISTÍVEL - Nº " + controleClassificacao);*/

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void ClassificarEmissaoCO2(DadosColetadosSensores dadosColetadosSensores, Veiculo veiculo){

        //Envia para o classificador, os dados de Entrada da variável Emissão de Óxido de Carbobo.
        ClassificadorEntradasSaidas.EntradasParaCO2();

        //Total de litro(s) de combustível gasto(s) a cada janela estabelecida no experimento.
        float litrosCombustivel = dadosColetadosSensores.getLitrosCombustivel() / 1000;
        //Total da distância percorrida na janela estabelecida para o percurso.
        float quilometragem = dadosColetadosSensores.getDistanciaPercorrida() / 1000;

        /**
         * Referência abaixo para calcular a emissão de CO2 g/km:
         * *****************************************************
         * Diesel:
         *
         * 1 litro de diesel pesa 820 gramas. Diesel consistem em 86,2% de carbono, ou 707 gramas de carbono por litro de diesel.
         * Para queimar este carbono em CO2 , são necessários 1920 gramas de oxigénio. A soma é então 707 + 1920 = 2627 gramas
         * de CO2 / litro diesel.

         Um consumo médio de 5 litros / 100 km corresponde então a 5 lx 2627 g/l / 100 (por km) = 131,35 g CO2/km.
         *
         *****************************************************************************************************************************
         * Gasolina:
         *
         1 litro de gasolina pesa 750 gramas. A gasolina consiste em 87% de carbono, ou 652 gramas de carbono por litro de
         gasolina. Para queimar este carbono em CO2 , são necessários 1740 gramas de oxigénio. A soma é então 652 + 1740 = 2392
         gramas de CO2 / litro de gasolina.

         Um consumo médio de 5 litros / 100 km corresponde então a 5 lx 2392 g/l / 100 (por km) = 120 g CO2/km.

         FONTE: http://ecoscore.be/en/info/ecoscore/co2
         **/

        /** Resultado da equação em gramas por quilometors (g/km) de acordo com a tabela de referência do INMETRO.
         ** o atributo "somaCarbonoOxigenio", traz o valor da constante referente ao tipo de combustível se Gasolina = 2392 ou Diesel = 2627.

         */

        //Tipo de Combustível Gasolina ou Flex
        if ( (dadosColetadosSensores.getTipoCombustivel().equals("1")) || (dadosColetadosSensores.getTipoCombustivel().equals("3"))){
            somaCarbonoOxigenio = 2392; //consistem em 87% de carbono (652g)+  1740 gramas de oxigénio.
        }else if (dadosColetadosSensores.getTipoCombustivel().equals("2")){
            somaCarbonoOxigenio = 2627; //consistem em 86,2% de carbono (707g) + 1920 gramas de oxigénio.
        }

        double EmissaoCO2Fabricante = veiculo.getCo2();

        double EmissaoCO2Motorista = ((litrosCombustivel * somaCarbonoOxigenio) / quilometragem);

       /* //Caso o tipo de combustível seja FLEX, deduz da quantidade de CO2 emitido pelo motorista o percentual de etanol.
        if (tipoCombustivel.equals("3")){
            //Restaura as preferencias gravadas em tipo de combustível para coletar os dados dos dispositivos.
            SharedPreferences recuperaSharedPercAlc = context.getSharedPreferences(PERCENTUALALCOOL_NAME, MODE_PRIVATE);
            String percentualAlcool = recuperaSharedPercAlc.getString(PERCENTUALALCOOL_KEY, "");

            EmissaoCO2Motorista = EmissaoCO2Motorista - (EmissaoCO2Motorista * Float.parseFloat(percentualAlcool));
        }*/

        EmissaoCO2Motorista = EmissaoCO2Motorista - (EmissaoCO2Motorista * Float.parseFloat(percentualAlcool));

        //Envia os dois parâmetros para a classe classificadorFuzzy. Parâmetro1: indica a variável: "ÓXIDO DE CARBONO", Parâmetro 2: indica o co2 normalizado.
        double NCCO2 =  EmissaoCO2Fabricante / EmissaoCO2Motorista;

        /** Classifica o motorista quanto à variável Emissão de CO2 penalizando-o de acordo com a escolha do veículo.
         *  Quanto maior for a emissão de CO2, maior será a penalização .
         */
        ClassificadorFuzzy.calcularNotas("co2", NCCO2 * fatorPenalizacaoCO2);

        //tvTituloCO2.setText("ÓXIDO DE CARBONO (CO2) - Nº " + controleClassificacao);

        /*tvNotaCO2.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCO2.setText("Classificação: "+ClassificadorFuzzy.classe);
        tvCO2gkm.setText("CO2 em g/km : "+ new DecimalFormat("0.00").format(gramasPorQuilometrosCO2));*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void ClassificarAceleracaoLongitudinal(float accLong){

        //Envia para o classificador, os dados de Entrada das variáveis Aceleraçoes Longitudinal e Transversal.
        ClassificadorEntradasSaidas.EntradasParaAceleracoes();


        /** Classifica o motorista quanto às variáveis Aceleração Longitudinal.
         *  Quanto maior for a aceleração, pior será a sua nota.
         */
        ClassificadorFuzzy.calcularNotas("aceleracoes", accLong);

        //tvTituloCO2.setText("ÓXIDO DE CARBONO (CO2) - Nº " + controleClassificacao);

        /*tvNotaCO2.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCO2.setText("Classificação: "+ClassificadorFuzzy.classe);
        tvCO2gkm.setText("CO2 em g/km : "+ new DecimalFormat("0.00").format(gramasPorQuilometrosCO2));*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void ClassificarAceleracaoTransversal(float accTrans){

        //Envia para o classificador, os dados de Entrada das variáveis Aceleraçoes Longitudinal e Transversal.
        ClassificadorEntradasSaidas.EntradasParaAceleracoes();


        /** Classifica o motorista quanto às variáveis Acelerações Longitudinal e/ou Transversal.
         *  Quanto maior for a aceleração, pior será a sua nota.
         */
        ClassificadorFuzzy.calcularNotas("aceleracoes", accTrans);

        //tvTituloCO2.setText("ÓXIDO DE CARBONO (CO2) - Nº " + controleClassificacao);

        /*tvNotaCO2.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCO2.setText("Classificação: "+ClassificadorFuzzy.classe);
        tvCO2gkm.setText("CO2 em g/km : "+ new DecimalFormat("0.00").format(gramasPorQuilometrosCO2));*/
    }

}
