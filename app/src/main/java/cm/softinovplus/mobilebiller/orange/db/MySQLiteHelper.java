package cm.softinovplus.mobilebiller.orange.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	public static final String TABLE_SMS = "sms";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    public static final String COLUMN_TRANSACTION_AMOUNT = "transaction_amount";
    public static final String COLUMN_TRANSACTION_BENEFICIARY_NAME = "transaction_beneficiary_name";
    public static final String COLUMN_TRANSACTION_BENEFICIARY_ACCOUNT_NUMBER = "transaction_beneficiary_account_number";
    public static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_TRANSACTION_REFERENCE = "transaction_reference";
    public static final String COLUMN_TRANSACTION_FEES = "transaction_fees";
    public static final String COLUMN_TRANSACTION_STATE = "transaction_state";
    public static final String COLUMN_TRANSACTION_BALANCE = "transaction_balance";
    public static final String COLUMN_TRANSACTION_CURRENCY = "transaction_currency";
	public static final String COLUMN_TRANSACTION_MADE_BY = "transaction_made_by";
	public static final String COLUMN_SMS_SENDER = "sms_sender";
    public static final String COLUMN_SMS_DATE = "sms_date";
    public static final String COLUMN_SMS_BODY = "sms_body";
    public static final String COLUMN_SMS_RECEIVER = "sms_receiver";
	public static final String COLUMN_BELONGS_TO = "belongs_to";
	public static final String COLUMN_TENANT = "tenant";
	public static final String COLUMN_RECEIVED_AT = "received_at";
	public static final String COLUMN_IS_YET_PRINTED = "is_yet_printed";
	public static final String COLUMN_IS_ONLINE_SAVED = "is_online_saved";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_TAXPAYERNUMBER = "taxpayernumber";
	public static final String COLUMN_NUMBERTRADEREGISTER = "numbertraderegister";

	public static final String DATABASE_NAME = "sms.db";
	public static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table " + TABLE_SMS + "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_TRANSACTION_TYPE + " text, " +
            COLUMN_TRANSACTION_AMOUNT + " real, " +
            COLUMN_TRANSACTION_BENEFICIARY_NAME + " text, " +
            COLUMN_TRANSACTION_BENEFICIARY_ACCOUNT_NUMBER + " text, " +
            COLUMN_TRANSACTION_DATE  + " text, " +
            COLUMN_TRANSACTION_ID + " text, " +
            COLUMN_TRANSACTION_REFERENCE + " text, " +
            COLUMN_TRANSACTION_FEES + " real, " +
            COLUMN_TRANSACTION_STATE + " text, " +
            COLUMN_TRANSACTION_BALANCE + " real, " +
            COLUMN_TRANSACTION_CURRENCY + " text, " +
			COLUMN_TRANSACTION_MADE_BY + " text, " +
            COLUMN_SMS_SENDER + " text, " +
            COLUMN_SMS_DATE + " text, " +
            COLUMN_SMS_BODY + " text, " +
            COLUMN_SMS_RECEIVER + " text, " +
            COLUMN_BELONGS_TO + " text, " +
            COLUMN_TENANT + " text, " +
            COLUMN_RECEIVED_AT + " integer, " +
            COLUMN_IS_YET_PRINTED + " integer, " +
			COLUMN_IS_ONLINE_SAVED + " integer, " +
			COLUMN_EMAIL + " text, " +
			COLUMN_PHONE + " text, " +
			COLUMN_TAXPAYERNUMBER + " text, " +
			COLUMN_NUMBERTRADEREGISTER + " text" +
            ")";

	 public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	 
	 @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }
	 
	 @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
	    onCreate(db);
	  }
	
}
