package service.developer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import dao.devuser.DevUserMapper;

import pojo.DevUser;

@Service
public class DevUserServiceImpl implements DevUserService {
	
	@Resource
	DevUserMapper mapper;

	public DevUser login(String devCode, String password) {
		DevUser user = null;
		user =mapper.getLoginUser(devCode);
		if(user!=null){
			if(!user.getDevPassword().equals(password)){
				user = null;
			}
		}
		return user;
	}

}
