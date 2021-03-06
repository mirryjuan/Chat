package com.example.mirry.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.Msg;
import com.example.mirry.chat.utils.HeadUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.List;

/**
 * Created by Mirry on 2017/2/10.
 */

public class MsgAdapter extends BaseAdapter {
    private Context mContext;
    private List<Msg> mData = null;
    public MsgAdapter(Context mContext, List<Msg> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_message,null);
            holder = new ViewHolder();

            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.msg = (TextView) convertView.findViewById(R.id.msg);
            holder.msgCount = (TextView) convertView.findViewById(R.id.count_msg);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Msg msg = mData.get(position);
//        holder.head.setImageResource(R.drawable.head);
        if(msg.getHead() != null && !msg.getHead().equals("")){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + msg.getAccount() +".jpg";
            File file = new File(path);
            if(file.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                holder.head.setImageBitmap(bitmap);
            }else{
                HeadUtil.setHead(holder.head,msg.getHead());
            }
        }

        holder.username.setText(msg.getUsername());
        holder.msg.setText(msg.getMsg());
        if(msg.getCount() == 0){
            holder.msgCount.setVisibility(View.GONE);
        }else{
            holder.msgCount.setVisibility(View.VISIBLE);
            holder.msgCount.setText(""+msg.getCount());
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView head;
        TextView username;
        TextView msg;
        TextView msgCount;
    }
}
