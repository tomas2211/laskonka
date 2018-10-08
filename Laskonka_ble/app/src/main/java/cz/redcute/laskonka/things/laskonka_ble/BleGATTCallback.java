package cz.redcute.laskonka.things.laskonka_ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
/*
public class BleGATTCallback  {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final List<BleDevice> devices;

    public BleGATTCallback(){

        devices = new ArrayList<BleDevice>();
    }


}




    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG,"Connection state change.");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                Log.i(TAG, mBluetoothDeviceAddress + ": Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, mBluetoothDeviceAddress + ": Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, mBluetoothDeviceAddress + ": Disconnected from GATT server.");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG,mBluetoothDeviceAddress + ": Discovered GATT services");

                if(setUpDevice(gatt)){
                    //disconnect();
                }

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, mBluetoothDeviceAddress + ": onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(characteristic.getUuid().toString().equals(BleAttributes.LASKONKA_DATA_CHAR)) {
                    Log.d(TAG, "Read Laskonka data char: "+characteristic.getValue());
                }
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


    }
*/