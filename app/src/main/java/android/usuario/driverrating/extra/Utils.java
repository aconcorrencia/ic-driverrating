package android.usuario.driverrating.extra;

import java.util.Calendar;

/**
 * Created by Sillas on 04/09/2017.
 */

public class Utils {
    public static final int GASOLINA = 1;
    public static final int DIESEL = 2;
    public static final int FLEX = 3;
    public static final int TYPE_FUEL_DEFAUT = GASOLINA;
    public static final int JANELA_DEFAUT = 300;
    public static final int PERCENTUAL_ALCOOL_DEFAUT = 25;

    public static String getTypeFuelString(int type){
        switch (type){
            case GASOLINA:
                return "Gasolina";
            case DIESEL:
                return "Diesel";
            case FLEX:
                return "Flex";
        }
        return "";
    }

    public static String getHour(Calendar c) {
        String hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE));
        return hour;
    }

    public static String getDate(Calendar c) {
        int month = c.get(Calendar.MONTH);
        month++;
        String date = String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", month) + "/" + c.get(Calendar.YEAR);
        return date;
    }
}
