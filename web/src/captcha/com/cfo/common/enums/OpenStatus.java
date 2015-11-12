package com.cfo.common.enums;

public enum OpenStatus {
	
	OPEN(1),
	UNOPEN(0),
	WAIT(-1);
	
	public final int status;
	private OpenStatus(int status){
		this.status=status;
	}
}
