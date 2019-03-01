package com.jeecg.p3.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jeecgframework.p3.core.logger.Logger;
import org.jeecgframework.p3.core.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weixin.util.redis.JedisPoolUtil;
import com.jeecg.p3.commonweixin.entity.MyJwWebJwid;
import com.jeecg.p3.commonweixin.util.AccessTokenUtil;
import com.jeecg.p3.system.service.MyJwWebJwidService;
import com.jeecg.p3.weixinInterface.entity.WeixinAccount;

@Service
public class RefreshTokenTask {
	public final static Logger LOG = LoggerFactory.getLogger(RefreshTokenTask.class);
	@Autowired
	private MyJwWebJwidService myJwWebJwidService;
	
	public void run() {
		LOG.info("===================重置AccseeToken定时任务开启==========================");
		long start = System.currentTimeMillis();
		try {
			Date date = new Date();
			long time= date.getTime()-1000*60*90;
			Date refDate = new Date(time);
			List<MyJwWebJwid> myJwWebJwids = myJwWebJwidService.queryResetTokenList(refDate);
			for(MyJwWebJwid myJwWebJwid:myJwWebJwids){
				try {
					Map<String, Object> map = AccessTokenUtil.getAccseeToken(myJwWebJwid.getWeixinAppId(), myJwWebJwid.getWeixinAppSecret());
					if(map!=null && map.containsKey("status")){
						if("success".equals(map.get("status"))){
							String accessToken = map.get("accessToken").toString();
							myJwWebJwid.setAccessToken(accessToken);
							myJwWebJwid.setTokenGetTime(date);
							//获取api(卡券用)
							myJwWebJwid.setApiTicket(map.get("apiTicket").toString());
							myJwWebJwid.setApiTicketTime(date);
							//获取jsapi(JS-SDK用)
							myJwWebJwid.setJsApiTicket(map.get("jsApiTicket").toString());
							myJwWebJwid.setJsApiTicketTime(date);
							
							myJwWebJwidService.doEdit(myJwWebJwid);
							//-------H5平台独立公众号，重置redis缓存-------------------------------------------
							try {
								WeixinAccount po = new WeixinAccount();
								po.setAccountappid(myJwWebJwid.getWeixinAppId());
								po.setAccountappsecret(myJwWebJwid.getWeixinAppSecret());
								po.setAccountaccesstoken(myJwWebJwid.getAccessToken());
								po.setAddtoekntime(myJwWebJwid.getTokenGetTime());
								po.setAccountnumber(myJwWebJwid.getWeixinNumber());
								po.setApiticket(myJwWebJwid.getApiTicket());
								po.setApiticketttime(myJwWebJwid.getApiTicketTime());
								po.setAccounttype(myJwWebJwid.getAccountType());
								po.setWeixinAccountid(myJwWebJwid.getJwid());//原始ID
								po.setJsapiticket(myJwWebJwid.getJsApiTicket());
								po.setJsapitickettime(myJwWebJwid.getJsApiTicketTime());
								JedisPoolUtil.putWxAccount(po);
							} catch (Exception e) {
								LOG.error("----------定时任务：H5平台独立公众号，重置redis缓存token失败-------------"+e.toString());
							}
							//--------H5平台独立公众号，重置redis缓存---------------------------------------
						}
					}
				} catch (Exception e) {
					LOG.info("重置AccseeToken定时任务异常e={}",new Object[]{e});
				}
			}
		} catch (Exception e) {
			LOG.info("重置AccseeToken定时任务异常e={}",new Object[]{e});
		}
		LOG.info("===================重置AccseeToken定时任务结束，用时={}ms.==========================",new Object[]{System.currentTimeMillis()-start});
	}
}
