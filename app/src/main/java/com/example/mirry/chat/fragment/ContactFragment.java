package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.adapter.ContactAdapter;
import com.example.mirry.chat.utils.ToastUtil;
import com.example.mirry.chat.view.QuickIndexBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView contactList;
    private MainActivity mActivity;
    private QuickIndexBar bar;
    private List<Object> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        contactList = (ListView) view.findViewById(R.id.contactList);
        bar = (QuickIndexBar) view.findViewById(R.id.bar);

        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                ToastUtil.showToast(mActivity,letter);
            }
        });
        contactList.setAdapter(new ContactAdapter(mActivity,list));
        contactList.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
