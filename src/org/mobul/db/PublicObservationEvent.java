package org.mobul.db;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class PublicObservationEvent extends AbstractObservationEvent {

	public static final String TABLE_NAME = "public_observation_events";
	
	public static void create(SQLiteDatabase db) {
		AbstractObservationEvent.create(db, TABLE_NAME);		
	}	
	
	public static void drop(SQLiteDatabase db) {
		AbstractObservationEvent.drop(db, TABLE_NAME);		
	}	

	public PublicObservationEvent(SQLiteDatabase db) {
		super(db, TABLE_NAME);
	}
	
	@Override
	protected String getSQLForItem(String clause, String order, String limit) {
		if (limit == null) {
			limit = "";
		} else {
			limit = " LIMIT " + limit;
		}
		
		if (order == null) {
			order = "";
		} else {
			order = " ORDER BY " + order;
		}
		
		return "SELECT id AS _id, key, title, description, SUBSTR(description, 0, 128) || '...' AS formated_description, "
		+ "tags, country, province, city, "
		+ "CASE WHEN key IN (SELECT key FROM " + MyObservationEvent.TABLE_NAME + ") THEN '(Participating)'"
		+ "ELSE ''"
		+ "END AS participating"
		+ " FROM "
		+ tableName
		+ " WHERE "
		+ clause
		+ " "
		+ order
		+ " " + limit;
	}
	
	public void insert(List<Model> l) {
		for (Model m : l) {
			insert(m.key, m.title, m.description, m.observationDataRequest, m.tags, m.country, m.province, m.city, m.technicalParams, m.version);
		}
	}
	
}
