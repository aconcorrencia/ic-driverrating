package android.usuario.driverrating;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_NAME;
import static android.usuario.driverrating.DriverRatingActivity.JANELATEMPO_KEY;
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

        sharedJanelaTempo = getSharedPreferences(JANELATEMPO_NAME, Context.MODE_PRIVATE);
        sharedJanelaTempoEditor = sharedJanelaTempo.edit();

        String janela = sharedJanelaTempo.getString(JANELATEMPO_KEY, String.valueOf(edtJanelaTempo.getText()));

        if (janela != "") {
            edtJanelaTempo.setText(janela);
        }
        else{
            edtJanelaTempo.setText("0");
        }

    }

    //Guardar valor da janela de tempo
    protected void onPause() {
        super.onPause();
        if (sharedJanelaTempoEditor != null) {
            sharedJanelaTempoEditor.putString(JANELATEMPO_KEY, valueOf(edtJanelaTempo.getText()));
            sharedJanelaTempoEditor.commit();
        }
    }

    public void btnRetornar(View view) {
      finish();
    }
}
