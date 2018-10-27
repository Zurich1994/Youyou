package com.example.youyou.service;



import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.xbill.DNS.CNAMERecord;

import android.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.youyo.R;
import com.example.youyou.activity.ChatActivity;
import com.example.youyou.comm.Constant;
import com.example.youyou.manager.MessageManager;
import com.example.youyou.manager.NoticeManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.IMMessage;
import com.example.youyou.model.Notice;
import com.example.youyou.util.DateUtil;
import com.example.youyou.util.StringUtil;


/**
 * 
 * 聊天服务.
 * 
 * @author shimiso
 */
public class IMChatService extends Service {
	private Context context;
	private NotificationManager notificationManager;

	@Override
	public void onCreate() {
		System.out.println("creat ..chat service ");
		context = this;
		super.onCreate();
		initChatManager();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initChatManager() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		XMPPConnection conn = XmppConnectionManager.getInstance()
				.getConnection();
		conn.addPacketListener(pListener, new MessageTypeFilter(
				Message.Type.chat));
XMPPConnection connection=XmppConnectionManager.getInstance().getConnection();
		
		ServiceDiscoveryManager sdManager= ServiceDiscoveryManager.getInstanceFor(connection);
		if(sdManager==null)
		{
			sdManager=new ServiceDiscoveryManager(connection);
		}
		sdManager.addFeature("http://jabber.org/protocol/disco#info");
		sdManager.addFeature("jabber:iq:privacy");
		FileTransferNegotiator.setServiceEnabled(connection, true);
		// fileTransferManager = new FileTransferManager(connection);//在0.8.1.1版本（之后应该也是如此），正确的应该是放在这里。
		
		FileTransferManager fileManager =  new FileTransferManager(connection);
//		FileTransferManager fileManager=new FileTransferManager(conn);
		MyFileListener fileListener=new MyFileListener();
		fileManager.addFileTransferListener(fileListener);
		
		//添加聊天监听
//		conn.getChatManager().addChatListener(new ChatManagerListener() {
//			
//			@Override
//			public void chatCreated(Chat arg0, boolean arg1) {
//				System.out.println("chatlisten....");
//				// TODO Auto-generated method stub
//				if(!arg1){
//				arg0.addMessageListener(new MessageListener() {
//					
//					@Override
//					public void processMessage(Chat arg0, Message arg1) {
//						// TODO Auto-generated method stub
//						
//						System.out.println("received...hahah 来自"+arg1.getFrom()+"内容："+arg1.getBody());
//					}
//				});
//				}
//			}
//		});
	}
	public class MyFileListener implements FileTransferListener{

		public String getFileType(String fileFullName) {  
	        if (fileFullName.contains(".")) {  
	            return "." + fileFullName.split("//.")[1];  
	        } else {  
	            return fileFullName;  
	        }  
	  
	    }  
		@Override
		public void fileTransferRequest(FileTransferRequest arg0) {
			// TODO Auto-generated method stub
			System.out.println("receive  file.");
			IncomingFileTransfer infile=arg0.accept();
			System.out.println("size "+infile.getFileSize());
			String filename=arg0.getFileName();
		//	String filetype=getFileType(filename);
			//System.out.println(filetype);
			String requsetor=arg0.getRequestor();
			
			System.out.println(filename);
			
			String description=arg0.getDescription();
			String dbPath = "/yoyo/chatCache";
			String path=Environment.getExternalStorageDirectory().getAbsolutePath()+dbPath;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			try {
				System.out.println(infile.getProgress());
//				
				
					
				System.out.println("accept success");
				infile.recieveFile(new File(f, filename));
				//InputStream inputStream=infile.recieveFile();
				
//				while(!infile.isDone()){
//					System.out.println("progress.."+infile.getProgress());
//					
//				}
				
                while(!infile.isDone()) {
                   try{
                      Thread.sleep(1000L);
                      System.out.println(infile.getProgress());
                   }catch (Exception e) {
                      Log.e("NewChat", e.getMessage());
                   }
                   if(infile.getStatus().equals(FileTransfer.Status.error)) {
                      Log.e("ERROR!!! ", infile.getError() +"");
                   }
                }
				String content=path+"/"+filename;
				System.out.println(content);
				String from=StringUtil.getJidByName(StringUtil.getUserNameByJid(requsetor));
				String time = DateUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART);
				
				//消息
				IMMessage imMessage=new IMMessage();
				imMessage.setContent(content);
				if(description.equals(Constant.FILETYPE_VOICE)){
					Long size=arg0.getFileSize();
					System.out.println("file size"+size);
					imMessage.setMsgType(4);
				}
				else {
					imMessage.setMsgType(2);
				}
				imMessage.setFromSubJid(from);
				imMessage.setTime(time);
				MessageManager.getInstance(context).saveIMMessage(imMessage);
				//通知
				Notice notice = new Notice();
				notice.setTitle("会话信息");
				notice.setNoticeType(Notice.CHAT_MSG);
				
				notice.setContent(content);
				notice.setFrom(from);
				notice.setStatus(Notice.UNREAD);
				notice.setNoticeTime(time);
				
				NoticeManager noticeManager = NoticeManager
						.getInstance(context);
				long noticeId = -1;

				noticeId = noticeManager.saveNotice(notice);
				//广播
				Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
				intent.putExtra(IMMessage.IMMESSAGE_KEY, imMessage);
				intent.putExtra("notice", notice);
				sendBroadcast(intent);
				
				 
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
		}
		
	}
	PacketListener pListener = new PacketListener() {

		@Override
		public void processPacket(Packet arg0) {
			System.out.println("有packet...");
			System.out.println(arg0.getFrom());
			
			Message message = (Message) arg0;
			if (message != null && message.getBody() != null
					&& !message.getBody().equals("null")) {
				IMMessage msg = new IMMessage();
				// String time = (String)
				// message.getProperty(IMMessage.KEY_TIME);
				String time = DateUtil.date2Str(Calendar.getInstance(),
						Constant.MS_FORMART);
				msg.setTime(time);
				msg.setContent(message.getBody());
				if (Message.Type.error == message.getType()) {
					msg.setType(IMMessage.ERROR);
				} else {
					msg.setType(IMMessage.SUCCESS);
				}
				String from = message.getFrom().split("/")[0];
				String filefrom=message.getFrom();
				msg.setFromSubJid(from);
				System.out.println("content...."+msg.getContent());

				// 生成通知
				NoticeManager noticeManager = NoticeManager
						.getInstance(context);
				Notice notice = new Notice();
				notice.setTitle("会话信息");
				notice.setNoticeType(Notice.CHAT_MSG);
				notice.setContent(message.getBody());
				notice.setFrom(from);
				notice.setStatus(Notice.UNREAD);
				notice.setNoticeTime(time);

				// 历史记录
				IMMessage newMessage = new IMMessage();
				newMessage.setMsgType(0);
				newMessage.setFromSubJid(from);
				newMessage.setContent(message.getBody());
				newMessage.setTime(time);
				MessageManager.getInstance(context).saveIMMessage(newMessage);
				long noticeId = -1;

				noticeId = noticeManager.saveNotice(notice);

				if (noticeId != -1) {
					Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
					intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
					intent.putExtra("notice", notice);
					sendBroadcast(intent);
					setNotiType(R.drawable.icon,
							getResources().getString(R.string.new_message),
							notice.getContent(), ChatActivity.class, from);
							
					
				}

			}

		}

	};

	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内容
	 * @param activity
	 * @author shimiso
	 * @update 2012-5-14 下午12:01:55
	 */
	private void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("to", from);
		// notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		// 点击自动消失
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
		notificationManager.notify(0, myNoti);
	}
}
