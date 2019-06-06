<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta content=”width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;” name=”viewport” /> 
<meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no" />
<meta content=”yes” name=”apple-mobile-web-app-capable” />  
<meta content=”black” name=”apple-mobile-web-app-status-bar-style” /> 
<meta content=”telephone=no” name=”format-detection” />
<title>易阅通</title>
<%@ include file="/mobile/tools.jsp"%>
<%@ include file="/mobile/ico.jsp"%>
<link rel="stylesheet" href="${ctx}/css/highlight.css" type="text/css"></link>
<script type="text/javascript" src="${ctx }/js/jquery.highlight.js"></script>
<script type="text/javascript">

//<![data[
$(document).ready(function() {
	//高亮搜索结果
	//high();
	//订阅情况
/* 	$("input[name='isLicenses']").click(function(){
		var lcense = $("#lcense1").val();
		var param = $(this).attr("id");
		if(param=="selectAll"){
			$("#lcense1").val("");
			$("#pageCount").val(10);
			$("#order1").val('');
		}else if(param=="selectLicense"){
			$("#lcense1").val("1");
			$("#pageCount").val(10);
			$("#order1").val('');
		}
		$("input[name='curpage']").val(0);
		if($("#lcense1").val()=="1"){
			document.formListSearch.action="${ctx}/index/searchLicense";
		}else{
			document.formListSearch.action="${ctx}/index/search";
		}
		document.formListSearch.submit();
	}); */
	
	$("li[name='stab1']").click(function(){
		var lcense = $("#lcense1").val();
		$("input[name='isLicenses']").attr("id","selectAll");
		var param = $("input[name='isLicenses']").attr("id");
		if(param=="selectAll"){
			$("#lcense1").val("");
			$("#order1").val('');
		}else if(param=="selectLicense"){
			$("#lcense1").val("1");
			$("#order1").val('');
		}else if(param=="selectFree"){
			$("#lcense1").val("2");
			$("#order1").val('');
		}
		$("input[name='curpage']").val(0);
		document.formListSearch.action="${ctx}/index/search";
		document.formListSearch.submit();
	});
	
	$("li[name='stab2']").click(function(){
		var lcense = $("#lcense1").val();
		$("input[name='isLicenses']").attr("id","selectLicense");
		var param = $("input[name='isLicenses']").attr("id");
		if(param=="selectAll"){
			$("#lcense1").val("");
			$("#order1").val('');
		}else if(param=="selectLicense"){
			$("#lcense1").val("1");
			$("#order1").val('');
		}else if(param=="selectFree"){
			$("#lcense1").val("2");
			$("#order1").val('');
		}
		$("input[name='curpage']").val(0);
		document.formListSearch.action="${ctx}/index/searchLicense";
		document.formListSearch.submit();
	});
	
	$("li[name='stab3']").click(function(){
		var lcense = $("#lcense1").val();
		$("input[name='isLicenses']").attr("id","selectFree");
		var param = $("input[name='isLicenses']").attr("id");
		if(param=="selectAll"){
			$("#lcense1").val("");
			$("#order1").val('');
		}else if(param=="selectLicense"){
			$("#lcense1").val("1");
			$("#order1").val('');
		}else if(param=="selectFree"){
			$("#lcense1").val("2");
			$("#order1").val('');
		}
		$("input[name='curpage']").val(0);
		document.formListSearch.action="${ctx}/index/searchOaFree";
		document.formListSearch.submit();
	});
	//排序
	$("#sort").change(function(){
		sortChange($(this).val());
	});
	$("#sort2").change(function(){
		sortChange($(this).val());
	});
	
	//条件删除
	$("a[name='conditions']").click(function(){
		var lcense = $("#lcense1").val();
		var param = $(this).attr("id");
		if(param=="pubType_label"){
			$("#pubType1").val('');
		}else if(param=="publisher_label"){
			$("#publisher1").val('');
		}else if(param=="pubDate_label"){
			$("#pubDate1").val('');
		}else if(param.indexOf("taxonomy_label")==0){
		/* $("#taxonomy1").val(''); */
		var tax=$(this).text().trim();
		var allTax = $("#taxonomy1").val();
		allTax=allTax.replace(tax,'').replace(',,',',');
		allTax=allTax.replace(/^,+/,'');
		allTax=allTax.replace(/,+$/,'');
		$("#taxonomy1").val(allTax);
		}else if(param=="taxonomyEn_label"){
			$("#taxonomyEn1").val('');
		}else if(param=="language_label"){
			$("#language1").val('');
		}
		$(this).css("display","none");
		$("input[name='curpage']").val(0);
		
		if(${sessionScope.selectType==1}){
			$("#lcense").val("1");
			document.formListSearch.action="${ctx}/index/searchLicense";
		}else if(${sessionScope.selectType==2}){
			$("#lcense").val("2");
			document.formListSearch.action="${ctx}/index/searchOaFree";
		}else{
			document.formListSearch.action="${ctx}/index/search";
		}
		document.formListSearch.submit();
	});	
	//下载列表
	$("#downList").click(function(){
		var url;
		if(${sessionScope.selectType==1}){
	 		url = "${ctx}/index/searchLicenseDownList";
	 	}else if(${sessionScope.selectType==2}){
	 		url = "${ctx}/index/searchOAFreeDownList";
	 	}else{
	 		url = "${ctx}/index/searchDownList";
	 	}
        url += "?searchsType="+$("#type1").val();
        url += "&searchValue="+$("#searchValue1").val();
        url += "&pubType="+$("#pubType1").val();
        url += "&publisher="+$("#publisher1").val();
        url += "&language="+$("#language1").val();
        url += "&pubDate="+$("#pubDate1").val();
        	
        url += "&taxonomy="+$("#taxonomy1").val();
        url += "&taxonomyEn="+$("#taxonomyEn1").val();
        url += "&searchOrder="+$("#order1").val();
        url += "&lcense="+$("#lcense1").val();
        	
        url += "&code="+$("#code1").val();
        url += "&pCode="+$("#pCode1").val();
        url += "&publisherId="+$("#publisherId1").val();
        url += "&subParentId="+$("#subParentId1").val();
        url += "&parentTaxonomy="+$("#parentTaxonomy1").val();
        url += "&parentTaxonomyEn="+$("#parentTaxonomyEn1").val();
        url += "&curPage="+$("#curpage").val();
        url += "&pageCount="+$("#pageCount").val();
        window.location.href=url;
	});
});
function sortChange(v){
	var lcense = $("#lcense1").val();
	var param = v;
	$("#order1").val(param);
	$("input[name='curpage']").val(0);
	if(lcense=="1"){
		document.formListSearch.action="${ctx}/index/searchLicense";
	}else if(lcense=="2"){
		document.formListSearch.action="${ctx}/index/searchOaFree";
	}else{
		document.formListSearch.action="${ctx}/index/search";
	}
	document.formListSearch.submit();
}
//高亮显示
function high(){
	var fullText = new Array();
	var titleext = new Array();
	var authorText = new Array();
	var isbnText = new Array();
	var publisherText = new Array();
	var remarkText = new Array();
	
	fullText = [${form.keyMap['fullText']}];
	titleext = [${form.keyMap['title']}];
	authorText = [${form.keyMap['author']}];
	isbnText = [${form.keyMap['isbn']}];
	publisherText = [${form.keyMap['publisher']}];
	remarkText = [${form.keyMap['remark']}];
	if('${form.searchsType}'=='0'){ 
		$("a[name='title']").highlight(titleext);
		$("td[name='author']").highlight(authorText);
		$("td[name='isbn']").highlight(isbnText);
		$("td[name='publisher']").highlight(publisherText);
		$("td[name='remark']").highlight(remarkText);
	}else if('${form.searchsType}'=='1'){
		$("a[name='title']").highlight(titleext);
	}else if('${form.searchsType}'=='2'){
		$("td[name='author']").highlight(authorText);
	}else if('${form.searchsType}'=='3'){
		$("td[name='isbn']").highlight(isbnText);
	}else if('${form.searchsType}'=='4'){
		$("td[name='publisher']").highlight(publisherText);
	}else{
		$("a[name='title']").highlight(titleext);
		$("td[name='author']").highlight(authorText);
		$("td[name='isbn']").highlight(isbnText);
		$("td[name='publisher']").highlight(publisherText);
		$("td[name='remark']").highlight(remarkText);
	}
}

//在线阅读
 function viewPopTips(id,page,yon) {
	var url="";
	var tmp=window.open("about:blank","","scrollbars=yes,resizable=yes,channelmode") ;          
        //tmp.focus() ;
	if(page=='0'){
		url = "${ctx}/pages/view/form/view?id="+id;
	}else{
		url = "${ctx}/pages/view/form/view?id="+id+"&nextPage="+page;
	}
	//首先Ajax查询要阅读的路径
	if(yon=='2'){
		//window.location.href=url;
		  tmp.location=url;
	}else{
	$.ajax({
		type : "POST",
		async : false,
		url : "${ctx}/mobile/pages/publications/form/getUrl",
		data : {
			id : id,
			nextPage:page,
			r_ : new Date().getTime()
		},
		success : function(data) {
			var s = data.split(";");
			if (s[0] == "success") {
				if(s[1].indexOf('/pages/view/form/view')>=0){
					//window.location.href=s[1];
					tmp.location=s[1];
				}else{
					//window.location.href="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
				    tmp.location="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
				}
			}else if(s[0] == "error"){
				art.dialog.tips(s[1],1,'error');
			}
		},
		error : function(data) {
			art.dialog.tips(data,1,'error');
		}
	});
	}
} 
 function dividePage(val){
	 if(val<0){return;}
	 $("#curpage").val(val);
	 document.formListSearch.submit();
 }
 function GO(obj){
	 $("#pageCount").val($(obj).val());
	 $("#curpage").val(0);
	 //searchByCondition('','');
	 document.formListSearch.submit();
	}
	 
//推荐
function recommends(pid) {
	//先将信息放到对应的标签上title, code, type, pubSubject
	art.dialog.open("${ctx}/pages/recommend/form/edit?pubid="+pid,{title:"<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/>",top: 100,width: '95%', height: '70%',lock:true});
}
//获取资源弹出层调用
function popTips2(pid) {
/* 	showTipsWindown("",
			'simTestContent', $(window).width()*0.6, $(window).height()*0.65); */
			/* alert(pid); */
			art.dialog.open("${ctx}/pages/publications/form/getResource?pubid="+pid,{id : "getResourceId",title:"",top: 200,width: 340, height: 200,lock:true}); /* <ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/> */
}
//购买
function addToCart(pid, ki) {
	var price = $("#price_" + pid).val();
	$.ajax({
		type : "POST",
		url : "${ctx}/pages/cart/form/add",
		data : {
			pubId : pid,
			priceId : price,
			kind : ki,
			r_ : new Date().getTime()
		},
		success : function(data) {
			var s = data.split(":");
			if (s[0] == "success") {
				art.dialog.tips(s[1],1);//location.reload();
				$("#add_"+pid).attr("class", "blank");
				$("#add_"+pid).parent().html("<img class='vm' src='${ctx}/images/ico/ico14-grey.png'><ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang' />");
				$("#cartCount").html("["+s[2]+"]");
				$("#price_" + pid).css("display","none");
			}else{
				art.dialog.tips(s[1],1,'error');
			}
		},
		error : function(data) {
			art.dialog.tips(data,1,'error');
		}
	});
}
//收藏
function favourite2(id) {
	var This = $(this);
	var obj=$("#"+id);
	//This.each(function() {
		$.get("${ctx}/mobile/pages/favourites/form/commit", { pubId : id }, function(data) {
			if ("success" == data) {
				obj.find("a").attr("class", "blank");
				obj.find("img").attr("src", "${ctx}/mobile/images/favourite.png");
				obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
				//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.success' sessionKey='lang' />', 1, 'success');
			} else if ("del" == data) {
				obj.find("a").attr("class", "");
				obj.find("img").attr("src", "${ctx}/mobile/images/unfavourite.png");
				obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />");
				//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.cancel' sessionKey='lang' />', 1, '');
			}
		});
	//});
}
//]]-->
</script>
<script type="text/javascript">
function searchMe(a,type){
	var searchVal=$(a).text();
	var url='';
	if(type=='author'){
		url='${ctx }/index/search?type=2&isAccurate=1&searchValue='+searchVal;
	}else if(type=='publisher'){
		url='${ctx }/index/search?type=2&searchsType2=4&searchValue2='+searchVal;
	}
	window.location.href=url;
}

$(document).ready(function() {isTouchDevice();});

//全局变量，触摸开始位置  
var startX = 0, startY = 0;  
//touchstart事件  
function touchSatrtFunc(evt) {  
try  
		{  
      //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等  
      var touch = evt.touches[0]; //获取第一个触点  
      var x = Number(touch.pageX); //页面触点X坐标  
      var y = Number(touch.pageY); //页面触点Y坐标  
      		//记录触点初始位置  
      startX = x;  
      startY = y;  
      var text = 'TouchStart事件触发：（' + x + ', ' + y + '）';  
      //document.getElementById("result").innerHTML = text;  
 		 }  
  catch (e) {  
      alert('touchSatrtFunc：' + e.message);  
  }  
}  

//touchmove事件，这个事件无法获取坐标  
function touchMoveFunc(evt) {  
try  
		{  
  //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等  
  var touch = evt.touches[0]; //获取第一个触点  
  var x = Number(touch.pageX); //页面触点X坐标  
  var y = Number(touch.pageY); //页面触点Y坐标  

  var text = 'TouchMove事件触发：（' + x + ', ' + y + '）';  

 		//判断滑动方向  
	    var pubDateOrder;
		var shelvesDateOrder;
		var pubDatesuffix;
		var shelvesDatesuffix;
	
	   var curPage =$("#curPage").val();	
	   if(curPage=="" || curPage=="0"){
	   	curPage=1;
	   }
 
		if(/([^\+\-\*\/\(\:]+)$/.test($("#ShelvesDate")[0].src)){
			shelvesDatesuffix=RegExp.$1;
		}
		if(shelvesDatesuffix == "icoDown.png"){
			$("#ShelvesDate").attr('src',"${ctx}/mobile/images/icoUp.png");
			shelvesDateOrder = "asc";
		}else{
			$("#ShelvesDate").attr('src',"${ctx}/mobile/images/icoDown.png");
			shelvesDateOrder = "desc";
		}
		
		if(/([^\+\-\*\/\(\:]+)$/.test($("#pubDate")[0].src)){
			pubDatesuffix=RegExp.$1;
		}
		if(pubDatesuffix == "icoDown.png"){
			$("#pubDate").attr('src',"${ctx}/mobile/images/icoUp.png");
			pubDateOrder = "asc";
		}else{
			$("#pubDate").attr('src',"${ctx}/mobile/images/icoDown.png");
			pubDateOrder = "desc";
		}
  if(startY - y >50){
		text += '<br/>向上滑动';  
		var marginBot = 0;
		if (document.documentElement.scrollTop){
			marginBot = document.documentElement.scrollHeight - (document.documentElement.scrollTop+document.body.scrollTop)-document.documentElement.clientHeight;
		} else {
			marginBot = document.body.scrollHeight - document.body.scrollTop- document.body.clientHeight;
			}
		if(marginBot<=2) {
			var url;
 			var lcense = $("#lcense1").val();
 			if(lcense=='1'){
 				url="${ctx}/index/searchLicense";
 			}else if(lcense=="2"){
 				url="${ctx}/index/searchOaFree";
 			}else{
 				url="${ctx}/index/search";
 			}
			$.ajax({
				type : "GET",
				async : false,    
		        url: url,
		        data: {	
		        	isJson:"true",
		        	curpage:curPage,
	       			language:"${form.language}",
	       			publisher:"${form.publisher}",
	       			pubDate:"${form.pubDate}",
	       			taxonomy:"${form.taxonomy}",
	       			taxonomyEn:"${form.taxonomyEn}",
	       			searchOrder:"${form.searchOrder}",
	       			lcense:"${form.lcense}",
	       			title:"${form.title}",
	       			pageCount:"20",
	       			selectflag:"${form.selectflag}",
	       			sortFlag:"${form.sortFlag}",
	       			searchValue:"${form.searchValue}",
       				searchsType:"${form.searchsType}"
		        		},
		        success : function(data) { 
		            var json = eval(data.list);
		            pubtype=data.pubtype;
		            if(typeof(json)!='undefined' && json.length>0){
		            	$("#curPage").val(data.curpage);
		             	for(var i=0;i<json.length;i++){
		             		var license ;
		             		var news;
		             		var oa;
		             		var free;
		             		var collection;
		             		var add1;
		             		var add;
		             		var favourite;
		             		var recommand;
		             		var user="${sessionScope.mainUser}";
		             		var level="${sessionScope.mainUser.level}";
		             		var insId ="${sessionScope.institution.id}";
		             		var userInsId="${sessionScope.mainUser.institution.id}";
		             		var ins ="${sessionScope.mainUser.institution}";
		             		if((json[i].subscribedIp!=null || json[i].subscribedUser!=null) && (json[i].subscribedIp || json[i].subscribedUser>0) ){
		             			license = true;
		             		}else{
		             			license = false;
		             		}
		             		if(json[i].latest !=null && json[i].latest>0){
		             			news = true;
		             		}else{
		             			news = false;
		             		}
		             		if(json[i].oa !=null && json[i].oa ==2){
		             			oa = true;
		             		}else{
		             			oa = false;
		             		}
		             		if(json[i].free !=null && json[i].free==2){
		             			free = true
		             		}else{
		             			free = false;
		             		}
		             		if(json[i].inCollection !=null && json[i].inCollection>0){
		             			collection = true;
		             		}else{
		             			collection = false;
		             		}
		             		if(json[i].priceList !=null && json[i].priceList.size >0 && json[i].free !=2 && json[i].oa !=2 && user!=null &&  json[i].subscribedUser <=0 && ( json[i].buyInDetail <=0 && json[i].exLicense >=0)){
		             			add1 = true
		             		}else{
		             			add1 = false;
		             		}
		             		if(add1!=true){
		             			add = false;
		             		}else{
		             			add = true;
		             		}
		             		if(add1==true && json[i].subscribedIp>0){
		             			if(insId = userInsId && level==2 ){
		             				add = false;
		             			}else{
		             				add = true;
		             			}
		             		}
		             		if(insId = userInsId && level!=2){
		             			add = true;
		             		}else{
		             			add = false;
		             		}
		             		if(insId != userInsId){
		             			add = true;
		             		}else{
		             			add = false;
		             		}
		             		if(add1==true && json[i].subscribedIp ==null || json[i].subscribedIp <=0 ){
		             			add=true;
		             		}else{
		             			add = false;
		             		}
		             		if(add1=false){
		             			add = false;
		             		}else{
		             			add = true;
		             		}
		             		if(user!=null && json[i].favorite<=0 ){
		             			favourite=true;
		             		}else{
		             			favourite = false;
		             		}
		             		if((json[i].recommand>0 || ins !=null) &&( json[i].subscribedIp ==null|| json[i].subscribedIp <=0)&&( json[i].free !=2 && json[i].oa !=2)){
		             			recommand = true;
		             		}else{
		             			recommand = false;
		             		}
		             		var htmlcode;
		             		if(license==false && oa==false && free==false){
		             			htmlcode+='<div  class="fl w40 mt2" style="margin-top: 10px;">';
		             			htmlcode+='<p class="p_left">';
		             			htmlcode+='<img src="${ctx }/images/ico/ico_close.png" />';
  		            		if(json[i].type==1){
  		            			htmlcode+='<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
  		            		}
  		            					
  		            		if(json[i].type ==2|| json[i].type ==6|| json[i].type==7){
  		            			htmlcode+='<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
  		            		}			
  		            		
  		            		if(json[i].type == 4){
  		            			htmlcode+='<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
  		            		}
  		            		htmlcode+='</p>';
  		            		if(news==true||free==true||oa==true||collection==true){
  		            			htmlcode+='<p class="p_right">';
  		            			if(free==true){
  		            				htmlcode+='<img src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';
  		            			}
  		            			if(oa==true){
  		            				htmlcode+='<img src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';
  		            			}
  		            			htmlcode+='</p>';
  		            		}		
  		            		
  		            		htmlcode+='</div>';
		             		}	
		            		if (license==true||oa==true||free==true ){
		            			htmlcode+='<div  class="fl w40 mt2"  style="margin-top: 10px;">';
		            			htmlcode+='<p class="p_left">';
		             				
            					if(oa==true){
            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';	
            					}
            					if(json[i].type==1){
            						htmlcode+='<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
            					}
            					if(json[i].type==2||json[i].type==6||json[i].type==7){
            						htmlcode+='<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
            					}	
            					if(json[i].type==4){
            						htmlcode+='<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
            					}
            					if(free==true ){
            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';	
            					}
            					
            					if(license==true){
            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_open.png" />';
            					}
            					htmlcode+='</p>';
            					htmlcode+='</div>';
		            		}
		            		
							htmlcode+='<div class="fl wid85" style="margin-top: 10px;">';
							 var t =json[i].title.replace(reg,'');		   		
		                    htmlcode+='<h1> <a href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'" data-ajax="false">'+t+'</a></h1>';
		                    if(json[i].author!=null){
		                    	htmlcode+='<p>By '+json[i].author;
		                    	htmlcode+='&nbsp;';
		                    	htmlcode+='</p>';
		                    }       
		                   if(json[i].type ==2 &&  json[i].startVolume !=null  && json[i].endVolume!=null){
		                	  htmlcode+='<p>';
		                	  htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> '+json[i].startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume +'</p>';
		                   }
		                   htmlcode+='<p>';
		                   htmlcode+=json[i].publisher.name;
		                   if(json[i].pubDate!=null ){
		                	   var pubdate  =json[i].pubDate.substring(0,4);
		                	   htmlcode+= '('+pubdate+')';
		                   }
		                  htmlcode+='</p>';
		                  if(json[i].type==4 || json[i].type==6 || json[i].type==7 || json[i].type==2 ){
		                	  htmlcode+='<p> ISSN:'+json[i].code+"</p>";
		                  }
		                  //无购买权限
		                  if(user!=null){
		                	  if(!add && license==false && oa==false && free==false){
		                		  htmlcode+='<span class="mr20">';
		                		  htmlcode+='<img src="${ctx }/images/ico/ico14-grey.png" class="vm" /><ingenta-tag:LanguageTag key="Page.Publications.Lable.Buy" sessionKey="lang"/>';
								  htmlcode+='</span>';
		                	  }
		                  }
		                  // 购买 
		                  if(add){
		                		htmlcode+='<span class="mr20">'
								htmlcode+='<a href="javascript:void(0)" onclick="addToCart('+json[i].id+',1);" id="add_${p.id }" title="<ingenta-tag:LanguageTag key="Page.Index.Search.Link.Buy" sessionKey="lang"/>">';
								htmlcode+='<img src="${ctx }/images/ico/ico14-blank.png" class="vm" />';
								htmlcode+='<ingenta-tag:LanguageTag key="Page.Publications.Lable.Buy" sessionKey="lang"/>';
								htmlcode+='</a>';
								htmlcode+='</span>'; 
		                  }
		                  //获取资源
		                  if(license == false && oa == false && free == false){
		                	  htmlcode+='<span class="mr20">';
		                	  htmlcode+='<a href="javascript:void(0)"  id="resource_div" onclick="popTips2('+json[i].id+');"><img src="${ctx }/images/ico/ico15-blue.png" class="vm" />';
		                	  htmlcode+='<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />';
		                	  htmlcode+='</a>';
		                	  htmlcode+='</span>';
		                  }
		                  if(license == true || oa == true || free == true){
		                	  if(json[i].type==1){
		                		  htmlcode+='<span class="mr20">';
	                			  htmlcode+='<a href="javascript:void(0)"  id="resource_div"  onclick="viewPopTips('+json[i].id+',"0",<c:if test="${oa==false&&free==false}">1</c:if><c:if test="${oa==true||free==true}">2</c:if>)"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />';
	                			  htmlcode+='<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />';
                				  htmlcode+='</a>';
               					  htmlcode+='</span>';
		                	  }
		                 
		                  	  if(json[i].type==4){
			                	  htmlcode+=' <span class="mr20">';
		                		  htmlcode+='<a href="javascript:void(0)"  id="resource_div1"  onclick="popTips2('+json[i].id+');"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />';
		                		  htmlcode+='<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />';
	                			  htmlcode+='</a>';
	               				  htmlcode+='</span>';
			                  }
	                 	 }
		                  //推荐
		                 if(recommand){
		                	 htmlcode+='<span class="mr20"><a href="javascript:void(0)" id="recommand_'+json[i].id+'" title="<ingenta-tag:LanguageTag key="Page.Index.Search.Link.Recommend" sessionKey="lang" />" onclick="recommends('+json[i].id+');">';
		                	 htmlcode+='<img src="${ctx }/images/ico/ico16-blue.png" class="vm" /><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Recommend" sessionKey="lang" /></a></span>';
		                 }
		                  //收藏
		                 if(favourite){
		                	 htmlcode+='<span class="mr20">';
	                		 htmlcode+='<a href="javascript:;" id="${P.id }" class="" onclick="favourite2('+json[i].id+')"><img src="${ctx}/mobile/images/unfavourite.png" class="vm" />';
	                		 htmlcode+='<span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span>';
                			 htmlcode+='</a>';
               				 htmlcode+='</span>'; 
		                 }
		                  //已收藏
		                  if(user!=null && !favourite){
		                	  htmlcode+=' <span class="mr20">';
	                		  htmlcode+='<a href="javascript:;" id="${p.id}" class="blank" onclick="favourite2('+json[i].id+')"><img src="${ctx}/mobile/images/favourite.png" class="vm" />';
	                		  htmlcode+='<span>';
                			  htmlcode+='<ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span></a>';
               				  htmlcode+='</span>';
		                  }
     					  htmlcode+='</div>';	
     					  htmlcode+='<p style="height:1px; width:1px; clear:both;">&nbsp;</p>';
		             	}	             	
	             	}else{
	             		if($("#nomore").length>0){
	 	            		return false;
	 	            	}
	 	            	$("#searchList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
						return false;
	             	}
		            //html全部拼完会出现一个undefined在最前面
					if(htmlcode.indexOf("undefined")>=0){
						htmlcode = htmlcode.substring(9,htmlcode.length);
					}
		            $("#searchList").append(htmlcode);
		           
		   		
	            },  
	            error : function(data) {  
	            	if($("#nomore").length>0){
 	            		return false;
 	            	}
 	            	$("#searchList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
					return false;
	            }  		           
	      });
			
		}
	}
			//document.getElementById("result").innerHTML = text;  
			}  
			catch (e) {  
			    alert('touchMoveFunc：' + e.message);  
			}  
		}  
			//touchend事件  
			function touchEndFunc(evt) {  
			   try {  
			       //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等  
			
			       var text = 'TouchEnd事件触发';  
			       //document.getElementById("result").innerHTML = text;  
			   }  
			   catch (e) {  
			       alert('touchEndFunc：' + e.message);  
			   }  
			}  
			
			//绑定事件  
			function bindEvent() {  
			   document.addEventListener('touchstart', touchSatrtFunc, false);  
			   document.addEventListener('touchmove', touchMoveFunc, false);  
			   //document.addEventListener('touchend', touchEndFunc, false);  
			}  
			
			//判断是否支持触摸事件  
			function isTouchDevice() {  
			   //document.getElementById("version").innerHTML = navigator.appVersion;  
			
			   try {  
			       document.createEvent("TouchEvent");  
			       //alert("支持TouchEvent事件！");  
			
			       bindEvent(); //绑定事件  
			   }  
			   catch (e) {  
			       //alert("不支持TouchEvent事件！" + e.message);  
			   }  
			}  
			 
			
			function searchAllResource(){
				$("#lcense1").val("");
				$("#order1").val('');
				$("input[name='curpage']").val(0);
				document.formListSearch.action="${ctx}/index/search";
				document.formListSearch.submit();
			}
			
			function searchLicense(){
				$("#lcense1").val("1");
				$("#order1").val('');
				$("input[name='curpage']").val(0);
				document.formListSearch.action="${ctx}/index/searchLicense";
				document.formListSearch.submit();
			}
			
			function searchOaFree(){
				$("#lcense1").val("2");
				$("#order1").val('');
				$("input[name='curpage']").val(0);
				document.formListSearch.action="${ctx}/index/searchOaFree";
				document.formListSearch.submit();
			}
			
			//筛选
			//左侧条件查询
	function searchByCondition(type,value){
		if(type=="type"){
			$("input[name=pubType]").val(value);
		}else if(type=="publisher"){
			$("input[name=publisher]").val(value); 
		}else if(type=="pubDate"){
			$("input[name=pubDate]").val(value);
		}else if(type=="taxonomy"){
			var tax=$("input[name=taxonomy]").val();
			var s=$("#taxonomy1").val();
			var strs= new Array(); 
			    strs=s.split(","); 
			    for (i=0;i<strs.length ;i++ ) 
			    { 
			    	if(strs[i]==value){
			    		verify=1;
			    		strs.remove(i); 
			    	}
			    }
			    b = strs.join("-");
			    document.getElementsByName("template").value=b;
			    if(tax){
	    			$("input[name=taxonomy]").val(tax+","+value);
	    		}else{
	    			$("input[name=taxonomy]").val(value);	
	    		}
		    		
		}else if(type=="taxonomyEn"){
			$("input[name=taxonomyEn]").val(value);
		}else if(type=="language"){
			$("input[name=language]").val(value);
		}else if(type=="selectflag"){
			$("input[name=selectflag]").val(value);
		}else if(type=="searchOrder"){
			$("input[name=searchOrder]").val(value);
			if($("#sortFlag").val()==""){
				$("input[name=sortFlag]").val("desc");
			}else if ($("#sortFlag").val()=="desc"){
				$("input[name=sortFlag]").val("asc");
			}else if ($("#sortFlag").val()=="asc"){
				$("input[name=sortFlag]").val("desc");
			}
	}
		var lcense = $("#lcense1").val();
		$("input[name='curpage']").val(0);
		$("#order1").val('');
		if(lcense=='1'){
			document.formList.action="${ctx}/index/searchLicense";
		}else if(lcense=="2"){
			document.formList.action="${ctx}/index/searchOaFree";
		}else{
			document.formList.action="${ctx}/index/search";
		}
		document.formList.submit();
	}
				

			//加载更多
			function loadNext(){
			try {  
				var curPage =$("#curPage").val();
				$("#load"+curPage).hide();
				
			    var pubDateOrder;
				var shelvesDateOrder;
				var pubDatesuffix;
				var shelvesDatesuffix;
			
				if(/([^\+\-\*\/\(\:]+)$/.test($("#ShelvesDate")[0].src)){
					shelvesDatesuffix=RegExp.$1;
				}
				if(shelvesDatesuffix == "icoDown.png"){
					$("#ShelvesDate").attr('src',"${ctx}/mobile/images/icoUp.png");
					shelvesDateOrder = "asc";
				}else{
					$("#ShelvesDate").attr('src',"${ctx}/mobile/images/icoDown.png");
					shelvesDateOrder = "desc";
				}
				
				if(/([^\+\-\*\/\(\:]+)$/.test($("#pubDate")[0].src)){
					pubDatesuffix=RegExp.$1;
				}
				if(pubDatesuffix == "icoDown.png"){
					$("#pubDate").attr('src',"${ctx}/mobile/images/icoUp.png");
					pubDateOrder = "asc";
				}else{
					$("#pubDate").attr('src',"${ctx}/mobile/images/icoDown.png");
					pubDateOrder = "desc";
				}
				var url;
	 			var lcense = $("#lcense1").val();
				if(lcense=='1'){
	 				url="${ctx}/index/searchLicense";
	 			}else if(lcense=="2"){
	 				url="${ctx}/index/searchOaFree";
	 			}else{
	 				url="${ctx}/index/search";
	 			}
		 			$.ajax({
		 				type : "GET",
		 				async : false,    
		 		        url:url,
		 		        data: {	
		 		        	curpage:curPage,
		 		        	isJson:"true",
			       			language:"${form.language}",
			       			publisher:"${form.publisher}",
			       			pubDate:"${form.pubDate}",
			       			taxonomy:"${form.taxonomy}",
			       			taxonomyEn:"${form.taxonomyEn}",
			       			searchOrder:"${form.searchOrder}",
			       			lcense:"${form.lcense}",
			       			title:"${form.title}",
			       			pageCount:"20",
			       			selectflag:"${form.selectflag}",
			       			sortFlag:"${form.sortFlag}"
		 		        		},
		 		        success : function(data) { 
		 		            var json = eval(data.list);
		 		            pubtype=data.pubtype;
		 		            if(typeof(json)!='undefined' && json.length>0){
		 		            	$("#curPage").val(data.curpage);
		 		             	for(var i=0;i<json.length;i++){
		 		             		var license ;
		 		             		var news;
		 		             		var oa;
		 		             		var free;
		 		             		var collection;
		 		             		var add1;
		 		             		var add;
		 		             		var favourite;
		 		             		var recommand;
		 		             		var user="${sessionScope.mainUser}";
		 		             		var level="${sessionScope.mainUser.level}";
		 		             		var insId ="${sessionScope.institution.id}";
		 		             		var userInsId="${sessionScope.mainUser.institution.id}";
		 		             		var ins ="${sessionScope.mainUser.institution}";
		 		             		if((json[i].subscribedIp!=null || json[i].subscribedUser!=null) && (json[i].subscribedIp || json[i].subscribedUser>0) ){
		 		             			license = true;
		 		             		}else{
		 		             			license = false;
		 		             		}
		 		             		if(json[i].latest !=null && json[i].latest>0){
		 		             			news = true;
		 		             		}else{
		 		             			news = false;
		 		             		}
		 		             		if(json[i].oa !=null && json[i].oa ==2){
		 		             			oa = true;
		 		             		}else{
		 		             			oa = false;
		 		             		}
		 		             		if(json[i].free !=null && json[i].free==2){
		 		             			free = true
		 		             		}else{
		 		             			free = false;
		 		             		}
		 		             		if(json[i].inCollection !=null && json[i].inCollection>0){
		 		             			collection = true;
		 		             		}else{
		 		             			collection = false;
		 		             		}
		 		             		if(json[i].priceList !=null && json[i].priceList.size >0 && json[i].free !=2 && json[i].oa !=2 && user!=null &&  json[i].subscribedUser <=0 && ( json[i].buyInDetail <=0 && json[i].exLicense >=0)){
		 		             			add1 = true
		 		             		}else{
		 		             			add1 = false;
		 		             		}
		 		             		if(add1!=true){
		 		             			add = false;
		 		             		}else{
		 		             			add = true;
		 		             		}
		 		             		if(add1==true && json[i].subscribedIp>0){
		 		             			if(insId = userInsId && level==2 ){
		 		             				add = false;
		 		             			}else{
		 		             				add = true;
		 		             			}
		 		             		}
		 		             		if(insId = userInsId && level!=2){
		 		             			add = true;
		 		             		}else{
		 		             			add = false;
		 		             		}
		 		             		if(insId != userInsId){
		 		             			add = true;
		 		             		}else{
		 		             			add = false;
		 		             		}
		 		             		if(add1==true && json[i].subscribedIp ==null || json[i].subscribedIp <=0 ){
		 		             			add=true;
		 		             		}else{
		 		             			add = false;
		 		             		}
		 		             		if(add1=false){
		 		             			add = false;
		 		             		}else{
		 		             			add = true;
		 		             		}
		 		             		if(user!=null && json[i].favorite<=0 ){
		 		             			favourite=true;
		 		             		}else{
		 		             			favourite = false;
		 		             		}
		 		             		if((json[i].recommand>0 || ins !=null) &&( json[i].subscribedIp ==null|| json[i].subscribedIp <=0)&&( json[i].free !=2 && json[i].oa !=2)){
		 		             			recommand = true;
		 		             		}else{
		 		             			recommand = false;
		 		             		}
		 		             		var htmlcode;
		 		             		if(license==false && oa==false && free==false){
		 		             			htmlcode+='<div class="fl w40 mt2">';
		 		             			htmlcode+='<p class="p_left">';
		 		             			htmlcode+='<img src="${ctx }/images/ico/ico_close.png" />';
		    		            		if(json[i].type==1){
		    		            			htmlcode+='<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
		    		            		}
		    		            					
		    		            		if(json[i].type ==2|| json[i].type ==6|| json[i].type==7){
		    		            			htmlcode+='<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
		    		            		}			
		    		            		
		    		            		if(json[i].type == 4){
		    		            			htmlcode+='<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
		    		            		}
		    		            		htmlcode+='</p>';
		    		            		if(news==true||free==true||oa==true||collection==true){
		    		            			htmlcode+='<p class="p_right">';
		    		            			if(free==true){
		    		            				htmlcode+='<img src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';
		    		            			}
		    		            			if(oa==true){
		    		            				htmlcode+='<img src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';
		    		            			}
		    		            			htmlcode+='</p>';
		    		            		}		
		    		            		
		    		            		htmlcode+='</div>';
		 		             		}	
		 		                	htmlcode+='<div class="booklist oh">';
		 		                	htmlcode+='<div class="fl w40">';
		 		            		if (license==true||oa==true||free==true ){
		 		            			htmlcode+='<div class="fl w40 mt2">';
			            					if(free==true ){
			            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';	
			            					}
			            					if(oa==true){
			            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';	
			            					}
			            					if(license==true){
			            						htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_open.png" />';
			            					}
			            					if(json[i].type==1){
			            						htmlcode+='<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
			            					}
			            					if(json[i].type==2||json[i].type==6||json[i].type==7){
			            						htmlcode+='<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
			            					}	
			            					if(json[i].type==4){
			            						htmlcode='<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
			            					}
			            					htmlcode+='</div>';
		 		            		}
		 		            		htmlcode+='</div>';
									htmlcode+='<div class="fl wid85">';
		 		                    htmlcode+='<h1> <a href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'" data-ajax="false">'+json[i].title +'</a></h1>';
		 		                    if(json[i].author!=null){
		 		                    	htmlcode+='<p>By '+json[i].author;
		 		                    	htmlcode+='&nbsp;';
		 		                    	htmlcode+='</p>';
		 		                    }       
		 		                   htmlcode+='<p>';
		 		                   htmlcode+=json[i].publisher.name;
		 		                   if(json[i].pubDate!=null &&  json[i].pubDate!=""){
		 		                	   var pubdate  =json[i].pubDate.substring(0,4);
		 		                	   htmlcode+= '('+pubdate+')';
		 		                   }
		 		                   htmlcode+='</p>';
		 		                   if(json[i].type ==2 &&  json[i].startVolume !=null  && json[i].endVolume!=null){
		 		                	  htmlcode+='<p>';
		 		                	  htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> '+json[i].startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume +'</p>';
		 		                   }
		 		                  htmlcode+='</div>';
		       					  htmlcode+='</div>';	
		 		             	}	             	
		 	             	}else{
		 	             		if($("#nomore").length>0){
			 	            		return false;
			 	            	}
			 	            	$("#searchList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
								return false;
		 	             	}
		 		            //html全部拼完会出现一个undefined在最前面
							if(htmlcode.indexOf("undefined")>=0){
								htmlcode = htmlcode.substring(9,htmlcode.length);
							}
		 		            $("#searchList").append(htmlcode);
		 	            },  
		 	            error : function(data) {  
		 	            	if($("#nomore").length>0){
		 	            		return false;
		 	            	}
		 	            	$("#searchList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
							return false;
		 	            }  		           
		 	      });
		 			
		 		}
					catch (e) {  
					    alert('touchMoveFunc：' + e.message);  
					}  
				}
				
				 	
</script>


</head>
<body>
<div data-role="page" data-theme="c" class="page" id="pageorder">
  <!-- header -->
  <jsp:include page="/header" />
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1">检索结果</h1>
    </div>
    <!-- 搜索条件开始 -->
     <div class="ui-grid-b resource">
  	 <div class="ui-block-a" style="width:35%;"><a href="javascript:;" onclick="searchByCondition('searchOrder','pubDate')" >出版时间 <img id="pubDate" src="${ctx}/mobile/images/icoUp.png" class="vm"></a></div>
     <div class="ui-block-b" style="width:35%;"><a href="javascript:;"onclick="searchByCondition('searchOrder','createOn')" >上架时间 <img id="ShelvesDate" src="${ctx}/mobile/images/icoUp.png" class="vm"></a></div>
     <div class="ui-block-c popup"  data-role="collapsible" style="width:30%;">
        	<h1><a class="updown" href="#">筛选</a></h1>
            <div class="screenList">
            	<ul class="menu">
                	<li><a href="#"><h2>资源</h2></a>
                    	<ul>
                        	<li><a href="javascript:void(0)" onclick="searchAllResource()" >全部资源</a></li>
                            <c:if test="${sessionScope.mainUser!=null }"><li><a href="javascript:void(0)" onclick="searchLicense()" >订阅资源</a></li></c:if> 
                            <li><a href="javascript:void(0)" onclick="searchOaFree()" >免费资源</a></li>
                        </ul>
                    </li>
                    <li><a href="#"><h2>语种</h2></a>
                    	<ul>
                    		<c:forEach items="${languageList }" var="l">
                    		<c:if test="${ l.count >0}">
								<c:if test="${l.name!=null && l.name!='' }">
		                            <li>
		                                <div class="ui-grid-a">
		                                    <div class="ui-block-a"><img src="${ctx}/mobile/images/ico/ico10.png" class="vm"> 
		                                    	<a href="javascript:void(0)" onclick="searchByCondition('language','${l.name}')" >${l.name }</a>
		                                    </div>
		                                    <div class="ui-block-b"><a href="#">[ ${l.count } ]</a></div>
		                                </div>
		                            </li>
		                         </c:if>
	                         </c:if>
                           </c:forEach>
                        </ul>
                    </li>
                    <li><a href="#"><h2>分类法</h2></a>
                    	<ul>
	                    	<c:forEach items="${taxonomyList }" var="t">
	                    		<c:if test="${ t.count >0}">
									<c:if test="${t.name!=null && t.name!='' }">
			                            <li>
			                                <div class="ui-grid-a">
			                                    <div class="ui-block-a"><img src="${ctx}/mobile/images/ico/ico10.png" class="vm"> 
			                                   	 	<a href="javascript:void(0)" onclick="searchByCondition('taxonomy','${t.name }')" >${t.name }</a>
			                                    </div>
			                                    <div class="ui-block-b"><a href="#">[ ${t.count } ]</a></div>
			                                </div>
			                            </li>
			                       </c:if>
			                   </c:if>
							</c:forEach>
                        </ul>
                    </li>
                    <li><a href="#"><h2>出版社</h2></a>
                    	<ul>
	                    	<c:forEach items="${publisherList }" var="pub" varStatus="index">
								<c:if test="${pub.count>0 }">
		                            <li>
			                            <div class="ui-grid-a">
			                                <div class="ui-block-a"><img src="${ctx}/mobile/images/ico/ico10.png" class="vm">
			                                	<a href="javascript:void(0)" onclick="searchByCondition('publisher','${pub.name }')" >${pub.name }</a>
				                            </div>
			                                <div class="ui-block-b"><a href="#">[ ${pub.count } ]</a></div>
			                            </div>
		                            </li>
	                            </c:if>
                            </c:forEach>
                        </ul>
                    </li>
                    <li><a href="#"><h2>出版时间</h2></a>
                    	<ul>
	                    	<c:forEach items="${pubDateMap }" var="p" varStatus="index">
		          				<c:if test="${p.value>0}">
		          					<c:if test="${'0000' != p.key}">
		          						<li>
		                           	 	 <div class="ui-grid-a">
			                                    <div class="ui-block-a"><img src="${ctx}/mobile/images/ico/ico10.png" class="vm"> 
			                                    	<a href="javascript:void(0)" onclick="searchByCondition('pubDate','${p.key }')">${p.key }</a>
			                                    </div>
			                                    <div class="ui-block-b"><a href="#">[ ${p.value } ]</a></div>
			                               </div>
			                            </li>
		                            </c:if>
	                            </c:if>
                            </c:forEach>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
      </div>
    <div  class="StabContent ScontentSelected CtabContent">	
					<div class="mt5 mb5 oh">
						<span class="fl">
							<c:if test="${form.searchValue!=null&&form.searchValue!='' }">
								<c:if test="${sessionScope.lang=='zh_CN'}">
									<c:if test="${queryCount > 1000}">
									<p>关键字'${form.searchValue}'命中了${queryCount }个结果，仅显示命中关键字的前1000条资源 </p>
									</c:if>
									<c:if test="${queryCount <= 1000}">
									<p>关键字'${form.searchValue }'命中了${queryCount }个结果 </p>
									</c:if>
								</c:if>
								<c:if test="${sessionScope.lang=='en_US'}">
									<c:if test="${queryCount > 1000}">
									<p>${queryCount } Result(s) for '${form.searchValue }',Display only a keyword before 1000 resources </p>
									</c:if>
									<c:if test="${queryCount <= 1000}">
									<p>${queryCount } Result(s) for '${form.searchValue }' </p>
									</c:if>
								</c:if>
							</c:if>
						</span>
					</div>
  		 <!--列表内容开始-->
			<div class="oh mb20" id="searchList">
				<c:forEach items="${list }" var="p" varStatus="index">
					<c:set var="license">${(p.subscribedIp!=null||p.subscribedUser!=null)&&(p.subscribedIp>0||p.subscribedUser>0) }</c:set>
					<c:set var="news">${p.latest!=null&&p.latest>0 }</c:set>
					<c:set var="oa">${p.oa!=null&&p.oa==2 }</c:set>
					<c:set var="free">${p.free!=null&&p.free==2 }</c:set>
					<c:set var="collection">${p.inCollection!=null&&p.inCollection>0 }</c:set>
					<c:set var="add1" value="${p.priceList!=null&&fn:length(p.priceList)>0&&p.free!=2&&p.oa!=2&&sessionScope.mainUser!=null && p.subscribedUser<=0&&(p.buyInDetail<=0&&p.exLicense>=0)}"/>
					<c:if test="${add1==false }">
						<c:set var="add" value="false"/>
					</c:if>
					<c:if test="${add1==true &&p.subscribedIp>0 }">
						<c:if test="${sessionScope.mainUser.institution.id==sessionScope.institution.id&&sessionScope.mainUser.level==2 }">
						<c:set var="add" value="false"/>
						</c:if>
						<c:if test="${sessionScope.mainUser.institution.id==sessionScope.institution.id &&sessionScope.mainUser.level!=2 }">
						<c:set var="add" value="true"/>
						</c:if>
						<c:if test="${sessionScope.mainUser.institution.id!=sessionScope.institution.id}">
						<c:set var="add" value="true"/>
						</c:if>
					</c:if>
					<c:if test="${add1==true &&(p.subscribedIp==null||p.subscribedIp<=0) }">
						<c:set var="add" value="true"/>
					</c:if>
					<c:if test="${add1==false }">
						<c:set var="add" value="false"/>
					</c:if>
					<c:set var="favourite" value="${sessionScope.mainUser!=null&&p.favorite<=0 }"/>
					<c:set var="recommand" value="${(p.recommand>0 || sessionScope.mainUser.institution!=null) &&(p.subscribedIp==null||p.subscribedIp<=0)&&(p.free!=2&&p.oa!=2)}"/>	
					<c:if test="${license==false&&oa==false&&free==false }">
						<div class="fl w40 mt2" style="margin-top: 10px;">
							<p class="p_left">
							<img src="${ctx }/images/ico/ico_close.png" />
							<c:if test="${p.type==1 }">
								<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />
							</c:if>
							<c:if test="${p.type==2||p.type==6||p.type==7 }">
								<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />
							</c:if>
							<c:if test="${p.type==4 }">
								<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />
							</c:if>
							<%-- <a name="title" href="${ctx}/pages/publications/form/article/${p.id}?sv=${form.searchValue}&fp=2" title="${p.title }">${p.title }</a> --%>
							</p>
							<c:if test="${news==true||free==true||oa==true||collection==true }">
							<p class="p_right">
								<%-- <c:if test="${news==true }"><img src="${ctx }/images/n.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.New" sessionKey="lang" />"/></c:if> --%>
								<c:if test="${free==true }"><img src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/></c:if>
								<c:if test="${oa==true }"><img src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/></c:if>
								<%-- <c:if test="${collection==true }"><img src="${ctx }/images/c.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Collection" sessionKey="lang" />"/></c:if> --%>
							</p>
							</c:if>
						</div>
					</c:if>
				<c:if test="${license==true||oa==true||free==true }">
           			  <div class="fl w40 mt2"  style="margin-top: 10px;">
							<c:if test="${free==true }"><img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/></c:if>
							<c:if test="${oa==true }"><img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/></c:if>
							<c:if test="${license==true }"><img width="14" height="14" src="${ctx }/images/ico/ico_open.png" /></c:if>
							<c:if test="${p.type==1 }">
								<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />
							</c:if>
							<c:if test="${p.type==2||p.type==6||p.type==7 }">
								<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />
							</c:if>
							<c:if test="${p.type==4 }">
								<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />
							</c:if>
						</div>
				</c:if>	
							
				<div class="fl wid85" style="margin-top: 10px;">
						<h2>
							<a class="a_title" name="title" href="${ctx}/mobile/pages/publications/form/article/${p.id}?fp=3&sv=${form.searchValue}" title="${fn:replace(p.title,"\"","\'")}">
							
							${fn:replace(fn:replace(fn:replace(p.title, "&lt;", "<"),"&gt;",">"),"&amp;","&")}
							</a>
							
						</h2>
						<c:if test="${p.type!=2 }">
						<p>
						    By
						    <c:set var="authors" value="${fn:split(p.author,',')}" ></c:set>
			                <c:forEach items="${authors}" var="a" >
			                <a href="javascript:void(0)" onclick="searchMe(this,'author')">${a}</a>
			                </c:forEach>
							<c:if test="${p.type==4||p.type==6||p.type==7 }"> in 
							<a href="${ctx}/pages/publications/form/article/${p.publications.id}?fp=3&sv=${form.searchValue}">${p.publications.title}</a>
							</c:if>
						</p>
						</c:if>
						<c:if test="${p.type==2 }">
						<p>
						<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.endVolume }
						</p>
						</c:if>
						<p>
						 <a href="javascript:void(0)" onclick="searchMe(this,'publisher')">${p.publisher.name}</a>(${fn:substring(p.pubDate,0,4) })
						</p>
						<p>
						  <c:if test="${p.type==4||p.type==6||p.type==7||p.type==2 }">
						   ISSN:${p.code}
						  </c:if>
						</p>
						<c:if test="${p.journalType==2}">
						<p>
					     <ingenta-tag:LanguageTag key="Page.Frame.Periodic.type" sessionKey="lang" />:
					     <c:if test="${p.periodicType==1}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Weekly" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==2}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Semi.monthly" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==3}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.monthly" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==4}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Quarterly" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==5}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Semi.annual" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==6}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.annual" sessionKey="lang" /></c:if>
					     <c:if test="${p.periodicType==7}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Other" sessionKey="lang" /></c:if>
						</p>
					     </c:if>
						<p>
						  <c:if test="${p.type==1}">
						   ISBN:${p.code}
						  </c:if>
						</p>
							<!-- 无购买权限 -->
							<c:if test="${sessionScope.mainUser!=null }">
								<c:if test="${!add && license==false && oa==false && free==false}">
									<span class="mr20">
										<img src="${ctx }/images/ico/ico14-grey.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang'/>
									</span>
								</c:if>
							</c:if>
							<c:if test="${add }">
							<!-- 购买 -->
								<span class="mr20">
									<a href="javascript:void(0)" onclick="addToCart('${p.id}',1);" id="add_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Buy' sessionKey='lang'/>">
										<img src="${ctx }/images/ico/ico14-blank.png" class="vm" />
											<ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang'/>
									</a>
								</span> 
							</c:if>
							<!-- 获取资源 -->
						<c:if test="${license==false&&oa==false&&free==false }">
							<span class="mr20" style="margin-top: 10px;">
							<a href="javascript:void(0)"  id="resource_div" onclick="popTips2('${p.id}');"><img src="${ctx }/images/ico/ico15-blue.png" class="vm" />
							<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
							</a>
							</span>
						</c:if>
						<c:if test="${license==true||oa==true||free==true }">
						<c:if test="${p.type==1 }">
							<span class="mr20" style="margin-top: 10px;">
								<a href="javascript:void(0)"  id="resource_div"  onclick="viewPopTips('${p.id}','0',<c:if test="${oa==false&&free==false}">1</c:if><c:if test="${oa==true||free==true}">2</c:if>)"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
								</a>
							</span>
						</c:if>
						<c:if test="${p.type==4 }">
							<span class="mr20" style="margin-top: 10px;">
								<a href="javascript:void(0)"  id="resource_div1"  onclick="popTips2('${p.id}');"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
								</a>
							</span>
						</c:if>
						</c:if>
							<c:if test="${recommand}">
							<!-- 推荐 -->
								<span class="mr20"><a href="javascript:void(0)" id="recommand_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' />" onclick="recommends('${p.id}');">
								<img src="${ctx }/images/ico/ico16-blue.png" class="vm" /><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Recommend" sessionKey="lang" /></a></span>
							</c:if>	
								<!-- 收藏 -->
							<c:if test="${favourite }">
							<span class="mr20"><a href="javascript:;" id="${P.id }" class="" onclick="favourite2('${p.id }')"><img src="${ctx}/mobile/images/unfavourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span></a></span> 
							</c:if>
								<!-- 已收藏 -->
							<c:if test="${sessionScope.mainUser!=null && !favourite }">
							  <span class="mr20"><a href="javascript:;" id="${p.id}" class="blank" onclick="favourite2('${p.id}')"><img src="${ctx}/mobile/images/favourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span></a></span>
							</c:if>
						</p>	
				
				</div>	
				<p style="height:1px; width:1px; clear:both;">&nbsp;</p>
				</c:forEach>
				<c:if test="${empty list }">
					<br/><br/>
					<p align="center">
						<b>
							<c:if test="${form.lcense==null || form.lcense==''}">
								<ingenta-tag:LanguageTag key="Page.All.Alert" sessionKey="lang" />
									<font color="red">${form.searchValue}</font>
								<ingenta-tag:LanguageTag key="Page.All.Alert1" sessionKey="lang" />
							</c:if>
							<c:if test="${form.lcense==2 || form.lcense=='2' }">
								<ingenta-tag:LanguageTag key="Page.OaFree.Alert" sessionKey="lang" />
									<font color="red">${form.searchValue}</font>
								<ingenta-tag:LanguageTag key="Page.OaFree.Alert1" sessionKey="lang" />
							</c:if>
							<c:if test="${form.lcense==1 || form.lcense=='1' }">
								<ingenta-tag:LanguageTag key="Page.SearchLicense.Alert" sessionKey="lang" />
									<font color="red">${form.searchValue}</font>
								<ingenta-tag:LanguageTag key="Page.SearchLicense.Alert1" sessionKey="lang" />
							</c:if>
					</p>
					<br/>
				</c:if>
					</div>
				</div>
	<!--列表内容结束-->
  	 <input type="hidden" value="${form.curpage }" id="curPage">
  	  <!--以下 提交查询Form 开始-->
		<form:form action="${form.url}" method="post" modelAttribute="form" commandName="form" name="formList" id="formList">
			<form:hidden path="pubType" id="pubType2"/>
			<form:hidden path="language" id="language2"/>
			<form:hidden path="publisher" id="publisher2"/>
			<form:hidden path="pubDate" id="pubDate2"/>
			<form:hidden path="taxonomy" id="taxonomy2"/>
			<form:hidden path="taxonomyEn" id="taxonomyEn2"/>
			<form:hidden path="lcense" id="lcense2"/>
	        <form:hidden path="title" id="title1"/>
	        <form:hidden path="isCn" id="isCn"/>
	        <form:hidden path="curpage" id="curpage1"/>
	        <form:hidden path="pageCount" id="pageCount1"/>
	        <form:hidden path="searchValue" id="searchValue"/>
	        <form:hidden path="searchsType" id="searchsType"/>
	         <form:hidden path="sortFlag" id="sortFlag"/>
	         <form:hidden path="searchOrder" id="searchOrder"/>
		</form:form>
     <jsp:include page="/footer" />
    </div>    
</div>
    
</body>
</html>
