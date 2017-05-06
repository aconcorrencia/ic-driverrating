package android.usuario.driverrating.extra;

import android.usuario.driverrating.GPS.GPSInfo;
import android.usuario.driverrating.GPS.OverpassInfo;
import android.usuario.driverrating.GPS.OverpassReader;
import android.usuario.driverrating.OBD.OBDInfo;

import static java.lang.String.valueOf;

/**
 * Created by Sillas on 24/04/2017.
 */

public class Calculate {

    public static float getFuelConsump(OBDInfo obdinfo, float cilindrada){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        int speed = obdinfo.getSpeed();
        float imap = rpm * map / iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada  * (28.97f / 8.314f);
        float fuel_flow = ((maf * 3600) / (14.7f * 750)); // usar variável para: diesel / gasolina
        float fuel_consump = (fuel_flow / speed) * 100;   // litro/100km

        return  fuel_consump;
    }

    //Prof. Jorge sugeriu:
    public static float getFuelflow(OBDInfo obdinfo, float cilindrada){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        float imap = rpm * map / iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada  * (28.97f / 8.314f);
        float litrosHora = ((maf * 3600) / (14.7f * 750)) * 100; // usar variável para: diesel / gasolina

        return  litrosHora;
    }

    public static float getVelocidade(OverpassInfo overpassInfo){

        String maximaveloc = overpassInfo.getMaxspeed();

        if (maximaveloc != "unknown"){

            return  Integer.valueOf(maximaveloc);
        }
        else {
            return 0;
        }
    }

}
