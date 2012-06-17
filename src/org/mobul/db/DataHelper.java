package org.mobul.db;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IntelliJ IDEA. User: lucho Date: 10-11-3 Time: 12:46 To change
 * this template use File | Settings | File Templates.
 */

public class DataHelper {

	private static final String TAG = "DataHelper";

	private static final String DATABASE_NAME = "mob.db";
	private static final int DATABASE_VERSION = 7;

	private static SQLiteDatabase db;

	private MyObservationEvent myoe = null;
	private PublicObservationEvent publicoe = null;
	private ObservationData od = null;

	public DataHelper(Context context) {
		singleDBConnection(context);
	}

	private static void singleDBConnection(Context context) {
		if (db == null) {
			OpenHelper openHelper = new OpenHelper(context);
			db = openHelper.getWritableDatabase();
		}
	}

	public void close() {
		db.close();
		db = null;
	}
	
	public AbstractObservationEvent getObservationEvent(String tableName) {
		AbstractObservationEvent aoe;
		if (tableName.equals(MyObservationEvent.TABLE_NAME)) {
			aoe = new MyObservationEvent(db);
		} else if (tableName.equals(PublicObservationEvent.TABLE_NAME)) {
			aoe = new PublicObservationEvent(db);
		} else {
			aoe = null;
		}
		
		return aoe;
	}

	public MyObservationEvent getMyObservationEvent() {
		if (myoe == null) {
			myoe = new MyObservationEvent(db);
		}
		return myoe;
	}
	
	public PublicObservationEvent getPublicObservationEvent() {
		if (publicoe == null) {
			publicoe = new PublicObservationEvent(db);
		}
		return publicoe;
	}

	public ObservationData getObservationData() {
		if (od == null) {
			od = new ObservationData(db);
		}
		return od;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			MyObservationEvent.create(db);
			PublicObservationEvent.create(db);
			ObservationData.create(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "Upgrading database, this will drop tables and recreate.");
			PublicObservationEvent.drop(db);
			MyObservationEvent.drop(db);
			ObservationData.drop(db);
			onCreate(db);
		}
	}

	public static String escape(String w) {
		return DatabaseUtils.sqlEscapeString(w);
	}

	public static String escapeLike(String w) {
		return DatabaseUtils.sqlEscapeString("%" + w + "%");
	}

	public static String escapeLower(String w) {
		return DatabaseUtils.sqlEscapeString(w).toLowerCase();
	}
}
