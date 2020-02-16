package cm.softinovplus.mobilebiller.mtn.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cm.softinovplus.mobilebiller.mtn.services.RunAfterBootService;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

/**
 * Created by nkalla on 16/09/18.
 */

public class BootDeviceReceiver extends BroadcastReceiver {


    private Context ctxt;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.ctxt = context;
        String action = intent.getAction();

        String message = "BootDeviceReceiver onReceive, action is " + action;

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        Log.e(Utils.TAG_BOOT_BROADCAST_RECEIVER, action);

        if(Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
            //startServiceDirectly(context);

           // startServiceByAlarm(context);

            /*SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
            editor.putString(Utils.BROADCAST_RECEIVER_REGISTERED, "OK");
            JSONArray jsonArray =  new JSONArray();
            jsonArray.put("691179154");
            editor.putString(Utils.SMS_SENDERS, jsonArray.toString());
            editor.apply();*/
            registerSmsReceiver(context);
        }
    }

    private void registerSmsReceiver(Context context) {


        Log.e("REGISTER", "Start registration");

        /*SharedPreferences prefs = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
        String broadcast_receiver_initiated = prefs.getString(Utils.BROADCAST_RECEIVER_REGISTERED, null);
        String smssendersJsonString = prefs.getString(Utils.SMS_SENDERS, null);*/

        //if (broadcast_receiver_initiated.equals("OK")){
            try {
                JSONArray jsonArray = new JSONArray();
                List<String> smssenders =  new ArrayList<>();
                smssenders.add("691179154");
                for (int i=0; i<jsonArray.length(); i++){
                    smssenders.add(jsonArray.getString(i));
                }
                SmsBroadcastReceiver receiver = new SmsBroadcastReceiver(smssenders);
                IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
                context.getApplicationContext().registerReceiver(receiver, intentFilter);
                Log.e("REGISTER", "End registration");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        //}


    }

    /* Start RunAfterBootService service directly and invoke the service every 10 seconds. */
    private void startServiceDirectly(Context context)
    {
        try {
            while (true) {
                String message = "BootDeviceReceiver onReceive start service directly.";

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                Log.e(Utils.TAG_BOOT_BROADCAST_RECEIVER, message);

                // This intent is used to start background service. The same service will be invoked for each invoke in the loop.
                Intent startServiceIntent = new Intent(context, RunAfterBootService.class);
                context.startService(startServiceIntent);

                // Current thread will sleep one second.
                Thread.sleep(10000);
            }
        }catch(InterruptedException ex)
        {
            Log.e(Utils.TAG_BOOT_BROADCAST_RECEIVER, ex.getMessage(), ex);
        }
    }

    /* Create an repeat Alarm that will invoke the background service for each execution time.
     * The interval time can be specified by your self.  */
    private void startServiceByAlarm(Context context)
    {
        // Get alarm manager.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Create intent to invoke the background service.
        Intent intent = new Intent(context, RunAfterBootService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime = System.currentTimeMillis();
        long intervalTime = 60*1000;

        String message = "Start service use repeat alarm. ";

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        Log.e(Utils.TAG_BOOT_BROADCAST_RECEIVER, message);

        // Create repeat alarm.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }

}
