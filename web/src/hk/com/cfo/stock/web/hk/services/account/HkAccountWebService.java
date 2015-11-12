package com.cfo.stock.web.hk.services.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfo.stock.web.hk.hkvo.StockHoldingVo;
import com.jrj.common.service.AbstractBaseService;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityHoldingService;
import com.jrj.stocktrade.hk.api.vo.StockHolding;

@Service
public class HkAccountWebService extends AbstractBaseService {
	
	@Autowired
	HkSecurityHoldingService hkSecurityHoldingService;
	
	/**
	 * 分页查询持仓（本接口只能返回总共500条数据）
	 * @param userId 盈利宝用户id
	 * @param accountId 账户ID
	 * @param pageNo 开始页数（第一页传1）
	 * @param pageSize 页大小
	 * @return 股票持仓
	 * @throws ServiceException
	 */
	public List<StockHoldingVo> stockHoldingQueryPage(String userId,long accountId, int pageNo, int pageSize) 
			throws ServiceException{
		List<StockHoldingVo> result = new ArrayList<StockHoldingVo>();
		List<StockHolding> positions = hkSecurityHoldingService.stockHoldingQueryPage(userId, accountId , pageNo, pageSize);
		if(positions != null && positions.size()>0){
			for(StockHolding stockHolding : positions){
				StockHoldingVo svo = new StockHoldingVo();
				BeanUtils.copyProperties(stockHolding, svo);
				svo.setMarket(stockHolding.getMarket());
				//算市值 当前价*证券数量=市值
				svo.setMarketValue(stockHolding.getLastPrice().multiply(stockHolding.getAvailableQuantity()));
				result.add(svo);
			}
		}
		return result;
	}
	
	/**
	 * 查询所有持仓（本接口只能返回总共500条数据）
	 * @param userId 盈利宝用户id
	 * @param accountId 账户ID
	 * @return 股票持仓
	 * @throws ServiceException
	 */
	public List<StockHoldingVo> stockHoldingQuery(String userId,long accountId)
			throws ServiceException{
		List<StockHoldingVo> result = new ArrayList<StockHoldingVo>();
		List<StockHolding> positions = hkSecurityHoldingService.stockHoldingQuery(userId, accountId);
		if(positions != null && positions.size()>0){
			for(StockHolding stockHolding : positions){
				StockHoldingVo svo = new StockHoldingVo();
				BeanUtils.copyProperties(stockHolding, svo);
				svo.setMarket(stockHolding.getMarket());
				//算市值 当前价*证券数量=市值
				svo.setMarketValue(stockHolding.getLastPrice().multiply(stockHolding.getAvailableQuantity()));
				result.add(svo);
			}
		}
		return result;
	}
}
