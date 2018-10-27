package com.example.youyou.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.example.youyo.R;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.ContacterManager.MRosterGroup;
import com.example.youyou.manager.UserManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.User;
import com.example.youyou.service.ContactService;
import com.example.youyou.util.StringUtil;
import com.example.youyou.view.ContacterAdapter;



import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SetActivity extends ActivitySupport implements OnItemClickListener{

	
	private ImageView iv_avatar;
	private TextView tv_nickname;
	private TextView tv_username;
	VCard vCard=XmppConnectionManager.getMyvCard();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.main_set);
		init();
		
	}
	private void init()  {
		// TODO Auto-generated method stub
		tv_nickname=(TextView) findViewById(R.id.tv_nickname);
		iv_avatar=(ImageView) findViewById(R.id.iv_avatar);
		tv_username=(TextView) findViewById(R.id.tv_id);
		tv_username.setText("帐号："+getLoginConfig().getUsername());
		
//		if(vCard.getAvatar()!=null){
//		Bitmap bitmap=BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
//			
//		iv_avatar.setImageBitmap(bitmap);
//		}
//		BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//		Bitmap bitmap=BitmapFactory.
//				decodeFile(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png",options);
		Bitmap bitmap=BitmapUtils.
				getBitmapByOption(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png", 80, 80);
		if(bitmap!=null)
			iv_avatar.setImageBitmap(bitmap);
		if(vCard.getNickName()!=null){
			
			tv_nickname.setText(vCard.getNickName());
		}
		else {
			tv_nickname.setText("快给自己起个名字把！");
		}
		myOncliclistener oncliclistener=new myOncliclistener();
		iv_avatar.setOnClickListener(oncliclistener);
		tv_nickname.setOnClickListener(oncliclistener);
		tv_username.setOnClickListener(oncliclistener);
		
	}
	
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		System.out.println("onstar...");
//		Bitmap bitmap=BitmapUtils.
//				getBitmapByOption(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png", 80, 80);
//		if(bitmap!=null)
//			iv_avatar.setImageBitmap(bitmap);
//		if(vCard.getNickName()!=null){
//			
//			tv_nickname.setText(vCard.getNickName());
//		}
//	}
	class myOncliclistener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(SetActivity.this, SetUserInfoActivity.class);
			
			//startActivity(intent);
			startActivityForResult(intent, 1);
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//更新iv—avatar
		System.out.println("on..."+requestCode);
		if(requestCode==1){
			System.out.println("doresult...");
			Bitmap bitmap=BitmapUtils.
					getBitmapByOption(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png", 80, 80);
			if(bitmap!=null)
				iv_avatar.setImageBitmap(bitmap);
		}
	}
//	public void setVcard(View view) {
//		// TODO Auto-generated method stub
//		XMPPConnection connection=XmppConnectionManager.getInstance().getConnection();
//		VCard vCard=new VCard();
//		Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bg_login);
//		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//		vCard.setAvatar(outputStream.toByteArray());
//		try {
//			vCard.save(connection);
//		} catch (XMPPException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public  void getVcard (View view) throws XMPPException {
//		VCard vCard=new VCard();
//		vCard.load(XmppConnectionManager.getInstance().getConnection());
//		Bitmap bitmap=BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
//		ImageView avatar=(ImageView) findViewById(R.id.iv_avatar);
//		avatar.setImageBitmap(bitmap);
//	}
}
