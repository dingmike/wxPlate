$(function(){
	WXC_SHARE.link = location.href.split('shake')[0] + 'shake-255-014c2c182d320d29.html';
	WXC_SHARE.desc = '本活动仅做功能演示之用';
	reload_wxshare_params();
	if (ISWP){ WXC.msgtips('暂不支持那1%的WP设备'); return; }	
	shakeEvent = new Shake({ threshold:10, timeout:1000 });
	shakeEvent.start();
	window.addEventListener('shake', gift_shake, false);
	
	$('#rule-btn').click(function(){
		shakeEvent.stop();
		var dlg = box_dialog({ title:'活动规则', content:$('#rule').html() });
		dlg.find('.foot .-gifts').text('关闭').click(function(){
			dlg.remove();
			shakeEvent.start();
		});
	});
	
	var touchstart_is = 0;
	$('body').on('touchstart', function(){
		if (touchstart_is == 1) return;
		touchstart_is = 1;
		$('#audio_shake')[0].play();
		$('#audio_shake')[0].pause();
	});
});

var sbox;
var gift_shake = function(){
	shakeEvent.stop();
	$('#audio_shake')[0].play();
	var vargs = [[-21,100], [0,300], [-21,600], [0,850]];
	$(vargs).each(function(){
		var _this = this;
		setTimeout(function(){ $('.shake-box .hand').rotate(_this[0]); }, _this[1]);
	});
	setTimeout(function(){ sbox = $('<div class="msgtips search"><div>正在搜寻奖品...</div></div>').appendTo(document.body); }, 1200);
	setTimeout(function(){
		/*		if (!!act_errmsg){
					if (!!sbox) sbox.remove();
					shakeEvent.start();
					WXC.msgtips(act_errmsg);
					return;
				}*/
		   shakeTicket();
	    }, 2500 + ~~(Math.random() * 1500));

};
function shakeTicket() {
	var actId = $("#actId").val();
	var openid = $("#openid").val();
	var subscribe = $("#subscribe").val();
	var jwid = $("#jwid").val();
	var url = "../shaketicket/shakeTicket.do";
	$.getJSON(url, {"actId": actId,"openid" : openid,"subscribe":subscribe,"jwid" : jwid}, function(data,status,xhr){	
		if (!!sbox) sbox.remove();
		if (data.success) {
			try {
				var drawStatus = data.attributes.shaketicketRecord.drawStatus;//抽奖状态
				var awardsName = data.attributes.shaketicketAward.awardsName;//奖品名称
				var jwid = data.attributes.shaketicketAward.jwid;//奖品名称
				var owner = data.attributes.shaketicketAward.owner;//发奖公司
				var cardId = data.attributes.shaketicketAward.cardId;//卡券ID
				var isCard = data.attributes.shaketicketAward.isCard;//是否卡券
				var img = data.attributes.shaketicketAward.img;//奖品图片
			} catch (e) {
			}
			
			if(drawStatus == '0'){//未中奖					
				var msg = "真遗憾，再接再厉";
				if(msg==null||msg==""){
					msg = (NOGIFT_TIPs[~~(Math.random()*NOGIFT_TIPs.length)]||NOGIFT_TIPs[0]);
				}
				var dlg = box_dialog({ title:'没有摇到~', content:'<p style="margin:0;margin-top:9px;">' + msg + '</p>' });
				dlg.find('.foot .-gifts').text('继续摇').click(function(){
					dlg.remove();
					shakeEvent.start();
				});
				
			}else{//已中奖
				var gift ="";
				var hf ="";
				if(isCard=='1'){//是卡券
					gift = '<div><span style="margin:4px 0;display:inline-block;color:#999">恭喜你获得</span><br><b style="font-size:16px;"></b><br><b style="font-size:16px;">' + awardsName + '</b></div>';
					var dlg = box_dialog2({ title:'恭喜！中奖啦！', content:gift });
					dlg.find('.foot .-gifts').attr('onclick', 'addCard("'+data.attributes.shaketicketRecord.id+'")');
				}else{
					gift = '<div><span style="margin:4px 0;display:inline-block;color:#999">恭喜你获得</span>'
						  +'<br><b style="font-size:16px;"></b><br><b style="font-size:16px;">' + awardsName + '</b><br>'
						  +'<img src="../upload/img/shaketicket/'+jwid+'/'+img+'"  style="width:50%;height:50%;">'
						  +'</div>';
					var dlg = box_dialog({ title:'恭喜！中奖啦！', content:gift });
					dlg.find('.foot .-gifts').text('关闭').click(function(){
						dlg.remove();
						shakeEvent.start();
					});
				}
			}	
			var use_times = $("#count").html();//每天剩余抽奖次数
			if(use_times==null){
				use_times = $("#num").html();//剩余抽奖次数
				use_times--;
				$("#num").html(use_times);
			}else{						
				use_times--;
				$("#count").html(use_times);
			}
		} else {
			if(data.obj=="isNotFoucs"){
				$('#no_focus').show();
				/*var dlg = box_dialog({ title:'友情提示', content:$('#no_focus').html() });
				dlg.find('.foot .-gifts').text('关闭').click(function(){
					dlg.remove();
					shakeEvent.start();
				});*/
    		}else if(data.obj=="isNotBind"){
				var dlg = box_dialog({ title:'友情提示', content:$('#no_binding_phone').html() });
				dlg.find('.foot .-gifts').text('关闭').click(function(){
					dlg.remove();
					shakeEvent.start();
				});
    		}else{   			
				var msg = data.msg;
				var dlg = box_dialog({ title:'友情提示~', content:'<p style="margin:0;margin-top:9px;">' + msg + '</p>' });
				dlg.find('.foot .-gifts').text('关闭').click(function(){
					dlg.remove();
					shakeEvent.start();
				});
    		}
		}
	});

}  

//初始化下标
function resetIndex(idv) {
	$box = $("#"+idv+"");
	$box.find("tr").each(function(i){
		$(this).find("td").each(function(){
			var $this = $(this).children(), name = $this.attr('name'), val = $this.val();
			if(name!=null){
				if (name.indexOf("#index#") >= 0){
					$this.attr("name",name.replace('#index#',i));
				}else{
					var s = name.indexOf("[");
					var e = name.indexOf("]");
					var new_name = name.substring(s+1,e);
					$this.attr("name",name.replace(new_name,i));
				}
			}
		});
	});	
}

function popup(id) {
	$("#" + id).show();
}

function popuphide(id) {
	$("#" + id).hide();
}
Date.prototype.format=function(format){
	if(!format){
		format="yyyy年MM月dd日 HH:mm:ss";
	}
	var paddNum = function(num){
		num += "";
		return num.replace(/^(\d)$/,"0$1");
	}
	 var cfg = {
		yyyy :this.getFullYear()//年
		,yy : this.getFullYear().toString().substring(2)//年
		,M  : this.getMonth() + 1  //月
		,MM : paddNum(this.getMonth() + 1) //月
		,d  : this.getDate()//日
		,dd : paddNum(this.getDate())//日
		,HH : paddNum(this.getHours())//时
		,mm : paddNum(this.getMinutes())//分
		,ss : paddNum(this.getSeconds())//秒
	};
	return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
}
function clwz(content){
	if(!content){
		return "";
	}
	if(content.length>5){
		return content.substring(0,5)+"...";
	}
	return content;
}