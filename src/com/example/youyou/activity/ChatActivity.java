package com.example.youyou.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youyo.R;
import com.example.youyou.comm.Constant;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.MessageManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.IMMessage;
import com.example.youyou.model.User;
import com.example.youyou.util.DateUtil;
import com.example.youyou.util.StringUtil;


public class ChatActivity extends AChatActivity {
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private ImageView titleBack;
	private MessageListAdapter adapter = null;
	private EditText messageInput = null;
	private Button messageSendBtn = null;
	private ImageButton userInfo;
	private ListView listView;
	private int recordCount;
	private View listHead;
	private Button listHeadButton;
	private User user;// 聊天人
	private TextView tvChatTitle;
	private String to_name;
	private ImageView iv_status;
	private ImageButton picsendButton;
	private boolean btn_vocie = false;

	//发送文字布局
	private RelativeLayout mBottom;
	private ImageView chatting_mode_btn;
	private ImageView volume;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
	voice_rcd_hint_tooshort;
	private LinearLayout del_re;
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private TextView mBtnRcd;
	private SoundMeter sound;
	private int flag = 1;
	private static final int POLL_INTERVAL = 300;
	static String time="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.chat);
		init();
	}

	private void init() {
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		volume = (ImageView) this.findViewById(R.id.volume);
		sound = new SoundMeter();
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		initRec();
		//模式切换
		
		titleBack = (ImageView) findViewById(R.id.title_back);
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// iv_status=findViewById(R.id.)
		// 与谁聊天
		tvChatTitle = (TextView) findViewById(R.id.to_chat_name);
		user = ContacterManager.getByUserJid(to, XmppConnectionManager
				.getInstance().getConnection());
		if (null == user) {
			to_name = StringUtil.getUserNameByJid(to);
		} else {
			to_name = user.getName() == null ? user.getJID() : user.getName();

		}
		tvChatTitle.setText(to_name);

		userInfo = (ImageButton) findViewById(R.id.user_info);
		//		userInfo.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				Intent intent = new Intent();
		//				intent.setClass(context, FriendInfoActivity.class);
		//				startActivity(intent);
		//			}
		//		});

		listView = (ListView) findViewById(R.id.chat_list);
		listView.setCacheColorHint(0);
		adapter = new MessageListAdapter(ChatActivity.this, getMessages(),
				listView);

		// 头 查看聊天记录 wb

		LayoutInflater mynflater = LayoutInflater.from(context);
		listHead = mynflater.inflate(R.layout.chatlistheader, null);
		listHeadButton = (Button) listHead.findViewById(R.id.buttonChatHistory);
		listHeadButton.setOnClickListener(chatHistoryCk);
		listView.addHeaderView(listHead);
		listView.setAdapter(adapter);

		messageInput = (EditText) findViewById(R.id.chat_content);
		messageSendBtn = (Button) findViewById(R.id.chat_sendbtn);
		messageSendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = messageInput.getText().toString();
				System.out.println(message);
				if ("".equals(message)) {
					Toast.makeText(ChatActivity.this, "不能为空",
							Toast.LENGTH_SHORT).show();
				} else {

					try {
						sendMessage(message);

						messageInput.setText("");
					} catch (Exception e) {
						showToast("信息发送失败");
						messageInput.setText(message);
					}
					closeInput();
				}
			}
		});
		picsendButton=(ImageButton) findViewById(R.id.imgBut_addpic);
		picsendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//进入相册
				gotoImages();
			}


		});

	}
	private void initRec() {
		// TODO Auto-generated method stub
		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		//按住录音
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1){
					System.out.println("down");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					startVoiceT = SystemClock.currentThreadTimeMillis();
					 time= DateUtil.date2Str(Calendar.getInstance(),
							Constant.MS_FORMART);
					System.out.println(startVoiceT);
					voiceName = time + ".amr";
					mBtnRcd.setText("请录音！");
					
					sound.start(voiceName);
					flag = 2;
				}
				else if(event.getAction()==MotionEvent.ACTION_UP&&flag==2){
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
					mBtnRcd.setText("按住说话！");
					System.out.println("up");
					sound.stop();
					endVoiceT = SystemClock.currentThreadTimeMillis();
					System.out.println("time"+(endVoiceT-startVoiceT));
					String path=android.os.Environment.getExternalStorageDirectory()+"/yoyo/voice/"+time+".amr";
					sendfile(path,"voice");
					flag=1;
				}
				return true;
			}
		});
	}

	private void gotoImages() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_UNSPECIFIED);
		startActivityForResult(intent, 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==1){
			Uri uri= data.getData();
			System.out.println(uri);
			String path=getFilePathByUri(uri);
			sendfile(path,"image");
		}
	}


	@Override
	protected void receiveNewMessage(IMMessage message) {

	}

	@Override
	protected void refreshMessage(List<IMMessage> messages) {

		adapter.refreshList(messages);
	}

	@Override
	protected void onResume() {
		System.out.println("chat activity onresume");
		super.onResume();
		recordCount = MessageManager.getInstance(context)
				.getChatCountWithSb(to);
		if (recordCount <= 0) {
			listHead.setVisibility(View.GONE);
		} else {
			listHead.setVisibility(View.VISIBLE);
		}
		adapter.refreshList(getMessages());
	}
	
	private class MessageListAdapter extends BaseAdapter {

		private List<IMMessage> items;
		private Context context;
		private ListView adapterList;
		private LayoutInflater inflater;
		
		public MessageListAdapter(Context context, List<IMMessage> items,
				ListView adapterList) {
			this.context = context;
			this.items = items;
			this.adapterList = adapterList;
		}

		public void refreshList(List<IMMessage> items) {
			this.items = items;

			this.notifyDataSetChanged();
			adapterList.setSelection(items.size() - 1);
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final IMMessage message = items.get(position);
			TextView msgView=null;
			ImageView fileView=null;
			if (message.getMsgType() == 0) {
				convertView = this.inflater.inflate(
						R.layout.formclient_chat_in, null);
			} else if(message.getMsgType() == 1){
				convertView = this.inflater.inflate(
						R.layout.formclient_chat_out, null);
			}else if(message.getMsgType()==2){
				convertView=this.inflater.inflate(R.layout.formclient_chat_picin, null);
			}else if(message.getMsgType()==3){
				convertView=this.inflater.inflate(R.layout.formclient_chat_picout, null);
			}else if (message.getMsgType()==5) {
				convertView=this.inflater.inflate(R.layout.formclient_chat_voiceout, null);
			}
			if(message.getMsgType()==0||message.getMsgType()==1)
			{	msgView=(TextView) convertView
			.findViewById(R.id.formclient_row_msg);
			msgView.setText(message.getContent());
			}
			else if(message.getMsgType()==2||message.getMsgType()==3){
				fileView=(ImageView) convertView.findViewById(R.id.formclient_row_picmsg);
				Bitmap bitmap=BitmapUtils.getBitmapByOption(message.getContent(), 200, 200);
				fileView.setImageBitmap(bitmap);
				fileView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(context, ShowPicActivity.class);
						intent.putExtra("picpath", message.getContent());
						startActivity(intent);

					}
				});

			}else if (message.getMsgType()==4||message.getMsgType()==5) {
				final MediaPlayer player=new MediaPlayer();
				final String path=message.getContent();
				final TextView tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				System.out.println(path);
				final ImageView voiceView=(ImageView) convertView.findViewById(R.id.formclient_row_picmsg);
				try {
					player.setDataSource(path);
					player.prepare();
					String timeString=getTimeString(player.getDuration());
					tv_time.setText(timeString);
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				voiceView.setOnClickListener(new OnClickListener() {
					
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						voiceView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.chatto_voice_playing));
						player.start();
						
						
					}
				});
				player.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						voiceView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.chatto_voice_playing_f3));

					}
				});
				
			}
			TextView useridView = (TextView) convertView
					.findViewById(R.id.formclient_row_userid);
			TextView dateView = (TextView) convertView
					.findViewById(R.id.formclient_row_date);
			ImageView avatarView=(ImageView) convertView
					.findViewById(R.id.from_head);

			if (message.getMsgType() == 0||message.getMsgType() == 2||message.getMsgType() == 4) {
				if (null == user) {
					useridView.setText(StringUtil.getUserNameByJid(to));
				} else {
					useridView.setText(user.getName());
				}

			} else {
				useridView.setText("我");
			}
			dateView.setText(message.getTime());

			Bitmap bitmap;
			String dir=BitmapUtils.getImagePath()+"/";

			if(message.getMsgType()==0||message.getMsgType()==2||message.getMsgType()==4){
				bitmap=BitmapUtils.
						getBitmapByOption(dir+StringUtil.getUserNameByJid(to)+".png", 80, 80);
			}
			else {
				bitmap=BitmapUtils.
						getBitmapByOption(dir+StringUtil.getUserNameByJid(getUsername())+".png", 50, 50);
			}
			avatarView.setImageBitmap(bitmap);
			return convertView;
		}

	}
	private String getUsername() {
		// TODO Auto-generated method stub
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);

		return preferences.getString(Constant.USERNAME, null);
	}
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		MenuInflater inflater = getMenuInflater();
	//		inflater.inflate(R.menu.chat_menu, menu);
	//		return true;
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		Intent intent = new Intent();
	//		switch (item.getItemId()) {
	//		case R.id.menu_return_main_page:
	//			intent.setClass(context, MainActivity.class);
	//			startActivity(intent);
	//			finish();
	//			break;
	//		case R.id.menu_relogin:
	//			intent.setClass(context, LoginActivity.class);
	//			startActivity(intent);
	//			finish();
	//			break;
	//		case R.id.menu_exit:
	//			isExit();
	//			break;
	//		}
	//		return true;
	//
	//	}

	/**
	 * 点击进入聊天记录
	 */
	private OnClickListener chatHistoryCk = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//			Intent in = new Intent(context, ChatHistoryActivity.class);
			//			in.putExtra("to", to);
			//			startActivity(in);
		}
	};
	private static String getTimeString(long ts) {
		int time = (int) ts / 1000;
		int ms = time % 60;
		int ss = time / 60;
		ss = ss > 99 ? 99 : ss;
		StringBuffer str = new StringBuffer();
		str.append(ss < 10 ? "0" + ss + ":" : ss + ":");
		str.append(ms < 10 ? "0" + ms : ms + "");
		return str.toString();
	}
	

}