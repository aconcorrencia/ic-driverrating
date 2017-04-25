package android.usuario.driverrating;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.usuario.driverrating.OBD.IOBDBluetooth;
import android.usuario.driverrating.OBD.OBDInfo;
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

import static android.R.attr.angle;
import static android.R.attr.pivotX;
import static android.R.attr.pivotY;


public class DriverRatingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private TextView txtNomePerfil, txtMarca, txtModelo;
    private SelectableRoundedImageView imgPerfil;
    private ImageLoader mImageLoader;
    private int rotate = -80;
    private ImageView imageView;
    float newAngle, oldAngle;

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
        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Atualiza o cabeçalho do menu para o perfil padrão
     */
    public void headerReflesh() {
        long id = sharedPreferences.getLong("ID", -1);
        if (id != -1) {
            DataBasePerfis dataBasePerfis = new DataBasePerfis(this);
            Veiculo veiculo = dataBasePerfis.selectPerfilById(id);
            if (veiculo != null) {
                txtNomePerfil.setText("Perfil: " + veiculo.getNomeUsuario());
                txtMarca.setText("Marca: " + veiculo.getMarca().trim());
                txtModelo.setText("Modelo: " + veiculo.getModelo().trim());
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
