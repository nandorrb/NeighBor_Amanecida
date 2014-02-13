package com.uni.neighbor;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ClientCursorAdapter extends ResourceCursorAdapter {

	public ClientCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(cursor.getString(cursor.getColumnIndex("DEFINITION")));
		TextView Price = (TextView) view.findViewById(R.id.Price);
		
		TextView Brand = (TextView) view.findViewById(R.id.Brand);
		Brand.setText(cursor.getString(cursor.getColumnIndex("BRAND")));
		ImageView Image = (ImageView) view.findViewById(R.id.Offer);
		if (cursor.getFloat(cursor.getColumnIndex("OFFER")) > 0) {
			Image.setImageResource(R.drawable.offer);
			Float offer=cursor.getFloat(cursor.getColumnIndex("OFFER"))*cursor.getFloat(cursor.getColumnIndex("PRICE"));
			Price.setText(cursor.getString(cursor.getColumnIndex("PRICE")) + "$" + " now: "+String.valueOf(offer)+ "$");
		} else {
			Image.setImageResource(android.R.color.transparent);
			Price.setText(cursor.getString(cursor.getColumnIndex("PRICE")) + "$");
		}

	}
}