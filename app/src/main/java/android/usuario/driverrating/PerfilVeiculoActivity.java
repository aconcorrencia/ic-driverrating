package android.usuario.driverrating;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.usuario.driverrating.database.DataBaseDriverRating;
import android.usuario.driverrating.database.DataBasePerfis;
import android.usuario.driverrating.domain.Veiculo;
import android.usuario.driverrating.extra.SharedPreferencesKeys;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import static android.usuario.driverrating.DriverRatingActivity.FATORPENALIZACAO_KEY;
import static android.usuario.driverrating.DriverRatingActivity.FATORPENALIZACAO_NAME;
import static android.usuario.driverrating.DriverRatingActivity.menorValorCO2;
import static java.lang.String.valueOf;

import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;

public class PerfilVeiculoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText edtNomePerfil;
    private Spinner spnMarca, spnModelo, spnMotor, spnVersao;
    private Veiculo veiculo;
    private DataBaseDriverRating dataBaseDriverRating;
    private SelectableRoundedImageView imgPerfil;


    public static SharedPreferences sharedFatorCorrecao;
    public static SharedPreferences.Editor sharedFatorCorrecaoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_veiculo);

        sharedFatorCorrecao = getSharedPreferences(FATORPENALIZACAO_NAME, Context.MODE_PRIVATE);
        sharedFatorCorrecaoEditor = sharedFatorCorrecao.edit();

        edtNomePerfil = (EditText) findViewById(R.id.edtNomePerfil);
        spnMarca = (Spinner) findViewById(R.id.spnMarca);
        spnModelo = (Spinner) findViewById(R.id.spnModelo);
        spnMotor = (Spinner) findViewById(R.id.spnMotor);
        spnVersao = (Spinner) findViewById(R.id.spnVersao);

        dataBaseDriverRating = new DataBaseDriverRating(PerfilVeiculoActivity.this);
        if (!dataBaseDriverRating.tableExists()) {
            findViewById(R.id.screenLoading).setVisibility(ScrollView.VISIBLE);
            Thread timer = new Thread() {
                public void run() {
                    dataBaseDriverRating.importFiles();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            findViewById(R.id.screenLoading).setVisibility(ScrollView.GONE);
                            createAdapter();
                            findViewById(R.id.screenMain).setVisibility(ScrollView.VISIBLE);
                        }
                    });
                }
            };
            timer.start();
        } else {
            createAdapter();
            findViewById(R.id.screenMain).setVisibility(ScrollView.VISIBLE);
        }

        veiculo = new Veiculo();
        imgPerfil = (SelectableRoundedImageView) findViewById(R.id.imagePerfil);
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldAskPermissions()) {
                    askPermissions();
                }else{
                    intentActionPick();
                }
            }
        });
    }

    public void createAdapter() {
        ArrayList<String> arrayList = dataBaseDriverRating.leituraArqVeiculosByQuery("marca");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMarca.setAdapter(dataAdapter);
        spnMarca.setOnItemSelectedListener(this);
        spnModelo.setOnItemSelectedListener(this);
        spnMotor.setOnItemSelectedListener(this);
        spnVersao.setOnItemSelectedListener(this);
    }

    /**
     * Pega o caminho real da imagem no dispositivo do usuário
     *
     * @param uri
     * @return
     */
    public String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    /**
     * Caso o usuário selecionou uma foto para o seu perfil salve o bitmap no objeto veiculo
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getRealPathFromURI(selectedImageUri);
                imgPerfil.setImageBitmap(BitmapFactory
                        .decodeFile(selectedImagePath));
                ImageView imageView = new ImageView(PerfilVeiculoActivity.this);
                imageView.setImageBitmap(BitmapFactory
                        .decodeFile(selectedImagePath));
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                veiculo.setFoto(bitmap);
            }
        }
    }


    /**
     * Permissão para o APP acessar fotos, mídia e arquivos do dispositivo
     * OBS: Somente para API>=23
     */
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * Intent para acessar as fotos do dispositivo
     */
    public void intentActionPick(){
        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, 10);
        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10);
        }
    }


    /**
     * Resposta do usuário a permissão para o app acessar fotos, mídia e arquivos do dispositivo
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentActionPick();
                } else {
                    Toast.makeText(PerfilVeiculoActivity.this, "Permissão para acessar arquivos foi negada!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void btnSalvarPerfil(View view) {
        veiculo.setNomeUsuario(edtNomePerfil.getText().toString());
        if (!veiculo.getNomeUsuario().isEmpty()) {
            veiculo = dataBaseDriverRating.leituraArqVeiculosByQueryWhereAll(veiculo);
            DataBasePerfis dataBasePerfis = new DataBasePerfis(PerfilVeiculoActivity.this);
            final long id = dataBasePerfis.inserirVeiculo(veiculo);
            final float fatPenaliz = CalcularFatorPenalizacao();
            if (id != -1) {
                new android.app.AlertDialog.Builder(this)
                        .setMessage("Deseja tornar este perfil como padrão?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*editor = sharedPreferences.edit();
                                editor.putLong("ID", id);
                                editor.putFloat("FatorCorrecao", fatPenaliz);  //Nielson: 10/06/2017
                                editor.apply();*/
                                if (sharedFatorCorrecaoEditor != null) {
                                    sharedFatorCorrecaoEditor.putString(FATORPENALIZACAO_KEY, valueOf(fatPenaliz));
                                    sharedFatorCorrecaoEditor.commit();
                                }
                                Toast.makeText(PerfilVeiculoActivity.this, "Perfil selecionado como padrão!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            } else {
                Toast.makeText(PerfilVeiculoActivity.this, "Erro ao salvar perfil!", Toast.LENGTH_SHORT).show();
            }
        } else {
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Nome de Perfil invalido!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (parent.getId() == R.id.spnMarca) {
            veiculo.setMarca(item);
            ArrayList<String> arrayList = dataBaseDriverRating.leituraArqVeiculosByQueryWhereModelo(veiculo);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnModelo.setAdapter(dataAdapter);
        } else {
            if (parent.getId() == R.id.spnModelo) {
                veiculo.setModelo(item);
                ArrayList<String> arrayList = dataBaseDriverRating.leituraArqVeiculosByQueryWhereMotor(veiculo);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnMotor.setAdapter(dataAdapter);
            } else {
                if (parent.getId() == R.id.spnMotor) {
                    veiculo.setMotor(item);
                    ArrayList<String> arrayList = dataBaseDriverRating.leituraArqVeiculosByQueryWhereVersao(veiculo);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnVersao.setAdapter(dataAdapter);
                } else {
                    if (parent.getId() == R.id.spnVersao) {
                        veiculo.setVersao(item);
                    }
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Responsável pelo cálculo do fator de penalização do motorista em relação à variável CO2.
    private float CalcularFatorPenalizacao(){
        DataBaseDriverRating dataBaseDriverRating = new DataBaseDriverRating(PerfilVeiculoActivity.this);
        menorValorCO2 =  dataBaseDriverRating.selectSmallerCO2();

        float calcValorPenalizacaoCO2 = (float) (menorValorCO2 / veiculo.getCo2());

        return calcValorPenalizacaoCO2;
    }

}
