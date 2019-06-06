<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv='X-UA-Compatible' content='IE=edge'/>
		<title>易阅通</title>
		<%@ include file="/common/tools.jsp"%>
		<%@ include file="/common/ico.jsp"%>
		<script type="text/javascript">
		$(document).ready(function(){
			if('${form.msg}'!='null'&&'${form.msg}'!=''){
				alert('${form.msg}');
				if('${form.isSuccess}'=='true'){
					window.location.href="${ctx}/pages/configuration/form/manager";
				}
			}
		});
		function submit(){
			document.getElementById("form").submit();
		}
		function apply(){		
			if(document.getElementById("file").value==""){
				if(!confirm("<ingenta-tag:LanguageTag sessionKey="lang" key='Pages.Prompt.Institution.Upload.Check.file'/>")){
			     	return;
				}		
			}
			submit();		
		}
		</script>
	</head>
	<body>
	<div class="big">
		<!--以下top state -->
		<jsp:include page="/pages/header/headerData" flush="true" />
		<!--以上top end -->
		<!--以下中间内容块开始-->
		  <div class="main">
		  	<div class="login">
		     <h1>
		     	<ingenta-tag:LanguageTag sessionKey="lang" key="${form.obj.key }"/><ingenta-tag:LanguageTag key="Page.Configure.Lable.Infor.Manage" sessionKey="lang" />
		     </h1>
		     <form:form action="uploadSubmit" commandName="form" id="form" method="post" enctype="multipart/form-data">
		     <form:hidden path="obj.code" />
			 <form:hidden path="obj.type" />			
			 <form:hidden path="obj.content"/>
			 <form:hidden path="yyyy"/>
			 <form:hidden path="obj.key" />
			 <form:hidden path="obj.id" />
			 <form:hidden path="id"/>
			 <form:hidden path="mid" value="site_info"/>
		     <div class="t_list">
		     	<p>
		     		<span>
		     			<ingenta-tag:LanguageTag key="Page.Collection.Lable.Tips1" sessionKey="lang" /><ingenta-tag:LanguageTag sessionKey="lang" key="${form.obj.key }"/>。
		     		</span>
		     	</p>
		     	<p>
		     		<span>
		     			<ingenta-tag:LanguageTag sessionKey="lang" key="${form.obj.key }"/><ingenta-tag:LanguageTag key="Global.Button.Link" sessionKey="lang" />：
		     			<form:input path="obj.val" cssStyle="width:60%"/>
		     		</span>
		     	</p>
		     	<p>
		     		<span>
		     			<ingenta-tag:LanguageTag sessionKey="lang" key="${form.obj.key }"/><ingenta-tag:LanguageTag key="Page.Configure.Lable.Image" sessionKey="lang" />：
		     			<input type="file" id="file" name="file" style="width:100%"/>
		     		</span>
		     	</p>
		     	<p>
		     		<span>
		     			<a class="a_gret" onclick="apply();"><ingenta-tag:LanguageTag key="Global.Button.Save" sessionKey="lang" /></a>		     			
		     		</span>
		     	</p>
		     </div>
		     </form:form>
		    </div>
		    <!--左侧内容结束-->
		    <!--右侧菜单开始 -->
		    <jsp:include page="/pages/menu?mid=site_info" flush="true"/>
		    <!--右侧菜单结束-->
		  </div>
		  <!--以上中间内容块结束-->
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
		<!-- 底部的版权信息 -->
		<c:if test="${sessionScope.lang == 'zh_CN'}"><div id="footer_zh_CN"></div></c:if>
		<c:if test="${sessionScope.lang == 'en_US'}"><div id="footer_en_US"></div></c:if>
	</div>	
	</body>
</html>
