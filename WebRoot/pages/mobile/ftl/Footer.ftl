<div class="ui-grid-a">
	<div class="ui-block-a tl">
		<#if (mainUser??)>
			 (<span><a href="${request.contextPath}/mobile/pages/user/usercenter">${mainUser.name!}</a></span> | <span><a href="javascript:;" onclick="loginout();">退出</a></span>)
		<#else>
			<span><a href="${request.contextPath}/login" data-ajax="false">登录</a></span> | <span><a href="${request.contextPath}/mobile/pages/user/form/register" data-ajax="false">注册</a></span>
		</#if>
	</div>
	<div class="ui-block-b tr"><span><a href="${request.contextPath}/help">帮助</a></span> | <span><a id="backTop" href="javascript:;">回到顶部</a></span></div>
</div>

<div data-role="footer" data-theme="d"><h1>${footer}</h1></div>

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
			url: "${request.contextPath}/logout",
			data: {r_ : new Date().getTime()},
			success : function(data) { 
			 	var s = data.split("::");
			 	if("success" == s[0]){
			    	art.dialog.tips(s[1], 1, 'success');
			    	location = s[2];
			    }	
			},  
			error : function(data) {  
			    art.dialog.tips(data,1,'error');
			}  
		});
	}
</script>