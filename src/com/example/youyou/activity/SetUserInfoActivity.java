package com.example.youyou.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youyo.R;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.XmppConnectionManager;
import com.example.youyou.util.StringUtil;

public class SetUserInfoActivity extends ActivitySupport {
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private ImageView iv_avatar;
	private EditText tv_nickname;
	private EditText tv_desc;
	private Button but_save;
	private VCard vCard=XmppConnectionManager.getMyvCard();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		 requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
	     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		setContentView(R.layout.set_vcard);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		iv_avatar=(ImageView) findViewById(R.id.iv_avatar);
		tv_nickname=(EditText) findViewById(R.id.et_nickname);
		tv_desc=(EditText) findViewById(R.id.et_decs);
		but_save=(Button) findViewById(R.id.but_save);
		
//		if(vCard.getAvatar()!=null){
//			Bitmap bitmap=BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
//			iv_avatar.setImageBitmap(bitmap);
//			}
//		BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//		Bitmap bitmap=BitmapFactory.
//				decodeFile(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png",options);
		Bitmap bitmap=BitmapUtils.
				getBitmapByOption(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png", 80, 80);
		if(bitmap!=null)
			iv_avatar.setImageBitmap(bitmap);
		if(vCard.getNickName()!=null){
				
				tv_nickname.setText(vCard.getNickName());
			}
//			else {
//				tv_nickname.setText("快给自己起个名字把！");
//			}
		
		if(vCard.getField("DESC")!=null){
			tv_desc.setText(vCard.getField("DESC"));
		}
		iv_avatar.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				System.out.println("iv.....");
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog=new AlertDialog.Builder(context);
				dialog.setTitle("选择图片")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("选择图片")
				.setPositiveButton("相册",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								gotoImages();
								
							}

							
						})
				.setNegativeButton("拍照",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								gotoCamera();
							}

						}).create().show();
			}
		});
		but_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				//Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.id.iv_avatar);
				ByteArrayOutputStream stream=new ByteArrayOutputStream();
				iv_avatar.setDrawingCacheEnabled(true);
				Bitmap bitmap=Bitmap.createBitmap(iv_avatar.getDrawingCache());
				iv_avatar.setDrawingCacheEnabled(false);
				bitmap.compress(CompressFormat.PNG, 100, stream);
				vCard.setAvatar(stream.toByteArray());
				BitmapUtils.delFile(BitmapUtils.getImagePath()+"/"+getLoginConfig().getUsername()+".png");
				File file=BitmapUtils.getImagePath();
				BitmapUtils.savaBitmap(file, getLoginConfig().getUsername()+".png", bitmap);
				vCard.setField("DESC", tv_desc.getText().toString());
				vCard.setNickName(tv_nickname.getText().toString());
				try {
					vCard.save(XmppConnectionManager.getInstance().getConnection());
					showToast("保存成功！");
					
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	/*
	 * 从相册获取
	 * 
	 */
	private void gotoImages() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_UNSPECIFIED);
		startActivityForResult(intent, PHOTOZOOM);
	}
	/*
	 * 拍照
	 */
	private void gotoCamera() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), "temp.jpg")));
		startActivityForResult(intent, PHOTOHRAPH);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/temp.jpg");
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)
			return;

		// 读取相册缩放图片
		if (requestCode == PHOTOZOOM) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 75, stream);// (0 -
																		// 100)压缩文件
				iv_avatar.setImageBitmap(photo);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	/*
	 * 裁剪
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		//intent.putExtra("", value)
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);
	}
}
