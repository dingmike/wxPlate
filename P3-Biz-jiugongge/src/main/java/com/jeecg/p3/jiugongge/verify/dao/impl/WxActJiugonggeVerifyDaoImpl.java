package com.jeecg.p3.jiugongge.verify.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.PageQueryWrapper;
import org.jeecgframework.p3.core.utils.persistence.mybatis.GenericDaoDefault;
import org.springframework.stereotype.Repository;
import com.jeecg.p3.jiugongge.verify.dao.WxActJiugonggeVerifyDao;
import com.jeecg.p3.jiugongge.verify.entity.WxActJiugonggeVerify;

/**
 * 描述：</b>WxActJiugonggeVerifyDaoImpl<br>
 * @author：junfeng.zhou
 * @since：2018年04月18日 18时17分28秒 星期三 
 * @version:1.0
 */
@Repository("wxActJiugonggeVerifyDao")
public class WxActJiugonggeVerifyDaoImpl extends GenericDaoDefault<WxActJiugonggeVerify> implements WxActJiugonggeVerifyDao{

	@Override
	public Integer count(PageQuery<WxActJiugonggeVerify> pageQuery) {
		return (Integer) super.queryOne("count",pageQuery);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WxActJiugonggeVerify> queryPageList(
			PageQuery<WxActJiugonggeVerify> pageQuery,Integer itemCount) {
		PageQueryWrapper<WxActJiugonggeVerify> wrapper = new PageQueryWrapper<WxActJiugonggeVerify>(pageQuery.getPageNo(), pageQuery.getPageSize(),itemCount, pageQuery.getQuery());
		return (List<WxActJiugonggeVerify>)super.query("queryPageList",wrapper);
	}

	@Override
	public WxActJiugonggeVerify queryByOpenId(String openid,String actId) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("openid",openid);
		map.put("actId", actId);
		return  (WxActJiugonggeVerify) super.queryOne("queryByOpenId", map);
	}

	@Override
	public WxActJiugonggeVerify queryAllJiuGongGe(String actId, String cardPsd) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("actId",actId);
		map.put("cardPsd", cardPsd);
		return (WxActJiugonggeVerify) super.queryOne("queryAllJiuGongGe", map);
	}


}

