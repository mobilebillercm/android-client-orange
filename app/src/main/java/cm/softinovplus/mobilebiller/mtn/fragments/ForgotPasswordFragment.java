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

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.utils.CustomToast;
import cm.softinovplus.mobilebiller.mtn.utils.Tenant;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

import static cm.softinovplus.mobilebiller.mtn.utils.Utils.SignUpFragment;

public class ForgotPasswordFragment extends Fragment implements OnClickListener {

	private static View view;
	private static EditText emailId;
	private static Button submit, back;
    private LinearLayout form;
	private static FragmentManager fragmentManager;
	private static TextView createAccount;
	private String tenantName;
	private String tenantId;
	private AppCompatSpinner spinner_tenant;
	private LinearLayout tenant_layout;
	private List<Tenant> tenants;
	private ArrayAdapter<String> arrayAdapter;
    private Handler handler = new Handler();

    private Runnable postToServerRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO: PUT CODE HERE TO HANDLE CURRENT VALUE OF EDIT TEXT AND SEND TO SERVER

            // Check patter for email id
            String email = emailId.getText().toString();
            if (email.length() > 0){
                Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
                Matcher m = p.matcher(email);
                if (!m.matches()) {
                    new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
                }else{
                    Toast.makeText(getActivity(), "Getting Tenant", Toast.LENGTH_LONG).show();
                    ProgressBar progressBarGetTenant = view.findViewById(R.id.progressBarGetTenant);
                    GetTenant getTenant = new GetTenant(getActivity(), progressBarGetTenant);
                    getTenant.execute(Utils.makeUrlUserTenants(email));
                }
            }

        }
    };

	public ForgotPasswordFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.forgotpassword_layout, container,
				false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize the views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
		emailId = (EditText) view.findViewById(R.id.registered_emailid);
		submit = (Button) view.findViewById(R.id.forgot_button);
		//submit.setEnabled(false);
		createAccount = view.findViewById(R.id.createAccount);
		spinner_tenant = view.findViewById(R.id.spinner_tenant);
		tenant_layout = view.findViewById(R.id.tenant_layout);
        form = view.findViewById(R.id.form);


		//back = (TextView) view.findViewById(R.id.backToLoginBtn);

		// Setting text selector over textviews
		/*XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			back.setTextColor(csl);
			submit.setTextColor(csl);

		} catch (Exception e) {
		}*/

	}

	// Set Listeners over buttons
	private void setListeners() {
		//back.setOnClickListener(this);
		submit.setOnClickListener(this);
		createAccount.setOnClickListener(this);

		emailId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
							getTenant.execute(Utils.makeUrlUserTenants(emailId.getText().toString()));
						}
					}

				}

			}
		});

        emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // remove existing callback (timer reset)
                handler.removeCallbacks(postToServerRunnable);
                // 500 millisecond delay. Change to whatever delay you want.
                handler.postDelayed(postToServerRunnable, 2000);
            }
        });

        /*form.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
            }
        });*/

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createAccount:

			// Replace Login Fragment on Back Presses
			//new Welcome().replaceLoginFragment();
			//break;
			// Replace signup frgament with animation
			fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer, new SignUpFragment(), SignUpFragment).commit();
			break;

		case R.id.forgot_button:
			// Call Submit button task
			submitButtonTask();
			break;

		}

	}

	private void submitButtonTask() {
		String getEmailId = emailId.getText().toString();
		// Pattern for email id validation
		Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
		// Match the pattern
		Matcher m = p.matcher(getEmailId);
		// First check if email id is not null else show error toast
		if (getEmailId.equals("") || getEmailId.length() == 0){
			new CustomToast().Show_Toast(getActivity(), view, "Please enter your Email Id.");
		}
		// Check if email id is valid or not
		else if (!m.find()){
			new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
		}
		// Else submit email id and fetch passwod or do your stuff
		else{
			Toast.makeText(getActivity(), "Get Forgot Password.", Toast.LENGTH_SHORT).show();
			ProgressBar passordreset_loader = view.findViewById(R.id.passordreset_loader);
			DoRequestResetPassword doRequestResetPassword = new DoRequestResetPassword(getActivity(), passordreset_loader, tenantId);
			doRequestResetPassword.execute(Utils.HOST_IDENTITY_AND_ACCESS + "api/users/" + getEmailId + "/password-reset-request");
		}
	}


	private class DoRequestResetPassword extends AsyncTask<String, Integer, String> {
		private ProgressBar dialog;
		private Context context;
		private String tenantid;
		private int statusCode = 0;

		public DoRequestResetPassword(Context context, ProgressBar dialog, String tenantid) {
			this.context = context;
			this.dialog = dialog;
			this.tenantid = tenantid;
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
					//Log.e("ACCESSTOKEN", this.token.getString(Utils.ACCESS_TOKEN));
					//urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token.getString(Utils.ACCESS_TOKEN));
					//urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    /*JSONObject body = new JSONObject();
                    body.put(Utils.EMAIL, this.username);
                    body.put(Utils.PASSWORD, this.pwd);
                    body.put(Utils.TENANT_NAME, tenantName);
                    String query = body.toString();*///"email=" + this.username + "&password=" + this.pwd;
					String query =  "tenantid=" + this.tenantid;
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

			TextView textView = view.findViewById(R.id.result_reset_password);
			try {
				JSONObject returnedResult = new JSONObject(result);
				if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){
					textView.setText(returnedResult.getString(Utils.RESPONSE));
					textView.setTextColor(Color.GREEN);

				}else {
					textView.setText(returnedResult.getString("raison"));
					textView.setTextColor(Color.RED);
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
				}				try {
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