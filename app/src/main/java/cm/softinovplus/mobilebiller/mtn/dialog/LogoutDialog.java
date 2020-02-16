package cm.softinovplus.mobilebiller.mtn.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import cm.softinovplus.mobilebiller.mtn.Authenticated;
import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

public class LogoutDialog extends DialogFragment {
    private LogoutDialog this_delete_dialog;
    private static Authenticated.DoLogout doLogout;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static LogoutDialog newInstance(Authenticated.DoLogout dl, int num) {
    	LogoutDialog f = new LogoutDialog();

        doLogout = dl;
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
    	getDialog().setTitle("Confirmation de Deconnexion");
        final View fragment_dialog = inflater.inflate(R.layout.logout_dialog, container, false);
        Button button_cancel = (Button)fragment_dialog.findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				this_delete_dialog.dismiss();
			}
		});
       // ((TextView)tv).setText("Dialog #" + mNum + ": using style "
               // + getNameForNum(mNum));

        // Watch for button clicks.
        Button button_yes = (Button)fragment_dialog.findViewById(R.id.confirm);
        button_yes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                this_delete_dialog.dismiss();
                SharedPreferences authPreferences = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, Context.MODE_PRIVATE);
                doLogout.execute(Utils.HOST_IDENTITY_AND_ACCESS + "api/lougout-user/" + authPreferences.getString(Utils.EMAIL,"") + "/" + authPreferences.getString(Utils.TENANT_ID,""));

            }
        });
        return fragment_dialog;
    }
}

