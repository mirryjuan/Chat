package com.example.mirry.chat.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mirry.chat.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;

import java.util.List;

/**
 * Created by Mirry on 2017/3/26.
 */
public class AllUsersAdapter extends BaseAdapter{
    private Context context;
    private List<String> users = null;
    private String user = "";
    private Handler handler = null;

    public AllUsersAdapter(Context context, List<String> users, Handler handler){
        this.context = context;
        this.users = users;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_user_popup,null);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.delete = (IconFontTextView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        user = users.get(position);
        holder.username.setText(user);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/26 删除指定user的消息记录等数据
                users.remove(position);
                notifyDataSetChanged();
                //删除用户名密码
                PreferencesUtil.deleteItem(context,"users",user);
                String name = PreferencesUtil.getString(context, "config","userName", "");
                if(user.equals(name)){
                    PreferencesUtil.deleteItem(context,"config","userName");
                    PreferencesUtil.deleteItem(context,"config","password");
                    handler.sendEmptyMessage(Common.USER_DELETE);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        TextView username;
        IconFontTextView delete;
    }
}
