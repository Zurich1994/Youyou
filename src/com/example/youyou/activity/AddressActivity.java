package com.example.youyou.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.youyo.R;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.User;
import com.example.youyou.view.AllContacterAdapter;

public class AddressActivity extends ActivitySupport implements OnItemClickListener{

	private List<User> users=new ArrayList<User>();
	private ListView listView;
	private ViewGroup vgadd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_address);
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		ContacterManager.init(XmppConnectionManager.getInstance().getConnection());
		users=ContacterManager.getContacterList();
		listView=(ListView) findViewById(R.id.lv_contacters);
		listView.setAdapter(new AllContacterAdapter(users, this));
		listView.setOnItemClickListener(this);
		vgadd=(ViewGroup) findViewById(R.id.layout_add);
		vgadd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(AddressActivity.this, AddContacterActivity.class));
			}
		});
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		createChat((User) v.findViewById(R.id.tv_user_nickname).getTag());
	}
	private void createChat(User user) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, ChatActivity.class);
		System.out.println("to ..user"+user.getJID());
		intent.putExtra("to", user.getJID());
		startActivity(intent);
	}
	
	
}
