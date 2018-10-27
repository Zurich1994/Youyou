package com.example.youyou.view;

import java.io.File;
import java.util.List;

import org.jivesoftware.smackx.packet.VCard;

import com.example.youyo.R;
import com.example.youyou.db.BitmapUtils;
import com.example.youyou.manager.UserManager;
import com.example.youyou.model.User;
import com.example.youyou.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContacterAdapter extends BaseAdapter{

	private List<User> list;
	private LayoutInflater inflater;
	private Context context;
	private VCard vCard;
	private UserManager userManager=UserManager.getInstance();
	public ContacterAdapter(List<User> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.contact_item, null);
		ImageView iv_user_pic=(ImageView) viewGroup.findViewById(R.id.iv_user_pic);
		TextView nickname=(TextView) viewGroup.findViewById(R.id.tv_user_nickname);
//		TextView lasttime=(TextView) viewGroup.findViewById(R.id.tv_user_lasttime);
//		TextView lasttalk=(TextView) viewGroup.findViewById(R.id.tv_lasttalk);
		User user=list.get(arg0);
		//vCard=user.getvCard();
		String dir=BitmapUtils.getImagePath()+"/";
		Bitmap bitmap=BitmapUtils.
			getBitmapByOption(BitmapUtils.getImagePath()+"/"+StringUtil.getUserNameByJid(user.getJID())+".png", 50, 50);
		
		if(bitmap==null&&vCard!=null){
			vCard=userManager.getUserVCard(user.getJID());
			if(vCard!=null){
			if(vCard.getAvatar()!=null){
			bitmap=BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
			BitmapUtils.savaBitmap(new File(dir), StringUtil.getUserNameByJid(user.getJID())+".png", bitmap);
		    bitmap=BitmapUtils.
					getBitmapByOption(BitmapUtils.getImagePath()+"/"+StringUtil.getUserNameByJid(user.getJID())+".png", 50, 50);
			 }
			}
		}
		if(bitmap!=null)
			iv_user_pic.setImageBitmap(bitmap);
		nickname.setText(user.getName());
		nickname.setTag(user);
		return viewGroup;
	}

}
