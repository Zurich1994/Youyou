package com.example.youyou.activity;

import com.example.youyo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowPicActivity extends ActivitySupport{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.showpic);
		ImageView imageView=(ImageView) findViewById(R.id.iv_show);
		Intent intent=getIntent();
		String path=intent.getStringExtra("picpath");
		Bitmap bm=BitmapFactory.decodeFile(path);
		imageView.setImageBitmap(bm);
	}
}
