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
</head>
<script type="text/javascript">
$(document).ready(function(){ 
	if ($.cookie("uname") !=null) { 
		$("#chkuser").prop("checked", true); 
		$("#loginid").val($.cookie("uname")); 
	} 
}); 

function saveLoginId() { 
	if ($("#chkuser").prop("checked")) { 
			var username = $("#loginid").val(); 
			$.cookie("uname", "true", { expires: 7 }); //存储一个带7天期限的cookie 
			$.cookie("uname", username, { expires: 7 }); 
		}else{ 
			$.cookie("uname", "false", { expire: -1 }); 
			$.cookie("uname", "", { expires: -1 }); 
		} 
	}; 

	$(function() {
		document.onkeydown = function(e) {
			var evt = document.all ? window.event : e;
			if(13 == evt.keyCode) {
				signin();
			}
		}
		
	});
		function signin(){
			saveLoginId();
			$.ajax({
	  			type : "POST",  
				url: "${ctx}/mobile/pages/user/form/login",
				data: {
					uid:$("#loginid").val(),
					pwd:$("#loginpw").val(),
					beforPath:$("#beforPath").val(),
					r_ : new Date().getTime()
				},
				success : function(data) {
				    var s = data.split("::");			     
				    if(s[0]=="success"){
				    	art.dialog.tips(s[1],1,'success');
				    	if (0 < s[2].indexOf("/pages/user/form/register")) {
				    		location="/";
				    	} else if(0 < s[2].indexOf("/index/search")){
				    		location="/";
				    	} else if(0 < s[2].indexOf("/pages/user/form/newLogin")){
				    		location="/";
				    	}else if(0 < s[2].indexOf("/pages/user/form/newLogin")){
				    		location="/";
				    	}else if(s[2]==""){
				    		location="/";
				    	}else  {
				    		location=s[2];
				    	}

				    }else{
				    	$("#tips3").show().html(s[1]);
				    }			    
				},  
				error : function(data) {  
					$("#tips3").show().html(data);
				}  
			});
		}
		function findpwd(){
			window.location.href="${ctx}/mobile/pages/user/form/findPwd";
		}
</script>

<body>
<div data-role="page" data-theme="c" class="page">
<!-- header -->
  <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Tips9" sessionKey="lang" /></h1>
    </div>
    <!-- 表单开始 -->
    <div class="mt10 mb30">
    	<div data-role="content">
        	<p style="margin-top: 10px; "><div id="tips3" style="margin: 0 70px 14px;"></div></P>
          <div>
            <table width="80%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td align="right" ><ingenta-tag:LanguageTag key="Pages.Index.Lable.Login.Name" sessionKey="lang" />：</td>
                <td><input type="text" id="loginid"></td>
              </tr>
              <tr>
                <td align="right"><ingenta-tag:LanguageTag key="Pages.Index.Lable.Login.Password" sessionKey="lang" />：</td>
                <td><input type="password" id="loginpw"></td>
              </tr>
              <tr>
                <td align="right"><a href="javascript:;" onclick="findpwd();"><ingenta-tag:LanguageTag key="Page.Frame.Left.Link.Forget" sessionKey="lang" /></a></td>
                <td><input type="checkbox" class="checkbox" id="chkuser" onclick="saveLoginId();"><span class="pl15"><ingenta-tag:LanguageTag key="Page.Frame.Left.Link.Remember" sessionKey="lang" /></span></td>
              </tr>
            </table>
			<input type="hidden" id="beforPath" value="${beforPath}" />
          </div>
         

          <div class="tc mt30">
          	<a href="javascript:void(0)" class="loginBig" onclick="signin();"><ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Tips5" sessionKey="lang" /></a>
          </div>
          <!-- 
          <div class="oh mt40 tc">
          	<p class="mb10 fb"><ingenta-tag:LanguageTag key="Pages.User.Other.Login" sessionKey="lang" /></p>
            <p><a href="#"><img src="${ctx}/mobile/images/ico/sina.png"></a> <a href="#"><img src="${ctx}/mobile/images/ico/qq.png"></a></p>
          </div>
          -->
    	</div>
    </div>
    <!-- 表单结束 -->
   <jsp:include page="/footer" />
</div>
</body>
</html>