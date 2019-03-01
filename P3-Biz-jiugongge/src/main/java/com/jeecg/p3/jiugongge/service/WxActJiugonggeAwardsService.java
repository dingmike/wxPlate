package com.jeecg.p3.jiugongge.service;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;

import com.jeecg.p3.jiugongge.entity.WxActJiugonggeAwards;

/**
 * 描述：</b>WxActJiugonggeAwardsService<br>
 * @author：junfeng.zhou
 * @since：2015年11月16日 11时07分12秒 星期一 
 * @version:1.0
 */
public interface WxActJiugonggeAwardsService {
	
	
	public void doAdd(WxActJiugonggeAwards wxActJiugonggeAwards);
	
	public void doEdit(WxActJiugonggeAwards wxActJiugonggeAwards);
	
	public void doDelete(String id);
	
	public WxActJiugonggeAwards queryById(String id);
	public Boolean validReat(int value,String jwid);
	public Boolean validReat(String id,int value,String jwid);
	
	public PageList<WxActJiugonggeAwards> queryPageList(PageQuery<WxActJiugonggeAwards> pageQuery);
	
	
	public List<WxActJiugonggeAwards> queryAwards(String jwid);
	
	public List<WxActJiugonggeAwards> queryAwards(String jwid,String creatBy);
	
	/**
	 * @功能:通过奖项名称查询奖项
	 * @作者:liwenhui 
	 * @时间:2018-3-28 下午02:54:25
	 * @修改：
	 * @param jwid
	 * @param creatBy
	 * @param content
	 * @return  
	 */
	public List<WxActJiugonggeAwards> queryAwardsByName(String jwid,String createBy,String content);
	
	public Boolean validUsed(String id);
	
	public Integer getMaxAwardsValue(String jwid);
	public Integer getMaxAwardsValueByCreateBy(String jwid,String createBy);
}

