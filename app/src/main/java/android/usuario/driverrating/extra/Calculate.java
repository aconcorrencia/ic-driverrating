package android.usuario.driverrating.extra;

import android.usuario.driverrating.GPS.GPSInfo;
import android.usuario.driverrating.GPS.OverpassInfo;
import android.usuario.driverrating.GPS.OverpassReader;
import android.usuario.driverrating.IniciarClassificacao;
import android.usuario.driverrating.OBD.OBDInfo;

import static android.usuario.driverrating.DriverRatingActivity.densityFuel;
import static java.lang.String.valueOf;

/**
 * Created by Sillas on 24/04/2017.
 */

public class Calculate {

    // Calcular o fluxo de combustível
    public static float getFuelflow(OBDInfo obdinfo, float cilindrada, int delta,int densityFuel){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        float imap = rpm * map / iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada * (28.97f / 8.314f);

        /*float densityFuel = 0; //Densidade do combstível
        if (tipoCombustivel.equals("1")){
            densityFuel = 750; //Gasolina
        }
        else if (tipoCombustivel.equals("3")){
            densityFuel = 820; //Diesel
        }*/

        float litrosConsumidos = (maf * (delta/1000)) / (14.7f * densityFuel) * 10; // multiplica por "10", pois, dm^3 = 10^3 = (10 x 10 x 10) = 1.000

        //valormaf2 = maf;

        return litrosConsumidos;
    }

    // Calcular a emissão de CO2
    /*public static float getEmissionCO2(OBDInfo obdinfo, float cilindrada, int delta){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        float imap = rpm * map / iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada * (28.97f / 8.314f);

        float densityFuel = 0; //Densidade do combstível
        if (tipoCombustivel.equals("1")){
            densityFuel = 750; //Gasolina
        }
        else if (tipoCombustivel.equals("3")){
            densityFuel = 820; //Diesel
        }

        float quilogramasPorSegundosCO2 = (((maf / 14.7f) / densityFuel) * 2.35f) * (delta / 1000);

        return quilogramasPorSegundosCO2;
    }*/




















    /*public static float getFuelConsump(OBDInfo obdinfo, float cilindrada){
        int rpm = obdinfo.getRpm();
        float iat = obdinfo.getIntakeTemp();
        float map = obdinfo.getIntakePressure();
        int speed = obdinfo.getSpeed();
        float imap = rpm * map / iat / 2;
        float maf =  (imap / 60) * 0.85f * cilindrada  * (28.97f / 8.314f);
        float fuel_flow = ((maf * 3600) / (14.7f * 750)); // usar variável para: diesel / gasolina
        float fuel_consump = (fuel_flow / speed) * 100;   // litro/100km

        return  fuel_consump;
    }*/


    //Utilizando o Abs_Load no cálculo do maf.
    /*public static float getLitrosCombustivel(OBDInfo obdinfo, float cilindrada, int delta){
        int rpm = obdinfo.getRpm();
        float abs_load = obdinfo.getAbsoluteLoad();

        float maf = 1.184f * cilindrada * (abs_load/100) * (rpm/2) * (rpm/60);

        float densityFuel = 0; //Densidade do combstível
        if (tipoCombustivel.equals("1")){
            densityFuel = 750; //Gasolina
        }
        else if (tipoCombustivel.equals("3")){
            densityFuel = 820; //Diesel
        }

        float litrosConsumidos = ((maf / (14.7f * densityFuel))) * (delta/1000);

        valormaf1 = maf;
        valormaf3 = abs_load;

        return litrosConsumidos;
    }*/

}
