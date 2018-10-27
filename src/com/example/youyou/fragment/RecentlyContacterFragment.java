package com.example.youyou.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.youyo.R;
import com.example.youyou.activity.ChatActivity;
import com.example.youyou.comm.Constant;
import com.example.youyou.manager.MessageManager;
import com.example.youyou.model.ChartHisBean;
import com.example.youyou.model.Notice;
import com.example.youyou.model.User;
import com.example.youyou.view.RecentChartAdapter;

public class RecentlyContacterFragment extends Fragment{
	private Context context;
	private ListView inviteList = null;
	private RecentChartAdapter noticeAdapter = null;
	private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context=getActivity();
		View view=inflater.inflate(R.layout.contact_list, null);
		
		init(view);
		return 	view;
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//添加监听
		System.out.println("registe news recevie!");
		ContacterReceiver contacterReceiver=new ContacterReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.registerReceiver(contacterReceiver, filter);
	}
	private void init(View view) {
		// TODO Auto-generated method stub
		inviteList = (ListView) view.findViewById(R.id.lv_contacters);
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
		intent.putExtra("to", user.getJID());
		startActivity(intent);
	}
	
	@Override
	
	public  void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshList();
		
	}
	@Override
	public  void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	public void refreshList() {
		// 刷新notice信息
		inviteNotices = MessageManager.getInstance(context)
				.getRecentContactsWithLastMsg();
		noticeAdapter.setNoticeList(inviteNotices);
		noticeAdapter.notifyDataSetChanged();
	}
	private  class ContacterReceiver extends BroadcastReceiver{

			
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("onrecevie..");
				// TODO Auto-generated method stub
				String action=intent.getAction();
				User user = intent.getParcelableExtra(User.userKey);
				Notice notice = (Notice) intent.getSerializableExtra("notice");
				if(action.equals(Constant.NEW_MESSAGE_ACTION)){
					System.out.println("msgrecevie...");
					msgReceive(notice);
				}
			}
			
		}
	//更新聊天现实信息
	public void msgReceive(Notice notice) {
		// TODO Auto-generated method stub
		System.out.println("reflish");
		for(ChartHisBean ch:inviteNotices){
			if(notice.getFrom().equals(ch.getFrom())){
				ch.setContent(notice.getContent());
				ch.setNoticeTime(notice.getNoticeTime());
				Integer x = ch.getNoticeSum() == null ? 0 : ch.getNoticeSum();
				ch.setNoticeSum(x + 1);
			}
			noticeAdapter.setNoticeList(inviteNotices);
			noticeAdapter.notifyDataSetChanged();
			//setPaoPao();
		}
	}
	/**
	 * tab上的气泡设置 有新消息来的通知气泡，数量设置,
	 */
	/*
	private void setPaoPao() {
		if (null != inviteNotices && inviteNotices.size() > 0) {
			int paoCount = 0;
			for (ChartHisBean c : inviteNotices) {
				Integer countx = c.getNoticeSum();
				paoCount += (countx == null ? 0 : countx);
			}
			if (paoCount == 0) {
				noticePaopao.setVisibility(View.GONE);
				return;
			}
			noticePaopao.setText(paoCount + "");
			noticePaopao.setVisibility(View.VISIBLE);
		} else {
			noticePaopao.setVisibility(View.GONE);
		}
	}
*/
	
}

