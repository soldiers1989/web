package com.cfo.stock.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.alibaba.fastjson.JSONObject;
import com.cfo.stock.web.util.ActionUtils;

public class JsonView extends AbstractView{

	@Override
	protected void renderMergedOutputModel(Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json=new JSONObject(map);
		ActionUtils.sendJsonAjax(json.toJSONString(), response);
	}

}
