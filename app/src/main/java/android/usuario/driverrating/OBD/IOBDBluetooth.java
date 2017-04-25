package android.usuario.driverrating.OBD;

/**
 * Created by Sillas on 25/11/2016.
 */

public interface IOBDBluetooth {
    public void obdUpdate(OBDInfo obdinfo);
    void errorConnectBluetooth();//erro na conexão
    void notSupportedBluetooth();//Bluetooth não é suportado pelo dispositivo
    void connectingBluetooth();//Enquanto o bluetooth se conecta ao dispositivo
    void bluetoothConnected();//Bluetooth conectado
}
