package com.example.mirry.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.mirry.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirry on 2017/2/10.
 */

public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<Object> mData = null;

    public ContactAdapter(Context mContext,List<Object> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
//        return mData.size();
        return 0;
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
        ViewHolder holder = null;
        // 通过加了 if (convertView != null) 这个判断条件。就省略了重复创建的浪费资源的情况
        if (convertView == null) {
            convertView = View.inflate ( mContext, R.layout.item_contact , null);
            holder = new ViewHolder();

//            holder. tv_item_name = (TextView) convertView.findViewById(R.id. tv_item_name);
//            holder. tv_item_word = (TextView) convertView.findViewById(R.id. tv_item_word);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder. tv_item_word.setText( data .get(position).getPinyin().substring( 0, 1));
//        holder.tv_item_name .setText( data.get(position).getName());
        return convertView;
    }

    private class ViewHolder{

    }
}
