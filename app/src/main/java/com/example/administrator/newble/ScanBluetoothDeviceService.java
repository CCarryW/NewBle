package com.example.administrator.newble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScanBluetoothDeviceService extends Service {
    private static final String TAG = ScanBluetoothDeviceService.class.getSimpleName();
    public ScanBluetoothDeviceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
