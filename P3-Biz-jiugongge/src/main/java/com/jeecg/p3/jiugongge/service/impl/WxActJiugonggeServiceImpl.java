package com.jeecg.p3.jiugongge.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecg.p3.baseApi.service.BaseApiActTxtService;
import com.jeecg.p3.jiugongge.dao.WxActJiugonggeAwardsDao;
import com.jeecg.p3.jiugongge.dao.WxActJiugonggeDao;
import com.jeecg.p3.jiugongge.dao.WxActJiugonggePrizesDao;
import com.jeecg.p3.jiugongge.dao.WxActJiugonggeRelationDao;
import com.jeecg.p3.jiugongge.entity.WxActJiugongge;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeAwards;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggePrizes;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRelation;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeService;
import com.jeecg.p3.jiugongge.util.ContextHolderUtils;

@Service("wxActJiugonggeService")
public class WxActJiugonggeServiceImpl implements WxActJiugonggeService {
	@Resource
	private WxActJiugonggeDao wxActJiugonggeDao;
	@Resource
	private WxActJiugonggeRelationDao wxActJiugonggeRelationDao;
	@Autowired
	private BaseApiActTxtService baseApiActTxtService;
	@Autowired
	private WxActJiugonggeAwardsDao wxActJiugonggeAwardsDao;
	@Autowired
	private WxActJiugonggePrizesDao wxActJiugonggePrizesDao;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doAdd(WxActJiugongge wxActJiugongge) {
		wxActJiugongge.setProjectCode("jiugongge");
		wxActJiugongge.setDescription(wxActJiugongge.getDescription().replaceAll(" ", ""));
		wxActJiugonggeDao.add(wxActJiugongge);
		List<WxActJiugonggeRelation> awardsList= wxActJiugongge.getAwarsList();
		if(awardsList!=null){
			for (WxActJiugonggeRelation wxActJiugonggeRelation : awardsList) {
				//--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:判断奖项是否为空,为空则增加
				if(StringUtils.isEmpty(wxActJiugonggeRelation.getAwardId())){
					wxActJiugonggeRelation.setAwardId(saveAwards(wxActJiugonggeRelation.getAwardName()));
				}else{
					WxActJiugonggeAwards wxActJiugonggeAward = wxActJiugonggeAwardsDao.get(wxActJiugonggeRelation.getAwardId());
					//判断awardId和awardName是否是匹配的，不匹配则增加
					if(!wxActJiugonggeAward.getAwardsName().equals(wxActJiugonggeRelation.getAwardName())){
						wxActJiugonggeRelation.setAwardId(saveAwards(wxActJiugonggeRelation.getAwardName()));
					}
				}
				//--update-end--date:2018-3-27 18:14:03 author:liwenhui for:判断奖项是否为空,为空则增加
				//--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:判断奖品是否为空,为空则增加
				if(StringUtils.isEmpty(wxActJiugonggeRelation.getPrizeId())){
					wxActJiugonggeRelation.setPrizeId(savePrizes(wxActJiugonggeRelation.getPrizeName()));
				}else{
					WxActJiugonggePrizes wxActJiugonggePrize = wxActJiugonggePrizesDao.get(wxActJiugonggeRelation.getPrizeId());
					//prizeId和prizeName是否是匹配的，不匹配则增加
					if(!wxActJiugonggePrize.getName().equals(wxActJiugonggeRelation.getPrizeName())){
						wxActJiugonggeRelation.setPrizeId(savePrizes(wxActJiugonggeRelation.getPrizeName()));
					}
				}
				//--update-end--date:2018-3-27 18:14:03 author:liwenhui for:判断奖品是否为空,为空则增加
				
				wxActJiugonggeRelation.setActId(wxActJiugongge.getId());
				if(wxActJiugonggeRelation.getProbability()==null){
					wxActJiugonggeRelation.setProbability(0d);
				}
				if(wxActJiugonggeRelation.getAmount()==null){
					wxActJiugonggeRelation.setAmount(0);
				}
			}
			wxActJiugonggeRelationDao.batchInsert("insert",awardsList);
		}
		baseApiActTxtService.copyActText(WeiXinHttpUtil.getLocalValue("jiugongge", WeiXinHttpUtil.TXT_ACTID_KEY),
				wxActJiugongge.getId());
		//systemActTxtService.doCopyTxt(WeiXinHttpUtil.getLocalValue("jiugongge", WeiXinHttpUtil.TXT_ACTID_KEY), wxActJiugongge.getId());
	}

	/**
	 * @功能:保存奖项
	 * @作者:liwenhui 
	 * @时间:2018-3-28 上午09:56:47
	 * @修改：
	 * @param awardName
	 * @return
	 * @throws Exception  
	 */
	private String saveAwards(String awardName){
		WxActJiugonggeAwards wxActJiugonggeAwards=new WxActJiugonggeAwards();
		wxActJiugonggeAwards.setAwardsName(awardName);
		String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
	 	String defaultJwid = WeiXinHttpUtil.getLocalValue("jiugongge", "defaultJwid");
	 	String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
	 	//如果是H5活动汇
	 	if(defaultJwid.equals(jwid)){
	 		List<WxActJiugonggeAwards> queryAwardsByName = wxActJiugonggeAwardsDao.queryAwardsByName(jwid, createBy, awardName);
	 		if(queryAwardsByName.size()>0){
	 			return queryAwardsByName.get(0).getId();
	 		}
	 		Integer maxAwardsValue =wxActJiugonggeAwardsDao.getMaxAwardsValueByCreateBy(jwid,createBy);
	 		Integer nextAwardsValue = maxAwardsValue+1;
	 		wxActJiugonggeAwards.setAwardsValue(nextAwardsValue);
	 	}else{
	 		List<WxActJiugonggeAwards> queryAwardsByName = wxActJiugonggeAwardsDao.queryAwardsByName(jwid, "", awardName);
	 		if(queryAwardsByName.size()>0){
	 			return queryAwardsByName.get(0).getId();
	 		}
	 		Integer maxAwardsValue =wxActJiugonggeAwardsDao.getMaxAwardsValue(jwid);
	 		Integer nextAwardsValue = maxAwardsValue+1;
	 		wxActJiugonggeAwards.setAwardsValue(nextAwardsValue);
	 	}
	 	wxActJiugonggeAwards.setCreateBy(createBy);
	 	wxActJiugonggeAwards.setJwid(jwid);
		wxActJiugonggeAwardsDao.add(wxActJiugonggeAwards);
		return  wxActJiugonggeAwards.getId();
	}
	
	/**
	 * @功能:保存奖品
	 * @作者:liwenhui 
	 * @时间:2018-3-28 上午10:00:09
	 * @修改：
	 * @param prizeName
	 * @return  
	 */
	private String savePrizes(String prizeName){
		WxActJiugonggePrizes wxActJiugonggePrizes= new WxActJiugonggePrizes();
		String jwid =  ContextHolderUtils.getSession().getAttribute("jwid").toString();
		String createBy = ContextHolderUtils.getSession().getAttribute("system_userid").toString();
		
		//判断数据库是否已经存在奖品名称
		List<WxActJiugonggePrizes> queryPrizesByName = wxActJiugonggePrizesDao.queryPrizesByName(jwid, createBy, prizeName);
		if(queryPrizesByName.size()>0){
			return queryPrizesByName.get(0).getId();
		}
		
		wxActJiugonggePrizes.setCreateBy(createBy);
		wxActJiugonggePrizes.setJwid(jwid);
		wxActJiugonggePrizes.setName(prizeName);
		wxActJiugonggePrizes.setImg("content/jiugongge/img/defaultGoods.png");
		wxActJiugonggePrizesDao.add(wxActJiugonggePrizes);
		return  wxActJiugonggePrizes.getId();
	}
	
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doEdit(WxActJiugongge wxActJiugongge) {
		wxActJiugonggeDao.update(wxActJiugongge	);
		List<WxActJiugonggeRelation> newAwardsList= wxActJiugongge.getAwarsList();//新的明细配置集合
		List<String> ids=new ArrayList<String>();
		if(newAwardsList!=null){
			for (WxActJiugonggeRelation relation : newAwardsList) {
				//--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:判断奖项是否为空,为空则增加
				if(StringUtils.isEmpty(relation.getAwardId())){
					relation.setAwardId(saveAwards(relation.getAwardName()));
				}else{
					WxActJiugonggeAwards wxActJiugonggeAward = wxActJiugonggeAwardsDao.get(relation.getAwardId());
					//判断awardId和awardName是否是匹配的，不匹配则增加
					if(!wxActJiugonggeAward.getAwardsName().equals(relation.getAwardName())){
						relation.setAwardId(saveAwards(relation.getAwardName()));
					}
				}
				//--update-end--date:2018-3-27 18:14:03 author:liwenhui for:判断奖项是否为空,为空则增加
				//--update-begin--date:2018-3-27 18:14:03 author:liwenhui for:判断奖品是否为空,为空则增加
				if(StringUtils.isEmpty(relation.getPrizeId())){
					relation.setPrizeId(savePrizes(relation.getPrizeName()));
				}else{
					WxActJiugonggePrizes wxActJiugonggePrize = wxActJiugonggePrizesDao.get(relation.getPrizeId());
					//prizeId和prizeName是否是匹配的，不匹配则增加
					if(!wxActJiugonggePrize.getName().equals(relation.getPrizeName())){
						relation.setPrizeId(savePrizes(relation.getPrizeName()));
					}
				}
				//--update-end--date:2018-3-27 18:14:03 author:liwenhui for:判断奖品是否为空,为空则增加
				
				if(StringUtils.isNotEmpty(relation.getId())){				
					ids.add(relation.getId());
				}
			}
			wxActJiugonggeRelationDao.bactchDeleteOldAwards(ids,wxActJiugongge.getId());//批量删除不在新的明细配置集合的数据
			for (WxActJiugonggeRelation wxActJiugonggeRelation : newAwardsList) {
				if(StringUtils.isEmpty(wxActJiugonggeRelation.getId())){
					wxActJiugonggeRelation.setActId(wxActJiugongge.getId());
					if(wxActJiugonggeRelation.getAmount()==null){
						wxActJiugonggeRelation.setAmount(0);
					}
					wxActJiugonggeRelation.setRemainNum(wxActJiugonggeRelation.getAmount());
					wxActJiugonggeRelationDao.add(wxActJiugonggeRelation);
				}else{
					
					WxActJiugonggeRelation jiugonggeRelation = wxActJiugonggeRelationDao.get(wxActJiugonggeRelation.getId());
					if(wxActJiugonggeRelation.getAmount()!=null&&jiugonggeRelation!=null&&jiugonggeRelation.getAmount()!=null){
						if(wxActJiugonggeRelation.getAmount()!=jiugonggeRelation.getAmount()){
							//更新。抛异常
							Integer num= wxActJiugonggeRelation.getAmount()-jiugonggeRelation.getAmount();
							
							wxActJiugonggeRelationDao.updateNum(wxActJiugonggeRelation.getId(),num);
						}
					}
					wxActJiugonggeRelation.setAmount(null);
					wxActJiugonggeRelation.setRemainNum(null);
					wxActJiugonggeRelationDao.update(wxActJiugonggeRelation);
				}
			}
		}else{
			wxActJiugonggeRelationDao.bactchDeleteOldAwards(ids,wxActJiugongge.getId());//批量删除不在新的明细配置集合的数据
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void doDelete(String id) {
		wxActJiugonggeDao.delete(id);
		wxActJiugonggeRelationDao.batchDeleteByActId(id);//同步活动明细配置
		baseApiActTxtService.batchDeleteByActCode(id);//同步删除系统文本
	}

	@Override
	public WxActJiugongge queryById(String id) {
		WxActJiugongge wxActJiugongge  = wxActJiugonggeDao.get(id);
		return wxActJiugongge;
	}

	@Override
	public PageList<WxActJiugongge> queryPageList(
		PageQuery<WxActJiugongge> pageQuery) {
		PageList<WxActJiugongge> result = new PageList<WxActJiugongge>();
		Integer itemCount = wxActJiugonggeDao.count(pageQuery);
		List<WxActJiugongge> list = wxActJiugonggeDao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public List<WxActJiugongge> queryActs(String jwid) {
		// TODO Auto-generated method stub
		return wxActJiugonggeDao.queryActs(jwid);
	}

	@Override
	public void doUpdateShortUrl(String id, String shortUrl) {
		wxActJiugonggeDao.doUpdateShortUrl(id, shortUrl);
	}
	
}
