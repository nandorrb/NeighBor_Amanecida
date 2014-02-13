package com.uni.neighbor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String TABLE_PRODUCTS = "PRODUCTS";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DEFINITION = "DEFINITION";
	public static final String COLUMN_PRICE = "PRICE";
	public static final String COLUMN_BRAND = "BRAND";
	public static final String COLUMN_OFFER= "OFFER";
	public static final String COLUMN_BARCODE = "BARCODE";

	private static final String DATABASE_NAME = "products.db";
	private static final int DATABASE_VERSION = 19;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PRODUCTS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_DEFINITION
			+ " text, " + COLUMN_PRICE + " integer, " + COLUMN_BRAND
			+ " text, " + COLUMN_OFFER + " integer, " + COLUMN_BARCODE
			+ " integer  " + ");";

	private static final String DATABASE_CREATE2 = "create table " + "videos"
			+ "(" + "_id" + " integer primary key autoincrement, " + "title"
			+ " text  " + ");";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE2);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		db.execSQL("DROP TABLE IF EXISTS " + "videos");
		onCreate(db);
	}

	public Cursor getListCursor(String userfk) throws Exception {
	    StringBuilder builder = new StringBuilder("select name as _id from mytable where  userfk=?");
	    List<String> values = new ArrayList<String>();
	    values.add(userfk);
	    try {
	        return this.getReadableDatabase().rawQuery(builder.toString(), values.toArray(new String[values.size()]));
	    } catch (Exception e) {
	        Log.d("test", e.getMessage());
	        throw e;
	    }
	}
}
