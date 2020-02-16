package cm.softinovplus.mobilebiller.mtn.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

import cm.softinovplus.mobilebiller.mtn.BluetoothPrinterActivity;
import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.SMSsActivity;
import cm.softinovplus.mobilebiller.mtn.TicketToShareActivity;
import cm.softinovplus.mobilebiller.mtn.db.SMSDataSource;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

import static android.content.Context.MODE_PRIVATE;


public class MySMSAdapter extends BaseAdapter {
	private Context context;
	private final List<SMS> values;
    public MySMSAdapter(Context ctx, List<SMS> values) {
	   this.context = ctx;
	   this.values = values;
	}
    
    public int getCount() {
      return values.size();
    }

    public Object getItem(int position) {
       //return values[position];
    	return values.get(position);
    }
    
    public long getItemId(int position) {
      return position+1;
    }
    
     @SuppressLint("InflateParams") public View getView(int pos, View v, ViewGroup p) {
    	 final int pos_ = pos;
    	 //String  how_many_time = "";
        if (v == null) {
          LayoutInflater li=(LayoutInflater) context.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.item_layout, null);
        }
        final SMS sms = values.get(pos);
         if (sms.getIs_yet_printed() == 1){
             LinearLayout linearLayout = v.findViewById(R.id.smsview);
             linearLayout.setBackgroundColor(Color.rgb(240, 240, 240));
         }else{
             LinearLayout linearLayout = v.findViewById(R.id.smsview);
             linearLayout.setBackgroundColor(Color.rgb(255, 255, 255));
         }
        
        /*if(sms.getHas_been_printed() == 0){
        	v.setBackgroundColor(0x2200FF00);
        }else if(sms.getHas_been_printed() == 2 && sms.getHowmany_time()==0){
        	v.setBackgroundColor(0xFFFFFFFF);
        }else if(sms.getHas_been_printed() != 0 && sms.getHowmany_time()>1){
        	v.setBackgroundColor(0x22FF0000);
        }else if(sms.getHas_been_printed() == 1){
        	v.setBackgroundColor(0x22FF0000);
        	//how_many_time += "" + sms.getHowmany_time();
        	
        }*/
        
        TextView numero = (TextView)v.findViewById(R.id.numero);
        numero.setText((pos+1) + "-" /*+ sms.getHas_been_printed() + "-" + sms.getHowmany_time()*/);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        //String username = settings.getString(Utils.USERNAME, "");
        //String pdv = settings.getString(Utils.PDV, "");
        
        
        
       /* TextView pdv_value = (TextView)v.findViewById(R.id.pdv_value);
        pdv_value.setText(sms.getTenant());
        
        TextView username_value = (TextView)v.findViewById(R.id.username_value);
        username_value.setText(sms.getBelongs_to());*/
        
        TextView sent_date_result = (TextView)v.findViewById(R.id.sent_date_result);
        sent_date_result.setText(sms.getSms_date());
        
        TextView receive_date_result = (TextView)v.findViewById(R.id.receive_date_result);
        receive_date_result.setText(sms.getTransaction_date());
        
        TextView from_result = (TextView)v.findViewById(R.id.from_result);
        from_result.setText(sms.getSms_sender());

        final TextView message_text = (TextView)v.findViewById(R.id.message_text);
         int index = 100;

         if (sms.getSms_body().length() < 100 ){
             index = sms.getSms_body().length();
         }

        message_text.setText(sms.getSms_body().substring(0,index) + " ...");

         final ProgressBar progressBar = v.findViewById(R.id.print_loader);
        Button imprimer_btn = (Button)v.findViewById(R.id.imprimer_btn);
        imprimer_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

                String macaddress = sharedPreferences.getString(Utils.DEFAULT_MAC_ADDRESS, null);

                Log.e("macaddress", "macaddress: " + macaddress);
                if (macaddress != null){
                    Log.e("macaddress", "macaddress1: " + macaddress);
                    BluetoothAdapter G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (G_bluetoothAdapter == null) {
                        Intent intent = new Intent(context, BluetoothPrinterActivity.class);
                        Bundle bundle = new Bundle();
                        SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                        editor.putLong(Utils.LAST_SMS_ID, sms.getId());
                        editor.apply();
                        context.startActivity(intent);
                    } else {

                        if (!G_bluetoothAdapter.isEnabled()) {
                            G_bluetoothAdapter.enable();
                        }
                        Set<BluetoothDevice> G_devices = G_bluetoothAdapter.getBondedDevices();

                        Object []devices = G_devices.toArray();
                        BluetoothDevice choosenDevice = null;
                        for (int i=0; i<devices.length; i++){
                            BluetoothDevice device = (BluetoothDevice) devices[i];
                            if (device.getAddress().equals(macaddress)){
                                choosenDevice = device;
                                break;
                            }
                        }
                        if (choosenDevice != null){

                            SharedPreferences sharedPreferences_access_service = context.getSharedPreferences(Utils.APP_SERVICE_ACCESS, MODE_PRIVATE);

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
                                Toast.makeText(context.getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (!serviceFound || !serviceValidity){
                                Toast.makeText(context.getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                                return;
                            }

                            SMSDataSource dataSource = new SMSDataSource(context);
                            dataSource.open();
                            SMS datasourcesms = dataSource.getSMSById(sms.getId());
                            dataSource.close();

                            BluetoothPrinterActivity.MyAsyncTask mat = new BluetoothPrinterActivity.MyAsyncTask(context, choosenDevice, datasourcesms, progressBar);
                            mat.execute("");
                        }else {
                            Toast.makeText(context, "No bluetooth choosen", Toast.LENGTH_LONG).show();
                        }

                    }
                }else{
                    Intent intent = new Intent(context, BluetoothPrinterActivity.class);
                    Bundle bundle = new Bundle();
                    SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                    editor.putLong(Utils.LAST_SMS_ID, sms.getId());
                    editor.apply();
                    context.startActivity(intent);
                }
			}
		});
        
       final Button me_supprimer = v.findViewById(R.id.me_supprimer);
       me_supprimer.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
            ((SMSsActivity) context).prepareRemoveSms(sms);
		    }
	    });

         Button partger_btn = v.findViewById(R.id.partager_btn);
         partger_btn.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                Intent intent = new Intent(context, TicketToShareActivity.class);
                intent.putExtra(Utils.SMS, (new Gson()).toJson(sms));
                 context.startActivity(intent);
             }
         });

         return v;
      }
    
}
