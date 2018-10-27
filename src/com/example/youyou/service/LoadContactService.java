package com.example.youyou.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.example.youyou.comm.Constant;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.NoticeManager;
import com.example.youyou.manager.UserManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.Notice;
import com.example.youyou.util.DateUtil;
import com.example.youyou.util.StringUtil;

public class LoadContactService extends Service {

	private Roster roster = null;
	private Context context;
	private UserManager userManager;
	/* 声明对象变量 */
	private NotificationManager myNotiManager;
	@Override
	public void onCreate() {
		context = this;
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
		init();
		return super.onStartCommand(intent, flags, startId);
	}
	private void init() {
		// TODO Auto-generated method stub
		
		initRoster();
		initAvata();
		
	}
	private void initAvata()  {
		// TODO Auto-generated method stub
		userManager=UserManager.getInstance();
		for(RosterEntry entry:roster.getEntries()){
			String jid=entry.getUser();
			String name=StringUtil.getUserNameByJid(entry.getUser());
			InputStream inputStream=userManager.getUserImage(jid);
			System.out.println(jid+inputStream);
			if(inputStream!=null){
				Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
				File file=BitmapUtils.getImagePath();
				BitmapUtils.savaBitmap(file, name+".png", bitmap);
				
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		onDestroy();
	}
	//初始化花名册
	private void initRoster() {
		// TODO Auto-generated method stub
		roster = XmppConnectionManager.getInstance().getConnection()
				.getRoster();
		Log.d("roster", roster.toString());
	}
	@Override
	public void onDestroy() {
		// 释放资源
		
		ContacterManager.destroy();
		super.onDestroy();
	}
}
