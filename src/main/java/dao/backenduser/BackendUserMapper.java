package dao.backenduser;

import org.apache.ibatis.annotations.Param;

import pojo.BackendUser;


public interface BackendUserMapper {
	/**
	 * 根据devCode获取用户记录
	 * @param devCode
	 * @return
	 */
	public BackendUser getLoginUser(@Param("userCode") String userCode);
}
