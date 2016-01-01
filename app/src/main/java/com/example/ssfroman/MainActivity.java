package com.example.ssfroman;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dataprovider.ContentProviderDataSource;
import com.example.dataprovider.RomanUrduRepresentationDataSource;
import com.example.dataprovider.RomanUrduWordDataSource;
import com.example.model.RomanUrduWord;
import com.example.model.SMSLocal;

import java.util.List;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


        /*"content://sms"
		0- _id 						int
        1- thread_id 				int
        2- address 					string
        3- person					int
        4- date						int
        5- date_sent 				int
        6- protocol					int
        7- read						int
        8- status					int
        9- type						int
        10- reply_path_present		int
        11- subject					string
        12- snippet //body			string
        13- service_center			string
        14- locked					int
        15- error_code				int
        16- seen					int	*/

        /*0- _id
		1- date
        2- message_count
        3- recipient_ids
        4- snippet
        5- snippet_cs
        6- read
        7 -type
        8- error
        9- has_attachment
        */

		(findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

//				List<RomanUrduWord> ruw = RomanUrduWordDataSource.getInstance(MainActivity.this).getTestWords();
//
//				if(ruw != null) {
//					for(RomanUrduWord r:ruw) {
//
//						Log.d("privateLog", r.id + ", " + r.word);
//					}
//				}else{
//					Log.d("privateLog","no recod");
//				}

                List<SMSLocal> rndSMS = ContentProviderDataSource.getRandomSMSFromInbox(MainActivity.this,3,null);

                if(rndSMS != null) {
					for(SMSLocal r:rndSMS) {

						Log.d("privateLog", r.id + ", " + r.body);
					}
				}else{
					Log.d("privateLog","no recod");
				}


				/*Uri uriSMSURI = Uri.parse("content://mms-sms/conversations?simple=true");
				Cursor cur = getContentResolver().query( uriSMSURI, null, null, null, null );

				if ( cur.moveToNext() ) {
					for ( int i = 0; i < cur.getColumnCount(); i++ ) {
						Log.d("privateLog", cur.getColumnName(i) + " " + cur.getType(i) + "  " + cur.getString(i));
					}
				}*/

				/*ContentValues values = new ContentValues();
				values.put( "address", "5005" );
				values.put( "date", new Date().getTime() );
				values.put( "read", 1 );
				values.put( "status", -1 );
				values.put( "type", 1 );
				values.put( "body", "This is a test" );
				values.put( "locked", 0 );
				values.put( "error_code", 0 );
				values.put( "seen", 1 );

				getContentResolver().insert( Uri.parse( "content://sms/inbox" ), values );*/


				/*Uri uriSMSURI = Uri.parse( "content://sms/inbox" );
				Cursor cur = getContentResolver().query( uriSMSURI, null, null, null, null );

				while ( cur.moveToNext() ) {
					for ( int i = 0; i < cur.getColumnCount(); i++ ) {
						Log.d( "privateLog", cur.getColumnName( i ) +" "+ cur.getType( i )+"  " + cur.getString( i ) );
					}
				}*/

                /* Cursor cursor = getContentResolver().query( uriSms, new String[]{"person", "address", "date", "snippet"}, null, null, null );

                cursor.moveToFirst();

                String sms = "";
                while ( cursor.moveToNext() ) {
                    SMSlist.add( cursor.getString( 0 ) + ": " + cursor.getString( 1 ) + " : " + new Date( cursor.getLong( 2 )).toString() + ": " + cursor.getString( 3 ) );
                }*/

				/*final SMSListItemAdapter adapter = new SMSListItemAdapter( MainActivity.this, R.layout.sms_list_item_template, sil );
				((ListView) MainActivity.this.findViewById( R.id.listViewSMSs )).setAdapter( adapter );*/
			}
		});

        /*String contact=address;
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cs= context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME},PhoneLookup.NUMBER+"='"+address+"'",null,null);

        if(cs.getCount()>0)
        {
            cs.moveToFirst();
            contact=cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
