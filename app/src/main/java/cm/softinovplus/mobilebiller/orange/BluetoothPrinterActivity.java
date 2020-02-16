package cm.softinovplus.mobilebiller.orange;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import cm.softinovplus.mobilebiller.orange.adapter.MyBluetoothAdapter;
import cm.softinovplus.mobilebiller.orange.db.MySQLiteHelper;
import cm.softinovplus.mobilebiller.orange.db.SMSDataSource;
import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.CustomToast;
import cm.softinovplus.mobilebiller.orange.utils.TraiteImage;
import cm.softinovplus.mobilebiller.orange.utils.Utils;


public class BluetoothPrinterActivity extends AppCompatActivity {

    public static AppCompatActivity thisActivity;
    public static Bundle sms_data;
    public static String corps_message;
    public static long id;
    private BluetoothAdapter G_bluetoothAdapter;
    private Set<BluetoothDevice> G_devices;

    private Toolbar toolbar;
    private long sms_id;

    private SMS sms ;

    public static long getId() {
        return id;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_printer_layout);

        thisActivity = this;

        SharedPreferences prefs = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);


        sms_id = prefs.getLong(Utils.LAST_SMS_ID, 0);

        SMSDataSource dataSource = new SMSDataSource(getApplicationContext());
        dataSource.open();
        sms = dataSource.getSMSById(sms_id);
        dataSource.close();

        G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (G_bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Pas de Bluetooth", Toast.LENGTH_SHORT).show();
        } else {

            if (!G_bluetoothAdapter.isEnabled()) {
                G_bluetoothAdapter.enable();
            }
            G_devices = G_bluetoothAdapter.getBondedDevices();

            MyBluetoothAdapter myBluetooth_adapter = new MyBluetoothAdapter(this, G_devices, sms);
            ListView listeView = findViewById(R.id.bluetooth_list);
            listeView.setAdapter(myBluetooth_adapter);
            listeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SharedPreferences sharedPreferences_access_service = getSharedPreferences(Utils.APP_SERVICE_ACCESS, MODE_PRIVATE);

                    boolean serviceValidity = false;
                    boolean serviceFound = false;

                    try {
                        String accesses = sharedPreferences_access_service.getString(Utils.SERVICE_ACCESS,"");
                        Log.e("ACCESSES", accesses);
                        JSONArray jsonArray = new JSONArray(accesses);

                        for (int i= 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString(Utils.serviceid).equals(Utils.PRINT_SERVICE_ID)){
                                long startdate = jsonObject.getLong("startdate");
                                long enddate   = jsonObject.getLong("enddate");
                                long currentdate = jsonObject.getLong("currentdate");
                                if (startdate <= currentdate && currentdate <= enddate){
                                    serviceValidity = true;
                                }
                                serviceFound = true;
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!serviceFound || !serviceValidity){
                        Toast.makeText(getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ProgressBar progressBar = view.findViewById(R.id.print_loader);
                    MyAsyncTask mat = new MyAsyncTask(thisActivity,(BluetoothDevice)G_devices.toArray()[position], sms, progressBar);
                    mat.execute("");
                }
            });

        }

    }

    @SuppressLint("SimpleDateFormat") public static String makeDate(Long when){
        Date date = new Date(when);
        return (new SimpleDateFormat("dd/MM/yy HH:mm:ss")).format(date);
    }


    public static class MyAsyncTask extends AsyncTask<Object, Integer, Object> {
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mysocket = null;
        private OutputStream mmOutStream;
        private ProgressBar dialog;
        private ArrayList<Byte> tous_les_donnee;
        private SMS sms;
        private Context context;

        public MyAsyncTask(Context context, BluetoothDevice bt_device_, SMS sms, ProgressBar dialog) {
            this.context = context;
            mmDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bt_device_.getAddress());
            this.sms = sms;
            this.dialog = dialog;
            tous_les_donnee = new ArrayList<Byte>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            //String data = (String) params[0];
            TraiteImage traiteImage = new TraiteImage(this.context);
            try {
                mysocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (!mysocket.isConnected()) {
                    mysocket.connect();
                }
                try {

                    mmOutStream = mysocket.getOutputStream();

                    Log.e("SMS SMS SMS", sms.toString());

                    byte [] characterFont = {0x1B, 0x4D, 0x00};

                    for(int i=0; i<characterFont.length; i++){
                        Byte B = new Byte(characterFont[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] center = {0x1B, 0x61, 0x01};

                    for(int i=0; i<center.length; i++){
                        Byte B = new Byte(center[i]);
                        tous_les_donnee.add(B);
                    }

                    JSONArray jsonarray = traiteImage.processImage("logo/entete_recu_orange_money.jpg");
                    int leng = jsonarray.length();
                    for (int i = 0; i < leng; i++) {
                        Byte B = new Byte((byte) jsonarray.getInt(i));
                        tous_les_donnee.add(B);
                    }

                    byte [] bold = {0x1B, 0x45, 0x01};
                    for(int i=0; i<bold.length; i++){
                        Byte B = new Byte(bold[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<center.length; i++){
                        Byte B = new Byte(center[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] double_height = {0x1D, 0x21, 0x01};
                    for(int i=0; i<double_height.length; i++){
                        Byte B = new Byte(double_height[i]);
                        tous_les_donnee.add(B);
                    }

                   /* byte [] double_width = {0x1D, 0x21, 0x10};
                    for(int i=0; i<double_width.length; i++){
                        Byte B = new Byte(double_width[i]);
                        tous_les_donnee.add(B);
                    }*/


                    String tenant = sms.getTenant();
                    byte [] tenantBytes = (tenant + "\r\n").getBytes();
                    for(int i=0; i<tenantBytes.length; i++){
                        Byte B = new Byte(tenantBytes[i]);
                        tous_les_donnee.add(B);
                    }



                    byte [] double_height_width_off = {0x1D, 0x21, 0x00};
                    for(int i=0; i<double_height_width_off.length; i++){
                        Byte B = new Byte(double_height_width_off[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] linespacing = {0x1B, 0x33, 0x31};
                    for(int i=0; i<linespacing.length; i++){
                        Byte B = new Byte(linespacing[i]);
                        tous_les_donnee.add(B);
                    }


                    String part0 = "Tel: " + sms.getPhone() + "\r\n";
                    part0 += "E-Mail: " + sms.getEmail() + "\r\n";
                    part0 += "N.C: " + sms.getTaxpayernumber() + "\r\n";
                    part0 += "N.RC: " + sms.getNumbertraderegister() + "\r\n"+
                            "--------------------------------";

                    byte [] bytePart0 = part0.getBytes();

                    for(int i=0; i<bytePart0.length; i++){
                        Byte B = new Byte(bytePart0[i]);
                        tous_les_donnee.add(B);
                    }


                    /////////////////////////////////////////////////
                    /*  Line    */
                    byte [] boldOff = {0x1B, 0x45, 0x00};
                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] left = {0x1B, 0x61, 0x00};

                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }


                    /*String line = "_________________________________\r\n";

                    byte [] lineBytes = line.getBytes();
                    for(int i=0; i<lineBytes.length; i++){
                        Byte B = new Byte(lineBytes[i]);
                        tous_les_donnee.add(B);
                    }*/

                    /*  End line */
                    ////////////////////////////////////////////////



                    String part1 = "\r\nLe: " + Utils.makeDateDate(System.currentTimeMillis()) + "\r\n";


                    byte [] xx = part1.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xx.length; i++){
                        Byte B = new Byte(xx[i]);
                        tous_les_donnee.add(B);
                    }

                    String part2 = "ID Trans.: " + sms.getTransaction_id() + "\r\n";

                    for(int i=0; i<bold.length; i++){
                        Byte B = new Byte(bold[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] bytePart2 = part2.getBytes();
                    for(int i=0; i<bytePart2.length; i++){
                        Byte B = new Byte(bytePart2[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_operation = "Operation: ".getBytes() ;

                    for(int i=0; i<label_operation.length; i++){
                        Byte B = new Byte(label_operation[i]);
                        tous_les_donnee.add(B);
                    }


                    /*byte [] right = {0x1B, 0x61, 0x02};
                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }*/

                    String transactionType = (sms.getTransaction_type() == null)?" ":sms.getTransaction_type() + "\r\n";
                    byte [] operations = transactionType.getBytes();
                    for(int i=0; i<operations.length; i++){
                        Byte B = new Byte(operations[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] labelbeneficiaire= "Beneficiaire: ".getBytes(); //+ ;
                    for(int i=0; i<labelbeneficiaire.length; i++){
                        Byte B = new Byte(labelbeneficiaire[i]);
                        tous_les_donnee.add(B);
                    }

                    /*for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }*/

                    byte [] beneficiaire = (sms.getTransaction_beneficiary_name() + "\r\n").getBytes();
                    for(int i=0; i<beneficiaire.length; i++){
                        Byte B = new Byte(beneficiaire[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_compte_bene= "Tel. Benef.: " .getBytes(); //+ ;
                    for(int i=0; i<label_compte_bene.length; i++){
                        Byte B = new Byte(label_compte_bene[i]);
                        tous_les_donnee.add(B);
                    }

                    /*for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }*/

                    byte [] compte_bene = (sms.getTransaction_beneficiary_account_number() + "\r\n").getBytes();
                    for(int i=0; i<compte_bene.length; i++){
                        Byte B = new Byte(compte_bene[i]);
                        tous_les_donnee.add(B);
                    }

                    if (sms.getTransaction_made_by() != null && !sms.getTransaction_made_by().equals("")){

                        for(int i=0; i<bold.length; i++){
                            Byte B = new Byte(bold[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_compte_madeby= "Facture de: " .getBytes(); //+ ;
                        for(int i=0; i<label_compte_madeby.length; i++){
                            Byte B = new Byte(label_compte_madeby[i]);
                            tous_les_donnee.add(B);
                        }

                        /*for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }*/

                        byte [] madeby = (sms.getTransaction_made_by() + "\r\n").getBytes();
                        for(int i=0; i<madeby.length; i++){
                            Byte B = new Byte(madeby[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<boldOff.length; i++){
                            Byte B = new Byte(boldOff[i]);
                            tous_les_donnee.add(B);
                        }
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_ref= "Reference: " .getBytes(); //+ ;
                    for(int i=0; i<label_ref.length; i++){
                        Byte B = new Byte(label_ref[i]);
                        tous_les_donnee.add(B);
                    }

                    /*for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }*/

                    byte [] ref = (sms.getTransaction_reference() + "\r\n").getBytes();
                    for(int i=0; i<ref.length; i++){
                        Byte B = new Byte(ref[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }


                    byte [] double_height_m = {0x1D, 0x21, 0x01};
                    for(int i=0; i<double_height_m.length; i++){
                        Byte B = new Byte(double_height_m[i]);
                        tous_les_donnee.add(B);
                    }

                    /*byte [] double_width_m = {0x1D, 0x21, 0x10};
                    for(int i=0; i<double_width_m.length; i++){
                        Byte B = new Byte(double_width_m[i]);
                        tous_les_donnee.add(B);
                    }*/

                    for(int i=0; i<bold.length; i++){
                        Byte B = new Byte(bold[i]);
                        tous_les_donnee.add(B);
                    }

                    String amount = "Montant: " + sms.getTransaction_amount() + " " + sms.getTransaction_currency() + "\r\n";

                    byte [] xxx = amount.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xxx.length; i++){
                        Byte B = new Byte(xxx[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<double_height_width_off.length; i++){
                        Byte B = new Byte(double_height_width_off[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }



                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    /*byte[] label_fees= "Frais: " .getBytes();
                    for(int i=0; i<label_fees.length; i++){
                        Byte B = new Byte(label_fees[i]);
                        tous_les_donnee.add(B);
                    }



                    byte [] fees = (sms.getTransaction_fees() + "  " + sms.getTransaction_currency() + "\r\n").getBytes();
                    for(int i=0; i<fees.length; i++){
                        Byte B = new Byte(fees[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }*/

                    byte[] label_transaction_date= "Transaction du: " .getBytes(); //+ ;
                    for(int i=0; i<label_transaction_date.length; i++){
                        Byte B = new Byte(label_transaction_date[i]);
                        tous_les_donnee.add(B);
                    }

                    /*for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }*/

                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    /*Date date = null;
                    String newDateString = "";
                    try {
                        date = sdf.parse(sms.getTransaction_date());
                        sdf.applyPattern("dd-MM-yyyy HH:mm:ss");
                        newDateString = sdf.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/

                    byte [] transactiondate = (sms.getTransaction_date().substring(0, sms.getTransaction_date().length() - 3) + "\r\n").getBytes();
                    for(int i=0; i<transactiondate.length; i++){
                        Byte B = new Byte(transactiondate[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }


                    if (sms.getIs_yet_printed() == 1){

                        ////////////////////////////////////

                        byte [] double_height_duplicata = {0x1D, 0x21, 0x02};
                        for(int i=0; i<double_height_duplicata.length; i++){
                            Byte B = new Byte(double_height_duplicata[i]);
                            tous_les_donnee.add(B);
                        }

                        /*
                        byte [] double_width_m = {0x1D, 0x21, 0x10};
                    for(int i=0; i<double_width_m.length; i++){
                        Byte B = new Byte(double_width_m[i]);
                        tous_les_donnee.add(B);
                    }
                         */
                        byte [] double_width_duplicata = {0x1D, 0x21, 0x10};
                        for(int i=0; i<double_width_duplicata.length; i++){
                            Byte B = new Byte(double_width_duplicata[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<bold.length; i++){
                            Byte B = new Byte(bold[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<center.length; i++){
                            Byte B = new Byte(center[i]);
                            tous_les_donnee.add(B);
                        }

                        String duplicata = "DUPLICATA";

                        byte [] duplicataByte = duplicata.getBytes();
                        //mmOutStream.write(left);
                        for(int i=0; i<duplicataByte.length; i++){
                            Byte B = new Byte(duplicataByte[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<double_height_width_off.length; i++){
                            Byte B = new Byte(double_height_width_off[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<boldOff.length; i++){
                            Byte B = new Byte(boldOff[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }
                        //////////////////
                    }



                    String part3 = "\r\n" + sms.getTenant() + " vous remercie pour votre confiance." +
                            "\r\n\r\n--------------------------------\r\n\r\n";


                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }


                    byte [] xxxx = part3.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xxxx.length; i++){
                        Byte B = new Byte(xxxx[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] tt = new byte [tous_les_donnee.size()];
                    for(int i=0; i<tous_les_donnee.size(); i++){
                        Byte B = (Byte)tous_les_donnee.get(i);
                        tt[i] = B.byteValue();
                    }
                    mmOutStream.write(tt);

                    mmOutStream.close();
                    mysocket.close();
                    Log.e("END PRINT", "in normal");
                    //Toast.makeText(context, "in normal", Toast.LENGTH_LONG).show();
                    SharedPreferences otherSharedPreferences = this.context.getSharedPreferences(Utils.APP_OTHER_CONFIGURAION, MODE_PRIVATE);
                    long last_sms_id = otherSharedPreferences.getLong(Utils.LAST_SMS_ID,-1);
                    if (last_sms_id == sms.getId()){
                        SharedPreferences.Editor otherEditor = this.context.getSharedPreferences(Utils.APP_OTHER_CONFIGURAION, MODE_PRIVATE).edit();
                        otherEditor.remove(Utils.LAST_SMS_ID);
                        otherEditor.apply();
                    }

                    SMSDataSource smsDatatSource = new SMSDataSource(context);
                    smsDatatSource.open();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.COLUMN_IS_YET_PRINTED, 1);
                    smsDatatSource.updateSMS(sms.getId(), contentValues);
                    smsDatatSource.close();

                    /*

                    sms.setIs_online_saved(1);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MySQLiteHelper.COLUMN_IS_ONLINE_SAVED, 1);
                        smsDatatSource.updateSMS(sms.getId(), contentValues);
                     */

                    return Utils.OK;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                //Toast.makeText(context, "Failed to Connect To Printer", Toast.LENGTH_LONG).show();
                Log.e("IOException Bluetooth", e.getMessage());
            }

            return Utils.KO;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            try {
                mysocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dialog.getVisibility() == View.VISIBLE){
                dialog.setVisibility(View.GONE);
            }
            String res = (String) result;

        }

    }
}
