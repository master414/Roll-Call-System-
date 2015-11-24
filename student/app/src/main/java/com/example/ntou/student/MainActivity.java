package com.example.ntou.student;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    static final UUID uuid = UUID.fromString(SPP_UUID);
    ArrayList<String> devices = new ArrayList<String>();
    BluetoothAdapter mBluetoothAdapter;
    IntentFilter intentfilter;
    Button btnsearch,btnconnect,bt;
    TextView tv1;
    Spinner spinner1;
    SppConnect sppConnect;
    SppReceiver sppReceiver;
    String devAddress = null;
    boolean sppConnected = false;
    static final String TAG = "BTSPP";
    private String msg = "";
    ArrayAdapter<String> adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt=(Button)findViewById(R.id.test);
        btnsearch= (Button) this.findViewById(R.id.search);
        btnconnect= (Button) this.findViewById(R.id.connect);
        btnsearch.setOnClickListener(this);
        btnconnect.setOnClickListener(this);
        tv1= (TextView) this.findViewById(R.id.textView);
        spinner1= (Spinner) this.findViewById(R.id.spinner);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentfilter = new IntentFilter();
        intentfilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentfilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentfilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        adapter1 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, devices);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

// TODO Auto-generated method stub
                devAddress = ((String) devices.get(position))
                        .split("\\|")[1];
                parent.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"您選擇裝置：" + parent.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
// TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "您沒有選擇任何裝置。",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void gotodiplay(View v){
        Intent it = new Intent(this,display.class);
        startActivity(it);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v==btnsearch)
        {
            registerReceiver(BluetoothBroadcastReveiver, intentfilter);
            mBluetoothAdapter.cancelDiscovery(); // 搜尋裝置前先確認藍芽裝置不是處於搜尋中
            mBluetoothAdapter.startDiscovery();

        }
        else if(v==btnconnect)
        {

            if (sppConnected || devAddress == null) {
                Log.d(TAG, "NULL sppConnected = " + sppConnected
                        + " ;devAddress= " + devAddress);
                return;
            }
            if (sppConnect != null) {
                sppConnect.cancel();
                sppConnect = null;
            }
            if (sppReceiver != null) {
                sppReceiver.cancel();
                sppReceiver = null;
            }
            sppConnect = new SppConnect();
            sppConnect.start();

        }

    }



    private class SppReceiver extends Thread {

        private final InputStream input;
        private final BluetoothSocket mBluetoothSocket;

        public SppReceiver(BluetoothSocket socketIn) {

            Log.i(TAG, "SppReceiver");
            mBluetoothSocket = socketIn;
            InputStream tmpIn = null;
            try {
                tmpIn = socketIn.getInputStream();

            } catch (IOException e) {
// TODO: handle exception
                Log.i(TAG, "SppReceiver : tmpIn is empty");
            }

            input = tmpIn;
            Log.i(TAG, "SppReceiver : input = tmpIn ");
        }

        public void run() {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);
            while(true) {

                try {
                    for (int k = 0; k < 10; k++)
                        msg = msg + br.readLine().toString() + "\n";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mBluetoothHandler.sendEmptyMessage(0);
            }
        }

        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
// TODO: handle exception
            }
        }
        Handler mBluetoothHandler = new Handler() {
            public void handleMessage(Message message) {
                if (msg != null)
                    tv1.append(msg);
                msg = null;
            }
        };
    }
    private class SppConnect extends Thread {

        private final BluetoothSocket mBluetoothSocket;

        public SppConnect() {
            BluetoothSocket tmpBluetoothSocket = null;

            try {
                tmpBluetoothSocket = mBluetoothAdapter.getRemoteDevice(
                        devAddress).createRfcommSocketToServiceRecord(uuid);
                Log.d(TAG, "SppConnect(): createRfcommSocketToServiceRecord ");
            } catch (Exception e) {
// TODO: handle exception
            }
            mBluetoothSocket = tmpBluetoothSocket;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "SppConnect(): mBluetoothAdapter.cancelDiscovery(); ");

            try {
                mBluetoothSocket.connect();
                Log.d(TAG, "SppConnect():mBluetoothSocket.connect(); ");
                synchronized (MainActivity.this) {
                    if (sppConnected) {
                        return;
                    }
                    connected(mBluetoothSocket);
                }
            } catch (IOException e) {
// TODO: handle exception
                Log.d(TAG,
                        "--- SppConnect():mBluetoothSocket.connect(); Failed!!! ");
                try {
                    mBluetoothSocket.close();
                    Log.d(TAG, "--- SppConnect():mBluetoothSocket.close() ");
                } catch (IOException e2) {
// TODO: handle exception
                    Log.d(TAG, "mBluetoothSocket.close() failed!");
                }
                connectionFailed();
            }
        }

        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
// TODO: handle exception
            }
        }
    }
    public void connected(BluetoothSocket BTSocketIn) {
// TODO Auto-generated method stub
        sppConnected = true;
        Log.e(TAG, "++ connected() : BTSocketIn = " + BTSocketIn);
        spinner1.setClickable(false);
        Log.e(TAG, "++ connected() : spinner1.setClickable(false);");
        if (sppReceiver != null) {
            sppReceiver.cancel();
            sppReceiver = null;
        }

        sppReceiver = new SppReceiver(BTSocketIn);
        sppReceiver.start();
        Log.e(TAG, "++ connected() : sppReceiver.start();");
        Log.e(TAG, "++ connected() 成功，連線中");
    }
    public void connectionFailed() {
// TODO Auto-generated method stub
        Log.d("TAG", "++ connectionFailed()");
//SppConnecthandler.sendEmptyMessage(0);
        sppConnected = false;
        if (sppConnect != null) {
            sppConnect.cancel();
            sppConnect = null;
        }
        if (sppReceiver != null) {
            sppReceiver.cancel();

            sppReceiver = null;
        }

    }
    private void disconnect() {
        spinner1.setClickable(true);
        sppConnected = false;
        Log.d(TAG, "+ disconnected() 連線取消，重新開始等待連線Thread SppServer()");

    }
    private BroadcastReceiver BluetoothBroadcastReveiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
// TODO Auto-generated method stub
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            Object[] listName = bundle.keySet().toArray();

// 顯示所有收到的資訊及細節
            for (int i = 0; i < listName.length; i++) {
                String keyName = listName[i].toString();

            }
            BluetoothDevice device = null;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + "|" + device.getAddress();
                if (devices.indexOf(str) == -1) {
                    devices.add(str);
                }
                adapter1.notifyDataSetChanged(); // 通知adapter1 devices有更新
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "正在配對...");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "完成配對");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "取消配對");
                        break;
                    default:
                        break;
                }
            }
        }
    };
}
