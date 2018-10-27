package com.example.youyou.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import com.example.youyo.R;
import com.example.youyou.manager.ContacterManager;
import com.example.youyou.manager.ContacterManager.MRosterGroup;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.User;
import com.example.youyou.service.ContactService;
import com.example.youyou.view.ContacterAdapter;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FindActivity extends ActivitySupport implements OnItemClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_find);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	
	
}
