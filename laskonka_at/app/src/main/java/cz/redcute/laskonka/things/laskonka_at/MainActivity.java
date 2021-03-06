package cz.redcute.laskonka.things.laskonka_at;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private static final String TAG = "HomeActivity";

    private Gpio ledGpio;
    private Gpio tlacitkoGpio;
    private DatabaseReference mDatabase;

    private SensorActivity sensorActivity;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        boolean state = false;

        @Override
        public void run() {
            state = !state;
            try {
                ledGpio.setValue(state);
            } catch (IOException e) {
                Log.w(TAG, "Error using GPIO", e);
            }
            //Log.d(TAG, "Changed state of LED to: " + state);
            timerHandler.postDelayed(this, 500);

            sensorActivity.readTempPress();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManager pioManager = PeripheralManager.getInstance();
        Log.d(TAG, "Available GPIO: " + pioManager.getGpioList());

        try {
            ledGpio = pioManager.openGpio("BCM17");
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            timerHandler.postDelayed(timerRunnable, 500);
        } catch (IOException e) {
            Log.w(TAG, "Error opening GPIO", e);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("message");
        listeningTlacitko(pioManager);

        sensorActivity = new SensorActivity();

    }


    protected void listeningTlacitko(PeripheralManager pioManager) {
        GpioCallback mGpioCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                // Read the active low pin state
                try {
                    Log.d(TAG, "Changed state of Tlacitko to: " + tlacitkoGpio.getValue());
                    if(!tlacitkoGpio.getValue())
                        mDatabase.setValue("Tlacitko zmacknute :-) <3");
                    else
                        mDatabase.setValue("Zmackni tlacitko...prosim!!!!");
                } catch (IOException e) {
                    Log.w(TAG, tlacitkoGpio + "Err with tlacitko value");
                }

                // Continue listening for more interrupts
                return true;
            }

            @Override
            public void onGpioError(Gpio gpio, int error) {
                Log.w(TAG, tlacitkoGpio + ": Error event " + error);
            }
        };

        try {
            tlacitkoGpio = pioManager.openGpio("BCM4");
            tlacitkoGpio.setDirection(Gpio.DIRECTION_IN);

            tlacitkoGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            tlacitkoGpio.registerGpioCallback(mGpioCallback);
            Log.d(TAG, "Tlacitko setup");

        } catch (IOException e) {
            Log.w(TAG, "Error opening GPIO", e);
        }




    }


}