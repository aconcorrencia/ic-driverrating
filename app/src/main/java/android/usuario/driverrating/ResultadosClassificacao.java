package android.usuario.driverrating;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.usuario.driverrating.adapter.ListViewAdapter;
import android.usuario.driverrating.adapter.RecyclerViewOnClickListenerHack;
import android.usuario.driverrating.adapter.ResultadosAdapter;
import android.usuario.driverrating.database.DataBaseLogClassificacao;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.database.DataBaseResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.DadosColetadosSensores;
import android.usuario.driverrating.domain.DadosLogClassificacao;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;
import android.usuario.driverrating.domain.Veiculo;
import android.usuario.driverrating.extra.ClassificadorEntradasSaidas;
import android.usuario.driverrating.extra.ClassificadorFuzzy;
import android.usuario.driverrating.extra.SharedPreferencesKeys;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.usuario.driverrating.extra.Utils.getDate;
import static android.usuario.driverrating.extra.Utils.getHour;

public class ResultadosClassificacao extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    protected RecyclerView mRecyclerView;
    private DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista;
    private ArrayList<DadosLogClassificacao> mList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_classificacao);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setSubtitle("t");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.DATABASE, MODE_PRIVATE);
        long idPerfil = sharedPreferences.getLong(SharedPreferencesKeys.ID_USER, -1);
        DataBaseLogClassificacao dataBaseLogClassificacao = new DataBaseLogClassificacao(this);

        mList = dataBaseLogClassificacao.selectLogByUserId(idPerfil);
        if (mList.size() > 0) {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(llm);
            ResultadosAdapter adapter = new ResultadosAdapter(this, mList);
            adapter.setRecyclerViewOnClickListenerHack(this);
            mRecyclerView.setAdapter(adapter);
        }else{
            findViewById(R.id.lyNoResults).setVisibility(View.VISIBLE);
        }


        /*dadosResultadosClassificacaoMotorista= new DadosResultadosClassificacaoMotorista();
        dadosResultadosClassificacaoMotorista.setId_log(9);
        dadosResultadosClassificacaoMotorista.setNota_acel_trans(15);
        dadosResultadosClassificacaoMotorista.setClas_velocid("CLASS");
        long idClassifMot = persistirClassificacoesMotorista(dadosResultadosClassificacaoMotorista);
        Log.w("ID","I: "+idClassifMot);*/


    }

    private long persistirClassificacoesMotorista(DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista) {

        // Será utilizado a base de dados para armazenamento das classificações efetuadas a cada valor da janela de tempo estabelecida pelo motorista.

        DataBaseResultadosClassificacaoMotorista dataBaseResultadosClassificacaoMotorista = new DataBaseResultadosClassificacaoMotorista(this);
        return dataBaseResultadosClassificacaoMotorista.inserirDadosResultClassMot(dadosResultadosClassificacaoMotorista);
    }


    public void btnRetornarGerais(View view) {
        finish();
    }

    @Override
    public void onClickListener(View view, int position) {

        switch (view.getId()) {
            case R.id.btnVer:
                Intent it = new Intent(this, VerResultadoActivity.class);
                it.putExtra("ID_LOG", mList.get(position).getId());
                startActivity(it);
                break;

            case R.id.btnApagar:

                break;
        }

    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
