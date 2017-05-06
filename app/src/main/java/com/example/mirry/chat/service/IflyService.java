package com.example.mirry.chat.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.mirry.chat.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Mirry on 2017/5/5.
 */

public class IflyService {
    private Context context = null;
    private StringBuffer buffer = new StringBuffer();
    private String mResult = null;
    private String mEngineType = "";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public IflyService(Context context) {
        this.context = context;
    }


    public void getResultOnline(){
        mEngineType = SpeechConstant.TYPE_CLOUD;

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(context, mInitListener);

        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    };


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("code","初始化失败，错误码：" + code);
            }
        }
    };

    public interface OnRecordFinishListener{
        void onRecordFinish(String result);
    }

    public OnRecordFinishListener getListener() {
        return listener;
    }

    public void setListener(OnRecordFinishListener listener) {
        this.listener = listener;
    }

    private OnRecordFinishListener listener;

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = getResult(results);
            if(!isLast){
                buffer.append(result);
            }else{
                mResult = buffer.toString();
                if(listener != null){
                    listener.onRecordFinish(mResult);
                }
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            int code = error.getErrorCode();
            switch (code){
                case 20006:
                    Toast.makeText(context, "请在\"设置\"中开启录音权限", Toast.LENGTH_SHORT).show();
                    break;
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if(mIatDialog.isShowing()){
                        mIatDialog.dismiss();
                    }
                }
            }, 1500);
        }

    };

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = "mandarin";
        // 设置语言     汉语
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域    普通话
        mIat.setParameter(SpeechConstant.ACCENT, lag);

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,"4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private String getResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        return resultBuffer.toString();
    }
}
