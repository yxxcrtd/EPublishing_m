<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
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
		<%@include file="/mobile/tools.jsp"%>
		<%@ include file="/mobile/ico.jsp"%>
	</head>

	<body>
	<div data-role="page" data-theme="c" class="page" id="pageorder">
		<jsp:include page="/header" flush="true" />
		  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1);"><img src="${ctx}/mobile/images/left.png" /></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Pages.Cart.Type.Ejournal" sessionKey="lang" /></h1>
    </div>
    			 <!-- 图书信息开始 -->
    <div class="oh mt20 mb20">
					<!--列表内容开始-->
					<c:set var="objnews">${form.obj.latest!=null&&form.obj.latest>0 }</c:set>
					<c:set var="objoa">${form.obj.oa!=null&&form.obj.oa==2 }</c:set>
					<c:set var="objfree">${form.obj.free!=null&&form.obj.free==2 }</c:set>
					<c:set var="objcollection">${form.obj.inCollection!=null&&form.obj.inCollection>0 }</c:set>
					<c:set var="add1"
						value="${form.obj.priceList!=null&&fn:length(form.obj.priceList)>0&&form.obj.free!=2&&form.obj.oa!=2&&sessionScope.mainUser!=null && form.obj.subscribedUser<=0&&(form.obj.buyInDetail<=0&&form.obj.exLicense>=0)}" />
					<c:if test="${add1==false }">
						<c:set var="objadd" value="false" />
					</c:if>
					<c:if test="${add1==true &&form.obj.subscribedIp>0 }">
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id&&sessionScope.mainUser.level==2 }">
							<c:set var="objadd" value="false" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id &&sessionScope.mainUser.level!=2 }">
							<c:set var="objadd" value="true" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id!=sessionScope.institution.id}">
							<c:set var="objadd" value="true" />
						</c:if>
					</c:if>
					<c:if
						test="${add1==true &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0) }">
						<c:set var="objadd" value="true" />
					</c:if>
					<c:if test="${add1==false }">
						<c:set var="objadd" value="false" />
					</c:if>
	
					<c:set var="objfavourite"
						value="${sessionScope.mainUser!=null&&form.obj.favorite<=0 }" />
					<c:set var="objrecommand"
						value="${(form.obj.recommand>0||sessionScope.mainUser.institution!=null) &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0)&&(form.obj.free!=2&&form.obj.oa!=2)}" />
					<c:set var="objlicense">${(form.obj.subscribedIp!=null||form.obj.subscribedUser!=null)&&(form.obj.subscribedIp>0||form.obj.subscribedUser>0) }</c:set>

					<c:if test="${objlicense==true }">
						<div class="h2_list">
					</c:if>
					<c:if test="${objlicense==false }">
						<div class="h1_list">
					</c:if>
					

	    <div class="book">
	    	<c:choose>
				<c:when test="${null == form.obj.cover || '' == form.obj.cover}"><img width="100%" src="${ctx}/images/noimg.jpg" /></c:when>
				<c:otherwise><img width="100%" src="${ctx}/mobile/pages/publications/form/cover?t=2&id=${form.obj.id}" onerror="this.src='${ctx}/images/noimg.jpg'" /></c:otherwise>
			</c:choose>
	    </div>
	    <div class="fl w22 mt5"> <span title="${form.obj.title}">
			<c:if test="${objlicense==true}">
				<img src="${ctx }/images/ico/ico_open.png" class="vm"/>
			</c:if>
			<c:if test="${objoa}">
				<img src="${ctx }/images/ico/o.png" class="vm"/>
			</c:if>
			<c:if test="${objfree}">
				<img src="${ctx }/images/ico/f.png" class="vm"/>
			</c:if>
			<c:if test="${objlicense==false && !objoa && !objfree }">
				<img src="${ctx }/images/ico/ico_close.png" class="vm"/>
			</c:if>
			<img src="${ctx }/images/ico/ico3.png" class="vm" />
		</span> 
		</div>
	   
        <div class="bookCont fontFam">
        	<h1><a href="#">${fn:replace(fn:replace(fn:replace(form.obj.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}
		 	<c:if test="${form.obj.type!=2}">&nbsp(${form.obj.month}-${form.obj.year})</c:if></a></h1>
					<div class="oh w760 fl" style="width: 100%; height: 100%;">
			        <div class="prodDetal" <c:if test="${form.obj.journalType==2&&form.obj.type==7}">style="width: 700px; float: left;"</c:if> >
			            <div class="oh">
			          		<div class="fl pridDetalCont">
			          		<c:if test="${form.obj.available==5 }">	
       							<div style="clear:both; padding:2px 0 10px 20px;"><img src="${ctx }/images/ico_20.png" style="vertical-align:middle; margin-right:8px;"/> <span style="color:red"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.available"/></span></div>
       						</c:if>
       		<c:if test="${form.obj.journalType!=2}">	
       		<c:if test="${pcrlist1!=null||pcrlist3!=null||pcrlist5!=null }">
            <td valign="middle" class="tdb">
      			<c:forEach items="${pcrlist3 }" var="pc3" varStatus="index">
      				<c:if test="${pc3.mark=='21' }"><p class="ml50"><img src="${ctx}/images/ico/ico21.png" class="vm mr5" />本刊自${pc3.occurTime}年起，由<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.separateCon.id}">${pc2.separateCon.title }</a></c:forEach>合刊而成</p></c:if>
      			</c:forEach>
      			<c:forEach items="${pcrlist5 }" var="pc5" varStatus="index">
      				<c:if test="${pc5.mark=='3' }"><p class="ml50"><%-- ${pc5.occurTime} --%>并正式更名为<c:forEach items="${pcrlist4 }" var="pc4" varStatus="index">${pc4.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc4.issueCon.id}">${pc4.issueCon.title }</a></c:forEach></p></c:if>
      			</c:forEach>
			</td>
			</c:if>
			</c:if>
			</p>			
				 <%-- <c:if test="${form.obj.publications.id !=null && form.obj.publications.id !='' }">
                    <p class="blockP">
                    	<span class="w100 tr">刊名：</span>
                        <span>
                       
                        ${form.obj.publications.title }
                        </span>
                    </p>
                 </c:if> --%>
                 <c:if test="${pcrlist1!=null||pcrlist3!=null||pcrlist5!=null }">
						<p class="ml50">
							<img class="vm mr5" src="${ctx }/images/ico/ico21.png">
								 <c:forEach items="${pcrlist1 }" var="pc1" varStatus="index">
				      				<c:if test="${pc1.mark=='12' }">${pc1.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.Points" sessionKey="lang" />：<c:forEach items="${pcrlist }" var="pc" varStatus="index">${pc.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc.issueCon.id}">${pc.issueCon.title }</a></c:forEach></c:if>
				      				<c:if test="${pc1.mark=='1' }">${pc1.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.Combined1" sessionKey="lang" />：<c:forEach items="${pcrlist }" var="pc" varStatus="index">${pc.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc.issueCon.id}">${pc.issueCon.title }</a></c:forEach></c:if>
				      			</c:forEach>
				      			<c:forEach items="${pcrlist3 }" var="pc3" varStatus="index">
				      				<c:if test="${pc3.mark=='21' }">${pc3.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.By" sessionKey="lang" />：<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.separateCon.id}">${pc2.separateCon.title }</a></c:forEach><ingenta-tag:LanguageTag key="Page.Publications.Journals.Combined" sessionKey="lang" /></c:if>
				      				<c:if test="${pc3.mark=='2' }">${pc3.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.By" sessionKey="lang" />：<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.separateCon.id}">${pc2.separateCon.title }</a></c:forEach><ingenta-tag:LanguageTag key="Page.Publications.Journals.Points" sessionKey="lang" /></c:if>
				      			</c:forEach>
				      			<c:forEach items="${pcrlist5 }" var="pc5" varStatus="index">
				      				<c:if test="${pc5.mark=='3' }">${pc5.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.Changed" sessionKey="lang" />：<c:forEach items="${pcrlist4 }" var="pc4" varStatus="index">${pc4.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc4.issueCon.id}">${pc4.issueCon.title }</a></c:forEach></c:if>
				      			</c:forEach>
				      			<c:forEach items="${pcrlist5 }" var="pc5" varStatus="index">
				      				<c:if test="${pc5.mark=='31' }">${pc5.occurTime}<ingenta-tag:LanguageTag key="Page.Publications.Journals.By" sessionKey="lang" />：<c:forEach items="${pcrlist4 }" var="pc4" varStatus="index">${pc4.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc4.separateCon.id}">${pc4.separateCon.title }</a></c:forEach><ingenta-tag:LanguageTag key="Page.Publications.Journals.Changed1" sessionKey="lang" /></c:if>
				      			</c:forEach>
						</p>
					</c:if>
        <%--        <c:if test="${form.obj.type==7}">
               	<p class="blockP">
               		<span class="w100 tr"><ingenta-tag:LanguageTag key="Page.Frame.Issue" sessionKey="lang" />：</span>
               		<span>
               			期次
               		</span>
               	</p>
               </c:if> --%>
					<p >
                    	<ingenta-tag:LanguageTag key="Pages.publications.article.Label.publisher" sessionKey="lang" />： ${form.obj.publisher.name }
                				</p>
                				<c:if test="${form.obj.pubDate!=null }">
                				<p >
                    	<ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.Year"/>:${form.obj.pubDate }
                    </p>
                    </c:if>
                    <p >
                    	<ingenta-tag:LanguageTag key="Page.Frame.Count.Lable.Total" sessionKey="lang" />：${artCount}
                    </p>
                    <c:if test="${form.obj.journalType==2}">
                      <p >
                    	<ingenta-tag:LanguageTag key="Page.Frame.Periodic" sessionKey="lang" />：                        
                         	 <c:if test="${form.obj.periodicType==1}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Weekly" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==2}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Semi.monthly" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==3}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.monthly" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==4}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Quarterly" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==5}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Semi.annual" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==6}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.annual" sessionKey="lang" /></c:if>
						     <c:if test="${form.obj.periodicType==7}"><ingenta-tag:LanguageTag key="Page.Frame.Periodic.type.Other" sessionKey="lang" /></c:if>
                        </p>
                    </c:if>
                    <c:if test="${form.obj.eissn!=null}">
                    <p>
                    	E-ISSN： ${form.obj.eissn }
                    </p>
                    </c:if>
					<p >
							ISSN：${fn:split(form.obj.code,'|')[0]}
					</p>
					<p >
                    	<ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.CLC"/>：
						<c:set var="csName"></c:set>
						<c:set var="names"></c:set>					
						<c:forEach items="${form.obj.csList }" var="cs" varStatus="a">
						<c:set var="csName">${cs.subject.code }  <c:if test="${sessionScope.lang=='zh_CN' }">${cs.subject.name}</c:if><c:if test="${sessionScope.lang=='en_US' }">${cs.subject.nameEn }</c:if></c:set>
						<c:set var="names">${names }${csName }</c:set>
			            <c:if test="${fn:length(form.obj.csList)!=(a.index+1) }"><c:set var="names">${names };</c:set></c:if>
						</c:forEach>					
						${names }
						
                    </p>
                    <%-- <c:if test="${form.obj.free!=2&&form.obj.oa!=2}">
			          	<p class="blockP">
			          		<span class="w100 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.price"/>：</span>
			             	<span>${form.obj.lcurr}&nbsp;<fmt:formatNumber value="${form.obj.listPrice}" pattern="0.00" /></span>
			          	</p>
			         </c:if> --%>
                    <p >
					     <c:if test="${objadd }">
							<a href="javascript:void(0)" id="add_${form.obj.id}" class="ico ico_cart" title="<ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang' />" onclick="addToCart('${form.obj.id}',1,'1')">
				        	  	<ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang' />
				          	</a>
						</c:if>
						<c:if test="${sessionScope.mainUser!=null && !objadd }">
								<span><a href="javascript:void(0)" id="add_${form.obj.id}"  class="ico ico_cart2"><ingenta-tag:LanguageTag key="Page.Publications.Lable.Buy" sessionKey="lang" /></a></span>
						</c:if>
<%-- 		<c:if test="${objlicense==true||objoa==true||objfree==true}">
		
		<a class="link gret_eye" onclick="viewPopTips('${form.obj.id}','0')">
					<ingenta-tag:LanguageTag key="Page.Pop.Title.OLRead" sessionKey="lang" />
				</a>
</c:if> --%>
			
			             <c:if test="${form.obj.type==7 && form.obj.journalType==2}">
						    <c:set var="license" value="${form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2 }"/>
							  <c:if test="${objlicense||objadd||objfavourite||objrecommand||objoa==true||objfree==true}">
							    <c:if test="${license==false }">
								<span>
								<a href="javascript:void(0)" id="resource_div" class="ico ico_do" onclick="viewPopTips1('${form.obj.id}','0');">
								<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
								</a>
								</span>
							 </c:if>
							 <c:if test="${license==true }">
							  <span>
								   <a href="javascript:void(0)" id="resource_div" class="ico ico_doin" onclick="viewPopTips1('${form.obj.id}','0');">
								<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
								</a>
								 </span>
								 </c:if>
							  </c:if>
							   </c:if>
								<c:if test="${objfavourite}">
									<span class="favourite"  id="${form.obj.id }" >
									   	<a href="javascript:;"  class="ico ico_collection" >
									   		<span style="margin-right: 15px;"><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span>
										</a>
									</span>
								</c:if>
								<c:if test="${sessionScope.mainUser!=null && !objfavourite}">
									<span class="favourite" id="${form.obj.id }">
									   	<a href="javascript:;"  class="ico ico_collection2" >
											<span style="margin-right: 15px;"><ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span>
										</a>
									</span>
								</c:if>	
		                    </p>
		                    </div>
				</div>
				
				<c:if test="${form.obj.remark!=null && form.obj.remark!=''&& form.obj.remark!='[无简介]' }">
				<div class="mt10">
	                <h1 class="h1Tit borBot"><ingenta-tag:LanguageTag sessionKey="lang" key="Page.Publications.Journals.Abstract"/></h1>
	                <p class="fontFam">${fn:replace(fn:replace(fn:replace(fn:replace(form.obj.remark,"&lt;","<"),"&gt;",">"),"&amp;","&"),"<ul>","<ul class='patientia'>")}</p>
	            </div>
	            </c:if>
	            </div>
	         </div>
         </div>
       </div>
      </div>
     
		<!--列表内容结束-->
	 <!-- 章节列表开始 -->
	 <c:if test="${form.obj.journalType!=2 }">
	<c:if test="${artCount >0}">
   		<div class="captList">
    	<h1>刊内卷期</h1>
        <!-- 一个开始 -->
        <c:if test="${form.obj.journalType!=2 }">
			<c:if test="${artCount >0}">
		        <c:forEach items="${ylist}" var="v" varStatus="index">
		        	<div class="mb20">
		        	<h2 class="mb10 fb">${v.year}</h2>
		        			<ul class="menu journalList">
		        		<c:forEach items="${issueList }" var="iss">
		        			<c:if test="${v.year == iss.year }">
		                <li><a id="year_${iss.year }_${iss.volumeCode}_${iss.issue.id}" href="javascript:;" onclick="getIssues(this);">第${iss.volumeCode}卷(${iss.year }-${iss.month})</a> </li>
		        			</c:if>
		        		</c:forEach>
		              </ul>
		        </div>
		        </c:forEach>
        	</c:if>
        </c:if>
        <!-- 一个结束 -->	
        </div>
       </c:if>
      </c:if> 
      <jsp:include page="/footer" />



<script type="text/javascript">
$(function() {
	initMenu();
	openFirst();
});
function initMenu() {
	$(".menu ul").hide();
	$(".menu ul:first").show();
	$(".menu li a").bind("click", function() {
		//getIssues(this);
		var checkElement = $(this).next();
		if((checkElement.is("ul")) && (checkElement.is(":visible"))) {
			$(checkElement).slideToggle("normal");
		}
		if((checkElement.is("ul")) && (!checkElement.is(":visible"))) {
 	        $(".menu ul:visible").slideUp("normal")
			checkElement.slideDown("normal");
			return false;
		}
	});
}

function openFirst() {
	var objYear = $(".journalList li").eq(0).find("a").prop("id");
	var year_id = objYear.replace("year_", "");
	var arr = year_id.split("_");
	var year = arr[0];
	var volumeCode = arr[1];
	var issueId = arr[2];
	var	url = "${ctx}/mobile/pages/publications/form/queryPubs";
	var jID = '${journal.id}';
	
	var htmlcode = "";
	var parObj = $(objYear).parent();
	if(parObj.find("ul").length==0){
		$.ajax({
			type : "GET",
	        url: url,
	        data: {	pubYear:year,
	        		journalId:jID,
	        		type:4,
	        		issueId:issueId
	        		},
	        dataType: "json", 
	        success : function(data) {
	            var json = eval(data.list);
	            pubtype=data.pubtype;
	            htmlcode='<ul>';	
	            if(json.length>0){
	             	for(var i=0;i<json.length;i++){
	             		var favourite;
	             		var favouritechaper;
	             		var user ="${sessionScope.mainUser}";
	             		var objfav="${form.obj.favorite}";
	             		if(user!=null && json[i].favorite<=0){
	             			favouritechaper=true;
	             		}else{
	             			favouritechaper=false;
	             		}
	             		
	             		htmlcode +='<li class=""><p><a href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'">';
	             		htmlcode +=json[i].title+'</a></p>';	
	             		if(json[i].publisher.name!=null){
	             			htmlcode +=' <p>'+json[i].publisher.name+'</p>'
	             		}
	             		if(json[i].startPage!=null && json[i].startPage!="" && json[i].endPage !=null && json[i].endPage !=""){
	             			htmlcode +=' <p> page '+json[i].startPage+'-'+json[i].endPage+'</p>'
	             		}
	             		htmlcode+='<p><img src="${ctx }/images/ico/ico-det.png" width="16" height="16" class="vm mr5 mt1"/><a href="javascript:abst(\''+json[i].id+'\');">摘要</a>  &nbsp;&nbsp;&nbsp;'; 
	             		htmlcode+='<img src="${ctx}/images/ico/ico15-blue.png" class="vm" /><a href="javascript:;" onclick="popTips2(\''+json[i].id+'\');"}>获取资源</a> &nbsp;&nbsp;&nbsp;';
	             		
	             		// 收藏 
	             		if(user!=null && user!="" && favouritechaper ){
	             			htmlcode+='<span class="favourite2">';
             				htmlcode+='<a href="javascript:;" id="'+json[i].id+'" class="" onclick="favourite2(\''+json[i].id+'\')">';
         					htmlcode+='<img src="${ctx}/mobile/images/unfavourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span>';
         					htmlcode+='</a>';
       						htmlcode+='</span>'; 
	             		}
	             		//已收藏 
	             		if(user!=null && user!="" && !favouritechaper){
	             			htmlcode+='<span class="favourite2" >';
	             			htmlcode+='<a href="javascript:;" id="'+json[i].id+'" class="blank" onclick="favourite2(\''+json[i].id+'\')">';
             				htmlcode+='<img src="${ctx}/mobile/images/favourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span>';
             				htmlcode+='</a>';
         					htmlcode+='</span>';
	             		}
	             		htmlcode+='</p>';
	             		htmlcode += '</li>';
	             	}	             	
	             	htmlcode +='</ul>';
             	}
	            $(".journalList li").eq(0).append(htmlcode);
            }	           
      });
	}
}

function getIssues(objYear){
	var year_id =$(objYear).attr("id").replace("year_","");
	var arr = year_id.split("_");
	var year = arr[0];
	var volumeCode=arr[1];
	var issueId=arr[2];
	var	url="${ctx}/mobile/pages/publications/form/queryPubs";
	issueList(url,objYear,year,issueId);
}

//查询期刊文章列表
function issueList(url,objYear,year,issueId){
		var parObj=$(objYear).parent();
		var jID='${journal.id}';
		
		if(parObj.find("ul").length==0){
			$.ajax({
				type : "POST",
				async : false,    
		        url: url,
		        data: {	pubYear:year,
		        		journalId:jID,
		        		type:4,
		        		issueId:issueId
		        		},
		        success : function(data) { 
		            var json = eval(data.list);
		            pubtype=data.pubtype;
		            var htmlcode='<ul  style="display: none;">';	
		            if(json.length>0){
		             	for(var i=0;i<json.length;i++){
		             		var favourite;
		             		var favouritechaper;
		             		var user ="${sessionScope.mainUser}";
		             		var objfav="${form.obj.favorite}";
		             		if(user!=null && json[i].favorite<=0){
		             			favouritechaper=true;
		             		}else{
		             			favouritechaper=false;
		             		}

		             		
		             		htmlcode +='<li class=""><p><a href="${ctx}/mobile/pages/publications/form/article/'+json[i].id+'">';
		             		htmlcode +=json[i].title+'</a></p>';	
		             		if(json[i].publisher.name!=null){
		             			htmlcode +=' <p>'+json[i].publisher.name+'</p>'
		             		}
		             		if(json[i].startPage!=null && json[i].startPage!="" && json[i].endPage !=null && json[i].endPage !=""){
		             			htmlcode +=' <p> page '+json[i].startPage+'-'+json[i].endPage+'</p>'
		             		}
		             		htmlcode+='<p><img src="${ctx }/images/ico/ico-det.png" width="16" height="16" class="vm mr5 mt1"/><a href="javascript:abst(\''+json[i].id+'\');">摘要</a>  &nbsp;&nbsp;&nbsp;'; 
		             		htmlcode+='<img src="${ctx}/images/ico/ico15-blue.png" class="vm" /><a href="javascript:;" onclick="popTips2(\''+json[i].id+'\');"}>获取资源</a> &nbsp;&nbsp;&nbsp;';
		             		
		             		// 收藏 
		             		if(user!=null && user!="" && favouritechaper ){
		             			htmlcode+='<span class="favourite2">';
	             				htmlcode+='<a href="javascript:;" id="'+json[i].id+'" class="" onclick="favourite2(\''+json[i].id+'\')">';
             					htmlcode+='<img src="${ctx}/mobile/images/unfavourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.Favorite" sessionKey="lang" /></span>';
             					htmlcode+='</a>';
           						htmlcode+='</span>'; 
		             		}
		             		//已收藏 
		             		if(user!=null && user!="" && !favouritechaper){
		             			htmlcode+='<span class="favourite2" >';
		             			htmlcode+='<a href="javascript:;" id="'+json[i].id+'" class="blank" onclick="favourite2(\''+json[i].id+'\')">';
	             				htmlcode+='<img src="${ctx}/mobile/images/favourite.png" class="vm" /><span><ingenta-tag:LanguageTag key="Page.Index.Search.Link.collected" sessionKey="lang" /></span>';
	             				htmlcode+='</a>';
             					htmlcode+='</span>';
		             		}
		             		htmlcode+='</p>';
		             		htmlcode += '</li>';
		             	}	             	
		             	htmlcode +='</ul>';
	             	}

		            $(parObj).append(htmlcode);
	            },  
	            error : function(data) {  
	              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
	            }  		           
	      });
		}
	}



function favourite2(id) {
	var This = $(this);
	var obj=$("#"+id);
	//This.each(function() {
		$.get("${ctx}/mobile/pages/favourites/form/commit", { pubId : id }, function(data) {
			if ("success" == data) {
				obj.find("a").attr("class", "blank");
				obj.find("img").attr("src", "${ctx}/mobile/images/favourite.png");
				obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
				//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.success' sessionKey='lang' />', 1, 'success');
			} else if ("del" == data) {
				obj.find("a").attr("class", "");
				obj.find("img").attr("src", "${ctx}/mobile/images/unfavourite.png");
				obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />");
				//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.cancel' sessionKey='lang' />', 1, '');
			}
		});
	//});
}

function favourite(id){
		var This = $(this);	
		var obj=$("#"+id);
		//This.each(function() {
			$.get("${ctx}/mobile/pages/favourites/form/commit", { pubId : id }, function(data) {
				if ("success" == data) {
					obj.find("a").attr("class", "ico ico_collection2");
					obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
					obj.find("img").attr("src", "${ctx}/mobile/images/favourite.png");
					//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.success' sessionKey='lang' />', 1, 'success');
				} else if ("del" == data) {
					obj.find("a").attr("class", "ico ico_collection");
					obj.find("img").attr("src", "${ctx}/mobile/images/unfavourite.png");
					obj.find("span").html("<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />");
					//art.dialog.tips('<ingenta-tag:LanguageTag key='Controller.Favourites.commit.cancel' sessionKey='lang' />', 1, '');
				}
			});
		// });
}

	function abst(id){
		window.location.href="${ctx}/mobile/pages/publications/form/article/"+id+"#abstract";
	}
	
	//添加到购物车
	function addToCart(pid, ki,type) {
		var price = $("#price_" + pid).val();
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
					$("#cartCount").html("["+s[2]+"]");
					if(type=='1'){
						$("#add_"+pid).attr("class","ico ico_collection2");
						if($("#select_price_" + pid)!=null&&$("#select_price_" + pid)!=undefined ){
							$("#select_price_" + pid).css("display","none");
						}else{
							$("#price_" + pid).css("display","none");
						};
					}else{
						var pl = $("#options_ li").length;//价格长度
						//删除价格
						if(pl>1){//还有其他价格，隐藏现在购买的价格
							$("#price_"+pid+" option[value='"+price+"']").remove();
							//删除全部样式
							$("div[class='select_box']").remove();
							selects = document.getElementsByTagName('select');
							rSelects();
						}else{
							//没有价格了，隐藏购买按钮，隐藏价格
							$("#add_"+pid).css("display","none");
							$("#select_info_").css("display","none");
						}
						//$("#select_info_").css("display","none");
						//$("#selected_").css("display","none");
					}
				}else{
					$("#add_"+pid).attr("class","ico ico_collection2");
				}
			},
			error : function(data) {
				art.dialog.tips(data,1,'error');
			}
		});
	}
	
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
	function popTips() {
		showTipsWindown("<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang" />",
				'simTestContent', $(window).width()*0.6, $(window).height()*0.65);
	}
	//弹出层调用
	function popTips(pid) {
		art.dialog.open("${ctx}/pages/recommend/form/edit?pubid="+pid,{title:"<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/>",top: 100,width: 520, height: 480,lock:true});
	}
	//获取资源弹出层调用
	function popTips2(downId) {
		art.dialog.open("${ctx}/pages/publications/form/getResource?pubid="+downId + "&downId=" + downId, {id : "getResourceId",title:"",top: 200,width: 340, height: 200,lock:true});
	}
	//在线阅读
	function viewPopTips(id,page,yon) {
		var url="";
		var tmp=window.open("about:blank","","fullscreen=1") ;
            tmp.moveTo(0,0)  ;
            tmp.resizeTo(screen.width-800,screen.height-100);
            //tmp.focus() ;
		if(page=='0'){
			url = "${ctx}/pages/view/form/view?id="+id;
		}else{
			url = "${ctx}/pages/view/form/view?id="+id+"&nextPage="+page;
		}
		//首先Ajax查询要阅读的路径
	if(yon=='2'){
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
				if(s[1].indexOf('/pages/view/form/view')>=0){
				//	window.location.href=s[1];
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
	
	//在线阅读弹出层调用
	function viewPopTips1(id,page) {
		alert("====================2=========");
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
</script>
	</body>
</html>
