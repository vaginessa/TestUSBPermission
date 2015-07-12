package com.micronet_inc.abest.testusbpermission;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "UsbPermissionTest";
    private PendingIntent mPermissionIntent;
    private static int RQS_USB_PERMISSION = 0;


    private BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_USB_PERMISSION)){

                synchronized(this){
                   // UsbDevice usbAccessory = UsbManager.getAccessory(intent);

                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                    //    OpenUsbAccessory(usbAccessory);

                        Toast.makeText(MainActivity.this,
                                "ACTION_USB_PERMISSION accepted",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this,
                                "ACTION_USB_PERMISSION rejected",
                                Toast.LENGTH_LONG).show();
                        //finish();
                    }
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction();

        Intent intent_UsbPermission = new Intent(ACTION_USB_PERMISSION);
        mPermissionIntent = PendingIntent.getBroadcast(this, RQS_USB_PERMISSION,
                intent_UsbPermission,0);
        IntentFilter intentFilter_UsbPermission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbPermissionReceiver, intentFilter_UsbPermission);



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

    private static final String ACTION_USB_PERMISSION = "abest.USB_PERMISSION";

    public void buttonOnClick(View v)
    {
        Button button = (Button)v;
        ((Button)v).setText("Clicked");
        UsbManager usbManager;
        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);

        TextView textView = (TextView)findViewById(R.id.textview1);


        //usbManager.getDeviceList();
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Log.d(TAG, "size = " + deviceList.size());

        Iterator<UsbDevice> it = deviceList.values().iterator();

        UsbDevice qbridge = null;
        UsbInterface usbInterface;

        String t = "";

        while(it.hasNext())
        {
            UsbDevice device = it.next();
            Log.d(TAG, "device name = " + device.getDeviceName() + ", "
                            + Integer.toHexString(device.getVendorId())
                            + ":"
                            + Integer.toHexString(device.getProductId())
            );

            t += "device name = " + device.getDeviceName()
                    + ", "
                    + Integer.toHexString(device.getVendorId())
                    + ":"
                    + Integer.toHexString(device.getProductId()) + "\n";

            /*Toast toast = Toast.makeText(getApplicationContext(), "One " + device.getDeviceName(), Toast.LENGTH_SHORT );
            toast.show();*/

            if(device.getVendorId() == 0x0403/* && device.getProductId() == 0x6015*/)
            {
                if(device.getInterfaceCount() == 1) {
                    usbInterface = device.getInterface(0);
                    qbridge = device;
                }

            }
        }




        if(null != qbridge) {
            UsbDeviceConnection connection = usbManager.openDevice(qbridge);

            t += "try to open device " + qbridge.getDeviceName() + "\n";
            if(connection == null)
            {
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);


                synchronized (usbManager) {
                    usbManager.requestPermission(qbridge, mPermissionIntent);
                }
            }

            //Toast toast2 = Toast.makeText(getApplicationContext(), "tryed open", Toast.LENGTH_SHORT );
            //toast2.show();

        }

        textView.setText(t);


/*
        UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
        if(connection == null)
        {
            button.setText("Open device fail");
            usbManager.requestPermission(sPort.getDriver().getDevice(), mPermissionIntent);
        }
*/



    }
}
