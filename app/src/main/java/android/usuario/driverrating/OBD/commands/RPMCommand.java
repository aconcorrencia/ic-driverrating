
package android.usuario.driverrating.OBD.commands;

public class RPMCommand extends ObdCommand {


    private int rpm = -1;

    public RPMCommand() {
        super("01 0C");
    }

    public RPMCommand(RPMCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
    }

    @Override
    public String getFormattedResult() {
        return String.format("%d%s", rpm, getResultUnit());
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(rpm);
    }

    @Override
    public String getResultUnit() {
        return "RPM";
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ENGINE_RPM.getValue();
    }

    public int getRPM() {
        return rpm;
    }

}