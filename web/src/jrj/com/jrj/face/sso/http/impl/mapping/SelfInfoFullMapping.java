/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jrj.common.utils.DateUtil;
import com.jrj.face.JsonObjectMapping;
import com.jrj.face.exception.FaceException;
import com.jrj.face.sso.type.SelfInfoFull;

/**
 * 个性化信息完整版映射
 * 
 * @author coldwater
 * 
 */
public class SelfInfoFullMapping extends AbstractJsonMapping implements
		JsonObjectMapping<SelfInfoFull> {
	/**
	 * 头像地址前缀
	 */
	public static final String HEAD_PIC_URL_PREFIX = "http://i5.jrjimg.cn";
	//我的金融界上传头像格式:宽,高,文件名后缀
	public static final String MYJRJ_AVATAR_FORMATS = "m,150,150,_m;s,75,75,_s;xs,50,50,_xs;ms,25,25,_ms;";

	private static SelfInfoFullMapping instance = null;

	public static SelfInfoFullMapping getInstance() {
//		if (instance == null) {
			instance = new SelfInfoFullMapping();
//		}
		return instance;
	}

	private SelfInfoFullMapping() {

	}

	private String getheadPicM(String headPic) {
		return HEAD_PIC_URL_PREFIX+"/"+getFormatPath(headPic, "m");
	}

	private String getheadPicXS(String headPic) {
		return HEAD_PIC_URL_PREFIX+"/"+getFormatPath(headPic, "xs");
	}

	private String getheadPicMS(String headPic) {
		return HEAD_PIC_URL_PREFIX+"/"+getFormatPath(headPic, "ms");
	}

	private String getheadPicS(String headPic) {
		return HEAD_PIC_URL_PREFIX+"/"+getFormatPath(headPic, "s");
	}
	/**
	 * 根据指定格式返回图像相对路径
	 * @param imagePath
	 * @param imageFormat
	 * @return
	 */
	public static String getFormatPath(String imagePath, String imageFormat) {
		String[] formats = MYJRJ_AVATAR_FORMATS.split(";");
		String formatPath = imagePath;
		for (String format : formats) {
			String[] adpaters = format.split(",");	
			if (adpaters[0].equals(imageFormat.toLowerCase())) {
				if (imagePath!=null&&!imagePath.equals("")&&imagePath.indexOf(".")>0) {
					formatPath = imagePath.substring(0, imagePath.lastIndexOf("."))+adpaters[3]+"."+imagePath.substring(imagePath.lastIndexOf(".")+1);	
				}				
			}
		}		
		return formatPath;
	}
	// private String getheadPicName(String headPic,String suffix){
	// String result=null;
	// if (headPic!=null&&(!"".equals(headPic.trim()))){
	// int dotpos=headPic.lastIndexOf(".");
	// StringBuffer sb=new StringBuffer();
	// sb.append(headPic.substring(0,
	// dotpos)).append(suffix).append(headPic.substring(dotpos));
	// result=sb.toString();
	// }
	//		
	// return result;
	// }
	@SuppressWarnings("unchecked")
	@Override
	public SelfInfoFull map(String json) {
		SelfInfoFull result = null;
		if (StringUtils.isNotBlank(json)) {

			Map<String, Object> jo = getJsonMap(json);
			if (jo != null) {
				result = new SelfInfoFull();
				result.setUserId((String) jo.get("userID"));
				result.setUserName((String) jo.get("userName"));
				result.setSpaceHost((String) jo.get("spaceHost"));
				result.setTrueName((String) jo.get("trueName"));
				result.setHeadPicS(getheadPicS((String) jo.get("headPic")));
				result.setHeadPicM(getheadPicM((String) jo.get("headPic")));
				result.setHeadPicXS(getheadPicXS((String) jo.get("headPic")));
				result.setHeadPicMS(getheadPicMS((String) jo.get("headPic")));
				result.setGender((String) jo.get("gender"));
				result.setEmail((String) jo.get("email"));
				result.setMobile((String) jo.get("mobile"));
				result.setMobileIsBind(Boolean.valueOf(String.valueOf(jo
						.get("mobileIsBind"))));
				result.setEducation((String) jo.get("education"));
				result.setOccupation((String) jo.get("occupation"));
				result.setProvince((String) jo.get("province"));
				result.setCity((String) jo.get("city"));
				result.setRegisterTime(DateUtil.parseAll((String) jo
						.get("registerTime")));
				result.setFinance((String) jo.get("finance"));
				result.setOperationType((String) jo.get("operationType"));
				result.setChooseType((String) jo.get("chooseType"));
				result.setInvestExperience((String) jo.get("investExperience"));

				result.setUid((Long) jo.get("uid"));
				result.setBirthDay((String) jo.get("birthDay"));
				result.setEmail2((String) jo.get("email2"));
				result.setUpdateTime((String) jo.get("updateTime"));
				result.setHeadPicUTime((String) jo.get("headPicUTime"));

				List l = (List) jo.get("investTypes");
				if (l == null) {
					result.setInvestTypes(new Long[] {});
				} else {
					result.setInvestTypes((Long[]) l.toArray(new Long[] {}));
				}
				result.setSummary((String) jo.get("summary"));
				result.setHeadPicIsDefault(Boolean.valueOf(String.valueOf(jo
						.get("headPicIsDefault"))));
				result.setSpaceHostIsDefault(Boolean.valueOf(String.valueOf(jo
						.get("spaceHostIsDefault"))));
			}
		} else {
			throw new FaceException("Need paramers :String json");
		}
		return result;
	}

}
