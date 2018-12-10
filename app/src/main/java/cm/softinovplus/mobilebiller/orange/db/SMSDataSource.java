package cm.softinovplus.mobilebiller.orange.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cm.softinovplus.mobilebiller.orange.sms.SMS;
import cm.softinovplus.mobilebiller.orange.utils.Utils;

public class SMSDataSource {
	
	 private SQLiteDatabase database;
	 private MySQLiteHelper dbHelper;
	 private Context mContext;
	  private String[] allColumns = {
			  MySQLiteHelper.COLUMN_ID,
		      MySQLiteHelper.COLUMN_TRANSACTION_TYPE,
			  MySQLiteHelper.COLUMN_TRANSACTION_AMOUNT,
			  MySQLiteHelper.COLUMN_TRANSACTION_BENEFICIARY_NAME,
		      MySQLiteHelper.COLUMN_TRANSACTION_BENEFICIARY_ACCOUNT_NUMBER,
			  MySQLiteHelper.COLUMN_TRANSACTION_DATE,
		      MySQLiteHelper.COLUMN_TRANSACTION_ID,
			  MySQLiteHelper.COLUMN_TRANSACTION_REFERENCE,
			  MySQLiteHelper.COLUMN_TRANSACTION_FEES,
			  MySQLiteHelper.COLUMN_TRANSACTION_STATE,
			  MySQLiteHelper.COLUMN_TRANSACTION_BALANCE,
			  MySQLiteHelper.COLUMN_TRANSACTION_CURRENCY,
			  MySQLiteHelper.COLUMN_TRANSACTION_MADE_BY,
			  MySQLiteHelper.COLUMN_SMS_SENDER,
			  MySQLiteHelper.COLUMN_SMS_DATE,
			  MySQLiteHelper.COLUMN_SMS_BODY,
			  MySQLiteHelper.COLUMN_SMS_RECEIVER,
			  MySQLiteHelper.COLUMN_BELONGS_TO,
			  MySQLiteHelper.COLUMN_TENANT,
			  MySQLiteHelper.COLUMN_RECEIVED_AT,
			  MySQLiteHelper.COLUMN_IS_YET_PRINTED,
			  MySQLiteHelper.COLUMN_IS_ONLINE_SAVED,
			  MySQLiteHelper.COLUMN_EMAIL,
			  MySQLiteHelper.COLUMN_PHONE,
			  MySQLiteHelper.COLUMN_TAXPAYERNUMBER,
			  MySQLiteHelper.COLUMN_NUMBERTRADEREGISTER

	  };
	  
	  public SMSDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	    mContext = context;
	  }
	  
	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }
	  
	  public void close() {
	    dbHelper.close();
	  }
	  
	  public SMS createSMS(long id, String transaction_type, float transaction_amount, String transaction_beneficiary_name,
						   String transaction_beneficiary_account_number, String transaction_date, String transaction_id,
						   String transaction_reference, float transaction_fees, String transaction_state, float transaction_balance,
						   String transaction_currency, String transaction_made_by, String sms_sender, String sms_date, String sms_body, String sms_receiver, String belongs_to, String tenant,
						   long received_at, int is_yet_printed, int is_online_saved, String email, String phone, String tapayernumber, String numbertraderegister) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_ID, id);
	    values.put(MySQLiteHelper.COLUMN_TRANSACTION_TYPE, transaction_type);
	    values.put(MySQLiteHelper.COLUMN_TRANSACTION_AMOUNT, transaction_amount);
		values.put(MySQLiteHelper.COLUMN_TRANSACTION_BENEFICIARY_NAME, transaction_beneficiary_name);
	    values.put(MySQLiteHelper.COLUMN_TRANSACTION_BENEFICIARY_ACCOUNT_NUMBER, transaction_beneficiary_account_number);
	    values.put(MySQLiteHelper.COLUMN_TRANSACTION_DATE, transaction_date);
	    values.put(MySQLiteHelper.COLUMN_TRANSACTION_ID, transaction_id);
		values.put(MySQLiteHelper.COLUMN_TRANSACTION_REFERENCE, transaction_reference);
		  values.put(MySQLiteHelper.COLUMN_TRANSACTION_FEES, transaction_fees);
		  values.put(MySQLiteHelper.COLUMN_TRANSACTION_STATE, transaction_state);
		  values.put(MySQLiteHelper.COLUMN_TRANSACTION_BALANCE, transaction_balance);
		  values.put(MySQLiteHelper.COLUMN_TRANSACTION_CURRENCY, transaction_currency);
		  values.put(MySQLiteHelper.COLUMN_TRANSACTION_MADE_BY, transaction_made_by);
		  values.put(MySQLiteHelper.COLUMN_SMS_SENDER, sms_sender);
		  values.put(MySQLiteHelper.COLUMN_SMS_DATE, sms_date);
		  values.put(MySQLiteHelper.COLUMN_SMS_BODY, sms_body);
		  values.put(MySQLiteHelper.COLUMN_SMS_RECEIVER, sms_receiver);
		  values.put(MySQLiteHelper.COLUMN_BELONGS_TO, belongs_to);
		  values.put(MySQLiteHelper.COLUMN_TENANT, tenant);
		  values.put(MySQLiteHelper.COLUMN_RECEIVED_AT, received_at);
		  values.put(MySQLiteHelper.COLUMN_IS_YET_PRINTED, is_yet_printed);
		  values.put(MySQLiteHelper.COLUMN_IS_ONLINE_SAVED, is_online_saved);
		  values.put(MySQLiteHelper.COLUMN_EMAIL, email);
		  values.put(MySQLiteHelper.COLUMN_PHONE, phone);
		  values.put(MySQLiteHelper.COLUMN_TAXPAYERNUMBER, tapayernumber);
		  values.put(MySQLiteHelper.COLUMN_NUMBERTRADEREGISTER, numbertraderegister);
		  Log.e("Start Save SMS", sms_body);

          Cursor cursore = database.query(MySQLiteHelper.TABLE_SMS, allColumns, "1" , null, null, null, MySQLiteHelper.COLUMN_ID + " ");
          long size = cursore.getCount();
          if (size >= Utils.DATABASE_SIZE){
              Cursor curseur = database.query(MySQLiteHelper.TABLE_SMS, allColumns, "1" , null, null, null, MySQLiteHelper.COLUMN_ID + "  ", "1");
              curseur.moveToFirst();
              SMS sms = cursorToSMS(curseur);
              this.deleteSMS(sms);
          }

	    database.insert(MySQLiteHelper.TABLE_SMS, null,values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_SMS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
	    cursor.moveToFirst();
	    SMS newSMS = cursorToSMS(cursor);
	    cursor.close();
		  Log.e("End Save SMS", newSMS.getSms_body());

	    return newSMS;
	  }
	  
	  public boolean deleteSMS(SMS sms) {
		if(sms != null){
			long id = sms.getId();
		    int res = database.delete(MySQLiteHelper.TABLE_SMS, MySQLiteHelper.COLUMN_ID  + " = " + id , null);
		    if(res == 1){
				Toast.makeText(mContext, "SMS supprime avec succes", Toast.LENGTH_LONG).show();
				return  true;
			} else {
				Toast.makeText(mContext, "Ne peut etre supprime", Toast.LENGTH_LONG).show();
				return  false;
			}
		}
		return  false;
	  }

	public boolean deleteAllSMS() {
		int res = database.delete(MySQLiteHelper.TABLE_SMS, " 1 " , null);
		if(res == 1){
			Toast.makeText(mContext, "SMS supprime avec succes", Toast.LENGTH_LONG).show();
			return  true;
		} else {
			Toast.makeText(mContext, "Ne peut etre supprime", Toast.LENGTH_LONG).show();
			return  false;
		}
	}
	  
	  
	  public List<SMS> getAllSMS() {
	    List<SMS> smss = new ArrayList<SMS>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_SMS, allColumns, "1" , null, null, null, MySQLiteHelper.COLUMN_ID + " DESC");
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      SMS sms = cursorToSMS(cursor);
	      smss.add(sms);
	      cursor.moveToNext();
	    }
	    return  smss;
	  }
	  
	  private SMS cursorToSMS(Cursor cursor) {
	      SMS sms = new SMS();
	      sms.setId(cursor.getLong(0));
	      sms.setTransaction_type(cursor.getString(1));
	      sms.setTransaction_amount(cursor.getInt(2));
          sms.setTransaction_beneficiary_name(cursor.getString(3));
	      sms.setTransaction_beneficiary_account_number(cursor.getString(4));
	      sms.setTransaction_date(cursor.getString(5));
	      sms.setTransaction_id(cursor.getString(6));
		  sms.setTransaction_reference(cursor.getString(7));
		  sms.setTransaction_fees(cursor.getInt(8));
		  sms.setTransaction_state(cursor.getString(9));
		  sms.setTransaction_balance(cursor.getInt(10));
		  sms.setTransaction_currency(cursor.getString(11));
		  sms.setTransaction_made_by(cursor.getString(12));
		  sms.setSms_sender(cursor.getString(13));
		  sms.setSms_date(cursor.getString(14));
		  sms.setSms_body(cursor.getString(15));
		  sms.setSms_receiver(cursor.getString(16));
		  sms.setBelongs_to(cursor.getString(17));
		  sms.setTenant(cursor.getString(18));
		  sms.setReceived_at(cursor.getLong(19));
		  sms.setIs_yet_printed(cursor.getInt(20));
		  sms.setIs_online_saved(cursor.getInt(21));
		  sms.setEmail(cursor.getString(22));
		  sms.setPhone(cursor.getString(23));
		  sms.setTaxpayernumber(cursor.getString(24));
		  sms.setNumbertraderegister(cursor.getString(25));
		  Log.e("SMS FROM CURSOR", sms.toString());
	    return sms;
	  }

	public boolean updateSMS(long id, ContentValues values){
		String [] params = {"" + id};
		   int a  = database.update(MySQLiteHelper.TABLE_SMS, values, MySQLiteHelper.COLUMN_ID + "=?"  , params);

		Log.e("UPDATE RETURNED", "" + a);

		SMS sms = getSMSById(id);

		Log.e("SAVED SMS", sms.toString());

		   return (a==1);
	  }

	public SMS getSMSById(long id){
		String args[] = {""+id};
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SMS, allColumns, MySQLiteHelper.COLUMN_ID + "=?" , args, null, null, null);
		long size = cursor.getCount();
		if (size != 1){
			cursor.close();
			return null;
		}
		cursor.moveToFirst();
		SMS newSMS = cursorToSMS(cursor);
		cursor.close();
		return newSMS;
	}

	public List<SMS>  getSMSNotSaved(){
		String args[] = {"" + 0};
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SMS, allColumns, MySQLiteHelper.COLUMN_IS_ONLINE_SAVED + "=?" , args, null, null, null);
		List<SMS> smss = new ArrayList<>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SMS sms = cursorToSMS(cursor);
			smss.add(sms);
			cursor.moveToNext();
		}
		return  smss;
	}

}
