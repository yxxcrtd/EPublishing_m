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
	getPPV4();
// 	gettitleView();
	//getReadable();
	getRecommend();
	$(".meaudown").mouseover(function(){
	  $(this).children("ul").css('display','block');
  });
    $(".meaudown").mouseleave(function(){
	  $(this).children("ul").css('display','none');
  });
})

//获取分类列表
			function gettitleView(){
				var parObj=$("#title_view");
				$.ajax({
					type : "POST",  
			        url: "${ctx}/pages/index/titleView",
			        success : function(data) { 
		             	$(parObj).html(data);
		             	$(parObj).css("text-align","left");
		            },  
		            error : function(data) {
		              	$(parObj).html(data);
		            }  
		      });
			}
//获取分类列表
function getSubjectAll(){
	var parObj=$("#subject_all");
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/subject/subjectAll",
        success : function(data) { 
         	$(parObj).html(data);
         	$(parObj).css("text-align","left");
        },  
        error : function(data) {
          	$(parObj).html(data);
        }  
  });
}
//获取单篇文章列表
function getPPV4(){
	var parObj=$("#ppv4");
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/collection/form/getPPV4",
        success : function(data) { 
         	$(parObj).html(data);
         	$(parObj).css("text-align","left");
        },  
        error : function(data) {
          	$(parObj).html(data);
        }  
  });
}
//获取热读文章
function getHotReading(){
	var parObj=$("#hot_reading");
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/publications/hotReading",
        success : function(data) { 
         	$(parObj).html(data);
         	$(parObj).css("text-align","left");
        },  
        error : function(data) {
          	$(parObj).html(data);
        }  
  });
}
//获取可读资源
function getReadable(){
	var parObj=$("#readable");
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/publications/form/readable",
        success : function(data) { 
         	$(parObj).html(data);
         	$(parObj).css("text-align","left");
        },  
        error : function(data) {
          	$(parObj).html(data);
        }  
  });
}
//获取推荐期刊
function getRecommend(){
	var parObj=$("#recommend");
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/publications/form/recJournal",
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
	location.href=encodeURI("${ctx}/index/advancedSearchSubmit?pubType=2&taxonomy=" + encodeURI(s));
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
					<a href="javascript:void(0)"><img src="${ctx}/images/ico/ico3.png"
						width="16" height="16" class="vm" /> <ingenta-tag:LanguageTag key="Pages.Cart.Type.EjournalEn" sessionKey="lang" /></a>
				</p>
				<p class="p2">
					<ingenta-tag:LanguageTag sessionKey="lang"
						key="Pages.Index.Lable.Journal" />
					<span>${journalsCount}</span>
				</p>
				<p class="p2">
					<ingenta-tag:LanguageTag sessionKey="lang"
						key="Pages.Cart.Type.Article" />
					<span>${articlesCount}</span>
				</p>
				<p class="tc mt15"><a href="${ctx}/pages/collection/form/getPPV" class="a_Blue white"><ingenta-tag:LanguageTag key="Pages.Publications.PayPerView" sessionKey="lang" /></a></p>
			</div>
			<!------- 按标题浏览开始 ---------->
			<div class="mb20">
        	<h1 class="h1Tit borBot">A-Z</h1>
            <p>
                <%for(int i=65;i<91;i++){
						char a = (char) i;
					%>
					  <span><a href="${ctx}/index/advancedSearchSubmit?prefixWord=<%=a %>&pubType=2"><%=a %></a></span>
					<%
						}
					%>
                <span><a href="${ctx}/index/advancedSearchSubmit?prefixWord=0&pubType=2">0-9</a></span>
            </p>
        </div>
			<%-- <div id="subject_all">
			<img src="${ctx}/images/loading.gif"/>
        </div> --%>
			<div class="leftClassity">
				<h1 class="h1Tit borBot"><ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Subject" sessionKey="lang"/></h1>
				<ul>

					<c:forEach items="${subList}" var="s" varStatus="index">
					 <c:if test="${sessionScope.lang=='zh_CN'}">
							<li><a href="${ctx}/index/advancedSearchSubmit?taxonomy=${s.code} ${s.name }&pubType=2" >${s.code} ${s.name}</a></li>
					 </c:if>
					 <c:if test="${sessionScope.lang!='zh_CN'}">
							<li><a href="${ctx}/index/advancedSearchSubmit?taxonomyEn=${s.code} ${s.nameEn }&pubType=2" >${s.code} ${s.nameEn} </a></li>
					 </c:if>
					</c:forEach>
				</ul>
			</div>
		</div>
		<!-- 左侧内容结束 -->
		<!-- 右侧内容开始 -->
		<div class="chineseRight">
			<div class="mb30 oh">
				<script type="text/javascript">
				<!--
				$(function() {
					$(".mb30").load("/upload/ad/ad_journal.html");
				});
				//-->
				</script>
			</div>
	    	
			<!-- 可读资源开始 -->
			<% if (!new File(request.getSession().getAttribute("path") + "Journal_Readable.html").exists()){ %>
				<jsp:include page="${ctx}/pages/publications/form/readable" flush="true" />
			<% } %>
			 <div class="oh h350">
				<h1 class="h1Tit borBot">
					<a class="ico1" href="javascript:void(0)"><ingenta-tag:LanguageTag key="Global.Label.Readable_Resource" sessionKey="lang" /></a>
				</h1>
				<div id="journal_readable">
					<script type="text/javascript">
					<!--
					$(function() {
						$("#journal_readable").load("/upload/Journal_Readable.html");
					});
					//-->
					</script>
				</div>
			</div>
			<!-- 可读资源结束 -->
			
			<!-- 单篇文章开始 -->
			<div id="ppv4"></div>
			<!-- 单篇文章结束 -->
			
			<!-- 推荐期刊开始 -->
			<c:if test="${insInfo==null || insInfo==''}">
				<% if (!new File(request.getSession().getAttribute("path") + "journal_recJournal" + File.separator + "recJournal_all.html").exists()){ %>
						<jsp:include page="${ctx}/pages/publications/form/recJournalStatic" flush="true" />
				<% } %>
				  <div class="oh h350">
					<h1 class="h1Tit borBot">
						<a class="ico1" href="javascript:void(0)"><ingenta-tag:LanguageTag key="Pages.Publications.Recommend.Journal" sessionKey="lang" /></a>
					</h1>
					<div id="journal_recommend">
							<script type="text/javascript">
							$(function() {
								$("#journal_recommend").load("/upload/journal_recJournal/recJournal_all.html");
							});
							</script>
					</div>
				</div>
			</c:if>
			<c:if test="${insInfo!=null && insInfo!='' }">
					<%if (!new File(request.getSession().getAttribute("path") + "journal_recJournal" + File.separator + "recJournal_"+ request.getAttribute("insInfo") +".html").exists()){ %>
 							<jsp:include page="${ctx}/pages/publications/form/recJournalStatic" flush="true" /> 
					<% } %>
					 <div class="oh h350">
        				<h1 class="h1Tit borBot"><a class="ico1" href="javascript:void(0)"><ingenta-tag:LanguageTag key="Pages.Publications.Recommend.Journal" sessionKey="lang" /></a></h1>
						<div id="journal_recommend">
							<input id="insId" type="text" value="${insInfo}" />
							<script type="text/javascript">
								$(function() {
									var ins = document.getElementById("insId").value;
									$("#journal_recommend").load("/upload/journal_recJournal/recJournal_"+ins+".html");
								});
							</script>
						</div>
					</div>
			</c:if>
			<!-- 推荐期刊结束 -->
			
			<!-- 热读文章开始 -->
			<c:if test="${insInfo==null || insInfo==''}">
				<% if (!new File(request.getSession().getAttribute("path") + "journal_hotReading/" + "Journal_HotReadingArticle_all.html").exists()){ %>
						<jsp:include page="${ctx}/pages/publications/hotReading" flush="true" />
				<% } %>
				<div class="oh h450">
       				<h1 class="h1Tit borBot"><a class="ico1" href="javascript:void(0)"><ingenta-tag:LanguageTag key="Page.Publications.Hotreading" sessionKey="lang" /></a></h1>
					<div id="journal_reading">
							<script type="text/javascript">
							$(function() {
								$("#journal_reading").load("/upload/journal_hotReading/Journal_HotReadingArticle_all.html");
							});
							</script>
					</div>
				</div>
			</c:if>
			<c:if test="${insInfo!=null && insInfo!='' }">
					<%if (!new File(request.getSession().getAttribute("path") + "journal_hotReading" + File.separator + "Journal_HotReadingArticle_"+ request.getAttribute("insInfo") +".html").exists()){ %>
 							<jsp:include page="${ctx}/pages/publications/hotReading" flush="true" /> 
					<% } %>
					<div class="oh h450">
        				<h1 class="h1Tit borBot"><a class="ico1" href="javascript:void(0)"><ingenta-tag:LanguageTag key="Page.Publications.Hotreading" sessionKey="lang" /></a></h1>
						<div id="journal_reading">
							<input id="insId" type="hidden" value="${insInfo}" />
							<script type="text/javascript">
								$(function() {
									var ins = document.getElementById("insId").value;
									$("#journal_reading").load("/upload/journal_hotReading/Journal_HotReadingArticle_"+ins+".html");
								});
							</script>
						</div>
					</div>
			</c:if>
			<!-- 热读文章结束 -->
				
			<!-- 二级外文电子期刊 - 合作出版社推荐 -->				
			<% if (new File(request.getSession().getAttribute("path") + "press" + File.separator + "press_journal.html").exists()) { %>
				<div class="logoList">
					<h1 class="h1Tit borBot"><ingenta-tag:LanguageTag key="Global.Label.RelatedPublisher" sessionKey="lang" /></h1>
					<div id="press_journal"></div>
				</div>
			<% } %>
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