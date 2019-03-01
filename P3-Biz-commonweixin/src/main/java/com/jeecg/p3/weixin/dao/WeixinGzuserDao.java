package com.jeecg.p3.weixin.dao;

import java.util.List;
import java.util.Map;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.weixin.entity.WeixinGzuser;

/**
 * 描述：</b>粉丝表<br>
 * @author：weijian.zhang
 * @since：2018年07月26日 15时38分40秒 星期四 
 * @version:1.0
 */
public interface WeixinGzuserDao extends GenericDao<WeixinGzuser>{
	
	public Integer count(PageQuery<WeixinGzuser> pageQuery);
	
	public List<WeixinGzuser> queryPageList(PageQuery<WeixinGzuser> pageQuery,Integer itemCount);
	
	/**
	 * 根据JWID分页查询粉丝信息
	 * @param jwid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<WeixinGzuser> queryNumberByJwid(String jwid, int pageNo, int pageSize);
	//update-begin--Author:zhangweijian  Date: 20180820 for：根据OpenId查询粉丝信息
	/**
	 * 根据OpenId查询粉丝信息
	 * @param openId
	 * @param jwid 
	 * @return
	 */
	public WeixinGzuser queryByOpenId(String openId, String jwid);
	//update-end--Author:zhangweijian  Date: 20180820 for：根据OpenId查询粉丝信息
}

