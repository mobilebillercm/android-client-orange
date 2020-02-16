package cm.softinovplus.mobilebiller.mtn.sms;

public class SMS {

	private long id, received_at;
	private String transaction_type, transaction_beneficiary_name, transaction_beneficiary_account_number,
			transaction_date, transaction_id, transaction_reference, transaction_state,
			transaction_currency, transaction_made_by, sms_sender, sms_date, sms_body, sms_receiver, belongs_to, tenant, email, phone, taxpayernumber, numbertraderegister;
	private int is_yet_printed, is_online_saved;
	private float transaction_amount, transaction_balance, transaction_fees;
	public SMS(){}

	public SMS(long id, String transaction_type, float transaction_amount, String transaction_beneficiary_name,
			   String transaction_beneficiary_account_number, String transaction_date, String transaction_id,
			   String transaction_reference, float transaction_fees, String transaction_state, float transaction_balance,
			   String transaction_currency, String transaction_made_by, String sms_sender, String sms_date, String sms_body, String sms_receiver, String belongs_to, String tenant,
			   long received_at, int is_yet_printed, int is_online_saved,  String email, String phone, String taxpayernumber, String numbertraderegister) {
		this.id = id;
		this.received_at = received_at;
		this.transaction_type = transaction_type;
		this.transaction_beneficiary_name = transaction_beneficiary_name;
		this.transaction_beneficiary_account_number = transaction_beneficiary_account_number;
		this.transaction_date = transaction_date;
		this.transaction_id = transaction_id;
		this.transaction_reference = transaction_reference;
		this.transaction_fees = transaction_fees;
		this.transaction_state = transaction_state;
		this.transaction_currency = transaction_currency;
		this.transaction_made_by = transaction_made_by;
		this.sms_sender = sms_sender;
		this.sms_date = sms_date;
		this.sms_body = sms_body;
		this.sms_receiver = sms_receiver;
		this.belongs_to = belongs_to;
		this.tenant = tenant;
		this.transaction_amount = transaction_amount;
		this.transaction_balance = transaction_balance;
		this.is_yet_printed = is_yet_printed;
		this.is_online_saved = is_online_saved;
		this.email= email;
		this.phone = phone;
		this.taxpayernumber = taxpayernumber;
		this.numbertraderegister = numbertraderegister;

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getReceived_at() {
		return received_at;
	}

	public void setReceived_at(long received_at) {
		this.received_at = received_at;
	}

	public String getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	public String getTransaction_beneficiary_name() {
		return transaction_beneficiary_name;
	}

	public void setTransaction_beneficiary_name(String transaction_beneficiary_name) {
		this.transaction_beneficiary_name = transaction_beneficiary_name;
	}

	public String getTransaction_beneficiary_account_number() {
		return transaction_beneficiary_account_number;
	}

	public void setTransaction_beneficiary_account_number(String transaction_beneficiary_account_number) {
		this.transaction_beneficiary_account_number = transaction_beneficiary_account_number;
	}

	public String getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getTransaction_reference() {
		return transaction_reference;
	}

	public void setTransaction_reference(String transaction_reference) {
		this.transaction_reference = transaction_reference;
	}

	public float getTransaction_fees() {
		return transaction_fees;
	}

	public void setTransaction_fees(float transaction_fees) {
		this.transaction_fees = transaction_fees;
	}

	public String getTransaction_state() {
		return transaction_state;
	}

	public void setTransaction_state(String transaction_state) {
		this.transaction_state = transaction_state;
	}

	public String getTransaction_currency() {
		return transaction_currency;
	}

	public void setTransaction_currency(String transaction_currency) {
		this.transaction_currency = transaction_currency;
	}

	public String getSms_sender() {
		return sms_sender;
	}

	public void setSms_sender(String sms_sender) {
		this.sms_sender = sms_sender;
	}

	public String getSms_date() {
		return sms_date;
	}

	public void setSms_date(String sms_date) {
		this.sms_date = sms_date;
	}

	public String getSms_body() {
		return sms_body;
	}

	public void setSms_body(String sms_body) {
		this.sms_body = sms_body;
	}

	public String getSms_receiver() {
		return sms_receiver;
	}

	public void setSms_receiver(String sms_receiver) {
		this.sms_receiver = sms_receiver;
	}

	public String getBelongs_to() {
		return belongs_to;
	}

	public void setBelongs_to(String belongs_to) {
		this.belongs_to = belongs_to;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public float getTransaction_amount() {
		return transaction_amount;
	}

	public void setTransaction_amount(float transaction_amount) {
		this.transaction_amount = transaction_amount;
	}

	public float getTransaction_balance() {
		return transaction_balance;
	}

	public void setTransaction_balance(float transaction_balance) {
		this.transaction_balance = transaction_balance;
	}

	public int getIs_yet_printed() {
		return is_yet_printed;
	}

	public void setIs_yet_printed(int is_yet_printed) {
		this.is_yet_printed = is_yet_printed;
	}

	public String getTransaction_made_by() {
		return transaction_made_by;
	}

	public void setTransaction_made_by(String transaction_made_by) {
		this.transaction_made_by = transaction_made_by;
	}

	public int getIs_online_saved() {
		return is_online_saved;
	}

	public void setIs_online_saved(int is_online_saved) {
		this.is_online_saved = is_online_saved;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTaxpayernumber() {
		return taxpayernumber;
	}

	public void setTaxpayernumber(String taxpayernumber) {
		this.taxpayernumber = taxpayernumber;
	}

	public String getNumbertraderegister() {
		return numbertraderegister;
	}

	public void setNumbertraderegister(String numbertraderegister) {
		this.numbertraderegister = numbertraderegister;
	}

	@Override
	public String toString() {
		return "SMS{" +
				"id=" + id +
				", received_at=" + received_at +
				", transaction_type='" + transaction_type + '\'' +
				", transaction_beneficiary_name='" + transaction_beneficiary_name + '\'' +
				", transaction_beneficiary_account_number='" + transaction_beneficiary_account_number + '\'' +
				", transaction_date='" + transaction_date + '\'' +
				", transaction_id='" + transaction_id + '\'' +
				", transaction_reference='" + transaction_reference + '\'' +
				", transaction_state='" + transaction_state + '\'' +
				", transaction_currency='" + transaction_currency + '\'' +
				", transaction_made_by='" + transaction_made_by + '\'' +
				", sms_sender='" + sms_sender + '\'' +
				", sms_date='" + sms_date + '\'' +
				", sms_body='" + sms_body + '\'' +
				", sms_receiver='" + sms_receiver + '\'' +
				", belongs_to='" + belongs_to + '\'' +
				", tenant='" + tenant + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", taxpayernumber='" + taxpayernumber + '\'' +
				", numbertraderegister='" + numbertraderegister + '\'' +
				", transaction_amount=" + transaction_amount +
				", transaction_balance=" + transaction_balance +
				", is_yet_printed=" + is_yet_printed +
				", transaction_fees=" + transaction_fees +
				", is_online_saved=" + is_online_saved +
				'}';
	}
}
