package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class GuangmingLaunch implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2821090147236827599L;
	
	private String version;
	private String merId;
	private String orderAmt;
	private String orderType;
	private String norifyUrl;
	private String tid;
	private String tidSeq;
	private String userId;
	private String sign;
	
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getTidSeq() {
		return tidSeq;
	}
	public void setTidSeq(String tidSeq) {
		this.tidSeq = tidSeq;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(String orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getNorifyUrl() {
		return norifyUrl;
	}
	public void setNorifyUrl(String norifyUrl) {
		this.norifyUrl = norifyUrl;
	}
}
