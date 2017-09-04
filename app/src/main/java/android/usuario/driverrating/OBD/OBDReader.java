package android.usuario.driverrating.OBD;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.usuario.driverrating.OBD.ConfigEML327.AutoProtocolCommand;
import android.usuario.driverrating.OBD.ConfigEML327.ObdResetCommand;
import android.usuario.driverrating.OBD.commands.AbsoluteLoadCommand;
import android.usuario.driverrating.OBD.commands.AirIntakeTemperatureCommand;
import android.usuario.driverrating.OBD.commands.AvailablePidsCommand_01_20;
import android.usuario.driverrating.OBD.commands.IntakeManifoldPressureCommand;
import android.usuario.driverrating.OBD.commands.RPMCommand;
import android.usuario.driverrating.OBD.commands.SpeedCommand;
import android.util.Log;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Sillas on 10/12/2016.
 */

public class OBDReader {
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private ConnectThread mConnectThread;
    private Context context;
    private String mac;
    private IOBDBluetooth listener;

    public OBDReader(Context context, IOBDBluetooth listener, String mac) {
        this.context = context;
        this.mac = mac;
        this.listener = listener;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            listener.notSupportedBluetooth();
            return;
        }
    }

    public void start() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Activity activity = (Activity) context;
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mConnectThread == null) {
            connectDevice();
        }
    }

    public void stop() {
        if (mConnectThread != null) {
            stopBluetooth();
        }
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void read(int OBDparameters) {
        Log.w("OBD","READ");
        Bundle obdbundle = new Bundle();
        try {
            if ((OBDInfo.INTAKE_PRESSURE & OBDparameters) != 0) {
                IntakeManifoldPressureCommand intakeManifoldPressureCommand = new IntakeManifoldPressureCommand();
                intakeManifoldPressureCommand.run(mmInStream, mmOutStream);
                obdbundle.putFloat(OBDInfo.KEY_INTAKE_PRESSURE, intakeManifoldPressureCommand.getImperialUnit());
            }
            if ((OBDInfo.INTAKE_TEMP & OBDparameters) != 0) {
                AirIntakeTemperatureCommand airIntakeTemperatureCommand = new AirIntakeTemperatureCommand();
                airIntakeTemperatureCommand.run(mmInStream, mmOutStream);
                obdbundle.putFloat(OBDInfo.KEY_INTAKE_TEMP, airIntakeTemperatureCommand.getKelvin());
            }
            if ((OBDInfo.RPM & OBDparameters) != 0) {
                RPMCommand rpmCommand = new RPMCommand();
                rpmCommand.run(mmInStream, mmOutStream);
                Log.w("TESTE","T: "+rpmCommand.getRPM());
                obdbundle.putInt(OBDInfo.KEY_RPM, rpmCommand.getRPM());
            }
            if ((OBDInfo.SPEED & OBDparameters) != 0) {
                SpeedCommand speedCommand = new SpeedCommand();
                speedCommand.run(mmInStream, mmOutStream);
                obdbundle.putInt(OBDInfo.KEY_SPEED, speedCommand.getMetricSpeed());
            }
            if ((OBDInfo.ABS_LOAD & OBDparameters) != 0) { //Nielson: 12/05/2017
                AbsoluteLoadCommand absoluteLoadCommand = new AbsoluteLoadCommand();
                absoluteLoadCommand.run(mmInStream, mmOutStream);
                obdbundle.putFloat(OBDInfo.KEY_ABS_LOAD, absoluteLoadCommand.getPercentage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listener.obdUpdate(new OBDInfo(obdbundle));
    }

    /**
     * Estabelece conexão com o dispositivo,
     * enviando BluetoothDevice a partir do endereço
     * MAC do dispositivo.
     */
    public void connectDevice() {
        listener.connectingBluetooth();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**
     * GET SOCKET
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;
        private final Activity activity = (Activity) context;
        private boolean connect = true;
        BluetoothSocket sockFallback = null;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
        }

        public void run() {

            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket.connect();
                Log.w("OBD","O1");

            } catch (Exception e1) {
                Log.w("OBD","O2");
                Class<?> clazz = mmSocket.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                try {
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};
                    sockFallback = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                    sockFallback.connect();
                    mmSocket = sockFallback;
                } catch (Exception e2) {
                    connect = false;
                }
            }
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
            } catch (IOException e) {
                listener.errorConnectBluetooth();
            }

            try {
                EchoOffCommand echo =new EchoOffCommand();
                echo.run(mmInStream, mmOutStream);
                String e = echo.getFormattedResult();
                Log.w("OBD","C: "+e);
                new LineFeedOffCommand().run(mmInStream, mmOutStream);
                new TimeoutCommand(125).run(mmInStream, mmOutStream);
                new SelectProtocolCommand(ObdProtocols.AUTO).run(mmInStream, mmOutStream);
                new AutoProtocolCommand().run(mmInStream, mmOutStream);
                new AmbientAirTemperatureCommand().run(mmInStream,mmOutStream);
                RPMCommand rpmCommand = new RPMCommand();
                rpmCommand.run(mmInStream, mmOutStream);
                Log.w("OBD","C: "+rpmCommand.getRPM());
                //new ObdResetCommand().run(mmInStream, mmOutStream);
                //new AutoProtocolCommand().run(mmInStream, mmOutStream);
                //new AvailablePidsCommand_01_20().run(mmInStream, mmOutStream);
            } catch (IOException e) {
                connect = false;
                e.printStackTrace();
            } catch (InterruptedException e) {
                connect = false;
                e.printStackTrace();
            }
            Thread timer = new Thread() {
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (connect)
                                listener.bluetoothConnected();
                            else
                                listener.errorConnectBluetooth();
                        }
                    });
                }
            };
            timer.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Stop thread
     */
    public synchronized void stopBluetooth() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void startBluetooth() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

}
