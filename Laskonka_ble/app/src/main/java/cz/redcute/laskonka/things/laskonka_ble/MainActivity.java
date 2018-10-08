package cz.redcute.laskonka.things.laskonka_ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    //private LeDeviceList mLeDeviceList;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;

    private List<BleDevice> mBleDevices;

    private boolean mScanning;
    private Handler mHandler;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int STATUS_CHECK_INTERVAL = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Hello world");

        mBleDevices = new ArrayList<>();

        mHandler = new Handler();
        mStatusChecker.run();

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG,"BLE NOT SUPPORTED!");
            finish();
        }

        scanLeDevice(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mScanning = true;
            mBluetoothScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothScanner.stopScan(mLeScanCallback);
        }
    }

    private boolean isConnected(String address){
        for(BleDevice bd: mBleDevices){
            if(bd.getAddress().equals(address)){
                return true;
            }
        }
        return false;
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                for(BleDevice bd : mBleDevices){
                    Log.d(TAG,bd.toString());
                }
            } finally {
                mHandler.postDelayed(mStatusChecker, STATUS_CHECK_INTERVAL);
            }
        }
    };

    // Device scan callback.
    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice dev = result.getDevice();
                    Log.d(TAG,"Looked up "+dev.getName()+" : "+dev.getAddress());

                    if(!isConnected(dev.getAddress())) {
                        //Log.d(TAG," - not in the list");
                        if (BleAttributes.recognizedDevices.contains(dev.getAddress())) {
                            Log.d(TAG,"Connecting to "+dev.getName()+" : "+dev.getAddress());
                            //Log.d(TAG," - connecting to it");
                            BleDevice bd = new BleDevice(dev.getAddress(),dev.getName(),getApplicationContext());
                            mBleDevices.add(bd);
                            bd.createConnection();
                            //scanLeDevice(false);
                        }
                    }
                }
            };

}
