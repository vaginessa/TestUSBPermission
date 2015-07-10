package com.micronet_inc.abest.testusbpermission;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "UsbPermissionTest";
    private PendingIntent mPermissionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            if(device.getVendorId() == 0x0403 && device.getProductId() == 0x6015)
            {
                if(device.getInterfaceCount() == 1) {
                    usbInterface = device.getInterface(0);
                    qbridge = device;
                }

            }
        }

        Toast toast = Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT );
            toast.show();

        if(null != qbridge) {
            UsbDeviceConnection connection = usbManager.openDevice(qbridge);

            if(connection == null)
            {
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);


                usbManager.requestPermission(qbridge, null);
            }

        }


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
