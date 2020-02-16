package cm.softinovplus.mobilebiller.orange;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import cm.softinovplus.mobilebiller.orange.adapter.SettingBluetoothAdapter;
import cm.softinovplus.mobilebiller.orange.db.SMSDataSource;
import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.Utils;


public class PrintNewSMS extends AppCompatActivity {

	public static AppCompatActivity printSingleSMS_Self ;
	private TextView sms_sender, soustitre, le, raison, beneficiaire, compte_bene, montant, frais, fair_par;
	private Button yes_print, no_print, back;
	private SMS sms;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean is_connected = true;
		SharedPreferences prefs = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

		String pref_email =  prefs.getString(Utils.EMAIL,null);
		String pref_passowrd = prefs.getString(Utils.PASSWORD, null);
		String pref_token_type = prefs.getString(Utils.TOKEN_TYPE, null);
		String pref_access_token = prefs.getString(Utils.ACCESS_TOKEN, null);
		long pref_expires_in = prefs.getLong(Utils.EXPIRES_IN, -1);
		String pref_refresh_token = prefs.getString(Utils.REFRESH_TOKEN,null);
		is_connected = (pref_email != null && pref_passowrd != null && pref_token_type != null
				&& pref_access_token != null && !(pref_expires_in == -1) && pref_refresh_token != null);

		if(!is_connected){
			SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
			SharedPreferences.Editor otherEditor = getSharedPreferences(Utils.APP_OTHER_CONFIGURAION, MODE_PRIVATE).edit();
			otherEditor.putLong(Utils.LAST_SMS_ID, sharedPreferences.getLong(Utils.LAST_SMS_ID, -1));
			otherEditor.apply();
			Intent intent = new Intent(this, Welcome.class);
			long smsid = getIntent().getExtras().getLong(Utils.SMS_ID, -1);
			intent.putExtra(Utils.SMS_ID, smsid);
			startActivity(intent);
            finish();
			return;
		}

		setContentView(R.layout.new_sms);

		printSingleSMS_Self = this;

		sms_sender = findViewById(R.id.sms_sender);
		soustitre = findViewById(R.id.soustitre);
		le = findViewById(R.id.le);
		raison = findViewById(R.id.raison);
		beneficiaire = findViewById(R.id.beneficiaire);
		compte_bene = findViewById(R.id.compte_bene);
		fair_par =  findViewById(R.id.fair_par);
		frais = findViewById(R.id.frais);
		montant = findViewById(R.id.montant);
		yes_print = findViewById(R.id.yes_print);
		no_print = findViewById(R.id.no_print);
		back = findViewById(R.id.back);
		soustitre.setText(prefs.getString(Utils.EMAIL, "Erro@Error"));
		long smsid = getIntent().getExtras().getLong(Utils.SMS_ID, -1);

		Log.e("SMSID_SMSID", "" + smsid);

		SMSDataSource dataSource = new SMSDataSource(this);
		dataSource.open();
		sms = dataSource.getSMSById(smsid);
		dataSource.close();
		if(sms != null){
			sms_sender.setText(sms.getSms_sender());
			le.setText("Le: " + sms.getTransaction_date());
			raison.setText("Raison: " + sms.getTransaction_type());
			beneficiaire.setText("Beneficiaire: " + sms.getTransaction_beneficiary_name());
			compte_bene.setText("Compte Beneficiaire: " + sms.getTransaction_beneficiary_account_number());
			fair_par.setText("Par: " + sms.getTransaction_made_by());
			frais.setText("Frais: " + sms.getTransaction_fees());
			montant.setText("Montant: " + sms.getTransaction_amount());
		}

		Log.e("SMS IN PRINTNEWSMS", sms.toString());

		no_print.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		yes_print.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sms != null){

					SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

					String macaddress = sharedPreferences.getString(Utils.DEFAULT_MAC_ADDRESS, null);

					Log.e("macaddress", "macaddress: " + macaddress);
					if (macaddress != null){
						Log.e("macaddress", "macaddress1: " + macaddress);
						BluetoothAdapter G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
						if (G_bluetoothAdapter == null) {
							Toast.makeText(getApplicationContext(), "Pas de Bluetooth", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(PrintNewSMS.this, BluetoothPrinterActivity.class);
							SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
							editor.putLong(Utils.LAST_SMS_ID, sms.getId());
							editor.apply();
							startActivity(intent);
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

								SMSDataSource dataSource = new SMSDataSource(getApplicationContext());
								dataSource.open();
								SMS datasourcesms = dataSource.getSMSById(sms.getId());
								dataSource.close();

								ProgressBar progressBar = findViewById(R.id.print_loader);
								BluetoothPrinterActivity.MyAsyncTask mat = new BluetoothPrinterActivity.MyAsyncTask(printSingleSMS_Self, choosenDevice, datasourcesms, progressBar);
								mat.execute("");
							}else {
								Toast.makeText(getApplicationContext(), "No bluetooth choosen", Toast.LENGTH_LONG).show();
							}
						}
					}else{
						Log.e("macaddress", "macaddress3: " + macaddress);
						Toast.makeText(getApplicationContext(), "Pas de Bluetooth",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(PrintNewSMS.this, BluetoothPrinterActivity.class);
						SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
						editor.putLong(Utils.LAST_SMS_ID, sms.getId());
						editor.apply();
						startActivity(intent);
					}

				}
			}
		});

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}
	
	@SuppressLint("SimpleDateFormat") public static String makeDate(Long when){
		Date date = new Date(when);
		return (new SimpleDateFormat("dd/MM/yy HH:mm:ss")).format(date);
	}
	
	@SuppressLint("SimpleDateFormat") public static long unMakeDate(String date){

		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date d = null;
		try {
			d = (Date) f.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	@Override
	public void onBackPressed() {
		//SharedPreferences.Editor otherEditor = getSharedPreferences(Utils.APP_OTHER_CONFIGURAION, MODE_PRIVATE).edit();
		//otherEditor.remove(Utils.LAST_SMS_ID);
		//otherEditor.apply();

        getIntent().getExtras().remove(Utils.SMS_ID);

		super.onBackPressed();
	}
}
