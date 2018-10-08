package cz.redcute.laskonka.things.laskonka_ble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BleDevice {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final String address, name;
    private BluetoothLeService mBluetoothLeService;
    private final Context context;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "Connecting to: "+address);
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Disconnected: "+address);
            mBluetoothLeService = null;
        }
    };

    public BleDevice(String address, String name, Context context){
        this.address = address;
        this.context = context;
        if(name == null){
            this.name = "";
        }else {
            this.name = name;
        }
    }

    public void createConnection(){
        Log.d(TAG,"Connecting to: "+address);

        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, context.BIND_AUTO_CREATE);
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public boolean isActiveConnection(){
        return mBluetoothLeService.isConnected();
    }

    @Override
    public String toString() {
        return address + " > " + name + (isActiveConnection() ? " connected" : " disconnected");
    }
}