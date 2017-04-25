package android.usuario.driverrating.OBD.commands;

/**
 * Created by Sillas on 10/12/2016.
 */

public abstract class TemperatureCommand extends ObdCommand implements
        SystemOfUnits {

    private float temperature = 0.0f;

    public TemperatureCommand(String cmd) {
        super(cmd);
    }


    public TemperatureCommand(TemperatureCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        temperature = buffer.get(2) - 40;
    }


    @Override
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.1f%s", getImperialUnit(), getResultUnit())
                : String.format("%.0f%s", temperature, getResultUnit());
    }

    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(temperature);
    }

    @Override
    public String getResultUnit() {
        return useImperialUnits ? "F" : "C";
    }

    public float getTemperature() {
        return temperature;
    }

    public float getImperialUnit() {
        return temperature * 1.8f + 32;
    }

    public float getKelvin() {
        return temperature + 273.15f;
    }

    public abstract String getName();

}