package org.mobul.db;

import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyObservationEvent extends AbstractObservationEvent {
	
	public static final String TABLE_NAME = "my_observation_events";

	public static void create(SQLiteDatabase db) {
		AbstractObservationEvent.create(db, TABLE_NAME);		
	}	
	
	public static void drop(SQLiteDatabase db) {
		AbstractObservationEvent.drop(db, TABLE_NAME);		
	}	

	public MyObservationEvent(SQLiteDatabase db) {
		super(db, TABLE_NAME);
		// TODO Auto-generated constructor stub
	}
	
	public Set<String> getParticipationKeys() {
		Set<String> keys = new HashSet<String>();
		
		Cursor cursor = db.query(TABLE_NAME, new String[]{"key"}, null,
				null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				keys.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		
		return keys;
	}

}
