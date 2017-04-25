package android.usuario.driverrating;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.usuario.driverrating.adapter.ListViewAdapter;
import android.usuario.driverrating.adapter.RecyclerViewOnClickListenerHack;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.Veiculo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ListarPerfis extends AppCompatActivity implements RecyclerViewOnClickListenerHack {
    protected RecyclerView mRecyclerView;
    private ArrayList<Veiculo> mVeiculos;
    private ListViewAdapter adapter;
    private DataBasePerfis dataBasePerfis;
    private long idPadrao;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_perfis);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Perfil do Veículo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        idPadrao = sharedPreferences.getLong("ID", -1);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        dataBasePerfis = new DataBasePerfis(this);

        adapter = new ListViewAdapter(ListarPerfis.this, mVeiculos, idPadrao);
        adapter.setRecyclerViewOnClickListenerHack(this);
        adapter.setRecyclerViewOnLongClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ListarPerfis.this, PerfilVeiculoActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        idPadrao = sharedPreferences.getLong("ID", -1);
        adapter.setIdPadrao(idPadrao);
        mVeiculos = dataBasePerfis.selectAll();
        adapter.swap(mVeiculos);
    }

    @Override
    public void onClickListener(View view, int position) {
        editor = sharedPreferences.edit();
        editor.putLong("ID", mVeiculos.get(position).getId());
        editor.apply();
        Toast.makeText(ListarPerfis.this, "Perfil selecionado!", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Apaga o perfil pressionado, se for o perfil padrão seta o ID padrão para -1
     *
     * @param view
     * @param position
     */
    @Override
    public void onLongPressClickListener(View view, final int position) {
        new android.app.AlertDialog.Builder(this)
                .setMessage("Deseja apagar este perfil?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (idPadrao == mVeiculos.get(position).getId()) {
                            editor = sharedPreferences.edit();
                            editor.putLong("ID", -1);
                            editor.apply();
                        }
                        dataBasePerfis.deletaPerfil(mVeiculos.get(position).getId());
                        adapter.removeListItem(position);
                        Toast.makeText(ListarPerfis.this, "Perfil apagado!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
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
