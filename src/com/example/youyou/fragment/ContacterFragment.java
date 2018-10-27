package com.example.youyou.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.youyo.R;
import com.example.youyou.activity.AddContacterActivity;
import com.example.youyou.activity.AddressActivity;
import com.example.youyou.activity.ChatActivity;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.User;
import com.example.youyou.view.AllContacterAdapter;

public class ContacterFragment extends Fragment {
	private List<User> users=new ArrayList<User>();
	private ListView listView;
	private ViewGroup vg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.main_address, null);
		listView=(ListView) view.findViewById(R.id.lv_contacters);
		vg=(ViewGroup) view.findViewById(R.id.layout_add);
		init();
		return view;
	}
	private void init() {
		// TODO Auto-generated method stub
		ContacterManager.init(XmppConnectionManager.getInstance().getConnection());
		users=ContacterManager.getContacterList();
		listView.setAdapter(new AllContacterAdapter(users, getActivity()));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				createChat((User) view.findViewById(R.id.tv_user_nickname).getTag());
			}
		});
		vg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), AddContacterActivity.class));
			}
		});
	}
	private void createChat(User user) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("to", user.getJID());
		startActivity(intent);
	}
}
