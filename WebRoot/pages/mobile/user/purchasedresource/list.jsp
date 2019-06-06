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


               //判断滑动方向  
               
               var curPage =$("#curPage").val();	
               if(curPage=="" || curPage=="0"){
               	curPage=1;
               }
               if(startY - y >50){
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
             				 $("#purResource").append('<div style="text-align: center;"><input type="hidden" id="next'+curPage+'" value="0" /><a href ="javascript:;" id="load'+curPage+'" onclick="loadNext();"  >加载更多</a></div>');
             				 return false;
             			}     
               			$.ajax({
               				type : "GET",
               				async : false,    
               		        url: "${ctx}/mobile/pages/user/purchasedResourceJson",
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
               		             		if(json[i].type==1){
               		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
               		             		}
               		             		if(json[i].type==2  || json[i].type==6|| json[i].type==7){
               		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
               		             		}
               		             		if(json[i].type==4){
               		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
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
               		            $("#purResource").append(htmlcode);
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
      		        url: "${ctx}/mobile/pages/user/purchasedResourceJson",
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
      		             		if(json[i].type==1){
      		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />';
      		             		}
      		             		if(json[i].type==2  || json[i].type==6|| json[i].type==7){
      		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />';
      		             		}
      		             		if(json[i].type==4){
      		             			htmlcode+='<img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />';
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
      		            $("#purResource").append(htmlcode);
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
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:window.location.href='${ctx }/mobile/pages/user/usercenter'"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Page.Users.Publications.Purchased.Resource" sessionKey="lang" /></h1>
    </div>
    <!-- 列表开始 -->
    <div class="order" id="purResource">
		<c:forEach items="${list}" var="p" varStatus="index">
	         <div class="mb20 fontFam oh">
	             <div class="fl w22">
	        <c:if test="${p.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" /></c:if>
			<c:if test="${p.type==2 || p.type==6|| p.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" /></c:if>
			<c:if test="${p.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png"  /></c:if>
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
			<p>${p.publisher.name}<c:if test="${fn:substring(p.pubDate,0,4)!=null }">(${fn:substring(p.publications.pubDate,0,4) })</c:if></p>
		</c:if>
		<c:if test="${p.type==2 && not empty p.startVolume && not empty p.endVolume}">
			<p>
			<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.endVolume }
			</p>
		</c:if>
    	</div>
      </div>
  	 </c:forEach>
  	 <input type="hidden" value="${form.curpage }" id="curPage">
    </div>
    <!-- 列表结束 -->
   <jsp:include page="/footer" />
</div>
    
</body>
</html>
