package android.usuario.driverrating.extra;

import java.util.ArrayList;

/**
 * Created by NIELSON on 29/04/2017.
 */

public class ClassificadorEntradasSaidas {

    public static ArrayList<Double> saidasParaClassificador() {
        ArrayList<Double> saidas = new ArrayList<>();
        saidas.add(1.65);
        saidas.add(2.50);
        saidas.add(4.00);
        return saidas;
    }

    public static ArrayList<Double> entradasParaConsumoCombustivel() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 0.50 , 1.00 )
          Medio -  Triangular( 0.65 , 1.00 , 1.35 )
          Alto  -  Rampa Direita( 1.00 , 1.40 , 1.50)
         */
        ArrayList<Double> entradas = new ArrayList<>();

        entradas.add(0, 0.0);
        entradas.add(1, 0.5);
        entradas.add(2, 1.0);
        entradas.add(3, 0.65);
        entradas.add(4, 1.0);
        entradas.add(5, 1.35);
        entradas.add(6, 1.0);
        entradas.add(7, 1.4);
        entradas.add(8, 1.5);

        return  entradas;

    }

    public static ArrayList<Double> entradasParaCO2() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 0.35 , 0.99 )
          Medio -  Triangular( 0.98 , 1.25 , 1.52 )
          Alto  -  Rampa Direita( 1.50 , 1.65 , 2.00 )
         */
        ArrayList<Double> entradas = new ArrayList<>();

        entradas.add(0, 0.0);
        entradas.add(1, 0.35);
        entradas.add(2, 0.99);
        entradas.add(3, 0.98);
        entradas.add(4, 1.25);
        entradas.add(5, 1.52);
        entradas.add(6, 1.50);
        entradas.add(7, 1.65);
        entradas.add(8, 2.00);

        return entradas;

    }

    public static ArrayList<Double>  entradasParaVelocidade(double velocMaxVia) {

        /*
          Dados de acordo com as regras de multas estabelecidas pelo Código Brasileiro de Trânsito(CBT):

          Baixo -  Rampa Esquerda( 0 , Valor mínimo de velocidade (10km/h) , Valor da velocidade máxima da via acrescido de 7 km/h)
          Medio -  Triangular( Velocidade máxima da via, Valor da velocidade máxima da via acrescido de 7 km/h , Valor da velocidade máxima da via acrescido de 20% )
          Alto  -  Rampa Direita( Valor da velocidade máxima da via acrescido de (20%)-1 , Valor da velocidade máxima da via acrescido de (35%), 200km/h )
         */

        /* Multa "média" exceder a velocidade em até 20%;
           Multa "grave" exceder a velocidade acima de 20% até 50%;
           Multa "Gravíssima" exceder a velocidade acima de 50%.

           A proposta do presente trabalho irá classificar o motorista da seguinte forma:

           "Bom":   até velocidade da via + 7km/h;
           "Médio": entre velocidade da via até 20% do excedente;
           "Ruim":  acima de 20%.

           //Na Entrada (7) o valor está definido dentro da seguinte regra:

           Para satisfazer a função de pertinência na logica fuzzy, foi adotado o valor de (20% + 50%) / 2 = 35%*/

        ArrayList<Double> entradas = new ArrayList<>();

        entradas.add(0, 0.0);
        entradas.add(1, 10.0);
        entradas.add(2, velocMaxVia+7);
        entradas.add(3, velocMaxVia);
        entradas.add(4, velocMaxVia+7);
        entradas.add(5, velocMaxVia*1.20);
        entradas.add(6, (velocMaxVia*1.20)-1);
        entradas.add(7, velocMaxVia*1.35);
        entradas.add(8, 200.0);

        return entradas;

    }

    public static ArrayList<Double>  entradasParaAceleracoes() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 0.98 , 2.16 )
          Medio -  Triangular( 1.77 , 2.84 , 4.02 )
          Alto  -  Rampa Direita( 3.73 , 5.69 , 5.88 )
         */

        ArrayList<Double> entradas = new ArrayList<>();

        entradas.add(0, 0.00);
        entradas.add(1, 0.98);
        entradas.add(2, 2.16);
        entradas.add(3, 1.77);
        entradas.add(4, 2.84);
        entradas.add(5, 4.02);
        entradas.add(6, 3.73);
        entradas.add(7, 5.69);
        entradas.add(8, 5.88);

        return entradas;

    }

    public static ArrayList<Double>  EntradasParaGerais() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 1.50 , 2.10 )
          Medio -  Triangular( 2.00 , 2.50 , 3.00 )
          Alto  -  Rampa Direita( 2.90 , 3.50 , 4.00)
         */
        ArrayList<Double> entradas = new ArrayList<>();

        entradas.add(0, 0.00);
        entradas.add(1, 1.50);
        entradas.add(2, 2.10);
        entradas.add(3, 2.00);
        entradas.add(4, 2.50);
        entradas.add(5, 3.00);
        entradas.add(6, 2.90);
        entradas.add(7, 3.50);
        entradas.add(8, 4.00);

        return entradas;

    }


}
