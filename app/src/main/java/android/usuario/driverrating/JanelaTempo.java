package android.usuario.driverrating;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.usuario.driverrating.extra.SharedPreferencesKeys;
import android.usuario.driverrating.extra.Utils;
import android.view.View;
import android.widget.EditText;


import static java.lang.String.valueOf;

public class JanelaTempo extends AppCompatActivity {

    public static SharedPreferences sharedJanelaTempo;
    public static SharedPreferences.Editor sharedJanelaTempoEditor;

    EditText edtJanelaTempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_janela_tempo);

        edtJanelaTempo = (EditText) findViewById(R.id.edtJanelaTempo);

        sharedJanelaTempo = getSharedPreferences(SharedPreferencesKeys.DATABASE, Context.MODE_PRIVATE);

        int janela = sharedJanelaTempo.getInt(SharedPreferencesKeys.JANELA_TEMPO, Utils.JANELA_DEFAUT);

        edtJanelaTempo.setText(valueOf(janela));

    }

    //Guardar valor da janela de tempo
    public void btnRetornar(View view) {
        sharedJanelaTempoEditor = sharedJanelaTempo.edit();
        sharedJanelaTempoEditor.putInt(SharedPreferencesKeys.JANELA_TEMPO, Integer.parseInt(edtJanelaTempo.getText().toString()));
        sharedJanelaTempoEditor.apply();
        finish();
    }
}
