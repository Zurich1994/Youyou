package com.example.youyou.service;

import java.util.Calendar;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.youyou.comm.Constant;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.NoticeManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.Notice;
import com.example.youyou.util.DateUtil;
import com.example.youyou.util.StringUtil;

public class ContactService extends Service {

	private Roster roster = null;
	private Context context;
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
		addSubscriptionListener();

		init();
		return super.onStartCommand(intent, flags, startId);
	}
	private void init() {
		// TODO Auto-generated method stub
		myNotiManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		initRoster();
	}
	/**
	 * 添加一个监听，监听好友添加请求。
	 */
	private void addSubscriptionListener() {
		PacketFilter filter = new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				if (packet instanceof Presence) {
					Presence presence = (Presence) packet;
					if (presence.getType().equals(Presence.Type.subscribe)) {
						return true;
					}
				}
				return false;
			}
		};
		XmppConnectionManager.getInstance().getConnection()
				.addPacketListener(subscriptionPacketListener, filter);
	}
	private PacketListener subscriptionPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			System.out.println("recived ....");
			String user = getSharedPreferences(Constant.LOGIN_SET, 0)
					.getString(Constant.USERNAME, null);
			if (packet.getFrom().contains(user))
				return;
			// 如果是自动接收所有请求，则回复一个添加信息
			if (Roster.getDefaultSubscriptionMode().equals(
					SubscriptionMode.accept_all)) {
				Presence subscription = new Presence(Presence.Type.subscribe);
				subscription.setTo(packet.getFrom());
				XmppConnectionManager.getInstance().getConnection()
						.sendPacket(subscription);
				System.out.println("add....");
			} else {
				NoticeManager noticeManager = NoticeManager
						.getInstance(context);
				Notice notice = new Notice();
				notice.setTitle("好友请求");
				notice.setNoticeType(Notice.ADD_FRIEND);
				notice.setContent(StringUtil.getUserNameByJid(packet.getFrom())
						+ "申请加您为好友");
				notice.setFrom(packet.getFrom());
				notice.setTo(packet.getTo());
				notice.setNoticeTime(DateUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART));
				notice.setStatus(Notice.UNREAD);
				long noticeId = noticeManager.saveNotice(notice);
				if (noticeId != -1) {
					Intent intent = new Intent();
					intent.setAction(Constant.ROSTER_SUBSCRIPTION);
					notice.setId("" + noticeId);
					intent.putExtra("notice", notice);
					sendBroadcast(intent);
//					setNotiType(R.drawable.icon, "好友请求",
//							StringUtil.getUserNameByJid(packet.getFrom())
//									+ "申请加您为好友", MyNoticeActivity.class);
				}

			}
		}
	};

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
		XmppConnectionManager.getInstance().getConnection()
				.removePacketListener(subscriptionPacketListener);
		ContacterManager.destroy();
		super.onDestroy();
	}
}
