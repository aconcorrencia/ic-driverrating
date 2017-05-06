package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.usuario.driverrating.IniciarClassificacao;
import android.usuario.driverrating.domain.DadosConsComb;

import java.sql.Time;
import java.util.Date;

/**
 * Created by NIELSON on 28/04/2017.
 */

public class DataBaseConsComb extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "dadoscoletados";
    private static final int VERSAO_AUX = 1;
    public static final String TABELA = "consumocombustivel";

    private static final String ID = "_id";
    public static final String FUELCONSUMP = "fuelconsump";
    public static final String FUELFLOW = "fuelflow";
    public static final String DATA = "data";
    public static final String HORA = "hora";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latidude";

    private Context context;

    public DataBaseConsComb(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + FUELCONSUMP + " double, "
                + FUELFLOW + " double, "
                + DATA + " date, "
                + HORA + " time, "
                + LONGITUDE + " double, "
                + LATITUDE + " double"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public void inserirDadosConsComb(DadosConsComb dadosConsComb) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(FUELCONSUMP, dadosConsComb.getFuelconsump());
        valores.put(FUELFLOW, dadosConsComb.getFuelflow());
        valores.put(DATA, String.valueOf(dadosConsComb.getData()));
        valores.put(HORA, String.valueOf(dadosConsComb.getHora()));
        valores.put(LONGITUDE, dadosConsComb.getLongitude());
        valores.put(LATITUDE, dadosConsComb.getLatidude());

        db.insert(TABELA, null, valores);

    }


}
