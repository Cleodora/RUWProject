package com.example.dataprovider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.model.SMSListItem;
import com.example.model.SMSLocal;
import com.example.model.ThreadListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.dataprovider.SQLiteDataHelper.COLUMN_ADDRESS;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_BODY;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_DATE;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_DATE_SENT;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_ERROR_CODE;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_LOCKED;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_PERSON;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_PROTOCOL;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_READ;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_REPLY_PATH_PRESENT;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_SEEN;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_SERVICE_CENTER;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_STATUS;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_SUBJECT;
import static com.example.dataprovider.SQLiteDataHelper.COLUMN_TYPE;

public class ContentProviderDataSource {
	public static List<ThreadListItem> getAllSMSThreads(Context context) {

		List<ThreadListItem> threads = new ArrayList<ThreadListItem>();

		Uri threadsUri = Uri.parse("content://mms-sms/conversations?simple=true");
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(threadsUri,
					new String[]{"_id", "recipient_ids", "message_count", "snippet", "date"}, null, null, "normalized_date desc");
		} catch (Exception ex) {
			cur = context.getContentResolver().query(threadsUri,
					new String[]{"_id", "recipient_ids", "message_count", "snippet", "date"}, null, null, "date desc");
		}

		while (cur.moveToNext()) {
			ThreadListItem tli = new ThreadListItem();
			tli.threadId = cur.getLong(0);
			tli.address = getAssociatedAddress(context, cur.getString(1));
			tli.count = cur.getLong(2);
			tli.snippet = cur.getString(3);
			tli.dateTime = new Date(cur.getLong(4));

			threads.add(tli);
		}

		cur.close();
		return threads;
	}

	public static List<SMSListItem> getAllSMSsInThread(Context context, long threadId) {
		List<SMSListItem> SMSs = new ArrayList<SMSListItem>();

		Uri SMSsUri = Uri.parse("content://sms/inbox");
		Cursor curSMS = context.getContentResolver().query(SMSsUri, new String[]{"_id", "body", "date", "thread_id"}, "thread_id=?", new String[]{threadId + ""}, null);

		while (curSMS.moveToNext()) {
			SMSListItem sli = new SMSListItem();

			sli.id = curSMS.getLong(0);
			sli.body = curSMS.getString(1);
			sli.dateTime = new Date(curSMS.getLong(2));

			SMSs.add(sli);
		}
		curSMS.close();
		return SMSs;
	}

	public static SMSLocal getSMSDetails(Context context, long SMSId) {
		Uri uriSMS = Uri.parse("content://sms/inbox");
		Cursor cur = context.getContentResolver().query(uriSMS, null, "_id=" + SMSId, null, null);

		cur.moveToFirst();

		SMSLocal sms = new SMSLocal();
		sms.id = cur.getInt(0);
		sms.address = cur.getString(2);
		sms.person = cur.getInt(3);
		sms.date = cur.getLong(4);
		sms.date_sent = cur.getLong(5);
		sms.protocol = cur.getInt(6);
		sms.read = cur.getInt(7);
		sms.status = cur.getInt(8);
		sms.type = cur.getInt(9);
		sms.reply_path_present = cur.getInt(10);
		sms.subject = cur.getString(11);
		sms.body = cur.getString(12);
		sms.service_center = cur.getString(13);
		sms.locked = cur.getInt(14);
		sms.error_code = cur.getInt(15);
		sms.read = cur.getInt(16);
		sms.inbox_id = cur.getLong(0);
		cur.close();

		return sms;
	}

	public static void deleteSMS(Context context, long SMSId) {
		Uri uriSMS = Uri.parse("content://sms/" + SMSId);
		context.getContentResolver().delete(uriSMS, null, null);
	}

	public static Uri restoreSMS(Context context, SMSLocal SMS) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ADDRESS, SMS.address);
		values.put(COLUMN_PERSON, SMS.person);
		values.put(COLUMN_DATE, SMS.date);
		values.put(COLUMN_DATE_SENT, SMS.date_sent);
		values.put(COLUMN_PROTOCOL, SMS.protocol);
		values.put(COLUMN_READ, SMS.read);
		values.put(COLUMN_STATUS, SMS.status);
		values.put(COLUMN_TYPE, SMS.type);
		values.put(COLUMN_REPLY_PATH_PRESENT, SMS.reply_path_present);
		values.put(COLUMN_SUBJECT, SMS.subject);
		values.put(COLUMN_BODY, SMS.body);
		values.put(COLUMN_SERVICE_CENTER, SMS.service_center);
		values.put(COLUMN_LOCKED, SMS.locked);
		values.put(COLUMN_ERROR_CODE, SMS.error_code);
		values.put(COLUMN_SEEN, SMS.seen);

		Uri SMSsUri = Uri.parse("content://sms/inbox");
		return context.getContentResolver().insert(SMSsUri, values);
		//context.getContentResolver().delete(Uri.parse("content://sms/conversations/-1"), null, null);
	}

	public static String getAssociatedAddress(Context context, String id) {
		String threadAddrContent = "content://mms-sms/canonical-addresses";
		Uri threadAddUri = Uri.parse(threadAddrContent);

		if (!id.equals("")) {
			String[] ids = id.split(" ");
			String addr = "";
			for (String adr : ids) {
				Cursor cur = context.getContentResolver().query(threadAddUri, null, "_id=" + adr, null, null);
				cur.moveToNext();
				addr += cur.getString(1) + ", ";
				cur.close();
			}
			addr = addr.substring(0, addr.length() - 2);
			return addr;
		}
		return null;
	}

	public static List<SMSLocal> getRandomSMSFromInbox(Context context, long count, List<Long> notInList) {

		//Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS,null," _id not in(0,1,2,3,4) and _id < 12",null,null,null,"random()  limit 4");

		Uri uriSMS = Uri.parse("content://sms/inbox");


		//select * from RomanUrduWords where _id not in(0,1,2,3,4) order by random()
//select * from (select * from RomanUrduWords where _id not in(0,1,2,3,4) and _id <10) order by random() limit 4

		Cursor cursor = null;
		if (notInList != null && notInList.size() > 0) {

			String notInString = "(";
			for (Long i : notInList) {
				notInString += i + ",";
			}

			notInString = notInString.substring(0, notInString.length() - 1) + ")";

			cursor = context.getContentResolver().query(uriSMS, null, SQLiteDataHelper.COLUMN_ID + " not in" + notInString, null, "random()  limit " + count);
		} else {
			cursor = context.getContentResolver().query(uriSMS, null, null, null, "random()  limit " + count);
		}

		List<SMSLocal> rndSMSs = new ArrayList<SMSLocal>();
		cursor.moveToNext();

		while (!cursor.isAfterLast()) {
			SMSLocal sms = new SMSLocal();
			sms.id = cursor.getLong(0);
			sms.address = cursor.getString(2);
			sms.person = cursor.getInt(3);
			sms.date = cursor.getLong(4);
			sms.date_sent = cursor.getLong(5);
			sms.protocol = cursor.getInt(6);
			sms.read = cursor.getInt(7);
			sms.status = cursor.getInt(8);
			sms.type = cursor.getInt(9);
			sms.reply_path_present = cursor.getInt(10);
			sms.subject = cursor.getString(11);
			sms.body = cursor.getString(12);
			sms.service_center = cursor.getString(13);
			sms.locked = cursor.getInt(14);
			sms.error_code = cursor.getInt(15);
			sms.read = cursor.getInt(16);
			sms.inbox_id = cursor.getLong(0);
			rndSMSs.add(sms);

			cursor.moveToNext();
		}

		cursor.close();

		return rndSMSs;
	}

	public static SMSLocal getSMSFromInbox(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

		SMSLocal sms = new SMSLocal();

		cursor.moveToNext();

		if (!cursor.isAfterLast()) {
			sms.id = cursor.getLong(0);
			sms.address = cursor.getString(2);
			sms.person = cursor.getInt(3);
			sms.date = cursor.getLong(4);
			sms.date_sent = cursor.getLong(5);
			sms.protocol = cursor.getInt(6);
			sms.read = cursor.getInt(7);
			sms.status = cursor.getInt(8);
			sms.type = cursor.getInt(9);
			sms.reply_path_present = cursor.getInt(10);
			sms.subject = cursor.getString(11);
			sms.body = cursor.getString(12);
			sms.service_center = cursor.getString(13);
			sms.locked = cursor.getInt(14);
			sms.error_code = cursor.getInt(15);
			sms.read = cursor.getInt(16);
			sms.inbox_id = cursor.getLong(0);
		}

		cursor.close();

		return sms;
	}
}
