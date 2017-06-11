package com.example.mirry.chat.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.ChatMessage;
import com.example.mirry.chat.utils.HeadUtil;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;

import static com.example.mirry.chat.R.id.msg;


public class ChatMessageAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<ChatMessage> mDatas;
	private String mAccount;

	public ChatMessageAdapter(Context context, List<ChatMessage> mDatas)
	{
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
		mAccount = PreferencesUtil.getString(context,"config","account","");
	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		ChatMessage chatMessage = mDatas.get(position);
		if (chatMessage.getType() == ChatMessage.Type.INCOMING)
		{
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ChatMessage chatMessage = mDatas.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			// 通过ItemType设置不同的布局
			if (getItemViewType(position) == 0)
			{
				convertView = mInflater.inflate(R.layout.item_msg_friend, parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.head = (CircleImageView) convertView.findViewById(R.id.head);
				viewHolder.msg = (TextView) convertView.findViewById(msg);
			} else
			{
				convertView = mInflater.inflate(R.layout.item_msg_me, parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.head = (CircleImageView) convertView.findViewById(R.id.head);
				viewHolder.msg = (TextView) convertView.findViewById(msg);
			}
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置数据
		if(getItemViewType(position) != 0){
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + mAccount +".jpg";
			File file = new File(path);
			if(file.exists()){
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				viewHolder.head.setImageBitmap(bitmap);
			}else{
				if(chatMessage.getHead() != null && !chatMessage.getHead().equals("")){
					HeadUtil.setHead(viewHolder.head,chatMessage.getHead());
				}
			}
		}
		viewHolder.msg.setText(chatMessage.getMsg());
		return convertView;
	}

	private final class ViewHolder
	{
		CircleImageView head;
		TextView msg;
	}

}
