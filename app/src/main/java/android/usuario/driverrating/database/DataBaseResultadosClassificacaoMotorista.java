package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosResultadosClassificacaoMotorista;

import static android.usuario.driverrating.DriverRatingActivity.notaConsumoCombustivelGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaEmissaoCO2Geral;
import static android.usuario.driverrating.DriverRatingActivity.notaVelocidadeGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoLongitudinalGeral;
import static android.usuario.driverrating.DriverRatingActivity.notaAceleracaoTransversalGeral;


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

    private float notaConsComb;
    private float notaEmissaoCO2;
    private float notaVelocidade;
    private float notaAccLong;
    private float notaAccTrans;

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

    public void selectResultadosClassificacaoByIdLog(long idlog) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABELA + " WHERE " + ID_LOG + " = " + idlog, null);

        notaConsumoCombustivelGeral = 0;
        notaEmissaoCO2Geral = 0;
        notaVelocidadeGeral = 0;
        notaAceleracaoLongitudinalGeral = 0;
        notaAceleracaoTransversalGeral = 0;

        notaConsComb = 0;
        notaEmissaoCO2 = 0;
        notaVelocidade = 0;
        notaAccLong = 0;
        notaAccTrans = 0;

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

           notaConsumoCombustivelGeral = notaConsComb / quant;
           notaEmissaoCO2Geral = notaEmissaoCO2 / quant;
           notaVelocidadeGeral = notaVelocidade / quant;
           notaAceleracaoLongitudinalGeral = notaAccLong / quant;
           notaAceleracaoTransversalGeral = notaAccTrans / quant;
        }
        db.close();
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
                        ultimaJanela = Integer.parseInt(cursor.getString(0));
                }
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return ultimaJanela;
    }
}

