package com.meng.hui.android.xiezuo.entity;

import org.json.JSONObject;

public class ResponseData {

	public ResponseData() {}

	public ResponseData(int code, String info, String data) {
		this.code = code;
		this.info = info;
		this.data = data;
	}

	private int code;
	private String info;
	private String data;
	
	public int getCode() {
		return code;
	}
	public String getInfo() {
		return info;
	}
	public String getData() {
		return data;
	}
	
}
