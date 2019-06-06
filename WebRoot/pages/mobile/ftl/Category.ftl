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
				<div data-role="header" class="classifyTit">
					<a class="back" href="javascript:history.back(-1);"><img src="${request.contextPath}/mobile/images/left.png"></a>
					<h1 class="ui-title-h1">分类法</h1>
				</div>
				<div class="classifyListDet mt20 mb20">
					<#if (subjectList?? && 0 < subjectList?size)>
						<#list subjectList as s>
							<div data-role="collapsible" class="fold">
								<h1>${s[0..0]} ${s?substring(1, s?index_of("@@@@@@@@@@"))}</h1>
								<div class="secClassify">
									<ul>
										<#list s?substring((s?index_of("@@@@@@@@@@") + 10), s?length)?split("@@@@@end@@@@@") as x>
											<li><a href="javascript:window.location.href='/mobile/index/advancedSearchSubmit?taxonomy=${x?substring(0, x?index_of("@@@@@code@@@@@"))} ${x?substring(x?index_of("@@@@@code@@@@@") + 14, x?length)}'">${x?substring(0, x?index_of("@@@@@code@@@@@"))} ${x?substring(x?index_of("@@@@@code@@@@@") + 14, x?length)}</a></li>
										</#list>
									</ul>
								</div>
							</div>
						</#list>
					</#if>
				</div>
				<#include "${request.contextPath}/mobile/ftl/Footer.ftl">
			</div>
		</div>
	</body>
</html>
