package com.example.ssfroman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.adapters.ThreadListItemAdapter;
import com.example.dataprovider.BlockedSMSDataSource;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.ThreadListItem;


public class BlockedThreadActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_thread);
    }

	@Override
	protected void onResume() {
		super.onResume();

		populateList();
	}

	private void populateList(){

		final ThreadListItemAdapter adapter = new ThreadListItemAdapter(this, R.layout.thread_list_item_template, BlockedSMSDataSource.getInstance(this).getThreads());
		if (adapter.getCount() > 0){
			this.findViewById(R.id.textView).setVisibility(View.GONE);
		}
		ListView threadList = (ListView)this.findViewById(R.id.listView);
		threadList.setAdapter(adapter);

		threadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String address = ((ThreadListItem)parent.getAdapter().getItem(position)).address;

				Intent intent = new Intent();
				intent.setClass(BlockedThreadActivity.this, BlockedSMSActivity.class);
				intent.putExtra("address", address);
				startActivity(intent);
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blocked_thread, menu);
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
