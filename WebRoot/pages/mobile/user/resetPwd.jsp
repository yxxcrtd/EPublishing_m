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
<script type="text/javascript">
var ctx='${ctx}';
	function apply() {
		var pwd = document.getElementById("pwd").value;
		if (pwd.replace(/\s+/g, "") == "") {
			alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.ResetPwd.Prompt.pwd'/>");
			document.getElementById("pwd").focus();
			return;
		}
		if (document.getElementById("pwd").value != document.getElementById("pwdCheck").value) {
			alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.ResetPwd.Prompt.twopwd'/>");
			return;
		}
		document.getElementById("form").submit();
	}
	</script>
</head>

<body>
<div data-role="page" data-theme="c" class="page">
<!-- header -->
 <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="${ctx}/mobile/pages/user/usercenter"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag sessionKey='lang'
												key='Pages.User.ResetPwd.title2' /></h1>
    </div>
    <!-- 表单开始 -->
    <div class="mt10 mb30">
    	<div data-role="content">  
    			<form:form id="form" commandName="form" action="resetPwdSubmit" method="post">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="tableSign">
              <tr>
                <td align="right" width="35%"> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.ResetPwd.Label.name' />：</td>
                <td>${form.account.uid}</td>
              </tr>
              <tr>
                <td align="right"><span class="red">*</span> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.ResetPwd.Label.pwd' />：</td>
                <td><form:password path="account.pwd" id="pwd" onkeyup="chkpwd(this)" style="vertical-align:middle"/> </td>
              </tr>
               <tr>
                <td align="right"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.ResetPwd.Label.safe' />：</td>
                <td><p id="chkResult" style="margin-top:7px;"></td>
              </tr>
              <tr>
                <td align="right"><span class="red">*</span> 重复密码 ：</td>
                <td><input type="password" id="pwdCheck" name="pwdCheck" style="vertical-align:middle"/> </td>
              </tr>
            </table> 
             <form:hidden path="account.id" />
               </form:form>
            <div class="tc mt30">
          	<a href="javascript:void(0)" class="login" onclick="apply();"><ingenta-tag:LanguageTag sessionKey='lang' key='Global.Button.Submit'/></a>
          </div>  
    	</div>
    </div>
    <!-- 表单结束 -->
     <jsp:include page="/footer" />

</div>
</body>
</html>
