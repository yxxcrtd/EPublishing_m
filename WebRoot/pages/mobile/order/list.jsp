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
</head>

<body>
<div data-role="page" data-theme="c" class="page" id="pageorder">
<!-- header -->
  <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Page.Order.List.Lable.MyOrder" sessionKey="lang" /></h1>
    </div>
    <!-- 列表开始 -->
    <div class="order">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="orderTab mt20">
       	<tr>
            <th class="pl50"><ingenta-tag:LanguageTag key="Page.Order.List.Lable.SN" sessionKey="lang" /></th>
            <th width="30%" class="tc"><ingenta-tag:LanguageTag key="Page.Order.List.Lable.Date" sessionKey="lang" /></th>
            <th width="30%" class="tc"><ingenta-tag:LanguageTag key="Page.Order.List.Lable.TotalPrice" sessionKey="lang" /></th>
          </tr>
          <tbody id="orderList">
           <c:forEach items="${list}" var="o" varStatus="index">
	          <tr>
	            <td  style="text-align:left;"><a data-ajax="false" href="${ctx}/mobile/pages/order/form/detail?id=${o.id}" title="${o.code}">${o.code}</a></td>
	            <td   style="text-align:left;">
	            <fmt:formatDate value="${o.createdon}" pattern="yyyy-MM-dd"/>
	            </td>
	            <td  style="text-align: left; padding-left: 50px;">${o.currency}&nbsp;<fmt:formatNumber value="${o.salePriceExtTax }" pattern="###,###0.00"/></td>
	          </tr>
         </c:forEach>
         </tbody>
        </table>
        <input id="curPage" type="hidden" value="${form.curpage }" />	
    </div>
    <!-- 列表结束 -->
   <jsp:include page="/footer" />
</div>
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
                     				 $("#orderList").append('<div style="text-align: center;"><input type="hidden" id="next'+curPage+'" value="0" /><a href ="javascript:;" id="load'+curPage+'" onclick="loadNext();"  >加载更多</a></div>');
                     				 return false;
                     			}     
                    			$.ajax({
                    				type : "GET",
                    				async : false,    
                    		        url: "${ctx}/mobile/pages/order/form/jsonList",
                    		        data: {	
                    		        	curpage:curPage
                    		        		},
                    		        success : function(data) { 
                    		            var json = eval(data.list);
                    		            pubtype=data.pubtype;
                    		            var htmlcode;	
                    		            if(json.length>0){
                    		            	$("#curPage").val(data.curpage);
                    		             	for(var i=0;i<json.length;i++){
                    		             		htmlcode+='<tr>';
                    		             		htmlcode +='<td class="p120" style="padding-left: 19px;" ><a data-ajax="false" href="${ctx}/mobile/pages/order/form/detail?id='+json[i].code+'" title="'+json[i].code+'" class="ui-link">'+json[i].code+'</a></td>';
                    		             		//alert(json[i].createdonStr);
                    		             		htmlcode +='<td class="tc">'+json[i].createdonStr+'</td>';
                    		             		var price =json[i].salePriceExtTax.toFixed(2);
                    		             		htmlcode +='<td class="tc" style="text-align: left; padding-left: 50px;">'+json[i].currency+'&nbsp;'+price+'</td>';
                    		             		htmlcode +='</tr>';
                    		             	}	             	
                    	             	}else{
                    	             		return false;
                    	             	}

                    		            $("#orderList").append(htmlcode);
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
          		        url: "${ctx}/mobile/pages/order/form/jsonList",
          		        data: {	
          		        	curpage:curPage
          		        		},
          		        success : function(data) { 
          		            var json = eval(data.list);
          		            pubtype=data.pubtype;
          		            var htmlcode;	
          		            if(json.length>0){
          		            	$("#curPage").val(data.curpage);
          		             	for(var i=0;i<json.length;i++){
          		             		htmlcode+='<tr>';
          		             		htmlcode +='<td class="p120" style="padding-left: 19px;" ><a data-ajax="false" href="${ctx}/mobile/pages/order/form/detail?id='+json[i].code+'" title="'+json[i].code+'" class="ui-link">'+json[i].code+'</a></td>';
          		             		//alert(json[i].createdonStr);
          		             		htmlcode +='<td class="tc">'+json[i].createdonStr+'</td>';
          		             		var price =json[i].salePriceExtTax.toFixed(2);
          		             		htmlcode +='<td class="tc" style="text-align: left; padding-left: 50px;">'+json[i].currency+'&nbsp;'+price+'</td>';
          		             		htmlcode +='</tr>';
          		             	}	             	
          	             	}else{
          	             		return false;
          	             	}

          		            $("#orderList").append(htmlcode);
          	            },  
          	            error : function(data) {  
          	              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
          	            }  		           
          	      });
                			
            }
    </script>  
</body>
</html>
