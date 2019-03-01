package com.jeecg.p3.shaketicket.web.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jeecgframework.p3.core.common.utils.DateUtil;

import com.jeecg.p3.shaketicket.annotation.Excel;

public class ShaketicketExcelDto {
	@Excel(exportName="openid", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String openid;
	@Excel(exportName="奖品名称", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String awardsName;
	@Excel(exportName="中奖时间", exportConvertSign = 1, exportFieldWidth = 30, importConvertSign = 0)
	private Date awardsTime;
	@Excel(exportName="领奖密码", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String psd;
	@Excel(exportName="领取状态", exportConvertSign = 1, exportFieldWidth = 30, importConvertSign = 0)
	private String status;
	@Excel(exportName="手机号", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String mobile;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAwardsName() {
		return awardsName;
	}
	public void setAwardsName(String awardsName) {
		this.awardsName = awardsName;
	}
	
	public Date getAwardsTime() {
		return awardsTime;
	}
	public void setAwardsTime(Date awardsTime) {
		this.awardsTime = awardsTime;
	}
	public String getPsd() {
		return psd;
	}
	public void setPsd(String psd) {
		this.psd = psd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "ShaketicketExcelVo [openid=" + openid + ", awardsName="
				+ awardsName + ", awardsTime=" + awardsTime + ", psd=" + psd
				+ ", status=" + status + ", mobile=" + mobile + "]";
	}
	public String getAwardsTimeConvert(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(this.awardsTime);
	}
	public String getStatusConvert(){
		if(this.status.equals("0")){
			return "未领取";
		}else{
			return "已领取";
		}
	}
}
