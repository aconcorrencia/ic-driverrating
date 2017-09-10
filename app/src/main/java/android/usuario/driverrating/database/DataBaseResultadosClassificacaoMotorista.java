package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;

import java.util.ArrayList;

/**
 * Created by NIELSON on 22/06/2017.
 */


public class DataBaseResultadosClassificacaoMotorista extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "resultclassmot";
    private static final int VERSAO_AUX = 2;
    private static final String TABELA = "tbresultclassmot";

    private static final String ID = "_idjanclasmot";
    private static final String ID_LOG = "idfklog";
    private static final String NOTACONSCOMB = "notaconscomb";
    private static final String CLASCONSCOMB = "clasconscomb";
    private static final String NOTAEMISCO2 = "notaemisco2";
    private static final String CLASEMISCO2 = "clasemisco2";
    private static final String NOTAVELOCID = "notavelocid";
    private static final String CLASVELOCID = "clasvelocid";
    private static final String NOTAACELLONG = "notaacellong";
    private static final String CLASACELLONG = "clasacellong";
    private static final String NOTAACELTRANS = "notaaceltrans";
    private static final String CLASACELTRANS = "clasaceltrans";

    private Context context;



    public DataBaseResultadosClassificacaoMotorista(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
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

    public long inserirDadosResultClassMot(DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
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

        return db.insert(TABELA, null, valores);
    }

    public ArrayList<DadosResultadosClassificacaoMotorista> selectResultadosClassificacaoByIdLog(long idlog) {
        ArrayList<DadosResultadosClassificacaoMotorista> mDadosResultadosClassificacaoMotorista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABELA + " WHERE " + ID_LOG + " = " + idlog, null);
        int count=cursor.getCount();

        if (count > 0){
            while (cursor.moveToNext()) {
                DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();
                dadosResultadosClassificacaoMotorista.setId_janclasmot(cursor.getInt(0));
                dadosResultadosClassificacaoMotorista.setId_log(cursor.getLong(1));
                dadosResultadosClassificacaoMotorista.setNota_cons_comb(cursor.getDouble(2));
                dadosResultadosClassificacaoMotorista.setClas_cons_comb(cursor.getString(3));

                dadosResultadosClassificacaoMotorista.setNota_emis_co2(cursor.getDouble(4));
                dadosResultadosClassificacaoMotorista.setClas_emis_co2(cursor.getString(5));

                dadosResultadosClassificacaoMotorista.setNota_velocid(cursor.getDouble(6));
                dadosResultadosClassificacaoMotorista.setClas_velocid(cursor.getString(7));

                dadosResultadosClassificacaoMotorista.setNota_acel_long(cursor.getDouble(8));
                dadosResultadosClassificacaoMotorista.setClas_acel_long(cursor.getString(9));

                dadosResultadosClassificacaoMotorista.setNota_acel_trans(cursor.getDouble(10));
                dadosResultadosClassificacaoMotorista.setClas_acel_trans(cursor.getString(11));
                mDadosResultadosClassificacaoMotorista.add(dadosResultadosClassificacaoMotorista);

            }
        }
        db.close();
        return mDadosResultadosClassificacaoMotorista;
    }

    public ArrayList<DadosResultadosClassificacaoMotorista> selectAllResultadosClassificacaoByIdLog(long idlog) {
        ArrayList<DadosResultadosClassificacaoMotorista> mDadosResultadosClassificacaoMotorista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABELA, null);
        int count=cursor.getCount();

        if (count > 0){
            while (cursor.moveToNext()) {
                DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();
                dadosResultadosClassificacaoMotorista.setId_janclasmot(cursor.getInt(0));
                dadosResultadosClassificacaoMotorista.setId_log(cursor.getLong(1));
                dadosResultadosClassificacaoMotorista.setNota_cons_comb(cursor.getDouble(2));
                dadosResultadosClassificacaoMotorista.setClas_cons_comb(cursor.getString(3));

                dadosResultadosClassificacaoMotorista.setNota_emis_co2(cursor.getDouble(4));
                dadosResultadosClassificacaoMotorista.setClas_emis_co2(cursor.getString(5));

                dadosResultadosClassificacaoMotorista.setNota_velocid(cursor.getDouble(6));
                dadosResultadosClassificacaoMotorista.setClas_velocid(cursor.getString(7));

                dadosResultadosClassificacaoMotorista.setNota_acel_long(cursor.getDouble(8));
                dadosResultadosClassificacaoMotorista.setClas_acel_long(cursor.getString(9));

                dadosResultadosClassificacaoMotorista.setNota_acel_trans(cursor.getDouble(10));
                dadosResultadosClassificacaoMotorista.setClas_acel_trans(cursor.getString(11));
                mDadosResultadosClassificacaoMotorista.add(dadosResultadosClassificacaoMotorista);

            }
        }
        db.close();
        return mDadosResultadosClassificacaoMotorista;
    }

    public DadosResultadosClassificacaoMotorista selectMediaResultadosClassificacaoByIdLog(long idlog) {
        DadosResultadosClassificacaoMotorista dadosResultadosClassificacaoMotorista = new DadosResultadosClassificacaoMotorista();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABELA + " WHERE " + ID_LOG + " = " + idlog, null);
        float notaConsComb =0;
        float notaEmissaoCO2 =0;
        float notaVelocidade =0;
        float notaAccLong =0;
        float notaAccTrans =0;


        int count=cursor.getCount();

        if (count > 0){

            int quant = 0;

            while (cursor.moveToNext()) {
                quant+=1;
                notaConsComb += cursor.getDouble(2);
                notaEmissaoCO2 += cursor.getDouble(4);
                notaVelocidade += cursor.getDouble(6);
                notaAccLong += cursor.getDouble(8);
                notaAccTrans += cursor.getDouble(10);
            }
            dadosResultadosClassificacaoMotorista.setNota_cons_comb(notaConsComb / quant);
            dadosResultadosClassificacaoMotorista.setNota_emis_co2(notaEmissaoCO2 / quant);
            dadosResultadosClassificacaoMotorista.setNota_velocid(notaVelocidade / quant);
            dadosResultadosClassificacaoMotorista.setNota_acel_long(notaAccLong / quant);
            dadosResultadosClassificacaoMotorista.setNota_acel_trans(notaAccTrans / quant);

        }
        db.close();
        return dadosResultadosClassificacaoMotorista;
    }

    //Recupera a última janela.
    //public int selectUltimaJanela(long idlog) {
    public int selectUltimaJanela() {
        int ultimaJanela = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT MAX("+ID+") FROM " + TABELA, null);

            if(cursor != null){
                if (cursor.moveToNext()) {
                        //ultimaJanela = Integer.parseInt(cursor.getString(0));
                    ultimaJanela = cursor.getInt(0);
                }
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return ultimaJanela;
    }
}

