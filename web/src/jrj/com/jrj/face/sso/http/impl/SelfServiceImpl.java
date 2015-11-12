/**
 * 
 */
package com.jrj.face.sso.http.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.jrj.common.net.KeyValue;
import com.jrj.common.net.NetException;
import com.jrj.common.page.PageInfo;
import com.jrj.common.utils.ListUtil;
import com.jrj.face.exception.FaceException;
import com.jrj.face.sso.SelfService;
import com.jrj.face.sso.http.impl.mapping.FansInfoMapping;
import com.jrj.face.sso.http.impl.mapping.SelfInfoFullMapping;
import com.jrj.face.sso.http.impl.mapping.SelfInfoMapping;
import com.jrj.face.sso.http.impl.mapping.SingleListMapping;
import com.jrj.face.sso.http.impl.mapping.SingleMapping;
import com.jrj.face.sso.http.impl.mapping.UserIdBySpaceHostMapping;
import com.jrj.face.sso.type.FansInfo;
import com.jrj.face.sso.type.SelfInfo;
import com.jrj.face.sso.type.SelfInfoFull;

/**
 * 个性化信息接口
 * 
 * @author coldwater
 * 
 */
public class SelfServiceImpl extends AbstractHttpJsonService implements
		SelfService {

	private static final String enc = "GBK";

	// 请在HOST文件里添加记录:
	// 59.151.7.12 myjrjface.i.jrj.com.cn
	// 2.4接口地址和参数
	private static final String MANYUSERSELFINFO_URL = "http://iservices.jrjc.local/self/manyUserSelfInfo.jsp";
	private static final String MANYUSERSELFINFO_PARAM = "userIDS";
	private static final String PARAM_SEPARATOR = ",";
	// 2.8接口地址
	private static final String USERINFO_URL = "http://ipages.jrjc.local/pages/self/userInfo_$1.html";

	// 2.1
	private static final String FINDUSERID_BYSPACEHOST_URL = "http://iservices.jrjc.local/self/findUserID.jsp?sh=$1";

	// 2.14 通过多个老用户ID（uid）取得用户ID
	private static final String FINDUSERID_BY_UID = "http://iservices.jrjc.local/self/findUserIDByUID.jsp?uid=$1";
	// 2.15 2.15 通过多个老用户ID（uid）取得用户ID
	private static final String FIND_MANYUSERID_BY_UIDS = "http://iservices.jrjc.local/self/manyUserIDByUIDs.jsp?uids=$1";
	// 2.16 通过多个用户名取得用户ID
	private static final String FIND_USER_BY_USERNAME = "http://iservices.jrjc.local/self/manyUserIDByUserNames.jsp?userNames=$1";
	// 2.17 查询用户名是否已经存在
	private static final String FIND_USER_EXIST = "http://iservices.jrjc.local/self/findUserIsExist.jsp?userName=$1";
	// 2.18 查询个性域名是否可以使用
	private static final String SPACEHOST_EXIST = "http://iservices.jrjc.local/self/spaceHostIsExist.jsp?sh=$1";
	// 3.11 返回关注我的人详细资料
	private static final String FIND_FANS = "http://iservices.jrjc.local/relation/findFans.jsp?UserId=$1&Number=$2&PageSize=$3";
	// 3.15 返回用户被关注数量
	private static final String COUNT_CONCERN = "http://iservices.jrjc.local/relation/countConcern.jsp?userID=$1";

	// 2.8 取得最后登录时间和活跃度
	private static final String ACTIVE_URL = "http://iservices.jrjc.local/self/findUserLoginTimeAndActvity.jsp?userID=$1";

	// 9.1 返回勋章列表
	private static final String HZ_URL = "http://iservices.jrjc.local/medal/medal.jsp?userID=$1";
	// 8.2 查询用户积分
	private static final String CENT_URL = "http://iservices.jrjc.local/cent/cent.jsp?userID=$1";

	/**
	 * 2.4 取得多用户个性化信息（Http）
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public List<SelfInfo> getManyUserSelfInfo(String... ids) {
		Assert.notNull(ids);
		Assert.notEmpty(ids);
		List<SelfInfo> result = null;
		if ((ids != null) && (ids.length > 0)) {
			List<KeyValue> param = new ArrayList<KeyValue>();
			param.add(new KeyValue(MANYUSERSELFINFO_PARAM, ListUtil.join(ids,
					PARAM_SEPARATOR)));
			String context = httpConnectionClient.getContextByPostMethod(
					MANYUSERSELFINFO_URL, param);

			result = getEntityForList(context, SelfInfoMapping.getInstance());
		} else {
			throw new FaceException("Need paramers: ids ");
		}
		return result;
	}

	/**
	 * 2.8 查询用户个性化信息（静态页-修改、登录时触发生成）
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@Override
	public SelfInfoFull getUserInfoFull(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id));
		String context = getUserInfoFullJson(id);
		return parseSelfInfoFull(context);
	}

	/**
	 * 获得用户信息Json数据
	 * 
	 * @param id
	 * @return
	 */
	public String getUserInfoFullJson(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id));
		String context;
		try {
			context = httpConnectionClient.getContextByGetMethod(USERINFO_URL
					.replaceAll("\\$1", id));
		} catch (NetException e) {
			List<KeyValue> params = new ArrayList<KeyValue>();
			params.add(new KeyValue(GETJSON_SERVICE_PARAM, USERINFO_URL
					.replaceAll("\\$1", id)));
			context = httpConnectionClient.getContextByPostMethod(
					GETJSON_SERVICE_URL, params);
		}
		return context;
	}

	/**
	 * 动态获得用户信息Json数据
	 * 
	 * @param id
	 * @return
	 */
	public String getUserInfoFullJsonDynamic(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id));
		String context = "";
		try {

			List<KeyValue> params = new ArrayList<KeyValue>();
			params.add(new KeyValue(GETJSON_SERVICE_PARAM, USERINFO_URL
					.replaceAll("\\$1", id)));
			context = httpConnectionClient.getContextByPostMethod(
					GETJSON_SERVICE_URL, params);

		} catch (NetException e) {
			throw new FaceException("getUserInfoFullJsonDynamic is wrong ");

		}
		return context;
	}

	/**
	 * 转换Json为个性化信息数据
	 * 
	 * @param json
	 * @return
	 */
	public SelfInfoFull parseSelfInfoFull(String json) {
		SelfInfoFull result = null;
		result = getEntity(json, SelfInfoFullMapping.getInstance());
		return result;
	}

	@Override
	public String getUserIdBySpaceHost(String spaceHost) {
		Assert.notNull(spaceHost);
		String cont = this.httpConnectionClient
				.getContextByGetMethod(FINDUSERID_BYSPACEHOST_URL.replaceFirst(
						"\\$1", spaceHost));
		return this.getEntity(cont, UserIdBySpaceHostMapping.getInstance());
	}

	@Override
	public String getUserIdByUid(String uid) {
		Assert.notNull(uid);
		String cont = this.httpConnectionClient
				.getContextByGetMethod(this.FINDUSERID_BY_UID.replaceFirst(
						"\\$1", uid));
		Map map = this.getEntity(cont, SingleMapping.getInstance());

		return (String) map.get("userID");
	}

	@Override
	public List<String[]> getManyUserIdByUids(String uids) {
		Assert.notNull(uids);
		String cont = this.httpConnectionClient
				.getContextByGetMethod(this.FIND_MANYUSERID_BY_UIDS
						.replaceFirst("\\$1", uids));
		List<String[]> list = this.getEntityForList(cont, SingleListMapping
				.getInstance());
		return list;
	}

	@Override
	public boolean findUserIsExistByUserName(String userName) {
		Assert.notNull(userName);
		try {
			userName = java.net.URLEncoder.encode(userName, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			new FaceException("findUserIsExistByUserName urlenoce is wrong", e);
		}
		String cont = this.httpConnectionClient
				.getContextByGetMethod(FIND_USER_EXIST.replaceFirst("\\$1",
						userName));
		Map map = this.getEntity(cont, SingleMapping.getInstance());
		Boolean result = (Boolean) map.get("isExist");
		if (result == null)
			return false;
		return result;
	}

	@Override
	public List<String[]> getManyUserIDByUserNames(String usernames) {
		Assert.notNull(usernames);
		String cont = null;
		try {
			usernames = java.net.URLEncoder.encode(usernames, "UTF-8");
			usernames = java.net.URLEncoder.encode(usernames, "UTF-8");
			cont = this.httpConnectionClient
					.getContextByGetMethod(FIND_USER_BY_USERNAME.replaceFirst(
							"\\$1", usernames));
			// log.debug("==>"+cont);
			cont = java.net.URLDecoder.decode(cont, enc);
			log.debug("1==>" + cont);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			new FaceException("getManyUserIDByUserNames urlenoce is wrong", e);
		}

		List list = this
				.getEntityForList(cont, SingleListMapping.getInstance());

		return list;
	}

	@Override
	public String spaceHostIsExist(String sh) {
		Assert.notNull(sh);
		try {
			sh = java.net.URLEncoder.encode(sh, enc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String cont = this.httpConnectionClient
				.getContextByGetMethod(this.SPACEHOST_EXIST.replaceFirst(
						"\\$1", sh));
		log.debug(cont);
		Map map = this.getEntity(cont, SingleMapping.getInstance());

		return (String) map.get("item");
	}

	@Override
	public List<FansInfo> findFans(String userId, PageInfo page) {
		Assert.notNull(userId);
		String url = this.FIND_FANS.replaceFirst("\\$1", userId);

		url = url.replaceFirst("\\$2", String.valueOf(page.getCurrentPageNo()));
		url = url.replaceFirst("\\$3", String.valueOf(page.getPageSize()));

		String cont = this.httpConnectionClient.getContextByGetMethod(url);

		return this.getEntityForPageList(cont, page, FansInfoMapping
				.getInstance());
	}

	@Override
	public Long getCountConcern(String userId) {
		Assert.notNull(userId);

		String cont = this.httpConnectionClient
				.getContextByGetMethod(this.COUNT_CONCERN.replaceFirst("\\$1",
						userId));

		Map map = this.getEntity(cont, SingleMapping.getInstance());

		return (Long) map.get("rocdeCount");
	}

	private String getContextByGetMethod(String url) {
		log.debug(url);
		try {
			return this.httpConnectionClient.getContextByGetMethod(url);
		} catch (Exception e) {
			throw new FaceException(e);
		}
	}

	@Override
	public String findUserLoginTimeAndActvity(String userID) {
		String url = ACTIVE_URL.replaceFirst("\\$1", userID);
		return getContextByGetMethod(url);
	}

	@Override
	public String getUserCent(String userID) {
		String url = CENT_URL.replaceFirst("\\$1", userID);
		return getContextByGetMethod(url);
	}

	@Override
	public String getUserMedal(String userID) {
		String url = HZ_URL.replaceFirst("\\$1", userID);
		return getContextByGetMethod(url);
	}
}// /~;
