package com.example.youyou.activity;

import java.util.ArrayList;
import com.example.youyo.R;
import com.example.youyou.db.BitmapUtils;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class StartActivity extends TabActivity {

	private TabHost tabHost;
	private LocalActivityManager manager;
	LayoutInflater inflater;
	private ViewPager pagers;
	private Class ActivityArray[] = {MainActivity.class,AddressActivity.class,FindActivity.class,SetActivity.class};
	
	//定义数组来存放按钮图片
	private int mImageViewArray[] = {R.drawable.tab_message_btn,R.drawable.tab_selfinfo_btn,
									 R.drawable.tab_square_btn,R.drawable.tab_more_btn};
	
	//Tab选项卡的文字
	private String mTextviewArray[] = {"Yoyo", "通信录", "发现", "设置"};
	//activity 
	private String ids[] = {"main", "address", "find", "set"};
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		//加载localActivityManager  tabActivity 中的tab 对应
		manager=new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		this.loadTabHost();
		this.loadPage();
		tabHost.setCurrentTab(0);
	}

	private void loadPage() {
		// TODO Auto-generated method stub
		pagers=(ViewPager) findViewById(R.id.viewPager);
		pagers.setOffscreenPageLimit(3);
		final ArrayList<View>  views=new ArrayList<View>();
//		for(int i=1;i<4;i++){
//			
//			views.add(getView(ids[i],new Intent(this,ActivityArray[i])));
//		}
		views.add(getView("mian", new Intent(this,MainActivity.class)));
		views.add(getView("address", new Intent(this,AddressActivity.class)));
		views.add(getView("find", new Intent(this,FindActivity.class)));
		views.add(getView("set", new Intent(this,SetActivity.class)));
		
		pagers.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}
//			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				container.removeView(views.get(position));
			}
			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				//pagers.removeView(views.get(arg1));
				((ViewPager) arg0).removeView(views.get(arg1));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				// TODO Auto-generated method stub
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
			@Override
			//刷新
			public int getItemPosition(Object object) {
				// TODO Auto-generated method stub
//				return super.getItemPosition(object);
				return -2;
			}
		});
		pagers.setOnPageChangeListener(new OnPageChangeListener() {
			
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

	private View getView(String string, Intent intent) {
		// TODO Auto-generated method stub
		
		return manager.startActivity(string, intent).getDecorView();
	}

	@SuppressWarnings("deprecation")
	private void loadTabHost() {
		// TODO Auto-generated method stub
		for(int index = 0;index<4;index++){
		tabHost=getTabHost();
		tabHost.setup(manager);
		tabHost.addTab(tabHost.newTabSpec(ids[index])
				.setIndicator(getTabItemView(index))
				.setContent(new Intent(this,ActivityArray[index]))
				
				);
		//tabHost.getTabWidget().getChildAt(index).setBackgroundResource(R.drawable.selector_tab_background);
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				
				if (tabId.equals("main")) {
					
					pagers.setCurrentItem(0);
				}
				if (tabId.equals("address")) {
					pagers.setCurrentItem(1);
				}
				if (tabId.equals("find")) {
					pagers.setCurrentItem(2);
				}
				if (tabId.equals("set")) {
					pagers.setCurrentItem(3);
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
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		View view=inflater.inflate(R.layout.set_vcard,null);
//		ImageView iv_avatar=(ImageView) view.findViewById(R.id.iv_avatar);
//		TextView tv_nickname=(TextView) findViewById(R.id.tv_nickname);
//		Bitmap bitmap=BitmapUtils.
//				getBitmapByOption(BitmapUtils.getImagePath()+"/"+ActivitySupport.getLoginConfig().getUsername()+".png", 80, 80);
//		if(bitmap!=null)
//			iv_avatar.setImageBitmap(bitmap);
		
	}
	
}
