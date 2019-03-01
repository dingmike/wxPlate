package com.jeecg.p3.shaketicket.dao;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.shaketicket.entity.WxActShaketicketAward;

/**
 * 描述：</b>WxActShaketicketAwardDao<br>
 * @author：pituo
 * @since：2015年12月24日 11时08分30秒 星期四 
 * @version:1.0
 */
public interface WxActShaketicketAwardDao extends GenericDao<WxActShaketicketAward>{
	
	public Integer count(PageQuery<WxActShaketicketAward> pageQuery);
	
	public List<WxActShaketicketAward> queryPageList(PageQuery<WxActShaketicketAward> pageQuery,Integer itemCount);
	public List<WxActShaketicketAward> queryRemainAwardsByActId(String actId);
	public List<WxActShaketicketAward> queryAwards(String jwid);
	public List<WxActShaketicketAward> queryAwards(String jwid,String createBy);

	//update-begin--Author:zhangweijian  Date: 20180329 for：根据jwid，创建人，奖项名称查询奖项表
	/**
	 * 根据jwid，创建人，awardsName查询奖项表
	 * @param jwid
	 * @param createBy
	 * @param awardsName
	 * */
	public List<WxActShaketicketAward> queryAwardsByName(String jwid, String createBy, String awardsName);
	//update-end--Author:zhangweijian  Date: 20180329 for：根据jwid，创建人，奖项名称查询奖项表
}

