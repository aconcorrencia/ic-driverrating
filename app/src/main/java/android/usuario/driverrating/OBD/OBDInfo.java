package android.usuario.driverrating.OBD;

import android.os.Bundle;

/**
 * Created by Sillas on 24/04/2017.
 */


public class OBDInfo {
    public static final String KEY_INTAKE_PRESSURE="INTAKE_PRESSURE";
    public static final String KEY_INTAKE_TEMP="INTAKE_TEMP";
    public static final String KEY_RPM="RPM";
    public static final String KEY_SPEED="SPEED";
    public static final String KEY_ABS_LOAD="ABD_LOAD"; //Nielson: 12/05/2017
    public static final int INTAKE_PRESSURE=2;
    public static final int INTAKE_TEMP=4;
    public static final int RPM=8;
    public static final int SPEED=16;
    public static final int ABS_LOAD=32; //Nielson: 12/05/2017

    private Bundle obdinfo;

    public OBDInfo() {
        this.obdinfo=new Bundle();
    }

    public OBDInfo(Bundle obdinfo) {
        this.obdinfo=obdinfo;
    }

    public float getIntakePressure() {
        return obdinfo.getFloat(KEY_INTAKE_PRESSURE,-1);
    }

    public float getIntakeTemp() {
        return obdinfo.getFloat(KEY_INTAKE_TEMP,-1);
    }

    public int getRpm() {
        return obdinfo.getInt(KEY_RPM,-1);
    }

    public int getSpeed() {
        return obdinfo.getInt(KEY_SPEED,-1);
    }

    public float getAbsoluteLoad() { return obdinfo.getFloat(KEY_ABS_LOAD,-1); } //Nielson: 12/05/2017


}
