package com.example.mirry.chat.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.ChatActivity;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.adapter.MsgAdapter;
import com.example.mirry.chat.bean.Msg;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.utils.HeadUtil;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @InjectView(R.id.msgList)
    ListView msgList;
    @InjectView(R.id.emptyView)
    LinearLayout emptyView;
    private MainActivity mActivity;
    private List<Msg> msgData = null;

    private Bundle newMsg;

    private String account;
    private String content;
    private String nickname;
    private int count;

    private String mAccount;

    private MsgAdapter adapter;
    private List<Map<String,String>> messages = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.MSG_COMING:
                    if(newMsg == null){
                        newMsg = new Bundle();
                    }
                    newMsg = msg.getData();
                    account = newMsg.getString("fromAccount");
                    nickname = newMsg.getString("fromNick");
                    content = newMsg.getString("content");
                    count = newMsg.getInt("count");
                    updateMsgList(account,nickname,content,count);
                    Collections.reverse(msgData);
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ButterKnife.inject(this, view);

        msgData = new ArrayList<>();
        adapter = new MsgAdapter(mActivity,msgData);
        msgList.setAdapter(adapter);
        msgList.setEmptyView(emptyView);

        initData();

        //消息列表单击事件
        msgList.setOnItemClickListener(this);
        msgList.setOnItemLongClickListener(this);

        return view;
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(mActivity,"config","account","");
        messages = (List<Map<String, String>>) getArguments().getSerializable("messages");
        if(messages.size() > 0 ){
            for (Map<String, String> message:messages){
                account = message.get("fromAccount");
                nickname = message.get("fromNick");
                content = message.get("content");
                count = Integer.parseInt(message.get("count"));
                updateMsgList(account,nickname,content,count);
            }
            Collections.reverse(msgData);
            adapter.notifyDataSetChanged();
        }
    }


    private void updateMsgList(String account, String nickname, String content,int count) {
        for (Msg message:msgData) {
            if(message.getAccount().equals(account)){
                msgData.remove(message);
                break;
            }
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + account+".jpg";
        File file = new File(path);
        if(!file.exists()){
            HeadUtil.downloadHeadImg(true, account, queryUserHeadUrl(account));
        }

        Msg message = new Msg();
        message.setAccount(account);
        if(!nickname.equals("")){
            message.setUsername(nickname);
        }else{
            message.setUsername(account);
        }

        message.setMsg(content);
        message.setCount(count);

        message.setHead(queryUserHeadUrl(account));
        msgData.add(message);
    }

    private String queryUserHeadUrl(String account){
        String url = "";
        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        if(mInfo != null){
            url = mInfo.getAvatar();
        }
        return url;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Msg msg = msgData.get(position);
        String curAccount = msg.getAccount();
        String curUsername = msg.getUsername();
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("curAccount",curAccount);
        intent.putExtra("curUsername",curUsername);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("删除该消息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //长按删除最近联系人
                        Msg msg = msgData.get(position);
                        String curAccount = msg.getAccount();
                        NIMClient.getService(MsgService.class).deleteRecentContact2(curAccount, SessionTypeEnum.P2P);
                        msgData.remove(msg);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}


