/**
 * 
 */
package com.cfo.common.session.constant;

/**  
 * 
 * @author yuanlong.wang 2013-4-25 
 *  
 */

public class AttributeKeys {
	//登录状态
	public final static String LOGIN="login";
	
	//token 使用Token拦截器
//	public final static String XJB_TOKEN="token";
	
	public final static String JRJ_SSOID="jrj_ssoid";
	
	public final static String JRJ_NAME="jrj_name";
	//登录用户ID
	public final static String USER_ID= "user_id";
	//登录次数
	public final static String LOGIN_COUNT="l_count";
	
	public final static String USER_INFO = "user_info";
	
	//广告来源
	public final static String AD_FROM="ad_from";

	//银行卡绑定时用到地鉴权信息
	public final static String IDENTIFY="identify";
	
	public final static String OPENED="open";
	
	public final static String BINDED="binded";
	
	public final static String FORGET="forget";
	
//	public final static String FROM="sp_from";
	
	//用户会话中保存的标记信息，如活动中购买的基金代码等
	public static final String FLAGS = "flags";
	//用户信息完整状态
	public static final String USER_INFO_STATUS="user_info_status";
	
	//邀请注册码
	public static final String INVITE_CODE="invite_code";
	
	//页面填写的邀请码
	public static final String INVITE_MOBILE="invite_mobile";
	
	//其它页面绑定主卡时传递参数
    public static final String BIND_MASTER="bind_master";
    
    //添加转账银行卡传递参数
    public static final String BIND_OTHER="bind_other";
    
    public static final String BANK_CHANNELID="back_channelid";
    
    //进入股票账户后选择的以绑定券商
    public static final String STOCK_BROKER ="stock_cur_broker";
    
    //股票买 相关
    public static final String STOCK_BUY_STOCKCODE ="stock_buy_name";
    public static final String STOCK_BUY_STOCKNAME ="stock_buy_code";
    public static final String STOCK_BUY_PRICE ="stock_buy_price";
    public static final String STOCK_BUY_COUNT ="stock_buy_count";
    public static final String STOCK_BUY_ENABLE="stock_buy_enable";
    public static final String STOCK_BUY_TRADE_MARKET_TYPE="stock_buy_trade_market_type";
    public static final String STOCK_BUY_TRADE_STOCK_TYPE="stock_buy_trade_stock_type";
    public static final String STOCK_BUY_MATCHING_TYPE = "stock_buy_trade_matching_type";
    //股票卖相关
    public static final String STOCK_SELL_STOCKCODE ="stock_sell_name";
    public static final String STOCK_SELL_STOCKNAME ="stock_sell_code";
    public static final String STOCK_SELL_PRICE ="stock_sell_price";
    public static final String STOCK_SELL_COUNT ="stock_sell_count";
    public static final String STOCK_SELL_ENABLE="stock_sell_enable";
    public static final String STOCK_SELL_TRADE_TYPE="stock_sell_trade_type";
    public static final String STOCK_SELL_MATCHING_TYPE = "stock_sell_matching_type";
    
    
	//股票撤单相关
    public static final String STOCK_CANCEL_ENTRUSTNO = "stock_cancel_entrustno";
    
    //接口防刷
    public static final String STOCK_FS_PREFIX = "stock_fs_";

    //中山交易回调数据存储key
	public static final String STOCK_TRADE_CALLBACK_PARAMS = "stock_trade_callback_params";
	
	//开户入口参数
	public static final String DH_CHANNEL = "kh_channel";

	//当前交易账户ID
	public static final String STOCK_TRADE_ACCOUNT = "stock_trade_accont_id";
	
	//港股密码
	public final static String HK_PASSWORD="hk_password_";
	
	//邮箱验证码前缀
    public static final String STOCK_EMAIL_PREFIX = "stock_email_code_";

	

	
}
