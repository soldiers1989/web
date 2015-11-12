package com.cfo.common.service.api.param;

import com.alibaba.fastjson.JSONObject;

public class Param {
	public Param() {
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return JSONObject.toJSONString(this);
	}
}
