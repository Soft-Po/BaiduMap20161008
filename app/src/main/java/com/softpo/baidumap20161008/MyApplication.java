package com.softpo.baidumap20161008;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		//强烈建议将百度地图SDK初始化的操作放在Application中
		SDKInitializer.initialize(getApplicationContext());
	}
}
