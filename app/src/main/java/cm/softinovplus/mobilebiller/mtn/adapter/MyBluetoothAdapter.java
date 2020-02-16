package cm.softinovplus.mobilebiller.mtn.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Set;

import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;

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