/**
 *  默认值
 *  $("#key").setDefauleValue();
 * 
 */
$.fn.setDefauleValue = function() {
    var defauleValue = $(this).val();
    $(this).val(defauleValue).css("color","#9f9f9f");
 
    return this.each(function() {      
        $(this).focus(function() {
            if ($(this).val() == defauleValue) {
                $(this).css("color","#555555");//输入值颜色
            }
        }).blur(function() {
            if ($(this).val() == "") {
                $(this).val(defauleValue).css("color","#9f9f9f");//默认值颜色
            }
            if ($(this).val() == defauleValue) {
                $(this).css("color","#9f9f9f");//输入值颜色
            }
        });
    });
}

/**
 * 
 * 时间格式化
 * 
 */
Date.prototype.format=function(format){
	if(!format){
		format="yyyy-MM-dd HH:mm:ss";
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