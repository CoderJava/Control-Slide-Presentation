package com.ysn.codepolitan_aplikasikontrolslidepresentasi;

import android.bluetooth.BluetoothDevice;

/**
 * Created by root on 20/03/17.
 */

public class Devices {
    private String deviceName;
    private BluetoothDevice bluetoothDevice;

    public Devices(String deviceName, BluetoothDevice bluetoothDevice) {
        this.deviceName = deviceName;
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
