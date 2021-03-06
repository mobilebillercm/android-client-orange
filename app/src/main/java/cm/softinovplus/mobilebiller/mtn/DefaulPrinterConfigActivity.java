package cm.softinovplus.mobilebiller.mtn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import cm.softinovplus.mobilebiller.mtn.adapter.SettingBluetoothAdapter;
import cm.softinovplus.mobilebiller.mtn.dialog.DefaultBluetoothSettingDialog;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

public class DefaulPrinterConfigActivity extends AppCompatActivity {

    public static AppCompatActivity thisActivity;
    private BluetoothAdapter G_bluetoothAdapter;
    private Set<BluetoothDevice> G_devices;
    private ListView bluetooth_list;
    private TextView default_printer_name, default_printer_mac_address;
    private SettingBluetoothAdapter adapter;
    public static int mStackLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defaul_printer_config);
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

        String macaddress = sharedPreferences.getString(Utils.DEFAULT_MAC_ADDRESS, null);

        thisActivity = this;
        default_printer_name = findViewById(R.id.default_printer_name);
        default_printer_mac_address = findViewById(R.id.default_printer_mac_address);
        bluetooth_list = findViewById(R.id.bluetooth_list);


        G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (G_bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (!G_bluetoothAdapter.isEnabled()) {
                G_bluetoothAdapter.enable();
            }
            G_devices = G_bluetoothAdapter.getBondedDevices();

            if (macaddress != null){
                Object []devices = G_devices.toArray();
                for (int i=0; i<devices.length; i++){
                    BluetoothDevice device = (BluetoothDevice) devices[i];
                    if (device.getAddress().equals(macaddress)){
                        default_printer_name.setText(device.getName());
                        default_printer_mac_address.setText("MAC: " + device.getAddress());
                        break;
                    }
                }
            }

            adapter = new SettingBluetoothAdapter(this, G_devices);
            bluetooth_list.setAdapter(adapter);
            bluetooth_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter = new SettingBluetoothAdapter(thisActivity, G_devices);
                    bluetooth_list.setAdapter(adapter);
                    //view.setBackgroundColor(Color.rgb(171, 242, 188));
                    //Log.e("ITEM VIEW", "" + ((TextView)view.findViewById(R.id.device_mac_address)).getText());
                    showDefaultPrinterSettingDialog(adapter, view, position);

                }
            });

        }

    }

    public void showDefaultPrinterSettingDialog(SettingBluetoothAdapter adapter, View view, int position){
        mStackLevel++;

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        SetDefaultPrinter defaultPrinter = new SetDefaultPrinter(this.G_devices, adapter, view, position, default_printer_name, default_printer_mac_address);

        DefaultBluetoothSettingDialog defaultBluetoothSettingDialog = DefaultBluetoothSettingDialog.newInstance(defaultPrinter, mStackLevel);
        //QRCodeDialog qrCodeDialog = new QRCodeDialog();
        //errorDialog.setErrorMessage(message);
        defaultBluetoothSettingDialog.show(ft, "dialog");
    }

    public class SetDefaultPrinter{
        private Set<BluetoothDevice> gdevices;
        private SettingBluetoothAdapter adapter;
        private View view;
        private int position;
        private TextView name, mac;
        private String nom;

        private SetDefaultPrinter(Set<BluetoothDevice> gdevices, SettingBluetoothAdapter adapter, View view, int position, TextView name, TextView mac) {
            this.gdevices = gdevices;
            this.adapter = adapter;
            this.view = view;
            this.position = position;
            this.name = name;
            this.mac = mac;
            this.nom = ((BluetoothDevice)this.gdevices.toArray()[this.position]).getName();
        }

        public void setDefaultPrinter(){

            BluetoothDevice device = (BluetoothDevice)this.gdevices.toArray()[this.position];
            SharedPreferences.Editor editeur  = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
            editeur.putString(Utils.DEFAULT_MAC_ADDRESS, device.getAddress());
            editeur.apply();
            this.name.setText(device.getName());
            this.mac.setText("MAC: " + device.getAddress());
            TextView textView = view.findViewById(R.id.device_name);
            textView.setTextColor(Color.rgb(50,200,50));
            textView.setText(textView.getText() + " -------------------------------------------" + device.getAddress());
            this.view.setBackgroundColor(Color.rgb(171, 242, 188));
            this.adapter.notifyDataSetChanged();

        }

        public String getNom(){
            return this.nom;
        }
    }


}
