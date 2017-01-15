package com.test.manbogitest;

import android.Manifest;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;


public class MainActivity extends TabActivity {

	public final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	public static boolean locPermission;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);
        
        TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;

		Intent intent;

		

		intent = new Intent(this, MView.class);
		spec = tabHost.newTabSpec("mView").setIndicator("만보기 화면").setContent(intent);
		tabHost.addTab(spec);


		intent = new Intent(this, MRecord.class);
		spec = tabHost.newTabSpec("mRecord").setIndicator("만보기 기록").setContent(intent);
		tabHost.addTab(spec);

		checkPermission();

		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(!Settings.canDrawOverlays(this)){
				Toast.makeText(getApplicationContext(), "manbogitest 앱의 '다른 앱 위에 표시되는 앱'설정 허용이 필요합니다.", Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
		        startActivity(myIntent);
			} 
	        
	    }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    public void checkPermission() {
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
        	ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					locPermission = true;
				} else {
					locPermission = false;
				}

		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(Settings.canDrawOverlays(this)){
			mStart();
		}
		
		super.onStop();
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		mStop();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	public void mStart() {
		startService(new Intent(this, MService.class));
	}
	
	public void mStop() {
		stopService(new Intent(this, MService.class));
	}
	
}
