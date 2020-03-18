package com.yumu.hexie.service.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.user.User;


/**
 * 用户服务
 */
public interface UserService {

    public User getById(long uId);
	//获取用户信息
	public UserWeiXin getOrSubscibeUserByCode(String code);
	public String getBindOrSubscibeUserOpenIdByCode(String code);
    public UserWeiXin getOrSubscibeUserByOpenId(String openid);
	
	//从profile页面进行修改用户信息
	public User saveProfile(long userId,String nickName,int sex);
    public User save(User user);
    public User bindPhone(User user,String phone);
	public User queryByShareCode(String code);
	User multiFindByOpenId(String openId);
	User updateUserLoginInfo(UserWeiXin weixinUser);
	void bindWuYeId(User user);
	List<User> getByOpenId(String openId);
	boolean checkDuplicateLogin(HttpSession httpSession);
	
}
