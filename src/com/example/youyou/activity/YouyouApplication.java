package com.example.youyou.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.example.youyou.activity.NearlyActivity.LoginThread;
import com.example.youyou.activity.NearlyActivity.MyLocationListener;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.StaticPara;
import com.example.youyou.util.HttpSubmit;

import android.app.Activity;
import android.app.Application;

/**
 * 
 * 完整的退出程序
 */
public class YouyouApplication extends Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
		
	}
	
	private List<Activity> activityList = new LinkedList<Activity>();

	// 添加activity容器
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 退出程序
	
	public void exit() {
		XmppConnectionManager.getInstance().disconnect();
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
	
}
