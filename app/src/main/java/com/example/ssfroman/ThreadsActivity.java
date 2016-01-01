package com.example.ssfroman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.ThreadListItemAdapter;
import com.example.dataprovider.ContentProviderDataSource;
import com.example.model.ThreadListItem;

import java.util.List;

public class ThreadsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threads);

    }

	@Override
	protected void onResume() {
		super.onResume();

		populateList();
	}

	private void populateList() {

		final ListView threadList = (ListView)this.findViewById(R.id.listView);

		final ProgressDialog dialog = new ProgressDialog(this);


		AsyncTask<Integer,Integer,ThreadListItemAdapter> restTask = new AsyncTask<Integer, Integer, ThreadListItemAdapter>() {
			@Override
			protected void onPreExecute() {
				//super.onPreExecute();
				dialog.setMessage("Loading...");
				dialog.show();
			}

			@Override
			protected ThreadListItemAdapter doInBackground(Integer... voids) {
				ThreadListItemAdapter adapter = new ThreadListItemAdapter(ThreadsActivity.this, R.layout.thread_list_item_template,ContentProviderDataSource.getAllSMSThreads(ThreadsActivity.this));
				return adapter;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				//super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(ThreadListItemAdapter adapter) {
				//super.onPostExecute(adapter);

				if (adapter.getCount() > 0){
					ThreadsActivity.this.findViewById(R.id.textView).setVisibility(View.GONE);
				}
				threadList.setAdapter(adapter);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		};

		restTask.execute();





		threadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				long threadId = ((ThreadListItem)parent.getAdapter().getItem(position)).threadId;

				Intent intent = new Intent();
				intent.setClass(ThreadsActivity.this, SMSActivity.class);
				intent.putExtra("threadId", threadId);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.threads, menu);
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
