package android.usuario.driverrating.extra;

import static android.usuario.driverrating.extra.ClassificadorFuzzy.ENTRADAS;
import static android.usuario.driverrating.extra.ClassificadorFuzzy.SAIDAS;

/**
 * Created by NIELSON on 29/04/2017.
 */

public class ClassificadorEntradasSaidas {

    public static void SaidasParaClassificador() {

        SAIDAS.clear();

        SAIDAS.add(1.65);
        SAIDAS.add(2.50);
        SAIDAS.add(4.00);
    }

    public static void EntradasParaConsumoCombustivel() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 0.50 , 1.00 )
          Medio -  Triangular( 0.65 , 1.00 , 1.35 )
          Alto  -  Rampa Direita( 1.00 , 1.40 , 1.50)
         */
        ENTRADAS.clear();

        ENTRADAS.add(0, 0.0);
        ENTRADAS.add(1, 0.5);
        ENTRADAS.add(2, 1.0);
        ENTRADAS.add(3, 0.65);
        ENTRADAS.add(4, 1.0);
        ENTRADAS.add(5, 1.35);
        ENTRADAS.add(6, 1.0);
        ENTRADAS.add(7, 1.4);
        ENTRADAS.add(8, 1.5);

    }

    public static void EntradasParaCO2() {

        /*
          Dados de acordo com artigo de referência:

          Baixo -  Rampa Esquerda( 0.00 , 9.50 , 12.50 )
          Medio -  Triangular( 0.65 , 1.00 , 1.35 )
          Alto  -  Rampa Direita( 1.00 , 1.40 , 1.50 )
         */

        ENTRADAS.clear();

        ENTRADAS.add(0, 0.0);
        ENTRADAS.add(1, 9.5);
        ENTRADAS.add(2, 12.5);
        ENTRADAS.add(3, 12.0);
        ENTRADAS.add(4, 13.0);
        ENTRADAS.add(5, 14.0);
        ENTRADAS.add(6, 13.5);
        ENTRADAS.add(7, 15.0);
        ENTRADAS.add(8, 20.0);

    }

    public static void EntradasParaVelocidade(double velocMaxVia) {

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

        ENTRADAS.clear();

        ENTRADAS.add(0, 0.0);
        ENTRADAS.add(1, 10.0);
        ENTRADAS.add(2, velocMaxVia+7);
        ENTRADAS.add(3, velocMaxVia);
        ENTRADAS.add(4, velocMaxVia+7);
        ENTRADAS.add(5, velocMaxVia*1.20);
        ENTRADAS.add(6, (velocMaxVia*1.20)-1);
        ENTRADAS.add(7, velocMaxVia*1.35);
        ENTRADAS.add(8, 200.0);

    }

}
