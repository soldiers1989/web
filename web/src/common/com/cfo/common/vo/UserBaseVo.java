package com.cfo.common.vo;
/**
 * 用户基础信息
 * @author yuanlong.wang
 *
 */
public class UserBaseVo extends BaseVo{
	//用户ID 也有可以能用户名
	private String userId;
	//客户代码 
	private String custno;
	//开户状态
	private Integer openstatus;
	//登录失败次数
	private Integer failtimes;
	//登录冻结剩余秒数
	private Integer frozenremainseconds;
	//身份证号
	private String idnumber;
	//绑卡状态
	private int bindstatus;
	//是否为员工
	private Integer companyuser;
	private Integer idchecked;//身份证是否经过验证 1:已验证，0:未验证
	public UserBaseVo() {
		
	}
	
	public UserBaseVo(String userid, String custno, Integer openstatus,
			Integer failtimes, Integer frozenremainseconds) {
		super();
		this.userId = userid;
		this.custno = custno;
		this.openstatus = openstatus;
		this.failtimes = failtimes;
		this.frozenremainseconds = frozenremainseconds;
	}

	public UserBaseVo(String userid, String custno, int openstatus,
			String idnumber, int bindstatus) {
		super();
		this.userId = userid;
		this.custno = custno;
		this.openstatus = openstatus;
		this.idnumber = idnumber;
		this.bindstatus = bindstatus;
	}
	public UserBaseVo(String userid, String custno, int openstatus,
			String idnumber, int bindstatus,Integer failtimes) {
		super();
		this.userId = userid;
		this.custno = custno;
		this.openstatus = openstatus;
		this.idnumber = idnumber;
		this.bindstatus = bindstatus;
		this.failtimes=failtimes;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userid) {
		this.userId = userid;
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


	public Integer getCompanyuser() {
		return companyuser;
	}


	public void setCompanyuser(Integer companyuser) {
		this.companyuser = companyuser;
	}


	public Integer getIdchecked() {
		return idchecked;
	}


	public void setIdchecked(Integer idchecked) {
		this.idchecked = idchecked;
	}
	
}
