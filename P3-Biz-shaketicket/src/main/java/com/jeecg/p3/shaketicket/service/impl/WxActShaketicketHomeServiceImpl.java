package com.jeecg.p3.shaketicket.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.util.plugin.ContextHolderUtils;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecg.p3.baseApi.service.BaseApiActTxtService;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketAwardDao;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketConfigDao;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketHomeDao;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketAward;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketConfig;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketHome;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeException;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeExceptionEnum;
import com.jeecg.p3.shaketicket.service.WxActShaketicketHomeService;

@Service("wxActShaketicketHomeService")
public class WxActShaketicketHomeServiceImpl implements WxActShaketicketHomeService {
	@Resource
	private WxActShaketicketHomeDao wxActShaketicketHomeDao;
	@Resource
	private WxActShaketicketConfigDao wxActShaketicketConfigDao;
	@Autowired
	private BaseApiActTxtService baseApiActTxtService;
	@Autowired
	private WxActShaketicketAwardDao wxActShaketicketAwardDao;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doAdd(WxActShaketicketHome wxActShaketicketHome) {
		wxActShaketicketHome.setProjectCode("shaketicket");
		wxActShaketicketHomeDao.add(wxActShaketicketHome);
		List<WxActShaketicketConfig> awardsList= wxActShaketicketHome.getAwarsList();
		if(awardsList!=null){
			for (WxActShaketicketConfig actShaketicketConfig : awardsList) {
				//update-begin--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
				if(StringUtils.isEmpty(actShaketicketConfig.getAwardId())){
					actShaketicketConfig.setAwardId(saveAwards(actShaketicketConfig.getAwardsName()));
				}else{
					WxActShaketicketAward wxActShaketicketAward=wxActShaketicketAwardDao.get(actShaketicketConfig.getAwardId());
					//判断awardId和awardName是否是匹配的，不匹配则增加
					if(!wxActShaketicketAward.getAwardsName().equals(actShaketicketConfig.getAwardsName())){
						actShaketicketConfig.setAwardId(saveAwards(actShaketicketConfig.getAwardsName()));
					}
				}
				//update-end--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
				actShaketicketConfig.setActId(wxActShaketicketHome.getId());
				actShaketicketConfig.setActive("1");
				if(actShaketicketConfig.getProbability()==null){
					actShaketicketConfig.setProbability(0d);
				}
			}
			wxActShaketicketConfigDao.batchInsert("insert",awardsList);
		}
		baseApiActTxtService.copyActText(WeiXinHttpUtil.getLocalValue("shaketicket", WeiXinHttpUtil.TXT_ACTID_KEY), wxActShaketicketHome.getId());
	}

	//update-begin--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
	private String saveAwards(String awardsName) {
		WxActShaketicketAward wxActShaketicketAward=new WxActShaketicketAward();
		wxActShaketicketAward.setAwardsName(awardsName);
		String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
		String defaultJwid = WeiXinHttpUtil.getLocalValue("shaketicket", "defaultJwid");
		String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
		//如果是H5活动汇
		if(defaultJwid.equals(jwid)){
			List<WxActShaketicketAward> queryAwardsByName = wxActShaketicketAwardDao.queryAwardsByName(jwid, createBy, awardsName);
			if(queryAwardsByName.size()>0){
				return queryAwardsByName.get(0).getId();
			}
		}else{
			List<WxActShaketicketAward> queryAwardsByName = wxActShaketicketAwardDao.queryAwardsByName(jwid, "", awardsName);
			if(queryAwardsByName.size()>0){
				return queryAwardsByName.get(0).getId();
			}
		}
		wxActShaketicketAward.setCreateBy(createBy);
		wxActShaketicketAward.setJwid(jwid);
		wxActShaketicketAward.setImg("content/shaketicket/default/img/picurl-shake.jpg");
		wxActShaketicketAwardDao.add(wxActShaketicketAward);
		return wxActShaketicketAward.getId();
	}
	//update-end--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doEdit(WxActShaketicketHome wxActShaketicketHome) {
		wxActShaketicketHomeDao.update(wxActShaketicketHome);
		List<WxActShaketicketConfig> newAwardsList= wxActShaketicketHome.getAwarsList();//新的明细配置集合
		List<String> ids=new ArrayList<String>();
		if(newAwardsList!=null){
			for (WxActShaketicketConfig relation : newAwardsList) {
				//update-begin--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
				if(StringUtils.isEmpty(relation.getAwardId())){
					relation.setAwardId(saveAwards(relation.getAwardsName()));
				}else{
					WxActShaketicketAward wxActShaketicketAward = wxActShaketicketAwardDao.get(relation.getAwardId());
					//判断awardId和awardName是否是匹配的，不匹配则增加
					if(!wxActShaketicketAward.getAwardsName().equals(relation.getAwardsName())){
						relation.setAwardId(saveAwards(relation.getAwardsName()));
					}
				}
				//update-end--Author:zhangweijian  Date: 20180329 for：判断奖项是否存在，不存在，则增加
				if(StringUtils.isNotEmpty(relation.getId())){				
					ids.add(relation.getId());
				}
			}
			wxActShaketicketConfigDao.bactchDeleteOldAwards(ids,wxActShaketicketHome.getId());//批量删除不在新的明细配置集合的数据
			for (WxActShaketicketConfig actShaketicketConfig : newAwardsList) {
				if(StringUtils.isEmpty(actShaketicketConfig.getId())){
					actShaketicketConfig.setActId(wxActShaketicketHome.getId());
					actShaketicketConfig.setActive("1");
					wxActShaketicketConfigDao.add(actShaketicketConfig);
				}else{
					WxActShaketicketConfig wxActShaketicketConfig = wxActShaketicketConfigDao.get(actShaketicketConfig.getId());
					if(wxActShaketicketConfig!=null&&wxActShaketicketConfig.getAmount()!=null&&actShaketicketConfig.getAmount()!=null){
						Integer num=actShaketicketConfig.getAmount()-wxActShaketicketConfig.getAmount();
						if(num!=0){
							//更新数据库
							if(wxActShaketicketConfig.getRemainNum()!=null){
								if(wxActShaketicketConfig.getRemainNum()>=0-num){
									wxActShaketicketConfigDao.updateNum(actShaketicketConfig.getId(), num);
								}else{
									throw new ShaketicketHomeException(ShaketicketHomeExceptionEnum.ACT_BARGAIN_DATA_ERROR,"数量更新失败");
								}
							}
						}
					}
					actShaketicketConfig.setAmount(null);
					wxActShaketicketConfigDao.update(actShaketicketConfig);
				}
			}
		}else{
			wxActShaketicketConfigDao.bactchDeleteOldAwards(ids,wxActShaketicketHome.getId());//批量删除不在新的明细配置集合的数据
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doDelete(String id) {
		wxActShaketicketHomeDao.delete(id);
		wxActShaketicketConfigDao.batchDeleteByActId(id);//同步活动明细配置
		baseApiActTxtService.batchDeleteByActCode(id);//同步删除系统文本
	}

	@Override
	public WxActShaketicketHome queryById(String id) {
		WxActShaketicketHome wxActShaketicketHome  = wxActShaketicketHomeDao.get(id);
		return wxActShaketicketHome;
	}

	@Override
	public PageList<WxActShaketicketHome> queryPageList(
		PageQuery<WxActShaketicketHome> pageQuery) {
		PageList<WxActShaketicketHome> result = new PageList<WxActShaketicketHome>();
		Integer itemCount = wxActShaketicketHomeDao.count(pageQuery);
		List<WxActShaketicketHome> list = wxActShaketicketHomeDao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public void doedit(WxActShaketicketHome wxActShaketicketHome) {
		wxActShaketicketHomeDao.update(wxActShaketicketHome);
		
	}
	
}
