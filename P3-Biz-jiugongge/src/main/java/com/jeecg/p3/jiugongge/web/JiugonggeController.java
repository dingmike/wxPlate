package com.jeecg.p3.jiugongge.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.jeecgframework.p3.base.vo.WeixinDto;
import org.jeecgframework.p3.core.common.utils.AjaxJson;
import org.jeecgframework.p3.core.common.utils.DateUtil;
import org.jeecgframework.p3.core.common.utils.RandomUtils;
import org.jeecgframework.p3.core.logger.Logger;
import org.jeecgframework.p3.core.logger.LoggerFactory;
import org.jeecgframework.p3.core.util.PropertiesUtil;
import org.jeecgframework.p3.core.util.WeiXinHttpUtil;
import org.jeecgframework.p3.core.util.plugin.ViewVelocity;
import org.jeecgframework.p3.core.utils.common.StringUtils;
import org.jeecgframework.p3.core.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jeecg.p3.baseApi.service.BaseApiJwidService;
import com.jeecg.p3.baseApi.service.BaseApiSystemService;
import com.jeecg.p3.dict.service.SystemActTxtService;
import com.jeecg.p3.jiugongge.entity.WxActJiugongge;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggePrizes;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRecord;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRegistration;
import com.jeecg.p3.jiugongge.entity.WxActJiugonggeRelation;
import com.jeecg.p3.jiugongge.exception.JiugonggeException;
import com.jeecg.p3.jiugongge.exception.JiugonggeExceptionEnum;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeAwardsService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggePrizesService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeRecordService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeRegistrationService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeRelationService;
import com.jeecg.p3.jiugongge.service.WxActJiugonggeService;
import com.jeecg.p3.jiugongge.util.EmojiFilter;
import com.jeecg.p3.jiugongge.util.LotteryUtil;
import com.jeecg.p3.jiugongge.verify.entity.WxActJiugonggeVerify;
import com.jeecg.p3.jiugongge.verify.service.WxActJiugonggeVerifyService;

/**
 * 描述：九宫格
 * 
 * @author junfeng.zhou
 * @since：2015年08月06日 18时46分35秒 星期四
 * @version:1.0
 */
@Controller
@RequestMapping("/jiugongge")
public class JiugonggeController extends BaseController {

	public final static Logger LOG = LoggerFactory
			.getLogger(JiugonggeController.class);
	@Autowired
	private WxActJiugonggeService wxActJiugonggeService;
	@Autowired
	private WxActJiugonggeRelationService wxActJiugonggeRelationService;
	@Autowired
	private WxActJiugonggePrizesService wxActJiugonggePrizesService;
	@Autowired
	private WxActJiugonggeRegistrationService wxActJiugonggeRegistrationService;
	@Autowired
	private WxActJiugonggeRecordService wxActJiugonggeRecordService;
	@Autowired
	private WxActJiugonggeAwardsService wxActJiugonggeAwardsService;
	@Autowired
	private SystemActTxtService systemActTxtService;
	@Autowired
	private BaseApiJwidService baseApiJwidService;
	@Autowired
	private WxActJiugonggeVerifyService VerifyService;
	@Autowired
	private BaseApiSystemService baseApiSystemService;
	@Autowired
	private static String VerificationUrl="";
	static {
	PropertiesUtil p=new PropertiesUtil("jiugongge.properties");
		VerificationUrl=p.readProperty("VerificationUrl");
	}
	/**
	 * 跳转到活动首页
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/toIndex", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void toIndex(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws Exception {
		LOG.info(request, "toIndex parameter WeixinDto={}.",
				new Object[] { weixinDto });
		long start = System.currentTimeMillis();
		// 装载微信所需参数
		String jwid = weixinDto.getJwid();
		String appid = weixinDto.getAppid();
		String actId = weixinDto.getActId();
		if (weixinDto.getOpenid() != null) {
			String nickname = WeiXinHttpUtil.getNickName(weixinDto.getOpenid(),jwid);
			weixinDto.setNickname(EmojiFilter.filterEmoji(nickname));
		}
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "jiugongge/vm/index.vm";
		WxActJiugongge wxActJiugongge=null;
		try {
			// 参数验证
			validateWeixinDtoParam(weixinDto);
			// 获取活动信息
			wxActJiugongge = wxActJiugonggeService
					.queryById(weixinDto.getActId());
			if (wxActJiugongge == null) {
				throw new JiugonggeException(
						JiugonggeExceptionEnum.DATA_NOT_EXIST_ERROR, "活动不存在");
			}
			velocityContext.put("bargain", wxActJiugongge);
			// 有效期内可参与
			Date currDate = new Date();
			if (currDate.before(wxActJiugongge.getStarttime())) {
				String begainTime = DateUtil.convertToShowTime(wxActJiugongge
						.getStarttime());
				throw new JiugonggeException(
						JiugonggeExceptionEnum.ACT_BARGAIN_NO_START,
						"活动未开始,开始时间为" + begainTime + ",请耐心等待！");
			}
			if (currDate.after(wxActJiugongge.getEndtime())) {
				throw new JiugonggeException(
						JiugonggeExceptionEnum.ACT_BARGAIN_END, "活动已结束");
			}
			// 活动奖品
			List<WxActJiugonggePrizes> wxActJiugonggePrizesList = wxActJiugonggePrizesService
					.queryByActId(weixinDto.getActId());
			Map<String,String> prizeMap =new HashMap<String, String>();
			int i=1;
			for (WxActJiugonggePrizes wxActJiugonggePrizes : wxActJiugonggePrizesList) {								
				prizeMap.put("prizeImg"+i, wxActJiugonggePrizes.getImg());
				i++;
			}
			velocityContext.put("prizeMap", prizeMap);
			velocityContext.put("prizeList", wxActJiugonggePrizesList);
			if(wxActJiugongge.getNumPerDay()==0){//每天次数设置为0，代表不限制每天抽奖次数
				velocityContext.put("perday", 0);
			}
			// 根据访问人openid查询访问人的信息
			WxActJiugonggeRegistration wxActJiugonggeRegistration = wxActJiugonggeRegistrationService
					.queryRegistrationByOpenidAndActIdAndJwid(
							weixinDto.getOpenid(), weixinDto.getActId(),
							weixinDto.getJwid());
			if (wxActJiugonggeRegistration == null) {
				//update-begin--Author:zhangweijian  Date: 20180704 for：判断当前活动参与人数是否已满
				//判断参与人数是否超标
				int count=wxActJiugonggeRecordService.getCountByActId(actId);
				int joinNumLimit=wxActJiugongge.getJoinNumLimit();
				if(joinNumLimit!=0){
					if(count>(joinNumLimit-1)){
						throw new JiugonggeException(
								JiugonggeExceptionEnum.ACT_BARGAIN_NUMBER_FULL, "参与人数已满，请联系管理员");
					}
				}
				//update-end--Author:zhangweijian  Date: 20180704 for：判断当前活动参与人数是否已满
				wxActJiugonggeRegistration = new WxActJiugonggeRegistration();
				wxActJiugonggeRegistration.setId(RandomUtils.generateID());
				wxActJiugonggeRegistration.setActId(actId);
				wxActJiugonggeRegistration.setOpenid(weixinDto.getOpenid());
				wxActJiugonggeRegistration.setNickname(weixinDto.getNickname());
				wxActJiugonggeRegistration.setCreateTime(new Date());
				wxActJiugonggeRegistration.setAwardsStatus("0");
				wxActJiugonggeRegistration.setAwardsNum(0);
				wxActJiugonggeRegistration.setJwid(wxActJiugongge.getJwid());
				wxActJiugonggeRegistrationService
						.add(wxActJiugonggeRegistration);// 如果当前访问人员不在参与活动的人员表中，则记录到参与活动人员表中
			}
			
			//--update-begin---author:huangqingquan---date:20161125-----for:是否关注可参加---------------
			if("1".equals(wxActJiugongge.getFoucsUserCanJoin())){//如果活动设置了需要关注用户才能参加	
				velocityContext.put("qrcodeUrl", baseApiJwidService.getQrcodeUrl(jwid));
			}
			//--update-end---author:huangqingquan---date:20161125-----for:是否关注可参加---------------
			
			//--update-begin---author:scott---date:20180301-----for:活动首页底部logo支持用户个性化设置---------------
			velocityContext.put("huodong_bottom_copyright", baseApiSystemService.getHuodongLogoBottomCopyright(wxActJiugongge.getCreateBy()));
			//--update-end---author:scott---date:20180301-----for:活动首页底部logo支持用户个性化设置---------------
			
			velocityContext.put("registration", wxActJiugonggeRegistration);
			velocityContext.put("weixinDto", weixinDto);
			velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);
			velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);
			velocityContext.put("hdUrl",wxActJiugongge.getHdurl());
			velocityContext.put("appId", appid);			
			velocityContext.put("signature",WeiXinHttpUtil.getRedisSignature(request, jwid));
			LOG.info(request, "toIndex time={}ms.",
					new Object[] { System.currentTimeMillis() - start });
		} catch (JiugonggeException e) {
			e.printStackTrace();
			LOG.error("toIndex error:{}", e.getMessage());
			//update-begin--Author:zhangweijian  Date: 20180316 for：活动开始结束页面
			viewName = "jiugongge/vm/index.vm";
			if(e.getDefineCode().equals(JiugonggeExceptionEnum.ACT_BARGAIN_NO_START.getErrCode())){
				velocityContext.put("act_Status", "false");
				velocityContext.put("act_Status_Msg", "活动未开始");
			}else if(e.getDefineCode().equals(JiugonggeExceptionEnum.ACT_BARGAIN_END.getErrCode())){
				velocityContext.put("act_Status", "false");
				velocityContext.put("act_Status_Msg", "活动已结束");
			}else if(e.getDefineCode().equals(JiugonggeExceptionEnum.ACT_BARGAIN_NUMBER_FULL.getErrCode())){
				velocityContext.put("act_Status", "false");
				velocityContext.put("act_Status_Msg", "参与人数已满，请联系管理员");
			}else{
				viewName= "system/vm/error.vm";
			}
			//update-end--Author:zhangweijian  Date: 20180316 for：活动开始结束页面
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("toIndex error:{}", e);
			viewName = "system/vm/error.vm";
			velocityContext.put("errCode",
					JiugonggeExceptionEnum.SYS_ERROR.getErrCode());
			velocityContext.put("errMsg",
					JiugonggeExceptionEnum.SYS_ERROR.getErrChineseMsg());
		}
		ViewVelocity.view(request,response,viewName,velocityContext);
	}

	private void validateWeixinDtoParam(WeixinDto weixinDto) {
		if (StringUtils.isEmpty(weixinDto.getActId())) {
			throw new JiugonggeException(JiugonggeExceptionEnum.ARGUMENT_ERROR,
					"活动ID不能为空");
		}
		if (StringUtils.isEmpty(weixinDto.getOpenid())) {
			throw new JiugonggeException(JiugonggeExceptionEnum.ARGUMENT_ERROR,
					"参与人openid不能为空");
		}
		if (StringUtils.isEmpty(weixinDto.getJwid())) {
			throw new JiugonggeException(JiugonggeExceptionEnum.ARGUMENT_ERROR,
					"微信ID不能为空");
		}
	}

	/**
	 * 抽奖
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAwards", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public AjaxJson getAwards(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		LOG.info(request, "getAwards parameter WeixinDto={}.",
				new Object[] { weixinDto });
		// 装载微信所需参数
		String jwid = weixinDto.getJwid();
		String appid = weixinDto.getAppid();
		String actId = weixinDto.getActId();
		try {

			// 参数验证
			validateWeixinDtoParam(weixinDto);
			if (weixinDto.getOpenid() != null) {
				String nickname = WeiXinHttpUtil.getNickName(
						weixinDto.getOpenid(), jwid);
				weixinDto.setNickname(EmojiFilter.filterEmoji(nickname));
			}
			// 获取活动信息
			WxActJiugongge wxActJiugongge = wxActJiugonggeService
					.queryById(weixinDto.getActId());
			
			//--update-begin---author:huangqingquan---date:20161125-----for:是否关注可参加---------------
			if("1".equals(wxActJiugongge.getFoucsUserCanJoin())){//如果活动设置了需要关注用户才能参加	
				//未关注
				weixinDto.setSubscribe("0");
				setWeixinDto(weixinDto);
				 if(!"1".equals(weixinDto.getSubscribe())){
					 j.setSuccess(false);
						j.setObj("isNotFoucs");
						return j;
				 }
			 }
			//--update-end---author:huangqingquan---date:20161125-----for:是否关注可参加---------------
			
			if("1".equals(wxActJiugongge.getBindingMobileCanJoin())){//如果活动设置了需要绑定手机号才能参加				
				// 获取绑定手机号
				String bindPhone = getBindPhone(weixinDto.getOpenid(),jwid);
				// 判断是否绑定了手机号
				if (StringUtils.isEmpty(bindPhone)) {
					j.setSuccess(false);
					j.setObj("isNotBind");
					return j;
				}
			}
			// 判断总抽奖次数是否用完
			Date currDate = new Date();
			List<WxActJiugonggeRecord> bargainRecordList = wxActJiugonggeRecordService
			.queryBargainRecordListByOpenidAndActidAndJwid(
					weixinDto.getOpenid(), weixinDto.getActId(),
					weixinDto.getJwid(), null);
			if (bargainRecordList != null&&wxActJiugongge.getCount()!=null&&wxActJiugongge.getCount()!=0
					&& bargainRecordList.size() >= wxActJiugongge.getCount()) {
				System.err.println(bargainRecordList.size());
				throw new JiugonggeException(
						JiugonggeExceptionEnum.DATA_EXIST_ERROR,systemActTxtService.queryActTxtByCode(
								"controller.exception.nocount",
								weixinDto.getActId()));
			}
			if(wxActJiugongge.getNumPerDay()!= 0){	//每天次数设置为0，代表不限制每天抽奖次数，如果不等于0代表限制了每天抽奖次数	
				bargainRecordList = wxActJiugonggeRecordService
				.queryBargainRecordListByOpenidAndActidAndJwid(
						weixinDto.getOpenid(), weixinDto.getActId(),
						weixinDto.getJwid(), currDate);
				if (bargainRecordList != null
						&& bargainRecordList.size() >= wxActJiugongge
						.getNumPerDay()) {
					throw new JiugonggeException(
							JiugonggeExceptionEnum.DATA_EXIST_ERROR,systemActTxtService.queryActTxtByCode(
									"controller.exception.nownocount",
									weixinDto.getActId()));
				}
			}
			//生成用户的抽奖记录
			WxActJiugonggeRecord wxActJiugonggeRecord = new WxActJiugonggeRecord();
			wxActJiugonggeRecord.setId(RandomUtils.generateID());
			wxActJiugonggeRecord.setActId(weixinDto.getActId());
			wxActJiugonggeRecord.setNickname(weixinDto.getNickname());
			wxActJiugonggeRecord.setOpenid(weixinDto.getOpenid());
			wxActJiugonggeRecord.setJwid(weixinDto.getJwid());
			//update-begin--Author:zhangweijian  Date: 20180413 for:修改抽奖时间,默认未中奖
			wxActJiugonggeRecord.setAwardTime(new Date());
			wxActJiugonggeRecord.setAwardStatus("0");
			//update-begin--Author:zhangweijian  Date: 20180413 for:修改抽奖时间，默认未中奖
			Map<String,Object> map = new HashMap<String,Object>();
			//为用户抽取活动奖品
			if("0".equals(wxActJiugongge.getPrizeStatus())){//中奖可继续参与		
				//活动奖品
				List<WxActJiugonggePrizes> awards = wxActJiugonggePrizesService
				.queryRemainAwardsByActId(weixinDto.getActId());
				//得到各奖品的概率列表
				List<Double> orignalRates = new ArrayList<Double>(awards.size());
				for (WxActJiugonggePrizes award : awards) {
					Integer remainNum = award.getRemainNum();
					Double probability = award.getProbability();
					if (remainNum==null||remainNum <= 0) {//剩余数量为零，需使它不能被抽到
						probability = new Double(0);
					}
					if(probability==null){
						probability = new Double(0);
					}
					orignalRates.add(probability);
				}
				//根据概率产生奖品
				WxActJiugonggePrizes tuple = new WxActJiugonggePrizes();			
				int index = LotteryUtil.lottery(orignalRates);
				if (index>=0) {//中奖啦
					tuple= awards.get(index);
					wxActJiugonggeRecord.setAwardsId(tuple.getAwardId());
					//update-begin--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码，默认领奖状态为0
					wxActJiugonggeRecord.setAwardStatus("1");
					wxActJiugonggeRecord.setRecieveStatus("0");
					String awardCode="";
					for(int i=0;i<3;i++){
						awardCode=getCoupon();
						//判断新生成的兑奖码是否存在
						WxActJiugonggeRecord codeRecord=wxActJiugonggeRecordService.queryByActIdAndawardCode(actId,awardCode);
						if(codeRecord==null){
							break;
						}
						if(i==2){
							throw new JiugonggeException(JiugonggeExceptionEnum.SYS_ERROR);
						}
					}
					wxActJiugonggeRecord.setAwardCode(awardCode);
					//update-end--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码，默认领奖状态为0
					map.put("index", index+1);
				}
			}else{//一旦中奖不可继续参与	
				// 中奖记录
				bargainRecordList  = wxActJiugonggeRecordService
				.queryMyAwardsByOpenidAndActidAndJwid(
						weixinDto.getOpenid(), weixinDto.getActId(),
						weixinDto.getJwid());
				if (bargainRecordList.size()==0) {//未曾中过奖项可继续正常参与抽奖
					//活动奖品
					List<WxActJiugonggePrizes> awards = wxActJiugonggePrizesService
					.queryRemainAwardsByActId(weixinDto.getActId());
					//得到各奖品的概率列表
					List<Double> orignalRates = new ArrayList<Double>(awards.size());
					for (WxActJiugonggePrizes award : awards) {
						Integer remainNum = award.getRemainNum();
						Double probability = award.getProbability();
						if (remainNum==null||remainNum <= 0) {//剩余数量为零，需使它不能被抽到
							probability = new Double(0);
						}
						if(probability==null){
							probability = new Double(0);
						}
						orignalRates.add(probability);
					}
					//根据概率产生奖品
					WxActJiugonggePrizes tuple = new WxActJiugonggePrizes();			
					Integer index = LotteryUtil.lottery(orignalRates);
					if (index!=null&&index>=0) {//中奖啦
						tuple= awards.get(index);
						wxActJiugonggeRecord.setAwardsId(tuple.getAwardId());
						//update-begin--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码
						wxActJiugonggeRecord.setAwardStatus("1");
						wxActJiugonggeRecord.setRecieveStatus("0");
						String awardCode="";
						for(int i=0;i<3;i++){
							awardCode=getCoupon();
							//判断新生成的兑奖码是否存在
							WxActJiugonggeRecord codeRecord=wxActJiugonggeRecordService.queryByActIdAndawardCode(actId,awardCode);
							if(codeRecord==null){
								break;
							}
							if(i==2){
								throw new JiugonggeException(JiugonggeExceptionEnum.SYS_ERROR);
							}
						}
						wxActJiugonggeRecord.setAwardCode(awardCode);
						//update-end--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码
						map.put("index", index+1);
					}
				}				
			}
						
			
			WxActJiugonggePrizes wxActJiugonggePrize = wxActJiugonggeRecordService.creatAwards(wxActJiugonggeRecord);
			j.setSuccess(true);
			String basePath = request.getContextPath();
			map.put("basePath",basePath);
			map.put("wxActJiugonggeRecord",wxActJiugonggeRecord);
			map.put("wxActJiugonggePrize", wxActJiugonggePrize);
			
			j.setAttributes(map);
			
			j.setObj(wxActJiugonggePrize);
		} catch (JiugonggeException e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			LOG.error("bargain error:{}", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("很遗憾，您未中奖!");
			LOG.error("bargain error:{}", e.getMessage());
		}
		return j;
	}

	/**
	 * 跳转到我的奖品
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/myawardrecord", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void myawardrecord(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws Exception {
		LOG.info(request, "myawardrecord parameter WeixinDto={}.",new Object[] { weixinDto });
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "jiugongge/vm/myprizes.vm";
		// 装载微信所需参数
		String jwid = weixinDto.getJwid();
		String appid = weixinDto.getAppid();
		try {
			// 我的中奖记录
			List<WxActJiugonggeRecord> recordList = new ArrayList<WxActJiugonggeRecord>();
			recordList = wxActJiugonggeRecordService
					.queryMyAwardsByOpenidAndActidAndJwid(
							weixinDto.getOpenid(), weixinDto.getActId(),
							weixinDto.getJwid());
			velocityContext.put("recordList", recordList);
			// 获取活动信息
			WxActJiugongge wxActJiugongge = wxActJiugonggeService
					.queryById(weixinDto.getActId());
			velocityContext.put("bargain", wxActJiugongge);
			velocityContext.put("weixinDto", weixinDto);
			
			velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);
			velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);
			velocityContext.put("hdUrl",wxActJiugongge.getHdurl());
			velocityContext.put("appId", appid);
			velocityContext.put("signature",WeiXinHttpUtil.getRedisSignature(request, jwid));
			//update-begin--Author:zhangweijian  Date: 20180314 for：底部logo修改
			velocityContext.put("huodong_bottom_copyright", baseApiSystemService.getHuodongLogoBottomCopyright(wxActJiugongge.getCreateBy()));
			//update-end--Author:zhangweijian  Date: 20180314 for：底部logo修改
		} catch (JiugonggeException e) {
			e.printStackTrace();
			LOG.error("myawardrecord error:{}", e.getMessage());
			viewName=handleCustomException(velocityContext, e);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("myawardrecord error:{}", e);
			viewName = "system/vm/error.vm";
			velocityContext.put("errCode",
					JiugonggeExceptionEnum.SYS_ERROR.getErrCode());
			velocityContext.put("errMsg",
					JiugonggeExceptionEnum.SYS_ERROR.getErrChineseMsg());
		}
		ViewVelocity.view(request,response,viewName,velocityContext);
	}

	/**
	 * 跳转到获奖名单
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/winners", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void winners(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws Exception {
		LOG.info(request, "winners parameter WeixinDto={}.",
				new Object[] { weixinDto });
		VelocityContext velocityContext = new VelocityContext();
		String viewName = "jiugongge/vm/winners.vm";
		// 装载微信所需参数
		String jwid = weixinDto.getJwid();
		String appid = weixinDto.getAppid();
		String actId = weixinDto.getActId();
		try {
			// 获取活动信息
			WxActJiugongge wxActJiugongge = wxActJiugonggeService
					.queryById(weixinDto.getActId());
			// 我的中奖记录
			List<WxActJiugonggeRecord> recordList = new ArrayList<WxActJiugonggeRecord>();
			velocityContext.put("bargain", wxActJiugongge);
			velocityContext.put("weixinDto", weixinDto);
			recordList = wxActJiugonggeRecordService.queryBargainRecordListByActidAndJwid(weixinDto.getActId(), weixinDto.getJwid());
			velocityContext.put("recordList", recordList);
			
			velocityContext.put("nonceStr", WeiXinHttpUtil.nonceStr);
			velocityContext.put("timestamp", WeiXinHttpUtil.timestamp);
			velocityContext.put("hdUrl",wxActJiugongge.getHdurl());
			velocityContext.put("appId", appid);
			velocityContext.put("signature",WeiXinHttpUtil.getSignature(request, jwid));
			//update-begin--Author:zhangweijian  Date: 20180314 for：底部logo修改
			velocityContext.put("huodong_bottom_copyright", baseApiSystemService.getHuodongLogoBottomCopyright(wxActJiugongge.getCreateBy()));
			//update-end--Author:zhangweijian  Date: 20180314 for：底部logo修改
		} catch (JiugonggeException e) {
			e.printStackTrace();
			LOG.error("winners error:{}", e.getMessage());
			viewName=handleCustomException(velocityContext, e);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("winners error:{}", e);
			viewName = "system/vm/error.vm";
			velocityContext.put("errCode",
					JiugonggeExceptionEnum.SYS_ERROR.getErrCode());
			velocityContext.put("errMsg",
					JiugonggeExceptionEnum.SYS_ERROR.getErrChineseMsg());
		}
		ViewVelocity.view(request,response,viewName,velocityContext);
	}
	

	/**
	 * 领奖
	 * 
	 * @return
	 */
	@RequestMapping(value = "/updateRecord", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public AjaxJson updateRecord(@ModelAttribute WxActJiugonggeRecord wxActJiugonggeRecord,
			HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		LOG.info(request, "updateRecord parameter wxActJiugonggeRecord={}.",
				new Object[] { wxActJiugonggeRecord });
		try {
			wxActJiugonggeRecordService.doEdit(wxActJiugonggeRecord);
			j.setSuccess(true);
		} catch (JiugonggeException e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			LOG.error("bargain error:{}", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("领奖失败!");
			LOG.error("bargain error:{}", e.getMessage());
		}
		return j;
	}
	
	private String getBindPhone(String openid, String jwid) {
		String bindPhine = "";
		try {
			JSONObject jsonObj = WeiXinHttpUtil.getUserInfo(openid, jwid);
			LOG.info("getBindPhine json{}.", new Object[] { jsonObj });
			if (jsonObj.containsKey("bindPhoneStatus")) {
				if ("Y".equals(jsonObj.getString("bindPhoneStatus"))) {
					if (jsonObj.containsKey("phone")) {
						bindPhine = jsonObj.getString("phone");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bindPhine;
	}
	/**
	 * 异常处理
	 * @param viewName
	 * @param e
	 */
	public String  handleCustomException(VelocityContext velocityContext,JiugonggeException e){
		velocityContext.put("errMsg", e.getMessage());
		velocityContext.put("errCode", e.getDefineCode());
		if(e.getDefineCode().equals("02007")){
			return "system/vm/before.vm";
		}else if(e.getDefineCode().equals("02008")){
			return "system/vm/over.vm";
		}else{
			return "system/vm/error.vm";
		}
	}
	/**
	 * 微信获取个人信息util
	 * 
	 * @param weixinDto
	 * @return 获取头像 Map两个键headimgurl,fxheadimgurl
	 */
	private Map<String, String> setWeixinDto(WeixinDto weixinDto) {
		log.info("setWeixinDto parameter weixinDto={}",
				new Object[] { weixinDto });
		Map<String, String> map = new HashMap<String, String>();
		try {
			if (weixinDto.getOpenid() != null) {
				JSONObject jsonObj = WeiXinHttpUtil.getGzUserInfo(
						weixinDto.getOpenid(), weixinDto.getJwid());
				log.info("setWeixinDto Openid getGzUserInfo jsonObj={}",
						new Object[] { jsonObj });
				if (jsonObj != null && jsonObj.containsKey("subscribe")) {
					weixinDto.setSubscribe(jsonObj.getString("subscribe"));
				} else {
					weixinDto.setSubscribe("0");
				}
				if (jsonObj != null && jsonObj.containsKey("nickname")) {
					weixinDto.setNickname(jsonObj.getString("nickname"));
				} else {
					weixinDto.setNickname("");
				}
				if (jsonObj != null && jsonObj.containsKey("headimgurl")) {
					map.put("headimgurl", jsonObj.getString("headimgurl"));
				} else {
					map.put("fxheadimgurl", "");
				}
			}
			if (StringUtils.isNotEmpty(weixinDto.getFxOpenid())) {
				JSONObject jsonObj = WeiXinHttpUtil.getGzUserInfo(
						weixinDto.getFxOpenid(), weixinDto.getJwid());
				log.info("setWeixinDto FxOpenid getGzUserInfo jsonObj={}",
						new Object[] { jsonObj });
				if (jsonObj != null && jsonObj.containsKey("nickname")) {
					weixinDto.setFxNickname(jsonObj.getString("nickname"));
				} else {
					weixinDto.setFxNickname("");
				}
				if (jsonObj != null && jsonObj.containsKey("headimgurl")) {
					map.put("fxheadimgurl", jsonObj.getString("headimgurl"));
				} else {
					map.put("fxheadimgurl", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("setWeixinDto e={}",
					new Object[] { e });
		}
		return map;
	}
	
	//update-begin--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码
	/**
	 * @功能：随机生成兑奖码
	 * @return
	 */
	private synchronized static String getCoupon(){
		char ch[]=new char[]{'0','1','2','3','4','5','6','7','8','9',
							'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
							'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
							};
		Random rand = new Random();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<20;i++){
			sb.append(ch[rand.nextInt(62)]);
		}
		return sb.toString();
	}
	//update-end--Author:zhangweijian  Date: 20180413 for:随机生成兑奖码
	
	
	
	/**
	 * 获取二维码地址
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getVerificationUrl", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public AjaxJson  getVerificationUrl(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AjaxJson j=new AjaxJson();
		try{
			String cardPsd=request.getParameter("cardPsd");
			String hdurl= VerificationUrl;
			hdurl=hdurl.replace("STATE", cardPsd);
			PropertiesUtil properties=new PropertiesUtil("jiugongge.properties");
			String shortUrl=WeiXinHttpUtil.getShortUrl(hdurl,properties.readProperty("defaultJwid"));
			LOG.info("二维码生成连接:" + hdurl);
			j.setSuccess(true);
			j.setObj(hdurl);
		}catch(Exception e){
			j.setSuccess(false);
		}
		return j;
	}
	/**
	 *去核销页面
	 * （需要检查扫码人是否有权限）
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/toVerificationreview", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody		
	public void getVerificationreview(@ModelAttribute WeixinDto weixinDto,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 参数验证
		validateWeixinDtoParam(weixinDto);
		String viewName="jiugongge/vm/verificationerror.vm";
		String cardPsd=request.getParameter("awd");
		String actId=weixinDto.getActId();
		String openid=weixinDto.getOpenid();
		if (StringUtils.isEmpty(cardPsd)) {
			throw new JiugonggeException(
					JiugonggeExceptionEnum.DATA_NOT_EXIST_ERROR, "中奖码不能为空");
		}
		VelocityContext velocityContext=new VelocityContext();
		WxActJiugonggeVerify verify=VerifyService.queryByOpenId(openid,actId);
		if(verify!=null&&"0".equals(verify.getStatus())){
			viewName="jiugongge/vm/coupon.vm";
			WxActJiugonggeVerify veri=VerifyService.queryAllJiuGongGe(actId,cardPsd);
			if(veri!=null){
				velocityContext.put("veri", veri);
				velocityContext.put("verify", verify);
			}else{
				viewName="jiugongge/vm/verificationerror.vm";
				 velocityContext.put("isopen", "1");
			}
		}
		// ------微信分享参数-------
		ViewVelocity.view(request, response, viewName, velocityContext);
		
	}
	
	/**
	 *核销
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doVerify", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody		
	public AjaxJson doVerify(@ModelAttribute WxActJiugonggeRecord Record,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AjaxJson j = new AjaxJson();
		try {
			if(StringUtils.isNotEmpty(Record.getActId())&&StringUtils.isNotEmpty(Record.getAwardCode())&&StringUtils.isNotEmpty(Record.getOpenid())){
				WxActJiugonggeRecord recor=wxActJiugonggeRecordService.queryByActIdAndawardCode(Record.getActId(), Record.getAwardCode());
				//获取核销员id
				WxActJiugonggeVerify Verify=VerifyService.queryByOpenId(Record.getOpenid(),Record.getActId());
				if(Verify!=null&&"0".equals(Verify.getStatus())){
					recor.setVerifyId(Verify.getId());
					recor.setRecieveStatus("1");
					recor.setRecieveTime(new Date());
					wxActJiugonggeRecordService.doEdit(recor);
					j.setSuccess(true);
				}
			}else{
				j.setSuccess(false);
				j.setObj("审核失败,参数错误");
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setObj("审核失败,请联系管理员");
		}
		return j;
	}
	
	/**
	 *搜索
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doSearch", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody		
	public AjaxJson doSearch(@ModelAttribute WxActJiugonggeRecord Record,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AjaxJson j = new AjaxJson();
		String awardCode=request.getParameter("search");
		try {
			WxActJiugonggeVerify veri=VerifyService.queryAllJiuGongGe(Record.getActId(),awardCode);
			StringBuffer sb=new StringBuffer();
			sb.append("$('#prizesname').html('"+veri.getName()+"');");
			sb.append("$('#prizesimg').html('<img src=\"/P3-Web/upload/img/jiugongge/"+veri.getJwid()+"/"+veri.getImg()+"\" />');");
			sb.append("$('#jiugonggetitle').html('<span style=\"font-size:1.1em;\">"+veri.getTitle()+"</span>');");
			sb.append("$('#recordawardCode').html('<span style=\"font-weight: bold;font-size:1em;\">"+veri.getAwardCode()+"</span>');");
			if(veri.getRealname()==null){
				sb.append("$('#recordrealname').html('<span style=\"font-weight: bold;\" >姓名:&nbsp;&nbsp;</span>');");
			}else{
				sb.append("$('#recordrealname').html('<span style=\"font-weight: bold;\" >姓名:&nbsp;&nbsp;</span>"+veri.getRealname()+"');");
			}
			if(veri.getPhone()==null){
				sb.append("$('#recordphone').html('<span style=\"font-weight: bold;\" >手机:&nbsp;&nbsp;</span>');");
			}else{
				sb.append("$('#recordphone').html('<span style=\"font-weight: bold;\" >手机:&nbsp;&nbsp;</span>"+veri.getPhone()+"');");
			}
			sb.append("$('#reviewtime').html('");
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			if("0".equals(veri.getRecieveStatus())){
				sb.append("即"+format.format(veri.getEndtime())+"之前有效");
			}else{
				sb.append("核销时间&nbsp;:&nbsp;"+format.format(veri.getRecieveTime())+"");
			}
			sb.append("');");
			sb.append("$('#reviewtext').html('");
			if("0".equals(veri.getRecieveStatus())){
				sb.append("未领取");
			}else{
				sb.append("已核销");
			}
			sb.append("');");
			sb.append("$('#reviewbutn').html('");
			if("0".equals(veri.getRecieveStatus())){
				sb.append("<div id=\"review\" class=\"btn btn-danger\" onclick=\"doVerificationreview();\" style=\"top:35px;width: 80%;left: 10%;height:40px;font-size:20px;line-height: 2;\">核销奖品</div>");
				sb.append("<div id=\"reviewOver\" class=\"btn btn-default btn-default-o order-cancel\" style=\"top:35px;width: 80%;left: 10%;height:40px;font-size:20px;line-height: 2;display:none;\">奖品已核销</div>");
			}else{
				sb.append("<div id=\"review\" class=\"btn btn-danger\" onclick=\"doVerificationreview();\" style=\"top:35px;width: 80%;left: 10%;height:40px;font-size:20px;line-height: 2;display:none;\">核销奖品</div>");
				sb.append("<div id=\"reviewOver\" class=\"btn btn-default btn-default-o order-cancel\" style=\"top:35px;width: 80%;left: 10%;height:40px;font-size:20px;line-height: 2;\">奖品已核销</div>");
			}
			sb.append("<input id=\"awardcode\" name=\"openid\" value=\""+veri.getAwardCode()+"\" type=\"hidden\"/>");
			sb.append("');");
			j.setObj(sb.toString());
			j.setSuccess(true);
		} catch (Exception e) {
			j.setSuccess(false);
		}
		return j;
	}
	
	
	
}
