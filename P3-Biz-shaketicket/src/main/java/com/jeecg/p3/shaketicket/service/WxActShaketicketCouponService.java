package com.jeecg.p3.shaketicket.service;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;

import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;

/**
 * 描述：</b>WxActShaketicketCouponService<br>
 * @author：junfeng.zhou
 * @since：2016年03月24日 14时33分55秒 星期四 
 * @version:1.0
 */
public interface WxActShaketicketCouponService {
	
	
	public void doAdd(WxActShaketicketCoupon wxActShaketicketCoupon);
	
	public void doEdit(WxActShaketicketCoupon wxActShaketicketCoupon);
	
	public void doDelete(String id);
	
	public WxActShaketicketCoupon queryById(String id);
	
	public PageList<WxActShaketicketCoupon> queryPageList(PageQuery<WxActShaketicketCoupon> pageQuery);
	
	public WxActShaketicketCoupon routeCardId(String actId,String awardId);
}

