package com.iot.barometer.bluetoothtest;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ListActivity implements BluetoothAdapter.LeScanCallback {

    private int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> mArrayAdapter ;
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord){

        int major = (scanRecord[5+20] & 0xff) * 0x100 + (scanRecord[6+20] & 0xff);
        int minor  = (scanRecord[7+20] & 0xff) * 0x100 + (scanRecord[8+20] & 0xff);
        String str1 = "";
			for(int i = 0; i < scanRecord.length;i++){
                str1 = str1+" " + (scanRecord[i]& 0xff);
			}
      //  Log.e("rrr",str1);
        String str = device.getAddress();
        String substring = str.substring(Math.max(str.length() - 5, 0));
        Log.i("ddddd", "New LE Device: " + device.getAddress() + " @ " + rssi);
        Message msg = new Message();
        String textTochange = scanRecord[25]+","+scanRecord[28]+",rssi "+String.valueOf(rssi)+",major "+major+",minor "+minor;//substring+","+String.valueOf(rssi);
      //  String textTochange = null;

           // textTochange = String.valueOf(scanRecord);

        msg.obj = textTochange;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String t = sdf.format(d)+","+(String)msg.obj;
            mArrayAdapter.add(t);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevices = new SparseArray<BluetoothDevice>();
    }
    private void  init(){
        mArrayAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1);
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ((TextView)findViewById(R.id.log)).setText("mBluetoothAdapter == null" );
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            ((TextView)findViewById(R.id.log)).setText("mBluetoothAdapter.isEnabled() ==  no" );
        }



     /*   mBluetoothAdapter.startDiscovery();


        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
         filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy*/

       // ListView mListView =(ListView)findViewById(R.id.list);
        setListAdapter(mArrayAdapter);
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

    }


    @Override
    protected void onPause() {
        mBluetoothAdapter.stopLeScan(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mBluetoothAdapter.startLeScan(this);
        super.onResume();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "    " + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                mArrayAdapter.clear();
                mBluetoothAdapter.cancelDiscovery();
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                mBluetoothAdapter.startDiscovery();
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        init();
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

}
