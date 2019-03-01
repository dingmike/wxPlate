function toDetail(){  
	var actId = $("#actId").val();
	var jwid = $("#jwid").val();
	var url = "../../shaketicket/act/detail.do";
	url = url + "?actId=" + actId+ "&jwid=" + jwid;
	location.href=url;
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