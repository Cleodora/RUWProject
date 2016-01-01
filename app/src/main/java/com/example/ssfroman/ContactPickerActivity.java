package com.example.ssfroman;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.ContactListAdapter;
import com.example.dataprovider.ContactsDataSource;
import com.example.model.BlockedContact;
import com.example.model.ContactListItem;


public class ContactPickerActivity extends ActionBarActivity {

	ActionMode mActionMode = null;
	private static final int CONTACT_PICKER_RESULT = 1001;

	private ContactListAdapter adapter = null;
	private ContactsDataSource contactsDataSource = null;
	ListView listView = null;
	int selectedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_picker);

		contactsDataSource = ContactsDataSource.getInstance(this);
		listView = (ListView) findViewById(R.id.listView);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				selectedIndex = i;

				if (mActionMode != null) {
					return;
				}
				mActionMode = ContactPickerActivity.this.startSupportActionMode(mActionModeCallback);
			}
		});
		updateList();
	}

	private void updateList() {
		adapter = new ContactListAdapter(this, R.layout.contact_list_item_template, contactsDataSource.getContactListItems(this));
		listView.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case CONTACT_PICKER_RESULT:
					Bundle extras = data.getExtras();
					Uri result = data.getData();
					String id = result.getLastPathSegment();

					Cursor cur = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
							new String[]{id}, null);


					cur.moveToFirst();

					while (!cur.isAfterLast()) {
						String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.d("privateLog", "Number: " + number);

						contactsDataSource.addContact(new BlockedContact(number));

						cur.moveToNext();
					}
					cur.close();

					updateList();

					break;
			}

		} else {

		}
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.contact_picker_contextual, menu);
			//mode.setTitle("Edit Task");
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}


		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			AlertDialog.Builder builder = null;
			switch (item.getItemId()) {

				case R.id.action_delete:

					builder = new AlertDialog.Builder(ContactPickerActivity.this);

					builder.setTitle("Delete Contact?")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									ContactListItem contactListItem = adapter.getItem(selectedIndex);
									contactsDataSource.deleteContact(new BlockedContact(contactListItem.getId(), contactListItem.getNumber()));
									adapter.remove(contactListItem);
									adapter.notifyDataSetChanged();
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

						}
					}).show();
					mode.finish();
					return true;

				default:
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_pick) {
			Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
			return true;
		} else if (id == R.id.action_add) {
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_add_contact);
			dialog.setTitle("Add number");

			final EditText etPhone = (EditText) dialog.findViewById(R.id.editText);
			dialog.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String number = etPhone.getText().toString();
					if (number.equals("")) {
						etPhone.setError("Add number!");
					} else {
						contactsDataSource.addContact(new BlockedContact(number));
						Toast.makeText(ContactPickerActivity.this, "Number added", Toast.LENGTH_SHORT).show();
						updateList();
						dialog.dismiss();
					}
				}
			});

			dialog.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});

			dialog.show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
