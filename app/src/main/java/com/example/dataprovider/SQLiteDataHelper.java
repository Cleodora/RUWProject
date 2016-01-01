package com.example.dataprovider;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SQLiteDataHelper extends SQLiteAssetHelper {

	public static final String COLUMN_ID = "_id";
	//Table RomanUrduWords
	public static final String TABLE_ROMAN_URDU_WORDS = "RomanUrduWords";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_IS_PROPER = "is_proper";
	//Table RomanUrduRepresentations
	public static final String TABLE_ROMAN_URDU_REPRESENTATIONS = "RomanUrduRepresentations";
	public static final String COLUMN_WORD_ID = "word_id";
	//public static final String COLUMN_WORD = "word";
	//SpamWords
	public static final String TABLE_SPAM_WORDS = "SpamWords";
	//public static final String COLUMN_WORD_ID = "word_id";
	public static final String COLUMN_PROB_SPAM = "prob_spam";
	public static final String COLUMN_PROB_HAM = "prob_ham";
	//Table SpamSMS
	public static final String TABLE_SPAM_SMS = "SpamSMS";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_PERSON = "person";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_DATE_SENT = "date_sent";
	public static final String COLUMN_PROTOCOL = "protocol";
	public static final String COLUMN_READ = "read";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_REPLY_PATH_PRESENT = "reply_path_present";
	public static final String COLUMN_SUBJECT = "subject";
	public static final String COLUMN_BODY = "body";
	public static final String COLUMN_SERVICE_CENTER = "service_center";
	public static final String COLUMN_LOCKED = "locked";
	public static final String COLUMN_ERROR_CODE = "error_code";
	public static final String COLUMN_SEEN = "seen";
	public static final String COLUMN_INBOX_ID = "inbox_id";
	public static final String COLUMN_HIDE = "hide";
	//Table BlockedSMS
	public static String TABLE_BLOCKED_SMS = "BlockedSMS";
	//Table HamSMS
	public static final String TABLE_HAM_SMS = "HamSMS";
	//public static final String COLUMN_INBOX_ID ="inbox_id";
	//public static final String COLUMN_DATE = "date";
	//SpamSMSWords
	public static final String TABLE_SPAM_SMS_WORDS = "SpamSMSWords";
	public static final String COLUMN_SPAM_SMS_ID = "spam_SMS_id";
	public static final String COLUMN_SPAM_WORD_ID = "spam_word_id";
	public static final String COLUMN_COUNT = "count";
	//HamSMSWords
	public static final String TABLE_HAM_SMS_WORDS = "HamSMSWords";
	public static final String COLUMN_HAM_SMS_ID = "ham_SMS_id";
	//public static final String COLUMN_SPAM_WORD_ID = "spam_word_id";
	//public static final String COLUMN_COUNT = "count";
	//BlockedContacts
	public static final String TABLE_BLOCKED_CONTACTS = "BlockedContacts";
	public static final String COLUMN_NUMBER = "number";
	//Data Base Name
	private static final String DATABASE_NAME = "ssfromandata";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE_ROMAN_URDU_WORDS = "CREATE TABLE "
			+ TABLE_ROMAN_URDU_WORDS + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_WORD
			+ " TEXT NOT NULL);";
	private static final String DATABASE_CREATE_ROMAN_URDU_REPRESENTATIONS =
			"CREATE TABLE " + TABLE_ROMAN_URDU_REPRESENTATIONS +
					"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_WORD + " TEXT NOT NULL," +
					COLUMN_WORD_ID + " INTEGER NOT NULL," +
					"FOREIGN KEY(" + COLUMN_WORD_ID + ") REFERENCES " + TABLE_ROMAN_URDU_WORDS + "(" + COLUMN_ID + ") ON DELETE CASCADE" + ");";


	private static SQLiteDataHelper sqLiteDataHelper = null;

	private SQLiteDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized SQLiteDataHelper getInstance(Context context){
		if (sqLiteDataHelper == null) {
			sqLiteDataHelper = new SQLiteDataHelper(context);
		}
		return sqLiteDataHelper;
	}

   /* @Override
	public void onCreate( SQLiteDatabase db ) {
        db.execSQL( DATABASE_CREATE_ROMAN_URDU_WORDS );
        db.execSQL( DATABASE_CREATE_ROMAN_URDU_REPRESENTATIONS );
    }*/

   /* @Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_ROMAN_URDU_WORDS );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_ROMAN_URDU_REPRESENTATIONS );
        onCreate( db );
    }*/
}
