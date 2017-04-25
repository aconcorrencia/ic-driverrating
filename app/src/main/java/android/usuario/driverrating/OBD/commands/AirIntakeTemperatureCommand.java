package android.usuario.driverrating.OBD.commands;


/**
 * Created by Sillas on 10/12/2016.
 */

public class AirIntakeTemperatureCommand extends TemperatureCommand {

    public AirIntakeTemperatureCommand() {
        super("01 0F");
    }

    public AirIntakeTemperatureCommand(AirIntakeTemperatureCommand other) {
        super(other);
    }

    @Override
    public String getName() {
        return AvailableCommandNames.AIR_INTAKE_TEMP.getValue();
    }

}