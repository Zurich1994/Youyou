package com.example.youyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.example.youyo.R;
import com.example.youyou.comm.Constant;
import com.example.youyou.fragment.ContacterFragment;
import com.example.youyou.fragment.FindFragment;
import com.example.youyou.fragment.RecentlyContacterFragment;
import com.example.youyou.fragment.SetFragment;
import com.example.youyou.model.Notice;
import com.example.youyou.model.User;
import com.example.youyou.service.ContactService;
import com.example.youyou.service.IMChatService;

public class StartFragmentActivity extends FragmentActivity {
	
	private FragmentTabHost tabHost;
	private ViewPager viewPager;
	private LayoutInflater inflater;
	private List<Fragment> fragments;
	private Myadaper myadaper;
	RecentlyContacterFragment recentlyContacterFragment=new RecentlyContacterFragment();
	ContacterFragment contacterFragment=new ContacterFragment();
	FindFragment findFragment=new FindFragment();
	SetFragment setFragment=new SetFragment();
	private Class FragmentArray[] = {RecentlyContacterFragment.class,ContacterFragment.class,FindFragment.class,SetFragment.class};
	
	//定义数组来存放按钮图片
	private int mImageViewArray[] = {R.drawable.tab_message_btn,R.drawable.tab_selfinfo_btn,
									 R.drawable.tab_square_btn,R.drawable.tab_more_btn};
	
	//Tab选项卡的文字
	private String mTextviewArray[] = {"Yoyo", "通信录", "发现", "设置"};
	//activity 
	private String ids[] = {"main", "address", "find", "set"};
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		super.onCreate(arg0);
		setContentView(R.layout.startfragment);
		initTab();
		initPager();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(new ContacterReceiver(), intentFilter);
	}

	private void initPager() {
		// TODO Auto-generated method stub
		viewPager=(ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(3);
		fragments=new ArrayList<Fragment>();
		
		fragments.add(recentlyContacterFragment);
		fragments.add(contacterFragment);
		fragments.add(findFragment);
		fragments.add(setFragment);
		/*fragments.add(new RecentlyContacterFragment());
		fragments.add(new ContacterFragment());
		fragments.add(new FindFragment());
		fragments.add(new SetFragment());*/
		myadaper=new Myadaper(getSupportFragmentManager(),fragments);
		viewPager.setAdapter(myadaper);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				tabHost.setCurrentTab(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	
	}
	

		
	private void initTab() {
		// TODO Auto-generated method stub
		tabHost=(FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(),R.id.viewPager);
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				//tabHost.setCurrentTab();
			}
		});
		for(int i=0;i<4;i++){
			TabSpec spec=tabHost.newTabSpec(ids[i]).setIndicator(getTabItemView(i));
			
			tabHost.addTab(spec, FragmentArray[i], null);
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("main")) {
					
					viewPager.setCurrentItem(0);
				}
				if (tabId.equals("address")) {
					viewPager.setCurrentItem(1);
				}
				if (tabId.equals("find")) {
					viewPager.setCurrentItem(2);
				}
				if (tabId.equals("set")) {
					viewPager.setCurrentItem(3);
				}
			}
		});
	}
	private View getTabItemView(int index){
		inflater=getLayoutInflater();
		View view = inflater.inflate(R.layout.tab_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);		
		textView.setText(mTextviewArray[index]);
		return view;
	}
	private  class ContacterReceiver extends BroadcastReceiver{

		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			User user = intent.getParcelableExtra(User.userKey);
			Notice notice = (Notice) intent.getSerializableExtra("notice");
			if(action.equals(Constant.NEW_MESSAGE_ACTION)){
				recentlyContacterFragment.msgReceive(notice);
			}
		}
		
	}
	
	
	/*
	public void stopService() {
		// 好友联系人服务
		
		Intent server = new Intent(StartFragmentActivity.this, ContactService.class);
		this.stopService(server);
		// 聊天服务
		Intent chatServer = new Intent(this, IMChatService.class);
		this.stopService(chatServer);
//
//		// 自动恢复连接服务
//		Intent reConnectService = new Intent(context, ReConnectService.class);
//		context.stopService(reConnectService);
//
//		// 系统消息连接服务
//		Intent imSystemMsgService = new Intent(context,
//				IMSystemMsgService.class);
//		context.stopService(imSystemMsgService);
	}*/
}
class Myadaper extends FragmentPagerAdapter{
	List<Fragment> fragments;
	public Myadaper(FragmentManager fm,List<Fragment> list) {
		super(fm);
		fragments=list;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	

}
