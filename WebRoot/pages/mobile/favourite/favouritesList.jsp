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
 				 $("#favouriteList").append('<div style="text-align: center;"><input type="hidden" id="next'+curPage+'" value="0" /><a href ="javascript:;" id="load'+curPage+'" onclick="loadNext();"  >加载更多</a></div>');
 				 return false;
 			}     
   			$.ajax({
   				type : "GET",
   				async : false,    
   		        url: "${ctx}/mobile/pages/favourites/form/favoritesJson",
   		        data: {	
   		        	curpage:curPage
   		        		},
   		        success : function(data) { 
   		        	var htmlcode;
   		            var json = eval(data.list);
   		            if(json.length>0){
   		            	$("#curPage").val(data.curpage);
   		             	for(var i=0;i<json.length;i++){
   		             		htmlcode+='<div class="mb20 fontFam oh">';
   		             		htmlcode+='<div class="fl w22">';
   		             		if(json[i].publications.type==1){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
   		             		}
   		             		if(json[i].publications.type==2  || json[i].publications.type==6|| json[i].publications.type==7){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
   		             		}
   		             		if(json[i].publications.type==4){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
   		             		}
   		             		if(json[i].publications.type==3){
		             			htmlcode+='<img  width="13" height="13"src="${ctx }/mobile/images/ico/infor.png" />';
		             		}

   		             		htmlcode+='</div>';
   		             		htmlcode+='<div class="fl wid90">';
   		             		htmlcode+='<h2><a title='+json[i].publications.title + ' data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/'+json[i].publications.id+'" >'+json[i].publications.title +' </a></h2>';
   		             		if(json[i].publications.author!=null){
   		             			htmlcode+='<p>By '+json[i].publications.author+' </p>';
   		             		}
   		             		if(json[i].publications.publisher.name!=null){
   		             			htmlcode+='<p>'+json[i].publications.publisher.name;
   		             			if(json[i].publications.pubDate!=null){
   		             				var pubdate =json[i].publications.pubDate.substring(0,4);
   		             			 	htmlcode+= '('+pubdate+')';
   		             			}
   		             			htmlcode+'</p>';
   		             		}
   		             		if(json[i].publications.type==2 && json[i].publications.startVolume !=null  && json[i].publications.endVolume !=null){
   		             			htmlcode+'<p>';
   		         				htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].publications.startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume;
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
   		            $("#favouriteList").append(htmlcode);
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
window.onload = isTouchDevice; 


function loadNext(){
	var curPage =$("#curPage").val();
	$("#load"+curPage).hide();
 			$.ajax({
 				type : "GET",
 				async : false,    
 		        url: "${ctx}/mobile/pages/favourites/form/favoritesJson",
 		        data: {	
 		        	curpage:curPage
 		        		},
   		        success : function(data) { 
   		        	var htmlcode;
   		            var json = eval(data.list);
   		            if(json.length>0){
   		            	$("#curPage").val(data.curpage);
   		             	for(var i=0;i<json.length;i++){
   		             		htmlcode+='<div class="mb20 fontFam oh">';
   		             		htmlcode+='<div class="fl w22">';
   		             		if(json[i].publications.type==1){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico4.png"  />';
   		             		}
   		             		if(json[i].publications.type==2  || json[i].publications.type==6|| json[i].publications.type==7){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico3.png" />';
   		             		}
   		             		if(json[i].publications.type==4){
   		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico5.png" " />';
   		             		}
   		             		if(json[i].publications.type==3){
   		             			htmlcode+='<img  width="13" height="13"src="${ctx }/mobile/images/ico/infor.png" />';
   		             		}
   		             		htmlcode+='</div>';
   		             		htmlcode+='<div class="fl wid90">';
   		             		htmlcode+='<h2><a title='+json[i].publications.title + ' data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/'+json[i].publications.id+'" >'+json[i].publications.title +' </a></h2>';
   		             		if(json[i].publications.author!=null){
   		             			htmlcode+='<p>By '+json[i].publications.author+' </p>';
   		             		}
   		             		if(json[i].publications.publisher.name!=null){
   		             			htmlcode+='<p>'+json[i].publications.publisher.name;
   		             			if(json[i].publications.pubDate!=null){
   		             				var pubdate =json[i].publications.pubDate.substring(0,4);
   		             			 	htmlcode+= '('+pubdate+')';
   		             			}
   		             			htmlcode+'</p>';
   		             		}
   		             		if(json[i].publications.type==2 && json[i].publications.startVolume !=null  && json[i].publications.endVolume !=null){
   		             			htmlcode+'<p>';
   		         				htmlcode+='<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].publications.startVolume +'-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />'+json[i].endVolume;
   		         				htmlcode+'</p>';
   		             		}
   		             		htmlcode+='</div>';
 		             		htmlcode+='</div>';
   		             	}	             	
   	             	}
   		       		//html全部拼完会出现一个undefined在最前面
					if(htmlcode.indexOf("undefined")>=0){
						htmlcode = htmlcode.substring(9,htmlcode.length);
					}
   		            $("#favouriteList").append(htmlcode);
   	            },  
   	            error : function(data) {  
   	              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
   	            }  		           
   	      });
	   			
}

</script>

</head>

<body>
<div data-role="page" data-theme="c" class="page" id="pageorder">
<!-- header -->
  <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:window.location.href='${ctx }/mobile/pages/user/usercenter'"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Pages.User.MyAccount.Label.favorites" sessionKey="lang" /></h1>
    </div>
    <!-- 列表开始 -->
    <div class="order" id="favouriteList">
    	<c:forEach items="${list}" var="p" varStatus="index">
	         <div class="mb20 fontFam oh">
	             <div class="fl w22">
	          	<c:if test="${p.publications.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
				<c:if test="${p.publications.type==2 || p.publications.type==6|| p.publications.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
				<c:if test="${p.publications.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
	         	<c:if test="${p.publications.type==3 }"><img src="${ctx }/mobile/images/ico/infor.png" class="vm" /></c:if>
	         </div>
	     <div class="fl wid90">
	       <h2><a title="${p.publications.title }" data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/${p.publications.id}" > ${fn:replace(fn:replace(fn:replace(p.publications.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
	       <c:if test="${not empty p.publications.author}">
				<p>By <c:set var="authors" value="${fn:split(p.publications.author,',')}" ></c:set>
	            <c:forEach items="${authors}" var="a" >
	                <a href='${ctx }/index/search?type=2&isAccurate=1&searchValue="${a}"'>${a}</a>&nbsp;
                </c:forEach></p>
			</c:if>
		<c:if test="${not empty p.publications.publisher.name}">
			<p><a href='${ctx }/index/search?type=2&isAccurate=1&searchValue="${p.publications.publisher.name }"'>${p.publications.publisher.name}</a><c:if test="${fn:substring(p.publications.pubDate,0,4)!=null }">(${fn:substring(p.publications.pubDate,0,4) })</c:if></p>
		</c:if>
		<c:if test="${p.publications.type==2 && not empty p.publications.startVolume && not empty p.publications.endVolume}">
			<p>
			<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.publications.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.publications.endVolume }
			</p>
		</c:if>
       </div>
	 </div>
   </c:forEach>
   <input type="hidden" value="${form.curpage }" id="curPage"/>
  </div>
    <!-- 列表结束 -->
   <jsp:include page="/footer" />
</div>
    
</body>
</html>
