package action;

import java.util.ArrayList;
import java.util.List;

import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.MarketType;

public class IndexActionTest extends AbstractStockBaseAction {
	Long accountId = null;

	public String reUrl(List<UserAccAuth> userAccAuthlist) {
		// 获取默认账户id
		// userAccAuthlist = userAuthService.queryAccessAble(userId);
		int countlist = 0;
		if(userAccAuthlist == null || userAccAuthlist.size()==0){
			return "redirect:/stock/accountManager.jspa";
		}if(userAccAuthlist.get(0).isDef()&&(userAccAuthlist.get(0).equals(BrokerId.CNHT_SECURITIES)||
				userAccAuthlist.get(0).equals(BrokerId.CGWS_SECURITIES)||userAccAuthlist.get(0).equals(BrokerId.ZJZQ_SECURITIES)||
				userAccAuthlist.get(0).equals(BrokerId.ZSZQ_SECURITIES)||userAccAuthlist.get(0).equals(BrokerId.PAZQ_SECURITIES))){
			accountId=userAccAuthlist.get(0).getAccountId();
			MarketType type = userAccAuthlist.get(0)._getMarketType();
			if (type == MarketType.HK) {
				return "redirect:/stock/hk/" + accountId+ "/stockInfoIndex.jspa";
			} else {
				return "redirect:/stock/" + accountId+ "/stockInfoIndex.jspa";
			}
		}else{
			for (UserAccAuth acc : userAccAuthlist) {
				if (acc.getBrokerId().equals(BrokerId.CNHT_SECURITIES)||acc.getBrokerId().equals(BrokerId.CGWS_SECURITIES)||
						acc.getBrokerId().equals(BrokerId.ZJZQ_SECURITIES)||acc.getBrokerId().equals(BrokerId.ZSZQ_SECURITIES)||acc.getBrokerId().equals(BrokerId.PAZQ_SECURITIES)){
				accountId = acc.getAccountId();
				MarketType type = acc._getMarketType();
				if (type == MarketType.HK) {
					return "redirect:/stock/hk/" + accountId+ "/stockInfoIndex.jspa";
				} else {
					return "redirect:/stock/" + accountId+ "/stockInfoIndex.jspa";
				}
			}
				
			}return "redirect:/stock/accountManager.jspa";
		}}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndexActionTest indexActionTest = new IndexActionTest();
		UserAccAuth userAccAuth = new UserAccAuth();
		UserAccAuth userAccAuth1 = new UserAccAuth();
		userAccAuth1.setMarketType(1);
		userAccAuth1.setAccountId(100004);
		userAccAuth.setAccountId(100002);
		userAccAuth.setBrokerId("PNZQ");
		userAccAuth1.setBrokerId("ITN_CJZQ");
		userAccAuth1.setDef(true);
		List<UserAccAuth> l = new ArrayList<UserAccAuth>();
		l.add(userAccAuth1);
		l.add(userAccAuth);

		String s = indexActionTest.reUrl(l);
		System.out.println(s);
	}

}
