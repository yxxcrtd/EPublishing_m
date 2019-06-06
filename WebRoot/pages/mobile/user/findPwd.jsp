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
<script type="text/javascript" src="${ctx}/js/checkPwd.js"></script>

<script language="javascript">
function apply(){
	var email = document.getElementById("email").value;
	
	var uid = document.getElementById("uid").value;
	if(uid.replace(/\s+/g,"")==""&&email.replace(/\s+/g,"")==""){
		$("#tips3").show().html("请填写您的注册邮箱！",1,'error');
		return;
	}else if(email.replace(/\s+/g,"")!=""){
		
	  	var myreg = /^([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
           if(!myreg.test(email)){
        	   $("#tips3").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.FindPwd.Prompt.tip'/>\n\n<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.FindPwd.Prompt.effemail'/>",1,'error');
                //myreg.focus();
             	return;
        	}
	
	}
		$.post("${ctx}/mobile/pages/user/form/findPwdSubmit", {uid:document.getElementById("uid").value,email:document.getElementById("email").value},
			function (data, textStatus){
				$("#tips3").show().html(data,1,'error');
			}
		);
		
}
</script>

</head>

<body>
<div data-role="page" data-theme="c" class="page">
  <jsp:include page="/header" flush="true" />
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="${ctx }/index"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Pages.User.FindPwd.title" sessionKey="lang" /></h1>
        
    </div>
    <!--定义01 mainContainer 内容区开始-->
  <div class="main personMain h700">
    <!--定义 0101 头部边框-->
        	
      <!--定义 0102 左边内容区域 开始-->

		<%-- <div class="zhuce_xinxi" style="width:80%; margin-left:100px;">
		<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.FindPwd.Label.title.detail'/> --%>
		<p>此途径仅供个人用户使用，提交后系统会将密码发送到您所填写的邮箱注册邮箱，请注意查收。</p>
    	<p class="red">如果您想知道您机构用户的密码，请联系您的管理员。</p>
    	<p style="margin-top: 10px; "><div id="tips3" style="margin: 0 25px 14px;"></div></p>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="regTable mt10">				
				<%-- <tr>
					<td class="tleft"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.FindPwd.Label.userName'/>：&nbsp;
					</td>
					<td><input type="text" id="uid"/></td>
				</tr> --%>
				<input type="hidden" id="uid"/>
				<tr>
					<td width="40" align="right"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.FindPwd.Label.email'/>:&nbsp;
					</td>
					<td colspan="3"><input type="text" id="email"/><span class="red">请填写您的注册邮箱</span></td>
				</tr>	
				<tr>
					<td align="right">&nbsp;</td>
					<td  colspan="3">
						 <p class="mt10"><a href="javascript:void(0)"  class="orgingA" onclick="apply();" style="text-align: center;"><ingenta-tag:LanguageTag sessionKey='lang' key='Global.Button.Submit'/></a></p>
					</td>
				</tr>		
			</table>
		<!-- </div> -->

      <!--定义 0102 左边内容区域 结束-->
  </div>
      <div class="boderBottom"></div>
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
  <!--定义01 mainContainer 内容区结束-->
  		<!-- 底部的版权信息 -->
		<c:if test="${sessionScope.lang == 'zh_CN'}"><div id="footer_zh_CN"></div></c:if>
		<c:if test="${sessionScope.lang == 'en_US'}"><div id="footer_en_US"></div></c:if>
  <input type="hidden" name="userType" value="1"/><!-- 用户类型为1：个人用户 -->
</div>

</body>
</html>
