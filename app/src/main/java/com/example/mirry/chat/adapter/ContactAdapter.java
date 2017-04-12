package com.example.mirry.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.Friend;

import java.util.List;

/**
 * Created by Mirry on 2017/2/10.
 */

public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<Friend> mData = null;

    public ContactAdapter(Context mContext,List<Friend> mData) {
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
        if (convertView == null) {
            convertView = View.inflate (mContext, R.layout.item_contact , null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.pinyin = (TextView) convertView.findViewById(R.id.tv_index);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String preLetter ;
        String letter = mData.get(position).getPinyin().substring(0,1);

        //根据上一个首字母决定是否显示字母
        if(position == 0){
            holder.pinyin.setVisibility(View.VISIBLE);
        }else{
            preLetter = mData.get(position - 1).getPinyin().substring(0,1);
            if (!TextUtils.equals(preLetter,letter)){
                holder.pinyin.setVisibility(View.VISIBLE);
            }else{
                holder.pinyin.setVisibility(View.GONE);
            }
        }

        holder.name.setText(mData.get(position).getName());
        holder.pinyin.setText(letter);

        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView pinyin;
    }
}
