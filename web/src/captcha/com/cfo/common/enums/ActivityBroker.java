package com.cfo.common.enums;

public enum ActivityBroker {
	
	CJZQ("CJZQ","长江证券");
	
	public final String key;
	public final String value;

	private ActivityBroker(String key,String value) {
		this.key = key;
		this.value = value;
	}
}
