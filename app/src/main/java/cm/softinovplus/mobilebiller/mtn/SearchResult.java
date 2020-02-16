package cm.softinovplus.mobilebiller.mtn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.softinovplus.mobilebiller.mtn.adapter.RemoteSmsAdapter;
import cm.softinovplus.mobilebiller.mtn.adapter.VerticalLineDecorator;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

public class SearchResult extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RemoteSmsAdapter adapter;
    private List<SMS> smses ;
    public static AppCompatActivity thisActivity;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        thisActivity = this;
        recyclerView =  findViewById(R.id.remote_sms_list);
        mLayoutManager = new LinearLayoutManager(this);

        smses = new ArrayList<>();

        JSONArray response = Utils.SEARCH_RESULT_RESPONSE;
        //List<SMS> smsList = new ArrayList<>();
        for (int i = 0; i<response.length(); i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = response.getJSONObject(i);

            SMS sms = parse(jsonObject.getString("body"));
            if (sms != null){
                smses.add(sms);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new RemoteSmsAdapter(this, smses, smses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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

            Log.e("VOILAAAAAAAAAAAAA", "VOILAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
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
