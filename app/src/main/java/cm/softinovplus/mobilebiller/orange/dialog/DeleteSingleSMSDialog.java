package cm.softinovplus.mobilebiller.orange.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cm.softinovplus.mobilebiller.orange.R;
import cm.softinovplus.mobilebiller.orange.SMSsActivity;
import cm.softinovplus.mobilebiller.orange.db.SMSDataSource;
import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

public class DeleteSingleSMSDialog extends DialogFragment {
    private DeleteSingleSMSDialog this_delete_dialog;
    private static SMS mSms;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static DeleteSingleSMSDialog newInstance(int num, SMS sms) {
    	mSms = sms;

    	DeleteSingleSMSDialog f = new DeleteSingleSMSDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mNum = getArguments().getInt("num");
        this_delete_dialog = this;
        
        // Pick a style based on the num.
        /*int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);*/
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	getDialog().setTitle("Confirmation");
        final View fragment_dialog = inflater.inflate(R.layout.delete_sms_dialog, container, false);
        Button button_cancel = (Button)fragment_dialog.findViewById(R.id.cancel_single);
        button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				this_delete_dialog.dismiss();
			}
		});
       // ((TextView)tv).setText("Dialog #" + mNum + ": using style "
               // + getNameForNum(mNum));

        // Watch for button clicks.
        
        final EditText edit_pwd_delete_single =
        		(EditText)fragment_dialog.findViewById(R.id.edit_pwd_delete_single);
        Button button_yes = (Button)fragment_dialog.findViewById(R.id.confirm_delete_single);
        button_yes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	SharedPreferences settings = getContext().getSharedPreferences(Utils.APP_AUTHENTICATION, Context.MODE_PRIVATE);
        		String p = settings.getString(Utils.PASSWORD, "");
        		String edit_pwd_delete_single_val  = edit_pwd_delete_single.getText().toString();
        		
            	if(p.equals(edit_pwd_delete_single_val)){
                    if (mSms != null){
                        this_delete_dialog.dismiss();
                        SMSDataSource data_source = new SMSDataSource(getContext());
                        data_source.open();
                        data_source.deleteSMS(mSms);
                        data_source.close();
                        SMSsActivity smsActivity = (SMSsActivity) getActivity();
                        smsActivity.refreshList();
                    }else {
                        this_delete_dialog.dismiss();
                        SMSDataSource data_source = new SMSDataSource(getContext());
                        data_source.open();
                        data_source.deleteAllSMS();
                        data_source.close();
                        SMSsActivity smsActivity = (SMSsActivity) getActivity();
                        smsActivity.refreshList();
                    }


            	}else{
            		TextView tv = (TextView)fragment_dialog.findViewById(R.id.result_error_single);
            		tv.setText("Mot de passe invalide");
            		tv.setTextColor(Color.RED);
            	}
            	
                // When button is clicked, call up to owning activity.
                //((FragmentDialog)getActivity()).showDialog();
            }
        });
        return fragment_dialog;
    }
}

