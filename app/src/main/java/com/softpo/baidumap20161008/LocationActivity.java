package com.softpo.baidumap20161008;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 演示百度地图定位
 * @author Administrator
 *
 */
public class LocationActivity extends Activity {

	private MapView mapView = null;
	private BaiduMap baiduMap = null;
	private LocationClient locationClient;//定位客户端 LocationClient类必须在主线程中声明。需要Context类型的参数。Context需要时全进程有效的context,推荐用getApplicationConext获取全进程有效的context
	private MyLocationListener myLocationListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basemap);
		
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		locationClient = new LocationClient(getApplicationContext());
		myLocationListener = new  MyLocationListener();
		
		//设置定位功能可用
		baiduMap.setMyLocationEnabled(true);
		//定位参数
		LocationClientOption option = new LocationClientOption();
		/**
		 * 设置定位的模式
		 * LocationMode.Hight_Accuracy：高精度定位模式 默认模式 在这种定位模式下会同时使用网络定位和GPS定位 优先返回最高精度的定位结果
		 * LocationMode.Battery_Saving：低功耗定位模式，不会使用GPS 只会使用网络定位
		 * LocationMode.Device_Sensors：仅用设备的定位模式，不需要连接网络，只使用GPS，这种模式不支持室内环境的定位
		 */
		option.setLocationMode(LocationMode.Hight_Accuracy);
		//设置坐标编码,可选，默认gcj02，设置返回的定位结果坐标系
		option.setCoorType("bd09ll");
		option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		//设置超时时间(5s)
		option.setTimeOut(5000);
		//设置是否需要设备方向
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setIsNeedAddress(true);//设置是否需要接收地址信息
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				//构造定位数据
				MyLocationData data = new MyLocationData.Builder()
						.direction(location.getDirection())//获取手机当前的方向
						.latitude(location.getLatitude())//获取纬度的坐标
						.longitude(location.getLongitude())//获取经度的坐标
						.accuracy(location.getRadius())//获取定位的精度(精确到的范围)
						.build();

				//设置地图的定位数据
				baiduMap.setMyLocationData(data);
				//更新地图状态
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
				//baiduMap.animateMapStatus(update);把你定位的点移动到地图的中心
				baiduMap.animateMapStatus(update);
			}
		});//注册定位监听
		locationClient.setLocOption(option);//设置定位参数

		//运行时权限
		//1、检验是否已经获取运行时权限
		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
			locationClient.start();//开启定位
			Log.d("flag","------------>开启定位");
		}else {
			//2、申请运行时权限
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION},101);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case 101:
				if(grantResults.length>0){
					if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1] == PackageManager.PERMISSION_GRANTED){
						Log.d("flag","-------------->允许定位");
						locationClient.start();//开启定位
					}
				}
				break;
		}
	}

	//定位监听
	class MyLocationListener implements BDLocationListener {

		//接收最终的定位结果
		@Override
		public void onReceiveLocation(BDLocation location) {
			//构造定位数据
			MyLocationData data = new MyLocationData.Builder()
				.direction(location.getDirection())//获取手机当前的方向
				.latitude(location.getLatitude())//获取纬度的坐标
				.longitude(location.getLongitude())//获取经度的坐标
				.accuracy(location.getRadius())//获取定位的精度(精确到的范围)
				.build();
			
			//设置地图的定位数据
			baiduMap.setMyLocationData(data);
			//更新地图状态
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
			//baiduMap.animateMapStatus(update);把你定位的点移动到地图的中心
			baiduMap.animateMapStatus(update);
		}
		
	}
	
	//注销定位监听 关闭地图
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		locationClient.unRegisterLocationListener(myLocationListener);
		locationClient.stop();
	}
}
