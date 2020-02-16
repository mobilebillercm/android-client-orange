package cm.softinovplus.mobilebiller.mtn.fragments;

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


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.mtn.Authenticated;
import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.utils.CustomToast;
import cm.softinovplus.mobilebiller.mtn.utils.Tenant;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static cm.softinovplus.mobilebiller.mtn.utils.Utils.SignUpFragment;

public class LoginFragment extends Fragment implements OnClickListener {

    private View view;

    private EditText emailid, password;
    private Button loginButton;
    private TextView forgotPassword, signUp, text_config_loader;
    private CheckBox show_hide_password;
    private String tenantName;
    private String tenantId;
    private AppCompatSpinner spinner_tenant;
    private LinearLayout tenant_layout;
    private List<Tenant> tenants;
    private ArrayAdapter<String> arrayAdapter;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    private  ProgressBar progressbar_config_regex;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);

        text_config_loader = view.findViewById(R.id.text_config_loader);
        progressbar_config_regex = view.findViewById(R.id.progressbar_config_regex);

        LoadConfigurationSmsRegex loadConfigurationSmsRegex = new LoadConfigurationSmsRegex(getActivity(), progressbar_config_regex, text_config_loader);
        loadConfigurationSmsRegex.execute("https://mobilebiller.idea-cm.club/sms_regular_expressions");

        hideKeyboard();
        initViews();
        return view;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = view.findViewById(R.id.login_emailid);
        password =  view.findViewById(R.id.login_password);
        spinner_tenant = view.findViewById(R.id.spinner_tenant);
        tenant_layout = view.findViewById(R.id.tenant_layout);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view.findViewById(R.id.show_hide_password);
        //loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        //textView = (TextView) view.findViewById(R.id.resultgetaccesstoken);

        // Setting text selector over textviews
        /*XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}*/
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                // If it is checkec then show password else hide
                // password
                if (isChecked) {

                    show_hide_password.setText(R.string.hide_pwd);// change
                    // checkbox
                    // text
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                } else {
                    show_hide_password.setText(R.string.show_pwd);// change
                    // checkbox
                    // text

                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                }

            }
        });

        emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String getEmailId = ((EditText)v).getText().toString().trim();
                    // Check patter for email id
                    if (getEmailId.length() > 0){
                        Pattern p = Pattern.compile(Utils.REGEX_EMAIL);

                        Matcher m = p.matcher(getEmailId);
                        if (!m.matches()) {
                            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
                        }else{
                            Toast.makeText(getActivity(), "Getting Tenant", Toast.LENGTH_LONG).show();
                            ProgressBar progressBarGetTenant = view.findViewById(R.id.progressBarGetTenant);
                            GetTenant getTenant = new GetTenant(getActivity(), progressBarGetTenant);
                            getTenant.execute(Utils.makeUrlUserTenants(emailid.getText().toString()));
                        }
                    }

                }

            }
        });

        //checkIfConnected();

    }

    private void checkIfConnected() {

        SharedPreferences preferences = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
        String pref_email =  preferences.getString(Utils.EMAIL,null);
        String pref_passowrd = preferences.getString(Utils.PASSWORD, null);
        String pref_token_type = preferences.getString(Utils.TOKEN_TYPE, null);
        String pref_access_token = preferences.getString(Utils.ACCESS_TOKEN, null);
        long pref_expires_in = preferences.getLong(Utils.EXPIRES_IN, -1);
        String pref_refresh_token = preferences.getString(Utils.REFRESH_TOKEN,null);
        if (pref_email != null && pref_passowrd != null && pref_token_type != null
                && pref_access_token != null && !(pref_expires_in == -1) && pref_refresh_token != null){
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.login_progrees_bar);
            progressBar.setVisibility(View.GONE);
            BeginLogin beginLogin = new BeginLogin(getActivity(), progressBar,  Utils.CLIENT_ID, Utils.CLIENT_SECRET, Utils.PASSWORD_GRANT_TYPE,
                    preferences.getString(Utils.TENANT_ID,""), pref_email, pref_passowrd);
            beginLogin.execute(Utils.LOGIN_ACCESS_TOKEN_URL);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;
            case R.id.forgot_password:
                // Replace forgot password fragment with animation
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out).replace(R.id.frameContainer,
                                new ForgotPasswordFragment(), Utils.ForgotPasswordFragment).commit();
                break;
            case R.id.createAccount:
                // Replace signup frgament with animation
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUpFragment(), SignUpFragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString().trim();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.REGEX_EMAIL);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0) {
            //loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view, "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.matches()) {
            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
        }

        // Else do login and do your stuff
        else {
            //Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT).show();

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.login_progrees_bar);
            progressBar.setVisibility(View.GONE);

            BeginLogin beginLogin = new BeginLogin(getActivity(), progressBar, Utils.CLIENT_ID, Utils.CLIENT_SECRET, Utils.PASSWORD_GRANT_TYPE,
                    tenantId, emailid.getText().toString().trim(), password.getText().toString());

            beginLogin.execute(Utils.LOGIN_ACCESS_TOKEN_URL);
        }


    }

    private class BeginLogin extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private int clientId;
        private String clienSecret;
        private String grantType;
        private String tenantid;
        private String username;
        private String pwd;
        private int statusCode = 0;

        public BeginLogin(Context context, ProgressBar dialog, int clientId, String clienSecret, String grantType, String tenantid, String username, String password) {
            this.context = context;
            this.clientId = clientId;
            this.clienSecret = clienSecret;
            this.grantType = grantType;
            this.tenantid = tenantid;
            this.username = username;
            this.pwd = password;
            this.dialog = dialog;
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
            //try {
                //url = new URL(str_url);
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
                        try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
                }




                try {
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    //urlConnection.setRequestProperty("Content-Type","application/json");
                    String query = "client_id=" + this.clientId + "&client_secret=" + this.clienSecret + "&grant_type=" + this.grantType +
                           "&tenantid=" + this.tenantid +  "&username=" + this.username + "&password=" + this.pwd;
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
            /*} catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return jsonObject.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return e.getMessage();
            }*/

            return resultat;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Utils.ERROR)){
                    textView.setText(jsonObject.getString(Utils.MESSAGE));
                }else if (jsonObject.has(Utils.ACCESS_TOKEN) && jsonObject.has(Utils.TOKEN_TYPE) && jsonObject.has(Utils.EXPIRES_IN) && jsonObject.getInt(Utils.EXPIRES_IN) > 0
                        && jsonObject.has(Utils.REFRESH_TOKEN) ){
                    DoLogin doLogin = new DoLogin(this.context, this.dialog, this.tenantid, this.username, this.pwd, jsonObject);
                    doLogin.execute(Utils.makeUrlLogin(this.username));
                }else{
                    textView.setText("Woop Something went Wrong...");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }


    private class BeginGetServiceVerification extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private int clientId;
        private String clienSecret;
        private String grantType;
        private String tenantid;
        private String userid;
        private int statusCode = 0;

        public BeginGetServiceVerification(Context context, ProgressBar dialog, int clientId, String clienSecret, String grantType, String tenantid, String userid) {
            this.context = context;
            this.clientId = clientId;
            this.clienSecret = clienSecret;
            this.grantType = grantType;
            this.dialog = dialog;
            this.tenantid = tenantid;
            this.userid = userid;
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
            //try {
            //url = new URL(str_url);
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
                    try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
            }




            try {
                //urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                //urlConnection.setRequestProperty("Content-Type","application/json");
                Log.e("SEA URL", str_url);
                String query = "client_id=" + this.clientId + "&client_secret=" + this.clienSecret + "&grant_type=" + this.grantType;
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
            /*} catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return jsonObject.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return e.getMessage();
            }*/

            return resultat;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            //TextView textView = view.findViewById(R.id.resultgetaccesstoken);

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Utils.ERROR)){
                    Toast.makeText(getActivity().getApplicationContext(), "Ne peut Verifier l'acces aux services", Toast.LENGTH_LONG).show();
                   // textView.setText(jsonObject.getString(Utils.MESSAGE));
                }else if (jsonObject.has(Utils.ACCESS_TOKEN) && jsonObject.has(Utils.TOKEN_TYPE) && jsonObject.has(Utils.EXPIRES_IN) && jsonObject.getInt(Utils.EXPIRES_IN) > 0){
                    GetServiceValidity getServiceValidity = new GetServiceValidity(getContext(), dialog, jsonObject.getString(Utils.ACCESS_TOKEN));

                    getServiceValidity.execute(Utils.HOST_SERVICE_ACCESS + "api/tenant/" + this.tenantid + "/client/" + this.userid + "/services-validities-periods");
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Ne peut Verifier l'acces aux services", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }

    private class DoLogin extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String tenantid;
        private String username;
        private String pwd;
        private JSONObject token;
        private int statusCode = 0;

        public DoLogin(Context context, ProgressBar dialog, String tenantid, String username, String password, JSONObject token) {
            this.context = context;
            this.dialog = dialog;
            this.tenantid = tenantid;
            this.username = username;
            this.pwd = password;
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
                        try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
                }
                try {
                    Log.e("URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.e("ACCESSTOKEN", this.token.getString(Utils.ACCESS_TOKEN));
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token.getString(Utils.ACCESS_TOKEN));
                    //urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    /*JSONObject body = new JSONObject();
                    body.put(Utils.EMAIL, this.username);
                    body.put(Utils.PASSWORD, this.pwd);
                    String query = body.toString();*///"email=" + this.username + "&password=" + this.pwd;
                    String query =  "tenantid=" + this.tenantid + "&password=" + this.pwd  + "&username=" + this.username ;
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
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);
            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){




                    JSONObject response = returnedResult.getJSONObject(Utils.RESPONSE);

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE).edit();
                    editor.putString(Utils.EMAIL, this.username);
                    editor.putString(Utils.USERNAME, this.username);
                    editor.putString(Utils.USERID, response.getString(Utils.USERID));
                    editor.putString(Utils.PASSWORD, this.pwd);
                    editor.putString(Utils.TOKEN_TYPE, this.token.getString(Utils.TOKEN_TYPE));
                    editor.putString(Utils.ACCESS_TOKEN, this.token.getString(Utils.ACCESS_TOKEN));
                    editor.putLong(Utils.EXPIRES_IN, this.token.getLong(Utils.EXPIRES_IN) + System.currentTimeMillis());
                    editor.putString(Utils.REFRESH_TOKEN, this.token.getString(Utils.REFRESH_TOKEN));

                    editor.putString(Utils.TENANT_ID, response.getString(Utils.TENANT_ID));
                    editor.putString(Utils.TENANT_NAME, response.getString(Utils.TENANT_NAME));
                    editor.putString(Utils.TENANT_DESCRIPTION, response.getString(Utils.TENANT_DESCRIPTION));
                    editor.putString(Utils.NAME, response.getString(Utils.NAME));

                    editor.putString(Utils.PHONE, response.getString(Utils.PHONE));
                    editor.putString(Utils.TAXPAYERNUMBER, response.getString(Utils.NUMCONTIBUABLE));
                    editor.putString(Utils.NUMBERTRADEREGISTER, response.getString(Utils.NUMREGISTRECOMMERCE));

                    editor.apply();

                    LoginFragment.this.emailid.setText("");
                    LoginFragment.this.password.setText("");
                    GetServiceValidity getServiceValidity = new GetServiceValidity(getContext(), dialog, this.token.getString(Utils.ACCESS_TOKEN));

                    getServiceValidity.execute(Utils.HOST_SERVICE_ACCESS + "api/tenant/" + this.tenantid + "/client/" + response.getString(Utils.USERID) +
                            "/services-validities-periods?scope=SCOPE_MANAGE_OWN_SERVICE_PAYEMENT");

                    /*BeginGetServiceVerification beginGetServiceVerification = new BeginGetServiceVerification(this.context,this.dialog, Utils.SERVICE_ACCESS_CLIENT_ID,
                            Utils.SERVICE_ACCESS_CLIENT_SECRET, Utils.CLIENT_GRANT_TYPE, response.getString(Utils.TENANT_ID), response.getString(Utils.USERID));

                    beginGetServiceVerification.execute(Utils.SERVICE_ACCESS_ACCESS_TOKEN_END_POINT);*/

/////tenant/{tenantid}/client/{clientid}/services-validities-periods


                    /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.APP_OTHER_CONFIGURAION, MODE_PRIVATE);
                    if (sharedPreferences.getLong(Utils.LAST_SMS_ID, -1) != -1){
                        startActivity(new Intent(getActivity(), PrintNewSMS.class));
                    }else{
                        Intent intent = new Intent(getActivity().getApplicationContext(), Authenticated.class);
                        // Check if we're running on Android 5.0 or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                            //startActivity(intent);
                        } else {
                            startActivity(intent);
                        }
                    }*/

                }else {
                    textView.setText(returnedResult.getString("raison"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }

    private class GetTenant extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private int statusCode = 0;

        public GetTenant(Context context, ProgressBar dialog) {
            this.context = context;
            this.dialog = dialog;
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
                        try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
                    Log.e("TENANT URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);

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
                        Log.e("ERRROOORRRRR", e.getMessage());
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "something Went wrong");
                        return jsonObject.toString();
                    } catch (JSONException e1) {
                        //e1.printStackTrace();

                    }

                    return e.getMessage();
                }
            } catch (MalformedURLException e) {
                Log.e("ERRROOORRRRR1", e.getMessage());
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

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);

            try {
                JSONObject jsonObject = new JSONObject(result);
                 if (jsonObject.has("success") && jsonObject.has("faillure") && jsonObject.getInt("success") == 1 && jsonObject.getInt("faillure") == 0 ){


                    tenants = new ArrayList<>();
                     ArrayList<String> tenantsName = new ArrayList<String>();
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i = 0; i<jsonArray.length(); i++){
                        tenants.add(new Tenant(jsonArray.getJSONObject(i).getString("tenant"), jsonArray.getJSONObject(i).getString("tenant_name")));
                        tenantsName.add(jsonArray.getJSONObject(i).getString("tenant_name"));
                    }

                    arrayAdapter = new ArrayAdapter<String>(context, R.layout.custom_simple_spinner_item, tenantsName);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_tenant.setAdapter(arrayAdapter);
                    spinner_tenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                           tenantName = tenants.get(position).getName();
                           tenantId = tenants.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            tenantName = null;
                        }
                    });

                    if (tenants.size() > 0){
                        tenantName = tenants.get(0).getName();
                        tenantId = tenants.get(0).getId();
                    }
                    Log.e("TENANT NAMEEE", tenantId + "           " + tenantName);
                    if (tenants.size() > 1){
                        tenant_layout.setVisibility(View.VISIBLE);
                    }else {
                        tenant_layout.setVisibility(View.GONE);
                    }

                }else{
                    textView.setText("Woop Something went Wrong...");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }

    private class LoadConfigurationSmsRegex extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private TextView loadMessage;
        private int statusCode = 0;

        public LoadConfigurationSmsRegex(Context context, ProgressBar dialog, TextView loadMessage) {
            this.context = context;
            this.dialog = dialog;
            this.loadMessage = loadMessage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setVisibility(View.VISIBLE);
            this.loadMessage.setVisibility(View.VISIBLE);
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
                        try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
                }


                try {
                    Log.e("TENANT URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);

                    this.statusCode = urlConnection.getResponseCode();

                    Log.e("statusCode", "4: " + statusCode);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader br = null;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    long total = 0;
                    int lenghtOfFile = urlConnection.getContentLength();
                    try {
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);

                            total += line.length();
                            //if (line.length() > 0){
                                publishProgress((int)(total*100/lenghtOfFile));
                            //}
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
                        jsonObject.put("message", "something Went wrong");
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

        protected void onProgressUpdate(Integer percentage) {
            Log.e("Percentage","  " + percentage);
            this.dialog.setProgress(percentage);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            if (loadMessage.getVisibility() == View.VISIBLE) {
                loadMessage.setVisibility(View.GONE);
            }

            //TextView textView = view.findViewById(R.id.resultgetaccesstoken);

            try {
                JSONArray jsonArray = new JSONArray(result);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                editor.putString(Utils.REMOTE_REGULAREXPRESSION, result);
                editor.apply();
                Log.e("FINISH parse", result);
            } catch (JSONException e) {
                e.printStackTrace();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                editor.putString(Utils.REMOTE_REGULAREXPRESSION, result);
                editor.apply();
                Log.e("FINISH parse", e.getMessage());
            }
            /*
            SharedPreferences.Editor editor = authPreferences.edit();
            editor.remove(Utils.USERNAME);
            editor.remove(Utils.PASSWORD);
            editor.remove(Utils.NAME);
            editor.remove(Utils.EMAIL);
            editor.remove(Utils.TENANT_ID);
            editor.apply();

             */

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
            String username = sharedPreferences.getString(Utils.USERNAME, null);
            String password = sharedPreferences.getString(Utils.PASSWORD, null);
            String name = sharedPreferences.getString(Utils.NAME, null);
            String email = sharedPreferences.getString(Utils.EMAIL, null);
            String tenantid = sharedPreferences.getString(Utils.TENANT_ID, null);
            if (username != null & password != null & name != null & email != null &tenantid != null){
                Intent intent = new Intent(getActivity().getApplicationContext(), Authenticated.class);
                // Check if we're running on Android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    //startActivity(intent);
                } else {
                    startActivity(intent);
                }
            }

            setListeners();
            Log.e("result", result);
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

    private class GetServiceValidity extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String access_token;
        private int statusCode = 0;

        public GetServiceValidity(Context context, ProgressBar dialog, String access_token) {
            this.context       = context;
            this.dialog        = dialog;
            this.access_token  = access_token;
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
                        try (InputStream caInput = getActivity().getAssets().open("mobilebiller.crt")) {
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
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);
            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_SERVICE_ACCESS, MODE_PRIVATE).edit();
                    JSONArray response = returnedResult.getJSONArray(Utils.RESPONSE);
                    editor.putString(Utils.SERVICE_ACCESS, response.toString());
                    editor.apply();
                }
                //}else {
                  //  textView.setText(returnedResult.getString("raison"));
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Bundle bundle = getActivity().getIntent().getExtras();
            long smsid = -1;
            if (bundle != null){
                smsid = bundle.getLong(Utils.SMS_ID, -1);
                getActivity().getIntent().getExtras().remove(Utils.SMS_ID);
            }

            Intent intent = new Intent(getActivity().getApplicationContext(), Authenticated.class);
            if (smsid != -1) {
                intent.putExtra(Utils.SMS_ID, smsid);
            }
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                getActivity().finish();
                //startActivity(intent);
            } else {
                startActivity(intent);
                getActivity().finish();
            }

            Log.e("result Service access", result);
        }
    }


}

/*
{
    "success": 1,
    "faillure": 0,
    "response": [
        {
            "id": 11,
            "serviceid": "61e857c0-cecd-11e8-bdce-49267d475577",
            "clientid": "e40394e0-d0a3-11e8-a37e-6b2692fae9e0",
            "tenantid": "57261bf0-d0a1-11e8-bd1f-47fec029a2ff",
            "startdate": 1539863364,
            "enddate": 1547639364,
            "enablementstatus": 1,
            "expirationstatus": 0,
            "reasonenablementchanged": null,
            "created_at": "2018-10-18 11:49:24",
            "updated_at": "2018-10-18 11:51:30"
        },
        {
            "id": 12,
            "serviceid": "f4adac70-ce2b-11e8-99bc-1b0e3dd244de",
            "clientid": "e40394e0-d0a3-11e8-a37e-6b2692fae9e0",
            "tenantid": "57261bf0-d0a1-11e8-bd1f-47fec029a2ff",
            "startdate": 1539863646,
            "enddate": 1547639646,
            "enablementstatus": 1,
            "expirationstatus": 0,
            "reasonenablementchanged": null,
            "created_at": "2018-10-18 11:54:06",
            "updated_at": "2018-10-18 11:54:06"
        }
    ]
}
 */
