package com.example.youyou.activity;

import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.youyo.R;
import com.example.youyou.manager.UserManager;
import com.example.youyou.util.StringUtil;
import com.example.youyou.view.GamePintuLayout;


public class AddfrindsActivity extends Activity {

	GamePintuLayout mGameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriend);
		Intent intent=getIntent();
		String jid=intent.getStringExtra("jid");
		System.out.println(jid);
		mGameView = (GamePintuLayout) findViewById(R.id.id_gameview);
		UserManager userManager=UserManager.getInstance();
		VCard vCard=userManager.getUserVCard(StringUtil.getJidByName(jid));
		byte[] avatar=vCard.getAvatar();
		Bitmap bitmap=BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
		mGameView.setBitmap(bitmap);
		mGameView.setJid(StringUtil.getJidByName(jid));
		
	}
}
