<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
			<jsp:include page="/header" />
			<div data-role="content">
				<jsp:include page="${ctx}/welcome" />
				<div data-role="navbar" class="navDiv">
					<ul>
						<li class="curLi"><a href="/" data-ajax="false">首 页</a></li>
						<li><a href="${ctx}/mobile/pages/publications/journalList" data-ajax="false">期 刊</a></li>
						<li><a href="${ctx}/mobile/pages/publications/lastPubsBook?isCn=false" data-ajax="false">外文书</a></li>
						<li><a href="${ctx}/mobile/pages/publications/lastPubsBook?isCn=true" data-ajax="false">中文书</a></li>
					</ul>
				</div>
				<div class="newSource"><jsp:include page="/mobile/pages/index/freePub" flush="true" /></div>
				<div class="newSource"><h1 class="underH1">热读资源</h1><jsp:include page="${ctx}/indexHotReading" flush="true" /></div>
				<div class="newSource">
					<c:choose>
						<c:when test="${not empty insInfo}"><div id="lastPubs"></div></c:when>
						<c:otherwise>
							<h1 class="underH1">最新资源<a href="javascript:window.location.href='/mobile/index/advancedSearchSubmit?lcense=1&subFlag=1&newFlag=true&sortFlag=desc'"  style="float: right; font-size: 0.7em;">more>></a></h1><jsp:include page="${ctx}/indexNewest" flush="true" />
						</c:otherwise>
					</c:choose>
				</div>
				<c:if test="${null != sessionScope.mainUser}"><div id="recently_read" class="newSource"></div></c:if>
				<jsp:include page="/footer" />
			</div>
		</div>
	
		<script type="text/javascript">
		<!--
		$(function() {
			<c:if test="${not empty insInfo}">getLastPubs(5);</c:if>
			if (${null != sessionScope.mainUser}) { recentlyRead(5); }
		});
		function getLastPubs(val) {
			var parObj=$("#lastPubs");
			$.ajax({
				type : "POST",   
		        url: "${ctx}/pages/publications/lastPubs",
		        data: {
		        num:val      	
		        },
		        success : function(data) { 
	             	$(parObj).html(data);
	             	$(parObj).css("text-align","left");
	            },
	            error : function(data) {
	              	$(parObj).html(data);
	            }  
	      });
		}
		function recentlyRead(val) {
			var parObj = $("#recently_read");
			$.ajax({
				type : "POST",    
				url : "${ctx}/mobile/pages/index/recentlyRead",
				data : { num : val },
				success : function(data) { $(parObj).html(data); },  
				error : function(data) { $(parObj).html(data); }  
			});
		}
		//-->
		</script>
	</body>
</html>
