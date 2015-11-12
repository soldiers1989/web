package com.cfo.stock.web.action.password;

import org.apache.commons.lang.ArrayUtils;

import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.BrokerType;

public final class PasswordShape {
	public final static int IFRAME = 1;// iframe
	public final static int ACTIVE = 2;// 控件
	public final static int NONE = 3;// 不要密码

	private final static String[] ACTIVEs = { BrokerId.CGWS_SECURITIES,
			BrokerId.CITIC_SECURITIES };
	private final static String[] IFRAMEs = { BrokerId.CJZQ_SECURITIES,
			BrokerId.CNHT_SECURITIES, BrokerId.ZJZQ_SECURITIES,
			BrokerId.ZXGJ_HK_SECURITIES };

	public static boolean isActive(Broker broker) {
		if(broker==null)return false;
		if (BrokerType.ITN == BrokerType.getType(broker.getBrokerType())
				|| ArrayUtils.contains(ACTIVEs, broker.getBrokerId())) {
			return true;
		}
		return false;
	}
}
