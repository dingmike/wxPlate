package com.jeecg.p3.jiugongge.verify.service;

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import com.jeecg.p3.jiugongge.verify.entity.WxActJiugonggeVerify;

/**
 * 描述：</b>WxActJiugonggeVerifyService<br>
 * @author：junfeng.zhou
 * @since：2018年04月18日 18时17分28秒 星期三 
 * @version:1.0
 */
public interface WxActJiugonggeVerifyService {
	
	
	public void doAdd(WxActJiugonggeVerify wxActJiugonggeVerify);
	
	public void doEdit(WxActJiugonggeVerify wxActJiugonggeVerify);
	
	public void doDelete(String id);
	
	public WxActJiugonggeVerify queryById(String id);
	
	public PageList<WxActJiugonggeVerify> queryPageList(PageQuery<WxActJiugonggeVerify> pageQuery);

	public WxActJiugonggeVerify queryByOpenId(String openid,String actId);

	public WxActJiugonggeVerify queryAllJiuGongGe(String actId, String cardPsd);
}

