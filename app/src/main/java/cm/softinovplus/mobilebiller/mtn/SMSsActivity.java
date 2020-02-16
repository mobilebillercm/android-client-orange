package cm.softinovplus.mobilebiller.mtn;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import cm.softinovplus.mobilebiller.mtn.adapter.MySMSAdapter;
import cm.softinovplus.mobilebiller.mtn.db.SMSDataSource;
import cm.softinovplus.mobilebiller.mtn.dialog.DeleteSingleSMSDialog;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;

public class SMSsActivity extends AppCompatActivity {

    //private List<SMS> smses;
    private  static ListView listView;
    private MySMSAdapter adapter;
    public static AppCompatActivity thisActivity;
    private int mStackLevel;
    private ImageView delete_all_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smss);
        thisActivity = this;
        listView = (ListView) findViewById(R.id.sms_list);
        delete_all_sms = findViewById(R.id.delete_all_sms);

        SMSDataSource smsDatatSource = new SMSDataSource(getApplicationContext());
        smsDatatSource.open();
        List<SMS> smss = smsDatatSource.getAllSMS();
        smsDatatSource.close();
        adapter = new MySMSAdapter(this, smss);
        listView.setAdapter(adapter);

        delete_all_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog_for_single(null);
            }
        });

    }

    public  static void refreshList(){

        SMSDataSource smsDatatSource = new SMSDataSource(SMSsActivity.thisActivity);
        smsDatatSource.open();
        List<SMS> smss = smsDatatSource.getAllSMS();
        Log.e("sms size refreshList", "" + smss.size());
        MySMSAdapter mySMSAdapter = new MySMSAdapter(SMSsActivity.thisActivity, smss);
        listView.setAdapter(mySMSAdapter);
        mySMSAdapter.notifyDataSetChanged();
        Log.e("REFRESHED", "Refreshed....");
        smsDatatSource.close();

    }

    public void prepareRemoveSms(SMS smsToDelete){
        showDialog_for_single(smsToDelete);
    }

    public void showDialog_for_single(SMS smsToDelete) {
        mStackLevel++;
        //SMS sms = values.get(index_to_remove);
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DeleteSingleSMSDialog newFragment = DeleteSingleSMSDialog.newInstance(mStackLevel, smsToDelete);
        //newFragment.
        //if(values != null && values.size() > 0)
        newFragment.show(ft, "dialog");
    }
}
