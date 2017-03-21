package com.example.mirry.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;

import java.util.List;
import java.util.Map;
/**
 * Created by Mirry on 2017/3/18.
 */
public class NewFriendAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> mData;
    private boolean addShow = false;

    public NewFriendAdapter(Context context, List<Map<String, Object>> list,boolean addShow) {
        this.context = context;
        mData = list;
        this.addShow = addShow;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_friend_new, null);
            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.account = (TextView) convertView.findViewById(R.id.account);
            holder.sexImg = (ImageView) convertView.findViewById(R.id.img_sex);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.add = (Button) convertView.findViewById(R.id.add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String,Object> info = mData.get(position);
        if (info != null) {
//            holder.head.setImageResource((Integer)info.get("head"));
            holder.nickname.setText(info.get("nickname")==null?"":info.get("nickname").toString());
            holder.account.setText(info.get("account")==null?"":info.get("account").toString());
            holder.sex.setText(info.get("sex")==null?"":info.get("sex").toString());
//            holder.sexImg.setImageResource((Integer) info.get("sexImg"));

            if(addShow){
                holder.add.setVisibility(View.VISIBLE);
                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2017/3/19 添加好友操作
                    }
                });
            }
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView head;
        TextView nickname;
        TextView account;
        ImageView sexImg;
        TextView sex;
        Button add;
    }
}
