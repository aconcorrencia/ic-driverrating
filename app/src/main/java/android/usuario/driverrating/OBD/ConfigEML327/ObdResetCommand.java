package android.usuario.driverrating.OBD.ConfigEML327;


/**
 * Created by Sillas on 10/12/2016.
 */

public class ObdResetCommand extends ObdProtocolCommand {

    public ObdResetCommand() {
        super("AT Z");
    }

    public ObdResetCommand(ObdResetCommand other) {
        super(other);
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Reset OBD";
    }

}