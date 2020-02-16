package cm.softinovplus.mobilebiller.mtn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.mtn.adapter.RemoteSmsAdapter;
import cm.softinovplus.mobilebiller.mtn.adapter.VerticalLineDecorator;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

public class ListeRecuDistant extends AppCompatActivity implements RemoteSmsAdapter.SMSAdapterListener{

    private RecyclerView recyclerView;
    private RemoteSmsAdapter adapter;
    private List<SMS> smses ;
    private List<SMS> selectedSmses ;
    public static AppCompatActivity thisActivity;
    private EditText editsearch;
    //private ProgressBar load_remote_sms;
    private String token;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_recu_distant);

        thisActivity = this;
        recyclerView =  findViewById(R.id.remote_sms_list);
        //load_remote_sms = findViewById(R.id.load_remote_sms);
        mLayoutManager = new LinearLayoutManager(this);
        smses = new ArrayList<>();
        selectedSmses = new ArrayList<>();
        smses.add(null);
        adapter = new RemoteSmsAdapter(this, smses, selectedSmses);

        token = null;


        adapter.setLoadMoreListener(new RemoteSmsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                //Log.e("onLoadMore is called", "onLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMoreonLoadMore");

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (token == null){
                            BeginGetRemoteReceipts beginGetRemoteReceipts = new BeginGetRemoteReceipts(ListeRecuDistant.this,Utils.TICKET_CLIENT_ID, Utils.TICKET_CLIENT_SECRET, Utils.TICKET_GRANT_TYPE);
                            beginGetRemoteReceipts.execute(Utils.TICKET_ACCESS_TOKEN_END_POINT);

                        }else{
                            Log.e("onLoadMore is c with to", token);
                            GetRemoteReceipts getRemoteReceipts = new GetRemoteReceipts(getApplicationContext(), token);
                            SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                            int indice = smses.size() - 1;
                            getRemoteReceipts.execute(Utils.TICKET_END_POINT + "/" + sharedPreferences.getString(Utils.TENANT_ID, "")  +  "/" + sharedPreferences.getString(Utils.USERID,"") + "/" + indice);
                        }
                    }
                });
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if (token == null){
            BeginGetRemoteReceipts beginGetRemoteReceipts = new BeginGetRemoteReceipts(ListeRecuDistant.this,Utils.TICKET_CLIENT_ID, Utils.TICKET_CLIENT_SECRET, Utils.TICKET_GRANT_TYPE);
            beginGetRemoteReceipts.execute(Utils.TICKET_ACCESS_TOKEN_END_POINT);

        }else{
            GetRemoteReceipts getRemoteReceipts = new GetRemoteReceipts(getApplicationContext(), token);
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
            int indice = smses.size() - 1;
            getRemoteReceipts.execute(Utils.TICKET_END_POINT + "/" + sharedPreferences.getString(Utils.TENANT_ID, "")  +  "/" + sharedPreferences.getString(Utils.USERID,"") + "/" + indice);
        }


        /*editsearch = findViewById(R.id.editsearch);

        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged", "" + s);
                //adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged", "" + s);
                adapter.getFilter().filter(s);

            }
        });*/

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getApplicationContext(), SearchSMSForm.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onSmsSelected(SMS sms) {
        Toast.makeText(getApplicationContext(), sms.getTransaction_beneficiary_name(), Toast.LENGTH_LONG).show();
    }


    private class GetRemoteReceipts extends AsyncTask<String, Integer, String> {
        private Context context;
        private String access_token;
        private int statusCode = 0;

        public GetRemoteReceipts(Context context, String access_token) {
            this.context       = context;
            this.access_token  = access_token;
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
                        try (InputStream caInput = getAssets().open("mobilebiller.crt")) {
                            ca = cf.generateCertificate(caInput);
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
                }

                try {
                    Log.e("URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    //urlConnection.setDoOutput(true);
                    //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                    //String access_token = sharedPreferences.getString(Utils.ACCESS_TOKEN, "");
                    Log.e("ACCESSTOKENSEASEA", this.access_token);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.access_token);
                    //urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    /*JSONObject body = new JSONObject();
                    body.put(Utils.EMAIL, this.username);
                    body.put(Utils.PASSWORD, this.pwd);
                    String query = body.toString();*///"email=" + this.username + "&password=" + this.pwd;

                    /*
                    https://mobilebiller.idea-cm.club:445/api/tenant/57261bf0-d0a1-11e8-bd1f-47fec029a2ff/client/e40394e0-d0a3-11e8-a37e-6b2692fae9e0/services-validities-periods?scope=SCOPE_MANAGE_IDENTITIES_AND_ACCESSES
                     */
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
                    //os.close();
                    resultat = sb.toString();
                    /*}else if (statusCode == 401){

                    }*/

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect " + e.getMessage());
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
                    jsonObject.put("message", "Wopp something went wrong0  " + e.getMessage());
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
            /*if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }*/

            //TextView textView = view.findViewById(R.id.resultgetaccesstoken);
            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){
                    if (returnedResult.has("response") && returnedResult.getJSONArray("response").length()>0){
                        JSONArray response = returnedResult.getJSONArray("response");
                        smses.remove(smses.size()-1);
                        List<SMS> smsList = new ArrayList<>();
                        for (int i = 0; i<response.length(); i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            SMS sms = parse(jsonObject.getString("body"));
                            if (sms != null){
                                smsList.add(sms);
                            }
                        }

                        smses.addAll(smsList);
                        smses.add(null);
                        adapter.setMoreDataAvailable(true);

                    }else{
                        Log.e("FINFINFIN", "FINFINFIN");
                        adapter.setMoreDataAvailable(false);

                        //smses.remove(smses.size()-1);
                    }
                }else{
                    adapter.setMoreDataAvailable(false);
                    //smses.remove(smses.size()-1);
                }

                adapter.notifyDataChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result Service access", result);
        }
    }


    private class BeginGetRemoteReceipts extends AsyncTask<String, Integer, String> {
        private int clientId;
        private String clienSecret;
        private String grantType;
        private Context context;
        private int statusCode = 0;

        private BeginGetRemoteReceipts(Context context, int clientId, String clienSecret, String grantType) {
            this.context = context;
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
                    token = jsonObject.getString(Utils.ACCESS_TOKEN);
                    GetRemoteReceipts getRemoteReceipts = new GetRemoteReceipts(getApplicationContext(), token);
                    SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                    int indice = smses.size() - 1;
                    getRemoteReceipts.execute(Utils.TICKET_END_POINT + "/" + sharedPreferences.getString(Utils.TENANT_ID, "")  +  "/" + sharedPreferences.getString(Utils.USERID,"") + "/" + indice);
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

    public SMS parse(String body){
        boolean booleen = false;
        String transaction_type = null;
        String transaction_amount= null;
        String transaction_currency= null;
        String transaction_state= null;
        String transaction_beneficiary_name= null;
        String transaction_beneficiary_account_number= null;
        String transaction_date= null;
        String transaction_fees= null;
        String transaction_id= null;
        String transaction_reference= null;
        String transaction_balance= null;
        String transaction_made_by= null;
        try {

            SharedPreferences mysp = getApplicationContext().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
            Log.e("TEST22222222222", "After APP_CONFIGURAION");
            JSONObject jsonObject0 = new JSONObject(mysp.getString(Utils.REMOTE_REGULAREXPRESSION,""));

            JSONObject mtncameroon = jsonObject0.getJSONObject(Utils.MTNCAMEROON);
            JSONArray frenchversion_mtncameroon = mtncameroon.getJSONArray(Utils.FRENCHVERSION);

            Log.e("TEST33333333333333", "After APP_CONFIGURAION");


            for (int i = 0; i<frenchversion_mtncameroon.length(); i++){
                try {
                    JSONObject jsonObject = (JSONObject)frenchversion_mtncameroon.get(i);
                    Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + body);
                    Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(body);
                    if (matcher1.find()){
                        Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                        Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(body);
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
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                        Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + body);
                        Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(body);
                        if (matcher1.find()){
                            Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                            Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(body);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                        Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + body);
                        Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(body);
                        if (matcher1.find()){
                            Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                            Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(body);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    //String aa = (matcher.group(9) == null)?"":matcher.group(9);      Terrain: 677879547 mme kameni Lydie.
                                    transaction_balance = matcher.group(8);
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                        Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + body);
                        Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(body);
                        if (matcher1.find()){
                            Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                            Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(body);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
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
            Log.e("VOILAAAAAAAAAAAAA", "VOILAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + e.getMessage());
            e.printStackTrace();
        }

        SMS sms;

        if (booleen){
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
            sms = new SMS(System.currentTimeMillis(),transaction_type,
                    Float.parseFloat(transaction_amount.replaceAll("[\\-\\+\\^:,]", "")),transaction_beneficiary_name,transaction_beneficiary_account_number,
                    transaction_date, transaction_id, transaction_reference, Float.parseFloat(transaction_fees.replaceAll("[\\-\\+\\^:,]", "")), transaction_state,
                    Float.parseFloat(transaction_balance.replaceAll("[\\-\\+\\^:,]", "")), transaction_currency,transaction_made_by ,"",
                    "", body, "",sharedPreferences.getString(Utils.TENANT_ID, Utils.DEFAULT_TENANT_ID),
                    sharedPreferences.getString(Utils.TENANT_NAME,Utils.DEFAULT_TENANT_NAME), System.currentTimeMillis(), 1, 1,
                    sharedPreferences.getString(Utils.EMAIL, ""), sharedPreferences.getString(Utils.PHONE, ""),
                    sharedPreferences.getString(Utils.TAXPAYERNUMBER, ""), sharedPreferences.getString(Utils.NUMBERTRADEREGISTER, ""));
        }else {
            sms = null;
        }
        return  sms;
    }
}
