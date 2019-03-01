package com.jeecg.p3.jiugongge.entity;

import java.util.Date;

import org.jeecgframework.p3.core.common.utils.DateUtil;
import org.jeecgframework.p3.core.utils.persistence.Entity;

import com.jeecg.p3.jiugongge.annotation.Excel;

/**
 * 描述：</b>WxActJiugonggeRecord:抽奖记录<br>
 * @author junfeng.zhou
 * @since：2015年11月20日 15时37分02秒 星期五 
 * @version:1.0
 */
public class WxActJiugonggeRecord implements Entity<String> {
	private static final long serialVersionUID = 1L;
	
	/**
	 *记录id
	 */
	private String id;
	/**
	 *
	 */
	private String actId;
	/**
	 *openid
	 */
	@Excel(exportName="openid", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String openid;
	/**
	 *昵称
	 */
	@Excel(exportName="昵称", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String nickname;
	/**
	 *抽奖时间
	 */
	@Excel(exportName="抽奖时间", exportConvertSign = 1, exportFieldWidth = 30, importConvertSign = 0)
	private Date recieveTime;
	/**
	 *奖项
	 */
	
	private String awardsId;
	/**
	 *收货人姓名
	 */
	@Excel(exportName="收货人姓名", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String realname;
	/**
	 *手机号
	 */
	@Excel(exportName="手机号", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String phone;
	/**
	 *地址
	 */
	@Excel(exportName="地址", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String address;
	/**
	 *对应微信平台原始id
	 */
	private String jwid;
	private String jwidName;
	private String actName;
	private Integer seq;
	//update-begin--Author:zhangweijian  Date: 20180413 for:新增领奖状态，兑奖码，中奖状态，中奖时间字段
	/**
	 * 领奖状态：'0':否；'1':是
	 */
	private String recieveStatus;
	/**
	 * 兑奖码
	 */
	private String awardCode;
	/**
	 * 中奖状态： '0':否；'1':是 
	 */
	private String awardStatus;
	/**
	 * 中奖时间 
	 */
	private Date awardTime;
	//update-end--Author:zhangweijian  Date: 20180413 for:新增领奖状态，兑奖码，中奖状态，中奖时间字段
	private String verifyId;
	public String getVerifyId() {
		return verifyId;
	}
	public void setVerifyId(String verifyId) {
		this.verifyId = verifyId;
	}
	public Date getAwardTime() {
		return awardTime;
	}
	public void setAwardTime(Date awardTime) {
		this.awardTime = awardTime;
	}
	public String getRecieveStatus() {
		return recieveStatus;
	}
	public void setRecieveStatus(String recieveStatus) {
		this.recieveStatus = recieveStatus;
	}
	public String getAwardCode() {
		return awardCode;
	}
	public void setAwardCode(String awardCode) {
		this.awardCode = awardCode;
	}
	public String getAwardStatus() {
		return awardStatus;
	}
	public void setAwardStatus(String awardStatus) {
		this.awardStatus = awardStatus;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	/**
	 *奖项名称
	 */
	@Excel(exportName="奖项", exportConvertSign = 0, exportFieldWidth = 30, importConvertSign = 0)
	private String awardsName;
	public String getId() {
	    return this.id;
	}
	public void setId(String id) {
	    this.id=id;
	}
	public String getActId() {
	    return this.actId;
	}
	public void setActId(String actId) {
	    this.actId=actId;
	}
	public String getOpenid() {
	    return this.openid;
	}
	public void setOpenid(String openid) {
	    this.openid=openid;
	}
	public String getNickname() {
	    return this.nickname;
	}
	public void setNickname(String nickname) {
	    this.nickname=nickname;
	}
	public Date getRecieveTime() {
	    return this.recieveTime;
	}
	public String getRecieveTimeConvert() {
	    return DateUtil.formatDateTime(this.recieveTime, "yyyy-MM-dd HH:mm:ss");
	}
	
	public void setRecieveTime(Date recieveTime) {
	    this.recieveTime=recieveTime;
	}
	public String getAwardsId() {
	    return this.awardsId;
	}
	public void setAwardsId(String awardsId) {
	    this.awardsId=awardsId;
	}
	public String getRealname() {
	    return this.realname;
	}
	public void setRealname(String realname) {
	    this.realname=realname;
	}
	public String getPhone() {
	    return this.phone;
	}
	public void setPhone(String phone) {
	    this.phone=phone;
	}
	public String getAddress() {
	    return this.address;
	}
	public void setAddress(String address) {
	    this.address=address;
	}
	public String getJwid() {
	    return this.jwid;
	}
	public void setJwid(String jwid) {
	    this.jwid=jwid;
	}
	public String getAwardsName() {
		return awardsName;
	}
	public void setAwardsName(String awardsName) {
		this.awardsName = awardsName;
	}
	public String getJwidName() {
		return jwidName;
	}
	public void setJwidName(String jwidName) {
		this.jwidName = jwidName;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	/**
	 * 中奖状态，数据库无该值
	 */
	private String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

