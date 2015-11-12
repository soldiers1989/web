/**
 * 
 */
package com.cfo.common;

/**  
 * 
 * @author yuanlong.wang 2013-7-18 
 *  
 */

public enum ShowType {
	资金股份页(1),交易买卖页(2),证券账户页(3),绑定账户页(4),撤单查询页(5),任何位置(0);
	public final int type;

	private ShowType(int type) {
		this.type = type;
	}
	
}
