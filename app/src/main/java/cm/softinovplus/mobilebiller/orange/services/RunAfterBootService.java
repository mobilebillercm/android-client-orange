package cm.softinovplus.mobilebiller.orange.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import cm.softinovplus.mobilebiller.orange.utils.Utils;

import static cm.softinovplus.mobilebiller.orange.utils.Utils.TAG_BOOT_EXECUTE_SERVICE;

/**
 * Created by nkalla on 16/09/18.
 */

public class RunAfterBootService extends Service {



    public RunAfterBootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onCreate() method.");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String message = "RunAfterBootService onStartCommand() method.";

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        Log.e(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onStartCommand() method.");

        SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
        editor.putString(Utils.INIT, Utils.INIT);
        editor.putString(Utils.BROADCAST_RECEIVER_REGISTERED, Utils.BROADCAST_RECEIVER_REGISTERED);
        editor.apply();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
