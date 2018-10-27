package com.example.youyou.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.youyou.comm.Constant;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.NoticeManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.Notice;
import com.example.youyou.model.StaticPara;

import com.example.youyou.util.DateUtil;
import com.example.youyou.util.HttpSubmit;
import com.example.youyou.util.StringUtil;

public class MyLocationService extends Service {

	private Context context;
	private LocationClient locationClient;
	private BDLocation location=new BDLocation();
	private BDLocationListener myListener=new MyLocationListener();
	/* 声明对象变量 */
	@Override
	public void onCreate() {
		context = getApplicationContext();
	super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		makelocation();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	@Override
	public void onDestroy() {
		// 释放资源
	
		super.onDestroy();
	}
	private void makelocation() {
		// TODO Auto-generated method stub
		 
		locationClient=new LocationClient(context);
		locationClient.registerNotifyLocationListener(myListener);
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
			StaticPara.direction=location.getDirection();
			StaticPara.radiuo=location.getRadius();
			new LoginThread().start();
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
}
