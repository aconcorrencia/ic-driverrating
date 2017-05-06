package android.usuario.driverrating.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.usuario.driverrating.domain.Veiculo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by NIELSON on 26/11/2016.
 */

public class DataBasePerfis extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "perfis";
    private static final int VERSAO_AUX = 9;

    public static final String TABELA = "refveiculos";

    private static final String ID = "_id";
    public static final String NOME = "nome";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String MOTOR = "motor";
    public static final String VERSAO = "versao";
    //public static final String TRANSMISSAO = "transmissao";
    public static final String EMISSNOX = "emissnox";
    public static final String EMISSCO2 = "emissco2";
    public static final String CONSETANCID = "consetancid";
    public static final String CONSETANESTR = "consetanestr";
    public static final String CONSGASCID = "consgascid";
    public static final String CONSGASESTR = "consgasestr";
    public static final String FOTO = "foto";

    private Context context;

    public DataBasePerfis(Context context) {
        super(context, NOME_BANCO, null, VERSAO_AUX);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + ID + " integer primary key autoincrement, "
                + NOME + " text, "
                + MARCA + " text, "
                + MODELO + " text, "
                + MOTOR + " text, "
                + VERSAO + " text, "
                /*+ TRANSMISSAO + " text, "*/
                + EMISSNOX + " double, "
                + EMISSCO2 + " double, "
                + CONSETANCID + " double, "
                + CONSETANESTR + " double, "
                + CONSGASCID + " double, "
                + CONSGASESTR + " double, "
                + FOTO + " blob"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public long inserirVeiculo(Veiculo veiculo) {
        long id=-1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(NOME, veiculo.getNomeUsuario());
        valores.put(MARCA, veiculo.getMarca());
        valores.put(MODELO, veiculo.getModelo());
        valores.put(MOTOR, veiculo.getMotor());
        valores.put(VERSAO, veiculo.getVersao());
        valores.put(EMISSNOX, veiculo.getNox());
        valores.put(EMISSCO2, veiculo.getCo2());
        valores.put(CONSETANCID, veiculo.getEtanolCidade());
        valores.put(CONSETANESTR, veiculo.getEtanolEstrada());
        valores.put(CONSGASCID, veiculo.getGasolinaCidade());
        valores.put(CONSGASESTR, veiculo.getGasolinaEstrada());
        if(veiculo.getFoto()!=null) {
            valores.put(FOTO, getBytes(veiculo.getFoto()));
        }
        id=db.insert(TABELA, null, valores);
        return id;
    }
    public static Bitmap getImage(byte[] image) {
        try{
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }catch (Exception e){
            return null;
        }

    }
    public  byte[] getBytes(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleX = (float) 300 / bitmap.getWidth();
        float scaleY = (float) 300 / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap auxBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
        bitmap.recycle();
        bitmap = auxBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public ArrayList<Veiculo> selectAll() {
        ArrayList<Veiculo> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA, null);
            while (cursor.moveToNext()) {
                Veiculo v = new Veiculo();
                v.setId(cursor.getInt(0));
                v.setNomeUsuario(cursor.getString(1));
                v.setMarca(cursor.getString(2));
                v.setModelo(cursor.getString(3));
                v.setMotor(cursor.getString(4));
                v.setVersao(cursor.getString(5));
                v.setNox(cursor.getDouble(6));
                v.setCo2(cursor.getDouble(7));
                v.setEtanolCidade(cursor.getDouble(8));
                v.setEtanolEstrada(cursor.getDouble(9));
                v.setGasolinaCidade(cursor.getDouble(10));
                v.setGasolinaEstrada(cursor.getDouble(11));
                if(cursor.getBlob(12)!=null) {
                    v.setFoto(getImage(cursor.getBlob(12)));
                }
                arrayList.add(v);
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    public Veiculo selectPerfilById(long id) {
        Veiculo v = new Veiculo();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT *FROM " + TABELA + " WHERE "+ID+"=" + id, null);
            int count=cursor.getCount();
            if(count>0) {
                cursor.moveToNext();
                v.setId(cursor.getInt(0));
                v.setNomeUsuario(cursor.getString(1));
                v.setMarca(cursor.getString(2));
                v.setModelo(cursor.getString(3));
                v.setMotor(cursor.getString(4));
                v.setVersao(cursor.getString(5));
                v.setNox(cursor.getDouble(6));
                v.setCo2(cursor.getDouble(7));
                v.setEtanolCidade(cursor.getDouble(8));
                v.setEtanolEstrada(cursor.getDouble(9));
                v.setGasolinaCidade(cursor.getDouble(10));
                v.setGasolinaEstrada(cursor.getDouble(11));
                if(cursor.getBlob(12)!=null) {
                    v.setFoto(getImage(cursor.getBlob(12)));
                }
            }else{
                db.close();
                return null;
            }
            db.close();
        } catch (SQLiteException e) {
            return null;
        }
        return v;
    }

    public void deletaPerfil(int id_perfil) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABELA, ID+"='" + id_perfil + "'", null);
        db.close();
    }

}
