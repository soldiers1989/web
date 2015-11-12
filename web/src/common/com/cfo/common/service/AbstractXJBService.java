/**
 * 
 */
package com.cfo.common.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.cfo.common.service.api.UserXJBServiceAPI;
import com.cfo.stock.web.platform.UserCenterInterface;
import com.jrj.common.service.AbstractBaseService;

/**
 * 现金宝业务基类
 * @author coldwater
 *
 */
public abstract class AbstractXJBService extends AbstractBaseService {
	//用户模块接口
	@Autowired
	protected UserXJBServiceAPI userXJBApi;
	@Autowired
	protected UserCenterInterface userCenterInterface;
}
