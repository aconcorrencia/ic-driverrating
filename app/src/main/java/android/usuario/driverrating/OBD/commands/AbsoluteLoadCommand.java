
package android.usuario.driverrating.OBD.commands;
import android.util.Log;


public class AbsoluteLoadCommand extends PercentageObdCommand {

    public AbsoluteLoadCommand() {
        super("01 43");
    }


    public AbsoluteLoadCommand(AbsoluteLoadCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        percentage = (a * 256 + b) * 100 / 255;
        Log.w("SW","SC: "+percentage);
    }


    public double getRatio() {
        return percentage;
    }


    @Override
    public String getName() {
        return AvailableCommandNames.ABS_LOAD.getValue();
    }

}