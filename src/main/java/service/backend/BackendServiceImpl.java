package service.backend;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import dao.backenduser.BackendUserMapper;

import pojo.BackendUser;

@Service
public class BackendServiceImpl implements BackendService {

	@Resource
	BackendUserMapper mapper;
	
	@Override
	public BackendUser login(String userCode, String password) {
		BackendUser user = null;
		user =mapper.getLoginUser(userCode);
		if(user!=null){
			if(!user.getUserPassword().equals(password)){
				user = null;
			}
		}
		return user;
	}

}
