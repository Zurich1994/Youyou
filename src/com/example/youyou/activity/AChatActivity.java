package com.example.youyou.activity;


import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.youyou.comm.Constant;
import com.example.youyou.manager.MessageManager;
import com.example.youyou.manager.NoticeManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.IMMessage;
import com.example.youyou.model.Notice;
import com.example.youyou.util.DateUtil;
//import org.jivesoftware.smack.Chat;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smackx.filetransfer.FileTransferManager;
//import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

/**
 * 
 * 聊天对话.
 * 
 * @author shimiso
 */
public abstract class AChatActivity extends ActivitySupport {

	private Chat chat = null;
	private List<IMMessage> message_pool = null;
	protected String to;// 聊天人
	protected String fileto;
	private static int pageSize = 10;
	private List<Notice> noticeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		to = getIntent().getStringExtra("to");

		if (to == null)
			return;
		chat = XmppConnectionManager.getInstance().getConnection()
				.getChatManager().createChat(to, new MessageListener() {

					@Override
					public void processMessage(Chat arg0, Message mes) {
						// TODO Auto-generated method stub
						System.out.println("from"+mes.getFrom()+":"+mes.getBody());

					}
				});
		//		chat=XmppConnectionManager.getInstance().getConnection()
		//				.getChatManager().createChat(to, new MessageListener() {
		//					
		//					@Override
		//					public void processMessage(Chat arg0, Message arg1) {
		//						// TODO Auto-generated method stub
		//						System.out.println("send..."+arg1.getBody());
		//					}
		//				});
		//		chat.addMessageListener(new MessageListener() {
		//			
		//			@Override
		//			public void processMessage(Chat arg0, Message arg1) {
		//				// TODO Auto-generated method stub
		//				System.out.println("收到信息了。。。"+arg1.getBody());
		//			}
		//		});
		//		/**
		//		 * 接收信息
		//		 */
		//		XmppConnectionManager.getInstance().getConnection().getChatManager().addChatListener(new ChatManagerListener() {
		//			
		//			@Override
		//			public void chatCreated(Chat chat, boolean arg1) {
		//			
		//					
		//				// TODO Auto-generated method stub
		//				if(!arg1){
		//					chat.addMessageListener(new MessageListener() {
		//						
		//						@Override
		//						public void processMessage(Chat arg0, Message arg1) {
		//							// TODO Auto-generated method stub
		//							System.out.println("recive message..."+arg1.getBody());
		//						}
		//					});
		//				}
		//			}
		//		});
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 第一次查询
		message_pool = MessageManager.getInstance(context)
				.getMessageListByFrom(to, 1, pageSize);
		if (null != message_pool && message_pool.size() > 0)
			Collections.sort(message_pool);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(receiver, filter);

		// 跟心某人所有通知
		NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
		super.onResume();

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("broadreceiver.....");
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				message_pool.add(message);
				receiveNewMessage(message);
				refreshMessage(message_pool);
			}
		}

	};

	protected abstract void receiveNewMessage(IMMessage message);

	protected abstract void refreshMessage(List<IMMessage> messages);

	protected List<IMMessage> getMessages() {
		return message_pool;
	}

	protected void sendMessage(String messageContent) throws Exception {
		//		Message message=new Message();
		//		message.setBody(messageContent);
		//		chat.sendMessage(message);
		//		System.out.println("chat,send...");


		String time = DateUtil.date2Str(Calendar.getInstance(),
				Constant.MS_FORMART);
		Message message = new Message();

		message.setProperty(IMMessage.KEY_TIME, time);
		message.setBody(messageContent);
		//XmppConnectionManager.getInstance().getConnection().sendPacket(message);
		chat.sendMessage(message);
		System.out.println(chat.getParticipant());
		System.out.println("短信已经发了");
		IMMessage newMessage = new IMMessage();
		newMessage.setMsgType(1);
		newMessage.setFromSubJid(chat.getParticipant());
		newMessage.setContent(messageContent);
		newMessage.setTime(time);
		message_pool.add(newMessage);
		MessageManager.getInstance(context).saveIMMessage(newMessage);
		// MChatManager.message_pool.add(newMessage);

		// 刷新视图
		System.out.println(message_pool.get(message_pool.size()-1).getType());
		refreshMessage(message_pool);

	}
	protected void sendfile(String  path,String type) {
		// TODO Auto-generated method stub
		//fileTransferManager = new FileTransferManager(connection);//网上的放在这里，这回导致Asmack崩溃
		System.out.println("path"+path);
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
		
		FileTransferManager manager =  new FileTransferManager(connection);
		// 创建输出的文件传输
		//		user3@ahic.com.cn/Spark 2.7.0
		String time = DateUtil.date2Str(Calendar.getInstance(),
				Constant.MS_FORMART);
		OutgoingFileTransfer transfer=manager.createOutgoingFileTransfer(to+"/Spark 2.7.0");
		//OutgoingFileTransfer transfer=manager.createOutgoingFileTransfer(to+"/smack");
		try {
			IMMessage imMessage=new IMMessage();
			if(type.equals(Constant.FILETYPE_IMAGE)){
				transfer.sendFile(new File(path),Constant.FILETYPE_IMAGE);
				imMessage.setMsgType(3);
			}
			else if (type.equals(Constant.FILETYPE_VOICE)) {
				transfer.sendFile(new File(path),Constant.FILETYPE_VOICE);
				imMessage.setMsgType(5);
			}
			imMessage.setFromSubJid(chat.getParticipant());
			imMessage.setTime(time);
			imMessage.setContent(path);
			System.out.println("send file"+imMessage.toString());
			message_pool.add(imMessage);
			MessageManager.getInstance(context).saveIMMessage(imMessage);
			refreshMessage(message_pool);
			System.out.println("sendsucess");
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//	        OutgoingFileTransfer transfer = manager  
		//	                .createOutgoingFileTransfer(pre.getFrom());  
		// 发送文件  
		//transfer.sendFile(new File("E:\\Chrysanthemum.jpg"), "图片");  
	}

	/**
	 * 下滑加载信息,true 返回成功，false 数据已经全部加载，全部查完了，
	 * 
	 * @param message
	 */
	protected Boolean addNewMessage() {
		List<IMMessage> newMsgList = MessageManager.getInstance(context)
				.getMessageListByFrom(to, message_pool.size(), pageSize);
		if (newMsgList != null && newMsgList.size() > 0) {
			message_pool.addAll(newMsgList);
			Collections.sort(message_pool);
			return true;
		}
		return false;
	}
	public String getFilePathByUri(Uri uri) {
		System.out.println(uri.toString());
		ContentResolver contentResolver= getContentResolver();
		Cursor cursor=contentResolver.query(uri, null, null, null, null);
		cursor.moveToFirst();  
		String path = cursor.getString(cursor.getColumnIndex("_data")); 
		// String path=cursor.getString(1);
		System.out.println("path"+path);

		return path;
	}
	protected void resh() {
		// 刷新视图
		refreshMessage(message_pool);
	}

}
