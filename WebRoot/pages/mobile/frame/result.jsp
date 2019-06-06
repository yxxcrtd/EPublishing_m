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
  	<div>
	  	<div >
	     <h1>${prompt}</h1>
	     
	        <div class="zhuce_xinxi" style="text-align: center;float:none;padding-top: 80px;">
	          <p>${message}</p>	         
	        </div>

	    </div>
	    <!-- 底部的版权信息 -->
		<jsp:include page="/footer"/>
</div>
		<!--以下 提交查询Form 开始-->
		<c:if test="${form==null }">
	  	<form action="" method="post" name="form" id="formList">
			<input type="hidden" name="searchsType" id="type1"/>
			<input type="hidden" name="searchValue" id="searchValue1"/>
			<input type="hidden" name="pubType" id="pubType1"/>
			<input type="hidden" name="publisher" id="publisher1"/>
			<input type="hidden" name="pubDate" id="pubDate1"/>
			<input type="hidden" name="taxonomy" id="taxonomy1"/>
			<input type="hidden" name="taxonomyEn" id="taxonomyEn1"/>
			<input type="hidden" name="searchOrder" id="order1"/>
			<input type="hidden" name="lcense" id="lcense1"/>
			
			<input type="hidden" name="code" id="code1"/>
			<input type="hidden" name="pCode" id="pCode1"/>
			<input type="hidden" name="publisherId" id="publisherId1"/>
			<input type="hidden" name="subParentId" id="subParentId1"/>
			<input type="hidden" name="parentTaxonomy" id="parentTaxonomy1"/>
			<input type="hidden" name="parentTaxonomyEn" id="parentTaxonomyEn1"/>
		</form>
	  </c:if>
	  <c:if test="${form!=null }">
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
		</c:if>
		
		
		</div>
	</body>
</html>
