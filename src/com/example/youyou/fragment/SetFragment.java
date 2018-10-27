package com.example.youyou.fragment;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.youyo.R;
import com.example.youyou.activity.SetUserInfoActivity;
import com.example.youyou.comm.Constant;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.XmppConnectionManager;

public class SetFragment extends Fragment {
	
	private ImageView iv_avatar;
	private TextView tv_nickname;
	private TextView tv_username;
	private ViewGroup vGroup;
	protected SharedPreferences preferences;
	VCard vCard=XmppConnectionManager.getMyvCard();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.main_set, null);
		
		init(view);
		return view;
	}

	private void init(View view) {
		// TODO Auto-generated method stub
		tv_nickname=(TextView) view.findViewById(R.id.tv_nickname);
		iv_avatar=(ImageView) view.findViewById(R.id.iv_avatar);
		tv_username=(TextView) view.findViewById(R.id.tv_id);
		vGroup=(ViewGroup) view.findViewById(R.id.layout_vcard);
		tv_username.setText("帐号："+getUsername());
		Bitmap bitmap=BitmapUtils.
				getBitmapByOption(BitmapUtils.getImagePath()+"/"+getUsername()+".png", 80, 80);
		if(bitmap!=null)
			iv_avatar.setImageBitmap(bitmap);
		if(vCard.getNickName()!=null){
			
			tv_nickname.setText(vCard.getNickName());
		}
		else {
			tv_nickname.setText("快给自己起个名字把！");
		}
		vGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), SetUserInfoActivity.class);
				startActivity(intent);
			}
		});
		
	}

	private String getUsername() {
		// TODO Auto-generated method stub
		preferences = getActivity().getSharedPreferences(Constant.LOGIN_SET, 0);
		
		return preferences.getString(Constant.USERNAME, null);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Bitmap bitmap=BitmapUtils.
				getBitmapByOption(BitmapUtils.getImagePath()+"/"+getUsername()+".png", 80, 80);
		if(bitmap!=null)
			iv_avatar.setImageBitmap(bitmap);
		if(vCard.getNickName()!=null){
			
			tv_nickname.setText(vCard.getNickName());
		}
		else {
			tv_nickname.setText("快给自己起个名字把！");
		}
	}
		
	
}
