package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;

import java.util.Date;

/**
 * Created by NIELSON on 22/06/2017.
 */


public class DataBaseResultadosClassificacaoMotorista extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "resultclassmot";
    private static final int VERSAO_AUX = 1;
    public static final String TABELA = "tbresultclassmot";

    private static final String ID = "_idjanclasmot";
    public static final String ID_LOG = "idfklog";
    public static final String NOTACONSCOMB = "notaconscomb";
    public static final String CLASCONSCOMB = "clasconscomb";
    public static final String NOTAEMISCO2 = "notaemisco2";
    public static final String CLASEMISCO2 = "clasemisco2";
    public static final String NOTAVELOCID = "notavelocid";
    public static final String CLASVELOCID = "clasvelocid";
    public static final String NOTAACELLONG = "notaacellong";
    public static final String CLASACELLONG = "clasacellong";
    public static final String NOTAACELTRANS = "notaaceltrans";
    public static final String CLASACELTRANS = "clasaceltrans";

    private Context context;

    public DataBaseResultadosClassificacaoMotorista(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " int, "
                + ID_LOG + " int, "
                + NOTACONSCOMB + " double, "
                + CLASCONSCOMB + " text, "
                + NOTAEMISCO2 + " double, "
                + CLASEMISCO2 + " text, "
                + NOTAVELOCID + " double, "
                + CLASVELOCID + " text, "
                + NOTAACELLONG + " double, "
                + CLASACELLONG + " text, "
                + NOTAACELTRANS + " double, "
                + CLASACELTRANS + " text "
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public void inserirDadosResultClassMot(DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(ID, dadosResultadosClassificacaoMotorista.getId_janclasmot());
        valores.put(ID_LOG, dadosResultadosClassificacaoMotorista.getId_log());
        valores.put(NOTACONSCOMB, dadosResultadosClassificacaoMotorista.getNota_cons_comb());
        valores.put(CLASCONSCOMB, dadosResultadosClassificacaoMotorista.getClas_cons_comb());
        valores.put(NOTAEMISCO2, dadosResultadosClassificacaoMotorista.getNota_emis_co2());
        valores.put(CLASEMISCO2, dadosResultadosClassificacaoMotorista.getClas_emis_co2());
        valores.put(NOTAVELOCID, dadosResultadosClassificacaoMotorista.getNota_velocid());
        valores.put(CLASVELOCID, dadosResultadosClassificacaoMotorista.getClas_velocid());
        valores.put(NOTAACELLONG, dadosResultadosClassificacaoMotorista.getNota_acel_long());
        valores.put(CLASACELLONG, dadosResultadosClassificacaoMotorista.getClas_acel_long());
        valores.put(NOTAACELTRANS, dadosResultadosClassificacaoMotorista.getNota_acel_trans());
        valores.put(CLASACELTRANS, dadosResultadosClassificacaoMotorista.getClas_acel_trans());

        db.insert(TABELA, null, valores);
    }

    public DadosResultadosClassificacaoMotorista selectResultadosClassificacaoByIdPerc(long idlog) {
        DadosResultadosClassificacaoMotorista d = new DadosResultadosClassificacaoMotorista();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE " + ID_LOG + " = " + idlog, null);
            int count=cursor.getCount();
            if(count>0) {
                cursor.moveToNext();
                d.setId_janclasmot(cursor.getInt(0));
                d.setId_log(cursor.getInt(1));
                d.setNota_cons_comb(cursor.getDouble(4));
                d.setClas_cons_comb(cursor.getString(5));
                d.setNota_emis_co2(cursor.getDouble(6));
                d.setClas_emis_co2(cursor.getString(7));
                d.setNota_velocid(cursor.getDouble(8));
                d.setClas_velocid(cursor.getString(9));
                d.setNota_acel_long(cursor.getDouble(10));
                d.setClas_acel_long(cursor.getString(11));
                d.setNota_acel_trans(cursor.getDouble(12));
                d.setClas_acel_trans(cursor.getString(13));
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

    //Recupera a última janela.
    public int selectUltimoPercurso(long idlog) {
        int ultimaJanela = 0;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+ID_LOG+"=" + idlog, null);

            while (cursor.moveToNext()) {

                if (cursor.getInt(0) > ultimaJanela) {
                    ultimaJanela = Integer.parseInt(cursor.getString(2));
                }
            }

            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return ultimaJanela;
    }
}

