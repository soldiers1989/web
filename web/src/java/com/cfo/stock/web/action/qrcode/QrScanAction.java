package com.cfo.stock.web.action.qrcode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfo.common.service.PersonalService;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.qrcode.QrConfig.QrCodeBean;
import com.jrj.stocktrade.api.common.IdType;
import com.jrj.stocktrade.api.common.StockAccountType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.helper.BrokerHelper;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.OpenAccountReq;
import com.jrj.stocktrade.api.rpc.HsRpcContext;

/**
 * 二维码结果扫描 QrScanAction
 * 
 * @history <PRE>
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2015年1月26日    	yuanlong.wang     create  
 * ---------------------------------------------------------
 * </PRE>
 * 
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping("/stock/qrScan")
public class QrScanAction {
	@Autowired
	protected PersonalService personalService;
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	protected BrokerHelper brokerHelper;
	/**
	 * 开户扫描结果
	 * 
	 * @param brokerId
	 * @param type
	 * @param userId
	 * @param sign
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping(value = "/{brokerId}/{type}/{userId}/open", method = RequestMethod.GET)
	@ResponseBody
	public void qrCodeOpen(@PathVariable("brokerId") String brokerId,
			@PathVariable("type") int type,
			@PathVariable("userId") String userId,
			@RequestParam(value = "sign") String sign,
			HttpServletRequest request, HttpServletResponse response,
			HttpSessionWrapper session) {
		if (brokerId == null)
			return;
		String key = brokerId + "_" + type + "_" + userId;
		if (QrConfig.sign(key).equals(sign)) {//简单验证数据
			// TODO 添加开户记录
			UserInfoVo user = personalService.getUserInfo(userId);
			if (user != null) {
				String realName = user.getRealName();// 真实姓名
				String idN = user.getIdNumber();// 用户身份证号
				String mobile = user.getMobile();// 手机号
				String email = user.getEmail();// 邮箱
				boolean insert = true;
				// 判断券商类型
				switch (brokerHelper.getBrokerType(brokerId)) {
				case STOCK: {// A股判断
					if (StringUtils.isBlank(realName)
							|| StringUtils.isBlank(idN)
							|| StringUtils.isBlank(mobile)
							|| "null".equals(realName) || "null".equals(idN)
							|| "null".equals(mobile)) {
						// 数据不完整
						insert = false;
					}
					break;
				}
				case HK: {// 港股判断
					if (StringUtils.isBlank(email) || "null".equals(email)) {
						// 数据不完整
						insert = false;
					}
					break;
				}
				default:
					break;
				}
				if (insert) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("realName", realName);
					params.put("mobile", mobile);
					params.put("email", email);
					params.put("idType", IdType.ID);
					params.put("idNo", idN);
					try {
						// 长江开户二维码都算作app开户
						HsRpcContext.setUserApps("stockrest");
						stockAccountStatusService.openAccount(userId, brokerId,
								StockAccountType.getStockAccountType(type),
								params);
						// 恢复
						HsRpcContext.setUserApps("stockweb");
						
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
			}
		}
		QrCodeBean bean = QrConfig.getQrConfig(brokerId, QrConfig.OPNE_TYPE);
		if (bean != null) {
			try {
				response.sendRedirect(bean.getAppUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
