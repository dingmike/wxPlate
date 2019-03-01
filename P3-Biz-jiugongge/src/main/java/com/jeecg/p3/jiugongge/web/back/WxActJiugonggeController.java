package com.jeecg.p3.jiugongge.web.back;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.util.PropertiesUtil;
import org.jeecgframework.p3.core.util.SystemTools;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.StringUtils;
import org.jeecgframework.p3.core.utils.persistence.OptimisticLockingException;
import org.jeecgframework.p3.core.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeecg.p3.baseApi.util.WxActReplaceUtil;
import com.jeecg.p3.jiugongge.entity.WxActJiugongge;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeAwards;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggePrizes;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRelation;
import com.jeecg.p3.jiugongge.enums.JiuGongGeActJoinNumEnum;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeAwardsService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggePrizesService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeRelationService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeService;
import com.jeecg.p3.jiugongge.util.ContextHolderUtils;

 /**
 * 描述：</b>WxActJiugonggeController<br>配置
 * @author junfeng.zhou
 * @since：2015年11月16日 11时07分11秒 星期一 
 * @version:1.0
 */
@Controller
@RequestMapping("/jiugongge/back/wxActJiugongge")
public class WxActJiugonggeController extends BaseController{
  @Autowired
  private WxActJiugonggeService wxActJiugonggeService;
  @Autowired
  private WxActJiugonggeAwardsService wxActJiugonggeAwardsService;
  @Autowired
  private WxActJiugonggePrizesService wxActJiugonggePrizesService;
  @Autowired
  private WxActJiugonggeRelationService wxActJiugonggeRelationService;

  
/**
  * 列表页面
  * @return
  */
@RequestMapping(value="list",method = {RequestMethod.GET,RequestMethod.POST})
public void list(@ModelAttribute WxActJiugongge query,HttpServletResponse response,HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
			@RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize) throws Exception{
	 	PageQuery<WxActJiugongge> pageQuery = new PageQuery<WxActJiugongge>();
	 	pageQuery.setPageNo(pageNo);
	 	pageQuery.setPageSize(pageSize);
	 	VelocityContext velocityContext = new VelocityContext();
	 	String jwid =  request.getSession().getAttribute("jwid").toString();
	 	String defaultJwid = WeiXinHttpUtil.getLocalValue("jiugongge", "defaultJwid");
	 	if(defaultJwid.equals(jwid)){
	 		String createBy = request.getSession().getAttribute("system_userid").toString();
	 		query.setCreateBy(createBy);
	 	}
	 	query.setJwid(jwid);
		pageQuery.setQuery(query);
		velocityContext.put("wxActJiugongge",query);
		velocityContext.put("pageInfos",SystemTools.convertPaginatedList(wxActJiugonggeService.queryPageList(pageQuery)));
		String viewName = "jiugongge/back/wxActJiugongge-list.vm";
		ViewVelocity.view(request,response,viewName,velocityContext);
}

 /**
  * 详情
  * @return
  */
@RequestMapping(value="toDetail",method = RequestMethod.GET)
public void wxActJiugonggeDetail(@RequestParam(required = true, value = "id" ) String id,HttpServletResponse response,HttpServletRequest request)throws Exception{
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "jiugongge/back/wxActJiugongge-detail.vm";
		WxActJiugongge wxActJiugongge = wxActJiugonggeService.queryById(id);
		velocityContext.put("wxActJiugongge",wxActJiugongge);
		 String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
		 List<WxActJiugonggeRelation> awarsDetailList=wxActJiugonggeRelationService.queryByActIdAndJwid(id,jwid);
		 velocityContext.put("awarsDetailList",awarsDetailList);
		 String defaultJwid = WeiXinHttpUtil.getLocalValue("jiugongge", "defaultJwid");
		 List<WxActJiugonggeAwards> awards;
		 List<WxActJiugonggePrizes> prizes;
		 	if(defaultJwid.equals(jwid)){
		 	String createBy = request.getSession().getAttribute("system_userid").toString();
		 	awards = wxActJiugonggeAwardsService.queryAwards(jwid,createBy);
		 	prizes = wxActJiugonggePrizesService.queryPrizes(jwid,createBy);
		 	}else{
		 		awards = wxActJiugonggeAwardsService.queryAwards(jwid);
		 		prizes = wxActJiugonggePrizesService.queryPrizes(jwid);
		 	}
		 	velocityContext.put("awards",awards);
			velocityContext.put("prizes",prizes);
			ViewVelocity.view(request,response,viewName,velocityContext);
}

	
	
/**
 * 跳转到添加页面
 * @return
 */
@RequestMapping(value = "/toAdd",method ={RequestMethod.GET, RequestMethod.POST})
public void toAddDialog(HttpServletRequest request,HttpServletResponse response,ModelMap model)throws Exception{
	 VelocityContext velocityContext = new VelocityContext();
        String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
        String defaultJwid = WeiXinHttpUtil.getLocalValue("jiugongge", "defaultJwid");
		 List<WxActJiugonggeAwards> awards;
		 List<WxActJiugonggePrizes> prizes;
		 	if(defaultJwid.equals(jwid)){
		 		String createBy = request.getSession().getAttribute("system_userid").toString();
		 	awards = wxActJiugonggeAwardsService.queryAwards(jwid,createBy);
		 	prizes = wxActJiugonggePrizesService.queryPrizes(jwid,createBy);
		 	}else{
		 		awards = wxActJiugonggeAwardsService.queryAwards(jwid);
		 		prizes = wxActJiugonggePrizesService.queryPrizes(jwid);
		 	}
		 	//update-begin--Author:zhangweijian  Date: 20180704 for：获取参与人数的枚举类型
		 	//限制参与总人数
		 	velocityContext.put("joinNumEnums",JiuGongGeActJoinNumEnum.values());
		 	//update-end--Author:zhangweijian  Date: 20180704 for：获取参与人数的枚举类型
		 	velocityContext.put("awards",awards);
			velocityContext.put("prizes",prizes);
			velocityContext.put("date",new Date().getTime());
	 String viewName = "jiugongge/back/wxActJiugongge-add.vm";
	 ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 保存信息
 * @return
 */
@RequestMapping(value = "/doAdd",method ={RequestMethod.GET, RequestMethod.POST})
@ResponseBody
public AjaxJson doAdd(@ModelAttribute WxActJiugongge wxActJiugongge){
	AjaxJson j = new AjaxJson();
	try {
		//update-being-alex-----Date:2017-2-24----for:替换活动说明中非法代码------
		wxActJiugongge.setDescription(WxActReplaceUtil.replace(wxActJiugongge.getDescription()));
		//update-end-alex-----Date:2017-2-24----for:替换活动说明中非法代码------
		
		/*//update-begin-alex Date:20170316 for:保存奖品奖项时记录创建人和当前jwid
		String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
		String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
		wxActJiugonggePrizes.setCreateBy(createBy);
		wxActJiugonggePrizes.setJwid(jwid);
		//update-end-alex Date:20170316 for:保存奖品奖项时记录创建人和当前jwid
		 */		
		wxActJiugonggeService.doAdd(wxActJiugongge);	
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
		 WxActJiugongge wxActJiugongge = wxActJiugonggeService.queryById(id);
		 velocityContext.put("wxActJiugongge",wxActJiugongge);
		 String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
		 List<WxActJiugonggeRelation> awarsDetailList=wxActJiugonggeRelationService.queryByActIdAndJwid(id,jwid);
		 velocityContext.put("awarsDetailList",awarsDetailList);
		 String defaultJwid = WeiXinHttpUtil.getLocalValue("jiugongge", "defaultJwid");
		 List<WxActJiugonggeAwards> awards;
		 List<WxActJiugonggePrizes> prizes;
		 	if(defaultJwid.equals(jwid)){
		 		String createBy = request.getSession().getAttribute("system_userid").toString();
		 	awards = wxActJiugonggeAwardsService.queryAwards(jwid,createBy);
		 	prizes = wxActJiugonggePrizesService.queryPrizes(jwid,createBy);
		 	}else{
		 		awards = wxActJiugonggeAwardsService.queryAwards(jwid);
		 		prizes = wxActJiugonggePrizesService.queryPrizes(jwid);
		 	}
		 	//update-begin--Author:zhangweijian  Date: 20180704 for：获取参与人数的枚举类型
		 	//限制参与总人数
		 	velocityContext.put("joinNumEnums",JiuGongGeActJoinNumEnum.values());
		 	//update-end--Author:zhangweijian  Date: 20180704 for：获取参与人数的枚举类型
		 	velocityContext.put("awards",awards);
			velocityContext.put("prizes",prizes);
		 String viewName = "jiugongge/back/wxActJiugongge-edit.vm";
		 ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 编辑
 * @return
 */
@RequestMapping(value = "/doEdit",method ={RequestMethod.GET, RequestMethod.POST})
@ResponseBody
public AjaxJson doEdit(@ModelAttribute WxActJiugongge wxActJiugongge){
	AjaxJson j = new AjaxJson();
	try {
		//update-being-alex-----Date:2017-2-24----for:替换活动说明中非法代码------
		wxActJiugongge.setDescription(WxActReplaceUtil.replace(wxActJiugongge.getDescription()));
		//update-end-alex-----Date:2017-2-24----for:替换活动说明中非法代码------
		wxActJiugonggeService.doEdit(wxActJiugongge);
		j.setMsg("操作成功");
	} catch (OptimisticLockingException e) {
		e.printStackTrace();
		j.setMsg("减少数量时,差不能少于当前的剩余数量");
		j.setSuccess(false);
	}catch (Exception e) {
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
			wxActJiugonggeService.doDelete(id);
			j.setMsg("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("删除失败");
		}
		return j;
}
/**
 * 获取shortUrl
 * @param id
 * @return
 */
@RequestMapping(value="getShortUrl",method = RequestMethod.POST)
@ResponseBody
public AjaxJson getShortUrl(@RequestParam(required = true, value = "id" ) String id){
	AjaxJson j=new AjaxJson();
	try {
		WxActJiugongge wxActJiugongge = wxActJiugonggeService.queryById(id);
		String shortUrl = wxActJiugongge.getShortUrl();
		if(StringUtils.isEmpty(shortUrl)){
			String hdurl=wxActJiugongge.getHdurl();
			PropertiesUtil properties=new PropertiesUtil("jiugongge.properties");
			shortUrl=WeiXinHttpUtil.getShortUrl(hdurl,properties.readProperty("defaultJwid"));
			if(StringUtils.isEmpty(shortUrl)){
				shortUrl=hdurl;
			}else{
				wxActJiugonggeService.doUpdateShortUrl(wxActJiugongge.getId(),shortUrl);
			}
		}
		if(StringUtils.isEmpty(shortUrl)){
			j.setMsg("获取地址失败！");
			j.setSuccess(false);
		}else{
			j.setObj(shortUrl);
			j.setSuccess(true);
			j.setMsg("获取地址成功！");
		}
	} catch (Exception e) {
		e.printStackTrace();
		j.setMsg("获取地址失败！");
		j.setSuccess(false);
	}
	return j;
}

}

