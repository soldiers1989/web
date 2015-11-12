/**
 * 
 */
package com.cfo.common;

/**  
 * 
 * @author yuanlong.wang 2013-7-18 
 *  
 */

public enum PublishType {
	系统公告(1),交易公告(2),活动公告(3),事件公告(4);
	public final int type;
	private PublishType(int type) {
		this.type = type;
	}
	
}
