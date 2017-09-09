package android.usuario.driverrating.extra;

import android.usuario.driverrating.domain.DadosColetadosSensores;
import android.usuario.driverrating.domain.Veiculo;
import java.util.ArrayList;


/**
 * Created by NIELSON on 27/05/2017.
 */

public class TratarVariaveisDimensoesClassificar {

    public static int getDensityFuel(int tipoCombustivel) {
        int densityFuel;
        if ((tipoCombustivel == Utils.GASOLINA) || (tipoCombustivel == Utils.FLEX)) {
            densityFuel = 750; //Gasolina
        } else {
            densityFuel = 820; //Diesel
        }
        return densityFuel;
    }

    public static int getSomaCarbonoOxigenio(DadosColetadosSensores dadosColetadosSensores) {
        //Tipo de Combustível Gasolina ou Flex
        int somaCarbonoOxigenio;
        if ((dadosColetadosSensores.getTipoCombustivel() == Utils.GASOLINA) || (dadosColetadosSensores.getTipoCombustivel() == Utils.FLEX)) {
            somaCarbonoOxigenio = 2392; //consistem em 87% de carbono (652g)+  1740 gramas de oxigénio.
        } else {
            somaCarbonoOxigenio = 2627; //consistem em 86,2% de carbono (707g) + 1920 gramas de oxigénio.
        }
        return somaCarbonoOxigenio;
    }


    public static ClassificadorFuzzy classificarConsumoDeCombustivel(DadosColetadosSensores dadosColetadosSensores, Veiculo veiculo) {

        // Preenche os dados de Entrada da variável consumo de combustível.
        ArrayList<Double> entradas = ClassificadorEntradasSaidas.entradasParaConsumoCombustivel();

        // Analisa qual o tipo de combustível informado pelo perfil do motorista a ser classificado.
        Double mediaCombustivelFabricante = 0.0;
        if ((dadosColetadosSensores.getTipoCombustivel() == Utils.GASOLINA) || (dadosColetadosSensores.getTipoCombustivel() == Utils.FLEX)) {
            mediaCombustivelFabricante = veiculo.getGasolinaCidade();
        } else if (dadosColetadosSensores.getTipoCombustivel() == Utils.DIESEL) {
            mediaCombustivelFabricante = veiculo.getEtanolCidade();
        }

        // Calcular o consumo médio de combustível.
        //double mediaConsCombustMotorista = (distanciaPercorrida / acumulaLitrosCombustivelConsumidos);
        double mediaConsCombustMotorista = (dadosColetadosSensores.getDistanciaPercorrida() / dadosColetadosSensores.getLitrosCombustivel());
        // Calcular o CONSUMO NORMALIZADO = consumo médio indicado pelo fabricante do veículo / consumo médio do motorista.
        double NC = mediaCombustivelFabricante / mediaConsCombustMotorista;
        // Envia os dois parâmetros para a classe classificadorFuzzy. Parâmetro1: indica a variável: "CONSUMO DE COMBUSTÍVEL", Paâmetro 2: indica o consumo normalizado.

        ClassificadorFuzzy classificadorFuzzy = new ClassificadorFuzzy(entradas);
        classificadorFuzzy.classificadorFuzzy("consumo", NC);

        return classificadorFuzzy;

    }


    /** Classifica o motorista quanto à variável Emissão de CO2 penalizando-o de acordo com a escolha do veículo.
     *  Quanto maior for a emissão de CO2, maior será a penalização .
     */
    public static ClassificadorFuzzy classificarEmissaoCO2(DadosColetadosSensores dadosColetadosSensores, Veiculo veiculo, int percentualAlcool, float fatorPenalizacaoCO2) {

        //Envia para o classificador, os dados de Entrada da variável Emissão de Óxido de Carbobo.
        ArrayList<Double> entradas = ClassificadorEntradasSaidas.entradasParaCO2();

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
        int somaCarbonoOxigenio = getSomaCarbonoOxigenio(dadosColetadosSensores);

        double EmissaoCO2Fabricante = veiculo.getCo2();

        double EmissaoCO2Motorista = ((litrosCombustivel * somaCarbonoOxigenio) / quilometragem);

        EmissaoCO2Motorista = EmissaoCO2Motorista - (EmissaoCO2Motorista * (percentualAlcool / 100));

        //Envia os dois parâmetros para a classe classificadorFuzzy. Parâmetro1: indica a variável: "ÓXIDO DE CARBONO", Parâmetro 2: indica o co2 normalizado.
        double NCCO2 = EmissaoCO2Fabricante / EmissaoCO2Motorista;

        ClassificadorFuzzy classificadorFuzzy = new ClassificadorFuzzy(entradas);
        classificadorFuzzy.classificadorFuzzy("co2", NCCO2 * fatorPenalizacaoCO2);

        return classificadorFuzzy;
    }

    /**
     * Classifica o motorista quanto às variáveis Aceleração Longitudinal.
     * Quanto maior for a aceleração, pior será a sua nota.
     */
    public static ClassificadorFuzzy classificarAceleracaoLongitudinal(float accLong) {

        //Envia para o classificador, os dados de Entrada das variáveis Aceleraçoes Longitudinal e Transversal.
        ArrayList<Double> entradas = ClassificadorEntradasSaidas.entradasParaAceleracoes();

        ClassificadorFuzzy classificadorFuzzy = new ClassificadorFuzzy(entradas);
        classificadorFuzzy.classificadorFuzzy("aceleracoes", accLong);

        return classificadorFuzzy;
    }

    /**
     * Classifica o motorista quanto às variáveis Acelerações Longitudinal e/ou Transversal.
     * Quanto maior for a aceleração, pior será a sua nota.
     */
    public static ClassificadorFuzzy ClassificarAceleracaoTransversal(float accTrans) {

        //Envia para o classificador, os dados de Entrada das variáveis Aceleraçoes Longitudinal e Transversal.

        ArrayList<Double> entradas = ClassificadorEntradasSaidas.entradasParaAceleracoes();

        ClassificadorFuzzy classificadorFuzzy = new ClassificadorFuzzy(entradas);
        classificadorFuzzy.classificadorFuzzy("aceleracoes", accTrans);

        return classificadorFuzzy;
    }

    /**
     * Acumula a nota trazida pelo cálculo fuzzy, este acumulo terá uma média, onde fará uma nova classificação no método
     * "ClassificaçãoVelocidade" após a janela de tempo ser fechada.
     */
    public static ClassificadorFuzzy classificarVelocidadeParcial(int velocidadeMotorista, int velocidadeMaximaDaVia) {

        //Informa para a classe Entradas, a velocidade máxima da via atual.
        ArrayList<Double> entradas = ClassificadorEntradasSaidas.entradasParaVelocidade(velocidadeMaximaDaVia);

        //Classificar o motorista segundo a variável "Velocidade"
        ClassificadorFuzzy classificadorFuzzy = new ClassificadorFuzzy(entradas);
        classificadorFuzzy.classificadorFuzzy("velocidade", velocidadeMotorista);

        return classificadorFuzzy;
    }

}
