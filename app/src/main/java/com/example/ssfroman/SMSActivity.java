package com.example.ssfroman;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.example.dataprovider.ContentProviderDataSource;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.SMSListItem;
import com.example.model.SMSLocal;

public class SMSActivity extends ActionBarActivity {

	ActionMode mActionMode = null;
	SMSListItemAdapter adapter = null;
	ListView SMSList = null;

	int selectedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);

		final long threadId = getIntent().getLongExtra("threadId", 0);


		SMSList = (ListView)this.findViewById(R.id.listView);


		final ProgressDialog dialog = new ProgressDialog(this);

		AsyncTask<Integer, Integer, SMSListItemAdapter> restTask = new AsyncTask<Integer, Integer, SMSListItemAdapter>() {
			@Override
			protected void onPreExecute() {
				//super.onPreExecute();
				dialog.setMessage("Loading...");
				dialog.show();
			}

			@Override
			protected SMSListItemAdapter doInBackground(Integer... voids) {
				adapter = new SMSListItemAdapter(SMSActivity.this, R.layout.sms_list_item_template, ContentProviderDataSource.getAllSMSsInThread(SMSActivity.this, threadId));
				return adapter;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				//super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(SMSListItemAdapter adapter) {
				//super.onPostExecute(adapter);

				SMSList.setAdapter(adapter);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		};

		restTask.execute();

		SMSList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

				selectedIndex = position;

				if (mActionMode != null) {
					return;
				}
				mActionMode = SMSActivity.this.startSupportActionMode(mActionModeCallback);

				/*AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
				builder.setMessage("Move to spam?")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								SMSListItemAdapter adapter = (SMSListItemAdapter)parent.getAdapter();

								final long SMSId = ((SMSListItem)adapter.getItem(position)).id;



								adapter.remove((SMSListItem)adapter.getItem(position));
								adapter.notifyDataSetChanged();

                                AsyncTask<Integer, Integer, Integer> restTask = new AsyncTask<Integer, Integer, Integer>() {

                                    @Override
                                    protected Integer doInBackground(Integer... voids) {
                                        SMSLocal smsLocal = ContentProviderDataSource.getSMSDetails(SMSActivity.this, SMSId);
                                        long insertId = SpamSMSDataSource.getInstance(SMSActivity.this).addSpamSMS(smsLocal);
                                        ContentProviderDataSource.deleteSMS(SMSActivity.this, SMSId);

                                        //to create relation with SpamSMSWords, SpamSMS must have local primary key
                                        smsLocal.id = insertId;
                                        BayesianClassifier.trainSpam2(SMSActivity.this,smsLocal);
                                        return 0;
                                    }

                                    @Override
                                    protected void onProgressUpdate(Integer... values) {
                                        //super.onProgressUpdate(values);
                                    }
                                };

                                restTask.execute();




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
			inflater.inflate(R.menu.sms_contextual, menu);
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

				case R.id.action_move_to_sapm:

					builder = new AlertDialog.Builder(SMSActivity.this);

					builder.setTitle("Move to spam?")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									final long SMSId = ((SMSListItem)adapter.getItem(selectedIndex)).id;



									adapter.remove((SMSListItem)adapter.getItem(selectedIndex));
									adapter.notifyDataSetChanged();

									AsyncTask<Integer, Integer, Integer> restTask = new AsyncTask<Integer, Integer, Integer>() {

										@Override
										protected Integer doInBackground(Integer... voids) {
											SMSLocal smsLocal = ContentProviderDataSource.getSMSDetails(SMSActivity.this, SMSId);
											long insertId = SpamSMSDataSource.getInstance(SMSActivity.this).addSpamSMS(smsLocal);
											ContentProviderDataSource.deleteSMS(SMSActivity.this, SMSId);

											//to create relation with SpamSMSWords, SpamSMS must have local primary key
											smsLocal.id = insertId;
											BayesianClassifier.trainSpam2(SMSActivity.this,smsLocal);
											return 0;
										}

										@Override
										protected void onProgressUpdate(Integer... values) {
											//super.onProgressUpdate(values);
										}
									};

									restTask.execute();

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sm, menu);
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
	}

}
