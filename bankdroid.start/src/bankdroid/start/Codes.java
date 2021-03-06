package bankdroid.start;

public interface Codes
{
	final static String TAG = "Start";

	public static final String TWITTER_URL = "http://twitter.com/bankdroid";
	public static final String GMAIL_URL = "mailto:info@bankdroid.info";
	public static final String FACEBOOK_URL = "http://www.facebook.com/pages/BankDroid/124814474207047?ref=ts";
	public static final String URL_PROJECT_HOME = "http://goo.gl/9oiKb";
	public static final String BLOG_HOME = "http://goo.gl/WBjeZ";
	public static final String SMSKEY_BLOG_HOME = "http://goo.gl/QVbci";
	public static final String SMSKEY_MARKET = "http://goo.gl/3skg0";

	final static int SERVICE_PROCESS = 1;
	final static int SERVICE_FAILED = 2;

	final static String SERVICE_EXCEPTION = "SERVICE_EXCEPTION";

	//PREFERENCES
	final static String PREF_SAVE_LAST_LOGIN = "bankdroid.start.SaveLastLogin";
	final static String PREF_SHOW_DUMMY_BANK = "bankdroid.start.ShowDummyBank";
	final static String PREF_SESSION_TIMEOUT = "bankdroid.start.SessionTimeout";

	final static String PREF_ENCRYPTED_STORE = "bankdroid.start.EncryptedStore";

	//REGISTRY KEYS
	final static String REG_CUSTOMER_PREFIX = "/customer/";
	final static String REG_CUSTOMERID_SEQ = "/customeridseq";

	//REMOTE PROPERTIES FOR REMOTE OBJECTS
	final static String RP_REGISTRY_ID = "bankdroid.start.registryId";
	final static String RP_ACCOUNT_PIN = "bankdroid.start.accountPin"; //JSON containing account number as name and pin as value, e.g. {"12345678":"123456", "23456789":"111222"}
	final static String RP_STORE_PASSWORD = "bankdroid.start.storePassword";

	//DEFAULTS
	final static String DEFAULT_SESSION_TIMEOUT = "3";

	//OTHERS
	final static int REQUEST_LOGIN = 0xbaba;
	final static int REQUEST_OTHER = 0xbab;
	final static int RESULT_KILL = 0xbebe;

	final static String DUMMY_BANK_ID = "DUMMY";
	final static int NOTIFICATION_ACTIVE_SESSION = 122112;
	final static int NOTIFICATION_SESSION_TIMEOUT = 122113;
	final static int NOTIFICATION_SESSION_TIMEOUT_EXPIRED = 122114;

	//EXTRAS
	final static String EXTRA_TRANSACTION_FILTER = "com.bankdroid.TransactionFilter";
	final static String EXTRA_PROPERTIES = "com.bankdroid.Properties";
	final static String EXTRA_ACTIVITY_TITLE = "com.bankdroid.ActivityTitle";
	final static String EXTRA_CUSTOMER = "com.bankdroid.Customer";
	final static String EXTRA_ANALYTICS_ACTION = "com.bankdroid.AnalyticsAction";
	final static String EXTRA_SHARE_SUBJECT = "com.bankdroid.ShareSubject";
	final static String EXTRA_SHARE_BODY_TOP = "com.bankdroid.ShareBodyTop";
	final static String EXTRA_ACCOUNT_LIST = "com.bankdroid.AccountList";
	final static String EXTRA_PINMASK = "com.bankdroid.PinMask";
}
