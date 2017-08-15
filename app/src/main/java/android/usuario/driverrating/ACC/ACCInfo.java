package android.usuario.driverrating.ACC;

/**
 * Created by Matheus on 28/06/2017.
 */

public class ACCInfo {
    private float accTranversal, accLongitudinal, accPerpendicular;

    public ACCInfo(float accTranversal, float accLongitudinal, float accPerpendicular) {
        this.accTranversal = accTranversal;
        this.accLongitudinal = accLongitudinal;
        this.accPerpendicular = accPerpendicular;
    }

    public float getAccTranversal() {
        return accTranversal;
    }

    public float getAccLongitudinal() {
        return accLongitudinal;
    }

    public float getAccPerpendicular() {
        return accPerpendicular;
    }
}
