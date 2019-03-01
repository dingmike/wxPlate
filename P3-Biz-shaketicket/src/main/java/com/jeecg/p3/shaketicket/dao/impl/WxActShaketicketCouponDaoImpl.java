package com.jeecg.p3.shaketicket.dao.impl;

import java.util.List;
import java.util.Map;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.PageQueryWrapper;
import org.jeecgframework.p3.core.utils.persistence.OptimisticLockingException;
import org.jeecgframework.p3.core.utils.persistence.mybatis.GenericDaoDefault;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketCouponDao;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;

/**
 * 描述：</b>WxActShaketicketCouponDaoImpl<br>
 * @author：junfeng.zhou
 * @since：2016年03月24日 14时33分55秒 星期四 
 * @version:1.0
 */
@Repository("wxActShaketicketCouponDao")
public class WxActShaketicketCouponDaoImpl extends GenericDaoDefault<WxActShaketicketCoupon> implements WxActShaketicketCouponDao{

	@Override
	public Integer count(PageQuery<WxActShaketicketCoupon> pageQuery) {
		return (Integer) super.queryOne("count",pageQuery);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WxActShaketicketCoupon> queryPageList(
			PageQuery<WxActShaketicketCoupon> pageQuery,Integer itemCount) {
		PageQueryWrapper<WxActShaketicketCoupon> wrapper = new PageQueryWrapper<WxActShaketicketCoupon>(pageQuery.getPageNo(), pageQuery.getPageSize(),itemCount, pageQuery.getQuery());
		return (List<WxActShaketicketCoupon>)super.query("queryPageList",wrapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WxActShaketicketCoupon> queryCouponListByActIdAndAwardId(
			String actId, String awardId) {
		Map<String,Object> param = Maps.newConcurrentMap();
		param.put("actId", actId);
		param.put("awardId", awardId);
		return (List<WxActShaketicketCoupon>)super.query("queryCouponListByActIdAndAwardId",param);
	}

	@Override
	public void updateStatus(String id) {
		Map<String,String> param = Maps.newConcurrentMap();
		param.put("id", id);
		int row = super.getSqlSession().update(getStatementId("updateStatus"), id);
		if (row == 0) {
			throw new OptimisticLockingException("乐观锁异常");
		}
	}


}

