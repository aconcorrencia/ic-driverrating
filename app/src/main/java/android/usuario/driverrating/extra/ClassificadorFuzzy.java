package android.usuario.driverrating.extra;

import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import java.util.ArrayList;

/* Os cálculos são baseados na lógica fuzzy, tendo como passos a fuzzyficação, as regras e a defuzificação:...*/

/*
   ENTRADAS.get(0) --> baixo A;
   ENTRADAS.get(1) --> baixo B ;
   ENTRADAS.get(2) --> baico C;

   ENTRADAS.get(3) --> medio A;
   ENTRADAS.get(4) --> medio B;
   ENTRADAS.get(5) --> medio C;

   ENTRADAS.get(6) --> alto A;
   ENTRADAS.get(7) --> alto B;
   ENTRADAS.get(8) --> alto C;

   SAIDA.get(0) --> Maior altura da função de pertinência Alto
   SAIDA.get(1) --> Maior altura da função de pertinência Medio
   SAIDA.get(2) --> Maior altura da função de pertinência Baixo

 */

/**
 * Created by NIELSON on 29/04/2017.
 */

public class ClassificadorFuzzy {
    //Receberá os valores das variáveis de entradas, ou seja, os valore que componhem as funções de pertinências de entradas
    //Para todas as dimenções: CONSUMO DE COMBUSTÍVEL; CO2; NOx; ACELERAÇÃO LONGITUDINAL; ACELERAÇÃO TRANSVERSAL; VELOCIDADE.

    public final static ArrayList<Double> ENTRADAS = new ArrayList<Double>();
    public final static ArrayList<Double> SAIDAS = new ArrayList<Double>();

    public static Double grauPertinBaixo = 0.0;
    public static Double grauPertinMedio = 0.0;
    public static Double grauPertinAlto  = 0.0;

    public static Double nota = 0.0;

    public static String classe = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static final void calcularNotas(String origem, double dadosSensores) {

        classe = null;

        if (ENTRADAS.get(0) < ENTRADAS.get(1) && ENTRADAS.get(1) < ENTRADAS.get(2) && ENTRADAS.get(3) < ENTRADAS.get(4) && ENTRADAS.get(4) < ENTRADAS.get(5) && ENTRADAS.get(6) < ENTRADAS.get(7) && ENTRADAS.get(7) < ENTRADAS.get(8)){

            //µbaixo
            if (dadosSensores < ENTRADAS.get(1)){
                grauPertinBaixo = 1.0;
            } else if (dadosSensores < ENTRADAS.get(0) || ENTRADAS.get(2) <= dadosSensores) {
                grauPertinBaixo = 0.0;
            } else if (ENTRADAS.get(0) <= dadosSensores && dadosSensores < ENTRADAS.get(1)) {
                grauPertinBaixo = (dadosSensores - ENTRADAS.get(0)) / (ENTRADAS.get(1) - ENTRADAS.get(0));
            } else if (ENTRADAS.get(1) <= dadosSensores && dadosSensores < ENTRADAS.get(2)) {
                grauPertinBaixo = (ENTRADAS.get(2) - dadosSensores) / (ENTRADAS.get(2) - ENTRADAS.get(1));
            }

            //µmedio
            if (dadosSensores < ENTRADAS.get(3) || ENTRADAS.get(5) <= dadosSensores) {
                grauPertinMedio = 0.0;
            } else if (ENTRADAS.get(3) <= dadosSensores && dadosSensores < ENTRADAS.get(4)) {
                grauPertinMedio = (dadosSensores - ENTRADAS.get(3)) / (ENTRADAS.get(4) - ENTRADAS.get(3));
            } else if (ENTRADAS.get(4) <= dadosSensores && dadosSensores < ENTRADAS.get(5)) {
                grauPertinMedio = (ENTRADAS.get(5) - dadosSensores) / (ENTRADAS.get(5) - ENTRADAS.get(4));
            }

            //µalto
            if (ENTRADAS.get(8) == dadosSensores || dadosSensores > ENTRADAS.get(7) ) {
                grauPertinAlto = 1.0;
            } else if (dadosSensores < ENTRADAS.get(6) || ENTRADAS.get(8) <= dadosSensores) {
                grauPertinAlto = 0.0;
            } else if (ENTRADAS.get(6) <= dadosSensores && dadosSensores < ENTRADAS.get(7)) {
                grauPertinAlto = (dadosSensores - ENTRADAS.get(6)) / (ENTRADAS.get(7) - ENTRADAS.get(6));
            } else if (ENTRADAS.get(7) <= dadosSensores && dadosSensores < ENTRADAS.get(8)) {
                grauPertinAlto = (ENTRADAS.get(8) - dadosSensores) / (ENTRADAS.get(8) - ENTRADAS.get(7));
            }

            if (origem.substring(0,3).equals("ger")) {

                if (grauPertinBaixo > grauPertinMedio && grauPertinBaixo > grauPertinAlto) {

                    if ((origem == "geraisConsumo") || (origem == "geraisVelocidade")) {
                        classe = "Ruim";
                    } else if (origem == "geraisCO2") {
                        classe = "Vermelho";
                    } else if (origem == "geraisAcc") {
                        classe = "Arriscado";
                    }

                } else if (grauPertinMedio > grauPertinAlto) {

                    if ((origem == "geraisConsumo") || (origem == "geraisVelocidade")) {
                        classe = "Médio";
                    } else if (origem == "geraisCO2") {
                        classe = "Amarelo";
                    } else if (origem == "geraisAcc") {
                        classe = "Moderado";
                    }
                } else {

                    if ((origem == "geraisConsumo") || (origem == "geraisVelocidade")) {
                        classe = "Bom";
                    } else if (origem == "geraisCO2") {
                        classe = "Verde";
                    } else if (origem == "geraisAcc") {
                        classe = "Cauteloso";
                    }
                }
                //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
                nota = ((grauPertinBaixo * 1 * (SAIDAS.get(0))) + (grauPertinMedio * 1 * (SAIDAS.get(1)) ) + (grauPertinAlto * 1 * (SAIDAS.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);
            }
            else {

                if (grauPertinBaixo > grauPertinMedio && grauPertinBaixo > grauPertinAlto) {

                    if ((origem == "consumo") || (origem == "velocidade")) {
                        classe = "Bom";
                    } else if (origem == "co2") {
                        classe = "Verde";
                    } else if (origem == "aceleracoes") {
                        classe = "Cauteloso";
                    }

                } else if (grauPertinMedio > grauPertinAlto) {

                    if ((origem == "consumo") || (origem == "velocidade")) {
                        classe = "Médio";
                    } else if (origem == "co2") {
                        classe = "Amarelo";
                    } else if (origem == "aceleracoes") {
                        classe = "Moderado";
                    }
                } else {

                    if ((origem == "consumo") || (origem == "velocidade")) {
                        classe = "Ruim";
                    } else if (origem == "co2") {
                        classe = "Vermelho";
                    } else if (origem == "aceleracoes") {
                        classe = "Arriscado";
                    }
                }
                //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
                nota = ((grauPertinAlto * 1 * (SAIDAS.get(0))) + (grauPertinMedio * 1 * (SAIDAS.get(1)) ) + (grauPertinBaixo * 1 * (SAIDAS.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);
            }
            //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
            //nota = ((grauPertinAlto * 1 * (SAIDAS.get(0))) + (grauPertinMedio * 1 * (SAIDAS.get(1)) ) + (grauPertinBaixo * 1 * (SAIDAS.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);

        }
    }
}
