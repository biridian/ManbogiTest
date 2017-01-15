package com.test.manbogitest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MView extends Activity implements View.OnClickListener, SensorEventListener {

	private Activity MViewActivity = this;
		
	String clientId = "Z1GneJil_T1B02b_zge0";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "rBH0cC4PYk";//애플리케이션 클라이언트 시크릿값";
    
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
    TextView tv1, tv2, tv3;
    public static int mCnt = 0;
    int mDis;
    String sDis, strCurDate;
    SQLiteDatabase db;
    
//    public final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationManager lm;
    double longitude, latitude;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mviewlayout);
	
		btn = (Button)findViewById(R.id.btn1);
		btn.setOnClickListener(this);

		tv1 = (TextView)findViewById(R.id.textView1);
		tv2 = (TextView)findViewById(R.id.textView2);
		tv3 = (TextView)findViewById(R.id.textView3);

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
        
        
        locPermissionCheck();
        
        
	}
	
	private final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d("MyLog", "onLocationChanged, location:" + location);
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
//            double altitude = location.getAltitude();   //고도
//            float accuracy = location.getAccuracy();    //정확도
//            String provider = location.getProvider();   //위치제공자

            Log.d("MyLog", "longitude: " + longitude);
            Log.d("MyLog", "latitude: " + latitude);
            
            
            
            
            
            new Thread() {
                public void run() {
                	String naverApi = getNaverApi();
                    Bundle bun = new Bundle();
                    bun.putString("NAVER_API", naverApi);
                    Message msg = handler.obtainMessage();
                    msg.setData(bun);
                    handler.sendMessage(msg);
                }
            }.start();
            
            
            
            
            
            
            
            
            
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.d("MyLog", "onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.d("MyLog", "onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.d("MyLog", "onProviderDisabled");
			tv3.setText("GPS 위성 사용을 체크하셔야 정확한 위치 서비스가 가능합니다.");
		}
		
	};
	
	private String getNaverApi(){
		Log.d("MyLog", "getNaverApi");
		String apiURL = "https://openapi.naver.com/v1/map/reversegeocode?query=" + longitude+","+latitude;
        String result = "";
		
        try {
        	Log.d("MyLog", "apiURL: " + apiURL);
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if(responseCode==200) { // 정상 호출
			    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {  // 에러 발생
			    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            result = response.toString();
            Log.d("MyLog", "result: " + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return result;
	}

	Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String naverApi = bun.getString("NAVER_API");
            
            String sigugun = "";
        	String dongmyun = "";
        	try {
        		
        		Log.d("MyLog", "JSON");
        		JSONObject jObject = new JSONObject(naverApi);
        		Log.d("MyLog", "JSON2");
        		
        		JSONObject jObject2 = jObject.getJSONObject("result");
        		Log.d("MyLog", "JSON3");
        		JSONArray jarray = jObject2.getJSONArray("items");
        		Log.d("MyLog", "JSON4");
        		if(jarray.length() > 0){
        			JSONObject jObject3 = jarray.getJSONObject(0);
        			JSONObject jObject4 = jObject3.getJSONObject("addrdetail");
        			
        			
        			sigugun = jObject4.getString("sigugun");
	                dongmyun = jObject4.getString("dongmyun");
	                
	                Log.d("MyLog", "sigugun: " + sigugun);
	                Log.d("MyLog", "dongmyun: " + dongmyun);
        		}
        		
        		
        		
        		
//				JSONArray jarray = new JSONArray(naverApi);
//				Log.d("MyLog", "JSON2");
//				if(jarray.length() > 0){
//					JSONObject jObject = jarray.getJSONObject(0);

//					sigugun = jObject.getString("sigugun");
//	                dongmyun = jObject.getString("dongmyun");
//
//	                Log.d("MyLog", "sigugun: " + sigugun);
//	                Log.d("MyLog", "dongmyun: " + dongmyun);
					
//					JSONObject jObject2 = jObject.getJSONObject("result");
//					Log.d("MyLog", "jObject: " + jObject.toString());
//				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("MyLog", "JSONException: "+e.toString());
			}
        	
            tv3.setText(sigugun+" "+dongmyun);
        }
    };
    
	public void locPermissionCheck(){
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
        	
        	Log.d("MyLog", "permissionCheck 1: " + permissionCheck);
        	
        	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, mLocationListener);
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
//                    100, // 통지사이의 최소 시간간격 (miliSecond)
//                    1, // 통지사이의 최소 변경거리 (m)
//                    mLocationListener);



        } else {
        	tv3.setText("'이 기기의 위치에 액세스하기' 권한을 허용하셔야 현재 위치를 가져올 수 있습니다.");
        }
	}

//	public void checkPermission() {
//		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        
//        Log.d("MyLog", "permissionCheck: "+permissionCheck);
//        
//        if(permissionCheck== PackageManager.PERMISSION_DENIED){
//        	Log.d("MyLog", "PERMISSION_DENIED 1");
//        	ActivityCompat.requestPermissions(MViewActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//	}
//	
//	@Override
//	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//		switch (requestCode) {
//			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
//				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					Log.d("MyLog", "PERMISSION_GRANTED");
//					
//				} else {
//					Log.d("MyLog", "PERMISSION_DENIED 2");
//					
//					tv3.setText("권한을 승인해주세요");
//				}
//
//		}
//	}


		

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
