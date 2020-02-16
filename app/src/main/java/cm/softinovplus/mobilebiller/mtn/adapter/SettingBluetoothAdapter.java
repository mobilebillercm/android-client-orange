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

public class SettingBluetoothAdapter extends BaseAdapter {
	private Context context;
	private final Set<BluetoothDevice> values;
	public SettingBluetoothAdapter(Context ctx, Set<BluetoothDevice> values) {
		this.context = ctx;
		this.values = values;
	}

	public int getCount() {
		return values.size();
	}

	public Object getItem(int position) {
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
		
		BluetoothDevice bluetoothDevice = (BluetoothDevice) values.toArray()[pos];

		TextView device_name =  v.findViewById(R.id.device_name);
		device_name.setText(bluetoothDevice.getName());

		TextView device_mac_address =  v.findViewById(R.id.device_mac_address);
		device_mac_address.setText(bluetoothDevice.getAddress());

		return v;
	}
}