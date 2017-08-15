package android.usuario.driverrating;

import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.usuario.driverrating.database.DataBaseResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;
import android.usuario.driverrating.extra.ClassificadorEntradasSaidas;
import android.usuario.driverrating.extra.ClassificadorFuzzy;
import android.view.View;
import android.widget.TextView;
import static android.usuario.driverrating.DriverRatingActivity.notaConsumoCombustivelGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaEmissaoCO2Geral;
import static android.usuario.driverrating.DriverRatingActivity.notaVelocidadeGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoLongitudinalGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoTransversalGeral;



public class ResultadosClassificacao extends AppCompatActivity {

    private DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista;

    TextView tvTituloCons,
             tvNotaCons,
             tvClassCons,

    tvTituloCO2,
            tvNotaCO2,
            tvClassCO2,

    tvTituloVeloc,
            tvNotaVeloc,
            tvClassVeloc,

    tvTituloAccLong,
            tvNotaAccLong,
            tvClassAccLong,

    tvTituloAccTrans,
            tvNotaAccTrans,
            tvClassAccTrans;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_classificacao);

        tvTituloCons = (TextView) findViewById(R.id.tvTituloCons);
        tvNotaCons = (TextView) findViewById(R.id.tvNotaCons);
        tvClassCons = (TextView) findViewById(R.id.tvClassCons);

        tvTituloCO2 = (TextView) findViewById(R.id.tvTituloCO2);
        tvNotaCO2 = (TextView) findViewById(R.id.tvNotaCO2);
        tvClassCO2 = (TextView) findViewById(R.id.tvClassCO2);

        tvTituloVeloc = (TextView) findViewById(R.id.tvTituloVeloc);
        tvNotaVeloc = (TextView) findViewById(R.id.tvNotaVeloc);
        tvClassVeloc = (TextView) findViewById(R.id.tvClassVeloc);

        tvTituloAccLong = (TextView) findViewById(R.id.tvTituloAccLong);
        tvNotaAccLong = (TextView) findViewById(R.id.tvNotaAccLong);
        tvClassAccLong = (TextView) findViewById(R.id.tvClassAccLong);

        tvTituloAccTrans = (TextView) findViewById(R.id.tvTituloAccTrans);
        tvNotaAccTrans = (TextView) findViewById(R.id.tvNotaAccTrans);
        tvClassAccTrans = (TextView) findViewById(R.id.tvClassAccTrans);

        //Armazena o número do log desejado;
        int logClass = 27;
        DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
        dataBaseResultadosClassificacaoMotorista.selectResultadosClassificacaoByIdLog(logClass);

        //Envia para o classificador, os dados de saída.
        ClassificadorEntradasSaidas.SaidasParaClassificador();

        // Envia para o classificador, os dados de Entradas gerais.
        ClassificadorEntradasSaidas.EntradasParaGerais();

        ClassificadorFuzzy.calcularNotas("geraisConsumo", notaConsumoCombustivelGeral);
        tvNotaCons.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCons.setText("Classificação: "+ClassificadorFuzzy.classe);

        ClassificadorFuzzy.calcularNotas("geraisCO2", notaEmissaoCO2Geral);
        tvNotaCO2.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassCO2.setText("Classificação: "+ClassificadorFuzzy.classe);

        // Envia para o classificador, os dados de Entrada da variável Velocidade.
        ClassificadorFuzzy.calcularNotas("geraisVelocidade", notaVelocidadeGeral);
        tvNotaVeloc.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassVeloc.setText("Classificação: "+ClassificadorFuzzy.classe);

        ClassificadorFuzzy.calcularNotas("geraisAcc", notaAceleracaoLongitudinalGeral);
        tvNotaAccLong.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassAccLong.setText("Classificação: "+ClassificadorFuzzy.classe);

        ClassificadorFuzzy.calcularNotas("geraisAcc", notaAceleracaoTransversalGeral);
        tvNotaAccTrans.setText("Nota: "+new DecimalFormat("0.00").format(ClassificadorFuzzy.nota));
        tvClassAccTrans.setText("Classificação: "+ClassificadorFuzzy.classe);

    }

    public void btnRetornarGerais (View view) {

        finish();
    }

}
