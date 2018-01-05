package com.zscdumin.zhixinapp.bean;

/**
 * Created by Administrator on 2016/1/31.
 */
public class TalkBean {
    public String content;//内容
    public int imageId;//图片ID
    public boolean isAsk;//判断是回答还是询问

    public TalkBean(String content, int imageId, boolean isAsk) {
        this.content = content;
        this.imageId = imageId;
        this.isAsk = isAsk;
    }
}
