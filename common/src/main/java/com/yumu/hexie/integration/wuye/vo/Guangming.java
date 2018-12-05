package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Guangming implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 692089849115966222L;
	
	
	private String merId;//商户号
	private String tid;//终端号
	private String tidSeq;//终端流水号
	private String orderId;//订单号
	private String orderAmt;//订单金额
	private String orderTime;//交易时间
	private String thirdOrderId;//微信/支付宝订单号
	private String resCode;//00表示成功  其他表示失败
	private String orderStatus;//支付状态   7777: 失效	  8888:未支付        9999:支付失败          6666:支付中         0000：支付成功 
	private String sign;
	private String orderType;//支付类型     0101 微信   0201 支付宝
	@JsonProperty("AppId")
	private String appId;
	private String resMsg;//提示
	@JsonProperty("TimeStamp")
	private String timeStamp;
	@JsonProperty("NonceStr")
	private String nonceStr;
	@JsonProperty("Package_str")
	private String package_str;
	@JsonProperty("PaySign")
	private String paySign;
	@JsonProperty("SignType")
	private String signType;
	private String refundStatus;//退款 状态
	
	@Transient
	public boolean isPaySuccess() {
		return "0000".equals(orderStatus);
	}
	
	@Transient
	public boolean isPaying() {
		return "6666".equals(orderStatus);
	}
	
	@Transient
	public boolean isPayFail() {
		return "9999".equals(orderStatus);
	}
	
	@Transient
	public boolean isPayInvalid() {
		return "7777".equals(orderStatus);
	}
	
	@Transient
	public boolean isPayUnpaid() {
		return "8888".equals(orderStatus);
	}
	
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String SignType) {
		this.signType = SignType;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String PaySign) {
		this.paySign = PaySign;
	}
	public String getPackage_str() {
		return package_str;
	}
	public void setPackage_str(String Package_str) {
		this.package_str = Package_str;
	}
	
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String NonceStr) {
		this.nonceStr = NonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String TimeStamp) {
		this.timeStamp = TimeStamp;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String AppId) {
		this.appId = AppId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getTidSeq() {
		return tidSeq;
	}
	public void setTidSeq(String tidSeq) {
		this.tidSeq = tidSeq;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(String orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getThirdOrderId() {
		return thirdOrderId;
	}
	public void setThirdOrderId(String thirdOrderId) {
		this.thirdOrderId = thirdOrderId;
	}
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	
	
}
