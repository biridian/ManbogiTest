package com.test.manbogitest;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class MRecord extends Activity {

	ListView listView;
	ListViewAdapter adapter;
	
//	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mrecordlayout);
		
		listView = (ListView) findViewById(R.id.ListView);
		adapter = new ListViewAdapter();
		listView.setAdapter(adapter);
		
//		db = new DatabaseHelper(getApplicationContext()).getReadableDatabase();
//		
//		String sql = "select mDate,mCnt from " +MView.TABLE_NAME2+" order by mDate;";
//		Cursor result = db.rawQuery(sql, null);
//        
//		while(result.moveToNext()){
//			String rDate = result.getString(0);
//			int rCnt = result.getInt(1);
//			adapter.addItem(rDate, rCnt, MView.calDis(rCnt));
//        	
//		}
//		result.close();
		
		
		ContentResolver cr = getContentResolver();
        String uri = "content://com.test.manbogitest.MProvider";
        String[] projection = { "mDate","mCnt" };
        Cursor c = cr.query(Uri.parse(uri), projection, null, null, null);
        
		while(c.moveToNext()){
			String rDate = c.getString(0);
			int rCnt = c.getInt(1);
			adapter.addItem(rDate, rCnt, MView.calDis(rCnt));
    	
		}
		c.close();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("MyLog", "onResume");
	}

	@Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
//    	if(db!=null){
//    		db.close();
//    	}
    	super.onDestroy();
    }

}
