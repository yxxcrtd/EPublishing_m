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
var ctx='${ctx}';
function apply(){

	var uid = document.getElementById("uid").value;
	if(uid.replace(/\s+/g,"")==""){
		$("#tips4").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Prompt.userName'/>",1,'error');
		document.getElementById("uid").focus();
		return;
	}
	var email = document.getElementById("email").value;
	if(email.replace(/\s+/g,"")==""){
		$("#tips4").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Prompt.email'/>");
		document.getElementById("email").focus();
		return;
	}else{
		
	  	var myreg = /^([a-zA-Z0-9]+[\-|_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
           if(!myreg.test(email)){
        	   $("#tips4").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Prompt.effEmail'/>");
        	   document.getElementById("email").focus();
             	return false;
        	}
	
	}
	
	
	var pwd = document.getElementById("pwd").value;
	if(pwd.replace(/\s+/g,"")==""){
		 $("#tips4").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Prompt.pwd'/>",1,'error');
		document.getElementById("pwd").focus();
		return;
	}

	if(!document.getElementById("agree").checked){ 
		 $("#tips4").show().html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Prompt.agree'/>",1,'error');
	    return;
	};
	
	document.getElementById("form").submit();
}
</script>
<script type="text/javascript" src="${ctx}/js/checkPwd.js"></script>
<c:if test="${form.msg!=null&&form.msg != ''}">
	<script language="javascript">
	 $("#tips4").show().html('${form.msg}');
	</script>
</c:if>
</head>

<body>
<div data-role="page" data-theme="c" class="page">
	<jsp:include page="/header" />
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Pages.User.Register.Login" sessionKey="lang" /></h1>
    </div>
    <!-- 表单开始 -->
    <div class="mt10 mb30">
    	<div data-role="content">
       		<p><ingenta-tag:LanguageTag key="Pages.User.Registration.Label.prompt" sessionKey="lang" /></p>
           	<p style="margin-top: 10px;"><div id="tips4"></div></P>
        		<form:form id="form" commandName="form" action="registerSubmit">  
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="tableSign">
              <tr>
                <td align="right" width="35%"><span class="red">*</span> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Label.userName'/>：</td>
                <td><form:input path="account.uid" id="uid" style="vertical-align:middle"/></td>
              </tr>
              <c:forEach items="${list}" var="c" varStatus="index">
            	<c:if test="${c.display!='select'&& (c.code=='email')}">
            		<input type="hidden" name="typePropIds" value="${c.id}"/>
					<tr>
						<td align="right" class="tleft"><span class="red">*</span> <ingenta-tag:LanguageTag sessionKey='lang' key='${c.key}'/>：&nbsp;</td>
						<td colspan="3"><input type="text" id="${c.code}" name="propsValue" value="" style="vertical-align:middle"/><c:if test="${c.must!=1}"> </c:if></td>
					</tr>
            	</c:if>
			</c:forEach> 
              <tr>
                <td align="right"><span class="red">*</span> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Label.pwd'/>：</td>
                <td><form:password path="account.pwd" id="pwd" onkeyup="chkpwd(this)" style="vertical-align:middle"/></td>
              </tr>
               <tr>
                <td align="right"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Label.safe'/>：</td>
                <td><p id="chkResult" style="margin-top:7px;"></p></td>
              </tr>
              <tr>
                <td align="right"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Label.name'/>：</td>
                <td><input type="text" id="userName" name="userName" value=""/></td>
              </tr>
              <tr>
                <td align="right">&nbsp;</td>
                <td><input type="checkbox" class="checkbox" id="agree" name="agree"> <span class="pl15"><a href="${ctx}/agreement"><ingenta-tag:LanguageTag sessionKey='lang' key='Pages.User.Registration.Label.agree'/></a></span></td>
              </tr>
            </table> 
            <input type="hidden" name="userType" value="1"/>
            </form:form>
            <div class="tc mt30">
          	<a  href="javascript:;" onclick="apply();" class="login"><ingenta-tag:LanguageTag sessionKey='lang' key='Global.Button.Register'/></a>
          </div>  
    	</div>
    </div>
    <!-- 表单结束 -->
    <jsp:include page="/footer" />

</div>
</body>
</html>
