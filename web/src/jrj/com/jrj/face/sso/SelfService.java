/**
 * 
 */
package com.jrj.face.sso;

import java.util.List;

import com.jrj.common.page.PageInfo;
import com.jrj.face.FaceService;
import com.jrj.face.sso.type.FansInfo;
import com.jrj.face.sso.type.SelfInfo;
import com.jrj.face.sso.type.SelfInfoFull;

/**
 * 个性化信息接口
 * 
 * @author coldwater
 * 
 */
public interface SelfService extends FaceService {
	
	/**
	 * 2.4 取得多用户个性化信息（Http）
	 * 
	 * @param id
	 * @return
	 */
	public List<SelfInfo> getManyUserSelfInfo(String... ids);
	
	/**
	 * 2.8	查询用户个性化信息（静态页-修改、登录时触发生成）
	 * @param id	用户id
	 * @return
	 */
	public SelfInfoFull getUserInfoFull(String id);
	/**
	 * 获得用户信息Json数据
	 * @param id
	 * @return
	 */
	public String getUserInfoFullJson(String id);
	
	/**
	 * 获得动态用户信息Json数据
	 * @param id
	 * @return
	 */
	
	public String getUserInfoFullJsonDynamic(String id);
	/**
	 * 转换Json为个性化信息数据
	 * @param json
	 * @return
	 */
	public SelfInfoFull parseSelfInfoFull(String json);
	/**
	 * 更加个性域名取用户id
	 * @param spaceHost 个性域名
	 * @return String userId
	 */
	public String getUserIdBySpaceHost(String spaceHost);
	/**
	 * 2.14	通过老用户ID（uid）取得用户ID
	 * @param uid
	 * @return String
	 */
	public String getUserIdByUid(String uid);
	/**
	 * 2.15	通过多个老用户ID（uid）取得用户ID
	 * @param uids xxx,xxx
	 * @return list<【uid,userID】>
	 */
	public List<String[]> getManyUserIdByUids(String uids);
	/**
	 * 2.16	通过多个用户名取得用户ID
	 * @param usernames 可以多个，用半角逗号隔开，程序内URLEncode编码，调用时候不必要考虑
	 * @return List<【userName，userID】>
	 */
	public List<String[]> getManyUserIDByUserNames(String usernames);
	/**
	 * 2.17	查询用户名是否已经存在
	 * @param userName,调用时候不用考虑 URLEncode
	 * @return boolean
	 */
	public boolean findUserIsExistByUserName(String userName);
	/**
	 * 2.18	查询个性域名是否可以使用
	 * <p>
	 * ok-可以使用，errorExist-已经被使用，errorNotUse-包含关键词不允许被使用，errorFormat-域名格式错误
	 * </p>
	 * @param sh
	 * @return String
	 */
	public String spaceHostIsExist(String sh);
	/**
	 * 3.11	返回关注我的人详细资料
	 * @param userId
	 * @param page
	 * @return
	 */
	public List<FansInfo> findFans(String userId,PageInfo page);
	/**
	 * 3.15	返回用户被关注数量
	 * @param userId
	 * @return
	 */
	public  Long getCountConcern(String userId);
	/**
	 * 2.8	取得最后登录时间和活跃度 的
	 * @param userID
	 * @return json串
	 */
	public String findUserLoginTimeAndActvity(String userID);
	/**
	 * 8.2	查询用户积分
	 * @param userID
	 * @return json串
	 */
	public String getUserCent(String userID);
	/**
	 * .1	返回勋章列表
	 * @param userID
	 * @return json串
	 */
	public String getUserMedal(String userID);
	
	
	
	
}
