package org.mobul.db;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class ObservationData {

	public final class Model {
		public Long id;
		public Long taskId;
		public Long state;
		public String serializedData;
		public Long taskCreated;
		public Long dataSize;

		public Model(Long id, Long taskId, Long state, String serializedData,
				Long taskCreated, Long dataSize) {
			this.id = id;
			this.taskId = taskId;
			this.state = state;
			this.serializedData = serializedData;
			this.taskCreated = taskCreated;
			this.dataSize = dataSize; 
		}
	}

	public static void create(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME
				+ " (id INTEGER PRIMARY KEY, task_id INTEGER, state INTEGER, "
				+ "serialized_data TEXT, task_created DATE, data_size INTEGER)");
	}

	public static void drop(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static final String TABLE_NAME = "observation_data";

	private SQLiteStatement insertSQL;
	private static final String INSERT = "INSERT INTO " + TABLE_NAME +
			"(task_id, state, serialized_data, task_created, data_size) " +
			"values (?, ?, ?, ?, ?)";

	private SQLiteDatabase db;

	public ObservationData(SQLiteDatabase db) {
		this.db = db;
		this.insertSQL = db.compileStatement(INSERT);
	}

	public synchronized long insert(long taskId, long state,
			String serializedData, long taskCreated, long dataSize) {
		this.insertSQL.bindLong(1, taskId);
		this.insertSQL.bindLong(2, state);
		this.insertSQL.bindString(3, serializedData);
		this.insertSQL.bindLong(4, taskCreated);
		this.insertSQL.bindLong(5, dataSize);
		return this.insertSQL.executeInsert();
	}

	public List<Model> selectByTaskIdAndState(Long taskId, Long state) {
		Cursor cursor = this.db.query(TABLE_NAME, null,
				"task_id = ? AND state = ?",
				new String[] { String.valueOf(taskId), String.valueOf(state) },
				null, null, null);
		List<Model> l = new LinkedList<ObservationData.Model>();

		if (cursor.moveToFirst()) {
			do {
				l.add(new Model(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), 
						cursor.getString(3), cursor.getLong(4), cursor.getLong(5)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return l;
	}

	public Model selectByTaskIdAndNotReady(Long taskId) {
		List<Model> models = selectByTaskIdAndState(taskId, 0L);
		if (models.size() == 0) {
			return null;
		}
		return models.get(0);
	}

	public Model selectById(Long id) {
		Cursor cursor = this.db.query(TABLE_NAME, null, "id = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		return singleObservationDataFromCursor(cursor);
	}

	public Model singleObservationDataFromCursor(Cursor cursor) {
		Model m = null;
		if (cursor.moveToFirst()) {
			m = new Model(cursor.getLong(0), cursor.getLong(1),
					cursor.getLong(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return m;
	}

	public List<Model> selectAllReady() {
		List<Model> l = new LinkedList<Model>();
		Cursor cursor = this.db.query(TABLE_NAME, null, "state = 1", null,
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				l.add(new Model(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), 
						cursor.getString(3), cursor.getLong(4), cursor.getLong(5)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return l;
	}
	
	public Model selectLastInserted(long taskId) {
		Model m = null;
		Cursor cursor = this.db.query(TABLE_NAME, null, "state = 1", null,
				null, null, "id DESC", "1");
		if (cursor.moveToFirst()) {
			m = new Model(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), 
					cursor.getString(3), cursor.getLong(4), cursor.getLong(5));
		}
		
		return m;
	}
	
	public int observationsCountByTaskId(long taskId) {
		Cursor cursor = this.db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE task_id = ? AND state = 1", 
											new String[]{String.valueOf(taskId)});
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		}
		
		return 0;
	}
	
	public int observationsCount() {
		Cursor cursor = this.db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE state = 1", null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		}
		
		return 0;
	}

	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	public void deleteByTaskId(Long taskId) {
		this.db.delete(TABLE_NAME, "task_id = ?",
				new String[] { String.valueOf(taskId) });
	}

	public void deleteNotReadyByTaskId(Long taskId) {
		this.db.delete(TABLE_NAME, "task_id = ? AND state = 0",
				new String[] { String.valueOf(taskId) });
	}

	public void deleteReadyByTaskId(Long taskId) {
		this.db.delete(TABLE_NAME, "task_id = ? AND state = 1",
				new String[] { String.valueOf(taskId) });
	}

	public void deleteById(Long id) {
		this.db.delete(TABLE_NAME, "id = ?",
				new String[] { String.valueOf(id) });
	}

}
