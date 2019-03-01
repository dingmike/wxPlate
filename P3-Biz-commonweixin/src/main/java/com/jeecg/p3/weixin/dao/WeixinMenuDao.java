package com.jeecg.p3.weixin.dao;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.weixin.entity.WeixinMenu;

/**
 * 描述：</b>微信菜单表<br>
 * @author：weijian.zhang
 * @since：2018年07月12日 13时58分38秒 星期四 
 * @version:1.0
 */
public interface WeixinMenuDao extends GenericDao<WeixinMenu>{
	
	public Integer count(PageQuery<WeixinMenu> pageQuery);
	
	public List<WeixinMenu> queryPageList(PageQuery<WeixinMenu> pageQuery,Integer itemCount);

	//update-begin--Author:zhangweijian Date:20181017 for：添加jwid参数
	/**
	 * @功能：根据orders获取父级id
	 * @param orders
	 * @param jwid 
	 * @return
	 */
	public String getFatherIdByorders(String orders, String jwid);

	/**
	 * @功能：根据orders查询菜单信息
	 * @param orders
	 * @param jwid 
	 * @return
	 */
	public WeixinMenu queryByOrders(String orders, String jwid);
	//update-end--Author:zhangweijian Date:20181017 for：添加jwid参数
	/**
	 * @功能：根据fatherId查询其子级菜单
	 * @param id
	 * @return
	 */
	public int getSonMenuByFatherId(String fatherId);

	//update-begin--Author:zhangweijian  Date: 20180723 for：获取一级菜单
	/**
	 * @功能：获取一级菜单
	 * @param query
	 * @return
	 */
	public List<WeixinMenu> queryMenusByJwid(WeixinMenu query);
	//update-end--Author:zhangweijian  Date: 20180723 for：获取一级菜单

	/**
	 * 根据菜单KEY和JWID查询到菜单信息
	 * @author LeeShaoQing
	 */
	public List<WeixinMenu> queryMenuByKeyAndJwid(String key, String jwid);
	
}

