package android.usuario.driverrating.OBD.commands;


import android.usuario.driverrating.OBD.ConfigEML327.AvailablePidsCommand;

/**
 * Created by Sillas on 10/12/2016.
 */

public class AvailablePidsCommand_01_20 extends AvailablePidsCommand {


    public AvailablePidsCommand_01_20() {
        super("01 00");
    }

    public AvailablePidsCommand_01_20(AvailablePidsCommand_01_20 other) {
        super(other);
    }

    @Override
    public String getName() {
        return AvailableCommandNames.PIDS_01_20.getValue();
    }
}