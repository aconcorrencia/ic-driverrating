package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosColetadosSensores;
import android.usuario.driverrating.domain.DadosColetadosSensoresBuilder;
import android.usuario.driverrating.domain.Veiculo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NIELSON on 27/05/2017.
 */

public class DataBaseColetadosSensores extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "dadoscoletados";
    private static final int VERSAO_AUX = 1;
    public static final String TABELA = "tabdadoscoletados";

    private static final String ID = "_id";
    public static final String USUARIO = "usuario";
    public static final String DISTANCIAPERCORRIDA = "distanciapercorrida";
    public static final String LITROSCOMBUSTIVEL = "litroscombustivel";
    public static final String NOTAVELOCIDADE = "notavelocidade";
    public static final String TIPOCOMBUSTIVEL = "tipocombustivel";

    private Context context;

    public DataBaseColetadosSensores(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + USUARIO + " int, "
                + DISTANCIAPERCORRIDA + " float, "
                + LITROSCOMBUSTIVEL + " float, "
                + NOTAVELOCIDADE + " float, "
                + TIPOCOMBUSTIVEL + " text"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public void inserirDadosColetadosSensores(DadosColetadosSensores dadosColetadosSensores) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(USUARIO, dadosColetadosSensores.getUsuario());
        valores.put(DISTANCIAPERCORRIDA, dadosColetadosSensores.getDistanciaPercorrida());
        valores.put(LITROSCOMBUSTIVEL, dadosColetadosSensores.getLitrosCombustivel());
        valores.put(NOTAVELOCIDADE, dadosColetadosSensores.getNotaVelocidade());
        valores.put(TIPOCOMBUSTIVEL, dadosColetadosSensores.getTipoCombustivel());

        db.insert(TABELA, null, valores);
    }

    public ArrayList<DadosColetadosSensores > selectAll() {
        ArrayList<DadosColetadosSensores> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA, null);
            Log.w("Qtd.", String.valueOf(cursor.getCount()));

            while (cursor.moveToNext()) {
                DadosColetadosSensores ds = new DadosColetadosSensoresBuilder().createDadosColetadosSensores();

                ds.setUsuario(cursor.getInt(1));
                ds.setDistanciaPercorrida(cursor.getFloat(2));
                ds.setLitrosCombustivel(cursor.getFloat(3));
                ds.setNotaVelocidade (cursor.getFloat(4));
                ds.setTipoCombustivel(cursor.getInt(5));

                arrayList.add(ds);
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    public DadosColetadosSensores selectPerfilById(long id) {
        DadosColetadosSensores d = new DadosColetadosSensores();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+USUARIO+"=" + id, null);
            int count=cursor.getCount();
            if(count>0) {
                cursor.moveToLast();
                d.setId(cursor.getInt(0));
                d.setUsuario(cursor.getInt(1));
                d.setDistanciaPercorrida(cursor.getFloat(2));
                d.setLitrosCombustivel(cursor.getFloat(3));
                d.setNotaVelocidade (cursor.getFloat(4));
                d.setTipoCombustivel(cursor.getInt(5));

            }else{
                db.close();
                return null;
            }
            db.close();
        } catch (SQLiteException e) {
            return null;
        }
        return d;
    }
}
