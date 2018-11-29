package cm.softinovplus.mobilebiller.orange.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cm.softinovplus.mobilebiller.orange.Authenticated;
import cm.softinovplus.mobilebiller.orange.DefaulPrinterConfigActivity;
import cm.softinovplus.mobilebiller.orange.R;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

public class DefaultBluetoothSettingDialog extends DialogFragment {
    private DefaultBluetoothSettingDialog this_delete_dialog;
    private static DefaulPrinterConfigActivity.SetDefaultPrinter defaultPrinter;
    private static  String name;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static DefaultBluetoothSettingDialog newInstance(DefaulPrinterConfigActivity.SetDefaultPrinter dp, int num) {
    	DefaultBluetoothSettingDialog f = new DefaultBluetoothSettingDialog();

        defaultPrinter = dp;
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this_delete_dialog = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	getDialog().setTitle("Confirmation de L'imprimante par Defaut");
        final View fragment_dialog = inflater.inflate(R.layout.default_bluetooth_setting_dialog, container, false);
        Button button_cancel = (Button)fragment_dialog.findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				this_delete_dialog.dismiss();
			}
		});
        TextView default_bluetooth_setiing_warning = fragment_dialog.findViewById(R.id.default_bluetooth_setiing_warning);

        default_bluetooth_setiing_warning.setText("Vous allez choisir l'imprimante <<" + defaultPrinter.getNom() + ">> Comme celui par defaut.");

        Button button_yes = (Button)fragment_dialog.findViewById(R.id.confirm);
        button_yes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                this_delete_dialog.dismiss();
                defaultPrinter.setDefaultPrinter();
            }
        });
        return fragment_dialog;
    }
}

