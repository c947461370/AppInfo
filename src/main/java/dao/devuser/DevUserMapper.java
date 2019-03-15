package dao.devuser;

import org.apache.ibatis.annotations.Param;

import pojo.DevUser;

public interface DevUserMapper {
	
	/**
	 * 根据devCode获取用户记录
	 * @param devCode
	 * @return
	 */
	public DevUser getLoginUser(@Param("devCode") String devCode);
}
