package android.usuario.driverrating.OBD.commands;

/**
 * Created by Sillas on 10/12/2016.
 */

public class PercentageObdCommand extends ObdCommand {

    protected float percentage = 0f;


    public PercentageObdCommand(String command) {
        super(command);
    }


    public PercentageObdCommand(PercentageObdCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = (buffer.get(2) * 100.0f) / 255.0f;
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.1f%s", percentage, getResultUnit());
    }


    public float getPercentage() {
        return percentage;
    }

    @Override
    public String getResultUnit() {
        return "%";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(percentage);
    }

}