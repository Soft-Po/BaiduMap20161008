package com.softpo.baidumap20161008;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * 演示添加地图覆盖物
 * @author Administrator
 *
 */
public class AddOverlayActivity extends Activity {
	private MapView mapView;
	private BaiduMap baiduMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basemap);
		
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		//1.创建点对应的图标对象 采用的是图片描述器根据资源的id创建Bitmap指示性图标(覆盖物的图标)
		BitmapDescriptor bm = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
		Bitmap bt  = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_gcoding);
		final int bitmapHeight = bt.getHeight();//图片的高度
		//2.创建Marker对应的经纬度对象 覆盖物的坐标点
		final LatLng latLng = new LatLng(39.915599, 116.403694);
		//3.创建图层描述对象，因为需要在地图上展示Marker标记
		OverlayOptions options = new MarkerOptions().position(latLng).icon(bm).title("我爱北京天安门").zIndex(9).draggable(true);
		//4.将带有图标的覆盖物添加到地图上
		baiduMap.addOverlay(options);
		
		//点击地图上的标记点时触发的监听器
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			//当点Marker点时会将点击的Marker点回传到这个方法中
			@Override
			public boolean onMarkerClick(Marker marker) {
				//实现弹窗 1.弹窗的View 2.弹窗的位置 3.重写点击弹窗后的回调

				Button bt = new Button(AddOverlayActivity.this);
				bt.setBackgroundColor(Color.BLUE);
				bt.setTextColor(Color.WHITE);
				bt.setText(marker.getTitle());
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(bt);
				/**
				 * 3.表示InfoWindow弹出位置的y轴偏移量
				 */
				InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -bitmapHeight, new InfoWindow.OnInfoWindowClickListener() {

					//表示当InfoWindow被点击时回调的方法
					@Override
					public void onInfoWindowClick() {
						baiduMap.hideInfoWindow();//隐藏弹窗

					}
				});
				baiduMap.showInfoWindow(infoWindow);//显示弹窗
				return false;
			}
		});

		baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDrag(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				Log.d("flag","------------>markerEnd: "+marker.getPosition().latitude+", "+marker.getPosition().longitude);

				LatLng position = marker.getPosition();

				getAddressFromLatLng(position);

			}

			@Override
			public void onMarkerDragStart(Marker marker) {

			}
		});
	}

	private void getAddressFromLatLng(LatLng position){
		GeoCoder coder = GeoCoder.newInstance();
		coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
			}
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

				final String add = reverseGeoCodeResult.getAddress();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(AddOverlayActivity.this,"当前位置是："+add, Toast.LENGTH_LONG).show();
					}
				});

			}
		});
		coder.reverseGeoCode(new ReverseGeoCodeOption().location(position));
	}

	
}
