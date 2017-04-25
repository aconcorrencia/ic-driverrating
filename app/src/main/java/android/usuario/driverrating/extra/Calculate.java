package android.usuario.driverrating.extra;

import android.usuario.driverrating.OBD.OBDInfo;

/**
 * Created by Sillas on 24/04/2017.
 */

public class Calculate {

    public static float getFuelConsump(OBDInfo obdinfo, float cilindrada){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        int speed = obdinfo.getSpeed();
        //imap = (((rpm * map) / iat) / 2);
        float imap = rpm * map/ iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada  * (28.97f / 8.314f);
        float fuel_flow = ((maf * 3600) / (14.7f * 750))/* / 100*/; // usar variável para: diesel / gasolina
        //Prof. Jorge sugeriu: fuel_flow = (maf / (14.7f * 750))
        float fuel_consump = (fuel_flow / speed) * 100; //litro / 100 km
        return  fuel_consump;
    }

}
