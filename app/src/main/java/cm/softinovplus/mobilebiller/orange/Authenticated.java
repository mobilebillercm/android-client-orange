package cm.softinovplus.mobilebiller.orange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.orange.dialog.LogoutDialog;
import cm.softinovplus.mobilebiller.orange.fragments.ForgotPasswordFragment;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

public class Authenticated extends AppCompatActivity {

    private TextView usernameView, nom_entreprise;
    //private   Bundle token;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Button btn_goto_bluetooth_devices, btn_goto_bluetooth_sms;
    private ImageView menu_bar;
    private CircularImageView logo, drawer_menu_header_photo;
    private View header;
    SharedPreferences authPreferences;

    private static SharedPreferences settings;

    private ProgressBar logout_loader;
    public static int mStackLevel;


    //private Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);
        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

        usernameView =  findViewById(R.id.username);
        nom_entreprise = findViewById(R.id.nom_entreprise);

        usernameView.setText(authPreferences.getString(Utils.EMAIL,"Error@Error.com"));
        nom_entreprise.setText(authPreferences.getString(Utils.TENANT_NAME, Utils.DEFAULT_TENANT_NAME));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        menu_bar = (ImageView) findViewById(R.id.menu_bar);

        logo = (CircularImageView) findViewById(R.id.logo);
        logout_loader = findViewById(R.id.logout_loader);

        settings = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.deconnexion) {
                    mDrawerLayout.closeDrawers();
                    DoLogout doLogout = new DoLogout(getApplicationContext(), logout_loader, authPreferences.getString(Utils.EMAIL,""),
                            authPreferences.getString(Utils.TENANT_ID, ""), authPreferences.getString(Utils.ACCESS_TOKEN, ""));
                    doLogout.execute(Utils.HOST_IDENTITY_AND_ACCESS + "api/lougout-user/" + authPreferences.getString(Utils.EMAIL,"") + "/" + authPreferences.getString(Utils.TENANT_ID,""));
                }

                if (menuItem.getItemId() == R.id.changermdp) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.inviter) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), InviteUser.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.mestransactions) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), SMSsActivity.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.setprinter) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), DefaulPrinterConfigActivity.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.recharge_my_account) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), RechargeAccountMenu.class);
                    startActivity(intent);
                }

                /*if (menuItem.getItemId() == R.id.register) {
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), Signup.class);
                    startActivity(intent);
                }*/

                return false;
            }

        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, null, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Menu menu = mNavigationView.getMenu();
                int size = menu.size();
                for (int i = 0 ; i<size; i++){
                    MenuItem item = menu.getItem(i);
                    item.setVisible(true);
                }
                mNavigationView.refreshDrawableState();
                super.onDrawerSlide(drawerView, slideOffset);
            }
            @Override
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(R.string.titre);
                Log.e("Close Drawer", "fermeture du drawer");
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(R.string.titre_apres_ouverture);
                Log.e("Open Drawer", "ouverture du drawer");
                invalidateOptionsMenu();
            }
        };

        header = mNavigationView.getHeaderView(0);
        TextView connected_user_name = (TextView) header.findViewById(R.id.connected_user_name);
        drawer_menu_header_photo = header.findViewById(R.id.drawer_menu_header_photo);
        TextView tenant_name_tv = header.findViewById(R.id.tenant_name);
        tenant_name_tv.setText(authPreferences.getString(Utils.TENANT_NAME, Utils.DEFAULT_TENANT_NAME).toUpperCase());
        //String string = "" + settings.getString(Utils.NAME, "") + " (" + settings.getString(Utils.EMAIL, "") + ")"
         //       + "\n" + settings.getString(Utils.LIBELE_PDV, "");
        connected_user_name.setText(authPreferences.getString(Utils.EMAIL,"Error@Error.com"));

        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);



        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(DrawerLayout.LOCK_MODE_LOCKED_OPEN)){
                    mDrawerLayout.closeDrawer(R.id.drawer_layout);
                }else {
                    mDrawerLayout.openDrawer(R.id.drawer_layout);
                }
            }
        });*/

        menu_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        hideKeyboard();

        btn_goto_bluetooth_devices = findViewById(R.id.btn_goto_bluetooth_devices);
        btn_goto_bluetooth_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BluetoothPrinterActivity.class);
                startActivity(intent);
            }
        });

        btn_goto_bluetooth_sms = findViewById(R.id.btn_goto_bluetooth_sms);
        btn_goto_bluetooth_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SMSsActivity.class);
                startActivity(intent);
            }
        });

        //        public GetAccessTokenForService(Context context, int clientId, String clienSecret, String grantType, ProgressBar progressBar) {


        BeginGetServices beginGetServices = new BeginGetServices(this, Utils.CLIENT_CLIENT_ID,
                Utils.CLIENT_CLIENT_SECRET, Utils.CLIENT_GRANT_TYPE, (ProgressBar) findViewById(R.id.id_image_loader));
        beginGetServices.execute(Utils.CLIENT_ACCESS_TOKEN_END_POINT);

        DownloadImageTask downloadImageTask = new DownloadImageTask(logo, (ProgressBar) findViewById(R.id.id_image_loader));
        downloadImageTask.execute(Utils.makeTenantLogoUrl(authPreferences.getString(Utils.TENANT_ID, "")));

    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircularImageView bmImage;
        ProgressBar progressBar;
        private int statusCode;

        public DownloadImageTask(CircularImageView bmImage, ProgressBar progressBar) {
            this.bmImage = bmImage;
            this.progressBar = progressBar;//findViewById(R.id.id_image_loader);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressBar.setVisibility(View.VISIBLE);
            this.bmImage.setVisibility(View.GONE);
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String resultat = "";
            String str_url = strings[0];
            Bitmap bmp = null;
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
                    Log.e("TENANT URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + authPreferences.getString(Utils.ACCESS_TOKEN, ""));
                    this.statusCode = urlConnection.getResponseCode();
                    Log.e("statusCode", "4: " + statusCode);
                    InputStream in = urlConnection.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "something Went wrong");
                    } catch (JSONException e1) {

                    }

                    return bmp;
                }
            } catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return bmp;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return bmp;
            }

            return bmp;
        }

        /*@Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();

                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }*/
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null){
                bmImage.setVisibility(View.VISIBLE);
                bmImage.setImageBitmap(result);
                drawer_menu_header_photo.setImageBitmap(result);
            }

            progressBar.setVisibility(View.GONE);


        }
    }

    @Override
    public void onBackPressed() {
       /* Fragment SignUpFragment = fragmentManager.findFragmentByTag(Utils.SignUpFragment);
        Fragment ForgotPasswordFragment = fragmentManager.findFragmentByTag(Utils.ForgotPasswordFragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUpFragment != null)
            replaceLoginFragment();
        else if (ForgotPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();*/
        showlogoutDialog();
    }

    public void showlogoutDialog(){
        mStackLevel++;

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DoLogout doLogout = new DoLogout(getApplicationContext(), logout_loader, authPreferences.getString(Utils.EMAIL,""),
                authPreferences.getString(Utils.TENANT_ID, ""), authPreferences.getString(Utils.ACCESS_TOKEN, ""));

        LogoutDialog logoutDialog = LogoutDialog.newInstance(doLogout, mStackLevel);

        logoutDialog.show(ft, "dialog");

    }

    public class DoLogout extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String username;
        private String tenant;
        private String token;
        private int statusCode = 0;

        public DoLogout(Context context, ProgressBar dialog, String username, String tenant, String token) {
            this.context = context;
            this.dialog = dialog;
            this.username = username;
            this.tenant = tenant;
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
                }                 try {
                    Log.e("URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.e("ACCESSTOKEN", this.token);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token);
                    //urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    //JSONObject body = new JSONObject();
                    //body.put(Utils.EMAIL, this.username);
                    String query = ""; //body.toString();//"email=" + this.username + "&password=" + this.pwd;
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

            SharedPreferences.Editor editor = authPreferences.edit();
            editor.remove(Utils.USERNAME);
            editor.remove(Utils.PASSWORD);
            editor.remove(Utils.NAME);
            editor.remove(Utils.EMAIL);
            editor.remove(Utils.TENANT_ID);
            editor.apply();

            finish();

            Log.e("result", result);
        }
    }




    private class GetServices extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String userid;
        private String access_token;
        private int statusCode = 0;

        public GetServices(Context context, ProgressBar dialog, String userid, String access_token) {
            this.context = context;
            this.userid = userid;
            this.dialog = dialog;
            this.access_token = access_token;
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
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.access_token);

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success") && jsonObject.has("faillure") && jsonObject.getInt("success") == 1 && jsonObject.getInt("faillure") == 0 ){
                    JSONArray jsonArray = jsonObject.getJSONArray(Utils.RESPONSE);
                    LinearLayout listeservices = findViewById(R.id.listeservices);
                    if (jsonArray.length() > 0){
                        for (int i = 0; i<jsonArray.length(); i++){
                            TextView serviceName = new TextView(Authenticated.this);
                            serviceName.setTextColor(Color.rgb(100, 0, 0));
                            serviceName.setText(jsonArray.getJSONObject(i).getString("name") + "      ");

                            TextView serviceDelay = new TextView(Authenticated.this);
                            serviceDelay.setTextColor(Color.rgb(100, 0, 0));
                            serviceDelay.setText(Utils.makeDateDate(jsonArray.getJSONObject(i).getLong("enddate")) + "      ");

                            LinearLayout myLinearLayout = new LinearLayout(Authenticated.this);
                            myLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                            myLinearLayout.addView(serviceDelay, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            myLinearLayout.addView(serviceName, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            listeservices.addView(myLinearLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                    }else {
                        TextView noservicefound = new TextView(Authenticated.this);
                        noservicefound.setTextColor(Color.rgb(100, 0, 0));
                        noservicefound.setText("Vous n'avez souscrit a aucun service !!!");
                        LinearLayout myLinearLayout = new LinearLayout(Authenticated.this);
                        myLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                        myLinearLayout.addView(noservicefound, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        listeservices.addView(myLinearLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    }


                }else{
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("SERVICES ", result);
        }
    }

    private class BeginGetServices extends AsyncTask<String, Integer, String> {
        private int clientId;
        private String clienSecret;
        private String grantType;
        private Context context;
        private int statusCode = 0;
        private ProgressBar dialog;

        public BeginGetServices(Context context, int clientId, String clienSecret, String grantType, ProgressBar progressBar) {
            this.context = context;
            this.clientId = clientId;
            this.clienSecret = clienSecret;
            this.grantType = grantType;
            this.dialog = progressBar;
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
                }
                try {
                    Log.e("query", str_url);

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
                    resultat = sb.toString();

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect       \n\n" + e.getMessage());
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
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Utils.ERROR)){
                }else if (jsonObject.has(Utils.ACCESS_TOKEN) && jsonObject.has(Utils.TOKEN_TYPE) && jsonObject.has(Utils.EXPIRES_IN) && jsonObject.getInt(Utils.EXPIRES_IN) > 0){
                    SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE).edit();
                    editor.putString(Utils.CLIENT_ACCESS_TOKEN, jsonObject.getString(Utils.ACCESS_TOKEN));
                    editor.apply();
                    GetServices getServices = new GetServices(Authenticated.this, (ProgressBar) findViewById(R.id.id_image_loader), authPreferences.getString(Utils.USERID, ""),
                            jsonObject.getString(Utils.ACCESS_TOKEN));
                    getServices.execute(Utils.HOST_CLIENT + "api/services/" + authPreferences.getString(Utils.USERID,""));
                    //        public GetServices(Context context, ProgressBar dialog, String email, String access_token) {

                    /*RechargeAccountMenu.GetBalance getBalance = new RechargeAccountMenu.GetBalance(RechargeAccountMenu.this, balanceloader,
                            preferencesAuth.getString(Utils.EMAIL, "Error@Error.cm"), jsonObject.getString(Utils.ACCESS_TOKEN));
                    getBalance.execute(Utils.HOST_WALLET + "api/mobilebillercreditaccounts/" + preferencesAuth.getString(Utils.EMAIL,"") + "?query=balance");*/
                }else{
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
