package com.jeecg.p3.shaketicket.dao;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;

import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;

/**
 * 描述：</b>WxActShaketicketCouponDao<br>
 * @author：junfeng.zhou
 * @since：2016年03月24日 14时33分55秒 星期四 
 * @version:1.0
 */
public interface WxActShaketicketCouponDao extends GenericDao<WxActShaketicketCoupon>{
	
	public Integer count(PageQuery<WxActShaketicketCoupon> pageQuery);
	
	public List<WxActShaketicketCoupon> queryPageList(PageQuery<WxActShaketicketCoupon> pageQuery,Integer itemCount);
	
	public List<WxActShaketicketCoupon> queryCouponListByActIdAndAwardId(String actId, String awardId);
	
	public void updateStatus(String id);
	
}

