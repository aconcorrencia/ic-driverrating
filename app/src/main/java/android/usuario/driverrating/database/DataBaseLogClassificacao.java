package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosLogClassificacao;

/**
 * Created by NIELSON on 22/06/2017.
 */


public class DataBaseLogClassificacao  extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "logclass";
    private static final int VERSAO_AUX = 1;
    public static final String TABELA = "tblogclass";

    private static final String ID = "_id";
    public static final  String USUARIO = "int";
    public static final String DATA = "data";
    public static final String HORA = "hora";

    public DataBaseLogClassificacao(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + USUARIO + " int, "
                + DATA + " Date, "
                + HORA + " long "
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public void inserirDadosLogClassificacao(DadosLogClassificacao dadosLogClassificacao) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(USUARIO, dadosLogClassificacao.getUsuario());
        valores.put(DATA, String.valueOf(dadosLogClassificacao.getData()));
        valores.put(HORA, dadosLogClassificacao.getHora());

        db.insert(TABELA, null, valores);
    }

    //Recupera o último ID do Log.
    public int selectUltimoLog(long id) {
        int ultimoLog = 0;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+USUARIO+"=" + id, null);

            while (cursor.moveToNext()) {

                if (cursor.getInt(0) > ultimoLog) {
                    ultimoLog = cursor.getInt(0);
                }
            }

            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return ultimoLog;
    }
}
