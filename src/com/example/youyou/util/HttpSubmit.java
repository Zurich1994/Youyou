package com.example.youyou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.youyou.model.StaticPara;

public class HttpSubmit {

	//get提交
	public static JSONArray GetJsonObject() {  
		HttpClient client = new DefaultHttpClient();  
		StringBuilder builder = new StringBuilder();  
		JSONArray jsonArray = null;  
		JSONObject jsonObject = null;

		String url = "http://api.map.baidu.com/geosearch/v3/nearby?ak=R4YzZd9fF95DLHbjCGgkFmuz&geotable_id=105038&q=''&location="+StaticPara.longitude+","+StaticPara.latitude+"&radius=3000";
		System.out.println(url);
		HttpGet get = new HttpGet(url);  

		try {  
			HttpResponse response = client.execute(get);  
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {  
				builder.append(s);  
			}  
			System.out.println(builder);
			jsonArray = new JSONArray("["+builder.toString()+"]");  

			jsonObject=jsonArray.getJSONObject(0);

			jsonArray = jsonObject.getJSONArray("contents");

		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return jsonArray;  
	}  

	//post提交方式
	public static String getReultForHttpPost(String action,List<BasicNameValuePair> list) throws ClientProtocolException, IOException{  

		String path=action;  
		HttpPost httpPost=new HttpPost(path);  

		httpPost.setEntity(new UrlEncodedFormEntity(list,HTTP.UTF_8));//编者按：与HttpGet区别所在，这里是将参数用List传递  

		String result="";  

		HttpResponse response=new DefaultHttpClient().execute(httpPost);  

		if(response.getStatusLine().getStatusCode()==200){  
			HttpEntity entity=response.getEntity();  
			result=EntityUtils.toString(entity, HTTP.UTF_8);  
		}  
		return result;  
	}  
}
