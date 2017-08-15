package android.usuario.driverrating.ACC;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Matheus on 28/06/2017.
 */

public class ACCReader implements SensorEventListener {
    public static final int FASTEST = SensorManager.SENSOR_DELAY_FASTEST;
    public static final int GAME = SensorManager.SENSOR_DELAY_GAME;
    public static final int NORMAL = SensorManager.SENSOR_DELAY_NORMAL;
    public static final int UI = SensorManager.SENSOR_DELAY_UI;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private static final float alpha = 0.2f;
    private int movingAverageCount = 0, subset = 6, verifyFirstValues = 0, verifyFirstComparison = 0, sentTwoVectors = 0;
    private float[] accelerometerValues, movingAverageValues, acceleration;


    public ACCReader(Context context) {
        senSensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        movingAverageValues = new float[3];
        acceleration = new float[3];

    }

    public void start(int delay) {
        senSensorManager.registerListener(this, senAccelerometer, delay);
    }

    public void stop() {
        senSensorManager.unregisterListener(this);
    }

    public ACCInfo read() {
        ACCInfo accinfo = new ACCInfo(acceleration[0], acceleration[1], acceleration[2]);
        acceleration = new float[3];
        return accinfo;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            applyFilters(event);
        }
    }

    private void applyFilters(SensorEvent evt) {
        float[] evtValues = evt.values.clone();

        //Low-pass filter
        if (verifyFirstValues != 0) {
            for (int i = 0; i < evtValues.length; i++) {
                accelerometerValues[i] = accelerometerValues[i] + alpha * (evtValues[i] - accelerometerValues[i]);
            }
        } else {
            verifyFirstValues = 1;
            accelerometerValues = evtValues;
        }

        //Moving Average filter
        if (movingAverageCount == 0) {
            movingAverageCount++;
            movingAverageValues[0] = accelerometerValues[0];
            movingAverageValues[1] = accelerometerValues[1];
            movingAverageValues[2] = accelerometerValues[2];
        } else if (movingAverageCount < subset) {
            movingAverageCount++;
            movingAverageValues[0] += accelerometerValues[0];
            movingAverageValues[1] += accelerometerValues[1];
            movingAverageValues[2] += accelerometerValues[2];
        } else {
            movingAverageCount = 0;
            movingAverageValues[0] = movingAverageValues[0] / subset;
            movingAverageValues[1] = movingAverageValues[1] / subset;
            movingAverageValues[2] = movingAverageValues[2] / subset;
            compare(movingAverageValues);
        }
    }

    private void compare(float[] values) {
        if (verifyFirstComparison == 0) {
            acceleration[0] = Math.abs(values[0]); //X
            acceleration[1] = Math.abs(values[1]); //Y
            acceleration[2] = Math.abs(values[2]); //Z
            verifyFirstComparison = 1;
        } else {
            for(int i=0; i<3; i++) {
                if (Math.abs(values[i]) > acceleration[i]) {
                    acceleration[i] = Math.abs(values[i]);
                }
            }
        }
    }
}
