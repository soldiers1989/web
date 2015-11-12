package com.cfo.common.vo;

import com.cfo.common.vo.BaseVo;

/**
 * 站内消息VO
 * @author hailong.qu
 *
 */
public class MessageVo extends BaseVo{
	
	private int msgId;
	private String msgTitle;
	private String msgText;
	private String msgTime;
	private int msgType;
	private String msgTypeName;
	private int publishType;
	private String readed;
	private String bizType;
	private String bizTypeName;
	
	public MessageVo() {
		
	}


	public MessageVo(String bizType, String bizTypeName, int msgId,
			String msgText, String msgTime, String msgTitle, int msgType,
			String msgTypeName, String readed) {
		super();
		this.bizType = bizType;
		this.bizTypeName = bizTypeName;
		this.msgId = msgId;
		this.msgText = msgText;
		this.msgTime = msgTime;
		this.msgTitle = msgTitle;
		this.msgType = msgType;
		this.msgTypeName = msgTypeName;
		this.readed = readed;
	}
	
	public MessageVo(int msgId,
			String msgText, String msgTime, String msgTitle, int msgType,
			String msgTypeName, int publishType,String readed) {
		super();
		this.msgId = msgId;
		this.msgText = msgText;
		this.msgTime = msgTime;
		this.msgTitle = msgTitle;
		this.msgType = msgType;
		this.msgTypeName = msgTypeName;
		this.publishType = publishType;
		this.readed = readed;
	}


	public String getBizType() {
		return bizType;
	}


	public void setBizType(String bizType) {
		this.bizType = bizType;
	}


	public String getBizTypeName() {
		return bizTypeName;
	}


	public void setBizTypeName(String bizTypeName) {
		this.bizTypeName = bizTypeName;
	}


	public int getMsgId() {
		return msgId;
	}


	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}


	public String getMsgText() {
		return msgText;
	}


	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}


	public String getMsgTime() {
		return msgTime;
	}


	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}


	public String getMsgTitle() {
		return msgTitle;
	}


	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}


	public int getMsgType() {
		return msgType;
	}


	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}


	public String getMsgTypeName() {
		return msgTypeName;
	}


	public void setMsgTypeName(String msgTypeName) {
		this.msgTypeName = msgTypeName;
	}


	public String getReaded() {
		return readed;
	}


	public void setReaded(String readed) {
		this.readed = readed;
	}


	public int getPublishType() {
		return publishType;
	}


	public void setPublishType(int publishType) {
		this.publishType = publishType;
	}
	
}
