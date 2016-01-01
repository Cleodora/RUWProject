package com.example.ssfroman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.adapters.SMSListItemAdapter;
import com.example.analysis.BayesianClassifier;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.SMSLocal;

public class SpamSMSActivity extends ActionBarActivity {

	ActionMode mActionMode = null;
	SpamSMSDataSource spamSmsDataSource = null;
	SMSListItemAdapter adapter = null;
	ListView SMSList = null;

	int selectedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spam_sms);

		String address = getIntent().getStringExtra("address");

		spamSmsDataSource = SpamSMSDataSource.getInstance(this);

		adapter = new SMSListItemAdapter(this, R.layout.sms_list_item_template, spamSmsDataSource.getSMSListItems(address));
		SMSList = (ListView)this.findViewById(R.id.listView);
		SMSList.setAdapter(adapter);

		SMSList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
				//view.setSelected(true);

				selectedIndex = i;

				if (mActionMode != null) {
					return;
				}
				mActionMode = SpamSMSActivity.this.startSupportActionMode(mActionModeCallback);

				/*AlertDialog.Builder builder = new AlertDialog.Builder(BlockedSMSActivity.this);
				builder.setMessage("Restore?")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								SMSListItemAdapter adapter = (SMSListItemAdapter)adapterView.getAdapter();

								long SMSId = ((SMSListItem)adapter.getItem(i)).id;

								final SMSLocal SMS = smsDataSource.getSMSLocal(SMSId);

								AsyncTask<Integer,Integer,Integer> restTask = new AsyncTask<Integer, Integer, Integer>() {
									@Override
									protected Integer doInBackground(Integer... voids) {
										ContentProviderDataSource.restoreSMS(BlockedSMSActivity.this, SMS);
										return 0;
									}
								};

								restTask.execute();


								smsDataSource.deleteSMS(SMSId);
								adapter.remove((SMSListItem)adapter.getItem(i));
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				builder.show();*/
			}
		});
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.spam_sm, menu);
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

					builder = new AlertDialog.Builder(SpamSMSActivity.this);

					builder.setTitle("Delete selected SMS?")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									long SMSId = adapter.getItem(selectedIndex).id;

									spamSmsDataSource.hideSMS(SMSId);
									adapter.remove(adapter.getItem(selectedIndex));
									adapter.notifyDataSetChanged();
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

						}
					}).show();
					mode.finish();
					return true;
				case R.id.action_not_spam:
					builder = new AlertDialog.Builder(SpamSMSActivity.this);

					builder.setTitle("SMS will move to inbox")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									long SMSId = adapter.getItem(selectedIndex).id;

									final SMSLocal SMS = spamSmsDataSource.getSMSLocal(SMSId);

									AsyncTask<Integer,Integer,Integer> restTask = new AsyncTask<Integer, Integer, Integer>() {
										@Override
										protected Integer doInBackground(Integer... voids) {
											BayesianClassifier.markNotSpam(SpamSMSActivity.this, SMS);
											return 0;
										}
									};

									restTask.execute();

									adapter.remove(adapter.getItem(selectedIndex));
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
			SMSList.clearChoices();
			SMSList.requestLayout();
			mActionMode = null;
		}
	};



	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.blocked_sm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

}
