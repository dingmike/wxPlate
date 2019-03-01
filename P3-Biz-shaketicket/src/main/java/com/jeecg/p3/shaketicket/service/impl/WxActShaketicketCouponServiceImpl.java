package com.jeecg.p3.shaketicket.service.impl;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.springframework.stereotype.Service;

import com.jeecg.p3.shaketicket.dao.WxActShaketicketCouponDao;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeException;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeExceptionEnum;
import com.jeecg.p3.shaketicket.service.WxActShaketicketCouponService;

@Service("wxActShaketicketCouponService")
public class WxActShaketicketCouponServiceImpl implements WxActShaketicketCouponService {
	@Resource
	private WxActShaketicketCouponDao wxActShaketicketCouponDao;

	@Override
	public void doAdd(WxActShaketicketCoupon wxActShaketicketCoupon) {
		wxActShaketicketCouponDao.add(wxActShaketicketCoupon);
	}

	@Override
	public void doEdit(WxActShaketicketCoupon wxActShaketicketCoupon) {
		wxActShaketicketCouponDao.update(wxActShaketicketCoupon);
	}

	@Override
	public void doDelete(String id) {
		wxActShaketicketCouponDao.delete(id);
	}

	@Override
	public WxActShaketicketCoupon queryById(String id) {
		WxActShaketicketCoupon wxActShaketicketCoupon  = wxActShaketicketCouponDao.get(id);
		return wxActShaketicketCoupon;
	}

	@Override
	public PageList<WxActShaketicketCoupon> queryPageList(
		PageQuery<WxActShaketicketCoupon> pageQuery) {
		PageList<WxActShaketicketCoupon> result = new PageList<WxActShaketicketCoupon>();
		Integer itemCount = wxActShaketicketCouponDao.count(pageQuery);
		List<WxActShaketicketCoupon> list = wxActShaketicketCouponDao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public WxActShaketicketCoupon routeCardId(String actId, String awardId) {
		// 根据主活动ID和面额获取现金券列表
		List<WxActShaketicketCoupon> couponsList = wxActShaketicketCouponDao
				.queryCouponListByActIdAndAwardId(actId, awardId);
		WxActShaketicketCoupon coupon = null;
		if (couponsList.size() > 0) {
			// 从列表中随机取得卡券ID
			Random random = new Random();
			int randomIndex = random.nextInt(couponsList.size())% (couponsList.size());
			coupon = couponsList.get(randomIndex);
		}else{
			throw new ShaketicketHomeException(
					ShaketicketHomeExceptionEnum.ACT_BARGAIN_CARD_NO_FIND, "没有可用的密码");
		}
		return coupon;
	}
	
}
