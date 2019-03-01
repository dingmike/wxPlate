package com.jeecg.p3.commonweixin.dao;

import java.util.List;
import java.util.Map;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.commonweixin.entity.WeixinSystemProject;


/**
 * 描述：</b>WeixinSystemProjectDao<br>
 * @author：pituo
 * @since：2015年12月21日 17时49分18秒 星期一 
 * @version:1.0
 */
public interface WeixinSystemProjectDao extends GenericDao<WeixinSystemProject>{
	
	public Integer count(PageQuery<WeixinSystemProject> pageQuery);
	
	public List<WeixinSystemProject> queryPageList(PageQuery<WeixinSystemProject> pageQuery,Integer itemCount);
	
	/**
	 * 修改某表的全部url
	 * @param tableName表名
	 * @param hdurlName 字段名
	 * @param jwidName 字段名
	 * @param shortUrlName 短链接
	 * @param linksucai 素材链接
	 */
	public void editHdurl(String tableName,String hdurlName,String jwidName,String shortUrlName,String linksucai);
	
	/**
	 * 根据表名查询长链接
	 * @param tableName
	 * @return
	 */
	public List<Map<String,String>> queryAllActByTableName(String tableName);
	
	/**
	 * 修改表中的短链接
	 * @param tableName表名
	 * @param actId 活动名称
	 */
	public void doEditShortByTableName(String tableName,String actId,String shortUrl);
	
	/**
	 * 设置表的短链接为空
	 * @param tableName
	 */
	public void doEditShortEmpty(String tableName);
}

