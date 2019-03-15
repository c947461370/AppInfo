package service.developer;

import pojo.DevUser;

public interface DevUserService {
	/**
	 * 用户登录
	 * @param devCode
	 * @param password
	 * @return
	 */
	public DevUser login(String devCode,String password);
}
