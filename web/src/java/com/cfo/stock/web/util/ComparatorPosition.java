package com.cfo.stock.web.util;

import java.util.Comparator;

import com.jrj.stocktrade.api.stock.vo.StockInfo;

public class ComparatorPosition implements Comparator<StockInfo>{

	@Override
	public int compare(StockInfo stock1, StockInfo stock2) {		
		return stock1.getMarketValue().compareTo(stock2.getMarketValue());
	}


}
