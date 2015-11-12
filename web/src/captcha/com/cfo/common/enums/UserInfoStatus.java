package com.cfo.common.enums;

/**
 * 用户注册信息状态
 * @author cheng
 *
 */
public enum UserInfoStatus {
	FULL("0"),//用户信息完全
	NEED_ID_INFO("1"),//用户身份信息为空，需要补充身份证号和真实姓名
	UNBIND("2"),//用户未绑定证券账户
	UNOPEN("3");//未开户
	
	public final String status;

	private UserInfoStatus(String status) {
		this.status = status;
	}
	
}
