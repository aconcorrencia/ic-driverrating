package android.usuario.driverrating;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.usuario.driverrating.extra.SharedPreferencesKeys;
import android.usuario.driverrating.extra.Utils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import static java.lang.String.valueOf;

/**
 * Define o tipo de Combustivel e o Percentual de Álcool
 * Valores Default:
 *          Tipo de Combustivel: Gasolina
 *          Percentual de Álcool: 25%
 *
 */
public class TipoCombustivel extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private int tipoCombustivel;
    private int op;

    EditText edtPercentualAlcool;
    RadioGroup rgOpcoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_combustivo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Tipo de Combustível");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rgOpcoes = (RadioGroup) findViewById(R.id.rgopcoes);
        edtPercentualAlcool = (EditText) findViewById(R.id.edtPercentAlcool);

        sharedPreferences = getSharedPreferences(SharedPreferencesKeys.DATABASE, MODE_PRIVATE);
        int tipoCombustivelExtenso = sharedPreferences.getInt(SharedPreferencesKeys.TIPO_COMBUSTIVEL, Utils.GASOLINA);

        if (tipoCombustivelExtenso == Utils.GASOLINA) {
            rgOpcoes.check(R.id.rbGasolina);
        } else if (tipoCombustivelExtenso == Utils.DIESEL) {
            rgOpcoes.check(R.id.rbDiesel);
        } else if (tipoCombustivelExtenso == Utils.FLEX) {
            rgOpcoes.check(R.id.rbFlex);
        }

        int percentual_alcool = sharedPreferences.getInt(SharedPreferencesKeys.PERCENTUAL_ALCOOL, 25);

        edtPercentualAlcool.setText(valueOf(percentual_alcool));

    }

    /**
     * Guardar o tipo de combustível
     */
    public void btnRetornar(View view) {
        int perc_alcool = Integer.parseInt(edtPercentualAlcool.getText().toString());
        op = rgOpcoes.getCheckedRadioButtonId();

        if (op == R.id.rbGasolina)
            tipoCombustivel = Utils.GASOLINA;
        else if (op == R.id.rbDiesel)
            tipoCombustivel = Utils.DIESEL;
        else if (op == R.id.rbFlex)
            tipoCombustivel = Utils.FLEX;

        editor = sharedPreferences.edit();
        editor.putInt(SharedPreferencesKeys.PERCENTUAL_ALCOOL,perc_alcool);
        editor.putInt(SharedPreferencesKeys.TIPO_COMBUSTIVEL,tipoCombustivel);
        editor.apply();
        finish();
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
