package service.developer;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import dao.appcategory.AppCategoryMapper;

import pojo.AppCategory;
@Service
public class AppCategoryServiceImpl implements AppCategoryService {

	@Resource
	private AppCategoryMapper mapper;
	
	@Override
	public List<AppCategory> getAppCategoryListByParentId(Integer parentId)
			throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppCategoryListByParentId(parentId);
	}

}
