<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
<div class="ui-grid-a">
      <div class="ui-block-a tl">
      <c:if test="${null == sessionScope.mainUser}"><span><a href="${ctx}/login" data-ajax="false">登录</a></span> | <span><a href="${ctx}/mobile/pages/user/form/register" data-ajax="false">注册</a></span></c:if>
      <c:if test="${null != sessionScope.mainUser}"> (<span><a href="${ctx}/mobile/pages/user/usercenter">${sessionScope.mainUser.name}</a></span> | <span><a href="javascript:void(0);" onclick="loginout()" ><ingenta-tag:LanguageTag key="Page.Frame.Left.Link.loginOut" sessionKey="lang" /></a></span>) </c:if></div>
      <div class="ui-block-b tr"><span><a href="javascript:window.location.href='${ctx}/help'">帮助</a></span> | <span><a id="backTop" href="javascript:;">回到顶部</a></span></div>
    </div>   
  </div>  	
  <div data-role="footer" data-theme="d">
    <h1>&copy; m.cnpereading.com</h1>
  </div>
  <script>
  $(function(){
	  $("#backTop").click(function(){  
          $('body,html').animate({scrollTop:0},1000);  
          return false;  
      });  
  });
  
  function loginout(){
		$.ajax({
			type : "POST",  
			url: "${ctx}/logout",
			data: {r_ : new Date().getTime()},
			success : function(data) { 
			 	var s = data.split("::");
			 	if ("success" == s[0]) {
			    	art.dialog.tips(s[1], 1, 'success');
			    	//<c:if test="${ctx!=''}">
			    		//location="${ctx}";
			    	//</c:if>
			    	//<c:if test="${ctx==''}">
			    		//location="${domain}";
			    	//</c:if>
			    	location = s[2];
			    }	
			},  
			error : function(data) {  
			    art.dialog.tips(data,1,'error');
			}  
		});
	}
  
  </script>