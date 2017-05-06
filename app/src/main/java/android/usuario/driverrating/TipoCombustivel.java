package android.usuario.driverrating;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_KEY;
import static android.usuario.driverrating.DriverRatingActivity.TIPOCOMBUSTIVEL_NAME;

public class TipoCombustivel extends AppCompatActivity {

    public static SharedPreferences sharedTipoCombustivel;
    public static SharedPreferences.Editor sharedJTipoCombustivelEditor;

    EditText edtTipoCombustivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_combustivo);

        edtTipoCombustivel = (EditText) findViewById(R.id.edtTipoCombustivel);

        sharedTipoCombustivel = getSharedPreferences(TIPOCOMBUSTIVEL_NAME, Context.MODE_PRIVATE);
        sharedJTipoCombustivelEditor = sharedTipoCombustivel.edit();

        String combustivel = sharedTipoCombustivel.getString(TIPOCOMBUSTIVEL_KEY, String.valueOf(edtTipoCombustivel.getText()));

        if (combustivel != "") {
            edtTipoCombustivel.setText(combustivel);
        }else{
            edtTipoCombustivel.setText("GASOLINA");
        }
    }

    //Guardar valor da janela de tempo
    protected void onPause() {
        super.onPause();
        if (sharedJTipoCombustivelEditor != null) {
            sharedJTipoCombustivelEditor.putString(TIPOCOMBUSTIVEL_KEY, String.valueOf(edtTipoCombustivel.getText()));
            sharedJTipoCombustivelEditor.commit();
        }
    }

    public void btnRetornar(View view) {
        finish();
    }
}
