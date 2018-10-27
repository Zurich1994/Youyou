package com.example.youyou.task;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.youyo.R;
import com.example.youyou.activity.GuideViewActivity;
import com.example.youyou.activity.IActivitySupport;
import com.example.youyou.activity.StartFragmentActivity;
import com.example.youyou.activity.NearlyActivity.MyLocationListener;

import com.example.youyou.comm.Constant;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.UserManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.LoginConfig;
import com.example.youyou.model.StaticPara;
import com.example.youyou.util.HttpSubmit;
/**
 * 登录异步任务类
 * @author wb
 *
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private ProgressDialog pd;
	private Context context;
	private IActivitySupport activitySupport;
	private LoginConfig loginConfig;
	private VCard vCard;
	
	UserManager userManager=UserManager.getInstance();
	public LoginTask(IActivitySupport activitySupport, LoginConfig loginConfig) {
		this.activitySupport = activitySupport;
		this.loginConfig = loginConfig;
		this.pd = activitySupport.getProgressDialog();
		this.context = activitySupport.getContext();
	}
	@Override
	protected void onPreExecute() {
		pd.setTitle("请稍等");
		pd.setMessage("正在登录...");
		pd.show();
		super.onPreExecute();
	}
	@Override
	protected Integer doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		return login();
		
	}
	
	protected void onPostExecute(Integer result) {
		pd.dismiss();
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			XmppConnectionManager.loadVCard();
			Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			if (loginConfig.isFirstStart()) {// 如果是首次启动
				intent.setClass(context, GuideViewActivity.class);
				loginConfig.setFirstStart(false);
				activitySupport.startLoadService();
				
			} else {
				//intent.setClass(context, MainActivity.class);
				intent.setClass(context, StartFragmentActivity.class);
			}
			//intent.setClass(context, StartActivity.class);
			vCard=XmppConnectionManager.getMyvCard();
			if(vCard!=null&&vCard.getAvatar()!=null){
				Bitmap bitmap=BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, userManager.getUserVCard().getAvatar().length);
				File file=BitmapUtils.getImagePath();
				System.out.println("file..."+file);
				BitmapUtils.savaBitmap(file, loginConfig.getUsername()+".png", bitmap);
				StaticPara.userName=vCard.getNickName();
				//内存问题？？
				//loginConfig.setBitmap(bitmap);
				}
			//loginConfig.setFirstStart(true);
			
			activitySupport.saveLoginConfig(loginConfig);// 保存用户配置信息
			activitySupport.startService(); // 初始化各项服务
			context.startActivity(intent);
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_invalid_username_password),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_server_unavailable),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.unrecoverable_error), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		super.onPostExecute(result);
	}

	private Integer login() {
		// TODO Auto-generated method stub
		String username=loginConfig.getUsername();
		String password=loginConfig.getPassword();
		try {
			//得到连接
			XMPPConnection connection=XmppConnectionManager.getInstance().getConnection();
			connection.connect();
			//登录；
			connection.login(username, password,"smack");
			//presence 表明用户状态
			connection.sendPacket(new Presence(Presence.Type.available));
			if (loginConfig.isNovisible()) {// 隐身登录
				Presence presence = new Presence(Presence.Type.unavailable);
				Collection<RosterEntry> rosters = connection.getRoster()
						.getEntries();
				for (RosterEntry rosterEntry : rosters) {
					presence.setTo(rosterEntry.getUser());
					connection.sendPacket(presence);
				}
			}
			
			loginConfig.setUsername(username);
			StaticPara.userId=username;
			if (loginConfig.isRemember()) {// 保存密码
				loginConfig.setPassword(password);
			} else {
				loginConfig.setPassword("");
			}
			loginConfig.setOnline(true);
			
			return Constant.LOGIN_SECCESS;
		} catch (XMPPException xee) {
			// TODO Auto-generated catch block
			if (xee instanceof XMPPException) {
				XMPPException xe = (XMPPException) xee;
				final XMPPError error = xe.getXMPPError();
				int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
				}
				if (errorCode == 401) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				}else if (errorCode == 403) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					return Constant.SERVER_UNAVAILABLE;
				}
			} else {
				return Constant.LOGIN_ERROR;
			}
			
		}
		
	}
	
}
