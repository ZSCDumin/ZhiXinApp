package com.zscdumin.zhixinapp.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/31.
 */
public class VoiceBean {
    public ArrayList<WS> ws;

    public class WS {
        public ArrayList<CW> cw;

    }

    public class CW {
        public  String w;

    }

}
