package android.usuario.driverrating.OBD.ConfigEML327;

import android.usuario.driverrating.OBD.commands.ObdCommand;

/**
 * Created by Sillas on 10/12/2016.
 */

public class ObdProtocolCommand extends ObdCommand {

    public ObdProtocolCommand(String command) {
        super(command);
    }

    public ObdProtocolCommand(ObdProtocolCommand other) {
        this(other.cmd);
    }

    protected void performCalculations() {
        // ignore
    }

    protected void fillBuffer() {
        // settings commands don't return a value appropriate to place into the
        // buffer, so do nothing
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(getResult());
    }

    @Override
    public String getFormattedResult() {
        return "";
    }
    @Override
    public String getName() {
        return "";
    }
}