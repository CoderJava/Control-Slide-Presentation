package com.ysn.codepolitan_aplikasikontrolslidepresentasi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivityTAG";
    private static final int NEXT = 2, PREVIOUS = 1;
    private ListView listViewDevices;
    private TextView textViewStatus;
    private FloatingActionButton floatingActionButtonAction;
    private FloatingActionButton floatingActionButtonNext;
    private FloatingActionButton floatingActionButtonPrevious;

    private final int request_enable_bt = 243;
    public boolean isConnected = false;
    private BluetoothAdapter bluetoothAdapter = null;
    private ArrayAdapter<Devices> arrayAdapterDevices = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Devices devices = new Devices(bluetoothDevice.getName(), bluetoothDevice);
                arrayAdapterDevices.add(devices);
            }
            if (arrayAdapterDevices.getCount() > 0) {
                textViewStatus.setVisibility(View.GONE);
                listViewDevices.setVisibility(View.VISIBLE);
            } else {
                textViewStatus.setVisibility(View.VISIBLE);
                listViewDevices.setVisibility(View.GONE);
                textViewStatus.setText("Oops... New Devices not detected :(");
            }
        }
    };

    private Devices devicesSelected;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadComponent();
        checkBluetoothAdapter();
    }

    private void checkBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showToast("Device not support Bluetooth", Toast.LENGTH_LONG);
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intentEnableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnableBluetooth, request_enable_bt);
        }
    }

    @Override
    protected void onResume() {
        isConnected = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        socketBluetoothClose();
        super.onStop();
    }

    private void socketBluetoothClose() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadComponent() {
        listViewDevices = (ListView) findViewById(R.id.list_view_devices);
        textViewStatus = (TextView) findViewById(R.id.text_view_status);
        floatingActionButtonAction = (FloatingActionButton) findViewById(R.id.floating_action_button_action_activity_main);
        floatingActionButtonNext = (FloatingActionButton) findViewById(R.id.floating_action_button_next_activity_main);
        floatingActionButtonPrevious = (FloatingActionButton) findViewById(R.id.floating_action_button_previous_activity_main);

        arrayAdapterDevices = new ArrayAdapter<Devices>(this, android.R.layout.simple_list_item_1);
        listViewDevices.setAdapter(arrayAdapterDevices);
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (isConnected) {
                    showToast("Before to connect another device, you must disconnected from current device", Toast.LENGTH_LONG);
                } else {
                    devicesSelected = (Devices) adapterView.getAdapter().getItem(position);
                    showToast("Device: " + devicesSelected.getDeviceName() + " selected", Toast.LENGTH_SHORT);
                }
            }
        });
        floatingActionButtonAction.setOnClickListener(this);
        floatingActionButtonNext.setOnClickListener(this);
        floatingActionButtonPrevious.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == floatingActionButtonAction) {
            if (isConnected) {
                socketBluetoothClose();
                hideActions();
                isConnected = false;
                showToast("You're device has been disconnected from: " + devicesSelected.getDeviceName(), Toast.LENGTH_SHORT);
            } else {
                bluetoothAdapter.cancelDiscovery();
                try {
                    bluetoothDevice = devicesSelected.getBluetoothDevice();
                    UUID uuid = UUID.fromString("0f2b61c1-8be2-40e6-ab90-e735818da0a7");
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);

                    Observable<BluetoothSocket> observable = Observable.create(new Observable.OnSubscribe<BluetoothSocket>() {
                        @Override
                        public void call(Subscriber<? super BluetoothSocket> subscriber) {
                            subscriber.onNext(bluetoothSocket);
                            subscriber.onCompleted();
                        }
                    });
                    Subscriber<BluetoothSocket> subscriber = new Subscriber<BluetoothSocket>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                            if (bluetoothSocket.isConnected()) {
                                isConnected = true;
                                showActions();
                                showToast("You're device connected to: " + devicesSelected.getDeviceName(), Toast.LENGTH_SHORT);
                                Log.d(TAG, "isConnected");
                            } else {
                                isConnected = false;
                                hideActions();
                                showToast("You're device fail to connect to: " + devicesSelected.getDeviceName(), Toast.LENGTH_SHORT);
                                Log.d(TAG, "isDisconnected");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(BluetoothSocket bluetoothSocket) {
                            try {
                                bluetoothSocket.connect();
                                Log.d(TAG, "onNext");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    observable.subscribeOn(Schedulers.newThread());
                    observable.observeOn(AndroidSchedulers.mainThread());
                    observable.subscribe(subscriber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (view == floatingActionButtonNext) {
            OutputStream outputStream;
            try {
                outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(NEXT);
                showToast("action: NEXT - Ok", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("action: NEXT - Fail", Toast.LENGTH_SHORT);
            }
        } else if (view == floatingActionButtonPrevious) {
            try {
                OutputStream outputStream;
                outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(PREVIOUS);
                showToast("action: PREVIOUS - Ok", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("action: PREVIOUS - Fail", Toast.LENGTH_SHORT);
            }
        }
    }

    private void hideActions() {
        floatingActionButtonAction.setImageResource(R.drawable.ic_screen_share_white_24dp);
        floatingActionButtonNext.setVisibility(View.GONE);
        floatingActionButtonPrevious.setVisibility(View.GONE);
    }

    private void showActions() {
        floatingActionButtonAction.setImageResource(R.drawable.ic_stop_screen_share_white_24dp);
        floatingActionButtonNext.setVisibility(View.VISIBLE);
        floatingActionButtonPrevious.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == request_enable_bt) {
            showToast("Bluetooth On", Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_scan_new_device:
                loadScanNewDevices();
                return true;
            case R.id.item_paired_device:
                loadPairedDevices();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            arrayAdapterDevices.clear();
            for (BluetoothDevice pairedDevice : pairedDevices) {
                Devices devices = new Devices(pairedDevice.getName(), pairedDevice);
                arrayAdapterDevices.add(devices);
            }
            arrayAdapterDevices.notifyDataSetChanged();
            textViewStatus.setVisibility(View.GONE);
            listViewDevices.setVisibility(View.VISIBLE);
        } else {
            textViewStatus.setVisibility(View.VISIBLE);
            listViewDevices.setVisibility(View.GONE);
            textViewStatus.setText("Sorry... You don't have a paired devices :(");
        }
        showToast("Paired Devices is successfully loaded", Toast.LENGTH_SHORT);
    }

    private void loadScanNewDevices() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
        arrayAdapterDevices.clear();
        bluetoothAdapter.startDiscovery();
        showToast("Scan new Devices is start", Toast.LENGTH_SHORT);
    }

    private void showToast(String message, int length) {
        Toast.makeText(this, message, length)
                .show();
    }
}
