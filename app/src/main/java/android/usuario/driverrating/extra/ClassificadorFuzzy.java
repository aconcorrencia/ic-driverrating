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

   private  ArrayList<Double> entradas;
   private  ArrayList<Double> saidas;
    private String classe;
    private Double nota;

    public ClassificadorFuzzy(ArrayList<Double> entradas) {
        this.entradas = entradas;
        this.saidas = ClassificadorEntradasSaidas.saidasParaClassificador();
    }

    public void classificadorFuzzy(String origem, double dadosSensores) {

        Double grauPertinBaixo = 0.0;
        Double grauPertinMedio = 0.0;
        Double grauPertinAlto = 0.0;


        if (entradas.get(0) < entradas.get(1) && entradas.get(1) < entradas.get(2) && entradas.get(3) < entradas.get(4) && entradas.get(4) < entradas.get(5) && entradas.get(6) < entradas.get(7) && entradas.get(7) < entradas.get(8)) {

            //µbaixo
            if (dadosSensores < entradas.get(1)) {
                grauPertinBaixo = 1.0;
            } else if (dadosSensores < entradas.get(0) || entradas.get(2) <= dadosSensores) {
                grauPertinBaixo = 0.0;
            } else if (entradas.get(0) <= dadosSensores && dadosSensores < entradas.get(1)) {
                grauPertinBaixo = (dadosSensores - entradas.get(0)) / (entradas.get(1) - entradas.get(0));
            } else if (entradas.get(1) <= dadosSensores && dadosSensores < entradas.get(2)) {
                grauPertinBaixo = (entradas.get(2) - dadosSensores) / (entradas.get(2) - entradas.get(1));
            }

            //µmedio
            if (dadosSensores < entradas.get(3) || entradas.get(5) <= dadosSensores) {
                grauPertinMedio = 0.0;
            } else if (entradas.get(3) <= dadosSensores && dadosSensores < entradas.get(4)) {
                grauPertinMedio = (dadosSensores - entradas.get(3)) / (entradas.get(4) - entradas.get(3));
            } else if (entradas.get(4) <= dadosSensores && dadosSensores < entradas.get(5)) {
                grauPertinMedio = (entradas.get(5) - dadosSensores) / (entradas.get(5) - entradas.get(4));
            }

            //µalto
            if (entradas.get(8) == dadosSensores || dadosSensores > entradas.get(7)) {
                grauPertinAlto = 1.0;
            } else if (dadosSensores < entradas.get(6) || entradas.get(8) <= dadosSensores) {
                grauPertinAlto = 0.0;
            } else if (entradas.get(6) <= dadosSensores && dadosSensores < entradas.get(7)) {
                grauPertinAlto = (dadosSensores - entradas.get(6)) / (entradas.get(7) - entradas.get(6));
            } else if (entradas.get(7) <= dadosSensores && dadosSensores < entradas.get(8)) {
                grauPertinAlto = (entradas.get(8) - dadosSensores) / (entradas.get(8) - entradas.get(7));
            }

            if (origem.substring(0, 3).equals("ger")) {

                if (grauPertinBaixo > grauPertinMedio && grauPertinBaixo > grauPertinAlto) {

                    if ((origem.equals("geraisConsumo")) || (origem.equals("geraisVelocidade"))) {
                        classe = "Ruim";
                    } else if (origem.equals("geraisCO2")) {
                        classe = "Vermelho";
                    } else if (origem.equals("geraisAcc")) {
                        classe = "Arriscado";
                    }

                } else if (grauPertinMedio > grauPertinAlto) {

                    if ((origem.equals("geraisConsumo")) || (origem.equals("geraisVelocidade"))) {
                        classe = "Médio";
                    } else if (origem.equals("geraisCO2")) {
                        classe = "Amarelo";
                    } else if (origem.equals("geraisAcc")) {
                        classe = "Moderado";
                    }
                } else {

                    if ((origem.equals("geraisConsumo")) || (origem.equals("geraisVelocidade"))) {
                        classe = "Bom";
                    } else if (origem.equals("geraisCO2")) {
                        classe = "Verde";
                    } else if (origem.equals("geraisAcc")) {
                        classe = "Cauteloso";
                    }
                }
                //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
                nota = ((grauPertinBaixo * 1 * (saidas.get(0))) + (grauPertinMedio * 1 * (saidas.get(1))) + (grauPertinAlto * 1 * (saidas.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);
            } else {

                if (grauPertinBaixo > grauPertinMedio && grauPertinBaixo > grauPertinAlto) {

                    if ((origem.equals("consumo")) || (origem.equals("velocidade"))) {
                        classe = "Bom";
                    } else if (origem.equals("co2")) {
                        classe = "Verde";
                    } else if (origem.equals("aceleracoes")) {
                        classe = "Cauteloso";
                    }

                } else if (grauPertinMedio > grauPertinAlto) {

                    if ((origem.equals("consumo")) || (origem.equals("velocidade"))) {
                        classe = "Médio";
                    } else if (origem.equals("co2")) {
                        classe = "Amarelo";
                    } else if (origem.equals("aceleracoes")) {
                        classe = "Moderado";
                    }
                } else {

                    if ((origem.equals("consumo")) || (origem.equals("velocidade"))) {
                        classe = "Ruim";
                    } else if (origem.equals("co2")) {
                        classe = "Vermelho";
                    } else if (origem.equals("aceleracoes")) {
                        classe = "Arriscado";
                    }
                }
                //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
                nota = ((grauPertinAlto * 1 * (saidas.get(0))) + (grauPertinMedio * 1 * (saidas.get(1))) + (grauPertinBaixo * 1 * (saidas.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);
            }
            //Segundo técnica de DEFUZZIFICAÇÃO Centro dos Máximos (C o M)
            //nota = ((grauPertinAlto * 1 * (SAIDAS.get(0))) + (grauPertinMedio * 1 * (SAIDAS.get(1)) ) + (grauPertinBaixo * 1 * (SAIDAS.get(2)))) / (grauPertinBaixo + grauPertinMedio + grauPertinAlto);

        }

    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
}
