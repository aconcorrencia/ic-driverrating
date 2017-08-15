package android.usuario.driverrating;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import static android.usuario.driverrating.DriverRatingActivity.PERCENTUALALCOOL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.PERCENTUALALCOOL_NAME;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;
import static java.lang.String.valueOf;

public class TipoCombustivel extends AppCompatActivity {

    public static SharedPreferences sharedTipoCombustivel;
    public static SharedPreferences.Editor sharedTipoCombustivelEditor;
    public static SharedPreferences sharedPercentualAlcool;
    public static SharedPreferences.Editor sharedPercentualAlcoolEditor;

    private String tipoCombustivel;
    private int op;

    EditText edtPercentualAlcool;
    RadioGroup rgOpcoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_combustivo);

        rgOpcoes = (RadioGroup) findViewById(R.id.rgopcoes);

        sharedTipoCombustivel = getSharedPreferences(TIPOCOMBUSTIVEL_NAME, MODE_PRIVATE);
        sharedTipoCombustivelEditor = sharedTipoCombustivel.edit();

        String tipoCombustivelExtenso = sharedTipoCombustivel.getString(TIPOCOMBUSTIVEL_KEY, "").toUpperCase();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgopcoes);
        if (tipoCombustivelExtenso.equals("GASOLINA")) {
            radioGroup.check(R.id.rbGasolina);
        } else if (tipoCombustivelExtenso.equals("DIESEL")) {
            radioGroup.check(R.id.rbDiesel);
        } else if (tipoCombustivelExtenso.equals("FLEX")) {
            radioGroup.check(R.id.rbFlex);
        }

        edtPercentualAlcool = (EditText) findViewById(R.id.edtPercentAlcool);

        sharedPercentualAlcool = getSharedPreferences(PERCENTUALALCOOL_NAME, MODE_PRIVATE);
        sharedPercentualAlcoolEditor = sharedPercentualAlcool.edit();

        String percentualalcool = sharedPercentualAlcool.getString(PERCENTUALALCOOL_KEY, "");

        if (percentualalcool != "") {
            edtPercentualAlcool.setText(percentualalcool);
        }
        else{
            edtPercentualAlcool.setText("25");
        }
    }

    //Guardar o tipo de combustível
    protected void onDestroy() {
        super.onDestroy();
        if (sharedTipoCombustivelEditor != null) {
            sharedTipoCombustivelEditor.putString(TIPOCOMBUSTIVEL_KEY, valueOf(tipoCombustivel));
            sharedTipoCombustivelEditor.commit();
        }

        if (sharedPercentualAlcoolEditor != null) {
            sharedPercentualAlcoolEditor.putString(PERCENTUALALCOOL_KEY, valueOf(edtPercentualAlcool.getText()));
            sharedPercentualAlcoolEditor.commit();
        }
    }

    public void btnRetornar(View view) {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rgopcoes);

        op = rg.getCheckedRadioButtonId();

        if(op==R.id.rbGasolina)
            tipoCombustivel = "GASOLINA";
        else
        if(op==R.id.rbDiesel)
            tipoCombustivel = "DIESEL";
        else
        if(op==R.id.rbFlex)
            tipoCombustivel = "FLEX";

        finish();
    }
}
