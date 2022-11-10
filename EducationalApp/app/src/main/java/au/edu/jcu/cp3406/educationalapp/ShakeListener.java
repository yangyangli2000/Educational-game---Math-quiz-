package au.edu.jcu.cp3406.educationalapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ShakeListener implements SensorEventListener
{
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private long mLastTime;
    private OnShakeListener mShakeListener;
    private final Context mContext;

    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;


    public interface OnShakeListener
    {
       void onShake();
    }

    public ShakeListener(Context context)
    {
        mContext = context;
        OnResume();
    }

    public void setOnShakeListener(OnShakeListener listener)
    {
        mShakeListener = listener;
    }

    public void OnResume() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensorManager == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
        boolean supported = sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        if (!supported) {
            sensorManager.unregisterListener(this);
            throw new UnsupportedOperationException("Accelerometer not supported");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = new float[0];
        int sensor = 0;
        if (sensor != Sensor.TYPE_ACCELEROMETER) return;
        long now = System.currentTimeMillis();
        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }
        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X] +
                    values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z]
                    - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    if (mShakeListener != null) {
                        mShakeListener.onShake();
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}

