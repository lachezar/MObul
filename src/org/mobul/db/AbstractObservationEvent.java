package org.mobul.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import org.mobul.utils.StringUtils;

/**
 * Created by IntelliJ IDEA. User: lucho Date: 10-11-3 Time: 13:11 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractObservationEvent {

	public static final class Model {
		public Long id;
		public String key;
		public String title;
		public String description;
		public String observationDataRequest;
		public String tags;
		public String country;
		public String province;
		public String city;
		public Long technicalParams;
		public Long version;

		public Model(Long id, String key, String title, String description, String observationDataRequest,
				String tags, String country, String province, String city, Long technicalParams, Long version) {
			this.id = id;
			this.key = key;
			this.title = title;
			this.description = description;
			this.observationDataRequest = observationDataRequest;
			this.tags = tags;
			this.country = country;
			this.province = province;
			this.city = city;
			this.technicalParams = technicalParams;
			this.version = version;
		}
	}

	public static void create(SQLiteDatabase db, String tableName) {
		db.execSQL("CREATE TABLE "
				+ tableName
				+ " (id INTEGER PRIMARY KEY, key VARCHAR(32), title VARCHAR(255), "
				+ "description TEXT, observation_data_request TEXT, tags TEXT, "
				+ "country TEXT, province TEXT, city TEXT, technical_params INTEGER, version DATE)");
	}

	public static void drop(SQLiteDatabase db, String tableName) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	//public static final String TABLE_NAME = "observation_events";

	private SQLiteStatement insertSQL;
	protected SQLiteDatabase db;
	protected String tableName;

	public AbstractObservationEvent(SQLiteDatabase db, String tableName) {
		this.tableName = tableName;
		this.db = db;
		this.insertSQL = db.compileStatement("INSERT INTO "
				+ tableName
				+ "(key, title, description, observation_data_request, tags, country, "
				+ "province, city, technical_params, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	}

	public synchronized long insert(String key, String title, String description, String observationDataRequest,
			String tags, String country, String province, String city, long technicalParams, long version) {
		this.insertSQL.bindString(1, key);
		this.insertSQL.bindString(2, title);
		this.insertSQL.bindString(3, description);
		this.insertSQL.bindString(4, observationDataRequest);
		this.insertSQL.bindString(5, tags);
		this.insertSQL.bindString(6, country);
		this.insertSQL.bindString(7, province);
		this.insertSQL.bindString(8, city);
		this.insertSQL.bindLong(9, technicalParams);
		this.insertSQL.bindLong(10, version);
		return this.insertSQL.executeInsert();
	}

	public Model selectById(Long id) {
		Cursor cursor = this.db.query(tableName, null, "id = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		return singleOEFromCursor(cursor);
	}

	public Model selectByKey(String key) {
		Cursor cursor = this.db.query(tableName, null, "key = ?",
				new String[] { key }, null, null, null);
		return singleOEFromCursor(cursor);
	}

	public Model singleOEFromCursor(Cursor cursor) {
		Model m = null;
		if (cursor.moveToFirst()) {
			m = new Model(cursor.getLong(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7),
					cursor.getString(8), cursor.getLong(9),
					cursor.getLong(10));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return m;
	}

	public List<Model> selectAllForList(String limit, String order,
			String titleLike, String[] tagsLike, String countryLike,
			String provinceLike, String cityLike, boolean worldwide) {
		List<Model> l = new LinkedList<Model>();
		Cursor cursor = cursorForOEs(limit, order, titleLike, tagsLike,
				countryLike, provinceLike, cityLike, worldwide);
		if (cursor.moveToFirst()) {
			do {
				l.add(new Model(cursor.getLong(0), cursor.getString(1), 
						cursor.getString(2), cursor.getString(3), null,
						cursor.getString(4), cursor.getString(5), cursor.getString(6), 
						cursor.getString(7), cursor.getLong(8), cursor.getLong(9)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return l;
	}
	
	public List<String[]> selectAllKeyVersion() {
		Cursor cursor = db.query(tableName, new String[]{"key", "version"}, null, null, null, null, null);
		List<String[]> l = new LinkedList<String[]>();
		if (cursor.moveToFirst()) {
			do {
				l.add(new String[]{cursor.getString(0), String.valueOf(cursor.getLong(1))});
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return l;
	}
	
	public Cursor cursorForOEsByKeyword(String keyword, String limit, String order) {
		String titleLikeClause = buildLikeClause(keyword, "title", "OR");
		String tagsLikeClause = buildLikeClause(keyword, "tags", "OR");
		String countryLikeClause = buildLikeClause(keyword, "country", "OR");
		String provinceLikeClause = buildLikeClause(keyword, "province", "OR");
		String cityLikeClause = buildLikeClause(keyword, "city", "OR");
		
		String clause = new StringBuilder().append(titleLikeClause).append(tagsLikeClause)
						.append(countryLikeClause).append(provinceLikeClause).append(cityLikeClause).toString();
		
		if (StringUtils.isEmpty(clause)) {
			clause = "1=1";
		} else {
			clause += "1=0";
		}
		
		String sql = getSQLForItem(clause, order, limit);
		Cursor cursor = this.db.rawQuery(sql, new String[] {});
		return cursor;
	}

	public Cursor cursorForOEs(String limit, String order, String titleLike,
			String[] tagsLike, String countryLike, String provinceLike,
			String cityLike, boolean worldwide) {

		String titleLikeClause = buildLikeClause(titleLike, "title");

		String tagsLikeClause = "";
		if (tagsLike != null && tagsLike.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append("tags LIKE '%")
					.append(DataHelper.escapeLike(tagsLike[0])).append("%'");
			for (int i = 1; i < tagsLike.length; i++) {
				sb.append(" OR tags LIKE '%")
						.append(DataHelper.escapeLike(tagsLike[i]))
						.append("%'");
			}
			sb.append(") AND ");
			tagsLikeClause = sb.toString();
		}

		String countryLikeClause = buildLikeClause(countryLike, "country");
		String provinceLikeClause = buildLikeClause(provinceLike, "province");
		String cityLikeClause = buildLikeClause(cityLike, "city");

		String regionClause = "(" + countryLikeClause + provinceLikeClause
				+ cityLikeClause + "1=1)";
		if (worldwide) {
			regionClause = "("
					+ regionClause
					+ " OR (country IS NULL AND province IS NULL AND city IS NULL))";
		}

		String clause = titleLikeClause + tagsLikeClause + regionClause;

		String sql = getSQLForItem(clause, order, limit);

		Log.i(tableName, sql);

		Cursor cursor = this.db.rawQuery(sql, new String[] {});
		return cursor;
	}
	
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
		+ "tags, country, province, city "
		+ " FROM "
		+ tableName
		+ " WHERE "
		+ clause
		+ " "
		+ order
		+ " " + limit;
	}
	
	private String buildLikeClause(String token, String columnName) {
		return buildLikeClause(token, columnName, "AND");
	}

	private String buildLikeClause(String token, String columnName, String connector) {
		String clause = "";
		if (token != null && !token.equals("")) {
			clause = columnName + " LIKE " + DataHelper.escapeLike(token) + " " + connector + " ";
		}

		return clause;
	}
	
	public void replace(List<Model> newOEs) {
		for (Model m : newOEs) {
			deleteByKey(m.key);
			insert(m.key, m.title, m.description, m.observationDataRequest, m.tags,
					m.country, m.province, m.city, m.technicalParams, m.version);
			
		}
	}

	public void deleteAll() {
		this.db.delete(tableName, null, null);
	}

	public void deleteByKey(String key) {
		this.db.delete(tableName, "key = ?", new String[] { key });
	}

	public void deleteById(Long id) {
		this.db.delete(tableName, "id = ?",
				new String[] { String.valueOf(id) });
	}

}
