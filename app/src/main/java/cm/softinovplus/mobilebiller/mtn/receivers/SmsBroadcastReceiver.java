package cm.softinovplus.mobilebiller.mtn.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.mtn.Authenticated;
import cm.softinovplus.mobilebiller.mtn.PrintNewSMS;
import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.SMSsActivity;
import cm.softinovplus.mobilebiller.mtn.Welcome;
import cm.softinovplus.mobilebiller.mtn.db.MySQLiteHelper;
import cm.softinovplus.mobilebiller.mtn.db.SMSDataSource;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nkalla on 16/09/18.
 */

/*
    Messagesw

    1- Vous avez envoye 10000 FCFA a LEONARD ZEBAZE (237674769537) le 2018-10-05 09:36:36. Message de l'expediteur: . Votre nouveau solde est de 8225 FCFA. Transaction Id: 402965086
    2- Le retrait de 1000FCFA initie pour FLORE REGINE NGAH BINDZI le 2018-10-05 08:18:33 a ete approuve le 2018-10-05 08:18:33.Nouveau solde: 43225FCFA
    4- Votre paiement de 100 FCFA pour MTNC AIRTIME a ete effectue le 2018-10-04 15:38:37. Votre nouveau solde: 50 FCFA. Frais 0 FCFA, Message: -. Transaction Id: 402479934.
    5- Recharge de 100 FCFA a 237652286653 le 2018-10-05 10:56:08 effectue avec succes. Commission: 5Fcfa. Nouveau solde: 8,125FCFA

    3- Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.
    7- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE de NYLON - 2nd cycle Date 06/09/2018 11:24:47 Montant 15000 XAF Pay ID 12367. Votre Pay ID est votre preuve de paiement.
    6- Paiement reussi de FONDJA MELANIE LYCEE TECHNIQUE DE NYLON - PROBATOIRE Date 05/09/2018 12:35:40 Montant 13000 XAF Pay ID 10505. Votre Pay ID est votre preuve de paiement.
    8- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE DE NYLON-BACCALAUREAT Date 06/09/2018 11:30:00 Montant 18500 XAF Pay ID 12398. Votre Pay ID est votre preuve de paiement
    9-
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private String sms_sender = "";
    private long dateMilli;
    private String sms_sent_date="", received_at;
    private  String sms_body, sms_receiver;
    private String transaction_type, transaction_state, transaction_amount = "0", transaction_beneficiary_name, transaction_beneficiary_account_number, transaction_date, transaction_id,
            transaction_reference, transaction_fees = "0", transaction_balance = "0", transaction_currency, transaction_made_by, pseudoSubject, serviceCenterAddress;

    private static List<String> serviceProviderNumber;
    //private final String serviceProviderSmsCondition;

    public SmsBroadcastReceiver(){

    }

    public SmsBroadcastReceiver(List<String> serviceProviderNumber/*, String serviceProviderSmsCondition*/) {
        this.serviceProviderNumber = serviceProviderNumber;
        //this.serviceProviderSmsCondition = serviceProviderSmsCondition;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    if (smsMessage.getOriginatingAddress() != null){
                        this.sms_sender = smsMessage.getOriginatingAddress();
                        //smsMessage.get
                    }
                    dateMilli = smsMessage.getTimestampMillis();
                    this.sms_sent_date = Utils.makeDateDate(dateMilli);
                    smsBody += smsMessage.getMessageBody();
                    this.pseudoSubject = smsMessage.getPseudoSubject();
                    this.serviceCenterAddress = smsMessage.getServiceCenterAddress();
                }
                this.sms_body = smsBody;


            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                    smsBody += "";
                    if (messages[0].getOriginatingAddress() != null){
                        this.sms_sender = messages[0].getOriginatingAddress();
                    }
                    dateMilli = messages[0].getTimestampMillis();
                    this.sms_sent_date = Utils.makeDateDate(dateMilli);
                    this.sms_body = smsBody;
                    this.pseudoSubject =  messages[0].getPseudoSubject();
                    this.serviceCenterAddress = messages[0].getServiceCenterAddress();
                }
            }

            //this.sms_body = "Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) le 2018-09-26 09:30:45. FRAIS 250 FCFA. Transaction Id:
            // 395587665 ; Reference: 123456789. Nouveau solde est: 33000 FCFA.";

            Log.e("service center address", this.serviceCenterAddress);
            Toast.makeText(context,this.serviceCenterAddress, Toast.LENGTH_LONG).show();


            boolean booleen = false;
            //JSONArray jsonArray = null;//Utils.keysPatterns();
            try {

                Log.e("TEST111111111111", "Before APP_CONFIGURAION " + context.getPackageCodePath() + "    " + context.getPackageName() + "    " + context.getPackageResourcePath());
                SharedPreferences mysp = context.getApplicationContext().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
                Log.e("TEST22222222222", "After APP_CONFIGURAION");
                JSONObject jsonObject0 = new JSONObject(mysp.getString(Utils.REMOTE_REGULAREXPRESSION,""));

                JSONObject mtncameroon = jsonObject0.getJSONObject(Utils.MTNCAMEROON);
                JSONArray frenchversion_mtncameroon = mtncameroon.getJSONArray(Utils.FRENCHVERSION);

                //jsonArray_mtncameroon = new JSONArray(mysp.getString(Utils.REMOTE_REGULAREXPRESSION,""));
                Log.e("TEST33333333333333", "After APP_CONFIGURAION");

                for (int i = 0; i<frenchversion_mtncameroon.length(); i++){
                    try {
                        JSONObject jsonObject = (JSONObject)frenchversion_mtncameroon.get(i);
                        Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + this.sms_body);
                        Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                        if (matcher1.find()){
                            Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                            Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                            if (matcher.find()){
                                Log.e("FOUND", "YES YES YES YES");
                                if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Transfert de fond")){
                                    Log.e("1", matcher.group(2));
                                    transaction_type = "Transfert de fond";//matcher.group(2);
                                    transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                    transaction_currency = matcher.group(3);
                                    transaction_state = matcher.group(4);
                                    transaction_beneficiary_name = matcher.group(5) + matcher.group(6);
                                    transaction_beneficiary_account_number = matcher.group(8);
                                    transaction_date = matcher.group(9)+"-" + matcher.group(10) + "-" + matcher.group(11) + " " + matcher.group(12) + ":" + matcher.group(13) + ":" + matcher.group(14);
                                    transaction_fees = matcher.group(15)==null?"0":matcher.group(15);
                                    transaction_id = matcher.group(16);
                                    transaction_reference = matcher.group(18);
                                    transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
                                    SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                    transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 1");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais de Scolarite")){
                                    transaction_type = "Paiement de Frais de Scolarite";//matcher.group(2);
                                    transaction_amount = matcher.group(13) == null ? "0":matcher.group(13);
                                    transaction_currency = matcher.group(14);
                                    transaction_state = "Succes";
                                    transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                    transaction_beneficiary_account_number = "NA";
                                    transaction_date = matcher.group(12);
                                    transaction_fees = "0";
                                    transaction_id = matcher.group(15);
                                    transaction_reference = matcher.group(15);
                                    transaction_balance = "0";
                                    String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                    transaction_made_by = matcher.group(1) + additional;
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 2");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais d'examen")){
                                    transaction_type = "Paiement de Frais d'examen";//matcher.group(2);
                                    transaction_amount = (matcher.group(15) == null)? "0":matcher.group(15);
                                    transaction_currency = matcher.group(16);
                                    transaction_state = "Succes";
                                    transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + " " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                    transaction_beneficiary_account_number = "NA";
                                    transaction_date = matcher.group(14);
                                    transaction_fees = "0";
                                    transaction_id = matcher.group(17);
                                    transaction_reference = matcher.group(17);
                                    transaction_balance = "0";
                                    String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                    transaction_made_by = matcher.group(1) + additional;
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 3");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Retrait")){
                                    transaction_type = "Retrait";//matcher.group(2);
                                    transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                    transaction_currency = matcher.group(3);
                                    transaction_state = "Succes";
                                    transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5);
                                    transaction_beneficiary_account_number = "NA";
                                    transaction_date = matcher.group(7);
                                    transaction_fees = "0";
                                    transaction_id = "NA";
                                    transaction_reference = "NA";
                                    transaction_balance = matcher.group(9);;
                                    String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                    transaction_made_by = matcher.group(4) + " " +  matcher.group(5);
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 4");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement")){
                                    transaction_type = "Paiement";//matcher.group(2);
                                    transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                    transaction_currency = matcher.group(2);
                                    transaction_state = "Succes";
                                    transaction_beneficiary_name = "NA";
                                    transaction_beneficiary_account_number = "NA";
                                    transaction_date = matcher.group(6);
                                    transaction_fees =  matcher.group(9);
                                    transaction_id =  matcher.group(11);
                                    transaction_reference = matcher.group(3) + matcher.group(4);
                                    transaction_balance = matcher.group(7);
                                    SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                    transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 5");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Recharge de credit")){
                                    transaction_type = "Recharge de credit";//matcher.group(2);
                                    transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                    transaction_currency = matcher.group(2);
                                    transaction_state = matcher.group(5);
                                    transaction_beneficiary_name = matcher.group(3);
                                    transaction_beneficiary_account_number = matcher.group(3);
                                    transaction_date = matcher.group(4);
                                    transaction_fees =  matcher.group(6);
                                    transaction_id =  "NA";
                                    transaction_reference = "NA";
                                    transaction_balance = matcher.group(8);
                                    SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                    transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 6");
                                }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Envoi de fond")){
                                    transaction_type = "Envoi de fond";//matcher.group(2);
                                    transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                    transaction_currency = matcher.group(2);
                                    transaction_state = "Succes";
                                    transaction_beneficiary_name = matcher.group(3) + matcher.group(4);
                                    transaction_beneficiary_account_number = matcher.group(6);
                                    transaction_date = matcher.group(7);
                                    transaction_fees =  "0";
                                    transaction_id =  matcher.group(11);
                                    transaction_reference = "NA";
                                    //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                    transaction_balance = matcher.group(9);
                                    SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                    transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                    booleen = true;
                                    Log.e("FOUND", "YES YES YES YES 7");
                                }else{
                                    transaction_type = "NA";//matcher.group(2);
                                    transaction_amount = "0";
                                    transaction_currency = "NA";
                                    transaction_state = "NA";
                                    transaction_beneficiary_name = "NA";
                                    transaction_beneficiary_account_number = "NA";
                                    transaction_date = "NA";
                                    transaction_fees =  "0";
                                    transaction_id =  "NA";
                                    transaction_reference = "NA";
                                    transaction_balance = "0";
                                    transaction_made_by = "NA";
                                    booleen = false;
                                    Log.e("FOUND", "YES YES YES YES 8");
                                }
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!booleen){
                    JSONArray englishversion_mtncameroon = mtncameroon.getJSONArray(Utils.ENGLISHVERSION);

                    for (int i = 0; i<englishversion_mtncameroon.length(); i++){
                        try {
                            JSONObject jsonObject = (JSONObject)englishversion_mtncameroon.get(i);
                            Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + this.sms_body);
                            Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                            if (matcher1.find()){
                                Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                                Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                                if (matcher.find()){
                                    Log.e("FOUND", "YES YES YES YES");
                                    if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Transfert de fond")){
                                        Log.e("1", matcher.group(2));
                                        transaction_type = "Transfert de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                        transaction_currency = matcher.group(3);
                                        transaction_state = matcher.group(4);
                                        transaction_beneficiary_name = matcher.group(5) + matcher.group(6);
                                        transaction_beneficiary_account_number = matcher.group(8);
                                        transaction_date = matcher.group(9)+"-" + matcher.group(10) + "-" + matcher.group(11) + " " + matcher.group(12) + ":" + matcher.group(13) + ":" + matcher.group(14);
                                        transaction_fees = matcher.group(15)==null?"0":matcher.group(15);
                                        transaction_id = matcher.group(16);
                                        transaction_reference = matcher.group(18);
                                        transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 1");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais de Scolarite")){
                                        transaction_type = "Paiement de Frais de Scolarite";//matcher.group(2);
                                        transaction_amount = matcher.group(13) == null ? "0":matcher.group(13);
                                        transaction_currency = matcher.group(14);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(12);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(15);
                                        transaction_reference = matcher.group(15);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 2");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais d'examen")){
                                        transaction_type = "Paiement de Frais d'examen";//matcher.group(2);
                                        transaction_amount = (matcher.group(15) == null)? "0":matcher.group(15);
                                        transaction_currency = matcher.group(16);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + " " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(14);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(17);
                                        transaction_reference = matcher.group(17);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 3");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Retrait")){
                                        transaction_type = "Retrait";//matcher.group(2);
                                        transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                        transaction_currency = matcher.group(3);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(7);
                                        transaction_fees = "0";
                                        transaction_id = "NA";
                                        transaction_reference = "NA";
                                        transaction_balance = matcher.group(9);;
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(4) + " " +  matcher.group(5);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 4");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement")){
                                        transaction_type = "Paiement";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(6);
                                        transaction_fees =  matcher.group(9);
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = matcher.group(3) + matcher.group(4);
                                        transaction_balance = matcher.group(7);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 5");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Recharge de credit")){
                                        transaction_type = "Recharge de credit";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = matcher.group(5);
                                        transaction_beneficiary_name = matcher.group(3);
                                        transaction_beneficiary_account_number = matcher.group(3);
                                        transaction_date = matcher.group(4);
                                        transaction_fees =  matcher.group(6);
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(8);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 6");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Envoi de fond")){
                                        transaction_type = "Envoi de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(3) + matcher.group(4);
                                        transaction_beneficiary_account_number = matcher.group(6);
                                        transaction_date = matcher.group(7);
                                        transaction_fees =  "0";
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(9);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 7");
                                    }else{
                                        transaction_type = "NA";//matcher.group(2);
                                        transaction_amount = "0";
                                        transaction_currency = "NA";
                                        transaction_state = "NA";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = "NA";
                                        transaction_fees =  "0";
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        transaction_balance = "0";
                                        transaction_made_by = "NA";
                                        booleen = false;
                                        Log.e("FOUND", "YES YES YES YES 8");
                                    }
                                }
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!booleen){
                    JSONObject orangecameroon = jsonObject0.getJSONObject(Utils.ORANGECAMEROON);
                    JSONArray frenchversion_orangecameroon = orangecameroon.getJSONArray(Utils.FRENCHVERSION);

                    for (int i = 0; i<frenchversion_orangecameroon.length(); i++){
                        try {
                            JSONObject jsonObject = (JSONObject)frenchversion_orangecameroon.get(i);
                            Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + this.sms_body);
                            Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                            if (matcher1.find()){
                                Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                                Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                                if (matcher.find()){
                                    Log.e("FOUND", "YES YES YES YES");
                                    if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Transfert de fond")){
                                        Log.e("1", matcher.group(2));

                                        transaction_type = "Transfert de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                        transaction_currency = matcher.group(3);
                                        transaction_state = matcher.group(4);
                                        transaction_beneficiary_name = matcher.group(5) + matcher.group(6);
                                        transaction_beneficiary_account_number = matcher.group(8);
                                        transaction_date = matcher.group(9)+"-" + matcher.group(10) + "-" + matcher.group(11) + " " + matcher.group(12) + ":" + matcher.group(13) + ":" + matcher.group(14);
                                        transaction_fees = matcher.group(15)==null?"0":matcher.group(15);
                                        transaction_id = matcher.group(16);
                                        transaction_reference = matcher.group(18);
                                        transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 1");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais de Scolarite")){
                                        transaction_type = "Paiement de Frais de Scolarite";//matcher.group(2);
                                        transaction_amount = matcher.group(13) == null ? "0":matcher.group(13);
                                        transaction_currency = matcher.group(14);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(12);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(15);
                                        transaction_reference = matcher.group(15);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 2");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais d'examen")){
                                        transaction_type = "Paiement de Frais d'examen";//matcher.group(2);
                                        transaction_amount = (matcher.group(15) == null)? "0":matcher.group(15);
                                        transaction_currency = matcher.group(16);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + " " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(14);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(17);
                                        transaction_reference = matcher.group(17);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 3");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Retrait")){
                                        transaction_type = "Retrait";//matcher.group(2);
                                        transaction_amount = matcher.group(12) == null ? "0":matcher.group(12);
                                        transaction_currency = matcher.group(14);
                                        transaction_state = matcher.group(2);
                                        String additionnel = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_beneficiary_name = matcher.group(8) + " " + additionnel;
                                        transaction_beneficiary_account_number = matcher.group(7);
                                        transaction_date = "" + Utils.makeDateDate(System.currentTimeMillis());
                                        transaction_fees = matcher.group(15) == null ? "0":matcher.group(15);
                                        transaction_id = matcher.group(11) == null ? "0":matcher.group(11);;
                                        transaction_reference = "NA";
                                        transaction_balance = matcher.group(24);;
                                        String additional = (matcher.group(5) == null)?"":matcher.group(5);
                                        transaction_made_by = matcher.group(4) + " " +  additional  + " (Tel: " + matcher.group(3) +")";
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 4");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Depot")){
                                        transaction_type = "Depot";//matcher.group(2);
                                        transaction_amount = matcher.group(14) == null ? "0":matcher.group(14);
                                        transaction_currency = matcher.group(16);
                                        transaction_state = matcher.group(7);
                                        String additionnel = (matcher.group(5) == null)?"":matcher.group(5);
                                        transaction_beneficiary_name = matcher.group(4) + " " + additionnel;
                                        transaction_beneficiary_account_number = matcher.group(3);
                                        transaction_date = "" + Utils.makeDateDate(System.currentTimeMillis());
                                        transaction_fees = matcher.group(17) == null ? "0":matcher.group(17);
                                        transaction_id = matcher.group(13) == null ? "0":matcher.group(13);;
                                        transaction_reference = "NA";
                                        transaction_balance = matcher.group(26);;
                                        String additional = (matcher.group(11) == null)?"":matcher.group(11);
                                        transaction_made_by = matcher.group(10) + " " +  additional  + " (Tel: " + matcher.group(9) +")";
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 4");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement")){
                                        transaction_type = "Paiement";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(6);
                                        transaction_fees =  matcher.group(9);
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = matcher.group(3) + matcher.group(4);
                                        transaction_balance = matcher.group(7);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 5");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Recharge de credit")){
                                        transaction_type = "Recharge de credit";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = matcher.group(5);
                                        transaction_beneficiary_name = matcher.group(3);
                                        transaction_beneficiary_account_number = matcher.group(3);
                                        transaction_date = matcher.group(4);
                                        transaction_fees =  matcher.group(6);
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(8);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 6");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Envoi de fond")){
                                        transaction_type = "Envoi de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(3) + matcher.group(4);
                                        transaction_beneficiary_account_number = matcher.group(6);
                                        transaction_date = matcher.group(7);
                                        transaction_fees =  "0";
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(9);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 7");
                                    }else{
                                        transaction_type = "NA";//matcher.group(2);
                                        transaction_amount = "0";
                                        transaction_currency = "NA";
                                        transaction_state = "NA";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = "NA";
                                        transaction_fees =  "0";
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        transaction_balance = "0";
                                        transaction_made_by = "NA";
                                        booleen = false;
                                        Log.e("FOUND", "YES YES YES YES 8");
                                    }
                                }
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (!booleen){
                    JSONObject orangecameroon = jsonObject0.getJSONObject(Utils.ORANGECAMEROON);
                    JSONArray englishversion_orangecameroon = orangecameroon.getJSONArray(Utils.ENGLISHVERSION);

                    for (int i = 0; i<englishversion_orangecameroon.length(); i++){
                        try {
                            JSONObject jsonObject = (JSONObject)englishversion_orangecameroon.get(i);
                            Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + this.sms_body);
                            Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                            if (matcher1.find()){
                                Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                                Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                                if (matcher.find()){
                                    Log.e("FOUND", "YES YES YES YES");
                                    if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Transfert de fond")){
                                        Log.e("1", matcher.group(2));
                                        transaction_type = "Transfert de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                        transaction_currency = matcher.group(3);
                                        transaction_state = matcher.group(4);
                                        transaction_beneficiary_name = matcher.group(5) + matcher.group(6);
                                        transaction_beneficiary_account_number = matcher.group(8);
                                        transaction_date = matcher.group(9)+"-" + matcher.group(10) + "-" + matcher.group(11) + " " + matcher.group(12) + ":" + matcher.group(13) + ":" + matcher.group(14);
                                        transaction_fees = matcher.group(15)==null?"0":matcher.group(15);
                                        transaction_id = matcher.group(16);
                                        transaction_reference = matcher.group(18);
                                        transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 1");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais de Scolarite")){
                                        transaction_type = "Paiement de Frais de Scolarite";//matcher.group(2);
                                        transaction_amount = matcher.group(13) == null ? "0":matcher.group(13);
                                        transaction_currency = matcher.group(14);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(12);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(15);
                                        transaction_reference = matcher.group(15);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 2");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais d'examen")){
                                        transaction_type = "Paiement de Frais d'examen";//matcher.group(2);
                                        transaction_amount = (matcher.group(15) == null)? "0":matcher.group(15);
                                        transaction_currency = matcher.group(16);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + " " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(14);
                                        transaction_fees = "0";
                                        transaction_id = matcher.group(17);
                                        transaction_reference = matcher.group(17);
                                        transaction_balance = "0";
                                        String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                        transaction_made_by = matcher.group(1) + additional;
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 3");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Retrait")){
                                        transaction_type = "Retrait";//matcher.group(2);
                                        transaction_amount = matcher.group(12) == null ? "0":matcher.group(12);
                                        transaction_currency = matcher.group(14);
                                        transaction_state = matcher.group(2);
                                        transaction_beneficiary_name = matcher.group(8);
                                        transaction_beneficiary_account_number = matcher.group(7);
                                        transaction_date = "" + Utils.makeDateDate(System.currentTimeMillis());
                                        transaction_fees = matcher.group(15) == null ? "0":matcher.group(15);
                                        transaction_id = matcher.group(11) == null ? "0":matcher.group(11);;
                                        transaction_reference = "NA";
                                        transaction_balance = matcher.group(24);;
                                        String additional = (matcher.group(5) == null)?"":matcher.group(5);
                                        transaction_made_by = matcher.group(4) + " " +  additional + " (Tel: " + matcher.group(3) +")";
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 4");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Depot")){
                                        transaction_type = "Depot";//matcher.group(2);
                                        transaction_amount = matcher.group(14) == null ? "0":matcher.group(14);
                                        transaction_currency = matcher.group(16);
                                        transaction_state = matcher.group(7);
                                        String additionnel = (matcher.group(5) == null)?"":matcher.group(5);
                                        transaction_beneficiary_name = matcher.group(4) + " " + additionnel;
                                        transaction_beneficiary_account_number = matcher.group(3);
                                        transaction_date = "" + Utils.makeDateDate(System.currentTimeMillis());
                                        transaction_fees = matcher.group(17) == null ? "0":matcher.group(17);
                                        transaction_id = matcher.group(13) == null ? "0":matcher.group(13);;
                                        transaction_reference = "NA";
                                        transaction_balance = matcher.group(26);;
                                        String additional = (matcher.group(11) == null)?"":matcher.group(11);
                                        transaction_made_by = matcher.group(10) + " " +  additional + " (Tel: " + matcher.group(9) +")";
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 4");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement")){
                                        transaction_type = "Paiement";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = matcher.group(6);
                                        transaction_fees =  matcher.group(9);
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = matcher.group(3) + matcher.group(4);
                                        transaction_balance = matcher.group(7);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 5");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Recharge de credit")){
                                        transaction_type = "Recharge de credit";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = matcher.group(5);
                                        transaction_beneficiary_name = matcher.group(3);
                                        transaction_beneficiary_account_number = matcher.group(3);
                                        transaction_date = matcher.group(4);
                                        transaction_fees =  matcher.group(6);
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(8);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 6");
                                    }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Envoi de fond")){
                                        transaction_type = "Envoi de fond";//matcher.group(2);
                                        transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                        transaction_currency = matcher.group(2);
                                        transaction_state = "Succes";
                                        transaction_beneficiary_name = matcher.group(3) + matcher.group(4);
                                        transaction_beneficiary_account_number = matcher.group(6);
                                        transaction_date = matcher.group(7);
                                        transaction_fees =  "0";
                                        transaction_id =  matcher.group(11);
                                        transaction_reference = "NA";
                                        //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                        transaction_balance = matcher.group(9);
                                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                        transaction_made_by = sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME);
                                        booleen = true;
                                        Log.e("FOUND", "YES YES YES YES 7");
                                    }else{
                                        transaction_type = "NA";//matcher.group(2);
                                        transaction_amount = "0";
                                        transaction_currency = "NA";
                                        transaction_state = "NA";
                                        transaction_beneficiary_name = "NA";
                                        transaction_beneficiary_account_number = "NA";
                                        transaction_date = "NA";
                                        transaction_fees =  "0";
                                        transaction_id =  "NA";
                                        transaction_reference = "NA";
                                        transaction_balance = "0";
                                        transaction_made_by = "NA";
                                        booleen = false;
                                        Log.e("FOUND", "YES YES YES YES 8");
                                    }
                                }
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                Log.e("booleen", "" + booleen);
            } catch (JSONException e) {

                Log.e("VOILAAAAAAAAAAAAA", "VOILAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                e.printStackTrace();
            }

            if (booleen){
                SMSDataSource smsDatatSource = new SMSDataSource(context);
                smsDatatSource.open();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

                Log.e("NUMBERS-NUMBERS", "" + this.transaction_amount + ", " + this.transaction_fees + ", " + this.transaction_balance);

                SMS sms = smsDatatSource.createSMS(System.currentTimeMillis(),this.transaction_type,
                        Float.parseFloat(this.transaction_amount.replaceAll("[\\-\\+\\^:,]", "")),this.transaction_beneficiary_name,this.transaction_beneficiary_account_number,
                        this.transaction_date, this.transaction_id, this.transaction_reference, Float.parseFloat(this.transaction_fees.replaceAll("[\\-\\+\\^:,]", "")), this.transaction_state,
                        Float.parseFloat(this.transaction_balance.replaceAll("[\\-\\+\\^:,]", "")), this.transaction_currency,this.transaction_made_by ,this.sms_sender,
                        this.sms_sent_date, this.sms_body, this.sms_receiver,sharedPreferences.getString(Utils.TENANT_ID, Utils.DEFAULT_TENANT_ID),
                        sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME), System.currentTimeMillis(), 0, 0,
                        sharedPreferences.getString(Utils.EMAIL, ""), sharedPreferences.getString(Utils.PHONE, ""),
                        sharedPreferences.getString(Utils.TAXPAYERNUMBER, ""), sharedPreferences.getString(Utils.NUMBERTRADEREGISTER, ""));
                smsDatatSource.close();
                /*SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                editor.putLong(Utils.LAST_SMS_ID, sms.getId());
                editor.apply();*/


                if (SMSsActivity.thisActivity != null){
                    SMSsActivity.refreshList();
                }

                //String ticketAccessToken = sharedPreferences.getString(Utils.TICKET_ACCESS_TOKEN,"OK");

                    BeginSaveReceipt beginSaveReceipt = new BeginSaveReceipt(context, sms, Utils.TICKET_CLIENT_ID, Utils.TICKET_CLIENT_SECRET, Utils.TICKET_GRANT_TYPE);
                    beginSaveReceipt.execute(Utils.TICKET_ACCESS_TOKEN_END_POINT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = context.getString(R.string.channel_name);
                    String description = context.getString(R.string.channel_description);
                    int importance = NotificationManager.IMPORTANCE_MAX;
                    NotificationChannel channel = new NotificationChannel(Utils.NOTIFICATION_CHANEL_ID, name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                CharSequence bigTitle = context.getResources().getString(R.string.app_name);
            /*NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(this.sms_body);
            bigText.setBigContentTitle("Pour Impression par blutooth");
            bigText.setSummaryText(bigTitle);*/

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.loggo));
                bigPictureStyle.setSummaryText(bigTitle);
                bigPictureStyle.setBigContentTitle("Pour Impression par blutooth");

                Intent notificationIntent  = null;
               // notificationIntent = new Intent(context.getApplicationContext(),PrintNewSMS.class);


                SharedPreferences sharedPreferences1 = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                String username = sharedPreferences1.getString(Utils.USERNAME, null);
                String password = sharedPreferences1.getString(Utils.PASSWORD, null);
                String name = sharedPreferences1.getString(Utils.NAME, null);
                String email = sharedPreferences1.getString(Utils.EMAIL, null);
                String tenantid = sharedPreferences1.getString(Utils.TENANT_ID, null);
                if (username != null & password != null & name != null & email != null &tenantid != null){
                    notificationIntent = new Intent(context.getApplicationContext(), Authenticated.class);
                    //notificationIntent.putExtra(Utils.COMING_FROM_NOTIFICATION, true);
                    notificationIntent.putExtra(Utils.SMS_ID, sms.getId());
                }else{
                    notificationIntent = new Intent(context.getApplicationContext(), Welcome.class);
                    notificationIntent.putExtra(Utils.SMS_ID, sms.getId());
                }
                Bundle data = new Bundle();
                data.putLong(Utils.sms_id, sms.getId());
                notificationIntent.putExtra(Utils.data, data);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

                //////

                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

                taskStackBuilder.addParentStack(PrintNewSMS.class);
                taskStackBuilder.addNextIntent(notificationIntent);

                //PendingIntent notificationPendingIntent =
                //PendingIntent.getActivity(context, 0, notificationIntent, 0);

                PendingIntent notificationPendingIntent =
                        taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                ///////




                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                long[] patterns =  {500,500,500,500,500,500,500,500,500};

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Utils.NOTIFICATION_CHANEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(this.sms_sender)
                        .setContentText(this.sms_body)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.loggo))
                        .setStyle(bigPictureStyle)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(notificationPendingIntent)
                        .setVibrate(patterns)
                        .setSound(soundUri)
                        .setLights(Color.CYAN, 500, 500)
                        .setAutoCancel(true);

                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(Utils.NOTIFICATION_ID ++, notification);
            }
        }
    }


    private class DoSaveTicket extends AsyncTask<String, Integer, String> {
        private Context context;
        private String access_token;
        private SMS smsToSend;
        private int statusCode = 0;

        private DoSaveTicket(Context context, SMS smsToSend, String token) {
            this.context = context;
            this.smsToSend = smsToSend;
            this.access_token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String resultat = "";
            String str_url = strings[0];
            URL url = null;
            try {
                url = new URL(str_url);
                HttpsURLConnection urlConnection = null;


                SSLContext context = null;
                try {
                    // Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

// From https://www.washington.edu/itconnect/security/ca/load-der.crt
                    //InputStream caInput = new BufferedInputStream(getAssets().open("pridesoft.crt"));
                    Certificate ca = null;
                    try {
                        try (InputStream caInput = this.context.getAssets().open("mobilebiller.crt")) {
                            ca = cf.generateCertificate(caInput);
                            //Log.e("CA=",  "\n\n\n\n\n" + ((X509Certificate) ca).getSubjectDN() + "\n\n\n\n");
                            //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

// Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = null;
                    try {
                        keyStore = KeyStore.getInstance(keyStoreType);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        keyStore.load(null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    keyStore.setCertificateEntry("ca", ca);

                    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

// Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = null;
                    try {
                        tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        tmf.init(keyStore);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }

// Create an SSLContext that uses our TrustManager
                    try {
                        context = SSLContext.getInstance("TLS");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        context.init(null, tmf.getTrustManagers(), null);
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    url = new URL(str_url);


                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setSSLSocketFactory(context.getSocketFactory());
                    urlConnection.setHostnameVerifier(new NullHostNameVerifier());

                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }                try {
                    Log.e("URL", str_url);
                   // urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.e("ACCESSTOKEN", this.access_token);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.access_token);
                    urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);

                    JSONObject body = new JSONObject();

                    SharedPreferences preferences = this.context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

                    body.put(Utils.USERID, preferences.getString(Utils.USERID, "mobilebiller"));
                    body.put(Utils.TENANTID, preferences.getString(Utils.TENANTID, "mobilebiller"));
                    body.put("transactionid", smsToSend.getTransaction_id());
                    body.put("amount", smsToSend.getTransaction_amount());
                    body.put("address", smsToSend.getSms_sender());
                    /*String transactionDate = smsToSend.getTransaction_date().replace('/', '-');
                    String vet [] = transactionDate.split("\\w+");
                    String vett [] = vet[0].split("\\-");*/
                    body.put("date", smsToSend.getTransaction_date());
                    body.put("body", smsToSend.getSms_body());
                    String dateSent = smsToSend.getSms_date().replace('/', '-');
                    /*String vet1 [] = dateSent.split("\\w+");
                    String vett2 [] = vet1[0].split("\\-");*/
                    body.put("date_sent", smsToSend.getSms_date());
                    body.put("current_balance", smsToSend.getTransaction_balance());
                    body.put("available_balance", -1);
                    body.put("beneficiary", smsToSend.getTransaction_beneficiary_name());
                    body.put("type", smsToSend.getTransaction_type());
                    body.put("verification_code", smsToSend.getTenant());
                    body.put("made_by", smsToSend.getTransaction_made_by());
                    body.put("currency", smsToSend.getTransaction_currency());
                    String query = body.toString();//"email=" + this.username + "&password=" + this.pwd;
                    Log.e("query", query);
                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(os);
                    out.write(query);
                    out.close();

                    this.statusCode = urlConnection.getResponseCode();

                    Log.e("statusCode", "4: " + statusCode);

                    InputStream in = urlConnection.getInputStream();

                    BufferedReader br = null;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        return e.getMessage();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                Log.e("Exception3", "3: " + e.getMessage());
                                return e.getMessage();
                            }
                        }
                    }
                    in.close();
                    resultat = sb.toString();
                } catch (IOException e) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect");
                        return jsonObject.toString();
                    } catch (JSONException e1) {
                    }
                    return e.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return jsonObject.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return e.getMessage();
            }

            return resultat;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){

                    SMSDataSource smsDatatSource = new SMSDataSource(context);
                    smsDatatSource.open();
                    smsToSend.setIs_online_saved(1);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.COLUMN_IS_ONLINE_SAVED, 1);
                    smsDatatSource.updateSMS(smsToSend.getId(), contentValues);
                    smsDatatSource.close();
                }else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result Return Result", result);
        }
    }


    private class BeginSaveReceipt extends AsyncTask<String, Integer, String> {
        private int clientId;
        private String clienSecret;
        private String grantType;
        private Context context;
        private SMS sms;
        private int statusCode = 0;

        private BeginSaveReceipt(Context context, SMS smsToSend, int clientId, String clienSecret, String grantType) {
            this.context = context;
            this.sms = smsToSend;
            this.clientId = clientId;
            this.clienSecret = clienSecret;
            this.grantType = grantType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String resultat = "";
            String str_url = strings[0];
            URL url = null;
            try {
                url = new URL(str_url);
                HttpsURLConnection urlConnection = null;


                SSLContext context = null;
                try {
                    // Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

// From https://www.washington.edu/itconnect/security/ca/load-der.crt
                    //InputStream caInput = new BufferedInputStream(getAssets().open("pridesoft.crt"));
                    Certificate ca = null;
                    try {
                        try (InputStream caInput = this.context.getAssets().open("mobilebiller.crt")) {
                            ca = cf.generateCertificate(caInput);
                            //Log.e("CA=",  "\n\n\n\n\n" + ((X509Certificate) ca).getSubjectDN() + "\n\n\n\n");
                            //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

// Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = null;
                    try {
                        keyStore = KeyStore.getInstance(keyStoreType);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        keyStore.load(null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    keyStore.setCertificateEntry("ca", ca);

                    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

// Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = null;
                    try {
                        tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        tmf.init(keyStore);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }

// Create an SSLContext that uses our TrustManager
                    try {
                        context = SSLContext.getInstance("TLS");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        context.init(null, tmf.getTrustManagers(), null);
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    url = new URL(str_url);


                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setSSLSocketFactory(context.getSocketFactory());
                    urlConnection.setHostnameVerifier(new NullHostNameVerifier());

                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }                try {
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    String query = "client_id=" + this.clientId + "&client_secret=" + this.clienSecret + "&grant_type=" + this.grantType ;
                    Log.e("query", query);
                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(os);
                    out.write(query);
                    out.close();



                    this.statusCode = urlConnection.getResponseCode();

                    Log.e("statusCode", "4: " + statusCode);

                    //if (statusCode ==  200) {
                    InputStream in = urlConnection.getInputStream();

                    BufferedReader br = null;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }

                    } catch (IOException e) {
                        return e.getMessage();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                Log.e("Exception3", "3: " + e.getMessage());
                                return e.getMessage();
                            }
                        }
                    }
                    in.close();
                    //os.close();
                    resultat = sb.toString();
                    /*}else if (statusCode == 401){

                    }*/

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect");
                        return jsonObject.toString();
                    } catch (JSONException e1) {
                        //e1.printStackTrace();

                    }

                    return e.getMessage();
                }
            } catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return jsonObject.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return e.getMessage();
            }

            return resultat;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Utils.ERROR)){
                }else if (jsonObject.has(Utils.ACCESS_TOKEN) && jsonObject.has(Utils.TOKEN_TYPE) && jsonObject.has(Utils.EXPIRES_IN) && jsonObject.getInt(Utils.EXPIRES_IN) > 0){
                    SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE).edit();
                    editor.putString(Utils.TICKET_ACCESS_TOKEN, jsonObject.getString(Utils.ACCESS_TOKEN));
                    editor.apply();
                    DoSaveTicket doSaveTicket =  new DoSaveTicket(this.context,this.sms, jsonObject.getString(Utils.ACCESS_TOKEN));
                    doSaveTicket.execute(Utils.TICKET_END_POINT);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("access_token", result);
        }
    }


    private class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            boolean retVal;
            try {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                retVal =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1 && Utils.HOSTNAME.equals("mobilebiller.idea-cm.club");
                //hv.verify("pridesoft.armp.cm", sslSession);
                //retVal = true;
            }catch (Exception e){
                //e.getStackTrace();
                //Log.e("NullHostNameVerifier", e.getMessage() + "\n\n\n" + e.getCause() + "\n\n\n");
                retVal = false;
            }
            return retVal;
        }
    }

}
