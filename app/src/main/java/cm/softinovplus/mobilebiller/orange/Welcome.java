package cm.softinovplus.mobilebiller.orange;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import cm.softinovplus.mobilebiller.orange.fragments.LoginFragment;
import cm.softinovplus.mobilebiller.orange.receivers.NetworkListener;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Welcome extends AppCompatActivity {

    private static FragmentManager fragmentManager;
    private NetworkListener networkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply activity transition
        // inside your activity (if you did not enable transitions in your theme)
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

// set an exit transition
        //getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_welcome);

        overridePendingTransition(R.anim.left_out, R.anim.right_out);



        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new LoginFragment(), Utils.LoginFragment).commit();
        }

        networkListener = new NetworkListener();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkListener, filter);
        //scheduleJob();
        hideKeyboard();

        initializeMap();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onDestroy() {
        if (networkListener != null) {
            Log.e("onDestroy" , networkListener.getClass().getName());
            unregisterReceiver(networkListener);
            networkListener = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
       // stopService(new Intent(this, NetworkSchedulerService.class));

        if (getIntent().getExtras() != null){
            getIntent().getExtras().remove(Utils.SMS_ID);
        }

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        /*Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);*/
    }


    public void initializeMap(){
        Log.e("INITIALIZE", "initializing initializing initializinginitializing");
        if (!checkPermission()) {
            requestPermission();
        } else {

            SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
            editor.putString(Utils.INIT, Utils.INIT);
            editor.apply();

            SharedPreferences prefs = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
            String broadcast_receiver_initiated = prefs.getString(Utils.BROADCAST_RECEIVER_REGISTERED, null);

            //Toast.makeText(getApplicationContext(), broadcast_receiver_initiated, Toast.LENGTH_LONG).show();

        }
    }

    // Replace Login Fragment with animation
    public void replaceLoginFragment() {
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.left_enter, R.anim.right_out).
                replace(R.id.frameContainer, new LoginFragment(), Utils.LoginFragment).commit();
    }

    public boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECEIVE_SMS, WRITE_EXTERNAL_STORAGE}, Utils.RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestcode, String permissions[], int[] grantResults){
        switch (requestcode){
            case Utils.RequestPermissionCode :{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS ) == PackageManager.PERMISSION_GRANTED){
                        //initializeMap();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.you_mustgrant_permission), Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
            default:{

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        Fragment SignUpFragment = fragmentManager.findFragmentByTag(Utils.SignUpFragment);
        Fragment ForgotPasswordFragment = fragmentManager.findFragmentByTag(Utils.ForgotPasswordFragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUpFragment != null)
            replaceLoginFragment();
        else if (ForgotPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static class NetworkSchedulerService extends JobService implements  NetworkListener.ConnectivityReceiverListener {

        private  final String TAG = NetworkSchedulerService.class.getSimpleName();

        private NetworkListener mConnectivityReceiver;

        public NetworkSchedulerService(){}

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e(TAG, "Service created");
            //mConnectivityReceiver = new NetworkListener(this);
        }



        /**
         * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
         * activity and this service can communicate back and forth. See "setUiCallback()"
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(TAG, "onStartCommand");
            return START_NOT_STICKY;
        }


        @Override
        public boolean onStartJob(JobParameters params) {
            Log.e(TAG, "onStartJob " + mConnectivityReceiver);
            registerReceiver(mConnectivityReceiver, new IntentFilter(Utils.CONNECTIVITY_ACTION));
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            Log.e(TAG, "onStopJob");
            unregisterReceiver(mConnectivityReceiver);
            return true;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Log.e("NETWORK STATE", message);
        }
    }


}
