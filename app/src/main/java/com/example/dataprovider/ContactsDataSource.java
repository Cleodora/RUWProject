package com.example.dataprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import com.example.model.BlockedContact;
import com.example.model.ContactListItem;

import java.util.ArrayList;
import java.util.List;

public class ContactsDataSource {

	private SQLiteDatabase database;
	private SQLiteDataHelper dbHelper;

	private static ContactsDataSource contactsDataSource = null;
	private boolean isOpened = false;

	private ContactsDataSource(Context context) {
		dbHelper = SQLiteDataHelper.getInstance(context);
	}

	public static synchronized ContactsDataSource getInstance(Context context) {
		if (contactsDataSource == null) {
			contactsDataSource = new ContactsDataSource(context);
			contactsDataSource.open();
		}
		return contactsDataSource;
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
		contactsDataSource = null;
		isOpened = false;
	}

	public long addContact(BlockedContact blockedContact) {

		Cursor cur = database.query(SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, null, null, null, null, null, null);

		cur.moveToFirst();

		boolean found = false;
		String n = "";
		while (!cur.isAfterLast()) {
			n = cur.getString(cur.getColumnIndex(SQLiteDataHelper.COLUMN_NUMBER));

			if (PhoneNumberUtils.compare(blockedContact.getNumber(), n)) {
				found = true;
				cur.close();
				break;
			}

			cur.moveToNext();
		}

		if (!found) {
			ContentValues values = new ContentValues();
			values.put(SQLiteDataHelper.COLUMN_NUMBER, blockedContact.getNumber());
			return database.insert(SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, null, values);
		} else {
			return -1;
		}
	}

	public long deleteContact(BlockedContact blockedContact) {
		return database.delete(SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, SQLiteDataHelper.COLUMN_ID + " = ?", new String[]{blockedContact.getId() + ""});
	}

	public List<ContactListItem> getContactListItems(Context context) {
		List<ContactListItem> contactListItems = new ArrayList<ContactListItem>();

		Cursor cur = database.query(SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, null, null, null, null, null, null);

		cur.moveToFirst();

		Cursor cur2 = null;
		while (!cur.isAfterLast()) {
			ContactListItem contactListItem = new ContactListItem();

			contactListItem.setId(cur.getLong(cur.getColumnIndex(SQLiteDataHelper.COLUMN_ID)));
			contactListItem.setNumber(cur.getString(cur.getColumnIndex(SQLiteDataHelper.COLUMN_NUMBER)));

			cur2 = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
					new String[]{contactListItem.getNumber()}, null);

			cur2.moveToFirst();
			if (!cur2.isAfterLast()) {
				contactListItem.setDisplayName(cur2.getString(cur2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
			} else {
				contactListItem.setDisplayName("Unknown");
			}
			cur2.close();

			contactListItems.add(contactListItem);
			cur.moveToNext();
		}

		return contactListItems;
	}

	public BlockedContact getContact(String number) {
		BlockedContact blockedContact = null;

		//number = number.replaceAll("\\p{Space}","");

		/*String number2 = PhoneNumberUtils.formatNumber(number);

		Cursor cur = database.query(
				SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, null, SQLiteDataHelper.COLUMN_NUMBER + " = ? OR " + SQLiteDataHelper.COLUMN_NUMBER + " = ?",
				new String[]{number, number2}, null, null, null);

		cur.moveToFirst();

		if (!cur.isAfterLast()) {
			blockedContact = new BlockedContact();
			blockedContact.setId(cur.getLong(cur.getColumnIndex(SQLiteDataHelper.COLUMN_ID)));
			blockedContact.setNumber(cur.getString(cur.getColumnIndex(SQLiteDataHelper.COLUMN_NUMBER)));
		}

		cur.close();*/

		Cursor cur = database.query(SQLiteDataHelper.TABLE_BLOCKED_CONTACTS, null, null, null, null, null, null);

		cur.moveToFirst();

		String n = "";
		while (!cur.isAfterLast()) {
			n = cur.getString(cur.getColumnIndex(SQLiteDataHelper.COLUMN_NUMBER));

			if (PhoneNumberUtils.compare(number, n)) {
				blockedContact = new BlockedContact();
				blockedContact.setId(cur.getLong(cur.getColumnIndex(SQLiteDataHelper.COLUMN_ID)));
				blockedContact.setNumber(cur.getString(cur.getColumnIndex(SQLiteDataHelper.COLUMN_NUMBER)));

				cur.close();
				break;
			}

			cur.moveToNext();
		}

		return blockedContact;
	}
}
