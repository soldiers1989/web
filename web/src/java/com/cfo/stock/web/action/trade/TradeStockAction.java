package com.cfo.stock.web.action.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.jrj.common.utils.DateUtil;
import com.jrj.stocktrade.api.hq.StockHqApi;
import com.jrj.stocktrade.api.hq.vo.HqQuoteinfo;

@Controller
@RequestMapping("/stock/trade")
public class TradeStockAction extends AbstractStockBaseAction {
	/**
	 * 查询所有股票
	 * 
	 * @return
	 */
	@RequestMapping(value = "/stocks", method = RequestMethod.GET, produces = "text/javascript;charset=UTF-8")
	public String stocks() {
		return "trade/trade_stockcode";
	}

	@RequestMapping(value = "/quoteinfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String quoteInfo(
			@RequestParam(value = "n", defaultValue = "mainQuote") String var,
			@RequestParam(value = "code", defaultValue = "") String stockCode) {
		HqQuoteinfo quoteinfo = stockHqService.getQuoteInfo(stockCode);
		JSONObject obj = new JSONObject();
		obj.put("Column", ColumnsMap);
		JSONObject summary=new JSONObject();
		summary.put("stat", 1);
		summary.put("hqtime", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		obj.put("Summary",summary);
		JSONArray arr = new JSONArray();
		try {
			if (quoteinfo != null) {
				List<Object> list = new ArrayList<Object>();
				list.add(quoteinfo.getId());
				list.add(quoteinfo.getCode());
				list.add(quoteinfo.getName());
				list.add(quoteinfo.getLcp());
				list.add(quoteinfo.getNp());
				list.add(quoteinfo.getStp());
				list.add(quoteinfo.getHp());
				list.add(quoteinfo.getLp());
				list.add(quoteinfo.getHhp());
				list.add(quoteinfo.getLlp());
				list.add(quoteinfo.getPa());
				list.add(quoteinfo.getTa());
				for (int i = 0; i < 5; i++) {
					list.add(quoteinfo.getOffer()[i].getPrice());
					list.add(quoteinfo.getOffer()[i].getVolume());
				}
				for (int i = 0; i < 5; i++) {
					list.add(quoteinfo.getBid()[i].getPrice());
					list.add(quoteinfo.getBid()[i].getVolume());
				}
				list.add(quoteinfo.getApe());
				list.add(quoteinfo.getHlp());
				list.add(quoteinfo.getTr());
				list.add(quoteinfo.getCot());
				list.add(quoteinfo.getCat());
				arr.add(list);
			}
		} catch (Exception e) {
		}
		obj.put("HqData", arr);
		return "var " + var + "=" + obj.toJSONString();
	}

	private final static String[] _Columns = { "id", "code", "name", "lcp",
			"np", "stp", "hp", "lp", "hhp", "llp", "pa", "ta", "sp1", "sa1",
			"sp2", "sa2", "sp3", "sa3", "sp4", "sa4", "sp5", "sa5", "bp1",
			"ba1", "bp2", "ba2", "bp3", "ba3", "bp4", "ba4", "bp5", "ba5",
			"ape", "hlp", "tr", "cot", "cat" };
	private final static Map<String, Integer> ColumnsMap = new LinkedHashMap<String, Integer>();
	static {
		for (int i = 0; i < _Columns.length; i++) {
			ColumnsMap.put(_Columns[i], i);
		}
	}

	public static void main(String[] args) {
		System.out.println(JSONObject.toJSONString(ColumnsMap));
	}

	@Autowired
	@Qualifier("stockHqService")
	private StockHqApi stockHqService;

	public StockHqApi getStockHqService() {
		return stockHqService;
	}

	public void setStockHqService(StockHqApi stockHqService) {
		this.stockHqService = stockHqService;
	}

}
