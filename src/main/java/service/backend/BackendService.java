package service.backend;

import pojo.BackendUser;


public interface BackendService {
	/**
	 * 管理员登录
	 * @param devCode
	 * @param password
	 * @return
	 */
	public BackendUser login(String userCode,String password);
}
