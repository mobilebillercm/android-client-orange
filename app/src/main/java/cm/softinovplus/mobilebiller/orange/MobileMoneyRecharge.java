package cm.softinovplus.mobilebiller.orange;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cm.softinovplus.mobilebiller.orange.utils.Utils;

public class MobileMoneyRecharge extends AppCompatActivity {

    public static AppCompatActivity thisActivity;
    private TextView titre, account, mobilemoney_providername;
    private Button submit_mobilemoney_recharge;
    private ProgressBar mobilemoneyproviderloader;
    private SharedPreferences preferencesAuth, preferencesConfig;
    private EditText edit_phonenumber, edit_mobilemoneyholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_money_recharge);

        thisActivity = this;
        preferencesAuth = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
        preferencesConfig = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

        titre = findViewById(R.id.titre);
        account = findViewById(R.id.account);
        mobilemoney_providername = findViewById(R.id.mobilemoney_providername);

        submit_mobilemoney_recharge = findViewById(R.id.submit_mobilemoney_recharge);
        mobilemoneyproviderloader= findViewById(R.id.mobilemoneyproviderloader);

        edit_mobilemoneyholder = findViewById(R.id.edit_mobilemoneyholder);
        edit_phonenumber = findViewById(R.id.edit_phonenumber);

        titre.setText(preferencesAuth.getString(Utils.TENANT_NAME, Utils.DEFAULT_TENANT_NAME));
        account.setText(preferencesAuth.getString(Utils.EMAIL, "Error@Error.cm"));

        submit_mobilemoney_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MobileMoneyRecharge.class));
                Toast.makeText(getApplicationContext(), "En Cours d'Implementation", Toast.LENGTH_LONG).show();
            }
        });

        edit_phonenumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String phonenumber = ((EditText)v).getText().toString().trim();

                    if (phonenumber.length() == 9){
                        if ((phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '9') ||
                                (phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '5' && Integer.parseInt("" + phonenumber.charAt(2)) >= 5)){
                            mobilemoney_providername.setText("ORANGE CAMEROON");
                            mobilemoney_providername.setBackgroundColor(Color.rgb(255,127,80));
                            mobilemoney_providername.setTextColor(Color.BLACK);
                        }else if ((phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '7') ||
                                (phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '8') ||
                                (phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '5' && Integer.parseInt("" + phonenumber.charAt(2)) < 5)){
                            mobilemoney_providername.setText("MTN CAMEROON");
                            mobilemoney_providername.setBackgroundColor(Color.YELLOW);
                            mobilemoney_providername.setTextColor(Color.BLUE);
                        }else if ((phonenumber.charAt(0) == '6' && phonenumber.charAt(1) == '6')){
                            mobilemoney_providername.setText("NEXTTEL CAMEROON");
                            mobilemoney_providername.setBackgroundColor(Color.rgb(200, 0, 0));
                            mobilemoney_providername.setTextColor(Color.WHITE);
                        }
                    }
                }
            }
        });
    }
}
