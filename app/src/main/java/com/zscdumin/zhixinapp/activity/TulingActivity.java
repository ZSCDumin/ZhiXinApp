package com.zscdumin.zhixinapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.bean.TalkBean;
import com.zscdumin.zhixinapp.bean.VoiceBean;
import com.zscdumin.zhixinapp.message.GetHttpMessage;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TulingActivity extends AppCompatActivity {
    private StringBuffer sbuff;
    @BindView(R.id.answer)
    ListView answer;
    private MyAdapter myAdapter;
    private ArrayList<TalkBean> mlist = new ArrayList<TalkBean>();

    private String message;
    private String answers;
    private String askContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
        myAdapter = new MyAdapter();
        answer.setAdapter(myAdapter);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56adade1");
        compose("主人您好,我是图灵机器人!");
    }


    /**
     * 语音识别
     *
     * @param v
     */
    public void startListen(View v) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        sbuff = new StringBuffer();
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            //最终的识别结果
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                String result = recognizerResult.getResultString();
                String spreak_word = pareData(result);
                sbuff.append(spreak_word + "  ");
                if (b) {
                    askContent = sbuff.toString();//得到最终结果
                    TalkBean askBean = new TalkBean(askContent, -1, true);//初始化提问对象
                    mlist.add(askBean);
                    answers = "这个问题我们机器要开个会，等商量出来再告诉你";
                    new GetMessage().start();
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private String pareData(String _json) {
        //Gson解析
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(_json, VoiceBean.class);
        StringBuffer sb = new StringBuffer();
        ArrayList<VoiceBean.WS> ws = voiceBean.ws;
        for (VoiceBean.WS w : ws) {
            String word = w.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public TalkBean getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.list_iten, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }
            TalkBean item = getItem(position);
            if (item.isAsk) {
                //提问
                holder.tvAsk.setVisibility(View.VISIBLE);
                holder.llAnswer.setVisibility(View.GONE);
                holder.tvAsk.setText(item.content);
            } else {

                holder.tvAsk.setVisibility(View.GONE);
                holder.tvAnswer.setVisibility(View.VISIBLE);
                holder.tvAnswer.setText(item.content);
                //图片
                if (item.imageId > 0) {
                    holder.ivPic.setVisibility(View.VISIBLE);
                    holder.ivPic.setImageResource(item.imageId);
                } else {
                    holder.ivPic.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_ask)
        TextView tvAsk;
        @BindView(R.id.tv_answer)
        TextView tvAnswer;
        @BindView(R.id.tv_pic)
        ImageView ivPic;
        @BindView(R.id.ll_answer)
        LinearLayout llAnswer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 语音合成
     *
     * @param speak
     */
    public void compose(String speak) {

        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);

        //2.合成参数设置
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "60");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(speak, null);

    }

    private class GetMessage extends Thread {
        public void run() {
            Message msg = new Message();
            try {
                msg.what = 1;
                message = new GetHttpMessage().testGetRequest(askContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int imageId = -1;
                Toast.makeText(TulingActivity.this, message, Toast.LENGTH_SHORT);
                answers = message;
                TalkBean answerBean = new TalkBean(answers, imageId, false);
                mlist.add(answerBean);
                myAdapter.notifyDataSetChanged();
                compose(answers);
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
