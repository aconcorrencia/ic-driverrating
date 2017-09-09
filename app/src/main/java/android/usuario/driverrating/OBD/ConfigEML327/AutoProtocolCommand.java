package android.usuario.driverrating.OBD.ConfigEML327;


/**
 * Created by Sillas on 10/12/2016.
 */

public class AutoProtocolCommand extends ObdProtocolCommand {

    public AutoProtocolCommand() {
        super("AT SP0");
        //super("AT SP6");
    }

    public AutoProtocolCommand(AutoProtocolCommand other) {
        super(other);
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return "Auto Protocol OBD";
    }

}