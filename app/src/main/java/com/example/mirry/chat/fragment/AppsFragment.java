package com.example.mirry.chat.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.AppsActivity;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.RobotActivity;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.notes.NoteActivity;
import com.example.mirry.chat.utils.ImageUtil;
import com.example.zxing.activity.CaptureActivity;
import com.example.zxing.activity.CodeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tencent.open.utils.Global.getPackageName;

public class AppsFragment extends Fragment implements View.OnClickListener {
    private MainActivity mActivity;
    private LinearLayout scan;
    private LinearLayout robot;
    private LinearLayout record;
    private LinearLayout news;
    private LinearLayout weather;
    private LinearLayout joke;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(mActivity,R.layout.fragment_apps,null);
        scan = (LinearLayout) view.findViewById(R.id.scan);
        robot = (LinearLayout) view.findViewById(R.id.robot);
        record = (LinearLayout) view.findViewById(R.id.record);
        news = (LinearLayout) view.findViewById(R.id.news);
        weather = (LinearLayout) view.findViewById(R.id.weather);
        joke = (LinearLayout) view.findViewById(R.id.joke);

        scan.setOnClickListener(this);
        robot.setOnClickListener(this);
        record.setOnClickListener(this);
        news.setOnClickListener(this);
        weather.setOnClickListener(this);
        joke.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(mActivity, AppsActivity.class);
        switch (v.getId()){
            case R.id.scan:
                openCamera();
                break;
            case R.id.robot:
                startActivity(new Intent(mActivity,RobotActivity.class));
                break;
            case R.id.record:
//                intent.putExtra("item","record");
//                startActivity(intent);
//                Toast.makeText(mActivity, "暂未实现", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mActivity,NoteActivity.class));
                break;
            case R.id.news:
                intent.putExtra("item","news");
                startActivity(intent);
                break;
            case R.id.weather:
                intent.putExtra("item","weather");
                startActivity(intent);
                break;
            case R.id.joke:
                intent.putExtra("item","joke");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void openCamera() {
        //检查权限
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity,Manifest.permission.CAMERA)){
                //已经禁止提示了
                Toast.makeText(mActivity, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, Common.CALL_CAMERA);
            }
        } else {
            startActivityForResult(new Intent(mActivity, CaptureActivity.class), Common.REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == Common.REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(mActivity, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    Pattern pattern = Pattern
                            .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
                    // 忽略大小写的写法
                    Matcher matcher = pattern.matcher(result);
                    // 字符串是否与正则表达式相匹配
                    boolean rs = matcher.matches();
                    if(rs == true){
                        Uri uri = Uri.parse(result);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }else{
                        Toast.makeText(mActivity, "解析结果："+result, Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(mActivity, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

        /**
         * 选择系统图片并解析
         */
        else if (requestCode == Common.REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(mActivity, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(mActivity, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(mActivity, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == Common.REQUEST_CAMERA_PERM) {
            Toast.makeText(mActivity, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Common.CALL_CAMERA:
                if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //用户同意授权
                    startActivityForResult(new Intent(mActivity, CaptureActivity.class), Common.REQUEST_CODE);
                }else{
                    //用户拒绝授权
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

                    String pkg = "com.android.settings";
                    String cls = "com.android.settings.applications.InstalledAppDetails";

                    intent.setComponent(new ComponentName(pkg, cls));
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                break;
        }
    }

}
