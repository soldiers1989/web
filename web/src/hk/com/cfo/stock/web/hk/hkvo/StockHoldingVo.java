package com.cfo.stock.web.hk.hkvo;

import java.math.BigDecimal;

import com.jrj.stocktrade.hk.api.vo.StockHolding;

public class StockHoldingVo  extends StockHolding{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2910071117186491622L;
	private BigDecimal marketValue;

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}
	
}
