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
<div data-role="page" data-theme="c" class="page">
<!-- header -->
  <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
    <!-- 章节列表开始 -->
    <div class="captList">
    	<div class="oh perUser">
    		<div class="fl"><a href="${ctx}/mobile/pages/user/usercenter" data-ajax="false">${sessionScope.mainUser.name}</a> | <a href="#" data-ajax="false">账户余额</a></div>
            <div class="fr"><a href="${ctx}/mobile/pages/user/form/resetPwd" data-ajax="false">修改密码</a> | <a href="javascript:void(0);" onclick="loginout()"  data-ajax="false">退出</a></div>
        </div>
        <div class="mt20 mb50">
        	<!-- 已购资源开始 -->
            <div data-role="collapsible" class="mb15">
              <h1 class="oh"><span class="fl"><ingenta-tag:LanguageTag sessionKey='lang' key='Page.Users.Publications.Purchased.Resource'/></span>
              <span class="fr" id="moreResource">全部</span></h1>
              <div class="mt20">
              <c:forEach items="${pubList }" var="d">
                <div class="mb20 fontFam oh">
                    <div class="w22 fl">
                   	 	<c:if test="${d.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
						<c:if test="${d.type==2 || d.type==6|| d.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
						<c:if test="${d.type==3 }"><img src="${ctx }/mobile/images/ico/infor.png" class="vm" /></c:if>	
						<c:if test="${d.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
                     </div>
                   	 <div class="fl wid90">
			        <c:if test="${d.type!=99 }">
				        <h2><a href="${ctx}/mobile/pages/publications/form/article/${d.id}">${fn:replace(fn:replace(fn:replace(d.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
				        <c:if test="${d.author!=''&& d.author!=null}"><p> By ${d.author }</p></c:if>
           	             <p>${d.publisher.name }</p>
			        </c:if>
                    </div>
                </div>
               </c:forEach>
               <c:if test="${empty pubList}">
               			暂无资源
               </c:if>
              </div>
            </div>
            <!-- 已购资源结束 -->
            <!-- 我的收藏夹开始 -->
            <div data-role="collapsible" class="mb15">
              <h1 class="oh"><span class="fl"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Favorites.title'/></span> <span class="fr" id="moreFavourate">全部</span></h1>
              <div class="mt20">
              		 <c:forEach items="${flist}" var="f" varStatus="index">
	                <div class="mb20 fontFam oh">
	                    <div class="fl w22">
	                    			<c:if test="${f.publications.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
									<c:if test="${f.publications.type==2 || p.publications.type==6|| p.publications.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
									<c:if test="${f.publications.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
                 					<c:if test="${f.publications.type==3 }"><img src="${ctx }/mobile/images/ico/infor.png" class="vm" /></c:if>			
	                   					
	                    </div>
	                    <div class="fl wid90">
	                        <h2><a title="${f.publications.title }" data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/${f.publications.id}" > ${fn:replace(fn:replace(fn:replace(f.publications.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
	                        <c:if test="${not empty f.publications.author}">
										<p>By <c:set var="authors" value="${fn:split(f.publications.author,',')}" ></c:set>
								                <c:forEach items="${authors}" var="a" >
								                <a href='${ctx }/index/search?type=2&isAccurate=1&searchValue="${a}"'>${a}</a>&nbsp;
								                </c:forEach></p>
									</c:if>
									<c:if test="${not empty f.publications.publisher.name}">
										<p><a href='${ctx }/index/search?type=2&isAccurate=1&searchValue="${f.publications.publisher.name }"'>${f.publications.publisher.name}</a><c:if test="${fn:substring(f.publications.pubDate,0,4)!=null && fn:substring(f.publications.pubDate,0,4)!='' }">(${fn:substring(f.publications.pubDate,0,4) })</c:if></p>
									</c:if>
									<c:if test="${f.publications.type==2 && not empty f.publications.startVolume && not empty f.publications.endVolume}">
										<p>
										<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${f.publications.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${f.publications.endVolume }
										</p>
									</c:if>
	                    </div>
	                </div>
                	 </c:forEach>
                <c:if test="${empty flist}">
              		暂无资源
               	</c:if>
              </div>
            </div>
            <!-- 我的收藏夹结束-->
            <!-- 我的订单开始 -->
            <div data-role="collapsible" class="mb15">
              <h1 class="oh"><span class="fl" ><ingenta-tag:LanguageTag key="Page.Order.List.Lable.MyOrder" sessionKey="lang" /></span> <span class="fr" id="moreOrder">更多</span></h1>
              <div class="mt20">
					<c:if test="${!empty flist }">
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="orderTab">
						<td width="42%" align="center" style="font-weight: bold;">订单号</td>
				        <td width="30%" style="font-weight: bold; text-align: center;">订单日期</td>
				        <td width="28%" style="font-weight: bold; text-align: left; padding-right: 3px;">订单总金额</td>
			          	<c:forEach items="${olist}" var="o" varStatus="index">
				          <tr>
				            <td width="42%" style="text-align: center;"><a data-ajax="false" href="${ctx}/mobile/pages/order/form/detail?id=${o.id}" title="${o.code}">${o.code}</a></td>
				            <td width="30%" style="text-align: center;"><fmt:formatDate value="${o.createdon}" pattern="yyyy-MM-dd"/></td>
				            <td width="28%" style="text-align: left;">${o.currency}&nbsp;<fmt:formatNumber value="${o.salePriceExtTax }" pattern="###,###0.00"/></td>
				          </tr>
			          	</c:forEach>
			        </table>
					</c:if>
			     <c:if test="${empty flist}">
              		暂无资源
               	</c:if>
              </div>
            </div>
            <!-- 我的订单结束 -->
            <!-- 最近阅读开始-->
            <div data-role="collapsible" class="perCent" class="mb15">
            		<h1 class="oh"><span class="fl"><ingenta-tag:LanguageTag key="Global.Label.Recently_Read" sessionKey="lang"/></span></h1>
              <div class="mt20">
              		<c:forEach items="${list}" var="p" varStatus="index">
             		<c:set var="license">${(p.publications.subscribedIp!=null||p.publications.subscribedUser!=null)&&(p.publications.subscribedIp>0||p.publications.subscribedUser>0) }</c:set>
						<c:set var="oa">${p.publications.oa!=null&&p.publications.oa==2 }</c:set>
						<c:set var="free">${p.publications.free!=null&&p.publications.free==2 }</c:set>
                <div class="mb20 fontFam oh">
                    <div class="w22 fl">
                    			<c:if test="${p.publications.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
									<c:if test="${p.publications.type==2 || p.publications.type==6|| p.publications.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
									<c:if test="${p.publications.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
                    </div>
                    <div class="fl wid90">
                        <h2><a class="a_title" data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/${p.publications.id}" title="${p.publications.title}">${fn:replace(fn:replace(fn:replace(p.publications.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
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
                <c:if test="${empty list}">
              		暂无资源
               	</c:if>
              </div>
            </div>
            <!-- 最近阅读结束 -->
        </div>
        
    </div>
    <!-- 章节列表结束 -->
    <jsp:include page="/footer" />	
</div>
<script type="text/javascript">
	$(function(){
		$("#moreOrder").click(function(){
			window.location.href="${ctx}/mobile/pages/order/form/list";
		});

		$("#moreResource").click(function(){
			window.location.href="${ctx}/mobile/pages/user/purchasedResource";
		});
		
		$("#moreFavourate").click(function(){ 
			window.location.href="${ctx}/mobile/pages/favourites/form/favorites";
		});
	});
</script>
</body>
</html>
