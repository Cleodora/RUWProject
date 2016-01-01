package com.example.ssfroman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.adapters.SMSListItemAdapter;
import com.example.analysis.BayesianClassifier;
import com.example.dataprovider.BlockedSMSDataSource;
import com.example.dataprovider.ContentProviderDataSource;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.SMSLocal;


public class BlockedSMSActivity extends ActionBarActivity {

	ActionMode mActionMode = null;
	BlockedSMSDataSource blockedSmsDataSource = null;
	SMSListItemAdapter adapter = null;
	ListView SMSList = null;

	int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_sms);

		String address = getIntent().getStringExtra("address");

		blockedSmsDataSource = BlockedSMSDataSource.getInstance(this);

		adapter = new SMSListItemAdapter(this, R.layout.sms_list_item_template, blockedSmsDataSource.getSMSListItems(address));
		SMSList = (ListView)this.findViewById(R.id.listView);
		SMSList.setAdapter(adapter);

		SMSList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

				view.setSelected(true);

				selectedIndex = i;

				if (mActionMode != null) {
					return;
				}
				mActionMode = BlockedSMSActivity.this.startSupportActionMode(mActionModeCallback);
			}
		});
    }

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.blocked_sm, menu);
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

					builder = new AlertDialog.Builder(BlockedSMSActivity.this);

					builder.setTitle("Delete selected SMS?")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									long SMSId = adapter.getItem(selectedIndex).id;

									blockedSmsDataSource.deleteSMS(SMSId);
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
				case R.id.action_recover:
					builder = new AlertDialog.Builder(BlockedSMSActivity.this);

					builder.setTitle("SMS will move to inbox")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									long SMSId = adapter.getItem(selectedIndex).id;

									final SMSLocal SMS = blockedSmsDataSource.getSMSLocal(SMSId);

									AsyncTask<Integer,Integer,Integer> restTask = new AsyncTask<Integer, Integer, Integer>() {
										@Override
										protected Integer doInBackground(Integer... voids) {
											ContentProviderDataSource.restoreSMS(BlockedSMSActivity.this,SMS);
											blockedSmsDataSource.deleteSMS(SMS.id);
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


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blocked_sm, menu);
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
