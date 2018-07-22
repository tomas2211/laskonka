package cz.redcute.laskonka.things.laskonka_at;

import android.util.Log;

import com.samgol.driver.bmp180.Bmp180;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SensorActivity {
    private Bmp180 mBmp180;
    private static final String I2C_BUS = "I2C1";

    public SensorActivity(){
        initSensor();
    }

    public float[] readTempPress(){
        float temp = 0, press = 0;
        float[] arr = {0,0};
        try {
            temp = mBmp180.readTemperature();
            press = mBmp180.readPressure();
            double alt = mBmp180.readAltitude();
            Log.d(TAG, "loop: temp " + temp + " alt: " + alt + " press: " + press);
        } catch (IOException e) {
            Log.e(TAG, "Sensor loop  error : ", e);
        }
        arr[0] = temp;
        arr[1] = press;
        return arr;
    }


    private void initSensor(){
        mBmp180 = new Bmp180(I2C_BUS);
    }

    private void readData(){
        try {
            float temp = mBmp180.readTemperature();
            float press = mBmp180.readPressure();
            double alt = mBmp180.readAltitude();
            Log.d(TAG, "loop: temp " + temp + " alt: " + alt + " press: " + press);
        } catch (IOException e) {
            Log.e(TAG, "Sensor loop  error : ", e);
        }
    }

    private void closeSensor(){
        try {
            mBmp180.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSensor  error: ", e);
        }
        mBmp180 = null;
    }
}
