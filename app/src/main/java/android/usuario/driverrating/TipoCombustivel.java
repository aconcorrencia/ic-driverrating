package android.usuario.driverrating;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;

public class TipoCombustivel extends AppCompatActivity {

    public static SharedPreferences sharedTipoCombustivel;
    public static SharedPreferences.Editor sharedTipoCombustivelEditor;

    private String tipoCombustivel;

    private int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_combustivo);

        sharedTipoCombustivel = getSharedPreferences(TIPOCOMBUSTIVEL_NAME, Context.MODE_PRIVATE);
        sharedTipoCombustivelEditor = sharedTipoCombustivel.edit();

        String tipoCombustivelExtenso = sharedTipoCombustivel.getString(TIPOCOMBUSTIVEL_KEY, "").toUpperCase();

        if (tipoCombustivelExtenso.equals("GASOLINA")) {
            ((RadioGroup) findViewById(R.id.rgopcoes)).check(R.id.rbGasolina);
        } else if (tipoCombustivelExtenso.equals("DIESEL")) {
            ((RadioGroup) findViewById(R.id.rgopcoes)).check(R.id.rbDiesel);
        } else if (tipoCombustivelExtenso.equals("FLEX")) {
            ((RadioGroup) findViewById(R.id.rgopcoes)).check(R.id.rbFlex);
        }

        //String combustivel = sharedTipoCombustivel.getString(TIPOCOMBUSTIVEL_KEY, String.valueOf(tipoCombustivel));

    }

    //Guardar o tipo de combustível
    protected void onPause() {
        super.onPause();
        if (sharedTipoCombustivelEditor != null) {
            sharedTipoCombustivelEditor.putString(TIPOCOMBUSTIVEL_KEY, String.valueOf(tipoCombustivel));
            sharedTipoCombustivelEditor.commit();
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
