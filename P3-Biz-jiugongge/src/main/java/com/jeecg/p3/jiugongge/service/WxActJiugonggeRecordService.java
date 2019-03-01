package com.jeecg.p3.jiugongge.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;

import com.jeecg.p3.jiugongge.entity.WxActJiugonggePrizes;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRecord;

/**
 * 描述：</b>WxActJiugonggeRecordService<br>
 * @author：junfeng.zhou
 * @since：2015年11月17日 12时18分44秒 星期二 
 * @version:1.0
 */
public interface WxActJiugonggeRecordService {
	
	
	public void doAdd(WxActJiugonggeRecord wxActJiugonggeRecord);
	
	public void doEdit(WxActJiugonggeRecord wxActJiugonggeRecord);
	
	public void doDelete(String id);
	
	public WxActJiugonggeRecord queryById(String id);
	
	public PageList<WxActJiugonggeRecord> queryPageList(PageQuery<WxActJiugonggeRecord> pageQuery);
	public PageList<WxActJiugonggeRecord> queryPageListForJoin(PageQuery<WxActJiugonggeRecord> pageQuery);
	
	public List<WxActJiugonggeRecord> queryBargainRecordListByOpenidAndActidAndJwid(String openid,String actId,String jwid,Date currDate);
	public List<WxActJiugonggeRecord> queryMyAwardsByOpenidAndActidAndJwid(String openid,String actId,String jwid);
	public List<WxActJiugonggeRecord> queryBargainRecordListByActidAndJwid(String actId,String jwid);
	
	
	public WxActJiugonggePrizes creatAwards(WxActJiugonggeRecord wxActJiugonggeRecord);
	
	public InputStream  exportExcel(String actId,String jwid)throws FileNotFoundException;

	//update-begin--Author:zhangweijian  Date: 20180413 for:根据actId和awardCode判断改兑奖码是否存在
	/**
	 * @功能：根据actId和awardCode判断改兑奖码是否存在
	 * @param actId
	 * @param awardCode
	 * @return
	 */
	public WxActJiugonggeRecord queryByActIdAndawardCode(String actId, String awardCode);
	//update-end--Author:zhangweijian  Date: 20180413 for:根据actId和awardCode判断改兑奖码是否存在

	//update-begin--Author:zhangweijian  Date: 20180704 for：根据actId获取当前活动的参与总人数
	/**
	 * @功能：根据活动id获取当前活动的参与总人数
	 * @param actId
	 * @return
	 */
	public int getCountByActId(String actId);
	//update-end--Author:zhangweijian  Date: 20180704 for：根据actId获取当前活动的参与总人数
}

