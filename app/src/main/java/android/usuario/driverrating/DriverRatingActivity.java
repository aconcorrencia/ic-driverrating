package android.usuario.driverrating;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.usuario.driverrating.OBD.IOBDBluetooth;
import android.usuario.driverrating.OBD.OBDInfo;
import android.usuario.driverrating.database.DataBaseDriverRating;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.Veiculo;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.base.Speedometer;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;

import static android.R.attr.angle;
import static android.R.attr.pivotX;
import static android.R.attr.pivotY;
import static android.R.attr.sharedUserId;
import static android.content.Context.MODE_PRIVATE;
import static android.usuario.driverrating.R.styleable.NavigationView;
import static android.usuario.driverrating.R.styleable.SelectableRoundedImageView;
import static android.usuario.driverrating.R.styleable.Toolbar;


public class DriverRatingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    public static final String JANELATEMPO_NAME = "janela_tempo";
    public static final String JANELATEMPO_KEY = "janela";

    public static final String TIPOCOMBUSTIVEL_NAME = "tipo_combustivel";
    public static final String TIPOCOMBUSTIVEL_KEY = "combustivel";

    public static final String PERCENTUALALCOOL_NAME = "percentualalcool_name";
    public static final String PERCENTUALALCOOL_KEY = "percentualalcool";

    public static final String FATORPENALIZACAO_NAME = "fatorcorrecao_name";
    public static final String FATORPENALIZACAO_KEY = "fatorcorrecao";

    public static float densityFuel = 0; //Densidade do combstível
    public static String tipoCombustivel = "1";
    public static String percentualAlcool = "0";
    public static String fatorPenalizacaoCO2 = "0";

    //Atributos responsáveis por armazenar os resultados das classificações - Início
    public static double notaConsumoCombustivel;
    public static String classificacaoConsumoCombustivel;

    public static double notaConsumoCombustivelGeral;

    public static double notaEmissaoCO2;
    public static String classificacaoEmissaoCO2;

    public static double notaEmissaoCO2Geral;

    public static double notaVelocidade;
    public static String classificacaoVelocidade;

    public static double notaVelocidadeGeral;

    public static double notaAceleracaoLongitudinal;
    public static String classificacaoAceleracaoLongitudinal;

    public static double notaAceleracaoLongitudinalGeral;

    public static double notaAceleracaoTransversal;
    public static String classificacaoAceleracaoTransversal;

    public static double notaAceleracaoTransversalGeral;

    //Atributos responsáveis por armazenar os resultados das classificações - Final

    private TextView txtNomePerfil, txtMarca, txtModelo, txtFatorPenaliz;
    private SelectableRoundedImageView imgPerfil;
    private ImageLoader mImageLoader;
    private int rotate = -80;
    private ImageView imageView;
    float newAngle, oldAngle;

    public static double menorValorCO2;
    public static int ultimaJanela;
    public static int ultimoLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        //Speedometer speedometer = (Speedometer)findViewById(R.id.speedView);
        //speedometer.speedTo(50, 4000);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_driver_rating);
        imgPerfil = (SelectableRoundedImageView) headerView.findViewById(R.id.imageView);
        txtNomePerfil = (TextView) headerView.findViewById(R.id.txtNomePerfil);
        txtMarca = (TextView) headerView.findViewById(R.id.txtMarca);
        txtModelo = (TextView) headerView.findViewById(R.id.txtModelo);
        txtFatorPenaliz = (TextView) headerView.findViewById(R.id.txtFatorPenaliz);

        navigationView.setNavigationItemSelectedListener(this);

        //read(OBDInfo.INTAKE_PRESSURE|OBDInfo.RPM);

    }

    @Override
    protected void onResume() {
        super.onResume();
        headerReflesh();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_rating, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_car) {
            Intent it = new Intent(DriverRatingActivity.this, ListarPerfis.class);
            startActivity(it);
        } else if (id == R.id.ic_bluetooth) {
            Intent it = new Intent(DriverRatingActivity.this, ProcurarActivity.class);
            startActivity(it);
        } else if (id == R.id.ic_janelaTempo ) {
            Intent it = new Intent(DriverRatingActivity.this, JanelaTempo.class);
            startActivity(it);
        } else if (id == R.id.ic_tipoCombustivel ) {
            Intent it = new Intent(DriverRatingActivity.this, TipoCombustivel.class);
            startActivity(it);
        } else if (id == R.id.ic_ClassificacaoGeral ) {
            Intent it = new Intent(DriverRatingActivity.this,  ResultadosClassificacao.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Atualiza o cabeçalho do menu para o perfil padrão
     */
    public void headerReflesh() {
        long id =  sharedPreferences.getLong("ID", -1);
        //float fatorPenalizacaoCO2 = sharedPreferences.getFloat("FatorCorrecao",-1);

        //Restaura as preferencias gravadas em fator de penalização.
        SharedPreferences recuperaSharedPercFP = getSharedPreferences(FATORPENALIZACAO_NAME, 0);
        fatorPenalizacaoCO2 = recuperaSharedPercFP.getString(FATORPENALIZACAO_KEY, "");
        float fatPenaCO2 = Float.parseFloat(fatorPenalizacaoCO2);

        if (id != -1) {
            DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
            Veiculo veiculo = dataBasePerfis.selectPerfilById(id);
            if (veiculo != null) {
                txtNomePerfil.setText("Perfil: " + veiculo.getNomeUsuario());
                txtMarca.setText("Marca: " + veiculo.getMarca().trim());
                txtModelo.setText("Modelo: " + veiculo.getModelo().trim());
                txtFatorPenaliz.setText("Fator de Penalização CO2: " + new DecimalFormat("0.000").format(fatPenaCO2));

                if (veiculo.getFoto() == null)
                    imgPerfil.setImageResource(R.drawable.img_car2);
                else
                    imgPerfil.setImageBitmap(veiculo.getFoto());
            }
        } else {
            txtNomePerfil.setText("Sem perfil");
            txtMarca.setText("Marca:");
            txtModelo.setText("Modelo:");
            imgPerfil.setImageResource(R.drawable.img_car2);
        }
    }

    public void btnIniciarClassificacao(View view) {
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String mac = sharedPreferences.getString("addressDevice", "");
        long idPerfil = sharedPreferences.getLong("ID", -1);
        if (!mac.equals("")) {
            if (idPerfil != -1) {
                Intent it = new Intent(this, IniciarClassificacao.class);
                startActivity(it);
            } else {
                new android.app.AlertDialog.Builder(this)
                        .setMessage("Sem perfil selecionado. Deseja selecionar?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(DriverRatingActivity.this, ListarPerfis.class);
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

        } else {
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Sem dispositivo selecionado. Deseja selecionar?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent it = new Intent(DriverRatingActivity.this, ProcurarActivity.class);
                            startActivity(it);
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }


    }


}
