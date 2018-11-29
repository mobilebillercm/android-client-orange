package cm.softinovplus.mobilebiller.orange.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.orange.db.MySQLiteHelper;
import cm.softinovplus.mobilebiller.orange.db.SMSDataSource;
import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nkalla on 25/11/18.
 */

public class NetworkListener extends BroadcastReceiver {
    //private static final String TAG = "NetworkConnectivityListener";
    private ConnectivityReceiverListener mConnectivityReceiverListener;
    private NetworkInfo.State mState;
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;
    private String mReason;
    private boolean mIsFailover;
    private static final boolean DBG = true;

    /*public NetworkListener(ConnectivityReceiverListener listener) {
        mConnectivityReceiverListener = listener;
    }*/

    public NetworkListener (){}

    @Override
    public void onReceive(Context context, final Intent intent) {
        //mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){


            /*SMSDataSource smsDatatSource1 = new SMSDataSource(context);
            smsDatatSource1.open();
            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

            //Log.e("NUMBERS-NUMBERS", "" + this.transaction_amount + ", " + this.transaction_fees + ", " + this.transaction_balance);

            SMS sms = smsDatatSource1.createSMS(System.currentTimeMillis(),"Retrait",
                    2000, "TAFOLONG FRANCIS","+23767174756", "2018-09-25 10:30:40", "123456", "147852369", 0, "2018-09-25 10:30:40",
                    30000, "XAF","NKALLA EHAWE" ,"+671747569", "2018-09-25 10:30:40", "Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.",
                    " ",sharedPreferences.getString(Utils.TENANT_ID, Utils.DEFAULT_TENANT_ID),
                    sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME), System.currentTimeMillis(), 0, 0);
            SMS sms1 = smsDatatSource1.createSMS(System.currentTimeMillis(),"Retrait",
                    2000, "TAFOLONG FRANCIS 1","+23767174756", "2018-09-25 10:30:40", "123456", "147852369", 0, "2018-09-25 10:30:40",
                    30000, "XAF","NKALLA EHAWE 1" ,"+671747569", "2018-09-25 10:30:40", "Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.",
                    " ",sharedPreferences.getString(Utils.TENANT_ID, Utils.DEFAULT_TENANT_ID),
                    sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME), System.currentTimeMillis(), 0, 0);
            SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
            editor.putLong(Utils.LAST_SMS_ID, sms1.getId());
            editor.apply();

            smsDatatSource1.close();*/



            Log.e("ListenConnection", "OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
            //Toast.makeText(context, "ListenConnection   OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK", Toast.LENGTH_LONG).show();

            boolean noConnectivity =
                    intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity) {
                mState = NetworkInfo.State.DISCONNECTED;
            } else {
                mState = NetworkInfo.State.CONNECTED;
            }

            mNetworkInfo = (NetworkInfo)
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
            mOtherNetworkInfo = (NetworkInfo)
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            mIsFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

                if (mState.toString().equals("CONNECTED")){
                    //Toast.makeText(context.getApplicationContext(), "Reconnected to network", Toast.LENGTH_LONG).show();

                    SMSDataSource smsDatatSource = new SMSDataSource(context);
                    smsDatatSource.open();

                    List<SMS> smssNotSaved = smsDatatSource.getSMSNotSaved();
                    Log.e("LIste sms " , smssNotSaved.toString());

                    if (smssNotSaved.size() > 0){
                        BeginSaveReceipt beginSaveReceipt = new BeginSaveReceipt(context, smssNotSaved, Utils.TICKET_CLIENT_ID, Utils.TICKET_CLIENT_SECRET, Utils.TICKET_GRANT_TYPE);
                        beginSaveReceipt.execute(Utils.TICKET_ACCESS_TOKEN_END_POINT);
                    }

                    smsDatatSource.close();

                }
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }


    private class DoSaveTickets extends AsyncTask<String, Integer, String> {
        private Context context;
        private String access_token;
        private List<SMS> smssToSend;
        private int statusCode = 0;

        private DoSaveTickets(Context context, List<SMS> smssToSend, String token) {
            this.context = context;
            this.smssToSend = smssToSend;
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

                    JSONArray body = new JSONArray();

                    SharedPreferences preferences = this.context.getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

                    for (SMS sms : this.smssToSend){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put(Utils.USERID, preferences.getString(Utils.USERID, "mobilebiller"));
                        jsonObject.put(Utils.TENANTID, preferences.getString(Utils.TENANTID, "mobilebiller"));
                        jsonObject.put("transactionid", sms.getTransaction_id());
                        jsonObject.put("amount", sms.getTransaction_amount());
                        jsonObject.put("address", sms.getSms_sender());
                    /*String transactionDate = smsToSend.getTransaction_date().replace('/', '-');
                    String vet [] = transactionDate.split("\\w+");
                    String vett [] = vet[0].split("\\-");*/
                        jsonObject.put("date", sms.getTransaction_date());
                        jsonObject.put("body", sms.getSms_body());
                        String dateSent = sms.getSms_date().replace('/', '-');
                    /*String vet1 [] = dateSent.split("\\w+");
                    String vett2 [] = vet1[0].split("\\-");*/
                        jsonObject.put("date_sent", sms.getSms_date());
                        jsonObject.put("current_balance", sms.getTransaction_balance());
                        jsonObject.put("available_balance", -1);
                        jsonObject.put("beneficiary", sms.getTransaction_beneficiary_name());
                        jsonObject.put("type", sms.getTransaction_type());
                        jsonObject.put("verification_code", sms.getTenant());
                        jsonObject.put("made_by", sms.getTransaction_made_by());
                        jsonObject.put("currency", sms.getTransaction_currency());

                        body.put(jsonObject);
                    }

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

                    for (SMS sms : smssToSend){
                        sms.setIs_online_saved(1);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MySQLiteHelper.COLUMN_IS_ONLINE_SAVED, 1);
                        smsDatatSource.updateSMS(sms.getId(), contentValues);
                    }
                    smsDatatSource.close();
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
        private List<SMS> smss;
        private int statusCode = 0;

        private BeginSaveReceipt(Context context, List<SMS> smssToSend, int clientId, String clienSecret, String grantType) {
            this.context = context;
            this.smss = smssToSend;
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
                    DoSaveTickets doSaveTickets =  new DoSaveTickets(this.context,this.smss, jsonObject.getString(Utils.ACCESS_TOKEN));
                    doSaveTickets.execute(Utils.BULK_TICKET_END_POINT);
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