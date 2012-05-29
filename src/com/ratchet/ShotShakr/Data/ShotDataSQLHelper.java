package com.ratchet.ShotShakr.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ShotDataSQLHelper extends SQLiteOpenHelper {
	 private static final String DATABASE_NAME = "shotshakr.db";
	   private static final int DATABASE_VERSION = 3;
	   
	   private static final String SHOT_TABLE_NAME = "shot";
	   private static final String FILTER_TABLE_NAME = "filter";


	public ShotDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	 @Override
     public void onCreate(SQLiteDatabase db) {
		 Log.w("SHOTDATA", "Creating Tables");
        db.execSQL("CREATE TABLE " + SHOT_TABLE_NAME + "(id INTEGER PRIMARY KEY, name TEXT, ingredients TEXT, instructions TEXT, amount TEXT)");
        db.execSQL("CREATE TABLE " + FILTER_TABLE_NAME + "(id INTEGER PRIMARY KEY, shot_id INTEGER, filter_type TEXT)");
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Example", "Upgrading database, this will drop tables and recreate.");
        db.execSQL("DROP TABLE IF EXISTS " + SHOT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FILTER_TABLE_NAME);
        onCreate(db);
     }
	

}
