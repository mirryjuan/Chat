package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.adapter.MsgAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.add)
    TextView add;
    @InjectView(R.id.msgList)
    ListView msgList;
    private MainActivity mActivity;

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

        add.setOnClickListener(this);

        msgList.setAdapter(new MsgAdapter());
        //消息列表单击事件
        msgList.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mActivity, "添加", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
