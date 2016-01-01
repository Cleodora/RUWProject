package com.example.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.model.ContactListItem;
import com.example.ssfroman.R;

import java.util.List;

public class ContactListAdapter extends ArrayAdapter<ContactListItem> {

	Context context;
	List<ContactListItem> items;
	int layoutResourceId;

	public ContactListAdapter(Context context, int layoutResourceId, List<ContactListItem> objects) {
		super(context, layoutResourceId, objects);

		this.context = context;
		items = objects;
		this.layoutResourceId = layoutResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		DataHolder holder;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DataHolder();

			holder.textViewDisplayName = (TextView) row.findViewById(R.id.textViewDisplayName);
			holder.textViewNumber = (TextView) row.findViewById(R.id.textViewNumber);

			row.setTag(holder);

		} else {
			holder = (DataHolder) row.getTag();
		}

		ContactListItem item = items.get(position);


		holder.textViewDisplayName.setText(item.getDisplayName());
		holder.textViewNumber.setText(item.getNumber());

		return row;
	}

	static class DataHolder {
		TextView textViewDisplayName;
		TextView textViewNumber;
	}
}