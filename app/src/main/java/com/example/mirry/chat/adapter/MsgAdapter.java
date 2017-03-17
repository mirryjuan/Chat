package com.example.mirry.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.Message;
import com.example.mirry.chat.view.CircleImageView;

import java.util.List;

/**
 * Created by Mirry on 2017/2/10.
 */

public class MsgAdapter extends BaseAdapter {
    private Context mContext;
    private List<Message> mData = null;
    public MsgAdapter(Context mContext, List<Message> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
//        return mData.size();
        return 10;
    }

    @Override
    public Object getItem(int position) {
//        return mData.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
//        return position;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_message,null);
            holder = new ViewHolder();

            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            holder.remark = (TextView) convertView.findViewById(R.id.remark);
            holder.msg = (TextView) convertView.findViewById(R.id.msg);
            holder.msgCount = (TextView) convertView.findViewById(R.id.count_msg);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


//        holder.head.setImageResource(R.drawable.head);

        return convertView;
    }

    private class ViewHolder {
        CircleImageView head;
        TextView remark;
        TextView msg;
        TextView msgCount;
    }
}
