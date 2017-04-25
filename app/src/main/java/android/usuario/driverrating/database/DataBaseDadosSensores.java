package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.domain.DadosSensores;
import android.usuario.driverrating.domain.DadosSensoresBuilder;

import java.util.ArrayList;

/**
 * Created by NIELSON on 06/12/2016.
 */

public class DataBaseDadosSensores extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "dadossensores";
    private static final int VERSAO_AUX = 1;

    public static final String TABELA = "recuperadados";
    private static final String ID = "_id";
    private static final String ID_USER="id_user";
    public static final String FLUXOCOMB = "fluxocomb";
    public static final String VELOCID = "velocid";
    public static final String CONSCOMB = "consumoComb";

    public static final String DATAOCORRENCIA = "dataOcorrencia";
    public static final String HORAOCORRENCIA = "horaOcorrencia";

    //public static final String LATIDUDE = "latidude";
    //public static final String LONGITUDE = "longitude";*/
    private Context context;

    Double somaConsCombust = 0.00;
    Double resulConsCombust = 0.00;
    int totalReg = 0;

    public int id_Perfil = 0;

    public DataBaseDadosSensores(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + ID_USER + " int, "
                + FLUXOCOMB + " double, "
                + VELOCID + " double, "
                + CONSCOMB + " double"
                /*+ DATAOCORRENCIA + " data,"
                + HORAOCORRENCIA + " time,"*/
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABELA);
            onCreate(db);
    }

    public void inserirDadosSensores(DadosSensores dadosSensores) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(ID_USER, dadosSensores.getId_user());
        valores.put(FLUXOCOMB, dadosSensores.getFluxoComb());
        valores.put(VELOCID, dadosSensores.getVelocid());
        valores.put(CONSCOMB, dadosSensores.getConsumoComb());
        /*valores.put(DATAOCORRENCIA, String.valueOf(dadosSensores.getDataOcorrencia()));
        valores.put(HORAOCORRENCIA, String.valueOf(dadosSensores.getHoraOcorrencia()));*/

        db.insert(TABELA, null, valores);

        //Log.w("Id.", String.valueOf(valores));

    }

    public ArrayList<DadosSensores> selectAll() {
        ArrayList<DadosSensores> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA, null);
            //Log.w("Qtd.", String.valueOf(cursor.getCount()));

            while (cursor.moveToNext()) {
                DadosSensores ds = new DadosSensoresBuilder().createDadosSensores();

                ds.setId_user(cursor.getInt(1));
                ds.setFluxoComb(cursor.getDouble(2));
                ds.setVelocid(cursor.getDouble(3));
                ds.setConsumoComb(cursor.getDouble(4));
                //ds.setDataOcorrencia(new Date(cursor.getLong(3)));
                //ds.setHoraOcorrencia(new Date(cursor.getLong(4)));

                arrayList.add(ds);
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    public Double CalcResults() {

        try {
            SQLiteDatabase db = getReadableDatabase();
            //int numID = 5;
            //Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE "+ID_USER+"="+numID, null);
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA, null);
            while (cursor.moveToNext()) {
                //DadosSensores ds = new DadosSensoresBuilder().createDadosSensores();

                if (id_Perfil == 0){
                    id_Perfil = cursor.getInt(1);
                }

                if (cursor.getDouble(3) != 0.0) {
                    somaConsCombust = somaConsCombust + 100 / cursor.getDouble(4);
                    totalReg = totalReg + 1;
                }
                /*Log.w("Nome Campo 1: ", String.valueOf(cursor.getColumnName(0))+" "+String.valueOf(cursor.getDouble(0)));
                Log.w("Nome Campo 2: ", String.valueOf(cursor.getColumnName(1))+" "+String.valueOf(cursor.getDouble(1)));
                Log.w("Nome Campo 3: ", String.valueOf(cursor.getColumnName(2))+" "+String.valueOf(cursor.getDouble(2)));
                Log.w("Nome Campo 4: ", String.valueOf(cursor.getColumnName(3))+" "+String.valueOf(cursor.getDouble(3)));
                Log.w("Nome Campo 5: ", String.valueOf(cursor.getColumnName(4))+" "+String.valueOf(cursor.getDouble(4)));*/

            }

            resulConsCombust = somaConsCombust / totalReg;

            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return resulConsCombust;
    }

    public void excluirDados(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABELA,"",null); //execSQL("DELETE FROM " + TABELA, null);
    }

}
