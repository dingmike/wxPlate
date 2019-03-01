package com.jeecg.p3.weixin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.jeecgframework.p3.core.util.PropertiesUtil;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.jeewx.api.wxsendmsg.JwSendMessageAPI;
import org.jeewx.api.wxsendmsg.util.ReadImgUrls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jeecg.p3.weixin.dao.WeixinNewsitemDao;
import com.jeecg.p3.weixin.dao.WeixinNewstemplateDao;
import com.jeecg.p3.weixin.entity.BaseGraphic;
import com.jeecg.p3.weixin.entity.UploadGraphic;
import com.jeecg.p3.weixin.entity.WeixinNewsitem;
import com.jeecg.p3.weixin.entity.WeixinNewstemplate;
import com.jeecg.p3.weixin.service.WeixinNewstemplateService;
import com.jeecg.p3.weixin.util.WeixinUtil;
import com.jeecg.p3.weixin.util.WxErrCodeUtil;

/**
 * 描述：</b>图文模板表<br>
 * @author：weijian.zhang
 * @since：2018年07月13日 12时46分13秒 星期五 
 * @version:1.0
 */
@Service("weixinNewstemplateService")
public class WeixinNewstemplateServiceImpl implements WeixinNewstemplateService {
	@Resource
	private WeixinNewstemplateDao weixinNewstemplateDao;
	@Resource
	private WeixinNewsitemDao weixinNewsitemDao;
	
	//上传图文消息素材
	public final static String upload_group_news_url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";

	private static String doMain="";
	static{
		  PropertiesUtil p=new PropertiesUtil("commonweixin.properties");
		  doMain=p.readProperty("domain");
	  }
	@Override
	public void doAdd(WeixinNewstemplate weixinNewstemplate) {
		weixinNewstemplateDao.add(weixinNewstemplate);
	}

	@Override
	public void doEdit(WeixinNewstemplate weixinNewstemplate) {
		weixinNewstemplateDao.update(weixinNewstemplate);
	}

	@Override
	public void doDelete(String id) {
		weixinNewstemplateDao.delete(id);
	}

	@Override
	public WeixinNewstemplate queryById(String id) {
		WeixinNewstemplate weixinNewstemplate  = weixinNewstemplateDao.get(id);
		return weixinNewstemplate;
	}

	@Override
	public PageList<WeixinNewstemplate> queryPageList(
		PageQuery<WeixinNewstemplate> pageQuery) {
		PageList<WeixinNewstemplate> result = new PageList<WeixinNewstemplate>();
		Integer itemCount = weixinNewstemplateDao.count(pageQuery);
		List<WeixinNewstemplate> list = weixinNewstemplateDao.queryPageList(pageQuery,itemCount);
		//author:sunkai--date:2018-10-08--for:上传状态转译
		for(WeixinNewstemplate newsTemplate:list){
			if(newsTemplate.getUploadType().equals("2")){
				if(newsTemplate.getUpdateTime() != null && newsTemplate.getUploadTime() != null && newsTemplate.getUpdateTime().after(newsTemplate.getUploadTime())){
					newsTemplate.setUploadStatus("1");
				}
			}
		}
		//author:sunkai--date:2018-10-08--for:上传状态转译
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	//update-begin--Author:zhangweijian  Date: 20180720 for：获取所有图文素材
	//获取所有图文素材
	@Override
	public List<WeixinNewstemplate> getAllItems(String jwid,String uploadType) {
		return weixinNewstemplateDao.getAllItems(jwid,uploadType);
	}
	//update-end--Author:zhangweijian  Date: 20180720 for：获取所有图文素材

	
}
