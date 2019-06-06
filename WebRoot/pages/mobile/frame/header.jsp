<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
<script type="text/javascript">
	$(function(){
		for(var i=0;i<5;i++){
			var sli=$("<li>").click(function(){
				$("#searchValue").val($(this).text());
				$("#suggestUl").hide();
				searchAll('0');
			});
			$("#suggestUl").append(sli);
		}
		
		var oInput = $("#searchValue");
		oInput.seletedIndex = -1;
		
		$("#searchValue").keyup(function(evt){
			evt = (evt) ? evt : ((window.event) ? window.event : ""); //兼容IE和Firefox获得keyBoardEvent对象
			var key = evt.keyCode?evt.keyCode:evt.which; //兼容IE和Firefox获得keyBoardEvent对象的键值
			// 当键盘按下的时候获取 suggestUl 里面的 li 中的值
			oInput.options = $("#suggestUl").find("li");
			if(key == 13){ 
				searchAll('0');
			} else if (38 == key) {
// 				clearSelectedColor(oInput);
// 				oInput.seletedIndex = oInput.seletedIndex - 1;
// 				if (0 > oInput.seletedIndex) {
// 					oInput.seletedIndex = oInput.options.length - 1;
// 				}
// 		    	oInput.val(oInput.options[oInput.seletedIndex].innerHTML);
// 				setSelectedColor(oInput);
			} else if (40 == key) {
// 				oInput.focus();
// 				clearSelectedColor(oInput);
// 				oInput.seletedIndex = oInput.seletedIndex + 1;
// 				if (oInput.seletedIndex >= oInput.options.length) {
// 					oInput.seletedIndex = 0;
// 		    	}
// 		    	oInput.val(oInput.options[oInput.seletedIndex].innerHTML);
// 				setSelectedColor(oInput);
			}else{
// 				var qtext=$(this).val();
// 				$.ajax({
// 						url:"${ctx}/pages/suggest",
// 						data:{wt:"json",q:qtext,row:5},
// 						success:function(data){
// 							$("#suggestUl li").each(function(i,item){
// 								$(item).hide();
// 								$(item).text("");
// 							});
// 							var sgStr=data.spellcheck.suggestions[1].suggestion;
// 							if(sgStr && sgStr.length){
// 								for(var i=0;i<sgStr.length;i++){
// 									$("#suggestUl li:eq("+i+")").text(sgStr[i]);
// 									$("#suggestUl li:eq("+i+")").show();
// 								}
// 								$("#suggestUl").show();
// 							}
// 						},
// 						dataType:"json"
// 				})
			}				
		});
		var This = $("#suggestUl");
		This.on("mouseleave", function() {
			This.fadeOut(2000);
		});
		
		var searchInput = $("#searchValue");
		searchInput.on("blur", function() {
			This.fadeOut(2000);
		});
	});
	
	function clearSelectedColor(target){
	    if (target.seletedIndex >= 0) {
	        target.options[target.seletedIndex].style.background = "";
	    }
	}
	function setSelectedColor(target) {
	    target.options[target.seletedIndex].style.background = "#87C1E6";
	}
	
	function searchAll(type){
		 var param = $("#searchValue").val();
		 if(param==''){
		 	art.dialog.tips("<ingenta-tag:LanguageTag key='Page.Frame.Header.Pormpt.KeyWord' sessionKey='lang' />",1,'error');
		 }else{
		 	$("#type1").val(type);
		 	$("input[name='curpage']").val(0);		 	
		 	$("#searchValue1").val(param);
		 	$("#pubType1").val('');
		 	$("#publisher1").val('');
		 	$("#pubDate1").val('');
		 	$("#taxonomy1").val('');
		 	$("#taxonomyEn1").val('');
		 	$("#order1").val('');
		 	$("#code1").val('');
		 	$("#pCode1").val('');
		 	$("#publisherId1").val('');
		 	$("#subParentId1").val('');
		 	$("#parentTaxonomy1").val('');
		 	$("#parentTaxonomyEn1").val('');
		 	$("#language").val('');	
		 	var flag = "${sessionScope.specialInstitutionFlag}" ;
			var status = (flag!=""&&flag.length>0)?true:false;	
		 	if(status){
		 		$("#pubType1").val("1"); 		// 查询图书
		 		$("#notLanguage1").val(flag);	//根据后台的语言添加查询条件
				$("#local1").val("2");		//本地
		 	}		 	
		 	if(${sessionScope.selectType==1}){
		 		$("#lcense1").val("1");
		 		document.formListSearch.action="${ctx}/index/searchLicense";
		 	}else if(${sessionScope.selectType==2}){
		 		$("#lcense1").val("2");
		 		document.formListSearch.action="${ctx}/index/searchOaFree";
		 	}else{
		 		document.formListSearch.action="${ctx}/index/search";
		 	}
			document.formListSearch.submit();
		 }
	}
</script>
<div data-role="header">
	<div class="top">
		<div class="logo">
			<a href="/" data-ajax="false"><img src="${ctx}/mobile/images/logo.png" width="80"></a>
		</div>
		
		<div class="topDiv" style="width: <c:choose><c:when test="${null != mainUser}">23</c:when><c:otherwise>37</c:otherwise></c:choose>%;" >
			<input type="search" name="search" placeholder="搜索" id="searchValue">
			<ul class="seaOver" id="suggestUl" style="display: none;"></ul>

		</div>
		<form:form action="${form.url}" method="post" modelAttribute="form"
			commandName="form" name="formListSearch" id="formListSearch">
			<form:hidden path="searchsType" id="type1" />
			<form:hidden path="searchValue" id="searchValue1" />
			<form:hidden path="searchValue2" id="searchValue2" />
			<form:hidden path="searchsType2" id="searchsType2" />
			<form:hidden path="pubType" id="pubType1" />
			<form:hidden path="language" id="language1" />
			<form:hidden path="publisher" id="publisher1" />
			<form:hidden path="pubDate" id="pubDate1" />
			<form:hidden path="taxonomy" id="taxonomy1" />
			<form:hidden path="taxonomyEn" id="taxonomyEn1" />
			<form:hidden path="searchOrder" id="order1" />
			<form:hidden path="lcense" id="lcense1" />
		
			<form:hidden path="code" id="code1" />
			<form:hidden path="pCode" id="pCode1" />
			<form:hidden path="publisherId" id="publisherId1" />
			<form:hidden path="subParentId" id="subParentId1" />
			<form:hidden path="parentTaxonomy" id="parentTaxonomy1" />
			<form:hidden path="parentTaxonomyEn" id="parentTaxonomyEn1" />
			<form:hidden path="keywordCondition" id="keywordCondition1" />
			<form:hidden path="notKeywords" id="notKeywords1" />
			<form:hidden path="author" id="author1" />
			<form:hidden path="title" id="title1" />
			<form:hidden path="curpage" id="curpage" />
			<form:hidden path="pageCount" id="pageCount" />
			<form:hidden path="fullText" id="fullText1" />
			<form:hidden path="nochinese" id="nochinese" />
			<form:hidden path="local" id="local1" />
			<form:hidden path="notLanguage" id="notLanguage1" />
	</form:form>
		<div class="topClass">
			<a class="searCif classIco" href="/subject" data-ajax="false">分类法</a>
			<a class="searCif userIco" href="${ctx}/mobile/pages/user/usercenter" data-ajax="false">个人中心</a>
			<c:if test="${null != mainUser}"><a class="searCif cartIco" href="${ctx }/mobile/pages/cart/form/list" data-ajax="false">购物车</a></c:if>
		</div>
	</div>
</div>
