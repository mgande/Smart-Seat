package h3o.smartseat;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Record mRecordFrag;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String[] mNameOrder = {"Back Left", "Back Right", "Front Left", "Front Right", "Middle",
            "Top", "Middle", "Bottom"};

    //Bluetooth globals
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket chair_socket;
    //standard uuid for serial boards
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return Monitor.newInstance();
                case 1:
                    mRecordFrag = Record.newInstance();
                    return mRecordFrag;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Monitor";
                case 1:
                    return "Record Data";
            }
            return null;
        }
    }

    public void connectOnClick(View view) {
        if (!checkBluetooth())
            return;

        Button btn = (Button) view;
        btn.setEnabled(false);

        if (btn.getText().toString().equals("Connect")) {
            btn.setText("Connecting...");
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("HC-05")) {
                        new ConnectThread().execute(device);
                        break;
                    }
                }
            }
        }
        else {
            new DisconnectThread().execute();
        }
    }

    public void sendOnClick(View view) {
        Button btn = (Button) view;
        if(btn.getText().toString().equals("Inflate")) {
            new SendOutput().execute(true);
        } else {
            new SendOutput().execute(false);
        }
        btn.setEnabled(false);
    }

    private boolean checkBluetooth() {
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setMessage("Your device does not support Bluetooth.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            //registers bluetooth change event
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK) {
                Button btn = (Button) findViewById(R.id.connect_button);
                connectOnClick(btn);
            }
        }
    }

    private class ConnectThread extends AsyncTask<BluetoothDevice, Void, Boolean> {
        @Override
        protected Boolean doInBackground(BluetoothDevice... params) {
            Boolean found = false;

            try {
                chair_socket = params[0].createRfcommSocketToServiceRecord(mDeviceUUID);
            } catch (IOException e) {
                //alertBox("Failed to create socket.");
                e.printStackTrace();
            }
            try {
                chair_socket.connect();
                found = true;
            } catch (IOException e) {
                try {
                    chair_socket.close();
                    //alertBox("Failed to connect.");
                    e.printStackTrace();
                } catch (IOException e1) {
                    //alertBox("Failed to close after not connecting.");
                    e.printStackTrace();
                }
            }

            return found;
        }

        protected void onPostExecute(Boolean result) {
            Button btn = (Button) findViewById(R.id.connect_button);
            if(result) {
                Button btn_inflate = (Button) findViewById(R.id.send_button);
                btn_inflate.setEnabled(true);

                btn.setText("Disconnect");
                new ReadInput();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT);
                toast.show();
                btn.setText("Connect");
            }

            btn.setEnabled(true);
        }
    }

    private class DisconnectThread extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean closed = false;
            try {
                chair_socket.close();
                closed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return closed;
        }

        protected void onPostExecute(Boolean result) {
            Button btn = (Button) findViewById(R.id.connect_button);
            if(!result) {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed to disconnect", Toast.LENGTH_SHORT);
                toast.show();
            }
            Button btn_inflate = (Button) findViewById(R.id.send_button);
            btn_inflate.setEnabled(false);

            btn.setText("Connect");
            btn.setEnabled(true);

            mRecordFrag.updateChairData(false, null);
        }
    }

    private class SendOutput extends AsyncTask<Boolean, Void, Boolean> {
        private boolean inflate;

        @Override
        protected Boolean doInBackground(Boolean... params) {
            OutputStream mmOutStream;
            inflate = params[0];

            try {
                mmOutStream = chair_socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try {
                if(inflate) {
                    mmOutStream.write(85);
                } else {
                    mmOutStream.write(86);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            Button btn = (Button) findViewById(R.id.send_button);

            if(result) {
                if(inflate) {
                    btn.setText("Deflate");
                } else {
                    btn.setText("Inflate");
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed to send", Toast.LENGTH_SHORT);
                toast.show();
            }
            btn.setEnabled(true);
        }
    }

    private class ReadInput implements Runnable {
        private Thread t;
        private InputStream mmInStream;
        private Handler handler;
        private TextView monitor;

        public ReadInput() {
            t = new Thread(this, "Input Thread");

            try {
                mmInStream = chair_socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            monitor = (TextView) findViewById(R.id.monitor_view);
            handler = new Handler();
            t.start();
        }

        @Override
        public void run() {
            byte[] buffer = new byte[128];
            final byte[] dataBuffer = new byte[45];
            int pos = 0;
            OutputStream mmOutStream;

            try {
                mmOutStream = chair_socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                mmOutStream.write(87);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int bytesRead;

            while(true) {
                try {
                    bytesRead = mmInStream.read(buffer);

                    for(int i = 0; i < bytesRead; i++) {
                        dataBuffer[pos] = buffer[i];
                        pos++;
                    }

                    if(pos > 44) {
                        Log.d("array", Arrays.toString(dataBuffer));
                        pos = 0;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String outStr = "";
                                double[] chairData = new double[8];
                                for(int i = 0; i < 8; i++) {
                                    double val = ((dataBuffer[(i*5) + 3] & 0xff) << 8) | (dataBuffer[(i*5)+4] & 0xff);
                                    chairData[i] = val;
                                    if(i < 5) {
                                        outStr += mNameOrder[i] + ": " + Double.toString(val) + "\n";
                                    }
                                    else {
                                        val = val * 0.00080566406;
                                        val = (13-val)/val;
                                        outStr += mNameOrder[i] + ": " + Double.toString(val) + "\n";
                                    }
                                }
                                monitor.setText(outStr);

                                mRecordFrag.updateChairData(true, chairData);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    mmOutStream.write(87);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}