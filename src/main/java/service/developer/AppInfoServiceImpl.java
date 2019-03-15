package service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import dao.appinfo.AppInfoMapper;

import pojo.AppInfo;

@Service
public class AppInfoServiceImpl implements AppInfoService{

	@Resource
	AppInfoMapper mapper;
	
	@Override
	public boolean add(AppInfo appInfo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.add(appInfo);
	}

	@Override
	public boolean modify(AppInfo appInfo) throws Exception {
		// TODO Auto-generated method stub
		if(mapper.modify(appInfo)>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean deleteAppInfoById(Integer delId) throws Exception {
		// TODO Auto-generated method stub
		if(mapper.deleteAppInfoById(delId)>0){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public List<AppInfo> getAppInfoList(String querySoftwareName,
			Integer queryStatus, Integer queryCategoryLevel1,
			Integer queryCategoryLevel2, Integer queryCategoryLevel3,
			Integer queryFlatformId, Integer devId, Integer currentPageNo,
			Integer pageSize) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, (currentPageNo-1)*pageSize, pageSize);
	}

	@Override
	public int getAppInfoCount(String querySoftwareName, Integer queryStatus,
			Integer queryCategoryLevel1, Integer queryCategoryLevel2,
			Integer queryCategoryLevel3, Integer queryFlatformId, Integer devId)
			throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
	}

	@Override
	public AppInfo getAppInfo(Integer id, String APKName) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfo(id, APKName);
	}

	@Override
	public boolean deleteAppLogo(Integer id) throws Exception {
		// TODO Auto-generated method stub
		if(mapper.deleteAppLogo(id)>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean appsysdeleteAppById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int updateSatus(Integer status, Integer id) throws Exception {
		// TODO Auto-generated method stub
		return mapper.updateSatus(status, id);
	}

	@Override
	public List<AppInfo> getAppInfoList1(String querySoftwareName,
			Integer queryStatus, Integer queryCategoryLevel1,
			Integer queryCategoryLevel2, Integer queryCategoryLevel3,
			Integer queryFlatformId, Integer devId, Integer currentPageNo,
			Integer pageSize) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfoList1(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
	}

}
