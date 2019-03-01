package com.jeecg.p3.shaketicket.entity;

import java.util.Date;
import java.math.BigDecimal;
import org.jeecgframework.p3.core.utils.persistence.Entity;

/**
 * 描述：</b>WxActShaketicketCoupon:卡券配置表<br>
 * @author junfeng.zhou
 * @since：2016年03月24日 14时33分55秒 星期四 
 * @version:1.0
 */
public class WxActShaketicketCoupon implements Entity<String> {
	private static final long serialVersionUID = 1L;
		/**	 *	 */	private String id;	/**	 *	 */	private String actId;	/**	 *奖项ID	 */	private String awardsId;	/**	 *卡券密码	 */	private String cardPsd;	/**	 *状态（0:未领取，1:已领取）	 */	private String status;	/**	 *微信原始id	 */	private String jwid;	public String getId() {	    return this.id;	}	public void setId(String id) {	    this.id=id;	}	public String getActId() {	    return this.actId;	}	public void setActId(String actId) {	    this.actId=actId;	}	public String getAwardsId() {	    return this.awardsId;	}	public void setAwardsId(String awardsId) {	    this.awardsId=awardsId;	}	public String getCardPsd() {	    return this.cardPsd;	}	public void setCardPsd(String cardPsd) {	    this.cardPsd=cardPsd;	}	public String getStatus() {	    return this.status;	}	public void setStatus(String status) {	    this.status=status;	}	public String getJwid() {	    return this.jwid;	}	public void setJwid(String jwid) {	    this.jwid=jwid;	}
}

