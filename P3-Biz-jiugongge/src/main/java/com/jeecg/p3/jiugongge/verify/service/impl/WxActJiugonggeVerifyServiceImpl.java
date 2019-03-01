package com.jeecg.p3.jiugongge.verify.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import com.jeecg.p3.jiugongge.verify.service.WxActJiugonggeVerifyService;
import com.jeecg.p3.jiugongge.verify.entity.WxActJiugonggeVerify;
import com.jeecg.p3.jiugongge.verify.dao.WxActJiugonggeVerifyDao;

@Service("wxActJiugonggeVerifyService")
public class WxActJiugonggeVerifyServiceImpl implements WxActJiugonggeVerifyService {
	@Resource
	private WxActJiugonggeVerifyDao wxActJiugonggeVerifyDao;

	@Override
	public void doAdd(WxActJiugonggeVerify wxActJiugonggeVerify) {
		wxActJiugonggeVerifyDao.add(wxActJiugonggeVerify);
	}

	@Override
	public void doEdit(WxActJiugonggeVerify wxActJiugonggeVerify) {
		wxActJiugonggeVerifyDao.update(wxActJiugonggeVerify);
	}

	@Override
	public void doDelete(String id) {
		wxActJiugonggeVerifyDao.delete(id);
	}

	@Override
	public WxActJiugonggeVerify queryById(String id) {
		WxActJiugonggeVerify wxActJiugonggeVerify  = wxActJiugonggeVerifyDao.get(id);
		return wxActJiugonggeVerify;
	}

	@Override
	public PageList<WxActJiugonggeVerify> queryPageList(
		PageQuery<WxActJiugonggeVerify> pageQuery) {
		PageList<WxActJiugonggeVerify> result = new PageList<WxActJiugonggeVerify>();
		Integer itemCount = wxActJiugonggeVerifyDao.count(pageQuery);
		List<WxActJiugonggeVerify> list = wxActJiugonggeVerifyDao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public WxActJiugonggeVerify queryByOpenId(String openid,String actId) {
		return wxActJiugonggeVerifyDao.queryByOpenId(openid,actId);
	}

	@Override
	public WxActJiugonggeVerify queryAllJiuGongGe(String actId, String cardPsd) {
		return wxActJiugonggeVerifyDao.queryAllJiuGongGe(actId, cardPsd);
	}
	
}
