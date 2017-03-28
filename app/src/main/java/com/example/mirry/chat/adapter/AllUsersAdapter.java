package com.example.mirry.chat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.SharedPreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Mirry on 2017/3/26.
 */
public class AllUsersAdapter extends BaseAdapter{
    private Context context;
    private List<String> users = null;
    private String user = "";

    public AllUsersAdapter(Context context, List<String> users){
        this.context = context;
        this.users = users;
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
                SharedPreferencesUtil.deleteItem(context,"users",user);
            }
        });
        return convertView;
    }

    private class ViewHolder{
        TextView username;
        IconFontTextView delete;
    }
}
