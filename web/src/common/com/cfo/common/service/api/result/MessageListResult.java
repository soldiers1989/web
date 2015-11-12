package com.cfo.common.service.api.result;


import java.util.ArrayList;
import java.util.List;

import com.cfo.common.vo.MessageVo;
/**
 * 消息列表 查询结果
 * @author hailong.qu
 *
 */
@SuppressWarnings("rawtypes")
public class MessageListResult extends Result{
	private List<MessageVo> messagelist=new ArrayList<MessageVo>();
	
	private Integer totalPage;
	private Integer totalSize;
	
	public List<MessageVo> getMessagelist() {
		return messagelist;
	}
	public void setMessagelist(List<MessageVo> messagelist) {
		this.messagelist = messagelist;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Integer getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}


}
