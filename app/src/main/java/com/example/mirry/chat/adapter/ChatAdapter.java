package com.example.mirry.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.bean.Me;
import com.example.mirry.chat.view.CircleImageView;

import java.util.List;

/**
 * Created by Mirry on 2017/3/6.
 */

public class ChatAdapter extends BaseAdapter {
    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;
    private Context mContext;
    private List<Object> mData = null;


    public ChatAdapter(Context mContext,List<Object> mData) {
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
        int type = getItemViewType(position);
        ViewHolderMe holderMe = null;
        ViewHolderFriend holderFriend = null;
        if(convertView == null){
            switch (type){
                case TYPE_ME:
                    holderMe = new ViewHolderMe();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_msg_me, parent, false);
                    holderMe.head = (CircleImageView) convertView.findViewById(R.id.head);
                    holderMe.msg = (TextView) convertView.findViewById(R.id.msg);
                    convertView.setTag(R.id.Tag_Me,holderMe);
                    break;
                case TYPE_FRIEND:
                    holderFriend = new ViewHolderFriend();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_msg_friend, parent, false);
                    holderFriend.head = (CircleImageView) convertView.findViewById(R.id.head);
                    holderFriend.msg = (TextView) convertView.findViewById(R.id.msg);
                    convertView.setTag(R.id.Tag_Friend,holderFriend);
                    break;
            }
        }else{
            switch (type){
                case TYPE_ME:
                    holderMe = (ViewHolderMe) convertView.getTag(R.id.Tag_Me);
                    break;
                case TYPE_FRIEND:
                    holderFriend = (ViewHolderFriend) convertView.getTag(R.id.Tag_Friend);
                    break;
            }
        }

        Object obj = mData.get(position);
        //设置下控件的值
        switch (type){
            case TYPE_ME:
                Me me = (Me) obj;
                if(me != null){
                    holderMe.msg.setText(me.getMsg());
                }
                break;
            case TYPE_FRIEND:
                Friend friend = (Friend) obj;
                if(friend != null){
                    holderFriend.msg.setText(friend.getMsg());
                }
                break;
        }

        return convertView;
    }

    //两个不同的ViewHolder
    private class ViewHolderMe{
        CircleImageView head;
        TextView msg;
    }

    private class ViewHolderFriend{
        CircleImageView head;
        TextView msg;
    }

    //多布局的核心，通过这个判断类别
    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof Me) {
            return TYPE_ME;
        } else if (mData.get(position) instanceof Friend) {
            return TYPE_FRIEND;
        } else {
            return super.getItemViewType(position);
        }
    }

    //类别数目
    @Override
    public int getViewTypeCount() {
        return 2;
    }

}
