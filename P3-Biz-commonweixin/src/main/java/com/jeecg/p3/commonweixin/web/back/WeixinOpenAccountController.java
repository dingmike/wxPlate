package com.jeecg.p3.commonweixin.web.back;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.jeecgframework.p3.core.util.PropertiesUtil;
import org.jeecgframework.p3.core.util.SystemTools;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.utils.common.PageQuery;

import com.alibaba.fastjson.JSONObject;
import com.jeecg.p3.commonweixin.entity.WeixinOpenAccount;
import com.jeecg.p3.commonweixin.service.WeixinOpenAccountService;
import com.jeecg.p3.commonweixin.util.HttpUtil;

import org.jeecgframework.p3.core.web.BaseController;

 /**
 * 描述：</b>WeixinOpenAccountController<br>
 * @author huangqingquan
 * @since：2016年12月05日 17时50分49秒 星期一 
 * @version:1.0
 */
@Controller
@RequestMapping("/commonweixin/back/weixinOpenAccount")
public class WeixinOpenAccountController extends BaseController{
  @Autowired
  private WeixinOpenAccountService weixinOpenAccountService;
  
/**
  * 列表页面
  * @return
  */
@RequestMapping(value="list",method = {RequestMethod.GET,RequestMethod.POST})
public void list(@ModelAttribute WeixinOpenAccount query,HttpServletResponse response,HttpServletRequest request,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
			@RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize) throws Exception{
	 	VelocityContext velocityContext = new VelocityContext();
	 	String viewName = "commonweixin/back/weixinOpenAccount-list.vm";
	 	try {
		 	PageQuery<WeixinOpenAccount> pageQuery = new PageQuery<WeixinOpenAccount>();
		 	pageQuery.setPageNo(pageNo);
		 	pageQuery.setPageSize(pageSize);
			pageQuery.setQuery(query);
			velocityContext.put("weixinOpenAccount",query);
			velocityContext.put("pageInfos",SystemTools.convertPaginatedList(weixinOpenAccountService.queryPageList(pageQuery)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ViewVelocity.view(request,response,viewName,velocityContext);
}

/**
 * 重置accessToken
 * @param response
 * @param request
 * @return
 */
@ResponseBody
@RequestMapping(value="resetAccessToken",method = {RequestMethod.GET,RequestMethod.POST})
public AjaxJson resetAccessToken(HttpServletResponse response,HttpServletRequest request){
	AjaxJson j=new AjaxJson();
	try {
		PropertiesUtil p=new PropertiesUtil("commonweixin.properties");
		String componentAppid = p.readProperty("component_appid");
		String componentAppsecret = p.readProperty("component_appsecret");
		String url=p.readProperty("COMPONENT_ACCESS_ACTOKEN_URL");
		WeixinOpenAccount weixinOpenAccount = weixinOpenAccountService.queryOneByAppid(componentAppid);
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("component_appid", componentAppid);
		param.put("component_appsecret", componentAppsecret);
		param.put("component_verify_ticket", weixinOpenAccount.getTicket());
		JSONObject jsonObject = new JSONObject(param);
		JSONObject jsonObj = HttpUtil.httpRequest(url, "POST", jsonObject.toString());
		log.info("重置第三方平台ACCESSTOKEN时返回的报文"+jsonObj);
		if(jsonObj!=null&&jsonObj.containsKey("component_access_token")){
			weixinOpenAccount.setComponentAccessToken(jsonObj.getString("component_access_token"));
			weixinOpenAccount.setGetAccessTokenTime(new Date());
			weixinOpenAccountService.doEdit(weixinOpenAccount);
			j.setMsg("重置ACCESSTOKEN成功");
		}else{
			j.setMsg("重置ACCESSTOKEN失败");
		}
	} catch (Exception e) {
		e.printStackTrace();
		j.setSuccess(false);
		j.setMsg("重置ACCESSTOKEN失败");
	}
	return j;
}
}

