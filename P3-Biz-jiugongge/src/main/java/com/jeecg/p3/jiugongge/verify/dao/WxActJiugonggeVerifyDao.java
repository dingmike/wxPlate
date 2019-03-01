package com.jeecg.p3.jiugongge.verify.dao;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.jiugongge.verify.entity.WxActJiugonggeVerify;

/**
 * 描述：</b>WxActJiugonggeVerifyDao<br>
 * @author：junfeng.zhou
 * @since：2018年04月18日 18时17分28秒 星期三 
 * @version:1.0
 */
public interface WxActJiugonggeVerifyDao extends GenericDao<WxActJiugonggeVerify>{
	
	public Integer count(PageQuery<WxActJiugonggeVerify> pageQuery);
	
	public List<WxActJiugonggeVerify> queryPageList(PageQuery<WxActJiugonggeVerify> pageQuery,Integer itemCount);

	public WxActJiugonggeVerify queryByOpenId(String openid,String actId);

	public WxActJiugonggeVerify queryAllJiuGongGe(String actId, String cardPsd);
	
}

