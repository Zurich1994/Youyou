package com.example.youyou.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.youyo.R;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.util.StringUtil;

public class AddContacterActivity extends ActivitySupport {

	TextView username=null;
	Button add=null;
	ContacterManager manager=new ContacterManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcontacter);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		username=(TextView) findViewById(R.id.et_name);
		add=(Button) findViewById(R.id.but_add);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userName = username.getText().toString();
				if (StringUtil.empty(userName)) {
					showToast(getResources().getString(
							R.string.username_not_null));
					return;
				}
				String jid=StringUtil.getJidByName(userName);
				System.out.println(jid);
				manager.sendSubsriber(jid);
			}
		});
		
	}
}
