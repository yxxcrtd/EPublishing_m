<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
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
<%@ include file="/mobile/tools.jsp"%>
<%@ include file="/mobile/ico.jsp"%>
</head>
<script language="javascript">
	function deleteItem(pid,item){
	art.dialog({
                   content:"<ingenta-tag:LanguageTag key='Pages.Cart.Lable.Delete' sessionKey='lang' /> ?",
                   okVal:'<ingenta-tag:LanguageTag key="Pages.Cart.Lable.Button1" sessionKey="lang" />',  
                   height:160,
                   width:300,
                   ok: function () {
                   		$.ajax({
						  			type : "POST",  
									url: "${ctx}/pages/cart/form/delete",
									data: { id:item,
										   r_ : new Date().getTime()
								},
								success : function(data) {  
								    var s = data.split(":");
								    if(s[0]=="success"){
								    	art.dialog.tips(s[1],2);//location.reload();			    	
								    }else{
								    	art.dialog.tips(s[1],2,'error');	
								    }
								},  
								error : function(data) {  
								    art.dialog.tips(data,2,'error');
								}  
							});
                   
                    },
	               cancelVal:'<ingenta-tag:LanguageTag key="Pages.Cart.Lable.Button2" sessionKey="lang" />',cancel:true,
                   lock:true
                   });
	}
	
	function apply(){
		if(document.getElementById("country")!=null&&(document.getElementById("country").value=="" || document.getElementById("country").value==0)){
			art.dialog.tips("<ingenta-tag:LanguageTag key='Page.Order.Checkout.Prompt.Check.Country' sessionKey='lang' />",1,'error');
			document.getElementById("country").focus();
			return;
		}
		document.getElementById("form").submit();
	}
	
</script>

<body>
	<div data-role="page" data-theme="c" class="page" id="pageorder">
		<jsp:include page="/header" />
		<div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1);"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1">购物车</h1>
    </div>
    <!-- 列表开始 -->
   	<div class="main">
   		<form:form id="form" commandName="form" action="${ctx}/mobile/pages/cart/form/checkout">
	<div class="cartNav">
    	<ul>
    	    <c:if test="${fn:length(list)==0 }">
            <div align="center">
            <c:if test="${sessionScope.lang == 'zh_CN'}"><span></br></br>您的购物车内暂时没有资源，您可以去<a href="${ctx}/">首页</a>挑选喜欢的资源。</br></br></span></c:if>
            <c:if test="${sessionScope.lang == 'en_US'}"><span></br></br>You currently have no items in your shopping cart, <a href="${ctx }">browse and find some items to add to your cart now!</a></br></br></span></c:if>
            </div>
            </c:if>
        </ul>
    </div>
   <c:if test="${isSpecial }">
     <div class="note">
    	<img src="${ctx }/images/ico/ico_20.png" class="vm" /> <ingenta-tag:LanguageTag key="Pages.Cart.Lable.Alert" sessionKey="lang" />
    </div>
   </c:if>
   
<c:if test="${fn:length(list)!=0}">
	<div class="order">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="cartInTab">
			<c:forEach items="${list }" var="l" varStatus="index">
				<input type="hidden" name="detailIds" value="${l.id}"/>
				<tr>
<c:if test="${l.collection==null}">
	<td>
		<p><a href="${ctx}/mobile/pages/publications/form/article/${l.price.publications.id}">${l.name}</a></p>
		<c:if test='${l.price.publications.author!=null&& l.price.publications.author!=""}'>
			<p>By <c:set var="authorName" value="${l.price.publications.author}" />${authorName}</p>
		</c:if>
		<p>
			<c:if test='${l.price.publications.publisher.name!=null&&l.price.publications.publisher.name!=""}'>
			${l.price.publications.publisher.name} 
			</c:if>
			<c:if test='${l.price.publications.pubDate!=null&&l.price.publications.pubDate!=""}'>
			(${fn:substring(l.price.publications.pubDate,0,4)})
			</c:if>
		</p>
	</td>
</c:if>
<c:if test="${l.collection!=null }">
	<td width="80" align="center" valign="top">
		<p><a href="${ctx}/pages/collection/form/list?id=${l.collection.id}">${l.name }</a></p>
		<c:if test='${ l.price.publications.author!=null&&l.price.publications.author!=""}'>
		<p>By  ${l.price.publications.author}</p>
		</c:if>
		<p>
		<c:if test='${l.price.publications.publisher.name!=null&&l.price.publications.publisher.name!=""}'>
		${l.price.publications.publisher.name} 
		</c:if>
		<c:if test='${l.price.publications.pubDate!=null&&l.price.publications.pubDate!=""}'>
		(${fn:substring(l.price.publications.pubDate,0,4) })
		</c:if>
		</p>
	</td>
</c:if>

	<td>
		<c:if test='${l.price.code=="special_price"}'><span style="color: red;">该出版物无价格,无法支付</span></c:if>        
		<c:if test='${l.price.code!="special_price"}'>￥${l.salePriceExtTax}</c:if>
	</td>
	<td width="50" align="right" valign="center">
		<a href="javascript:;" onclick="deleteItem('${l.price.publications.id }','${l.id}')" class="a_cancel">删除</a>
	</td>
          
				</tr>
          </c:forEach>
        </table>
        <c:if test="${fn:length(list)>0}">
			<p class="mt10">总计：<c:if test="${isSpecial&&form.totalPrice==0}">--</c:if><c:if test="${!isSpecial||form.totalPrice>0}">￥${form.totalPrice }</c:if></p>
	    </c:if>
	   
        <div class="tc mt30">
          	<a href="javascript:;" onclick="apply();" class="loginBig ui-link">进入结算中心</a>
        </div>
       </div>
        </c:if>
    </form:form>
   </div> 
   		<!--以上 提交查询Form 结束-->
		 <jsp:include page="/footer" />
</div>

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
   	</div>
</body>
</html>
