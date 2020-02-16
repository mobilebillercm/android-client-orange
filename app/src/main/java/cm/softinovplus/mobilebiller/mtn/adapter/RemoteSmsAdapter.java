package cm.softinovplus.mobilebiller.mtn.adapter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cm.softinovplus.mobilebiller.mtn.BluetoothPrinterActivity;
import cm.softinovplus.mobilebiller.mtn.R;
import cm.softinovplus.mobilebiller.mtn.TicketToShareActivity;
import cm.softinovplus.mobilebiller.mtn.sms.SMS;
import cm.softinovplus.mobilebiller.mtn.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

//679542831 DIRELLE  pK8

/**
 * Created by nkalla on 17/12/18.
 */

public class RemoteSmsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public final int TYPE_SMS = 0;
    public final int TYPE_LOAD = 1;

    private static Context context;
    private List<SMS> values;
    private List<SMS> smsFiltered;
    private OnLoadMoreListener loadMoreListener;

    private  boolean isLoading = false, isMoreDataAvailable = true;

    public RemoteSmsAdapter(Context context, List<SMS> values, List<SMS> smsFiltered) {
        this.context = context;
        this.values = values;
        this.smsFiltered = smsFiltered;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        if(i==TYPE_SMS){
            viewHolder =  new RemoteSMSHolder(inflater.inflate(R.layout.remote_sms_layout,viewGroup,false));
        }else{
            viewHolder =  new LoadHolder(inflater.inflate(R.layout.load_layout,viewGroup,false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //Log.e("condition", "i=" + i + ", size=" + getItemCount() + ", isMoreDataAvailable=" + isMoreDataAvailable + ", isloading="  + isLoading + ", loadMoreListener=" + loadMoreListener.toString());
        if(i>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            //Log.e("END_LIST", " " + i +", " + "calling load onLoadMore");
            isLoading = true;
            loadMoreListener.onLoadMore();

        }

        if(getItemViewType(i)==TYPE_SMS){
            SMS sms = values.get(i);
            ((RemoteSMSHolder)viewHolder).bindData(sms);


        }
        //No else part needed as load holder doesn't bind any data
        else{
            Log.e("condition", "i=" + i + ", size=" + getItemCount() + ", isMoreDataAvailable=" + isMoreDataAvailable);
            if (!isMoreDataAvailable && i >= getItemCount() - 1 ){
                LoadHolder loadHolder = (LoadHolder) viewHolder;
                loadHolder.hideLoader();
            }
        }



    }

    @Override
    public int getItemViewType(int position) {
        if(values.get(position) != null){
            return TYPE_SMS;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.e("WASSENT", charString);
                //List<SMS> filteredList = new ArrayList<>();
                smsFiltered = new ArrayList<>();

                if (charString.isEmpty()) {
                    smsFiltered = values;
                    Log.e("performFiltering0", smsFiltered.toString());
                } else {
                    for (SMS row : values) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSms_body().toLowerCase().contains(charString.toLowerCase())) {
                            Log.e("TROVATO", row.getSms_body());
                            smsFiltered.add(row);
                        }
                    }

                    Log.e("performFiltering", "aaaaaaaaaaaaa" + smsFiltered.toString());
                    //smsFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = smsFiltered;
                Log.e("WASSENT3", "bbbbbbbbbbbb" + values.toString() + "/////" + smsFiltered.toString() );
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //Log.e("WASSENT2", charSequence.toString());
                //smsFiltered = (ArrayList<SMS>) filterResults.values;
                Log.e("WASSENT4", "cccccccccc" + smsFiltered.toString());
                notifyDataSetChanged();
            }
        };
    }

    public interface SMSAdapterListener {
        void onSmsSelected(SMS sms);
    }


    private static class RemoteSMSHolder extends RecyclerView.ViewHolder{
        TextView madeby;
        TextView beneficiary;
        TextView body;
        Button imprimer_btn, partager_btn;
        ProgressBar print_loader;

        public RemoteSMSHolder(View itemView) {
            super(itemView);
            madeby=(TextView)itemView.findViewById(R.id.madeby);
            beneficiary=(TextView)itemView.findViewById(R.id.beneficiary);
            body=(TextView)itemView.findViewById(R.id.body);
            imprimer_btn = itemView.findViewById(R.id.imprimer_btn);
            partager_btn = itemView.findViewById(R.id.partager_btn);
            print_loader = itemView.findViewById(R.id.print_loader);

        }

        void bindData(final SMS sms){
            madeby.setText(sms.getTransaction_made_by());
            beneficiary.setText(""+sms.getTransaction_beneficiary_name() + "  " + sms.getTransaction_beneficiary_account_number());
            body.setText(sms.getSms_body());
            imprimer_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("ITEMCLICKED", sms.getTransaction_beneficiary_name());
                    if (sms != null){

                        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
                        String macaddress = sharedPreferences.getString(Utils.DEFAULT_MAC_ADDRESS, null);
                        Log.e("macaddress", "macaddress: " + macaddress);
                        if (macaddress != null){
                            Log.e("macaddress", "macaddress1: " + macaddress);
                            BluetoothAdapter G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (G_bluetoothAdapter == null) {
                                Toast.makeText(context.getApplicationContext(), "Pas de Bluetooth Configure. Veuiller configurer Dispositif", Toast.LENGTH_SHORT).show();
                                /*Intent intent = new Intent(context, BluetoothPrinterActivity.class);
                                SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                                editor.putLong(Utils.LAST_SMS_ID, sms.getId());
                                editor.apply();
                                context.startActivity(intent);*/
                            } else {
                                if (!G_bluetoothAdapter.isEnabled()) {
                                    G_bluetoothAdapter.enable();
                                }
                                Set<BluetoothDevice> G_devices = G_bluetoothAdapter.getBondedDevices();
                                Object []devices = G_devices.toArray();
                                BluetoothDevice choosenDevice = null;
                                for (int i=0; i<devices.length; i++){
                                    BluetoothDevice device = (BluetoothDevice) devices[i];
                                    if (device.getAddress().equals(macaddress)){
                                        choosenDevice = device;
                                        break;
                                    }
                                }
                                if (choosenDevice != null){
                                    SharedPreferences sharedPreferences_access_service = context.getSharedPreferences(Utils.APP_SERVICE_ACCESS, MODE_PRIVATE);
                                    boolean serviceValidity = false;
                                    boolean serviceFound = false;
                                    try {
                                        String accesses = sharedPreferences_access_service.getString(Utils.SERVICE_ACCESS,"");
                                        Log.e("ACCESSES", accesses);
                                        JSONArray jsonArray = new JSONArray(accesses);
                                        for (int i= 0; i<jsonArray.length(); i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            if (jsonObject.getString(Utils.serviceid).equals(Utils.PRINT_SERVICE_ID)){
                                                long startdate = jsonObject.getLong("startdate");
                                                long enddate   = jsonObject.getLong("enddate");
                                                long currentdate = jsonObject.getLong("currentdate");
                                                if (startdate <= currentdate && currentdate <= enddate){
                                                    serviceValidity = true;
                                                }
                                                serviceFound = true;
                                                break;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(context.getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if (!serviceFound || !serviceValidity){
                                        Toast.makeText(context.getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    /*SMSDataSource dataSource = new SMSDataSource(context.getApplicationContext());
                                    dataSource.open();
                                    SMS datasourcesms = dataSource.getSMSById(sms.getId());
                                    dataSource.close();*/

                                    //ProgressBar progressBar = findViewById(R.id.print_loader);
                                    BluetoothPrinterActivity.MyAsyncTask mat = new BluetoothPrinterActivity.MyAsyncTask(context, choosenDevice, sms, print_loader);
                                    mat.execute("");
                                }else {
                                    Toast.makeText(context.getApplicationContext(), "No bluetooth choosen", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else{
                            Log.e("macaddress", "macaddress3: " + macaddress);
                            Toast.makeText(context.getApplicationContext(), "Pas de Bluetooth. Veuillez configurer un dispositif", Toast.LENGTH_SHORT).show();
                            /*Intent intent = new Intent(context, BluetoothPrinterActivity.class);
                            SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                            editor.putLong(Utils.LAST_SMS_ID, sms.getId());
                            editor.apply();
                            context.startActivity(intent);*/
                        }

                    }
                }
            });

            partager_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TicketToShareActivity.class);
                    intent.putExtra(Utils.SMS, (new Gson()).toJson(sms));
                    context.startActivity(intent);
                }
            });
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{
        TextView loadingText;
        ProgressBar loading;
        public LoadHolder(View itemView) {
            super(itemView);
            loadingText = itemView.findViewById(R.id.loading);
            loading = itemView.findViewById(R.id.load_more);
        }

        public void hideLoader(){
            this.loading.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }


    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        /*if (!isMoreDataAvailable){

        }*/
        notifyDataSetChanged();
        isLoading = false;

    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


}
