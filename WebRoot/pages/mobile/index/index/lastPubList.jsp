<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
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
<script type="text/javascript">
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
	                    //alert('touchSatrtFunc：' + e.message);  
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
               
               var curPage =$("#curPage").val();	
               if(curPage=="" || curPage=="0"){
               	curPage=1;
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
               			if(curPage%5==0){
             				if($("#next"+curPage).val()=="0"){
             					return false;
             				}
             				 $("#advancedSearchList").append('<div style="text-align: center;"><input type="hidden" id="next'+curPage+'" value="0" /><a href ="javascript:;" id="load'+curPage+'" onclick="loadNext();"  >加载更多</a></div>');
             				 return false;
             			}
               			$.ajax({
               				type : "GET",
               				async : false,    
               		        url: "${ctx}/mobile/index/advancedSearchSubmit",
               		        data: {	
               		        	curpage:curPage,
               		        	isJson:"true",
               		        	taxonomy:"${form.taxonomy}",
               		        	lcense:"${form.lcense}",
               		        	subFlag:"${form.subFlag}",
               		        	newFlag:"${form.newFlag}",
               		        	sortFlag:"${form.sortFlag}"
               		        		},
               		        success : function(data) { 
               		        	var htmlcode;
               		            var json = eval(data.list);
               		            if(json.length>0){
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
               		             	htmlcode+='<div class="mb20 fontFam oh">';
               		             	htmlcode+='<div class="fl w22">';
               		            	if(license==false && oa==false && free==false){
               		            		if(license == true){
               		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_open.png" />';
               		            		}
               		            		if(free == true){
               		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';
               		            		}
               		            		if(oa ==  true){
               		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';
               		            		}
               		             	}
               		             	if(license==false){
               		             		htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_close.png" />';
               		             	}
          		             		if(json[i].type==1){
          		             			htmlcode+='<img width="14" height="14" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
          		             		}
          		             		if(json[i].type==4 || json[i].type==3){
          		             			htmlcode+='<img width="14" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
          		             		}
          		             		if(json[i].type==2 || json[i].type==6 || json[i].type==7){
          		             			htmlcode+='<img width="14" height="14" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
          		             		}
          		             		htmlcode+='</div>';
          		             		htmlcode+='<div class="fl wid90">';
          		             		htmlcode+='<h2><a title='+json[i].title + ' data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'" >'+json[i].title +' </a></h2>';
          		             		if(json[i].author!=null){
          		             			htmlcode+='<p>By '+json[i].author+' </p>';
          		             		}
           		             		if(json[i].publisher.name!=null){
           		             			htmlcode+='<p>'+json[i].publisher.name;
           		             			if(json[i].publisher.pubDate!=null){
           		             				var pubdate =json[i].publisher.pubDate.substring(0,4);
           		             			 	htmlcode+= '('+pubdate+')';
           		             			}
           		             			htmlcode+'</p>';
           		             		}
           		             		if(json[i].type==2 && json[i].startVolume !=null  && json[i].endVolume !=null){
           		             			htmlcode+'<p>';
           		         				htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume;
           		         				htmlcode+'</p>';
           		             		}
           		             		htmlcode+='</div>';
         		             		htmlcode+='</div>';
           		             	}	             	
           	             	}else{
           	             		return false;
           	             	}
               		       		//html全部拼完会出现一个undefined在最前面
            					if(htmlcode.indexOf("undefined")>=0){
            						htmlcode = htmlcode.substring(9,htmlcode.length);
            					}
               		            $("#advancedSearchList").append(htmlcode);
               	            },  
               	            error : function(data) {  
               	              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
               	            }  		           
               	      });
               			
               		}
               }

               //document.getElementById("result").innerHTML = text;  
           }  
           catch (e) {  
               //alert('touchMoveFunc：' + e.message);  
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
                    //alert('touchEndFunc：' + e.message);  
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
            window.onload = isTouchDevice; 
            
            //加载更多 
            function loadNext(){
            	  var curPage =$("#curPage").val();	
            	  $("#load"+curPage).hide();
           			$.ajax({
           				type : "GET",
           				async : false,    
           		        url: "${ctx}/mobile/index/advancedSearchSubmit",
           		        data: {	
           		        	curpage:curPage,
           		        	isJson:"true",
           		        	taxonomy:"${form.taxonomy}",
           		        	lcense:"${form.lcense}",
           		        	subFlag:"${form.subFlag}",
           		        	newFlag:"${form.newFlag}",
           		        	sortFlag:"${form.sortFlag}"
           		        		},
           		        success : function(data) { 
           		        	var htmlcode;
           		            var json = eval(data.list);
           		            if(json.length>0 ){
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
           		             	htmlcode+='<div class="mb20 fontFam oh">';
           		             	htmlcode+='<div class="fl w22">';
           		            	if(license==false && oa==false && free==false){
           		            		if(license == true){
           		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_open.png" />';
           		            		}
           		            		if(free == true){
           		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/>';
           		            		}
           		            		if(oa ==  true){
           		            			htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/>';
           		            		}
           		             	}
           		             	if(license==false){
           		             		htmlcode+='<img width="14" height="14" src="${ctx }/images/ico/ico_close.png" />';
           		             	}
      		             		if(json[i].type==1){
      		             			htmlcode+='<img width="14" height="14" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
      		             		}
      		             		if(json[i].type==4 || json[i].type==3){
      		             			htmlcode+='<img width="14" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
      		             		}
      		             		if(json[i].type==2 || json[i].type==6 || json[i].type==7){
      		             			htmlcode+='<img width="14" height="14" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
      		             		}
      		             		htmlcode+='</div>';
      		             		htmlcode+='<div class="fl wid90">';
      		             		htmlcode+='<h2><a title='+json[i].title + ' data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'" >'+json[i].title +' </a></h2>';
      		             		if(json[i].author!=null){
      		             			htmlcode+='<p>By '+json[i].author+' </p>';
      		             		}
       		             		if(json[i].publisher.name!=null){
       		             			htmlcode+='<p>'+json[i].publisher.name;
       		             			if(json[i].publisher.pubDate!=null){
       		             				var pubdate =json[i].publisher.pubDate.substring(0,4);
       		             			 	htmlcode+= '('+pubdate+')';
       		             			}
       		             			htmlcode+'</p>';
       		             		}
       		             		if(json[i].type==2 && json[i].startVolume !=null  && json[i].endVolume !=null){
       		             			htmlcode+'<p>';
       		         				htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume;
       		         				htmlcode+'</p>';
       		             		}
       		             		htmlcode+='</div>';
     		             		htmlcode+='</div>';
       		             	}	             	
       	             	}else{
       	             		return false;
       	             	}
           		       		//html全部拼完会出现一个undefined在最前面
        					if(htmlcode.indexOf("undefined")>=0){
        						htmlcode = htmlcode.substring(9,htmlcode.length);
        					}
           		            $("#advancedSearchList").append(htmlcode);
           	            },  
           	            error : function(data) {  
           	              	return false;
           	            }  		           
           	      });
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
        <h1 class="ui-title-h1">
        	<c:if test="${form.taxonomy!=null }">
        		分类法
        	</c:if>
        	<c:if test="${form.taxonomy==null }">
        		最新资源
        	</c:if>
        </h1>
    </div>
    <!-- 列表开始 -->
    <div class="order" id="advancedSearchList">
    <c:if test="${list!=null&&fn:length(list)>0}">
		<c:forEach items="${list}" var="p" varStatus="index">
					<c:set var="license">${(p.subscribedIp!=null||p.subscribedUser!=null)&&(p.subscribedIp>0||p.subscribedUser>0) }</c:set>
					<c:set var="oa">${p.oa!=null&&p.oa==2 }</c:set>
					<c:set var="free">${p.free!=null&&p.free==2 }</c:set>
					<c:set var="collection">${p.inCollection!=null&&p.inCollection>0 }</c:set>
					<c:set var="add1" value="${p.priceList!=null&&fn:length(p.priceList)>0&&p.free!=2&&p.oa!=2&&sessionScope.mainUser!=null && p.subscribedUser<=0&&(p.buyInDetail<=0&&p.exLicense>=0)}"/>
					<c:if test="${add1==false }">
						<c:set var="add" value="false"/>
					</c:if>
					<c:if test="${add1==true && p.subscribedIp>0 }">
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
	         <div class="mb20 fontFam oh">
	             <div class="fl w22">
					<c:if test="${license==true||oa==true||free==true }">
	               		<c:if test="${license==true }"><img width="14" height="14" src="${ctx }/images/ico/ico_open.png" /></c:if>
	               		<c:if test="${free==true }"><img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/></c:if>
						<c:if test="${oa==true }"><img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/></c:if>
               	 	</c:if>
               		<c:if test="${license==false }"><img width="14" height="14" src="${ctx }/images/ico/ico_close.png" /></c:if>
                    <c:if test="${p.type==1}"><img width="14" height="14" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
					<c:if test="${p.type==4 || p.type==3}"><img width="14" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
					<c:if test="${p.type==2||p.publications.type==6||p.publications.type==7}"><img width="14" height="14" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
	        </div>
	        <div class="fl wid90">
	        <h2><a title="${p.title }" data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/${p.id}" > ${fn:replace(fn:replace(fn:replace(p.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
	       <c:if test="${not empty p.author}">
				<p>By <c:set var="authors" value="${fn:split(p.author,',')}" ></c:set>
	                <c:forEach items="${authors}" var="a" >
	                ${a}&nbsp;
	                </c:forEach>
	            </p>
			</c:if>
		<c:if test="${not empty p.publisher.name}">
			<p>${p.publisher.name}<c:if test="${fn:substring(p.pubDate,0,4)!=null }">(${fn:substring(p.pubDate,0,4) })</c:if></p>
		</c:if>
		<c:if test="${p.type==2 && not empty p.startVolume && not empty p.endVolume}">
			<p>
			<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.endVolume }
			</p>
		</c:if>
    	</div>
      </div>
  	 </c:forEach>
  	 </c:if>
  	 <input type="hidden" value="${form.curpage }" id="curPage">
    </div>
   		<!--以下 提交查询Form 开始-->
		<form:form action="${form.url}" method="post" modelAttribute="form" commandName="form" name="formList" id="formList">
			<form:hidden path="searchsType" id="type1"/>
			<form:hidden path="searchValue" id="searchValue1"/>
			<form:hidden path="pubType" id="pubType1"/>
			<form:hidden path="language" id="language1"/>
			<form:hidden path="publisher" id="publisher1"/>
			<form:hidden path="pubDate" id="pubDate1"/>
			<form:hidden path="taxonomy" id="taxonomy1"/>
			<form:hidden path="taxonomyEn" id="taxonomyEn1"/>
			<form:hidden path="searchOrder" id="order1"/>
			<form:hidden path="lcense" id="lcense1"/>

			<form:hidden path="code" id="code1"/>
			<form:hidden path="pCode" id="pCode1"/>
			<form:hidden path="publisherId" id="publisherId1"/>
			<form:hidden path="subParentId" id="subParentId1"/>
			<form:hidden path="parentTaxonomy" id="parentTaxonomy1"/>
			<form:hidden path="parentTaxonomyEn" id="parentTaxonomyEn1"/>
		</form:form>
	<!--以上 提交查询Form 结束-->
    <!-- 列表结束 -->
   <jsp:include page="/footer" />
</div>
 		<c:if test="${list==null||fn:length(list)<=0 }">
			<ingenta-tag:LanguageTag key="Global.Label.Prompt.No.Product" sessionKey="lang"/>
		</c:if>
</body>
</html>
		