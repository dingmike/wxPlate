package com.jeecg.p3.shaketicket.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jeecgframework.p3.base.vo.WeixinDto;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecg.p3.shaketicket.dao.WxActShaketicketAwardDao;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketConfigDao;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketCouponDao;
import com.jeecg.p3.shaketicket.dao.WxActShaketicketRecordDao;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketAward;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketRecord;
import com.jeecg.p3.shaketicket.service.WxActShaketicketRecordService;
import com.jeecg.p3.shaketicket.util.ExcelUtil;
import com.jeecg.p3.shaketicket.web.dto.ShaketicketExcelDto;

@Service("wxActShaketicketRecordService")
public class WxActShaketicketRecordServiceImpl implements WxActShaketicketRecordService {
	@Resource
	private WxActShaketicketRecordDao wxActShaketicketRecordDao;
	@Resource
	private WxActShaketicketAwardDao wxActShaketicketAwardDao;
	@Resource
	private WxActShaketicketConfigDao wxActShaketicketConfigDao;
	@Resource
	private WxActShaketicketCouponDao wxActShaketicketCouponDao;
	
	@Override
	public void doAdd(WxActShaketicketRecord wxActShaketicketRecord) {
		wxActShaketicketRecordDao.add(wxActShaketicketRecord);
	}

	@Override
	public void doEdit(WxActShaketicketRecord wxActShaketicketRecord) {
		wxActShaketicketRecordDao.update(wxActShaketicketRecord);
	}

	@Override
	public void doDelete(String id) {
		wxActShaketicketRecordDao.delete(id);
	}

	@Override
	public WxActShaketicketRecord queryById(String id) {
		WxActShaketicketRecord wxActShaketicketRecord  = wxActShaketicketRecordDao.get(id);
		return wxActShaketicketRecord;
	}

	@Override
	public PageList<WxActShaketicketRecord> queryPageList(
		PageQuery<WxActShaketicketRecord> pageQuery) {
		PageList<WxActShaketicketRecord> result = new PageList<WxActShaketicketRecord>();
		Integer itemCount = wxActShaketicketRecordDao.count(pageQuery);
		List<WxActShaketicketRecord> list = wxActShaketicketRecordDao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public Map<String, Integer> getRecordCountByActIdAndOpenid(String actId, String openid,Date currDate) {
		// TODO Auto-generated method stub
		return wxActShaketicketRecordDao.getRecordCountByActIdAndOpenid(actId, openid,currDate);
	}

	@Override
	public WxActShaketicketAward creatRecordAndReturnAward(
			WxActShaketicketRecord shaketicketRecord, WeixinDto weixinDto) {
		shaketicketRecord.setActId(weixinDto.getActId());
		shaketicketRecord.setOpenid(weixinDto.getOpenid());
		shaketicketRecord.setJwid(weixinDto.getJwid());
		shaketicketRecord.setDrawTime(new Date());//记录抽奖时间
		shaketicketRecord.setReceiveStatus("0");//初始化为未领奖
		//获取中奖序号
		Integer maxAwardsSeq = wxActShaketicketRecordDao.getMaxAwardsSeq(shaketicketRecord.getActId());
		Integer nextAwardsSeq = maxAwardsSeq+1;
		shaketicketRecord.setAwardsSeq(nextAwardsSeq);
		wxActShaketicketRecordDao.add(shaketicketRecord);
		 if(StringUtils.isEmpty(shaketicketRecord.getAwardId())){
			 return new WxActShaketicketAward();			 
		 }else{
			 wxActShaketicketConfigDao.updateRemainNum(weixinDto.getActId(), weixinDto.getJwid(), shaketicketRecord.getAwardId());
			 return wxActShaketicketAwardDao.get(shaketicketRecord.getAwardId());
		 }
	}

	@Override
	public List<WxActShaketicketRecord> queryMyAwardsRecordByOpenidAndActid(
			String openid, String actId) {
		// TODO Auto-generated method stub
		return wxActShaketicketRecordDao.queryMyAwardsRecordByOpenidAndActid(openid, actId);
	}

	@Override
	public InputStream exportExcel(String actId)
			throws FileNotFoundException {
	    List<WxActShaketicketRecord> listAwards = wxActShaketicketRecordDao.queryBargainAwardsByActId(actId);  
	    File file = new File(new Date(0).getTime()+".xls");  
	    OutputStream outputStream;
			outputStream = new FileOutputStream(file);
	    ExcelUtil.exportExcel("导出信息", WxActShaketicketRecord.class, listAwards, outputStream);  
	    InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));  
	    file.delete();        
	    return is;  
	}
	@Override
	public InputStream exportExcelWin(String actId)throws FileNotFoundException {
		List<WxActShaketicketRecord> listAwards = wxActShaketicketRecordDao.queryBargainAwardsByActIdForWin(actId);  
		ShaketicketExcelDto shaketicketExcelDto=null;
		List<ShaketicketExcelDto> list=new ArrayList<ShaketicketExcelDto>();
		for(WxActShaketicketRecord wxActShaketicketRecord:listAwards){
			shaketicketExcelDto=new ShaketicketExcelDto();
			shaketicketExcelDto.setAwardsName(wxActShaketicketRecord.getAwardsName());
			shaketicketExcelDto.setAwardsTime(wxActShaketicketRecord.getDrawTime());
			shaketicketExcelDto.setMobile(wxActShaketicketRecord.getMobile());
			shaketicketExcelDto.setOpenid(wxActShaketicketRecord.getOpenid());
			shaketicketExcelDto.setPsd(wxActShaketicketRecord.getCardPsd());
			shaketicketExcelDto.setStatus(wxActShaketicketRecord.getReceiveStatus());
			list.add(shaketicketExcelDto);
		}
		File file = new File(new Date(0).getTime()+".xls");  
		OutputStream outputStream;
		outputStream = new FileOutputStream(file);
		ExcelUtil.exportExcel("摇一摇中奖记录", ShaketicketExcelDto.class, list, outputStream);  
		InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));  
		file.delete();        
		return is;  
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void creatRecord(
			WxActShaketicketRecord shaketicketRecord, WeixinDto weixinDto,WxActShaketicketAward award,WxActShaketicketCoupon coupon) {
		shaketicketRecord.setActId(weixinDto.getActId());
		shaketicketRecord.setOpenid(weixinDto.getOpenid());
		shaketicketRecord.setJwid(weixinDto.getJwid());
		shaketicketRecord.setDrawTime(new Date());//记录中奖奖时间
		shaketicketRecord.setReceiveStatus("0");//初始化为未领奖
		//获取中奖序号
		Integer maxAwardsSeq = wxActShaketicketRecordDao.getMaxAwardsSeq(shaketicketRecord.getActId());
		Integer nextAwardsSeq = maxAwardsSeq+1;
		shaketicketRecord.setAwardsSeq(nextAwardsSeq);
	    wxActShaketicketRecordDao.add(shaketicketRecord);
	    wxActShaketicketConfigDao.updateRemainNum(weixinDto.getActId(), weixinDto.getJwid(), shaketicketRecord.getAwardId());
		//更新卡券中奖状态
	    //TODO 所有券码都变成已使用
	    coupon.setStatus("1");//已使用
	    wxActShaketicketCouponDao.updateStatus(coupon.getId());
//	    if("A001".equals(award.getCardId())){
//	    	coupon.setStatus("1");//已使用
//		    wxActShaketicketCouponDao.updateStatus(coupon.getId());
//	    }
	}
	
}
