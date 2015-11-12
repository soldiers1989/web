package com.cfo.common.enums;

public enum BindStatus {
	
	BIND(1),
	UNBIND(0);
	
	public final int status;

	private BindStatus(int status) {
		this.status = status;
	}
	
}
