package com.zscdumin.zhixinapp.message;

public class Msg {
	private String code;  //属性都定义成String类型，并且属性名要和Json数据中的键值对的键名完全一样
	private String text;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
