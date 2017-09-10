package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosLogClassificacao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NIELSON on 22/06/2017.
 */


public class DataBaseLogClassificacao  extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "logclass";
    private static final int VERSAO_AUX = 2;
    public static final String TABELA = "tblogclass";

    private static final String ID = "_id";
    public static final  String ID_PERFIL = "id_perfil";
    public static final String DATA = "data";
    public static final String HORA = "hora";


    public DataBaseLogClassificacao(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + ID_PERFIL + " int, "
                + DATA + " text,"
                + HORA + " text"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public long inserirDadosLogClassificacao(DadosLogClassificacao dadosLogClassificacao) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(ID_PERFIL, dadosLogClassificacao.getIdPerfil());
        valores.put(DATA, dadosLogClassificacao.getData());
        valores.put(HORA, dadosLogClassificacao.getHora());
        return db.insert(TABELA, null, valores);
    }

    //Recupera o último ID do Log.
    public int selectUltimoLog(long id) {
        int ultimoLog = 0;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+ID_PERFIL+"=" + id, null);

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

    public ArrayList<DadosLogClassificacao>  selectLogByUserId(long idPerfil) {
        ArrayList<DadosLogClassificacao> mDadosLogClassificacao = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+ID_PERFIL+"=" + idPerfil +" ORDER BY " +ID + " DESC", null);

            while (cursor.moveToNext()) {
                DadosLogClassificacao dadosLogClassificacao = new DadosLogClassificacao();
                dadosLogClassificacao.setId(cursor.getInt(0));
                dadosLogClassificacao.setIdPerfil(cursor.getInt(1));
                dadosLogClassificacao.setData(cursor.getString(2));
                dadosLogClassificacao.setHora(cursor.getString(3));
                mDadosLogClassificacao.add(dadosLogClassificacao);
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return mDadosLogClassificacao;
    }



}
