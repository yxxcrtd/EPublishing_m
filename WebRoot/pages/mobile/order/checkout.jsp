<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
<script language="javascript">
function apply(){	
	/* var email = document.getElementById("email").value;
	if(email.replace(/\s+/g,"")==""){
		art.dialog.tips("<ingenta-tag:LanguageTag key='Page.Order.Checkout.Prompt.Check.Email' sessionKey='lang' />",1,'error');
		document.getElementById("email").focus();
		return;
	}else{
		
	  	var myreg = /^([a-zA-Z0-9]+[_|\-|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
           if(!myreg.test(email)){
                art.dialog.tips("<ingenta-tag:LanguageTag key='Page.Order.Checkout.Prompt.Check.Email2' sessionKey='lang' />",1,'error');
                myreg.focus();
             	return false;
        	}
        document.getElementById("emailCheck").value=email;
	} */	
	if(document.getElementById("country")!=null&&(document.getElementById("country").value=="" || document.getElementById("country").value==0)){
		art.dialog.tips("<ingenta-tag:LanguageTag key='Page.Order.Checkout.Prompt.Check.Country' sessionKey='lang' />",1,'error');
		document.getElementById("country").focus();
		return;
	}
	document.getElementById("form").submit();
}

</script>
</head>

<body>
<div data-role="page" data-theme="c" class="page" id="pageorder">
		<jsp:include page="/header"/>
		<div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1);"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1">结算中心</h1>
    </div>
<form:form id="form" commandName="form" action="${ctx }/mobile/pages/order/form/submit?alipay=3">
    <c:set var="isPack">${1==2 }</c:set>
    <c:set var="isSpecial">${1==2 }</c:set>
	<c:forEach items="${listdetails }" var="l" varStatus="index">
	    　  	<c:if test='${l.price.code=="special_price" }'>
	   		<c:set var="isSpecial">${1==1 }</c:set> 
	   	</c:if>
	   	<c:if test='${l.price.category==2 }'>
	   		<c:set var="isPack">${1==1 }</c:set> 
	   	</c:if>
   </c:forEach>
   <c:if test="${isSpecial }">
     <div class="note">
    	<img src="${ctx }/images/ico/ico_20.png" class="vm" /> <ingenta-tag:LanguageTag key="Pages.Cart.Lable.Alert" sessionKey="lang" />
    </div>
   </c:if>
   <div class="order">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="cartInTab">
   <c:forEach items="${listdetails }" var="l" varStatus="index">
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
	
	</tr>
  </c:forEach>
 </table>
	 		  <c:set var="flag">0</c:set>
		  <c:forEach items="${list}" var="c" varStatus="index">
            	<c:if test="${c.display!='select'&&c.code!='freetax'&&c.code!='businessCode'&&c.code!='notes' }">            		
            		           		
					<c:if test="${(c.user.level==1||c.user.level==5)&&(c.code=='emailCheck' || c.code=='email') }">
						<input type="hidden" id="${c.code}" name="propsValue" value="${form.values[c.code]}"/>
						<input type="hidden" name="typePropIds" value="${c.id}"/>
					</c:if>
					<c:if test="${c.code!='emailCheck' && c.code!='email'  }">
					<input type="hidden" name="typePropIds" value="${c.id}"/>
					<c:if test="${flag==0 }"><tr></c:if>					
						<td style="text-align:right;"><ingenta-tag:LanguageTag sessionKey='lang' key='${c.key}'/>： <c:if test="${c.must!=1}"> </c:if></td>
						<td><input type="hidden" id="${c.code}" name="propsValue" value="${form.values[c.code]}" style="vertical-align:middle;"/><!-- <span style="color: #900;margin:0 4px;">*</span> --></td>
					<c:if test="${flag==1 }"></tr></c:if>
            		</c:if>
            		<c:set var="flag">${1-flag}</c:set>
            	</c:if>
            	<c:if test="${c.display=='select' && c.svalue=='countryMap' }">
            		<input type="hidden" name="typePropIds" value="${c.id}"/>
            		<c:if test="${flag==0 }"><tr></c:if>		
            			<td style="text-align:right;"><ingenta-tag:LanguageTag sessionKey='lang' key='${c.key}'/>： <c:if test="${c.must!=1}"></c:if></td>
            			<td>
		            		<select id="${c.code}" name="propsValue" style="margin-top:3px; ">
			        		<option value="0"><ingenta-tag:LanguageTag sessionKey="lang" key="Global.Label.Select"/></option>
			        		<c:forEach items="${form.countryMap}" var="p">
			        			<option value="${p.key}" <c:if test="${p.key==form.values[c.code]}"> selected</c:if>>${p.value}</option>
							</c:forEach>	
			       			</select>
			       			<span style="color: #900;margin:0 4px;">*</span>
			       		</td>
			       	<c:if test="${flag==1 }"></tr></c:if>		
			       	<c:set var="flag">${1-flag}</c:set>
            	</c:if>
            	
			</c:forEach>	
			<c:if test="${flag==1}"><td>&nbsp;</td></tr></c:if>
			<c:forEach items="${list}" var="c" varStatus="index">
				<c:if test="${c.display!='select' && c.code=='notes' }">
					<input type="hidden" name="typePropIds" value="${c.id}"/>
					<tr>
						<td style="text-align:right;vertical-align:top;"><ingenta-tag:LanguageTag sessionKey='lang' key='${c.key}'/>： <c:if test="${c.must!=1}"> <span>*</span></c:if></td>
						<td style="vertical-align:top;" colspan="3"><textarea id="${c.code}" name="propsValue">${form.values[c.code]}</textarea></td>
					</tr>
				</c:if>         	
			</c:forEach>
	 
		        
	 
	 <c:if test="${fn:length(list)>0}">
			<p class="mt10">总计：<c:if test="${isSpecial&&form.totalPrice==0}">--</c:if><c:if test="${!isSpecial||form.totalPrice>0}">￥${form.totalPrice }</c:if></p>
	  </c:if>
	    
	 <form:hidden path="paytype" /> 
	<input type="hidden" name="${form_token_key}" value="${token}"/>
	</form:form>
	 <p class="off">支付信息</p>
	    <p>	
            <label for="female">支付宝支付</label>
            <input type="radio" name="gender" id="female" value="female" checked="checked">	
        </p>
        <div class="tc mt30">
          	<a href="javascript:;" onclick="apply();" class="loginBig ui-link">确认</a>
        </div>
	
	</div>
	 <jsp:include page="/footer" />
	</div>
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
		<!--以上 提交查询Form 结束-->
	
</body>
</html>
