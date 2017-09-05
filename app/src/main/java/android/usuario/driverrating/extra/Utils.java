package android.usuario.driverrating.extra;

/**
 * Created by Sillas on 04/09/2017.
 */

public class Utils {
    public static final int GASOLINA = 1;
    public static final int DIESEL = 2;
    public static final int FLEX = 3;
    public static final int TYPE_FUEL_DEFAUT = GASOLINA;
    public static final int JANELA_DEFAUT = 300;

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
}
