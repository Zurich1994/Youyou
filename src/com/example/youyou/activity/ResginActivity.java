package com.example.youyou.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.youyo.R;
import com.example.youyou.task.ResginTask;
import com.example.youyou.util.ValidateUtil;

public class ResginActivity extends ActivitySupport{
	EditText ed_password;
	EditText ed_username;
	//EditText ed_pickname;
	//ImageView iv_avata;
	Button bt_resgin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.resgin);
		init();
		
	}

	private void init() {
		// TODO Auto-generated method stub
		ed_password=(EditText) findViewById(R.id.et_password);
	//	ed_pickname=(EditText) findViewById(R.id.et_nickname);
		ed_username=(EditText) findViewById(R.id.et_username);
		//iv_avata=(ImageView) findViewById(R.id.iv_avatar);
		bt_resgin=(Button) findViewById(R.id.but_resgin);
		bt_resgin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkData()&&validateInternet()){
					String name=ed_username.getText().toString();
					String password=ed_password.getText().toString();
					ResginTask resginTask=new ResginTask(ResginActivity.this, name, password);
					resginTask.execute();
				}
			}
		});
	}
	private boolean checkData() {
		boolean checked = false;
		checked = (!ValidateUtil.isEmpty(ed_username, "登录名") && !ValidateUtil
				.isEmpty(ed_password, "密码"));
		return checked;
	}

}
