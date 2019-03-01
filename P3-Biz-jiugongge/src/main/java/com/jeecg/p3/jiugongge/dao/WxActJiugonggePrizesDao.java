package com.jeecg.p3.jiugongge.dao;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.jiugongge.entity.WxActJiugonggePrizes;

/**
 * 描述：</b>WxActJiugonggePrizesDao<br>
 * @author：junfeng.zhou
 * @since：2015年11月16日 11时07分12秒 星期一 
 * @version:1.0
 */
public interface WxActJiugonggePrizesDao extends GenericDao<WxActJiugonggePrizes>{
	
	public Integer count(PageQuery<WxActJiugonggePrizes> pageQuery);
	
	public List<WxActJiugonggePrizes> queryPageList(PageQuery<WxActJiugonggePrizes> pageQuery,Integer itemCount);
	public List<WxActJiugonggePrizes> queryByActId(String actid);
	public List<WxActJiugonggePrizes> queryRemainAwardsByActId(String actid);
	public List<WxActJiugonggePrizes> queryByAwardIdAndActId(String awardid,String actId);
	public List<WxActJiugonggePrizes> queryPrizes(String jwid);
	public List<WxActJiugonggePrizes> queryPrizes(String jwid,String creatBy);
	
	/**
	 * @功能:通过奖品名称查询
	 * @作者:liwenhui 
	 * @时间:2018-3-28 下午02:56:57
	 * @修改：
	 * @param jwid
	 * @param createBy
	 * @param name
	 * @return  
	 */
	public List<WxActJiugonggePrizes> queryPrizesByName(String jwid,String createBy,String name);
}

