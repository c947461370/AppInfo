package controller.developer;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import dao.datadictionary.DataDictionaryMapper;

import pojo.AppCategory;
import pojo.AppInfo;
import pojo.AppVersion;
import pojo.DataDictionary;
import pojo.DevUser;

import service.developer.AppCategoryService;
import service.developer.AppInfoService;
import service.developer.AppVersionService;
import service.developer.DataDictionaryService;
import tools.Constants;
import tools.ExcelUtil;
import tools.PageSupport;

@Controller
@RequestMapping(value = "/dev/flatform/app")
public class AppInfoController {
	@Resource
	AppInfoService appInfoService;
	@Resource
	DataDictionaryService dataDictionaryService;
	@Resource
	AppCategoryService appCategoryService;
	@Resource
	AppVersionService appVersionService;

	private Logger logger = Logger.getLogger(AppInfoController.class);

	/**
	 * 查询列表（分页查询，条件查询）
	 * @param model
	 * @param session
	 * @param querySoftwareName
	 * @param _queryStatus
	 * @param _queryCategoryLevel1
	 * @param _queryCategoryLevel2
	 * @param _queryCategoryLevel3
	 * @param _queryFlatformId
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String getAppInfoList(
			Model model,
			HttpSession session,
			@RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
			@RequestParam(value = "queryStatus", required = false) String _queryStatus,
			@RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
			@RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
			@RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
			@RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {

		Integer devId = ((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId();
		List<AppInfo> appInfoList = null;
		List<DataDictionary> statusList = null;
		List<DataDictionary> flatFormList = null;
		List<AppCategory> categoryLevel1List = null;// 列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
		List<AppCategory> categoryLevel2List = null;
		List<AppCategory> categoryLevel3List = null;
		// 页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		Integer currentPageNo = 1;

		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		Integer queryStatus = null;
		if (_queryStatus != null && !_queryStatus.equals("")) {
			queryStatus = Integer.parseInt(_queryStatus);
		}
		Integer queryCategoryLevel1 = null;
		if (_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")) {
			queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
		}
		Integer queryCategoryLevel2 = null;
		if (_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")) {
			queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
		}
		Integer queryCategoryLevel3 = null;
		if (_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")) {
			queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
		}
		Integer queryFlatformId = null;
		if (_queryFlatformId != null && !_queryFlatformId.equals("")) {
			queryFlatformId = Integer.parseInt(_queryFlatformId);
		}

		// 总数量（表）
		int totalCount = 0;
		try {
			totalCount = appInfoService.getAppInfoCount(querySoftwareName,
					queryStatus, queryCategoryLevel1, queryCategoryLevel2,
					queryCategoryLevel3, queryFlatformId, devId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount = pages.getTotalPageCount();
		// 控制首页和尾页
		if (currentPageNo < 1) {
			currentPageNo = 1;
		} else if (currentPageNo > totalPageCount) {
			currentPageNo = totalPageCount;
		}
		try {
			appInfoList = appInfoService.getAppInfoList(querySoftwareName,
					queryStatus, queryCategoryLevel1, queryCategoryLevel2,
					queryCategoryLevel3, queryFlatformId, devId, currentPageNo,
					pageSize);
			statusList = this.getDataDictionaryList("APP_STATUS");
			flatFormList = this.getDataDictionaryList("APP_FLATFORM");
			categoryLevel1List = appCategoryService
					.getAppCategoryListByParentId(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appInfoList", appInfoList);
		model.addAttribute("statusList", statusList);
		model.addAttribute("flatFormList", flatFormList);
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("pages", pages);
		model.addAttribute("queryStatus", queryStatus);
		model.addAttribute("querySoftwareName", querySoftwareName);
		model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
		model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
		model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
		model.addAttribute("queryFlatformId", queryFlatformId);

		// 二级分类列表和三级分类列表---回显
		// if (queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
		// categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
		// model.addAttribute("categoryLevel2List", categoryLevel2List);
		// }
		// if (queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")) {
		// categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
		// model.addAttribute("categoryLevel3List", categoryLevel3List);
		// }
		return "developer/appinfolist";
	}

	// public List<DataDictionary> getDataDictionaryList(String typeCode){
	public List<DataDictionary> getDataDictionaryList(String typeCode) {
		List<DataDictionary> dataDictionaryList = null;
		try {
			dataDictionaryList = dataDictionaryService
					.getDataDictionaryList(typeCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataDictionaryList;
	}

	/**
	 * 根据typeCode查询出相应的数据字典列表
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/datadictionarylist.json", method = RequestMethod.GET)
	@ResponseBody
	public List<DataDictionary> getDataDicList(@RequestParam String tcode) {
		return this.getDataDictionaryList(tcode);
	}

	/***
	 * 根据parentID查询出相应的分类级别的列表
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/categorylevellist.json", method = RequestMethod.GET, produces = { "application/json;charset=utf-8" })
	@ResponseBody
	public String getAppCategoryList(@RequestParam String pid) {
		if (pid.equals(""))
			pid = null;
		return getCategoryList(pid);
	}

	public String getCategoryList(String pid) {
		List<AppCategory> categoryLevelList = null;
		try {
			categoryLevelList = appCategoryService
					.getAppCategoryListByParentId(pid == null ? null : Integer
							.parseInt(pid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSON.toJSONString(categoryLevelList);
	}

	@RequestMapping(value = "apkexist.json", method = RequestMethod.GET, produces = { "application/json;charset=utf-8" })
	@ResponseBody
	public Object apkNameIsExist(@RequestParam String APKName) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isNullOrEmpty(APKName)) {
			resultMap.put("APKName", "empty");
		} else {
			AppInfo appInfo = null;
			try {
				appInfo = appInfoService.getAppInfo(null, APKName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (appInfo != null) {
				resultMap.put("APKName", "exist");
			} else {
				resultMap.put("APKName", "noexist");
			}
		}
		return JSONArray.toJSONString(resultMap);
	}

	/**
	 * 增加app信息（跳转到新增appinfo页面）
	 * 
	 * @param appInfo
	 * @return
	 */
	@RequestMapping(value = "/appinfoadd", method = RequestMethod.GET)
	public String add(@ModelAttribute("appInfo") AppInfo appInfo) {
		return "developer/appinfoadd";
	}

	/**
	 * 保存新增appInfo（主表）的数据
	 * 
	 * @param appInfo
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/appinfoaddsave", method = RequestMethod.POST)
	public String addSave(
			AppInfo appInfo,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "a_logoPicPath", required = false) MultipartFile attach) {

		String logoPicPath = null;
		String logoLocPath = null;
		if (!attach.isEmpty()) {
			String path = request
					.getSession()
					.getServletContext()
					.getRealPath(
							"statics" + java.io.File.separator + "uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();// 原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 原文件后缀
			int filesize = 500000;
			if (attach.getSize() > filesize) {// 上传大小不得超过 50k
				request.setAttribute("fileUploadError",
						Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
			} else if (prefix.equalsIgnoreCase("jpg")
					|| prefix.equalsIgnoreCase("png")
					|| prefix.equalsIgnoreCase("jpeg")
					|| prefix.equalsIgnoreCase("pneg")) {// 上传图片格式
				String fileName = appInfo.getAPKName() + ".jpg";// 上传LOGO图片命名:apk名称.apk
				File targetFile = new File(path, fileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError",
							Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				}
				logoPicPath = request.getContextPath()
						+ "/statics/uploadfiles/" + fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				request.setAttribute("fileUploadError",
						Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		appInfo.setCreatedBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setCreationDate(new Date());
		appInfo.setLogoPicPath(logoPicPath);
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setDevId(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setStatus(1);
		try {
			if (appInfoService.add(appInfo)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appinfoadd";
	}

	@RequestMapping(value = "/appinfomodify", method = RequestMethod.GET)
	public String modifyAppInfo(
			@RequestParam("id") String id,
			@RequestParam(value = "error", required = false) String fileUploadError,
			Model model) {
		AppInfo appInfo = null;
		logger.debug("modifyAppInfo --------- id: " + id);
		if (null != fileUploadError && fileUploadError.equals("error1")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		} else if (null != fileUploadError && fileUploadError.equals("error2")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		} else if (null != fileUploadError && fileUploadError.equals("error3")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		} else if (null != fileUploadError && fileUploadError.equals("error4")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_4;
		}
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute(appInfo);
		model.addAttribute("fileUploadError", fileUploadError);
		return "developer/appinfomodify";
	}

	
	
	/**
	 * 修改
	 * @param appInfo
	 * @param session
	 * @param request
	 * @param attach
	 * @return
	 */
	@RequestMapping(value = "/appinfomodifysave")
	public String modifySave(
			AppInfo appInfo,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "attach", required = false) MultipartFile attach) {
		String logoPicPath = null;
		String logoLocPath = null;
		String APKName = appInfo.getAPKName();
		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext()
					.getRealPath("statics" + File.separator + "uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();// 原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 原文件后缀
			int filesize = 500000;
			if (attach.getSize() > filesize) {// 上传大小不得超过 50k
				return "redirect:/dev/flatform/app/appinfomodify?id="
						+ appInfo.getId() + "&error=error4";
			} else if (prefix.equalsIgnoreCase("jpg")
					|| prefix.equalsIgnoreCase("png")
					|| prefix.equalsIgnoreCase("jpeg")
					|| prefix.equalsIgnoreCase("pneg")) {// 上传图片格式
				String fileName = APKName + ".jpg";// 上传LOGO图片命名:apk名称.apk
				File targetFile = new File(path, fileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appinfomodify?id="
							+ appInfo.getId() + "&error=error2";
				}
				logoPicPath = request.getContextPath()
						+ "/statics/uploadfiles/" + fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				return "redirect:/dev/flatform/app/appinfomodify?id="
						+ appInfo.getId() + "&error=error3";
			}
		}
		appInfo.setModifyBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setModifyDate(new Date());
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setLogoPicPath(logoPicPath);
		try {
			if (appInfoService.modify(appInfo)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appinfomodify";
	}

	/**
	 * 删除图片
	 * */
	
	@RequestMapping(value = "/delfile.json",method=RequestMethod.GET)
	@ResponseBody
	public Object delfile(
			@RequestParam(value = "id", required = false) String id) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String fileLocPath = null;
		if (id == null || id.equals("")) {
			resultMap.put("result", "failed");
		} else {
			try {
				fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id),
						null)).getLogoLocPath();
				File file = new File(fileLocPath);
				if (file.exists()) {
					if (file.delete()) {
						if (appInfoService.deleteAppLogo(Integer.parseInt(id))) {
							resultMap.put("result", "success");
						}
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JSONArray.toJSON(resultMap);
	}
	
	/**
	 * 增加appversion信息（跳转到新增app版本页面）
	 * @param appInfo
	 * @return
	 */
	@RequestMapping(value="/appversionadd",method=RequestMethod.GET)
	public String addVersion(@RequestParam(value="id")String appId,
							 @RequestParam(value="error",required= false)String fileUploadError,
							 AppVersion appVersion,Model model){
		logger.debug("fileUploadError============> " + fileUploadError);
		if(null != fileUploadError && fileUploadError.equals("error1")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		}else if(null != fileUploadError && fileUploadError.equals("error2")){
			fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
		}else if(null != fileUploadError && fileUploadError.equals("error3")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		appVersion.setAppId(Integer.parseInt(appId));
		List<AppVersion> appVersionList = null;
		try {
			appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
			appVersion.setAppName((appInfoService.getAppInfo(Integer.parseInt(appId),null)).getSoftwareName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appVersionList", appVersionList);
		model.addAttribute(appVersion);
		model.addAttribute("fileUploadError",fileUploadError);
		return "developer/appversionadd";
	}
	
	/**
	 * 保存新增appversion数据（子表）-上传该版本的apk包
	 * @param appInfo
	 * @param appVersion
	 * @param session
	 * @param request
	 * @param attach
	 * @return
	 */
	@RequestMapping(value="/addversionsave",method=RequestMethod.POST)
	public String addVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,
						@RequestParam(value="a_downloadLink",required= false) MultipartFile attach ){		
		String downloadLink =  null;
		String apkLocPath = null;
		String apkFileName = null;
		if(attach!=null){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
				 String apkName = null;
				 try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
				 } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
				 if(apkName == null || "".equals(apkName)){
					 return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
							 +"&error=error1";
				 }
				 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
				 File targetFile = new File(path,apkFileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
							 +"&error=error2";
				} 
				downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
				apkLocPath = path+File.separator+apkFileName;
			}else{
				return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
						 +"&error=error3";
			}
		}
		appVersion.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setCreationDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if(appVersionService.appsysadd(appVersion)){
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId();
	}
	
	
	/**
	 * 查看详情
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appview/{id}")
	public String appview(@PathVariable String id,Model model){
		AppInfo appInfo =null;
		List<AppVersion> appVersions=null;
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
			appVersions = appVersionService.getAppVersionList(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appVersionList",appVersions);
		model.addAttribute(appInfo);
		return "developer/appinfoview";
	}
	
	/**
	 * 删除app信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delapp.json",method=RequestMethod.GET)
	@ResponseBody
	public String delapp(@RequestParam String id){
		HashMap<String,String> hashMap = new HashMap<String,String>();
		try {
			int deleteVersionByAppId = appVersionService.deleteVersionByAppId(Integer.parseInt(id));
			AppVersion appVersionById = appVersionService.getAppVersionById(Integer.parseInt(id));
			AppInfo appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
			if(appVersionById!=null){
				File file = new File(appVersionById.getApkLocPath());
				boolean delete = file.delete();
			}
			if(appInfo!=null){
				File file1 = new File(appInfo.getLogoLocPath());
				boolean delete2 = file1.delete();
			}
			if(deleteVersionByAppId>0){
				boolean deleteAppInfoById = appInfoService.deleteAppInfoById(Integer.parseInt(id));
				if(deleteAppInfoById){
					hashMap.put("delResult", "true");
				}else{
					hashMap.put("delResult", "false");
				}
			}else{
				hashMap.put("delResult", "false");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSON.toJSONString(hashMap);
	}
	
	@RequestMapping(value="/appversionmodify")
	public String appversionmodify(@RequestParam String vid,@RequestParam String aid,Model model){
		List<AppVersion> appVersionList =null;
		AppVersion appVersionById = null;
		try {
			appVersionById = appVersionService.getAppVersionById(Integer.parseInt(vid));
			appVersionList = appVersionService.getAppVersionList(Integer.parseInt(aid));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appVersionList",appVersionList);
		model.addAttribute(appVersionById);
		return "developer/appversionmodify";
	}
	
	@RequestMapping(value="/appversionmodifysave")
	public String appversionmodifysave(AppVersion appVersion,
			@RequestParam(value="attach",required= false) MultipartFile attach,
			HttpSession session,HttpServletRequest request){
		String downloadLink =  null;
		String apkLocPath = null;
		String apkFileName = null;
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
				 String apkName = null;
				 try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
				 } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				 }
				 if(apkName == null || "".equals(apkName)){
					 return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
							 +"&aid="+appVersion.getAppId()
							 +"&error=error1";
				 }
				 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
				 File targetFile = new File(path,apkFileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
							 +"&aid="+appVersion.getAppId()
							 +"&error=error2";
				} 
				downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
				apkLocPath = path+File.separator+apkFileName;
			}else{
				return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
						 +"&aid="+appVersion.getAppId()
						 +"&error=error3";
			}
		}
		appVersion.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setModifyDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if(appVersionService.modify(appVersion)){
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "developer/appversionmodify";
	}

	@RequestMapping(value="{appId}/sale.json",method=RequestMethod.PUT)
	@ResponseBody
	public String sale(@PathVariable String appId,HttpSession session){
		HashMap<String, String> resultMap = new HashMap<String,String>();
		AppInfo aInfo = new AppInfo();
		AppInfo appInfo = null;
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(appId), null);
			if(appInfo != null){
				resultMap.put("errorCode", "0");
				if(appInfo.getStatus() == 4){
					int num = appInfoService.updateSatus(5, Integer.parseInt(appId));
					if(num > 0){
						resultMap.put("resultMsg", "success");
					}else{
						resultMap.put("resultMsg", "failed");
					}
				}else if(appInfo.getStatus() == 5){
					int num = appInfoService.updateSatus(4, Integer.parseInt(appId));
					if(num > 0){
						resultMap.put("resultMsg", "success");
					}else{
						resultMap.put("resultMsg", "failed");
					}
				}else if(appInfo.getStatus() == 2){
					int num = appInfoService.updateSatus(4, Integer.parseInt(appId));
					if(num > 0){
						resultMap.put("resultMsg", "success");
					}else{
						resultMap.put("resultMsg", "failed");
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return JSON.toJSONString(resultMap);
}
	
	// 导出excel表
		@RequestMapping(value = "/export/{currentPageNo}")
		@ResponseBody
		public void export(HttpServletRequest request, HttpServletResponse response,@PathVariable String currentPageNo) {
			// 获取数据
			DevUser user = (DevUser)request.getSession().getAttribute(Constants.DEV_USER_SESSION);
			List<AppInfo> list=null;
			try {
				list = appInfoService.getAppInfoList(null, null, null,
						null, null, null, user.getId(), Integer.parseInt(currentPageNo), Constants.pageSize);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// excel标题
			String[] title = { "软件ID", "软件名称", "APk名称", "软件大小", "所属平台", "所属分类",
					"状态", "下载次数", "最新版本" };

			// excel文件名
			String fileName = "App信息表" + System.currentTimeMillis() + ".xls";

			// sheet名
			String sheetName = "App信息表";
			String[][] content = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				content[i] = new String[title.length];
				AppInfo obj = list.get(i);
				content[i][0] = obj.getId().toString();
				content[i][1] = obj.getSoftwareName();
				content[i][2] = obj.getAPKName();
				content[i][3] = obj.getSoftwareSize().toString();
				content[i][4] = obj.getFlatformName();
				content[i][5] = obj.getCategoryLevel1Name() + "-"
						+ obj.getCategoryLevel2Name() + "-"
						+ obj.getCategoryLevel3Name();
				content[i][6] = obj.getStatusName();
				content[i][7] = obj.getDownloads().toString();
				content[i][8] = obj.getVersionNo();
			}
			// 创建HSSFWorkbook
			HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content,
					null);
			// 响应到客户端
			try {

				this.setResponseHeader(response, fileName);
				OutputStream os = response.getOutputStream();
				wb.write(os);

				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 发送响应流方法
		public void setResponseHeader(HttpServletResponse response, String fileName) {
			try {
				try {
					fileName = new String(fileName.getBytes(), "ISO8859-1");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				response.setContentType("application/octet-stream;charset=ISO8859-1");
				response.setHeader("Content-Disposition", "attachment;filename="
						+ fileName);
				response.addHeader("Pargam", "no-cache");
				response.addHeader("Cache-Control", "no-cache");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
}
