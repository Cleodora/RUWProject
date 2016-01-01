package com.example.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.SMSListItem;
import com.example.ssfroman.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SMSListItemAdapter extends ArrayAdapter<SMSListItem> {

	Context context;
	List<SMSListItem> items;
	int layoutResourceId;

	public SMSListItemAdapter( Context context, int layoutResourceId, List<SMSListItem> objects ) {
		super( context, layoutResourceId, objects );

		this.context = context;
		items = objects;
		this.layoutResourceId = layoutResourceId;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		View row = convertView;
		DataHolder holder;

		if ( row == null ) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate( layoutResourceId, parent, false );

			holder = new DataHolder();


			holder.imageView = (ImageView) row.findViewById( R.id.imageView );
			holder.textViewBody = (TextView) row.findViewById( R.id.textViewBody );
			holder.textViewDateTime = (TextView) row.findViewById( R.id.textViewDateTime );

			row.setTag( holder );

		} else {
			holder = (DataHolder) row.getTag();
		}

		SMSListItem item = items.get( position );

		//holder.imageView.setImageResource( item.imageId );
		holder.textViewBody.setText( item.body );

		String dateTime = "";
		if ( compareDate( item.dateTime ) == 1 )
			dateTime = new SimpleDateFormat( "hh:mm a" ).format( item.dateTime );
		else if ( compareDate( item.dateTime ) == 0 )
			dateTime = new SimpleDateFormat( "MMM d" ).format( item.dateTime );
		else
			dateTime = new SimpleDateFormat( "MMM d, yyyy" ).format( item.dateTime );

		holder.textViewDateTime.setText( dateTime );

		return row;
	}

	int compareDate( Date date ) {
		Date d = new Date();
		if ( d.getDate() == date.getDate() && d.getMonth() == date.getMonth() && d.getYear() == date.getYear() ) {
			return 1;
		} else if ( d.getYear() > date.getYear() ) {
			return -1;
		} else {
			return 0;
		}
	}

	static class DataHolder {
		ImageView imageView;
		TextView textViewBody;
		TextView textViewDateTime;
	}
}

