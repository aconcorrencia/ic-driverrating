package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.Veiculo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by NIELSON on 26/11/2016.
 */

public class DataBaseDriverRating extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "veiculos";
    private static final int VERSAO_AUX = 1;

    public static final String TABELA = "refveiculos";
    private static final String ID = "_id";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String MOTOR = "motor";
    public static final String VERSAO = "versao";
    public static final String TRANSMISSAO = "transmissao";
    public static final String EMISSNOX = "emissnox";
    public static final String EMISSCO2 = "emissco2";
    public static final String CONSETANCID = "consetancid";
    public static final String CONSETANESTR = "consetanestr";
    public static final String CONSGASCID = "consgascid";
    public static final String CONSGASESTR = "consgasestr";
    private Context context;

    public DataBaseDriverRating(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + MARCA + " text, "
                + MODELO + " text, "
                + MOTOR + " text, "
                + VERSAO + " text, "
                + TRANSMISSAO + " text, "
                + EMISSNOX + " double, "
                + EMISSCO2 + " double, "
                + CONSETANCID + " double, "
                + CONSETANESTR + " double, "
                + CONSGASCID + " double, "
                + CONSGASESTR + " double"
                + ")";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public boolean importFiles() {

        String nomarq = "Veiculos_Leves_Cols_Necessarios.txt";

        try {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream EntradaArq = assetManager.open(nomarq);
            InputStreamReader EntradaArqLeitor = new InputStreamReader(EntradaArq);
            BufferedReader bufferedLeitor = new BufferedReader(EntradaArqLeitor);

            String linha = bufferedLeitor.readLine();

            while (linha != null) {
                try {
                    String[] dados = linha.split(";"); //Separa os campos através do separador ';'

                    String marca = dados[0]; //pega o primeiro campo que representa a marca
                    String modelo = dados[1]; //pega o segundo campo que representa o modelo
                    String motor = dados[2]; //pega o terceiro campo que representa o motor
                    String versao = dados[3]; //pega o quarto campo que representa a versão
                    String transmissao = dados[4]; //pega o quinto campo que representa a transmissão
                    String emissnox = dados[5]; //pega o quinto campo que representa a emissão de NOx
                    String emissco2 = dados[6]; //pega o sexto campo que representa a emissão de CO2
                    String consetancid = dados[7]; //pega o sétimo campo que representa o consumo de etanol na cidade
                    String consetanestr = dados[8]; //pega o oitavo campo que representa o consumo de etanol na estrada
                    String consgascid = dados[9]; //pega o nono campo que representa o consumo de gasolina na cidade
                    String consgasestr = dados[10]; //pega o décimo campo que representa o consumo de gasolina na estrada

                    double emissnox_aux = Double.parseDouble(emissnox.replaceAll(" ", ""));
                    double emissco2_aux = Double.parseDouble(emissco2.replaceAll(" ", ""));
                    double consetancid_aux = Double.parseDouble(consetancid.replaceAll(" ", ""));
                    double consetanestr_aux = Double.parseDouble(consetanestr.replaceAll(" ", ""));
                    double consgascid_aux = Double.parseDouble(consgascid.replaceAll(" ", ""));
                    double consgasestr_aux = Double.parseDouble(consgasestr.replaceAll(" ", ""));

                    ContentValues valores = new ContentValues();

                    valores.put(MARCA, marca);
                    valores.put(MODELO, modelo);
                    valores.put(MOTOR, motor);
                    valores.put(VERSAO, versao);
                    valores.put(TRANSMISSAO, transmissao);
                    valores.put(EMISSNOX, emissnox_aux);
                    valores.put(EMISSCO2, emissco2_aux);
                    valores.put(CONSETANCID, consetancid_aux);
                    valores.put(CONSETANESTR, consetanestr_aux);
                    valores.put(CONSGASCID, consgascid_aux);
                    valores.put(CONSGASESTR, consgasestr_aux);
                    SQLiteDatabase db = getWritableDatabase();
                    db.insert(TABELA, null, valores);
                    linha = bufferedLeitor.readLine(); // lê da segunda até a última linha

                } catch (IOException e) {
                    Log.w("Test", e.getMessage());
                    System.err.printf("Erro na leitura do arquivo: %s.\n", e.getMessage());
                }
            }
            EntradaArq.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //Ler e traz os dados da tabela refveiculos para os SPINNER = "MARCA".

    public ArrayList<String> leituraArqVeiculosByQuery(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("Select distinct " + str + " from " + TABELA, null);

            int nomeCol = cursor.getColumnIndex(str);

            while (cursor.moveToNext()) {
                arrayList.add(cursor.getString(nomeCol));
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    //Ler e traz os dados da tabela refveiculos para os SPINNER = "MODELO".

    public ArrayList<String> leituraArqVeiculosByQueryWhereModelo(Veiculo veiculo) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("Select distinct modelo from " + TABELA + " where marca='" + veiculo.getMarca()+"' order by modelo", null);
            while (cursor.moveToNext()) {
                //Log.w("test","dfs: "+cursor.getString(0));
                arrayList.add(cursor.getString(0));
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    //Ler e traz os dados da tabela refveiculos para os SPINNER = "MOTOR".

    public ArrayList<String> leituraArqVeiculosByQueryWhereMotor(Veiculo veiculo) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("Select distinct motor from " + TABELA + " where marca='" + veiculo.getMarca()+"' and modelo='"+veiculo.getModelo()+"' order by motor", null);
            while (cursor.moveToNext()) {
                //Log.w("test","dfs: "+cursor.getString(0));
                arrayList.add(cursor.getString(0));
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    //Ler e traz os dados da tabela refveiculos para os SPINNER = "VERSAO".

    public ArrayList<String> leituraArqVeiculosByQueryWhereVersao(Veiculo veiculo) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT DISTINCT versao FROM " + TABELA + " WHERE marca='" + veiculo.getMarca()+"' AND modelo='"+veiculo.getModelo()+"' AND motor='"+veiculo.getMotor()+"' order by versao", null);
            while (cursor.moveToNext()) {
                //Log.w("test","dfs: "+cursor.getString(0));
                arrayList.add(cursor.getString(0));
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;

    }

    public Veiculo leituraArqVeiculosByQueryWhereAll(Veiculo veiculo) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT  " +EMISSNOX+"," +EMISSCO2+","+ CONSETANCID+","+CONSETANESTR+"," +CONSGASCID+","+CONSGASESTR+" FROM "
                    +TABELA + " WHERE marca='" + veiculo.getMarca()+"' AND modelo='"+veiculo.getModelo()+"' AND motor ='"+veiculo.getMotor()+"' AND versao='"+veiculo.getVersao()+"'";
            //Log.w("test","guh"+sql);
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                veiculo.setNox(Double.parseDouble(cursor.getString(0)));
                veiculo.setCo2(Double.parseDouble(cursor.getString(1)));
                veiculo.setEtanolCidade(Double.parseDouble(cursor.getString(2)));
                veiculo.setEtanolEstrada(Double.parseDouble(cursor.getString(3)));
                veiculo.setGasolinaCidade(Double.parseDouble(cursor.getString(4)));
                veiculo.setGasolinaEstrada(Double.parseDouble(cursor.getString(5)));
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return veiculo;
    }

    public boolean tableExists(){
        SQLiteDatabase db = getWritableDatabase();
        String count = "SELECT count(*) FROM "+TABELA;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0)return true;
        else return false;
    }

    //Recupera o menor valor de CO2 na tabela de referência do INMETRO.
    public double selectSmallerCO2() {
        double smallerCO2 = 10000.0;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA, null);

            while (cursor.moveToNext()) {

                if ((cursor.getDouble(7) < smallerCO2) && (Double.parseDouble(cursor.getString(7)) != 0.0)) {
                   smallerCO2 = Double.parseDouble(cursor.getString(7));
                }
            }

            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return smallerCO2;
    }
}
