package com.cfo.stock.web.exception;

import com.jrj.common.exception.JrjBaseException;


@SuppressWarnings("serial")
public class StockServiceException extends JrjBaseException {
	private int retcode;
	private String msg;
	public StockServiceException(int retcode, String msg) {
		super(msg);
		this.retcode = retcode;
		this.msg = msg;
	}

	public int getRetcode() {
		return retcode;
	}
	public void setRetcode(int retcode) {
		this.retcode = retcode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
