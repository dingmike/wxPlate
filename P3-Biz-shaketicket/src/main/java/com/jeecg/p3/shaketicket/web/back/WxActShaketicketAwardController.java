package com.jeecg.p3.shaketicket.web.back;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.jeecgframework.p3.core.util.SystemTools;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketAward;
import com.jeecg.p3.shaketicket.service.WxActShaketicketAwardService;
import com.jeecg.p3.shaketicket.util.ContextHolderUtils;

import org.jeecgframework.p3.core.web.BaseController;

 /**
 * 描述：</b>WxActShaketicketAwardController<br>奖项表
 * @author pituo
 * @since：2015年12月24日 11时08分30秒 星期四 
 * @version:1.0
 */
@Controller
@RequestMapping("/shaketicket/back/wxActShaketicketAward")
public class WxActShaketicketAwardController extends BaseController{
  @Autowired
  private WxActShaketicketAwardService wxActShaketicketAwardService;
  
/**
  * 列表页面
  * @return
  */
@RequestMapping(value="list",method = {RequestMethod.GET,RequestMethod.POST})
public void list(@ModelAttribute WxActShaketicketAward query,HttpServletResponse response,HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
			@RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(required = true, value = "showReturnFlag" ) String showReturnFlag) throws Exception{
	 	PageQuery<WxActShaketicketAward> pageQuery = new PageQuery<WxActShaketicketAward>();
	 	pageQuery.setPageNo(pageNo);
	 	pageQuery.setPageSize(pageSize);
	 	VelocityContext velocityContext = new VelocityContext();
	 	String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
	 	query.setJwid(jwid);
	 	String defaultJwid = WeiXinHttpUtil.getLocalValue("shaketicket", "defaultJwid");
	 	if(defaultJwid.equals(jwid)){
	 		String createBy = request.getSession().getAttribute("system_userid").toString();
	 		query.setCreateBy(createBy);
	 	}
		pageQuery.setQuery(query);
		velocityContext.put("jwid",jwid);
		velocityContext.put("wxActShaketicketAward",query);
		//update-begin--Author:zhangweijian  Date: 20180319 for：增加一个返回按钮是否显示的字段
		velocityContext.put("showReturnFlag",showReturnFlag);
		//update-end--Author:zhangweijian  Date: 20180319 for：增加一个返回按钮是否显示的字段
		velocityContext.put("pageInfos",SystemTools.convertPaginatedList(wxActShaketicketAwardService.queryPageList(pageQuery)));
		String viewName = "shaketicket/back/wxActShaketicketAward-list.vm";
		ViewVelocity.view(request,response,viewName,velocityContext);
}

 /**
  * 详情
  * @return
  */
@RequestMapping(value="toDetail",method = RequestMethod.GET)
public void wxActShaketicketAwardDetail(@RequestParam(required = true, value = "id" ) String id,HttpServletResponse response,HttpServletRequest request)throws Exception{
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "shaketicket/back/wxActShaketicketAward-detail.vm";
		WxActShaketicketAward wxActShaketicketAward = wxActShaketicketAwardService.queryById(id);
		velocityContext.put("wxActShaketicketAward",wxActShaketicketAward);
		 String jwid =  request.getSession().getAttribute("jwid").toString();
		 velocityContext.put("jwid",jwid);
		ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 跳转到添加页面
 * @return
 */
@RequestMapping(value = "/toAdd",method ={RequestMethod.GET, RequestMethod.POST})
public void toAddDialog(HttpServletRequest request,HttpServletResponse response,ModelMap model)throws Exception{
	 VelocityContext velocityContext = new VelocityContext();
	 String viewName = "shaketicket/back/wxActShaketicketAward-add.vm";
	 String jwid =  request.getSession().getAttribute("jwid").toString();
	 velocityContext.put("jwid",jwid);
	 ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 保存信息
 * @return
 */
@RequestMapping(value = "/doAdd",method ={RequestMethod.GET, RequestMethod.POST})
@ResponseBody
public AjaxJson doAdd(@ModelAttribute WxActShaketicketAward wxActShaketicketAward){
	AjaxJson j = new AjaxJson();
	try {
		//update-begin--Author:zhangweijian  Date: 20180329 for：如果没有上传图片，则给一张默认图片
		if(StringUtils.isEmpty(wxActShaketicketAward.getImg())){
			wxActShaketicketAward.setImg("content/shaketicket/default/img/defaultGoods.png");
		}
		//update-end--Author:zhangweijian  Date: 20180329 for：如果没有上传图片，则给一张默认图片
		wxActShaketicketAwardService.doAdd(wxActShaketicketAward);
		j.setMsg("保存成功");
	} catch (Exception e) {
		e.printStackTrace();
		j.setSuccess(false);
		j.setMsg("保存失败");
	}
	return j;
}

/**
 * 跳转到编辑页面
 * @return
 */
@RequestMapping(value="toEdit",method = RequestMethod.GET)
public void toEdit(@RequestParam(required = true, value = "id" ) String id,HttpServletResponse response,HttpServletRequest request) throws Exception{
	 VelocityContext velocityContext = new VelocityContext();
	 WxActShaketicketAward wxActShaketicketAward = wxActShaketicketAwardService.queryById(id);
	 velocityContext.put("wxActShaketicketAward",wxActShaketicketAward);
	 String viewName = "shaketicket/back/wxActShaketicketAward-edit.vm";
	 String jwid =  request.getSession().getAttribute("jwid").toString();
	 velocityContext.put("jwid",jwid);
	 ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 编辑
 * @return
 */
@RequestMapping(value = "/doEdit",method ={RequestMethod.GET, RequestMethod.POST})
@ResponseBody
public AjaxJson doEdit(@ModelAttribute WxActShaketicketAward wxActShaketicketAward){
	AjaxJson j = new AjaxJson();
	try {
		wxActShaketicketAwardService.doEdit(wxActShaketicketAward);
		j.setMsg("编辑成功");
	} catch (Exception e) {
		e.printStackTrace();
		j.setSuccess(false);
		j.setMsg("编辑失败");
	}
	return j;
}


/**
 * 删除
 * @return
 */
@RequestMapping(value="doDelete",method = RequestMethod.GET)
@ResponseBody
public AjaxJson doDelete(@RequestParam(required = true, value = "id" ) String id){
		AjaxJson j = new AjaxJson();
		try {
			//update-begin--Author:zhangweijian  Date: 20180329 for：//判断奖项是否被使用
			//判断奖项是否被使用
			Boolean used=wxActShaketicketAwardService.validUsed(id);
			if(used){
				j.setSuccess(false);
				j.setMsg("该奖项已经被活动使用，不能删除");
			}else{
				wxActShaketicketAwardService.doDelete(id);
				j.setMsg("删除成功");
			}
			//update-end--Author:zhangweijian  Date: 20180329 for：//判断奖项是否被使用
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("删除失败");
		}
		return j;
}

/**
 * 上传照片
 * @return
 */
@RequestMapping(value = "/doUpload",method ={RequestMethod.POST})
@ResponseBody
public AjaxJson doUpload(MultipartHttpServletRequest request,HttpServletResponse response){
	AjaxJson j = new AjaxJson();
	try {
		MultipartFile uploadify = request.getFile("file");
        byte[] bytes = uploadify.getBytes();  
        String realFilename=uploadify.getOriginalFilename();
        String fileNoExtension = realFilename.substring(0,realFilename.lastIndexOf("."));
        String fileExtension = realFilename.substring(realFilename.lastIndexOf("."));
        String filename=/*fileNoExtension+*/System.currentTimeMillis()+fileExtension;
        String jwid =  request.getSession().getAttribute("jwid").toString();
        String uploadDir = request.getSession().getServletContext().getRealPath("upload/img/shaketicket/"+jwid);   
        File dirPath = new File(uploadDir);  
        if (!dirPath.exists()) {  
            dirPath.mkdirs();  
        }  
        String sep = System.getProperty("file.separator");  
        File uploadedFile = new File(uploadDir + sep  
                + filename);  
        FileCopyUtils.copy(bytes, uploadedFile);  
        j.setObj(filename);
        j.setSuccess(true);
		j.setMsg("保存成功");
	} catch (Exception e) {
		e.printStackTrace();
		j.setSuccess(false);
		j.setMsg("保存失败");
	}
	return j;
}
}

