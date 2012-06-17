package org.mobul.db.adapter;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class OEsCursorAdapter extends SimpleCursorAdapter {
	
	private Set<Long> participatingIds;

	public OEsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, Set<String> participatingKeys) {
		super(context, layout, c, from, to);
		participatingIds = new HashSet<Long>();
		if (c.moveToFirst()) {
			do {
				if (participatingKeys.contains(c.getString(1))) {
					participatingIds.add(c.getLong(0));
				}
			} while (c.moveToNext());
		}
		c.moveToFirst();		
		
		//this.participatingIds = participatingIds;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		ViewGroup vg = (ViewGroup)v;
		if (participatingIds.contains(getItemId(position))) {
			vg.getChildAt(0).setVisibility(View.VISIBLE);
		}
		//getCursor().moveToPosition(position)
		//vg.getChildAt(0).setVisibility(View.VISIBLE);
		return v;
	}

}
