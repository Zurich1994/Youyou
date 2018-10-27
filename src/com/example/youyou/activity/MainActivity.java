package com.example.youyou.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.youyo.R;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.ContacterManager.MRosterGroup;
import com.example.youyou.manager.MessageManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.ChartHisBean;
import com.example.youyou.model.User;
import com.example.youyou.view.ContacterAdapter;
import com.example.youyou.view.RecentChartAdapter;


public class MainActivity extends ActivitySupport {
	private ListView inviteList = null;
	private RecentChartAdapter noticeAdapter = null;
	private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		init();
	}

	private void init() {
	//最近联系人
		inviteList = (ListView) findViewById(R.id.lv_contacters);
		inviteNotices = MessageManager.getInstance(context)
				.getRecentContactsWithLastMsg();
		noticeAdapter = new RecentChartAdapter(context, inviteNotices);
		inviteList.setAdapter(noticeAdapter);
		noticeAdapter.setOnClickListener(contacterOnClickJ);
		
	
	}
	private OnClickListener contacterOnClickJ = new OnClickListener() {

		@Override
		public void onClick(View v) {
			createChat((User) v.findViewById(R.id.new_content).getTag());

		}
	};
	private void createChat(User user) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, ChatActivity.class);
		System.out.println("to ..user"+user.getJID());
		intent.putExtra("to", user.getJID());
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("resume");
		super.onResume();
		refreshList();
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("pasue");
		super.onPause();
	}
	public void refreshList() {
		
		// 刷新notice信息
		inviteNotices = MessageManager.getInstance(context)
				.getRecentContactsWithLastMsg();
		noticeAdapter.setNoticeList(inviteNotices);
		noticeAdapter.notifyDataSetChanged();
	
		

	}

}
