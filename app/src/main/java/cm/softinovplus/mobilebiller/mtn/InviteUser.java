package cm.softinovplus.mobilebiller.mtn;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.mtn.utils.CustomToast;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

public class InviteUser extends AppCompatActivity {

    private TextView titre_invite_user, result_invite_user;
    private EditText edit_firtname, edit_lastname, edit_email, edit_phone, edit_city;
    private AppCompatSpinner spinner_region;
    private String selectedRegion;
    private SharedPreferences authPreferences;

    private ProgressBar invite_loader;
    private Button inviteBtn;

    private ArrayAdapter<CharSequence> adapter;
    private View invite_user_root_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);

        titre_invite_user = findViewById(R.id.titre_invite_user);
        result_invite_user = findViewById(R.id.result_invite_user);

        edit_firtname = findViewById(R.id.edit_firtname);
        edit_lastname = findViewById(R.id.edit_lastname);
        edit_email = findViewById(R.id.edit_email);
        edit_phone = findViewById(R.id.edit_phone);
        edit_city = findViewById(R.id.edit_city);

        invite_user_root_view = findViewById(R.id.invite_user_root_view);

        inviteBtn = findViewById(R.id.inviteBtn);

        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
        titre_invite_user.setText(authPreferences.getString(Utils.TENANT_NAME, ""));

        spinner_region =  findViewById(R.id.spinner_region);
        invite_loader = findViewById(R.id.invite_loader);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.regions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_region.setAdapter(adapter);
        selectedRegion = adapter.getItem(0).toString();
        Log.e("selectedRegion", selectedRegion);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = edit_firtname.getText().toString().trim();
                String lastname = edit_lastname.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String phone = edit_phone.getText().toString().trim();
                String city = edit_city.getText().toString().trim();

                // Pattern match for email id
                Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
                Matcher m = p.matcher(email);



                // Check if all strings are null or not
                if (firstname.equals("") || firstname.length() == 0
                        || lastname.equals("") || lastname.length() == 0
                        || email.equals("") || email.length() == 0
                        || phone.equals("") || phone.length() == 0
                        || city.equals("") || city.length() == 0){
                    new CustomToast().Show_Toast(InviteUser.this, invite_user_root_view, "All fields are required.");
                }// Check if email id valid or not
                else if (!m.find()){
                    new CustomToast().Show_Toast(InviteUser.this, invite_user_root_view, "Your Email Id is Invalid.");
                }// Check if both password should be equal
                else {

                    result_invite_user.setText("");
                    DoInviteUser doInviteUser = new DoInviteUser(InviteUser.this, invite_loader,
                            firstname, lastname, email, phone, selectedRegion, city, authPreferences.getString(Utils.USERID,""),
                            authPreferences.getString(Utils.TENANT_ID,""), authPreferences.getString(Utils.ACCESS_TOKEN,""));
                    doInviteUser.execute(Utils.HOST_IDENTITY_AND_ACCESS + "api/users-invitations?scope=SCOPE_MANAGE_COLLABORATORS");
                }
            }
        });

        spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRegion = adapter.getItem(position).toString();
                Log.e("selectedRegion", selectedRegion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRegion = null;
            }
        });

    }

    private class DoInviteUser extends AsyncTask<String, Integer, String> {
        private String token;
        private ProgressBar dialog;
        private Context context;
        private String firstname, lastname, email, phone, region, city, userid, tenantid;
        private int statusCode = 0;

        public DoInviteUser(Context context, ProgressBar dialog, String firstname, String lastname, String email,
                        String phone, String region, String city, String userid, String tenantid,String token) {
            this.context = context;
            this.dialog = dialog;
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.phone = phone;
            this.city = city;
            this.region = region;
            this.userid = userid;
            this.tenantid = tenantid;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setVisibility(View.VISIBLE);
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
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.e("ACCESSTOKEN", this.token);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token);
                    //urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    //JSONObject body = new JSONObject();
                    //body.put("firstname", this.firstname);
                    //body.put("lastname", this.lastname);
                    //body.put("email", this.email);
                    //body.put("phone", this.phone);
                    //body.put("city", this.city);
                    //body.put("region", this.region);
                    String query = "firstname=" + URLEncoder.encode(this.firstname, "UTF-8") + "&lastname=" + URLEncoder.encode(this.lastname ,"UTF-8")+ "&email=" + this.email
                            +"&tenantid=" + this.tenantid + "&phone1=" + URLEncoder.encode(this.phone, "UTF-8") + "&invited_by=" + this.userid +
                            "&city=" + URLEncoder.encode(this.city, "UTF-8") + "&region=" + this.region;
                    //body.toString();//"email=" + this.username + "&password=" + this.pwd;
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
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){
                    result_invite_user.setText(returnedResult.getString(Utils.RESPONSE));
                    result_invite_user.setTextColor(Color.GREEN);
                }else {
                    result_invite_user.setText(returnedResult.getString(Utils.RAISON));
                    result_invite_user.setTextColor(Color.RED);
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                result_invite_user.setText("Whoop Something Went wrong!!!");
                result_invite_user.setTextColor(Color.RED);
            }

           // Log.e("result", result);
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
