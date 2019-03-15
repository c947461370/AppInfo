package controller.developer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pojo.DevUser;

import service.developer.DevUserService;
import tools.Constants;

@Controller
@RequestMapping(value="/dev")
public class DevUserController {
	@Resource
	private DevUserService service;
	private Logger logger = Logger.getLogger(DevUserController.class);

	@RequestMapping(value="/login")
	public String login(){
		logger.debug("LoginController welcome AppinfoSystem develper==========");
		return "devlogin";
	}
	
	@RequestMapping(value="/dologin")
	public String dologin(@RequestParam String devCode,@RequestParam String devPassword,
			HttpSession session,HttpServletRequest request){
		logger.debug("dologin==========");
		DevUser user = null;
		user = service.login(devCode, devPassword);
		if(user!=null){
			session.setAttribute(Constants.DEV_USER_SESSION, user);
			return "redirect:/dev/flatform/main";
		}else {
			request.setAttribute("error","用户或用户名不正确");
			return "devlogin";
		}
	}
	
	@RequestMapping(value="/flatform/main")
	public String main(HttpSession session){
		if(session.getAttribute(Constants.DEV_USER_SESSION)==null){
			return "redirect:/dev/login";
		}
		return "developer/main";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session){
		//清除session
		session.removeAttribute(Constants.DEV_USER_SESSION);
		return "devlogin";
	}
	
	
}
