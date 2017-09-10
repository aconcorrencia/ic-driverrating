package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.R;
import android.usuario.driverrating.domain.DadosPercursosViagem;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by NIELSON on 22/06/2017.
 */

public class DataBasePercursosViagem extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "rotaspercurso";
    private static final int VERSAO_AUX = 1;
    public static final String TABELA = "tabrotaspercurso";

    private static final String ID = "_id";
    public static final String ID_JANELACLASSMOT = "idjanelaclassmot";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public DataBasePercursosViagem(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + ID_JANELACLASSMOT + " int, "
                + LONGITUDE + " double, "
                + LATITUDE + " double "
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public long  inserirDadosPercursosViagem (DadosPercursosViagem dadosPercursosViagem) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(ID_JANELACLASSMOT, dadosPercursosViagem.getId_janelaclassmot());
        valores.put(LONGITUDE, dadosPercursosViagem.getLongitude());
        valores.put(LATITUDE, dadosPercursosViagem.getLatitude());

        return db.insert(TABELA, null, valores);
    }

    public PolylineOptions selectPercusoByClassMot (ArrayList<DadosResultadosClassificacaoMotorista> dadosResultadosClassificacaoMotorista) {

        SQLiteDatabase db = getReadableDatabase();
        PolylineOptions rectOptions = new PolylineOptions();

        for (DadosResultadosClassificacaoMotorista d : dadosResultadosClassificacaoMotorista) {
            Cursor cursor = db.rawQuery("SELECT *FROM " + TABELA, null);
           // Cursor cursor = db.rawQuery("SELECT *FROM " + TABELA + " WHERE " + ID_JANELACLASSMOT + " = " + d.getId_janclasmot(), null);
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    rectOptions.add(new LatLng(cursor.getDouble(3), cursor.getDouble(2)));
                }
            }
        }
        return rectOptions;
    }

    public ArrayList<LatLng> selectPercusoByClassMot2 (ArrayList<DadosResultadosClassificacaoMotorista> dadosResultadosClassificacaoMotorista) {

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LatLng> l = new ArrayList<>();

        for (DadosResultadosClassificacaoMotorista d : dadosResultadosClassificacaoMotorista) {
            //Cursor cursor = db.rawQuery("SELECT *FROM " + TABELA, null);
             Cursor cursor = db.rawQuery("SELECT *FROM " + TABELA + " WHERE " + ID_JANELACLASSMOT + " = " + d.getId_janclasmot(), null);
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    l.add(new LatLng(cursor.getDouble(3), cursor.getDouble(2)));
                }
            }
        }
        return l;
    }
}
