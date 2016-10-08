package com.softpo.baidumap20161008;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

/**
 * 演示百度地图线路规划
 * @author Administrator
 *
 */
public class RoutePlanActivity extends Activity {

	private MapView mapView;
	private BaiduMap baiduMap;
	private EditText editText_city;
	private EditText editText_start;
	private EditText editText_end;
	private RoutePlanSearch planSearch;//线路规划查询器
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routeplan);
		
		initView();
		
		//1.实例化线路查询的查询器
		planSearch = RoutePlanSearch.newInstance();
		
		//4.绑定监听
		planSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
//			步行
			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult result) {
				if (result == null || result.error == ERRORNO.RESULT_NOT_FOUND) {
					Toast.makeText(RoutePlanActivity.this, "查询结果为空！", Toast.LENGTH_SHORT).show();
				}
				if (result.error == ERRORNO.NO_ERROR) {
					baiduMap.clear();
					WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(baiduMap);
					baiduMap.setOnMarkerClickListener(walkingRouteOverlay);
					walkingRouteOverlay.setData(result.getRouteLines().get(0));
					walkingRouteOverlay.addToMap();
					walkingRouteOverlay.zoomToSpan();
					
				}
			}

//			乘坐公交
			@Override
			public void onGetTransitRouteResult(TransitRouteResult result) {
				if (result == null || result.error == ERRORNO.RESULT_NOT_FOUND) {
					Log.d("flag","-------------------->transitRoutResult: failure ");
					Toast.makeText(RoutePlanActivity.this, "查询结果为空！", Toast.LENGTH_SHORT).show();
				}
				if (result.error == ERRORNO.NO_ERROR) {
					Log.d("flag","-------------------->transitRoutResult: "+result.getRouteLines().size());
					baiduMap.clear();
					TransitRouteOverlay transitRouteOverlay = new TransitRouteOverlay(baiduMap);
					baiduMap.setOnMarkerClickListener(transitRouteOverlay);
					transitRouteOverlay.setData(result.getRouteLines().get(0));
					transitRouteOverlay.addToMap();
					transitRouteOverlay.zoomToSpan();

				}


			}
//			自驾
			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
				if (arg0 == null || arg0.error == ERRORNO.RESULT_NOT_FOUND) {
					Toast.makeText(RoutePlanActivity.this, "查询结果为空！", Toast.LENGTH_SHORT).show();
				}
				if (arg0.error == ERRORNO.NO_ERROR) {
					baiduMap.clear();
					DrivingRouteOverlay drivingRouteOverlay = new MyDrivingRouteOverlay(baiduMap);
					baiduMap.setOnMarkerClickListener(drivingRouteOverlay);
					drivingRouteOverlay.setData(arg0.getRouteLines().get(0));
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();

				}
			}

			@Override
			public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

			}
		});
		
	}
	
	public void clickButton(View view){
		//2.设置线路规划的起始点
		//设置起点
		PlanNode startNode = PlanNode.withCityNameAndPlaceName(editText_city.getText().toString(), editText_start.getText().toString());
		//设置终点
		PlanNode endNode = PlanNode.withCityNameAndPlaceName(editText_city.getText().toString(), editText_end.getText().toString());
	
		//3.调用相应的方法开启相应的查询
		switch (view.getId()) {
		case R.id.driver://驾车
			planSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
			break;
		case R.id.walk://步行
			planSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
			break;
		case R.id.transit://公交
			Log.d("flag","---------------------->onClick: transit");
			planSearch.transitSearch(new TransitRoutePlanOption().city("北京").from(startNode).to(endNode));
			break;
		default:
			break;
		}
	
	}
	
	public class MyWalkingRouteOverlay extends WalkingRouteOverlay{

		public MyWalkingRouteOverlay(BaiduMap arg0) {
			super(arg0);
			
		}

		@Override
		public boolean onRouteNodeClick(int arg0) {
			return super.onRouteNodeClick(arg0);
		}
	}

	public class MyDrivingRouteOverlay extends DrivingRouteOverlay{

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onRouteNodeClick(int i) {
			return super.onRouteNodeClick(i);
		}
	}
	public class MyTransitRouteOverlay extends TransitRouteOverlay{

		/**
		 * 构造函数
		 *
		 * @param baiduMap 该TransitRouteOverlay引用的 BaiduMap 对象
		 */
		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);



		}

		@Override
		public boolean onRouteNodeClick(int i) {
			return super.onRouteNodeClick(i);

		}
	}

	private void initView() {
		editText_city = (EditText) findViewById(R.id.editText_city);
		editText_start = (EditText) findViewById(R.id.editText_start);
		editText_end = (EditText) findViewById(R.id.editText_end);
		mapView = (MapView) findViewById(R.id.mapView);
		baiduMap = mapView.getMap();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mapView!=null) {
			mapView.onDestroy();
		}
		//5.释放搜索实例
		planSearch.destroy();
	}
}
