package com.cfo.common.enums;

import com.cfo.common.service.api.result.Result;
import com.cfo.common.vo.UserBaseVo;



public class UserLoginRegResult extends Result<UserBaseVo> {
	private String userid;
	private String custno;
	private Integer openstatus;
	private Integer failtimes;
	private Integer frozenremainseconds;
	// 身份证号
	private String idnumber;
	// 绑卡状态
	private Integer bindstatus;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCustno() {
		return custno;
	}

	public void setCustno(String custno) {
		this.custno = custno;
	}

	public Integer getOpenstatus() {
		return openstatus;
	}

	public void setOpenstatus(Integer openstatus) {
		this.openstatus = openstatus;
	}

	public Integer getFailtimes() {
		return failtimes;
	}

	public void setFailtimes(Integer failtimes) {
		this.failtimes = failtimes;
	}

	public Integer getFrozenremainseconds() {
		return frozenremainseconds;
	}

	public void setFrozenremainseconds(Integer frozenremainseconds) {
		this.frozenremainseconds = frozenremainseconds;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public int getBindstatus() {
		return bindstatus;
	}

	public void setBindstatus(int bindstatus) {
		this.bindstatus = bindstatus;
	}

	@Override
	public UserBaseVo parse() {
		return new UserBaseVo(userid, custno, openstatus==null?0:openstatus, idnumber,
				bindstatus==null?0:bindstatus,failtimes);
	}
}
