package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;

public class Guangming implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 692089849115966222L;
	
	
	private String merId;//�̻���
	private String tid;//�ն˺�
	private String tidSeq;//�ն���ˮ��
	private String orderId;//������
	private String orderAmt;//�������
	private String orderTime;//����ʱ��
	private String thirdOrderId;//΢��/֧����������
	private String resCode;//00��ʾ�ɹ�  ������ʾʧ��
	private String orderStatus;//֧��״̬   7777: ʧЧ	8888:δ֧��        9999:֧��ʧ��          6666:֧����         0000��֧���ɹ� 
	private String sign;
	private String orderType;//֧������     0101 ΢��   0201 ֧����
	private String appId;
	private String resMsg;//��ʾ
	private String timeStamp;
	private String nonceStr;
	private String package_str;
	private String paySign;
	private String signType;
	private String refundStatus;//�˿� ״̬
	
	
	
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	public String getPackage_str() {
		return package_str;
	}
	public void setPackage_str(String package_str) {
		this.package_str = package_str;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		nonceStr = nonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		appId = appId;
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
