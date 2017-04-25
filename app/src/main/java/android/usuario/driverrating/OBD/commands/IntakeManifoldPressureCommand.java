package android.usuario.driverrating.OBD.commands;

/**
 * Created by Sillas on 10/12/2016.
 */

public class IntakeManifoldPressureCommand extends PressureCommand {


    public IntakeManifoldPressureCommand() {
        super("01 0B");
    }

    public IntakeManifoldPressureCommand(IntakeManifoldPressureCommand other) {
        super(other);
    }

    @Override
    public String getName() {
        return AvailableCommandNames.INTAKE_MANIFOLD_PRESSURE.getValue();
    }

}