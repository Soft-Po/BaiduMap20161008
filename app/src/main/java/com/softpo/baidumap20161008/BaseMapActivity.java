package com.softpo.baidumap20161008;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

/**
 * 演示基础地图
 * @author Administrator
 *
 */
public class BaseMapActivity extends Activity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;//声明百度地图的控制对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
//		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_basemap);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();//获取BaiduMap对象
		
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
//		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//卫星图
		mBaiduMap.setTrafficEnabled(true);//显示实时交通图
		mBaiduMap.setBaiduHeatMapEnabled(true);//显示城市热力图
		
//		mMapView.showScaleControl(false);//隐藏比例尺控件
//		mMapView.showZoomControls(false);//隐藏缩放按钮
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
