package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.adapter.ContactAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    @InjectView(R.id.add)
    TextView add;
    @InjectView(R.id.contactList)
    ListView contactList;
    private MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ButterKnife.inject(this, view);

        add.setOnClickListener(this);

        contactList.setAdapter(new ContactAdapter());
        contactList.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

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
