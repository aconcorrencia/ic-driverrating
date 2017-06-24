package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosPercursosViagem;

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

    public void inserirDadosPercursosViagem (DadosPercursosViagem dadosPercursosViagem) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(ID_JANELACLASSMOT, dadosPercursosViagem.getId_janelaclassmot());
        valores.put(LONGITUDE, dadosPercursosViagem.getLongitude());
        valores.put(LATITUDE, dadosPercursosViagem.getLatitude());

        db.insert(TABELA, null, valores);
    }
}
