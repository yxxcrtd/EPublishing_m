<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta content=”width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;” name=”viewport” /> 
<meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no" />
<meta content=”yes” name=”apple-mobile-web-app-capable” />  
<meta content=”black” name=”apple-mobile-web-app-status-bar-style” /> 
<meta content=”telephone=no” name=”format-detection” /> 
    <title><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.Error.Prompt.Title"/></title>
<%@ include file="/mobile/tools.jsp"%>
<%@ include file="/mobile/ico.jsp"%>
  <style type="text/css">
.search_word ul{
background:#f8f8f8;
border-left:1px solid #bcbcbc;
border-right:1px solid #bcbcbc;
border-bottom:1px solid #bcbcbc;
width:86px;
display:none;
position:absolute;
z-index:5;
margin-left:380px;
margin-top:55px;
margin-left:380px\9;
>margin-left/*IE5.5*/:-105px;
>margin-top/*IE5.5*/:53px;
}
*+html .search_word ul{
margin-left:-103px;
margin-top:53px;
}
@media screen and (-webkit-min-device-pixel-ratio:0){.search_word ul{margin-left:380px;}}
.search_help ul{
margin-top:-7px;
margin-left:484px;
margin-left:484x\9;
_margin-left:-93px;
_margin-top:56px;
}
*+html .search_help ul{
margin-left:-92px;
margin-top:55px;
}
@media screen and (-webkit-min-device-pixel-ratio:0){.search_help ul{margin-left:484px;}}
</style>
  </head>
  
 <body >
<div data-role="page" data-theme="c" class="page">
<!-- header -->
  <jsp:include page="/header" />	
  <div style="width : 100%; height: 350px;">
		<div  style="width: 100%; float: left;">
			<h1>
				<c:if test="${prompt!=null}">${prompt}</c:if>
				<c:if test="${prompt==null}">
					<ingenta-tag:LanguageTag sessionKey="lang"
						key="Pages.Error.Prompt.Title" />
				</c:if>
			</h1>

			<div class="zhuce_xinxi" style="text-align: center;float:none;padding-top: 100px;">
				<p>&nbsp;</p>
				<p class="p_mail">
					<c:if test="${message!=null}">${message}</c:if>
					<c:if test="${form.msg!=null}">${form.msg}</c:if>
				</p>
				
				 <jsp:include page="/footer" />
			</div>
		</div>
 	</div>
 </div>
</body>
</html>
