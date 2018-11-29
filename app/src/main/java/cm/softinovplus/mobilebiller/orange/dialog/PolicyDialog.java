package cm.softinovplus.mobilebiller.orange.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import cm.softinovplus.mobilebiller.orange.R;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 06/08/17.
 */

public class PolicyDialog extends DialogFragment {
    private static String mMessage;
    private static CheckBox termeCheckbox;

    public static PolicyDialog newInstance(CheckBox tcb, int num, String message) {
        PolicyDialog policyDialog = new PolicyDialog();
        mMessage = message;
        termeCheckbox = tcb;
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        policyDialog.setArguments(args);
        return policyDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View rootView = inflater.inflate(R.layout.mobilebiller_security_policy, null);
        builder.setView(rootView).
                setNegativeButton(R.string.refuser, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                        editor.remove(Utils.PRIVACY_POLICY_ACCEPTED);
                        editor.apply();
                        termeCheckbox.setChecked(false);
                        //PolicyDialog.this.getDialog().cancel();
                        //getActivity().finish();
                    }
                }).setPositiveButton(R.string.accepter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                        editor.putString(Utils.PRIVACY_POLICY_ACCEPTED, Utils.PRIVACY_POLICY_ACCEPTED);
                        editor.apply();
                        termeCheckbox.setChecked(true);
                    }
                });

        final WebView remote_privacy_policy = (WebView) rootView.findViewById(R.id.remote_privacy_policy);
        remote_privacy_policy.getSettings().setJavaScriptEnabled(true);
        remote_privacy_policy.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                ProgressBar load_policy = (ProgressBar) rootView.findViewById(R.id.load_policy);
                if (load_policy.getVisibility() == View.GONE){
                    load_policy.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                ProgressBar load_policy = (ProgressBar) rootView.findViewById(R.id.load_policy);
                if (load_policy.getVisibility() == View.VISIBLE){
                    load_policy.setVisibility(View.GONE);
                }
            }
        });
        /*remote_privacy_policy.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);

                if(progress == 100)
                    activity.setTitle(R.string.app_name);
            }
        });*/
        remote_privacy_policy.loadUrl(Utils.MOBILEBILLER_PRIVACY_POLICY);
        return builder.create();
    }

    @Override
    public void onResume(){
        super.onResume();
        Dialog dialogView = getDialog();
       // Toast.makeText(getActivity(), "Policy Dialog resumed", Toast.LENGTH_LONG).show();
       // TextView textView = (TextView)dialogView.findViewById(R.id.warningMessge);
        //textView.setText(mMessage);
    }
}
