package com.test.manbogitest;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MView extends Activity implements View.OnClickListener, SensorEventListener {

	private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
 
    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;
 
    public static final String DB_NAME = "db01";
    public static final String TABLE_NAME1 = "mflag";
    public static final String TABLE_NAME2 = "mdata";
    
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
    
    private boolean cflag = false;
    Button btn;
    TextView tv1, tv2;
    int mCnt = 0;
    int mDis;
    String sDis, strCurDate;
    SQLiteDatabase db;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mviewlayout);
	
		btn = (Button)findViewById(R.id.btn1);
		btn.setOnClickListener(this);

		tv1 = (TextView)findViewById(R.id.textView1);
		tv2 = (TextView)findViewById(R.id.textView2);

		db = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
		
        	
        
        
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		strCurDate = CurDateFormat.format(date);
		Log.d("MyLog", "strCurDate: "+strCurDate);
		
		
		String sql = "select mDate,mCnt from " +TABLE_NAME2+" where mDate='"+strCurDate+"';";
        Cursor result = db.rawQuery(sql, null);
        
        if(!result.moveToNext()){
        	db.execSQL("insert into "+TABLE_NAME2+" (mDate,mCnt) values('"+strCurDate+"',0);");
        } else {
        	mCnt = result.getInt(1);
        	
        	tv1.setText(""+mCnt);
//        	mDis = mCnt * 80;
        	tv2.setText(calDis(mCnt));
        }
        result.close();
        
        
        
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if(cflag){
			if (sensorManager != null)
	            sensorManager.unregisterListener(this);
			
			sensorManager = null;
	        accelerormeterSensor = null;
	        
	        cflag = false;
	        
	        btn.setText("START");
	        
	        db.execSQL("update "+TABLE_NAME1+" set cflag='0';");
	        
	        db.execSQL("update "+TABLE_NAME2+" set mCnt="+mCnt+" where mDate='"+strCurDate+"';");
		} else {
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        
	        if (accelerormeterSensor != null)
	            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
	        
	        cflag = true;
	        
	        btn.setText("STOP");
	        
	        db.execSQL("update "+TABLE_NAME1+" set cflag='1';");


	        
//	        ContentValues values = new ContentValues();
//	        values.put("cflag", 1);
//	        SQLiteDatabase db = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
//	        
////	        if (-1 != db.insert(DB_NAME, null, values)) {
////                Toast.makeText(getApplicationContext(), "save", Toast.LENGTH_SHORT).show();
////            }
//	        
//	        db.insert(DB_NAME, null, values);
//	        
//            db.close();
		}
		
	}


	@Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
        
        
        String sql = "select cflag from " +TABLE_NAME1+";";
        Cursor result = db.rawQuery(sql, null);
        
        if(!result.moveToNext()){
        	Log.d("MyLog", "sres == null");
        	db.execSQL("insert into "+TABLE_NAME1+" (cflag) values('0');");
        } else {
        	
        	String sres = result.getString(0);
            
            Log.d("MyLog", "sres not null: "+sres);
            
            if("1".equals(sres)){
            	cflag = false;
            } else {
            	cflag = true;
            }
            btn.performClick();
        }
        
        result.close();
    }
 
    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }
 
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	if(db!=null){
    		db.close();
    	}
    	super.onDestroy();
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
 
    }
 
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
 
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
 
                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
                	Log.d("MyLog", "SHAKE");
                	mCnt++;
                	tv1.setText(""+mCnt);
//                	btn.setText("");
//                	mDis = mCnt * 80;
                	tv2.setText(calDis(mCnt));
                }
 
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
 
        }
 
    }

    
    public static String calDis(int mCnt){
    	
    	String res;
    	int mDis = mCnt*80;
    	mDis = mDis/100;
    	
    	if(mDis < 1){
    		return "0m";
    	} else if(mDis < 1000){
    		return mDis+"m";
    	} else {
    		res = mDis/1000 + ".";
    		res += (mDis%1000)/100 + "km";
    		
    		return res;
    	}
    	
    }
    

}
