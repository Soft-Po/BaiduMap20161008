package com.softpo.baidumap20161008;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

public class PoisearchActivity extends FragmentActivity {

	private SupportMapFragment fragment;
	private BaiduMap baiduMap;
	private EditText editText_city;
	private EditText editText_name;
	private PoiSearch poiSearch;
	private int pageNumber = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poisearch);
		initView();

		fragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_poisearch));
		baiduMap = fragment.getBaiduMap();
		poiSearch = PoiSearch.newInstance();// 实例化poiSearch
		poiSearch
				.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
					// 表示获取poi检索结果后回调的方法
					@Override
					public void onGetPoiResult(PoiResult result) {
						// 判断查询过程中是否有错误
						if (result == null
								|| result.error == ERRORNO.RESULT_NOT_FOUND) {
							Toast.makeText(PoisearchActivity.this, "未搜索到查询结果",
									Toast.LENGTH_SHORT).show();
						}
						// 查询过程中没有任何错误.在地图上添加覆盖物
						if (result.error == ERRORNO.NO_ERROR) {
							baiduMap.clear();
							// 在地图上初始化覆盖物对象
							PoiOverlay overlay = new MyPoiOverlay(baiduMap);
							// 给地图上的覆盖物设置监听器
							baiduMap.setOnMarkerClickListener(overlay);
							// 在覆盖物上设置数据
							overlay.setData(result);
							// 将覆盖物添加到地图上
							overlay.addToMap();
							// 以适当的比例显示在地图上
							overlay.zoomToSpan();
							Toast.makeText(PoisearchActivity.this,"总共查到："+result.getTotalPoiNum()+"个兴趣点,分为："+
								      result.getTotalPageNum()+"页"+"当前页数编号为："+result.getCurrentPageNum()+"单页容量："+
								      result.getCurrentPageCapacity() , Toast.LENGTH_SHORT).show();
						}
					}
					// 表示获取poi检索详细结果后回调的方法
					@Override
					public void onGetPoiDetailResult(PoiDetailResult result) {
						if (result == null || result.error == ERRORNO.RESULT_NOT_FOUND) {
							Toast.makeText(PoisearchActivity.this, "未找到具体的查询结果", Toast.LENGTH_SHORT).show();
						}
						if (result.error == ERRORNO.NO_ERROR) {
							Toast.makeText(PoisearchActivity.this, result.getName()+":"+result.getAddress(), Toast.LENGTH_SHORT).show();
							if (result.getDetailUrl() != null) {
								Intent intent = new Intent(PoisearchActivity.this, DetailActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("URL", result.getDetailUrl());
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}
					}
					@Override
					public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

					}
				});
	}

	class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap arg0) {
			super(arg0);
		}

		//重写该方法的目的:poiSearch.searchPoiDetail()方法 该方法是为了获取每个poi的明细
		//因为只有获取了明细 才能执行onGetPoiDetailResult()方法 才能做到页面跳转到明细页面
		@Override
		public boolean onPoiClick(int index) {
			List<PoiInfo> list = getPoiResult().getAllPoi();
			PoiInfo poiInfo = list.get(index);
			poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.uid));
			return true;
		}

	}

	private void initView() {
		editText_city = (EditText) findViewById(R.id.editText_city);
		editText_name = (EditText) findViewById(R.id.editText_name);

	}

	public void click(View view) {
		switch (view.getId()) {
		case R.id.button_search:// 查询
			pageNumber = 1;
			break;
		case R.id.button_nextPage:// 下一页
			pageNumber++;
			break;

		}
//		citySearch(pageNumber);
//		boundSearch(pageNumber,39.91510f,116.40402f);
		nearbySearch(pageNumber,39.91510f,116.40402f);
	}
	
	/**
     * 城市内搜索,直接根据输入框的内容去实现Poi搜索.
     */
    private void citySearch(int page) {
        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
		String city = editText_city.getText().toString();
		citySearchOption.city(city);// 城市
		String key = editText_name.getText().toString();
		citySearchOption.keyword(key);// 关键字
        citySearchOption.pageCapacity(15);// 默认每页10条
        citySearchOption.pageNum(page);// 分页编号
        // 为PoiSearch设置搜索方式.
        poiSearch.searchInCity(citySearchOption);
    }

    /**
	 * 范围检索,范围搜索需要制定坐标.以矩形的方式进行范围搜索.
	 */
	private void boundSearch(int page,float latitude,float longitude) {
		PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
		LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
		LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
		LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
				.include(northeast).build();// 得到一个地理范围对象
		boundSearchOption.bound(bounds);// 设置poi检索范围
		boundSearchOption.keyword(editText_name.getText().toString());// 检索关键字
		boundSearchOption.pageNum(page);
		poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
	}

    /**
     * 附近检索,范围搜索需要指定圆心.以圆形的方式进行搜索.
     */
    private void nearbySearch(int page,float latitude,float longitude) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(latitude, longitude));
        nearbySearchOption.keyword(editText_name.getText().toString());
        nearbySearchOption.radius(10000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	poiSearch.destroy();
    }
}
