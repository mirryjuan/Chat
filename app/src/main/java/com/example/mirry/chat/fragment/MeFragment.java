package com.example.mirry.chat.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.ContactInfoActivity;
import com.example.mirry.chat.activity.GalleryActivity;
import com.example.mirry.chat.activity.LoginActivity;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.SettingsActivity;
import com.example.mirry.chat.activity.ShareActivity;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.notes.NotesDBHelper;
import com.example.mirry.chat.utils.HeadUtil;
import com.example.mirry.chat.utils.NimUserInfoCache;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MeFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    TextView nickname;
    @InjectView(R.id.settings)
    Button settings;
    @InjectView(R.id.share)
    Button share;
    @InjectView(R.id.exit)
    Button exit;
    @InjectView(R.id.info_user)
    LinearLayout userInfo;
    @InjectView(R.id.gallery)
    Button gallery;
    private MainActivity mActivity;
    private String mNickname = "";
    private int mSex = Common.MALE;
    private String mAccount = "";
    private String mPhone = "";
    private String mBirthday = "";

    private Map<String, Object> mInfo = null;

    private ArrayList<String> imgUrls = new ArrayList<>();
    private String mHead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        ButterKnife.inject(this, view);

        initUserData();

        head.setOnClickListener(this);
        gallery.setOnClickListener(this);
        nickname.setOnClickListener(this);
        settings.setOnClickListener(this);
        userInfo.setOnClickListener(this);

        share.setOnClickListener(this);

        exit.setOnClickListener(this);
        return view;
    }

    private void initUserData() {
        String account = PreferencesUtil.getString(mActivity, "config", "account", "");
        queryUserData(account);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryUserData(mAccount);
    }

    public void queryUserData(String account) {
//        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo nimUserInfo) {
                if (nimUserInfo != null) {
                    mAccount = nimUserInfo.getAccount();
                    mNickname = nimUserInfo.getName();
                    mSex = nimUserInfo.getGenderEnum().getValue();
                    mPhone = nimUserInfo.getMobile();
                    mBirthday = nimUserInfo.getBirthday();
                    mHead = nimUserInfo.getAvatar();

                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + mAccount +".jpg";
                    File file = new File(path);
                    if(file.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        head.setImageBitmap(bitmap);
                    }else{
                        if(mHead != null && !mHead.equals("")){
                            HeadUtil.setHead(head, mHead);
                        }
                    }


                    if (mNickname == null || mNickname.equals("")) {
                        nickname.setText(mAccount);
                    } else {
                        nickname.setText(mNickname);
                    }

                }
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
            case R.id.nickname:
            case R.id.info_user:
                Intent intent = new Intent(mActivity, ContactInfoActivity.class);
                intent.putExtra("account", mAccount);
                startActivity(intent);
                break;
            case R.id.gallery:
                getPicRes();
                Intent intentImg = new Intent(mActivity, GalleryActivity.class);
                intentImg.putStringArrayListExtra("imgUrls",imgUrls);
                startActivity(intentImg);
                break;
            case R.id.settings:
                startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case R.id.share:
                startActivity(new Intent(mActivity, ShareActivity.class));
                break;
            case R.id.exit:
                NIMClient.getService(AuthService.class).logout();
                startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    private void getPicRes() {
        imgUrls.clear();
        NotesDBHelper helper = new NotesDBHelper(mActivity);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(NotesDBHelper.TABLE_NAME, new String[]{NotesDBHelper.PATH}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String path = cursor.getString(cursor.getColumnIndex(NotesDBHelper.PATH));
            if(!path.equals("null")){
                imgUrls.add(path);
            }
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
