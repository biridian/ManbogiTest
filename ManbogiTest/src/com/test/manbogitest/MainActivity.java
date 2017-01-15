package com.test.manbogitest;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

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
}
