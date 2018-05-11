package com.example.administrator.newble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //日志
    public final static String TAG=MainActivity.class.getSimpleName();
    // 扫描蓝牙按钮
    private Button scan_btn;

    Switch swtich_btn;
    //预设的mac地址
    String mac="9C:1D:58:94:F4:32";

    //搜索到指定设备的标志
    private boolean flag=false;
    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 蓝牙扫描器
    private BluetoothLeScanner mBluetoothLeScanner;
    // 自定义Adapter
    private LeDeviceListAdapter mleDeviceListAdapter;
    // listview显示扫描到的蓝牙信息
    ListView lv;
    // 蓝牙扫描时间 10s
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    // 描述扫描蓝牙的状态
    //boolean mScanning;
    private boolean scan_flag;
    private Handler mHandler;
    int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        init();
        // 初始化蓝牙
        init_ble();
        scan_flag = true;
        // 自定义适配器
        mleDeviceListAdapter = new LeDeviceListAdapter(
                LayoutInflater.from(getApplicationContext()));
        // 为listview指定适配器
        lv.setAdapter(mleDeviceListAdapter);
        // 自定义蓝牙扫描函数（在Android 5.0以后重写）
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * 初始化
     */
    private void init() {
        //button 监听事件
        scan_btn = findViewById(R.id.scan_dev);


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scan_flag) {
                    mleDeviceListAdapter = new LeDeviceListAdapter(
                            LayoutInflater.from(getApplicationContext()));
                    lv.setAdapter(mleDeviceListAdapter);
                    scanLeDevice(true);


                } else {

                    scanLeDevice(false);
                    scan_btn.setText("扫描设备");
                }
            }
        });


        //开关监听事件
        swtich_btn = findViewById(R.id.switch1);
        swtich_btn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            startScaning();
                        } else {
                            stopScanning();
                        }
                    }
                });

        lv = this.findViewById(R.id.lv);
        mHandler = new Handler();
    }


    /**
     * 初始化化蓝牙，检查蓝牙是否打开
     */
    private void init_ble() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "init_ble:不支持Ble");
            finish();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Android M Permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        //打开蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "init_ble:请求打开蓝牙");
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 扫描蓝牙设备(enable)开始，（false）结束
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //  mScanning = false;
                    scan_flag = true;
                    scan_btn.setText("扫描设备");
                    Log.i(TAG, "run:扫描设备");
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            /* 开始扫描蓝牙设备，带ScanCallback 回调函数 */

            //mScanning = true;
            scan_flag = false;
            scan_btn.setText("停止扫描");
            mBluetoothLeScanner.startScan(mScanCallback);

        } else {
            Log.i(TAG, "scanLeDevice:停止扫描");
            //  mScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
            scan_flag = true;
        }
        invalidateOptionsMenu();
    }




    /**
     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
     */
    public void startScaning(){
        if(mScanCallback == null){
            Log.i(TAG, "startScaning:正在扫描");

        }
        mBluetoothLeScanner.startScan(mScanCallback);

    }
    /**
     * Stop scanning for BLE Advertisements.
     */
    public void stopScanning() {
        Log.d(TAG, "Stopping Scanning");

        // Stop the scan, wipe the callback.
        mBluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;
        mleDeviceListAdapter.clear();
        // Even if no new results, update 'last seen' times.
        //mleDeviceListAdapter.notifyDataSetChanged();
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(Constants.Service_UUID);
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }






    /**
     * 蓝牙扫描回调函数
     */
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //
            if(result.getDevice().getAddress().equals(mac)&&!flag)
            {
                Toast.makeText(MainActivity.this,"签到成功",Toast.LENGTH_SHORT).show();
                flag=true;
                Log.i(TAG, "getPosition:签到成功");
            }
            mleDeviceListAdapter.add(result);

            Log.i(TAG, result.getDevice().getName()+"onScanResult:rssi"+result.getRssi());
            mleDeviceListAdapter.notifyDataSetChanged();

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                mleDeviceListAdapter.add(result);
            }
            mleDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("error", "onScanFailed: 搜索蓝牙失败" + errorCode);
        }

    };
}
