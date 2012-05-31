package com.ratchet.ShotShakr.Data;

import java.util.ArrayList;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class ShotRepository {

	private static final String SHOT_TABLE_NAME = "shot";
	private static final String FILTER_TABLE_NAME = "filter";


	private SQLiteDatabase database;
	private ShotDataSQLHelper dbHelper;

	private SQLiteStatement shotinsertStmt;
	private SQLiteStatement filterinsertStmt;

	private static final String SHOT_INSERT = "insert into "
			+ SHOT_TABLE_NAME
			+ "(id, name, ingredients, instructions, amount) values (?,?,?,?,?)";

	private static final String FILTER_INSERT = "insert into "
			+ FILTER_TABLE_NAME + "(id, shot_id, filter_type) values (?,?,?)";

	public ShotRepository(Context context) {
		
		this.dbHelper = new ShotDataSQLHelper(context);
	}

	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
		this.shotinsertStmt = this.database.compileStatement(SHOT_INSERT);
		this.filterinsertStmt = this.database.compileStatement(FILTER_INSERT);
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public long shotInsert(Shot shot) {
		this.shotinsertStmt.bindLong(1, shot.shotId);
		this.shotinsertStmt.bindString(2, shot.shotName);
		this.shotinsertStmt.bindString(3, shot.ingredients);
		this.shotinsertStmt.bindString(4, shot.instructions);
		this.shotinsertStmt.bindString(5, shot.amount);
		return this.shotinsertStmt.executeInsert();
	}

	public long filterInsert(int id, int shotId, String filterType) {
		this.filterinsertStmt.bindLong(1, id);
		this.filterinsertStmt.bindLong(2, shotId);
		this.filterinsertStmt.bindString(3, filterType);

		return this.filterinsertStmt.executeInsert();
	}

	public void deleteAllShots() {
		Log.w("ShotRepo", "Deleting Shots");
		this.database.delete(SHOT_TABLE_NAME, null, null);
	}

	public void deleteAllFilters() {
		Log.w("ShotRepo", "Deleting Filters");
		this.database.delete(FILTER_TABLE_NAME, null, null);
	}


	public ArrayList<Shot> getAllShots() {
		ArrayList<Shot> list = new ArrayList<Shot>();
		Cursor cursor = this.database.query(SHOT_TABLE_NAME, new String[] { "id",
				"name", "ingredients", "instructions", "amount" }, null, null,
				null, null, "name desc");
		if (cursor.moveToFirst()) {
			do {
				Shot shot = new Shot(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4));

				list.add(shot);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		close();
		return list;
	}

	public ArrayList<Shot> getShots(ArrayList<String> filter) {
		Log.w("ShotRepository",
				"getShots() getting shots basted on filters...count"
						+ filter.size());

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables("shot JOIN filter ON (shot.id = filter.shot_id)");

		String[] filters = new String[filter.size()];
		String selection = "";
		String whereClause = "";
		for (int i = 0; i < filter.size(); i++) {

			Log.w("ShotRepository", "Filter = " + filter.get(i));
			filters[i] = filter.get(i);
			selection += " ,'" + filters[i] + "'";
		}
		if (!selection.equals("")) {
			selection = selection.replaceFirst(" ,", "");
			whereClause = "where filter_type IS null OR filter_type NOT IN ("
					+ selection + ")";
		}
		String q = "select filter_type, shot.id, name, ingredients, instructions, amount from shot LEFT OUTER JOIN filter on (shot.id = filter.shot_id)"
				+ whereClause;

		Log.w("ShotRepository", "Filter = " + filters[0]);
		Cursor cursor = this.database.rawQuery(q, null);
		// Cursor cursor = builder.query(this.db, new String[] {"filter_type",
		// "shot.id", "name", "ingredients", "instructions", "amount" },null,
		// null, null, null);

		ArrayList<Shot> list = new ArrayList<Shot>();
		Log.w("ShotRepository", "Returned shots = " + cursor.getCount());
		if (cursor.moveToFirst()) {
			do {

				Shot shot = new Shot(cursor.getInt(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5));
				list.add(shot);

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		close();
		return list;

	}
}
