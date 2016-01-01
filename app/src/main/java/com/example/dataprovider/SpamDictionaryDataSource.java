package com.example.dataprovider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.HamSMS;
import com.example.model.HamSMSWord;
import com.example.model.SpamSMSWord;
import com.example.model.SpamWord;

import java.util.ArrayList;
import java.util.List;

public class SpamDictionaryDataSource {

	private SQLiteDatabase database;
	private SQLiteDataHelper dbHelper;
	private static SpamDictionaryDataSource spamDictionaryDataSource = null;

	private boolean isOpened = false;

	private SpamDictionaryDataSource(Context context) {
		dbHelper = SQLiteDataHelper.getInstance(context);
	}

	public static synchronized SpamDictionaryDataSource getInstance(Context context) {
		if (spamDictionaryDataSource == null) {
			spamDictionaryDataSource = new SpamDictionaryDataSource(context);
			spamDictionaryDataSource.open();
		}
		return spamDictionaryDataSource;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (!database.isReadOnly()) {
			database.execSQL("PRAGMA foreign_keys = ON;");
		}
		isOpened = true;
	}

	public void close() {
		dbHelper.close();
		spamDictionaryDataSource = null;
		isOpened = false;
	}

	public boolean isWordInSpamDictionary(String wordId) {
		Cursor cursor = database.query(
				SQLiteDataHelper.TABLE_SPAM_WORDS, null, SQLiteDataHelper.COLUMN_WORD_ID + " = ?", new String[]{wordId}, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return true;
		}

		return false;
	}

	public SpamWord getSpamWord(String wordId) {
		Cursor cursor = database.query(
				SQLiteDataHelper.TABLE_SPAM_WORDS, null, SQLiteDataHelper.COLUMN_WORD_ID + " = ?", new String[]{wordId}, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			SpamWord spamWord = new SpamWord();

			spamWord.setId(cursor.getLong(0));
			spamWord.setWordId(cursor.getString(1));
			spamWord.setSpamProbability(cursor.getLong(2));
			spamWord.setHamProbability(cursor.getLong(3));
			cursor.close();

			return spamWord;
		}

		return null;
	}

	public List<SpamWord> getAllSpamWords() {
		Cursor cursor = database.query(
				SQLiteDataHelper.TABLE_SPAM_WORDS, null, null, null, null, null, null);
		List<SpamWord> spamWords = new ArrayList<SpamWord>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SpamWord spamWord = new SpamWord();

			spamWord.setId(cursor.getLong(0));
			spamWord.setWordId(cursor.getString(1));
			spamWord.setSpamProbability(cursor.getLong(2));
			spamWord.setHamProbability(cursor.getLong(3));

			spamWords.add(spamWord);
			cursor.moveToNext();
		}

		return spamWords;
	}

	public long getWordCountInSpamSMSs(long spamWordId) {
		//select sum(_id) from RomanUrduRepresentations where word_id=2 group by word_id
		Cursor cursor = database.rawQuery("SELECT SUM(" + SQLiteDataHelper.COLUMN_COUNT + ")"
				+ " FROM " + SQLiteDataHelper.TABLE_SPAM_SMS_WORDS
				+ " WHERE " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ? GROUP BY "
				+ SQLiteDataHelper.COLUMN_SPAM_WORD_ID, new String[]{spamWordId + ""});

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return cursor.getLong(0);
		}

		return 0;
	}

	public long getSpamSMSCountForWord(long wordId) {
		//select count() from RomanUrduRepresentations where word_id = 2
		Cursor cursor = database.rawQuery(
				"SELECT COUNT() FROM "
						+ SQLiteDataHelper.TABLE_SPAM_SMS_WORDS
						+ " WHERE " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?",
				new String[]{wordId + ""}
		);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return cursor.getLong(0);
		}

		return 0;
	}

	public long getWordCountInHamSMSs(long spamWordId) {
		//select sum(_id) from RomanUrduRepresentations where word_id=2 group by word_id
		Cursor cursor = database.rawQuery("SELECT SUM(" + SQLiteDataHelper.COLUMN_COUNT + ")"
				+ " FROM " + SQLiteDataHelper.TABLE_HAM_SMS_WORDS
				+ " WHERE " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ? GROUP BY "
				+ SQLiteDataHelper.COLUMN_SPAM_WORD_ID, new String[]{spamWordId + ""});

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return cursor.getLong(0);
		}

		return 0;
	}

    /*public long getHamSMSCountForWord(long wordId) {
		//select count() from RomanUrduRepresentations where word_id = 2
        Cursor cursor = database.rawQuery(
                "SELECT COUNT() FROM "
                        + SQLiteDataHelper.TABLE_HAM_SMS_WORDS
                        + " WHERE " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?",
                new String[]{wordId + ""}
        );

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            return cursor.getLong(0);
        }

        return 0;
    }*/

   /* public SpamWord getSpamWordDetails(long wordId) {
		Cursor cursor = database.query(
                SQLiteDataHelper.TABLE_SPAM_WORDS, null, SQLiteDataHelper.COLUMN_WORD_ID + " = ?", new String[]{wordId + ""}, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            SpamWord spamWord = new SpamWord();

            spamWord.setId(cursor.getLong(0));
            spamWord.setWordId(cursor.getString(1));
            spamWord.setSpamProbability(cursor.getLong(2));
            spamWord.setHamProbability(cursor.getLong(3));
            cursor.close();

            cursor = database.query(
                    SQLiteDataHelper.TABLE_SPAM_SMS_WORDS, null, SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?",
                    new String[]{spamWord.getId() + ""}, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                spamWord.getSpamSMSDetails().add(new SpamSMSWord(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getLong(3)));
            }
            cursor.close();

            cursor = database.query(
                    SQLiteDataHelper.TABLE_HAM_SMS_WORDS, null, SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?",
                    new String[]{spamWord.getId() + ""}, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                spamWord.getHamSMSDetails().add(new HamSMSWord(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getLong(3)));
            }
            cursor.close();

            return spamWord;
        }

        return null;
    }*/

	public long addSpamWord(SpamWord spamWord) {
		ContentValues values = new ContentValues();

		values.put(SQLiteDataHelper.COLUMN_WORD_ID, spamWord.getWordId());
		values.put(SQLiteDataHelper.COLUMN_PROB_SPAM, spamWord.getSpamProbability());
		values.put(SQLiteDataHelper.COLUMN_PROB_HAM, spamWord.getHamProbability());

		return database.insert(SQLiteDataHelper.TABLE_SPAM_WORDS, null, values);
	}

	public long deleteSpamWord(long spamWordID) {
		return database.delete(SQLiteDataHelper.TABLE_SPAM_WORDS, SQLiteDataHelper.COLUMN_ID + " = ?", new String[]{spamWordID + ""});
	}

	public void addSpamWordAssociation(List<SpamSMSWord> spamSMSWords) {

		ContentValues values = new ContentValues();

		for (SpamSMSWord ssw : spamSMSWords) {
			values.put(SQLiteDataHelper.COLUMN_SPAM_SMS_ID, ssw.getSpamSMSId());
			values.put(SQLiteDataHelper.COLUMN_SPAM_WORD_ID, ssw.getSpamWordId());
			values.put(SQLiteDataHelper.COLUMN_COUNT, ssw.getCount());

			database.insert(SQLiteDataHelper.TABLE_SPAM_SMS_WORDS, null, values);
			values.clear();
		}


	}

	public void addHamWordAssociations(List<HamSMSWord> hamSMSWords) {
		ContentValues values = new ContentValues();

		for (HamSMSWord hsw : hamSMSWords) {
			values.put(SQLiteDataHelper.COLUMN_HAM_SMS_ID, hsw.getHamSMSId());
			values.put(SQLiteDataHelper.COLUMN_SPAM_WORD_ID, hsw.getSpamWordId());
			values.put(SQLiteDataHelper.COLUMN_COUNT, hsw.getCount());

			database.insert(SQLiteDataHelper.TABLE_HAM_SMS_WORDS, null, values);
			values.clear();
		}
	}

	public long removeAssociationWithSpamSMS(long spamSMSId, long spamWordId) {

		return database.delete(SQLiteDataHelper.TABLE_SPAM_SMS_WORDS,
				SQLiteDataHelper.COLUMN_SPAM_SMS_ID + " = ? AND " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?", new String[]{spamSMSId + "", spamWordId + ""});
	}

	public long removeAssociationWithHamSMS(long hamSMSId, long spamWordId) {

		return database.delete(SQLiteDataHelper.TABLE_HAM_SMS_WORDS,
				SQLiteDataHelper.COLUMN_HAM_SMS_ID + " = ? AND " + SQLiteDataHelper.COLUMN_SPAM_WORD_ID + " = ?", new String[]{hamSMSId + "", spamWordId + ""});
	}

	public long updateSpamWord(long id, long probSpam, long probHam) {

		ContentValues values = new ContentValues();
		values.put(SQLiteDataHelper.COLUMN_PROB_SPAM, probSpam);
		values.put(SQLiteDataHelper.COLUMN_PROB_HAM, probHam);

		return database.update(SQLiteDataHelper.TABLE_SPAM_WORDS, values, SQLiteDataHelper.COLUMN_ID + " = ?", new String[]{id + ""});
	}

	public long associateWithSpamSMS(int spamSMSId, long spamWordId, int count) {
		ContentValues values = new ContentValues();

		values.put(SQLiteDataHelper.COLUMN_SPAM_SMS_ID, spamSMSId);
		values.put(SQLiteDataHelper.COLUMN_SPAM_WORD_ID, spamWordId);
		values.put(SQLiteDataHelper.COLUMN_COUNT, count);

		return database.insert(SQLiteDataHelper.TABLE_SPAM_SMS_WORDS, null, values);
	}

	public long associateWithHamSMS(int hamSMSId, long spamWordId, int count) {
		ContentValues values = new ContentValues();

		values.put(SQLiteDataHelper.COLUMN_HAM_SMS_ID, hamSMSId);
		values.put(SQLiteDataHelper.COLUMN_SPAM_WORD_ID, spamWordId);
		values.put(SQLiteDataHelper.COLUMN_COUNT, count);

		return database.insert(SQLiteDataHelper.TABLE_HAM_SMS_WORDS, null, values);
	}

	public long getSpamSMSAssociationsCount() {
		Cursor cursor = database.rawQuery("SELECT COUNT(DISTINCT " + SQLiteDataHelper.COLUMN_SPAM_SMS_ID + ") FROM " + SQLiteDataHelper.TABLE_SPAM_SMS_WORDS, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return cursor.getLong(0);
		}

		return -1;
	}

	public long getHamSMSAssociationsCount() {
		Cursor cursor = database.rawQuery("SELECT COUNT(DISTINCT " + SQLiteDataHelper.COLUMN_HAM_SMS_ID + ") FROM " + SQLiteDataHelper.TABLE_HAM_SMS_WORDS, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return cursor.getLong(0);
		}

		return -1;
	}

	public List<Long> getHamSMSsInboxIds() {
		Cursor cursor = database.query(true, SQLiteDataHelper.TABLE_HAM_SMS, new String[]{SQLiteDataHelper.COLUMN_INBOX_ID}, null, null, null, null, null, null);

		List<Long> inboxIds = new ArrayList<Long>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			inboxIds.add(cursor.getLong(0));
			cursor.moveToNext();
		}
		cursor.close();

		return inboxIds;
	}

	public long addHamSMS(HamSMS hamSMS) {
		ContentValues values = new ContentValues();
		values.put(SQLiteDataHelper.COLUMN_INBOX_ID, hamSMS.getInboxId());
		values.put(SQLiteDataHelper.COLUMN_DATE,hamSMS.getDate());

		return database.insert(SQLiteDataHelper.TABLE_HAM_SMS, null, values);
	}

	public long deleteHamSMS(long hamSMSId) {
		return database.delete(SQLiteDataHelper.TABLE_HAM_SMS, SQLiteDataHelper.COLUMN_ID + " = ?", new String[]{hamSMSId + ""});
	}

	public List<HamSMS> getHamSMSs(long inboxId) {
		Cursor cursor = database.query(SQLiteDataHelper.TABLE_HAM_SMS, null, SQLiteDataHelper.COLUMN_INBOX_ID + " = ?", new String[]{inboxId + ""}, null, null, null, null);

		List<HamSMS> hamSMSes = new ArrayList<HamSMS>();
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			HamSMS hamSMS = new HamSMS();

			hamSMS.setId(cursor.getLong(0));
			hamSMS.setInboxId(cursor.getLong(1));
			hamSMS.setDate(cursor.getLong(2));

			hamSMSes.add(hamSMS);

			cursor.moveToNext();
		}

		cursor.close();

		return hamSMSes;
	}

	public long removeAllAssociationsWithHamSMS(long hamSMSId) {
		return database.delete(SQLiteDataHelper.TABLE_HAM_SMS_WORDS,
				SQLiteDataHelper.COLUMN_HAM_SMS_ID + " = ?", new String[]{hamSMSId + ""});
	}
}
