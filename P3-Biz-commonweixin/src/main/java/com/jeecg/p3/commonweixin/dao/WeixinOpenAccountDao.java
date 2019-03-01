package com.jeecg.p3.commonweixin.dao;


import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.commonweixin.entity.WeixinOpenAccount;

/**
 * 描述：</b>WeixinOpenAccountDao<br>
 * @author：huangqingquan
 * @since：2016年11月30日 15时05分20秒 星期三 
 * @version:1.0
 */
public interface WeixinOpenAccountDao extends GenericDao<WeixinOpenAccount>{
	
	/**
	 * 查询，通过appid查询，按照获取ticket时间倒叙
	 * @param appid
	 * @return
	 */
	public WeixinOpenAccount queryOneByAppid(String appid);
	
	public Integer count(PageQuery<WeixinOpenAccount> pageQuery);
	
	public List<WeixinOpenAccount> queryPageList(PageQuery<WeixinOpenAccount> pageQuery,Integer itemCount);
}