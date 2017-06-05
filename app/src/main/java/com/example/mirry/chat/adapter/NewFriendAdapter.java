package com.example.mirry.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;

import java.util.List;
import java.util.Map;

import static com.example.mirry.chat.R.id.account;

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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_friend_new, null);
            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.account = (TextView) convertView.findViewById(account);
            holder.sexImg = (ImageView) convertView.findViewById(R.id.img_sex);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.line = convertView.findViewById(R.id.line);
            holder.addInfo = (LinearLayout) convertView.findViewById(R.id.info_add);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.add = (Button) convertView.findViewById(R.id.add);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Map<String,Object> info = mData.get(position);
        if (info != null) {
//            if(info.get("head") != null){
//                holder.head.setImageResource((Integer)info.get("head"));
//            }
            holder.nickname.setText(info.get("nickname")==null?"":info.get("nickname").toString());
            holder.account.setText(info.get("account")==null?"":info.get("account").toString());
            if(info.get("sex").equals(GenderEnum.FEMALE)){
                holder.sex.setText("女");
                holder.sexImg.setImageResource(R.drawable.female);
            }else{
                holder.sex.setText("男");
                holder.sexImg.setImageResource(R.drawable.male);
            }

            if(addShow){
                holder.line.setVisibility(View.VISIBLE);
                holder.addInfo.setVisibility(View.VISIBLE);
                holder.content.setText(info.get("content")==null?"":info.get("content").toString());
                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String friendAccount = info.get("account").toString();
                        NIMClient.getService(FriendService.class).ackAddFriendRequest(friendAccount, true); // 通过对方的好友请求

                        holder.add.setVisibility(View.GONE);
                        holder.status.setVisibility(View.VISIBLE);
                        holder.status.setText("已添加");
                        // TODO: 2017/3/19 加入通讯录
                        Intent intent = new Intent("myBroadcastReceiver");
                        intent.setAction(Common.ADD);
                        intent.putExtra("account",friendAccount);
                        context.sendBroadcast(intent);
                    }
                });
            }else{
                holder.line.setVisibility(View.GONE);
                holder.addInfo.setVisibility(View.GONE);
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
        TextView content;
        View line;
        LinearLayout addInfo;
        TextView status;
    }
}
