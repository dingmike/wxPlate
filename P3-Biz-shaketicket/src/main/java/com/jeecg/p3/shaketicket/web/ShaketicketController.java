package com.jeecg.p3.shaketicket.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.base.vo.WeixinDto;
import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.logger.Logger;
import org.jeecgframework.p3.core.logger.LoggerFactory;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.jeecgframework.p3.core.utils.common.StringUtils;
import org.jeecgframework.p3.core.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeecg.p3.shaketicket.entity.WxActShaketicketAward;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketCoupon;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketHome;
import com.jeecg.p3.shaketicket.entity.WxActShaketicketRecord;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeException;
import com.jeecg.p3.shaketicket.exception.ShaketicketHomeExceptionEnum;
import com.jeecg.p3.shaketicket.service.WxActShaketicketAwardService;
import com.jeecg.p3.shaketicket.service.WxActShaketicketCouponService;
import com.jeecg.p3.shaketicket.service.WxActShaketicketHomeService;
import com.jeecg.p3.shaketicket.service.WxActShaketicketRecordService;
import com.jeecg.p3.shaketicket.util.LotteryUtil;

@Controller
@RequestMapping("/shaketicket/act")
public class ShaketicketController extends BaseController {
	public final static Logger LOG = LoggerFactory
			.getLogger(ShaketicketController.class);
	@Autowired
	private WxActShaketicketHomeService homeService;
	@Autowired
	private WxActShaketicketRecordService recordService;
	@Autowired
	private WxActShaketicketAwardService awardService;
	@Autowired
	private WxActShaketicketCouponService couponService;
	
	/**
	 * 摇奖
	 * 
	 * @return
	 */
	@RequestMapping(value = "/shake", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void shake(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LOG.info(request, "shake parameter WeixinDto={}.",
				new Object[] { weixinDto });
		long start = System.currentTimeMillis();
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "shaketicket/0830/vm/wzj.vm";
		try {
			// 参数验证
			validateBargainDtoParam(weixinDto);
			// 获取活动信息
			WxActShaketicketHome shaketicket = homeService.queryById(weixinDto
					.getActId());
			velocityContext.put("shaketicket", shaketicket);
			if (shaketicket == null) {
				throw new ShaketicketHomeException(
						ShaketicketHomeExceptionEnum.DATA_NOT_EXIST_ERROR,
						"活动不存在");
			}
			Date currDate = new Date();
			// 根据活动id，访问人openid查询抽奖人中将次数，抽奖次数和每日抽奖次数
			Map<String, Integer> countMap = recordService
					.getRecordCountByActIdAndOpenid(weixinDto.getActId(),
							weixinDto.getOpenid(), currDate);
			int wincount = ((Number) countMap.get("wincount")).intValue();
			// 活动设置为中奖不可继续参与，即一旦中奖不可继续参与
			if (wincount == 0) {// 中将次数为0，表示没有中过奖。可继续参与抽奖
				if ("0".equals(shaketicket.getActiveFlag())) {
					viewName = "shaketicket/0830/vm/nostart.vm";
					LOG.info("用户" + weixinDto.getOpenid() + "未中过奖，活动未开始。活动ID:"
							+ weixinDto.getActId());
				} else {
					// 得到有剩余的活动奖品
					List<WxActShaketicketAward> awards = awardService
							.queryRemainAwardsByActId(weixinDto.getActId());
					// 得到各奖品的概率列表
					List<Double> orignalRates = new ArrayList<Double>();
					if(awards!=null){
						for (WxActShaketicketAward award : awards) {
							orignalRates.add(award.getProbability());
						}
					}
					// 根据概率产生奖品
					int index = LotteryUtil.lottery(orignalRates);
					if (index >= 0) {// 中奖啦
						WxActShaketicketAward award = awards.get(index);
						// 设置用户的中奖记录
						WxActShaketicketRecord record = new WxActShaketicketRecord();
						// 获取领奖密码
						WxActShaketicketCoupon coupon = couponService
								.routeCardId(weixinDto.getActId(),
										award.getId());
						record.setAwardId(award.getId());// 中奖人得到的奖品id
						record.setCardPsd(coupon.getCardPsd());// 中奖人得到的领奖密码
						record.setDrawStatus("1");// 设置为已中奖
						recordService.creatRecord(record, weixinDto, award,
								coupon);// 生成中奖纪录、更新奖品数量，领取密码变为已使用
						viewName = "shaketicket/0830/vm/zj.vm";
						velocityContext.put("record", record);
						velocityContext.put("award", award);
						LOG.info("用户" + weixinDto.getOpenid() + "中奖。密码为："
								+ coupon.getCardPsd() + ",活动ID:"
								+ weixinDto.getActId());
						velocityContext.put("mobileflag", "1");
					} else {
						LOG.info("用户" + weixinDto.getOpenid() + "未中奖。"
								+ "活动ID:" + weixinDto.getActId());
					}
				}
			} else {
				// 有效期内可兑换
				if (currDate.after(shaketicket.getDeadlinetime())) {
					viewName = "shaketicket/0830/vm/shixiao.vm";
					LOG.info("用户" + weixinDto.getOpenid() + "已中过奖，密码已失效。"
							+ "活动ID:" + weixinDto.getActId());
				} else {
					// 已中奖,返回已中奖的中奖记录
					List<WxActShaketicketRecord> recordList = recordService
							.queryMyAwardsRecordByOpenidAndActid(
									weixinDto.getOpenid(), weixinDto.getActId());
					if(StringUtils.isEmpty(recordList.get(0).getMobile())){
						velocityContext.put("mobileflag", "1");
					}
					WxActShaketicketAward award = awardService
							.queryById(recordList.get(0).getAwardId());
					viewName = "shaketicket/0830/vm/zj.vm";
					velocityContext.put("record", recordList.get(0));
					velocityContext.put("award", award);
					LOG.info("用户" + weixinDto.getOpenid() + "已中过奖，进入中奖页面。"
							+ "活动ID:" + weixinDto.getActId());
				}
			}
		} catch (ShaketicketHomeException e) {
			e.printStackTrace();
			LOG.error("shake error:{}", e.getMessage());
			viewName = "shaketicket/0830/vm/error.vm";
			velocityContext.put("errCode", e.getDefineCode());
			velocityContext.put("errMsg", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("shake error:{}", e);
			viewName = "shaketicket/0830/vm/error.vm";
			velocityContext.put("errCode",
					ShaketicketHomeExceptionEnum.SYS_ERROR.getErrCode());
			velocityContext.put("errMsg",
					ShaketicketHomeExceptionEnum.SYS_ERROR.getErrChineseMsg());
		}
		velocityContext.put("weixinDto",weixinDto);
		velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);
		velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);
		velocityContext.put(
				"signature",
				WeiXinHttpUtil.getRedisSignature(request,
						weixinDto.getJwid()));
		ViewVelocity.view(request, response, viewName, velocityContext);
		LOG.info(request, "shake time={}ms.",
				new Object[] { System.currentTimeMillis() - start });
	}

	private void validateBargainDtoParam(WeixinDto weixinDto) {
		if (StringUtils.isEmpty(weixinDto.getActId())) {
			throw new ShaketicketHomeException(
					ShaketicketHomeExceptionEnum.ARGUMENT_ERROR, "活动ID不能为空");
		}
		if (StringUtils.isEmpty(weixinDto.getOpenid())) {
			throw new ShaketicketHomeException(
					ShaketicketHomeExceptionEnum.ARGUMENT_ERROR,
					"参与人openid不能为空");
		}
		if (StringUtils.isEmpty(weixinDto.getJwid())) {
			throw new ShaketicketHomeException(
					ShaketicketHomeExceptionEnum.ARGUMENT_ERROR, "微信原始id不能为空");
		}
	}
	/**
	 * 更新手机号
	 * @param weixinDto
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updateRecord", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public AjaxJson updateRecord(HttpServletRequest request, HttpServletResponse response){
		AjaxJson j=new AjaxJson();
		try {
			String mobile = request.getParameter("mobile");
			String recodeId=request.getParameter("recodeId");
			WxActShaketicketRecord wxActShaketicketRecord = recordService.queryById(recodeId);
			wxActShaketicketRecord.setMobile(mobile);
			recordService.doEdit(wxActShaketicketRecord);
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
		}
		return j;
	}
	/**
	 * 跳转到活动说明页
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/detail", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void detail(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// ====================================================================================================
		// 装载微信所需参数
		String jwid = weixinDto.getJwid();
		String actId = weixinDto.getActId();
		// ====================================================================================================
		VelocityContext velocityContext;
		String viewName = "shaketicket/0830/vm/detail.vm";
		velocityContext = new VelocityContext();
		try {
			// 获取活动信息
			WxActShaketicketHome shaketicket = homeService
					.queryById(actId);
			if (shaketicket == null) {
				throw new ShaketicketHomeException(
						ShaketicketHomeExceptionEnum.DATA_NOT_EXIST_ERROR, "活动不存在");
			}
			velocityContext.put("shaketicket", shaketicket);
			// ------微信分享参数-------
			velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);
			velocityContext.put("signature",
					WeiXinHttpUtil.getRedisSignature(request, jwid));
		} catch (ShaketicketHomeException e) {
			e.printStackTrace();
			LOG.error("detail error:{}", e.getMessage());
			viewName = "shaketicket/0830/vm/error.vm";
			velocityContext.put("errCode", e.getDefineCode());
			velocityContext.put("errMsg", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("detail error:{}", e);
			viewName = "shaketicket/0830/vm/error.vm";
			velocityContext.put("errCode",
					ShaketicketHomeExceptionEnum.SYS_ERROR.getErrCode());
			velocityContext.put("errMsg",
					ShaketicketHomeExceptionEnum.SYS_ERROR.getErrChineseMsg());
		}
		// ------微信分享参数-------
		ViewVelocity.view(request, response, viewName, velocityContext);
	}
}
