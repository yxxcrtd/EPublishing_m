<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" /> 
		<meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no" />
		<meta content="yes" name="apple-mobile-web-app-capable" />  
		<meta content="black" name="apple-mobile-web-app-status-bar-style" /> 
		<meta content="telephone=no" name="format-detection" /> 
		<title>易阅通</title>
		<link rel="shortcut icon" href="${request.contextPath}/images/CNPe.ico" type="image/x-icon" />
		<#include "${request.contextPath}/mobile/ftl/Public.ftl" />
	</head>

	<body>
		<div data-role="page" data-theme="c" class="page">
			<#include "${request.contextPath}/mobile/ftl/Header.ftl">
			<div data-role="content">
				${dynamic}
				<#include "${request.contextPath}/mobile/ftl/Footer.ftl">
			</div>
		</div>
	</body>
</html>
