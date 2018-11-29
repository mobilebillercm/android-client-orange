package cm.softinovplus.mobilebiller.orange.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import cm.softinovplus.mobilebiller.orange.R;
import cm.softinovplus.mobilebiller.orange.db.SMSDataSource;
import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.TraiteImage;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

//import java.nio.ByteBuffer;

public class MyBluetoothAdapter extends BaseAdapter {
	private Context context;
	private final Set<BluetoothDevice> values;
	private String content_and_header ;
	private String corps ;
	private TextView debug_result;
    private SMS sms;

	public MyBluetoothAdapter(Context ctx, Set<BluetoothDevice> values, SMS sms) {
		this.context = ctx;
		this.values = values;
        this.sms = sms;
	}

	public int getCount() {
		return values.size();
	}

	public Object getItem(int position) {
		// return values[position];
		return values.toArray()[position];
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public View getView(int pos, View v, ViewGroup p) {
		if (v == null) {
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_layout_bluetooth, null);
		}

//		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
//		params.height = 150;
//		v.setLayoutParams(params);
		
		final BluetoothDevice bluetoothDevice = (BluetoothDevice) values
				.toArray()[pos];

		TextView device_name = (TextView) v.findViewById(R.id.device_name);
		device_name.setText(bluetoothDevice.getName());

		TextView device_mac_address = (TextView) v
				.findViewById(R.id.device_mac_address);
		device_mac_address.setText(bluetoothDevice.getAddress());
		debug_result = (TextView) v.findViewById(R.id.debug_result);
		// Button launchPrinting = (Button)v.findViewById(R.id.launchPrinting);
		/*v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyAsyncTask mat = new MyAsyncTask(bluetoothDevice, sms);
				mat.execute("");
			}
		});*/

		return v;
	}
}