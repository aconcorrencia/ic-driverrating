package android.usuario.driverrating.OBD.commands;

/**
 * Created by Sillas on 10/12/2016.
 */

public abstract class PressureCommand extends ObdCommand implements
        SystemOfUnits {

    protected int tempValue = 0;
    protected int pressure = 0;

    public PressureCommand(String cmd) {
        super(cmd);
    }


    public PressureCommand(PressureCommand other) {
        super(other);
    }

    protected int preparePressureValue() {
        return buffer.get(2);
    }

    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        pressure = preparePressureValue();
    }

    @Override
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.1f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", pressure, getResultUnit());
    }

    public int getMetricUnit() {
        return pressure;
    }

    public float getImperialUnit() {
        return pressure * 0.145037738f;
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(pressure);
    }

    @Override
    public String getResultUnit() {
        return useImperialUnits ? "psi" : "kPa";
    }

    @Override
    public String getName() {
        return null;
    }

}
