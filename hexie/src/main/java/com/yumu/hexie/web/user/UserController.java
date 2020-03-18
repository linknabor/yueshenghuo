package com.yumu.hexie.web.user;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.model.localservice.HomeServiceConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.o2o.OperatorService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.req.MobileYzm;
import com.yumu.hexie.web.user.req.SimpleRegisterReq;
import com.yumu.hexie.web.user.resp.UserInfo;

@Controller(value = "userController")
public class UserController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	private UserService userService;
	@Inject
	private SmsService smsService;
	@Autowired
    private OperatorService operatorService;
    
    

    @Value(value = "${testMode}")
    private Boolean testMode;
	
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<UserInfo> userInfo(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session,@ModelAttribute(Constants.USER)User user) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User sessionUser = user;
		try {
			log.info("user in session :" + sessionUser);
			List<User> userList = userService.getByOpenId(user.getOpenid());
			boolean needClearSession = true;
			if (userList!=null) {
				for (User baseduser : userList) {
					if (baseduser.getId() == user.getId()) {
						user = baseduser;
						needClearSession = false;
						break;
					}
				}
			}
			if(!needClearSession){
			    session.setAttribute(Constants.USER, user);
			    UserInfo userInfo = new UserInfo(user,operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,user.getId()));
			    long endTime = System.currentTimeMillis();
				log.info("user:" + user.getName() + "登陆，耗时：" + ((endTime-beginTime)/1000));
			    return new BaseResult<UserInfo>().success(userInfo);
			} else {
				log.error("current user id in session is not the same with the id in database. user : " + sessionUser + ", sessionId: " + session.getId());
				session.setMaxInactiveInterval(1);//将会话过期
				Thread.sleep(50);	//延时，因为上面设置了1秒。页面上也设置了延时，所以这里不需要1秒
				return new BaseResult<UserInfo>().success(null);
			}
		} catch (Exception e) {
			
			if (e instanceof BizValidateException) {
				throw (BizValidateException)e;
			}else {
				throw new Exception(e);
			}
		}
    
    }

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<UserInfo> profile(HttpSession session,@ModelAttribute(Constants.USER)User user,@RequestParam String nickName,@RequestParam int sex) throws Exception {
		user = userService.saveProfile(user.getId(), nickName, sex);
		if(user != null){
			session.setAttribute(Constants.USER, user);
	        return new BaseResult<UserInfo>().success(new UserInfo(user,
	            operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,user.getId())));
		} else {
            return new BaseResult<UserInfo>().failMsg("用户不存在！");
        }
    }
	
	@RequestMapping(value = "/login/{code}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<UserInfo> login(HttpSession session,@PathVariable String code) throws Exception {
		
		long beginTime = System.currentTimeMillis();
		User userAccount = null;
		if (StringUtil.isNotEmpty(code)) {
		    if(Boolean.TRUE.equals(testMode)) {
		        try{
			        Long id = Long.valueOf(code);
			    	userAccount = userService.getById(id);
		        }catch(Throwable t){}
		    }
		    if(userAccount == null) {
		    	
		    	if (userService.checkDuplicateLogin(session)) {
					throw new BizValidateException("正在登陆中，请耐心等待...如3秒内无响应，请刷新页面。");
				}
		    	
		    	UserWeiXin weixinUser = userService.getOrSubscibeUserByCode(code);
		    	userAccount = userService.updateUserLoginInfo(weixinUser);
		    }
		    
			session.setAttribute(Constants.USER, userAccount);
		}
		if(userAccount == null) {
		    return new BaseResult<UserInfo>().failMsg("用户不存在！");
		}
		long endTime = System.currentTimeMillis();
		log.info("user:" + userAccount.getName() + "login，耗时：" + ((endTime-beginTime)/1000));
		return new BaseResult<UserInfo>().success(new UserInfo(userAccount,
		    operatorService.isOperator(HomeServiceConstant.SERVICE_TYPE_REPAIR,userAccount.getId())));
    }
	
	@RequestMapping(value = "/getyzm", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> getYzm(@RequestBody MobileYzm yzm, @ModelAttribute(Constants.USER)User user) throws Exception {
		boolean result = smsService.sendVerificationCode(user.getId(), yzm.getMobile());
		if(result) {
		    return new BaseResult<String>().failMsg("发送验证码失败");
		}
	    return  new BaseResult<String>().success("验证码发送成功");
    }

	@RequestMapping(value = "/savePersonInfo/{captcha}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<UserInfo> savePersonInfo(HttpSession session,@RequestBody User editUser,@ModelAttribute(Constants.USER)User user,
			@PathVariable String captcha) throws Exception {
		if(StringUtil.equals(editUser.getTel(),user.getTel())) {
			user.setSex(editUser.getSex());
			user.setRealName(editUser.getRealName());
			user.setName(editUser.getName());
			session.setAttribute(Constants.USER, userService.save(user));

	        return new BaseResult<UserInfo>().success(new UserInfo(user));
		} else {
			if(!smsService.checkVerificationCode(editUser.getTel(),captcha)){
				return new BaseResult<UserInfo>().failMsg("短信校验失败！");
			} else {
				user.setTel(editUser.getTel());
				user.setSex(editUser.getSex());
				user.setRealName(editUser.getRealName());
				user.setName(editUser.getName());
				session.setAttribute(Constants.USER, userService.save(user));
	            return new BaseResult<UserInfo>().success(new UserInfo(user));
			}
		}
	}

    @RequestMapping(value = "/simpleRegister", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<UserInfo> simpleRegister(HttpSession session,@ModelAttribute(Constants.USER)User user,@RequestBody SimpleRegisterReq req) throws Exception {
        if(StringUtil.isEmpty(req.getMobile()) || StringUtil.isEmpty(req.getYzm())){
            return new BaseResult<UserInfo>().failMsg("信息请填写完整！");
        }
        boolean result = smsService.checkVerificationCode(req.getMobile(), req.getYzm());
        if(!result){
            return new BaseResult<UserInfo>().failMsg("校验失败！");
        } else {
            if(StringUtil.isNotEmpty(req.getName())) {
                user.setName(req.getName());
                user.setTel(req.getMobile());
            }
            
            user.setRegisterDate(System.currentTimeMillis());
            session.setAttribute(Constants.USER, userService.save(user));
            return new BaseResult<UserInfo>().success(new UserInfo(user));
        }
    }
    
//    /**
//     * 绑定主公众号的openid
//     * @param user
//     * @param code
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/bindWechat/{code}", method = RequestMethod.POST)
//    @ResponseBody
//    public BaseResult<String> bindMain(@ModelAttribute(Constants.USER)User user, @PathVariable String code) throws Exception {
//    	
//    	User currUser = userService.getById(user.getId());
//    	if (currUser == null) {
//    		return new BaseResult<String>().failMsg("user does not exist !");
//		}
//    	if (StringUtil.isEmpty(currUser.getBindOpenId())) {
//    		String openId = "";
//        	if (StringUtil.isNotEmpty(code)) {
//        		try {
//    				openId = userService.getBindOrSubscibeUserOpenIdByCode(code);
//    				currUser.setBindOpenId(openId);
//    	        	currUser.setBindAppId(ConstantWeChat.BIND_APPID);
//    	        	userService.save(currUser);
//    			} catch (Exception e) {
//    				throw new BizValidateException("get bind openid failed ! ");
//    			}
//        	}
//        	
//		}
//    	
//    	return new BaseResult<String>().success("bind succeeded!");
//    	
//    }
    
}
