<%@page import="java.io.File"%>
<%@ page language="java" pageEncoding="UTF-8"%>
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
$(document).ready(function(){
function initMenu() {
  $('.menu ul').hide();
  $('.menu ul:first').show();
  $('.menu li a').click(
    function() {
      var checkElement = $(this).next();
      if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
        return false;
        }
      if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
        $('.menu ul:visible').slideUp('normal');
        checkElement.slideDown('normal');
        return false;
        }
      }
    );
  }
$(document).ready(function() {initMenu(); isTouchDevice();});
});

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
                 			if(curPage%5==0){
                 				if($("#next"+curPage).val()=="0"){
                 					return false;
                 				}
                 				 $("#enBookList").append('<div style="text-align: center;"><input type="hidden" id="next'+curPage+'" value="0" /><a href ="javascript:;" id="load'+curPage+'" onclick="loadNext();"  >加载更多</a></div>');
                 				 return false;
                 			}   
                 			$.ajax({
                 				type : "GET",
                 				async : false,    
                 		        url: "${ctx}/mobile/pages/publications/lastPubsBook",
                 		        data: {	
                 		        	curpage:curPage,
                 		        	isCn:"false",
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
                 		            if( typeof(json)!='undefined' && json.length>0){
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
                 		                   if(json[i].pubDate!=null && json[i].pubDate!="" ){
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
            		 	            	$("#enBookList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
            							return false;
                 	             	}
                 		            //html全部拼完会出现一个undefined在最前面
									if(htmlcode.indexOf("undefined")>=0){
										htmlcode = htmlcode.substring(9,htmlcode.length);
									}
                 		            $("#enBookList").append(htmlcode);
                 	            },  
                 	            error : function(data) {  
                 	            	if($("#nomore").length>0){
        		 	            		return false;
        		 	            	}
        		 	            	$("#enBookList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
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
            
     

			//加载更多
			function loadNext(){
				 try  {  
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
              			$.ajax({
              				type : "GET",
              				async : false,    
              		        url: "${ctx}/mobile/pages/publications/lastPubsBook",
              		        data: {	
              		        	curpage:curPage,
              		        	isCn:"false",
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
              		                   if(json[i].pubDate!=null && json[i].pubDate!="" ){
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
    			 	            	$("#enBookList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
    								return false;
              	             	}
              		            //html全部拼完会出现一个undefined在最前面
									if(htmlcode.indexOf("undefined")>=0){
										htmlcode = htmlcode.substring(9,htmlcode.length);
									}
              		            $("#enBookList").append(htmlcode);
              	            },  
              	            error : function(data) {  
              	            	if($("#nomore").length>0){
			 	            		return false;
			 	            	}
			 	            	$("#enBookList").append('<div style="text-align: center;"><a href ="javascript:;" id="nomore">暂无更多数据</a></div>');
								return false;
              	            }  		           
              	      });
              			
              }

          catch (e) {  
              alert('touchMoveFunc：' + e.message);  
          }  
	}
  
			//筛选
			function searchByCondition(type,value){
				var verify=null;
				if(type=="type"){
					$("input[name=pubType]").val(value);
				}else if(type=="publisher"){
					//$("input[name=publisher]").val(value); 
					//增加左侧链接标识
					$("input[name=publisher]").val("_"+value); 
				}else if(type=="pubDate"){
					$("input[name=pubDate]").val(value);
				}else if(type=="taxonomy"){
					var tax=$("input[name=taxonomy]").val();
					var s=$("#taxonomy2").val();
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

				var lcense = $("#lcense2").val();
				$("input[name='curpage']").val(0);
				$("#order2").val('');
				if(verify!=1){
				document.formList.action="${ctx}/mobile/pages/publications/lastPubsBook";
				document.formList.submit();
				}
			}
			
    </script>  

</head>

<body>
<div data-role="page" data-theme="c" class="page">
  	 <!-- 所有页面引用的顶部 -->
	<jsp:include page="/header" />
  <div data-role="content">
  	<jsp:include page="${ctx}/welcome" />
    <div data-role="navbar" class="navDiv">
        <ul>
          <li><a href="${ctx}/index" data-ajax="false">首页</a></li>
          <li><a href="${ctx}/mobile/pages/publications/journalList" data-ajax="false">期刊</a></li>
          <li class="curLi"><a href="${ctx}/mobile/pages/publications/lastPubsBook?isCn=false" data-ajax="false">外文书</a></li>
          <li><a href="${ctx}/mobile/pages/publications/lastPubsBook?isCn=true" data-ajax="false">中文书</a></li>
        </ul>
  	</div>
    <!-- 中间内容部分开始 -->
    <div class="ui-grid-b resource">
    	<div class="ui-block-a" style="width:35%;"><a href="javascript:;" onclick="searchByCondition('searchOrder','pubDate')" >出版时间 <img id="pubDate" src="${ctx}/mobile/images/icoUp.png" class="vm"></a></div>
        <div class="ui-block-b" style="width:35%;"><a href="javascript:;"onclick="searchByCondition('searchOrder','createOn')" >上架时间 <img id="ShelvesDate" src="${ctx}/mobile/images/icoUp.png" class="vm"></a></div>
        <div class="ui-block-c popup"  data-role="collapsible" style="width:30%;">
        	<h1><a class="updown" href="#">筛选</a></h1>
            <div class="screenList">
            	<ul class="menu">
                	<li><a href="#"><h2>资源</h2></a>
                    	<ul>
                        	<li><a href="javascript:void(0)" onclick="searchByCondition('selectflag','all')" >全部资源</a></li>
                            <c:if test="${sessionScope.mainUser!=null }">
                           	 	<li><a href="javascript:void(0)" onclick="searchByCondition('selectflag','license')" >订阅资源</a></li>
                            </c:if> 
                            <li><a href="javascript:void(0)" onclick="searchByCondition('selectflag','oaFree')" >免费资源</a></li>
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
    <div class="oh mb20" id="enBookList">
    <c:forEach items="${list }" var="b">
    	<c:set var="license">${(b.subscribedIp!=null||b.subscribedUser!=null)&&(b.subscribedIp>0||b.subscribedUser>0) }</c:set>
			<c:set var="news">${b.latest!=null&&b.latest>0 }</c:set>
			<c:set var="oa">${b.oa!=null&&b.oa==2 }</c:set>
			<c:set var="free">${b.free!=null&&b.free==2 }</c:set>
			<c:set var="collection">${b.inCollection!=null&&b.inCollection>0 }</c:set>
			<c:set var="add1" value="${b.priceList!=null&&fn:length(b.priceList)>0&&b.free!=2&&b.oa!=2&&sessionScope.mainUser!=null && b.subscribedUser<=0&&(b.buyInDetail<=0&&b.exLicense>=0)}"/>
			<c:if test="${add1==false }">
				<c:set var="add" value="false"/>
			</c:if>
			<c:if test="${add1==true &&b.subscribedIp>0 }">
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
			<c:if test="${add1==true &&(b.subscribedIp==null||b.subscribedIp<=0) }">
				<c:set var="add" value="true"/>
			</c:if>
			<c:if test="${add1==false }">
				<c:set var="add" value="false"/>
			</c:if>
			<c:set var="favourite" value="${sessionScope.mainUser!=null&&b.favorite<=0 }"/>
			<c:set var="recommand" value="${(b.recommand>0 || sessionScope.mainUser.institution!=null) &&(b.subscribedIp==null||b.subscribedIp<=0)&&(b.free!=2&&b.oa!=2)}"/>	
			<c:if test="${license==false&&oa==false&&free==false }">
				<div class="fl w40 mt2">
					<p class="p_left">
					<img src="${ctx }/images/ico/ico_close.png" />
					
					<c:if test="${b.type==1 }">
						<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />
					</c:if>
					<c:if test="${b.type==2||b.type==6||b.type==7 }">
						<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />
					</c:if>
					<c:if test="${b.type==4 }">
						<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />
					</c:if>
					<%-- <a name="title" href="${ctx}/pages/publications/form/article/${b.id}?sv=${form.searchValue}&fp=2" title="${b.title }">${b.title }</a> --%>
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
			
 	
    	<div class="booklist oh">
        		<div class="fl w40">
		           <c:if test="${license==true||oa==true||free==true }">
				<div class="fl w40 mt2">
					<c:if test="${free==true }"><img width="14" height="14" src="${ctx }/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />"/></c:if>
					<c:if test="${oa==true }"><img width="14" height="14" src="${ctx }/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />"/></c:if>
					<c:if test="${license==true }"><img width="14" height="14" src="${ctx }/images/ico/ico_open.png" /></c:if>
					<c:if test="${b.type==1 }">
						<img src="${ctx }/images/ico/ico4.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" />
					</c:if>
					<c:if test="${b.type==2||b.type==6||b.type==7 }">
						<img src="${ctx }/images/ico/ico3.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" />
					</c:if>
					<c:if test="${b.type==4 }">
						<img src="${ctx }/images/ico/ico5.png" width="14" height="14" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" />
					</c:if>
				</div>
			</c:if>	</div>
            <div class="fl wid85">
                <h1> <a href="${ctx}/mobile/pages/publications/form/article/${b.id}" data-ajax="false">${b.title }</a></h1>
                <c:if test="${not empty b.author}">
               		<p>By <c:set var="authors" value="${fn:split(b.author,',')}" ></c:set>
		                <c:forEach items="${authors}" var="a" >
		                	${a}&nbsp;
		                </c:forEach>
		            </p>
                </c:if>
                <p>
					${b.publisher.name}
					<c:if test="${not empty fn:substring(b.pubDate,0,4)}">(${fn:substring(b.pubDate,0,4) })</c:if></p>
                <c:if test="${b.type==2 && not empty b.startVolume && not empty b.endVolume}">
				<p>
				<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${b.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${b.endVolume }
				</p>
				</c:if>
            </div>
        </div>
      </c:forEach>
      <input type="hidden" id="curPage" value="${form.curpage }" />
 	  <form:form action="${form.url}" method="post" modelAttribute="form" commandName="form" name="formList" id="formList">
			<form:hidden path="language" id="language2"/>
			<form:hidden path="publisher" id="publisher2"/>
			<form:hidden path="pubDate" id="pubDate2"/>
			<form:hidden path="taxonomy" id="taxonomy2"/>
			<form:hidden path="taxonomyEn" id="taxonomyEn2"/>
			<form:hidden path="searchOrder" id="searchOrder"/>
			<form:hidden path="lcense" id="lcense2"/>
	        <form:hidden path="title" id="title1"/>
	        <form:hidden path="pageCount" id="pageCount1"/>
	        <form:hidden path="selectflag" id="selectflag"/>
	         <form:hidden path="sortFlag" id="sortFlag"/>
	</form:form>
	</div>
    <!-- 分页条开始 
    <div class="pageLink">
    	<a href="#">< 上一页</a>
        <a class="curA" href="#">1</a>
        <a href="#">2</a>
        <a href="#">3</a>
		...
        <a href="#">189</a>
        <a href="#">下一页 ></a>
    </div>分页条结束 -->
    <!-- 中间内容部分结束 -->
     <jsp:include page="/footer" />
</div>
<!-------------------------  第三个页面结束 ------------------------------------->

</body>
</html>