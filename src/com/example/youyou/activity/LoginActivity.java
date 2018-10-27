package com.example.youyou.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youyo.R;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.model.LoginConfig;
import com.example.youyou.task.LoginTask;
import com.example.youyou.util.StringUtil;
import com.example.youyou.util.ValidateUtil;

/**
 * 登录
 * @author wb
 *
 */
public class LoginActivity extends ActivitySupport {

	

	public static final String IMAGE_UNSPECIFIED = "image/*";
	private EditText edt_username, edt_pwd;
	private CheckBox rememberCb, autologinCb, novisibleCb;
	private Button btn_login = null;
	private Button btn_reg = null;
	private LoginConfig loginConfig;
	private TextView tx_setconfig,tv_reg;
	private ImageView avatar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.login);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 校验SD卡
		checkMemoryCard();
		// 检测网络和版本
		validateInternet();
		// 初始化xmpp配置
		XmppConnectionManager.getInstance().init(loginConfig);
	}
	/**
	 * 初始化
	 */
	private void init() {
		// TODO Auto-generated method stub
		loginConfig=getLoginConfig();
		if(loginConfig.isAutoLogin()==true){
			LoginTask loginTask = new LoginTask(LoginActivity.this, loginConfig);
			loginTask.execute();
		}
		edt_pwd=(EditText) findViewById(R.id.et_login_password);
		edt_username=(EditText) findViewById(R.id.et_login_name);
		rememberCb=(CheckBox) findViewById(R.id.cb_remember);
		autologinCb=(CheckBox) findViewById(R.id.cb_autologin);
		novisibleCb=(CheckBox) findViewById(R.id.cb_novisible);
		btn_login=(Button) findViewById(R.id.but_login);
		
		
		tx_setconfig=(TextView) findViewById(R.id.tv_logconfig);
		tv_reg=(TextView) findViewById(R.id.tv_reg);
		avatar=(ImageView) findViewById(R.id.iv_avatar);
		// 初始化各组件的默认状态
		edt_username.setText(loginConfig.getUsername());
		edt_pwd.setText(loginConfig.getPassword());
		rememberCb.setChecked(loginConfig.isRemember());
		Bitmap bitmap=BitmapUtils.
				getBitmapByOption(BitmapUtils.getImagePath()+"/"+loginConfig.getUsername()+".png", 80, 80);
		if(bitmap!=null)
			avatar.setImageBitmap(bitmap);
		tv_reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getContext(), ResginActivity.class);
				startActivity(intent);
			}
		});
		//点击配置信息
		tx_setconfig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText xmppHostText = new EditText(context);
				xmppHostText.setText(loginConfig.getXmppHost());
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				
				dialog.setTitle("服务器设置")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(xmppHostText)
				.setMessage("请设置服务器IP地址")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String xmppHost = StringUtil
										.doEmpty(xmppHostText.getText()
												.toString());
								loginConfig.setXmppHost(xmppHost);
								XmppConnectionManager.getInstance().init(
										loginConfig);
								LoginActivity.this
										.saveLoginConfig(loginConfig);
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();// 取消弹出框
							}
						}).create().show();

			}
		});
		rememberCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked)
					autologinCb.setChecked(false);
			}
		});
		autologinCb.setChecked(loginConfig.isAutoLogin());
		novisibleCb.setChecked(loginConfig.isNovisible());
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkData()&&validateInternet()){
					String username = edt_username.getText().toString();
					String password = edt_pwd.getText().toString();
					loginConfig.setUsername(username);
					loginConfig.setPassword(password);
					// 先记录下各组件的目前状态,登录成功后才保存
					loginConfig.setPassword(password);
					loginConfig.setUsername(username);
					loginConfig.setRemember(rememberCb.isChecked());
					loginConfig.setAutoLogin(autologinCb.isChecked());
					loginConfig.setNovisible(novisibleCb.isChecked());
					System.out.println(loginConfig.getXmppHost()+"    "+loginConfig.getXmppPort());
					LoginTask loginTask = new LoginTask(LoginActivity.this,
							loginConfig);
					loginTask.execute();
				}
			}
		});
						
		
	}
	/**
	 * 
	 * 登录校验.
	 * 
	 */
	private boolean checkData() {
		boolean checked = false;
		checked = (!ValidateUtil.isEmpty(edt_username, "登录名") && !ValidateUtil
				.isEmpty(edt_pwd, "密码"));
		return checked;
	}
	@Override
	public void onBackPressed() {
		isExit();
	}

}
