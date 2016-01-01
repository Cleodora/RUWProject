package com.example.dataprovider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.SMSListItem;
import com.example.model.SMSLocal;
import com.example.model.ThreadListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockedSMSDataSource {

	private SQLiteDatabase database;
	private SQLiteDataHelper dbHelper;


	private static BlockedSMSDataSource blockedSMSDataSource = null;
	private boolean isOpened = false;

	private BlockedSMSDataSource(Context context) {
		dbHelper = SQLiteDataHelper.getInstance(context);
	}

	public static synchronized BlockedSMSDataSource getInstance(Context context) {
		if (blockedSMSDataSource == null) {
			blockedSMSDataSource = new BlockedSMSDataSource(context);
			blockedSMSDataSource.open();
		}
		return blockedSMSDataSource;
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
		blockedSMSDataSource = null;
		isOpened = false;
	}

	public long addBlockedSMS(SMSLocal sms) {
		ContentValues values = new ContentValues();
		values.put(SQLiteDataHelper.COLUMN_ADDRESS, sms.address);
		values.put(SQLiteDataHelper.COLUMN_PERSON, 0);
		values.put(SQLiteDataHelper.COLUMN_DATE, sms.date);
		values.put(SQLiteDataHelper.COLUMN_DATE_SENT, sms.date_sent);
		values.put(SQLiteDataHelper.COLUMN_PROTOCOL, sms.protocol);
		values.put(SQLiteDataHelper.COLUMN_READ, sms.read);
		values.put(SQLiteDataHelper.COLUMN_STATUS, sms.status);
		values.put(SQLiteDataHelper.COLUMN_TYPE, sms.type);
		values.put(SQLiteDataHelper.COLUMN_REPLY_PATH_PRESENT, sms.reply_path_present);
		values.put(SQLiteDataHelper.COLUMN_SUBJECT, sms.subject);
		values.put(SQLiteDataHelper.COLUMN_BODY, sms.body);
		values.put(SQLiteDataHelper.COLUMN_SERVICE_CENTER, sms.service_center);
		values.put(SQLiteDataHelper.COLUMN_LOCKED, sms.locked);
		values.put(SQLiteDataHelper.COLUMN_ERROR_CODE, sms.error_code);
		values.put(SQLiteDataHelper.COLUMN_SEEN, sms.seen);


		long insertId = database.insert(SQLiteDataHelper.TABLE_BLOCKED_SMS, null, values);

		return insertId;
	}

	public List<SMSLocal> getAll() {
		List<SMSLocal> list = new ArrayList<SMSLocal>();
		Cursor cursor = database.query(SQLiteDataHelper.TABLE_BLOCKED_SMS, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(cursorToSMS(cursor));
			cursor.moveToNext();
		}
		cursor.close();

		return list;
	}

	public List<ThreadListItem> getThreads() {
		List<ThreadListItem> list = new ArrayList<ThreadListItem>();

		Cursor cursor = database.rawQuery(
				"SELECT DISTINCT " + SQLiteDataHelper.COLUMN_ADDRESS
						+ ", MAX(" + SQLiteDataHelper.COLUMN_DATE
						+ "), COUNT(" + SQLiteDataHelper.COLUMN_ADDRESS + "), "
						+ SQLiteDataHelper.COLUMN_BODY
						+ " FROM " + SQLiteDataHelper.TABLE_BLOCKED_SMS
						+ " GROUP BY " + SQLiteDataHelper.COLUMN_ADDRESS
						+ " ORDER BY MAX(" + SQLiteDataHelper.COLUMN_DATE + ") DESC;", null
		);

		ThreadListItem tli = null;

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			tli = new ThreadListItem();
			tli.address = cursor.getString(0);
			tli.dateTime = new Date(cursor.getLong(1));
			tli.count = cursor.getLong(2);
			tli.snippet = cursor.getString(3);
			list.add(tli);
			cursor.moveToNext();
		}
		cursor.close();

		return list;
	}

	public List<SMSListItem> getSMSListItems(String address) {
		List<SMSListItem> list = new ArrayList<SMSListItem>();
		Cursor cursor = database.rawQuery("SELECT "
				+ SQLiteDataHelper.COLUMN_ID + ", "
				+ SQLiteDataHelper.COLUMN_DATE + ", "
				+ SQLiteDataHelper.COLUMN_BODY
				+ " FROM " + SQLiteDataHelper.TABLE_BLOCKED_SMS
				+ " WHERE " + SQLiteDataHelper.COLUMN_ADDRESS + " = " + "\"" + address + "\""
				+ " ORDER BY " + SQLiteDataHelper.COLUMN_DATE + " DESC;", null);

		SMSListItem sli = null;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			sli = new SMSListItem();
			sli.id = cursor.getLong(0);
			sli.dateTime = new Date(cursor.getLong(1));
			sli.body = cursor.getString(2);
			list.add(sli);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	public SMSLocal getSMSLocal(long SMSId) {
		Cursor cursor = database.rawQuery("SELECT * FROM " + SQLiteDataHelper.TABLE_BLOCKED_SMS + " WHERE _id=" + SMSId + ";", null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			SMSLocal sms = cursorToSMS(cursor);
			cursor.close();

			return sms;
		}

		return null;
	}

	public void deleteSMS(long SMSId) {
		database.delete(SQLiteDataHelper.TABLE_BLOCKED_SMS, SQLiteDataHelper.COLUMN_ID + "=?", new String[]{SMSId + ""});
	}

	private SMSLocal cursorToSMS(Cursor cursor) {
		SMSLocal sms = new SMSLocal();
		sms.id = cursor.getInt(0);
		sms.address = cursor.getString(1);
		sms.person = cursor.getInt(2);
		sms.date = cursor.getLong(3);
		sms.date_sent = cursor.getLong(4);
		sms.protocol = cursor.getInt(5);
		sms.read = cursor.getInt(6);
		sms.status = cursor.getInt(7);
		sms.type = cursor.getInt(8);
		sms.reply_path_present = cursor.getInt(9);
		sms.subject = cursor.getString(10);
		sms.body = cursor.getString(11);
		sms.service_center = cursor.getString(12);
		sms.locked = cursor.getInt(13);
		sms.error_code = cursor.getInt(14);
		sms.seen = cursor.getInt(15);
		return sms;
	}
}
