package com.example.youyou.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.youyo.R;
import com.example.youyo.R.layout;
import com.example.youyou.model.StaticPara;
import com.example.youyou.model.Station;
import com.example.youyou.util.HttpSubmit;

public class NearlyActivity extends ActivitySupport {

	private MapView mapView;
	private BaiduMap baiduMap;
	private LocationClient locationClient;
	private BDLocation location;
	private Button showButton;
	private InfoWindow mInfoWindow;
	private BDLocationListener myListener=new MyLocationListener();
	private int mDistance=1000;
	Handler mHandler = null;
	private ArrayList<Station > stationList=new ArrayList<Station>();
	private ProgressDialog pd;
	@Override


	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);
		showPd();
		
		initMap();
//		init();
	}
	private void showPd() {
		// TODO Auto-generated method stub
		pd=this.getProgressDialog();
		pd.setTitle("请稍等");
		pd.setMessage("正在加载...");
		
		pd.show();
	}
	private void init() {
		// TODO Auto-generated method stub
		showButton =(Button) findViewById(R.id.showmarker);
		showButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new FujinderenThread().start();
				setMarker(stationList);
			}
		});
	}
	// 得到附近的人的线程
	class FujinderenThread extends Thread  {
		public void run() {
			try {
				this.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//得到附近的人的信息的JSONArray
			JSONArray json = HttpSubmit.GetJsonObject();
			System.out.println("json:"+json.toString());
			String struser = "";
			for(int i=0;i<json.length();i++)
				try {
					JSONObject jo=json.getJSONObject(i);
					JSONArray locArray = jo.getJSONArray("location");
					double latitude = locArray.getDouble(1);
					double longitude = locArray.getDouble(0);
					String title=jo.getString("title");
					String tags=jo.getString("tags");
					Station station=new Station();
					station.setLat(latitude);
					station.setLon(longitude);
					station.setTitle(title);
					station.setTags(tags);
					stationList.add(station);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			pd.dismiss();
			setMarker(stationList);
		}

	}
	private void initMap() {
		// TODO Auto-generated method stub
		mapView=(MapView) findViewById(R.id.bmapView);
		mapView.showScaleControl(false);
		mapView.showZoomControls(false);
		baiduMap=mapView.getMap();
		//定位
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				System.out.println("markerclick..");
				// TODO Auto-generated method stub
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				Bundle bundle=marker.getExtraInfo();
				final String jidString=bundle.getString("jid");
				String name=bundle.getString("name");
				button.setText(name);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println("to add");
						// TODO Auto-generated method stub
						Intent intent=new Intent();
						intent.setClass(getApplicationContext(), AddfrindsActivity.class);
						intent.putExtra("jid", jidString);
						startActivity(intent);
						baiduMap.hideInfoWindow();

					}
				});
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(button, ll, -47);
				baiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
		baiduMap.setMyLocationEnabled(true);
//		MyLocationData locData = new MyLocationData.Builder().accuracy((float) StaticPara.radiuo).
//				direction((float) StaticPara.direction).latitude(new Double(StaticPara.latitude)).
//				longitude(new Double(StaticPara.longitude)).build();
//		baiduMap.setMyLocationData(locData);
		locationClient=new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 高精度;
		// Battery_Saving:低精度.
		option.setCoorType("bd09ll"); // 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09
		// 返回百度经纬度坐标系 ：bd09ll
		option.setScanSpan(0);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
		option.setIsNeedAddress(true);// 设置是否需要地址信息，默认为无地址
		option.setNeedDeviceDirect(true);// 在网络定位时，是否需要设备方向

		locationClient.setLocOption(option);
		locationClient.start();
		
		


	}
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			System.out.println("get location ");
			// TODO Auto-generated method stub
			if (arg0 == null) {
				return;
			}
			location=arg0;
			StaticPara.latitude=new Double(location.getLatitude()).toString();
			StaticPara.longitude=new Double(location.getLongitude()).toString();
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			new LoginThread().start();
			baiduMap.setMyLocationData(locData);
			Thread thread=new FujinderenThread();
			thread.start();
			//searchStation(location.getLatitude(), location.getLongitude(), mDistance);
		}
	}
	class LoginThread extends Thread  {
		List<BasicNameValuePair>list=null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null; 
		String result="";
		String poiId="";
		public void run() {
			String action="http://api.map.baidu.com/geodata/v3/poi/create";
			list=new ArrayList<BasicNameValuePair>();  
			
			list.add(new BasicNameValuePair("title", StaticPara.userName));  
			list.add(new BasicNameValuePair("tags", StaticPara.userId));
			list.add(new BasicNameValuePair("latitude", new Double(location.getLatitude()).toString()));  //维度
			list.add(new BasicNameValuePair("longitude", new Double(location.getLongitude()).toString()));  //经度
			list.add(new BasicNameValuePair("coord_type", "3")); //这个参数要设为3，百度加密类型
			list.add(new BasicNameValuePair("geotable_id", "105038"));  //对应的lbs云中的表的表识
			list.add(new BasicNameValuePair("ak", "R4YzZd9fF95DLHbjCGgkFmuz")); 
			String result="";
			try {
				result = HttpSubmit.getReultForHttpPost(action, list);
				jsonArray = new JSONArray("["+result+"]");
				jsonObject = jsonArray.getJSONObject(0);
				poiId = jsonObject.getString("id");
				System.out.println("id"+poiId);
				StaticPara.userPoiId=poiId;

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	public class QuitThread extends Thread{
		List<BasicNameValuePair>list=null;
		@Override

		public void run() {
			// TODO Auto-generated method stub
			String action="http://api.map.baidu.com/geodata/v3/poi/delete";
			list=new ArrayList<BasicNameValuePair>(); 
			list.add(new BasicNameValuePair("id", StaticPara.userPoiId));
			list.add(new BasicNameValuePair("geotable_id", "105038"));  //对应的lbs云中的表的表识
			list.add(new BasicNameValuePair("ak", "R4YzZd9fF95DLHbjCGgkFmuz")); 
			
			try {
				HttpSubmit.getReultForHttpPost(action, list);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@SuppressLint("ResourceAsColor") public void setMarker(ArrayList<Station> list) {
		System.out.println("setMar");
		//		mapView.getOverlay().clear();
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.marker, null);
		//final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
		for (int i = 0; i < list.size(); i++) {
			final Station s = list.get(i);
			TextView tv = new TextView(getApplicationContext());
						tv.setText((i + 1) + "");
			tv.setTextColor(Color.rgb(255, 255, 255));
			
			
			//tv.setTextColor(color)
			//tv.setText(s.getName());
			tv.setBackgroundResource(R.drawable.icon_mark);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
			//设置经纬度
			LatLng latLng = new LatLng(s.getLat(), s.getLon());
			Bundle b = new Bundle();
			b.putString("jid",s.getTags());
			b.putString("name",s.getTitle());
			OverlayOptions oo = new MarkerOptions().position(latLng).icon(bitmap).extraInfo(b);
			baiduMap.addOverlay(oo);
		}


	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		new QuitThread().start();
	}

}
