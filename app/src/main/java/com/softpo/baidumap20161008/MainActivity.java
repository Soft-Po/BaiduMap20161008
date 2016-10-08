package com.softpo.baidumap20161008;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
	}
	
	//点击按钮跳转到相应功能的展示界面
	public void click(View view){
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.button_base://基础地图
			intent.setClass(MainActivity.this, BaseMapActivity.class);
			break;
		case R.id.button_addOverlay://添加覆盖物
			intent.setClass(MainActivity.this, AddOverlayActivity.class);
			break;
		case R.id.button_location://地图定位
			intent.setClass(MainActivity.this, LocationActivity.class);
			break;
		case R.id.button_poiSearch://Poi检索
			intent.setClass(MainActivity.this, PoisearchActivity.class);
			break;
		case R.id.button_routePlan://线路规划
			intent.setClass(MainActivity.this, RoutePlanActivity.class);
			break;
		default:
			break;
		}
		
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
