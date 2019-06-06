<%@page import="java.io.File"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv='X-UA-Compatible' content='IE=edge' />
<title>易阅通</title>
<%@ include file="/common/tools.jsp"%>
<%@ include file="/common/ico.jsp"%>
<script type="text/ecmascript">
	
		
			$(document).ready(function(e) {
				//getHotReading();
				//getEditorRecommends();
				getResourceAll();//资源总数
				getLastPubs();
				$(".meaudown").mouseover(function(){
				  $(this).children("ul").css('display','block');
			  });
			    $(".meaudown").mouseleave(function(){
				  $(this).children("ul").css('display','none');
			  });
			})
		 //编辑推荐
			function getEditorRecommends(){
				var parObj=$("#editorRecommends");
				$.ajax({
					type : "POST",   
			        url: "${ctx}/pages/publications/editorRecommends",
			        data:{
			        	enBook:"yes"
			        },
			        success : function(data) { 
		             	$(parObj).html(data);
		             	$(parObj).css("text-align","left");
		            },  
		            error : function(data) {
		              	$(parObj).html(data);
		            }  
		      });
			}
	     //热读资源
		function getHotReading(){
				var parObj=$("#hot_reading");
				$.ajax({
					type : "POST",   
			        url: "${ctx}/pages/publications/hotReadingBook",
			        data:{
			        	enBook:"yes"
			        },
			        success : function(response) { 
		             	$(parObj).html(response);
		             	$(parObj).css("text-align","left");
		            },  
		            error : function(response) {
		              	$(parObj).html(response);
		            }  
		      });
			}
			

		//最新资源	
			function getLastPubs(){
				var parObj=$("#lastPubs");
				$.ajax({
					type : "POST",   
			        url: "${ctx}/pages/publications/lastPubsBook",
			        data:{
			        	isCn:"false",
			        },
			        success : function(data) { 
		             	$(parObj).html(data);
		             	$(parObj).css("text-align","left");
		            },  
		            error : function(data) {
		              	$(parObj).html(data);
		            }  
		      });
			}


			//获取资源数量列表
			function getResourceAll(){
				var parObj=$("#resource_all");
				$.ajax({
					type : "POST",   
			        url: "${ctx}/pages/publications/readingBook",
			        data: {			        	
			        	w:300
			        },
			        success : function(data) { 
		             	$(parObj).html(data);
		             	$(parObj).css("text-align","left");
		            },  
		            error : function(data) {
		              	$(parObj).html(data);
		            }  
		      });
			}

			function s(s) {
				location.href=encodeURI("${ctx}/index/advancedSearchSubmit?pubType=1&isCn=false&taxonomy=" + encodeURI(s));
			}
</script>
</head>

<body>
	<!--以下top state -->
	<jsp:include page="/pages/header/headerData" />
	<!--以上top end -->
	
	<!-- 中间内容部分开始 -->
	<div class="main">
		<!-- 左侧内容开始 -->
		<div class="chineseLeft">
			<div class="titClassity">
				<p class="p1">
					<a>
						<img src="${ctx}/images/ico/ico4.png" width="16" height="16" class="vm" />
						<ingenta-tag:LanguageTag sessionKey="lang" key="Pages.Cart.Type.EbookEn"/>
					</a>
				</p>
				<p class="p2">
				<ingenta-tag:LanguageTag sessionKey="lang" key="Global.Label.Total_Resources"/>：<span>${count}</span> 
				</p>
			</div>
			<div class="leftClassity">
				<h1 class="h1Tit borBot"><ingenta-tag:LanguageTag key="Global.Label.Subject_Categories" sessionKey="lang" /></h1>
				<ul>
						<c:forEach items="${subList}" var="s" varStatus="index">
							<c:if test="${sessionScope.lang=='zh_CN'}">
								<li><a href="${ctx}/index/advancedSearchSubmit?taxonomy=${s.code} ${s.name}&pubType=1&isCn=false">${s.code} ${s.name}</a></li>
							</c:if>
							<c:if test="${sessionScope.lang!='zh_CN'}">
								<li><a href="${ctx}/index/advancedSearchSubmit?taxonomyEn=${s.code} ${s.nameEn}&pubType=1&isCn=false">${s.code} ${s.nameEn} </a></li>
							</c:if>
						</c:forEach>
				</ul>
			</div>
				
			<!-- 二级英文 - 合作出版社推荐 -->				
			<% if (new File(request.getSession().getAttribute("path") + "press" + File.separator + "press_english.html").exists()) { %>
				<div class="logoList mt30">
					<h1 class="h1Tit borBot"><ingenta-tag:LanguageTag key="Global.Label.RelatedPublisher" sessionKey="lang" /></h1>
					<div id="press_english"></div>
				</div>
			<% } %>
		</div>
		<!-- 左侧内容结束 -->
		
		
		<!-- 右侧内容开始 -->
		<div class="chineseRight">
			<div class="mb30 oh">
				<script type="text/javascript">
				<!--
				$(function() {
					$(".mb30").load("/upload/ad/ad_english.html");
				});
				//-->
				</script>
			</div>
			
			<!-- 编辑推荐开始 -->
			<c:if test="${insInfo==null || insInfo==''}">
				<% if (!new File(request.getSession().getAttribute("path") + "editor/" + "english_editor_all.html").exists()){ %>
						<jsp:include page="${ctx}/pages/publications/editorRecommends?enBook=yes" flush="true" />
				<% } %>
				<div class="oh h450">
					<h1 class="h1Tit borBot"><a class="ico1"><ingenta-tag:LanguageTag key="Global.Label.Editor_Push" sessionKey="lang" /></a></h1>
					<div id="english_editor">
						<script type="text/javascript">
						$(function() {
							$("#english_editor").load("/upload/editor/english_editor_all.html");
						});
						</script>
					</div>
				</div>
			</c:if>
			<c:if test="${insInfo!=null && insInfo!='' }">
					
					<%if (!new File(request.getSession().getAttribute("path") + "editor" + File.separator + "english_editor_"+ request.getAttribute("insInfo") +".html").exists()){ %>
 							<jsp:include page="${ctx}/pages/publications/editorRecommends?enBook=yes" flush="true" />
					<% } %>
					<div class="oh h450">
						<h1 class="h1Tit borBot"><a class="ico1"><ingenta-tag:LanguageTag key="Global.Label.Editor_Push" sessionKey="lang" /></a></h1>
						<div id="english_editor">
							<input id="insId" type="hidden" value="${insInfo}" />
							<script type="text/javascript">
								$(function() {
									var ins = document.getElementById("insId").value;
									$("#english_editor").load("/upload/editor/english_editor_"+ins+".html");
								});
							</script>
						</div>
					</div>
				</c:if>
			<!-- 编辑推荐结束 -->	
			<!-- 最新资源开始 -->
			<div id="lastPubs"></div>
			<!-- 最新资源结束 -->
			<!-- 热读资源开始 -->
			<c:if test="${insInfo==null || insInfo==''}">
					<% if (!new File(request.getSession().getAttribute("path") + "hotReading/" + "english_hotReading_all.html").exists()){ %>
							<jsp:include page="${ctx}/pages/publications/hotReadingBook?enBook=yes" flush="true" />
					<% } %>
					<div class="oh h650">
						<h1 class="h1Tit borBot"><a class="ico1"><ingenta-tag:LanguageTag key="Global.Label.Hot_Reading_Resources" sessionKey="lang" /></a></h1>
						<div id="english_hotReading">
								<script type="text/javascript">
								$(function() {
									$("#english_hotReading").load("/upload/hotReading/english_hotReading_all.html");
								});
								</script>
						</div>
					</div>
				</c:if>
				<c:if test="${insInfo!=null && insInfo!='' }">
					
					<%if (!new File(request.getSession().getAttribute("path") + "hotReading" + File.separator + "english_hotReading_"+ request.getAttribute("insInfo") +".html").exists()){ %>
 							<jsp:include page="${ctx}/pages/publications/hotReadingBook?enBook=yes" flush="true" /> 
					<% } %>
					<div class="oh h650">
						<h1 class="h1Tit borBot"><a class="ico1"><ingenta-tag:LanguageTag key="Global.Label.Hot_Reading_Resources" sessionKey="lang" /></a></h1>
						<div id="english_hotReading">
							<input id="insId" type="hidden" value="${insInfo}" />
							<script type="text/javascript">
								$(function() {
									var ins = document.getElementById("insId").value;
									$("#english_hotReading").load("/upload/hotReading/english_hotReading_"+ins+".html");
								});
							</script>
						</div>
					</div>
				</c:if>
			<!-- 热读资源结束 -->
		</div>
		<!-- 右侧内容结束 -->
	</div>
	<!-- 中间内容部分结束 -->
	
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
		
		<!-- 底部的版权信息 -->
		<c:if test="${sessionScope.lang == 'zh_CN'}"><div id="footer_zh_CN"></div></c:if>
		<c:if test="${sessionScope.lang == 'en_US'}"><div id="footer_en_US"></div></c:if>
	</body>
</html>