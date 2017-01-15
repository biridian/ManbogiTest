package com.test.manbogitest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseHelper(Context context) {
        super(context, MView.DB_NAME, null, 1);
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

		String sql1 = "CREATE TABLE " + MView.TABLE_NAME1 + "(cflag TEXT)";
		String sql2 = "CREATE TABLE " + MView.TABLE_NAME2 + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, mDate TEXT, mCnt INTEGER)";
		arg0.execSQL(sql1);
		arg0.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
