<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta content=”width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;” name=”viewport” /> 
<meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no" />
<meta content=”yes” name=”apple-mobile-web-app-capable” />  
<meta content=”black” name=”apple-mobile-web-app-status-bar-style” /> 
<meta content=”telephone=no” name=”format-detection” /> 
<title>易阅通</title>
<%@ include file="/mobile/tools.jsp"%>
<%@ include file="/mobile/ico.jsp"%>
<script type="text/javascript" src="${ctx}/js/checkPwd.js"></script>
<script type="text/javascript" src="${ctx}/mobile/js/jquery.js"></script>
<script type="text/javascript" src="${ctx}/mobile/js/jquery.cookie.js"></script>
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
					r_ : new Date().getTime()
				},
				success : function(data) {  
				    var s = data.split(":");			     
				    if(s[0]=="success"){
				    	<c:if test="${ctx!=''}">
				    		window.location.href="${ctx}/mobile/pages/user/usercenter";
				    	</c:if>
				    	<c:if test="${ctx==''}">
				    		window.location.href="${domain}/mobile/pages/user/usercenter";
				    	</c:if>
				    	
				    }else{
				    	$("#tips3").show().html(s[1]);
				    }			    
				},  
				error : function(data) {  
					$("#tips3").show().html(data);
				}  
			});
		}
		
</script>
<script type="text/javascript">
//******************************弹出层--开始*********************************//
	/*
	 *弹出本页指定ID的内容于窗口
	 *id 指定的元素的id
	 *title: window弹出窗的标题
	 *width: 窗口的宽,height:窗口的高
	 */
		function showTipsWindown(title, id, width, height) {
			tipsWindown(title, "id:" + id, width, height, "true", "", "true", id);
		}
		function confirmTerm() {
			parent.closeWindown();
		}
		//弹出层调用
		function popTips(pid,type) {
			//先将信息放到对应的标签上title, code, type, pubSubject
			art.dialog.open("${ctx}/pages/recommend/form/edit?pubid="+pid,{title:"<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/>",top: 100,width: 550, height: 450,lock:true});
		}
		$(document).ready(function(e) {
			//	getRightList();
		 	  //getBottomList();
		 	  	getCount();
		 	  	getCount1();
			});
		function getCount(){
			var testExp=/^\d+$/;
			$(".chapt_author_p2 span span").each(function(i,item){
				var pagNubStr=$(item).text().trim();
				var numbers=pagNubStr.split("-");
				//console.info(numbers);
				var range=1;
				if(numbers){
					var spage=numbers[0].trim().match(testExp)?parseInt(numbers[0].trim()):numbers[0];
					if(numbers.length==2){
						var epage= numbers[1].trim().match(testExp)?parseInt(numbers[1].trim()):numbers[1];
						range=epage-spage+1;					
					}
					$(item).text(pagNubStr+" ("+range+")");
				}
			})
		}
		function getCount1(){
			var testExp=/^\d+$/;
			$(".chapt_author_p3 ").each(function(i,item){
				var pagNubStr=$(item).text().trim();
				var numbers=pagNubStr.split("-");
				console.info(numbers);
				var range=1;
				if(numbers){
					var spage=numbers[0].trim().match(testExp)?parseInt(numbers[0].trim()):numbers[0];
					if(numbers.length==2){
						var epage= numbers[1].trim().match(testExp)?parseInt(numbers[1].trim()):numbers[1];
						range=epage-spage+1;					
					}
					$(item).text(pagNubStr+" ("+range+")");
				}
			})
		}
	//在线阅读起关闭
	function confirmTerm2(id) {
		parent.closeWindown();
		$.ajax({
			type : "POST",
			async : false,
			url : "${ctx}/pages/publications/form/release",
			data : {
				id : id,
				r_ : new Date().getTime()
			},
			success : function(data) {
			},
			error : function(data) {
			}
		});
	}
	//获取资源弹出层调用
	function popTips2(pid) {
	/* 	showTipsWindown("",
				'simTestContent', $(window).width()*0.6, $(window).height()*0.65); */
				/* alert(pid); */
				art.dialog.open("${ctx}/mobile/pages/publications/form/getResource?pubid="+pid,{id : "getResourceId",title:"",top: 200,width: 340, height: 200,lock:true}); /* <ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/> */
	}
	//在线阅读弹出层调用
	function viewPopTips(id,page) {
		var url="";
		var tmp=window.open("about:blank","","scrollbars=yes,resizable=yes,channelmode") ;          
            //tmp.focus() ;
		if(page=='0'){
			url = "${ctx}/pages/view/form/view?id="+id;
		}else{
			url = "${ctx}/pages/view/form/view?id="+id+"&nextPage="+page;
		}
		//首先Ajax查询要阅读的路径
		if('${form.obj.free}'=='2'||'${form.obj.oa}'=='2'){
		//window.location.href=url;
		 tmp.location=url;
	}else{
	$.ajax({
		type : "POST",
		async : false,
		url : "${ctx}/mobile/pages/publications/form/getUrl",
		data : {
			id : id,
			nextPage:page,
			r_ : new Date().getTime()
		},
		success : function(data) {
			var s = data.split(";");
			if (s[0] == "success") {
				console.info("============================================" + s[1]);
				if(s[1].indexOf('/pages/view/form/view')>=0){
					//window.location.href=s[1];
					tmp.location=s[1];
				}else{
					//window.location.href="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
					tmp.location="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
				}
			}else if(s[0] == "error"){
				art.dialog.tips(s[1],1,'error');
			}
		},
			error : function(data) {
				art.dialog.tips(data,1,'error');
			}
		});
		}
	}
	//******************************弹出层--结束*********************************//
</script>
<script type="text/javascript">
	function searchByCondition(type,value,a1,a2,a3,a4){
		document.formList.action="${ctx}/pages/publications/form/list";
		if(type=="searchValue"){
			$("#searchValue1").val(value);
			if(${sessionScope.selectType==1}){
	 			$("#lcense1").val("1");
	 			document.formList.action="${ctx}/index/searchLicense";
		 	}else{
		 		document.formList.action="${ctx}/index/search";
		 	}
		}else if(type=="type"){
			$("input[name='pubType']").val(value);
		}else if(type=="publisher"){
			$("input[name='publisher']").val(value); 
			$("input[name='publisherId']").val(a1);
		}else if(type=="pubDate"){
			$("input[name='pubDate']").val(value);
		}else if(type=="taxonomy"){
			$("#taxonomy1").val(value);
			$("#pCode1").val(a1);
			$("#code1").val(a2);
			$("#subParentId1").val(a3);
			$("#parentTaxonomy1").val(a4);
			document.formList.action="${ctx }/pages/subject/form/list";
		}else if(type=="taxonomyEn"){
			$("#taxonomyEn1").val(value);
			$("#pCode1").val(a1);
			$("#code1").val(a2);
			$("#subParentId1").val(a3);
			$("#parentTaxonomyEn1").val(a4);
			document.formList.action="${ctx }/pages/subject/form/list";
		}
		$("#page").val(0);
		$("#pageCount").val(10);
		$("#order1").val('');
		$("#lcense1").val('${sessionScope.selectType}');
		document.formList.submit();
	}
	function addToCart(pid, ki) {
					var price = $("#priceSel").val();
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/cart/form/add",
						data : {
							pubId : pid,
							priceId : price,
							kind : ki,
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1);//location.reload();
								$("#add_"+pid).css("display","none");
								$("#cartCount").html("["+s[2]+"]");
								$("#priceSel").css("display","none");
							}else{
								art.dialog.tips(s[1],1,'error');
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				function recommendSubmit() {
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/recommend/form/submit",
						data : {
							pubid : $("#pubid").val(),
							note : $("#rnote").val(),
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1,'error');
								confirmTerm();
							}else{
								alert(s[1]);
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
			/* 	function recommendSubmit(pid) {
					
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/recommend/form/submit",
						data : {
					//		pubid : $("#pubid").val(),
							pubid :pid,
							note : $("#rnote").val(),
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1,'error');
								confirmTerm();
							}else{
								alert(s[1]);
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				
				function recommendSubmittwo(pid) {
					
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/recommend/form/submit",
						data : {
					//		pubid : $("#pubid").val(),
							pubid :pid,
							note : $("#rnote").val(),
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1,'error');
								confirmTerm();
								$("#recommand_img_"+pid).attr("src","${ctx}/images/ico/ico16-blank.png");
								$("#recommand_"+pid).removeAttr("onclick");
						//		$("#recommand_"+pid).attr("title","<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
							}else{
								alert(s[1]);
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				 */
				
				
</script>

<body>
<div data-role="page" data-theme="c" class="page">
<!-- header -->
  <jsp:include page="/header" />
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1">
        	<c:if test="${form.obj.lang eq 'CHS' }">
 				中文电子书
        	</c:if>
        	 <c:if test="${form.obj.lang eq 'ENG' }">
 				外文电子书
        	</c:if>
        </h1>
    </div>
  
    <div class="oh mt20 mb20">
    		<c:set var="objnews">${form.obj.latest!=null&&form.obj.latest>0 }</c:set>
		<c:set var="objoa">${form.obj.oa!=null&&form.obj.oa==2 }</c:set>
		<c:set var="objfree">${form.obj.free!=null&&form.obj.free==2 }</c:set>
		<c:set var="objlicense">${(form.obj.subscribedIp!=null||form.obj.subscribedUser!=null)&&(form.obj.subscribedIp>0||form.obj.subscribedUser>0) }</c:set>
		<c:set var="objcollection">${form.obj.inCollection!=null&&form.obj.inCollection>0 }</c:set>
      	<c:set var="readPubId" value="${form.obj.type==3?form.obj.publications.id:form.obj.id}"/>
      	<c:set var="readPubStartPage" value="${form.obj.type==3?form.obj.startPage:0}"/>
        <c:set var="add1" value="${pricelist!=null&&fn:length(pricelist)>0&&form.obj.free!=2&&form.obj.oa!=2&&sessionScope.mainUser!=null && form.obj.subscribedUser<=0&&(form.obj.buyInDetail<=0&&form.obj.exLicense>=0)}"/>
		<c:if test="${add1==false }">
			<c:set var="add" value="false"/>
		</c:if>
		<c:if test="${add1==true &&form.obj.subscribedIp>0 }">
			<c:if test="${sessionScope.mainUser.institution.id==sessionScope.institution.id&&sessionScope.mainUser.level==2 }">
			<c:set var="add" value="false"/>
			</c:if>
			<c:if test="${sessionScope.mainUser.institution.id==sessionScope.institution.id &&sessionScope.mainUser.level!=2 }">
			<c:set var="add" value="true"/>
			</c:if>
			<c:if test="${sessionScope.mainUser.institution.id!=sessionScope.institution.id}">
			<c:set var="add" value="true"/>
			</c:if>
		</c:if>
		<c:if test="${add1==true &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0) }">
			<c:set var="add" value="true"/>
		</c:if>
			<c:set var="favourite" value="${sessionScope.mainUser!=null&&form.obj.favorite<=0 }"/>
			<c:set var="recommand" value="${sessionScope.institution!=null && (form.obj.recommand>0||sessionScope.mainUser.institution!=null) &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0)&&(form.obj.free!=2&&form.obj.oa!=2)}"/>	
		 	<c:set var="license" value="${form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2 }"/>
   	
   	  <!-- 图书信息开始 -->
    	<div class="book">
    		<c:choose>
				<c:when test="${null == form.obj.cover || '' == form.obj.cover}"><img width="100%" src="${ctx}/images/noimg.jpg" /></c:when>
				<c:otherwise><img width="100%" src="${ctx}/mobile/pages/publications/form/cover?t=2&id=${form.obj.id}" onerror="this.src='${ctx}/images/noimg.jpg'" /></c:otherwise>
			</c:choose>
		</div>
        <div class="fl w22 mt5" style="padding-left:15px;">
        	<c:if test="${objnews==true || objfree==true || objoa==true  || objcollection==true}">
				<c:if test="${objfree==true }">
					<img src="${ctx }/mobile/images/ico/f.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.Free" sessionKey="lang" />" />
				</c:if>
				<c:if test="${objoa==true }">
					<img src="${ctx }/mobile/images/ico/o.png" title="<ingenta-tag:LanguageTag key="Page.Frame.Left.Lable.OA" sessionKey="lang" />" />
				</c:if>
			</c:if>
			<c:if test="${objlicense==true }">
				<img src="${ctx }/images/ico/ico_open.png" class="vm" />
			</c:if>
			<c:if test="${objlicense==false && objfree==false && objoa==false }">
				<img src="${ctx }/mobile/images/ico/ico_close.png" class="vm" />
			</c:if>
			<c:if test="${form.obj.type==3 }">
				<img src="${ctx }/mobile/images/ico/infor.png" class="vm" />
			</c:if>
			<span>
				<c:if test="${form.obj.type==1 }">
					<img src="${ctx }/images/ico/ico4.png" class="vm"  style="margin-top:4px;" />
				</c:if>
				<c:if test="${form.obj.type==3 }">
					<img src="${ctx }/images/ico/infor.png" class="vm" style="margin-top:4px;" />
				</c:if>
			</span>
         </div>
         
        <div class="bookCont fontFam" style="padding-left: 4px;">
        	<h1><a href="#">${form.obj.title}</a></h1>
            <p class="blockP"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.author"/>：${form.obj.author }</p>
            <p class="blockP"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.publisher"/>：${form.obj.publisher.name }</p>
            <p class="blockP"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.pubDate"/>：${form.obj.pubDate }</p>
            <p class="blockP">E-ISBN：${form.obj.code}</p>
            <c:if test="${form.obj.hisbn !=null && form.obj.hisbn !='' }">
            	<p class="blockP">P-ISBN：${form.obj.hisbn}</p>
            </c:if>
            <p class="blockP"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.CLC"/>:
	    		<c:set var="csName"></c:set>
				<c:set var="names"></c:set>					
				<c:forEach items="${form.obj.csList }" var="cs" varStatus="a">
				<c:set var="csName">${cs.subject.code }  <c:if test="${sessionScope.lang=='zh_CN' }">${cs.subject.name}</c:if><c:if test="${sessionScope.lang=='en_US' }">${cs.subject.nameEn }</c:if></c:set>
				<c:set var="names">${names }${csName }</c:set>
		           <c:if test="${fn:length(form.obj.csList)!=(a.index+1) }"><c:set var="names">${names };</c:set></c:if>
				</c:forEach>					
				${names }
            </p>
           <p class="blockP">
			          		<span class="w110 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.Language"/>：</span>
			            	<span>${form.obj.lang }</span>
			          	</p>
			          	<c:if test="${form.obj.free!=2&&form.obj.oa!=2}">
			          	<p class="blockP">
			          		<span class="w110 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.price"/>：</span>
			             	<span>${form.obj.lcurr}&nbsp;<fmt:formatNumber value="${form.obj.listPrice}" pattern="0.00" /></span>
			          	</p>
			          	</c:if>
			         
			          <%-- <c:if test='${sessionScope.mainUser!=null}'>
			          <c:if test="${pricelist!=null && fn:length(pricelist)>0 && add==true }">
			          <tr>
			            <td valign="middle" class="tda"><ingenta-tag:LanguageTag key="Page.Index.Search.Lable.Price" sessionKey="lang" />：&nbsp;</td>
			            <td valign="middle" class="tdb">			
					  		<select id="price_${form.obj.id }"  name="price_${form.obj.id}" >
							<c:forEach items="${pricelist}" var="pr" varStatus="indexPr">
							<option value="${pr.id }"><c:if test="${pr.type==2 }">L</c:if><c:if test="${pr.type==1 }">P</c:if>${pr.complicating}-${pr.price }${pr.currency }</option>
							</c:forEach>
							</select>
						</td>
			          </tr>             
			          </c:if>				       
			          </c:if>  --%>
			         
			          <p class="mt10">
			          <%-- 	<c:if test="${form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2 }">
							<a class="link gret_eye" onclick="viewPopTips('${readPubId}','${readPubStartPage}')">
								<ingenta-tag:LanguageTag key="Page.Pop.Title.OLRead" sessionKey="lang" />
							</a>				
						</c:if> --%>
			          	<c:if test="${pricelist!=null && fn:length(pricelist)>0 &&add==true  }">
			          		<span>
							  <a href="javascript:void(0)" class="ico ico_cart" id="add_cart" onclick="addToCart('${form.obj.id}',1,1)">
							  	<ingenta-tag:LanguageTag key="Page.Index.Search.Link.AddToCart" sessionKey="lang" />
							  </a>
						    </span>
						</c:if>
						<%-- <span><a href="javascript:void(0)" class="ico ico_do" onclick="getResource('${form.obj.id}')"><ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" /></a></span> --%>
				<%-- 	<c:if test="${getResource}"> --%>
					<c:if test="${license==false }">
							<span>
							<a href="javascript:void(0)" id="resource_div" style="white-space: nowrap;" class="read" onclick="popTips2('${form.obj.id}');">
							<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
							</a>
							</span>
						</c:if>
						<c:if test="${license==true }">
						<c:if test="${form.obj.type==1}">
						<span>
							<a href="javascript:void(0)" id="resource_div1" style="white-space: nowrap;" class="read" onclick="viewPopTips('${readPubId}','${readPubStartPage}')">
								<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
							</a>
							</span>
							</c:if>
							<c:if test="${form.obj.type==4}">
							<a href="javascript:void(0)" id="resource_div" style="white-space: nowrap;" class="read" onclick="popTips2('${form.obj.id}');">
							<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
							</a>
							</c:if>
							</c:if>
				<%-- 		</c:if> --%>
						<c:if test="${recommand}">
							<span>
							<a  href="javascript:void(0)" id="recommend_div" style="white-space: nowrap;" class="ico ico_recommed" onclick="popTips('${form.obj.id}');">
							<ingenta-tag:LanguageTag key="Page.Index.Search.Button.Recommed" sessionKey="lang" />
							</a>
							</span>
						</c:if>
						
						<c:if test="${favourite}">
							<span class="favourite" id="${form.obj.id}">
							  	<a href="javascript:;" style="white-space: nowrap;" class="ico ico_collection">
							  		<span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span>
							  	</a>
							</span>
						</c:if>
						<c:if test="${sessionScope.mainUser!=null && !favourite}">
							<span class="favourite" id="${form.obj.id}">
								<a href="javascript:;" style="white-space: nowrap;" class="ico ico_collection2">
									<span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span>
								</a>
							</span>
						</c:if>
			          </p>     
        </div>
    </div>
    <!-- 图书信息结束 -->
    <!-- 简介开始 -->
    <div class="contIn" >
         <c:if test="${form.obj.remark!=null && form.obj.remark !='' && form.obj.remark !='[无简介]' }">
         	
               <h1 class="h1Tit borBot"><span><ingenta-tag:LanguageTag key="Pages.publications.article.Lable.Description" sessionKey="lang" /></span></h1>
                <p class="fontFam">        
         		 ${fn:replace(fn:replace(fn:replace(form.obj.remark,"&lt;","<"),"&gt;",">"),"&amp;","&")}  
        		</p>
              </c:if>
    </div>
    <!-- 简介结束 -->
      <c:if test="${list!=null && fn:length(list)>0 }">
      <div class="captList">
        <h1 class="h1Tit borBot"><span><ingenta-tag:LanguageTag key="Pages.publications.article.Lable.TOC" sessionKey="lang" /></span></h1>
        <c:forEach items="${list }" var="p" varStatus="index">         	
        	<c:if test="${form.chaperShow==1}">
        	<div style="margin-left:${p.browsePrecent}px" class=" ${p.fullText}" <c:if test="${index.index==fn:length(list)-1 }">style="border-bottom:none"</c:if>>
	          <p class="chapt_author_p" >
	          <c:if test="${form.obj.local==2 && (form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2) }"> 
	          	<c:if test="${p.startPage>0}">
	        		<%-- 	<c:if test="${license==true }"><img src="${ctx }/images/ico/ico_open.png" class="vm"/></c:if>
						<c:if test="${license==false }"><img src="${ctx }/images/ico/ico_close.png" class="vm"/></c:if> --%>
			   			 <a href="javascript:void(0)" onclick="viewPopTips('${p.publications.id}','${p.startPage}')">${p.title}</a>
	          	</c:if>    	
              	<c:if test="${p.startPage<=0}">${p.title}</c:if>
              	</c:if>
                 
              <c:if test="${form.obj.local==1||!(form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2)}">
               	<c:if test="${p.startPage>0}">
	               	<a href="javascript:void(0)" >${p.title}</a>
		        </c:if> 
		            <c:if test="${p.startPage<=0}">${p.title}</c:if>
              </c:if> 
	          </p>    
	          
	          <c:if test="${(p.fileType!=null && (p.fileType==1 || p.fileType==0)) && p.startPage>0}">   
	          <p class="chapt_author_p2" >
		          <ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.PageRange" sessionKey="lang" />：&nbsp;
		          <span>
		          <c:if test="${p.endPage!=null }">
	              	${p.startPage} – ${p.endPage}
	              </c:if>                            
	              <c:if test="${p.endPage==null }">
	              	${p.startPage}
	              </c:if>
	              </span>
	          </p>
	          </c:if>
              <p>
              	<c:if test="${p.homepage==2}">
              		<c:if test="${license==false}">
         			     	<!-- 获取资源 -->
         			     	<c:if test="${add }">
							<!-- 购买 -->
								<span class="mr20"><a href="javascript:void(0)" onclick="addToCart('${p.id}',1);"  title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Buy' sessionKey='lang'/>"><img src="${ctx }/images/ico/ico14-blank.png" class="vm" />添加到购物车</a></span> 
							</c:if>
							
         			     	<span class="mr20">
							<a href="javascript:void(0)" id="resource_div" style="white-space: nowrap;" title="<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />" onclick="popTips2('${p.id}');"><img id="favourites_${p.id }" src="${ctx }/images/ico/ico15-blue.png" class="vm" />
							<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
							</a>
							</span>
         			     	
							<c:if test="${recommand}">
							<!-- 推荐 -->
								<span class="mr20"><a href="javascript:void(0)" id="recommand_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' />" onclick="recommends('${p.id}');"><img src="${ctx }/images/ico/ico16-blue.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' /></a></span>
							</c:if>	
							
							<c:if test="${favourite }">
							<!-- 收藏 -->
								<span><a href="javascript:void(0)" id="favourites_div_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />" onclick="addFavourites('${p.id }');"><img id="favourites_${p.id }" src="${ctx }/images/ico/ico13-blue.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' /></a></span> 
							</c:if>
							
							<c:if test="${sessionScope.mainUser!=null && !favourite }">
							<!-- 已收藏 -->
								<span><a style="cursor:auto" class="collected" ><img title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />" src="${ctx }/images/ico/ico13-blank.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' /></a></span>
							</c:if>
						</c:if>
				 </c:if>	
              </p>
	        </div> 
	        </c:if>
	        </c:forEach>
	        </div>
	        </c:if>
    <!-- 章节列表开始 -->
	       <c:if test="${form.chaperShow==2}">
	       <c:set var="favouritechaper" value="${sessionScope.mainUser!=null&&p.favorite<=0 }"/>
	       <c:set var="recommandchaper" value="${sessionScope.institution!=null && (p.recommand>0||sessionScope.mainUser.institution!=null) &&(p.subscribedIp==null||p.subscribedIp<=0)&&(p.free!=2&&p.oa!=2)}"/>	
		   <div class="captList">
	          <h1 class="h1Tit borBot"><a href="${ctx}/pages/publications/form/charperView?id=${p.id}&pid=${p.publications.id}">
	         <c:if test="${license==true||p.subscribedIp>0||p.subscribedUser>0||p.free==2||p.oa==2 }"><img src="${ctx }/images/ico/ico_open.png" /></c:if>
			 <c:if test="${license==false&&p.subscribedIp==0&&p.subscribedUser==0 }"><img src="${ctx }/images/ico/ico_close.png" /></c:if>
			 ${ p.title}
			 </a>
	          </h1>
	          <c:if test="${p.author!=null && p.author !='' }">
		          <p class="intro">
			          <ingenta-tag:LanguageTag key="Global.Label.Author" sessionKey="lang" />：
			          ${ p.author}
		          </p>
	          </c:if>
	          <c:if test="${p.remark!=null && p.remark !='' && p.remark !='[无简介]' }">
                <p class="intro">
	                <div style="margin-left:30px;height:20px;overflow: hidden;"><a style="cursor: pointer;" onclick="senfe(this);">+ <ingenta-tag:LanguageTag key="Page.Index.Search.Desc.Show" sessionKey="lang" /></a>
						<p>
							${p.remark}
						</p>
					</div>
				</p>
              </c:if>
	          <p><ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.PageRange" sessionKey="lang" />：
	          ${p.startPage} – ${p.endPage}</p>
	          <p class="mt5">
	           <span>
		           <!-- 添加到购物车 -->
		           	<c:if test="${add }">
						<span class="mr20"><a href="javascript:void(0)" onclick="addToCart('${p.id}',1);" id="add_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Buy' sessionKey='lang'/>"><img src="${ctx }/images/ico/ico14-blank.png" class="vm" />添加到购物车</a></span> 
					</c:if>
	           		<!-- 获取资源 -->
		           <c:if test="${objlicense==false&&objoa==false&&objfree==false }">
						<span class="mr20">
						<a href="javascript:void(0)"  id="resource_div" style="white-space: nowrap;" class="" onclick="popTips2('${p.id}');"><img src="${ctx }/mobile/images/ico/ico15-blue.png" class="vm" />
						<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
						</a>
						</span>
					</c:if>
	           		<c:if test="${objlicense==true||objoa==true||objfree==true }">
						<c:if test="${p.type==1 }">
							<span class="mr20">
								<a href="javascript:void(0)"  id="resource_div" style="white-space: nowrap;" class="read" onclick="viewPopTips('${p.id}','0',<c:if test="${oa==false&&free==false}">1</c:if><c:if test="${oa==true||free==true}">2</c:if>)"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
								</a>
							</span>
						</c:if>
						<c:if test="${p.type==4 }">
							<span class="mr20">
								<a href="javascript:void(0)"  id="resource_div1" style="white-space: nowrap;"  class="read" onclick="popTips2('${p.id}');"><img src="${ctx }/images/ico/ico15-green.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Publications.Lable.Read" sessionKey="lang" />
								</a>
							</span>
						</c:if>
				</c:if>
				<!-- 推荐 -->
	           	<c:if test="${recommand}">
					<span class="mr20"><a href="javascript:void(0)" id="recommand_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' />" onclick="recommends('${p.id}');"><img src="${ctx }/images/ico/ico16-blue.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' /></a></span>
				</c:if>	
	           	<!-- 收藏 -->
	          	<c:if test="${favourite&&favouritechaper }">
					<span><a href="javascript:void(0)" id="favourites_div_${p.id }" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />" onclick="addFavourites('${p.id }');"><img id="favourites_${p.id }" src="${ctx }/images/ico/ico13-blue.png" class="vm" /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' /></a></span> 
				</c:if>
				<!-- 已收藏 -->
				<c:if test="${sessionScope.mainUser!=null && (!favourite||!favouritechaper) }">
				  	<span><a href="javascript:void(0)" class="blank"><img title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />" src="${ctx }/images/ico/ico13-blank.png" class="vm" /> <ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' /></a></span>
				</c:if>
	         </span>
	          </p>
           </div>
	       </c:if>
	       <!-- ***************************************正常显示章节列表end************************************************** --> 
    <!-- 表单结束 -->
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
		<!--以上 提交查询Form 结束-->
		
		<!-- 底部的版权信息 -->
	<div  style="margin-top: 15px; ">
   <jsp:include page="/footer" />
   </div>
    </div>
  </div>  	
</body>
</html>