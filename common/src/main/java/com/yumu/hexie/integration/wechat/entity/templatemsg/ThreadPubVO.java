package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadPubVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775901918964205055L;
	private static final String CORLOR = "#173177";
	
	@JsonProperty("first")
	private TemplateItem title;
	@JsonProperty("keyword1")
	private TemplateItem threadId;
	@JsonProperty("keyword2")
	private TemplateItem userName;
	@JsonProperty("keyword3")
	private TemplateItem userTel;
	@JsonProperty("keyword4")
	private TemplateItem userAddress;
	
	private TemplateItem remark;

	public TemplateItem getTitle() {
		return title;
	}

	public void setTitle(TemplateItem title) {
		title.setColor(CORLOR);
		this.title = title;
	}

	public TemplateItem getThreadId() {
		return threadId;
	}

	public void setThreadId(TemplateItem threadId) {
		threadId.setColor(CORLOR);
		this.threadId = threadId;
	}

	public TemplateItem getUserName() {
		return userName;
	}

	public void setUserName(TemplateItem userName) {
		userName.setColor(CORLOR);
		this.userName = userName;
	}

	public TemplateItem getUserTel() {
		return userTel;
	}

	public void setUserTel(TemplateItem userTel) {
		userTel.setColor(CORLOR);
		this.userTel = userTel;
	}

	public TemplateItem getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(TemplateItem userAddress) {
		userAddress.setColor(CORLOR);
		this.userAddress = userAddress;
	}

	public TemplateItem getRemark() {
		return remark;
	}

	public void setRemark(TemplateItem remark) {
		remark.setColor(CORLOR);
		this.remark = remark;
	}
	
	
	
}
