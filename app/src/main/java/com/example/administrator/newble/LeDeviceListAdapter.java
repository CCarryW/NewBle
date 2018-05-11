package com.example.administrator.newble;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


    public class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<ScanResult> mLeDevices;

        private LayoutInflater mInflator;

        LeDeviceListAdapter(LayoutInflater inflater) {
            super();

            mLeDevices = new ArrayList<>();

            mInflator = inflater;
        }
        /**
         * Search the adapter for an existing device address and return it, otherwise return -1.
         */
        private int getPosition(String address) {
            int position = -1;
            for (int i = 0; i < mLeDevices.size(); i++) {
                if (mLeDevices.get(i).getDevice().getAddress().equals(address)) {
                    position = i;
                    break;
                }
            }
            return position;
        }


        public void add(ScanResult scanResult) {
            int existingPosition = getPosition(scanResult.getDevice().getAddress());
            if (existingPosition >= 0) {
                mLeDevices.set(existingPosition, scanResult);

            }else{
                // Add new Device's ScanResult to list.
                mLeDevices.add(scanResult);

            }
        }


        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mLeDevices.get(i).getDevice().getAddress().hashCode();
        }

        /**
         * Clear out the adapter.
         */
        public void clear() {
            mLeDevices.clear();

        }
        /**
         * 重写getview
         **/
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem,viewGroup,false);
            }

            TextView deviceAddress =  view.findViewById(R.id.tv_deviceAddr);
            TextView deviceName = view.findViewById(R.id.tv_deviceName);
            TextView rssi= view.findViewById(R.id.tv_rssi);

            ScanResult scanResult = mLeDevices.get(i);

            int deviceRssi=mLeDevices.get(i).getRssi();
            String Name =scanResult.getDevice().getName();
            if (deviceName != null && deviceName.length()>0) {
                deviceName.setText(Name);
            }
           // deviceName.setText(R.string.unknown_device);
            deviceAddress.setText(scanResult.getDevice().getAddress());
            //这里rssi是int型,不能直接使用，在前面加空字符可显示
            rssi.setText(""+deviceRssi+"dB");
            return view;
        }

    }

