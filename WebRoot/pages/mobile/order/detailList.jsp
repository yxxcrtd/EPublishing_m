<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
<script type="text/javascript">
			   
			$(document).ready(function(){
			/* alert('${form.alipay}');
			alert('${order.payType}'); */
				var sum = 0.00;
				var alipay1=3;
				$("input[name='statusPrice']").each(function(){
	               sum = add(sum,$(this).attr("value"));
            	});
            	$("#saleTotalPrice").html(sum);
            	$("#saleTotalPrice").formatCurrency({symbol:'￥'});
            	if(alipay1=="${form.alipay}"&&alipay1=="${order.payType}"){
            	sayHello();
            	}
			});
			function sayHello(){  
              // alert("Hello");  
               window.location.href="${ctx}/mobile/pages/order/alipaySubmit?orderId=${order.id}";
             } 
			
		</script>
</head>

<body>
<div data-role="page" data-theme="c" class="page">
<!--以下top state -->
<jsp:include page="/header" />
<!--以上top end -->
<!-- header -->
  <div data-role="content">
	<div data-role="header" class="classifyTit">
        <a class="back" href="javascript:history.go(-1)"><img src="${ctx}/mobile/images/left.png"></a>
        <h1 class="ui-title-h1"><ingenta-tag:LanguageTag key="Pages.User.MyAccount.Label.contentTitle4" sessionKey="lang" /></h1>
    </div>
    <div class="order">
         <div class="borderDiv noTop">
                    <p class="blockP">
                        <span class="w100 tr"><ingenta-tag:LanguageTag key="Page.Order.Checkout.Lable.OCode" sessionKey="lang" />：</span>
                        <span class="w200">${order.code}</span></br>
                        <span class="w100 tr"><ingenta-tag:LanguageTag key="Page.Order.Checkout.Lable.Date" sessionKey="lang" />：</span>
                        <span class="w200"><fmt:formatDate value="${order.createdon }" pattern="yyyy-MM-dd"/></span>
                    </p>
                    <p class="blockP">
                        <span class="w100 tr"><ingenta-tag:LanguageTag key="Page.Order.Checkout.Lable.TotalPrice" sessionKey="lang" />：</span>
                  	 	<span class="w200" id="saleTotalPrice"></span>
                        
                    </p>
                    <p class="blockP">
						<c:if test="${order.payType==3&&(order.status==2||order.status==1) &&order.tradeNo==null&&detailSendStatus!=2&&detailSendStatus!=10}"></c:if> 
                    </p>
                </div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="orderTab mt50" >
                  <tr class="trTop">
                    <th class="pl20" ><ingenta-tag:LanguageTag key="Page.Order.Checkout.Lable.Pub" sessionKey="lang" /></th>
                    <th width="100" class="tc"><ingenta-tag:LanguageTag key="Page.Order.Checkout.Lable.Price" sessionKey="lang" /></th>
                  </tr>
                  <c:forEach items="${detailList }" var="d" varStatus="index">
                 	 <tr class="trBody">
				        <c:set var="tdCss"><c:if test="${index.index%2==0 }">abodytd</c:if><c:if test="${index.index%2!=0 }">bbodytd</c:if></c:set>
					        <td align="left" class="pl10">
						        <c:if test="${d.itemType==99}">
						        	<a href="${ctx }/mobile/pages/collection/form/list?id=${d.collection.id}">${d.name }</a>
						        	<%-- ${d.name } --%><br />
						        	${d.price.publications.publisher.name }<br />
						        </c:if>
						        <c:if test="${d.itemType!=99 }">
						        <a href="javascript:window.location.href='${ctx}/mobile/pages/publications/form/article/${d.price.publications.id}'">${d.name}</a>
						        <%-- ${d.name } --%><br />
						        ${d.price.publications.publisher.name }<br />
						        </c:if>
					        </td>
					        <td  align="center">￥<fmt:formatNumber value="${d.salePriceExtTax }" pattern="0.00"/></td>
					        <td  align="center">
					        	<c:if test="${d.status!=99 }"><input type="hidden" name="statusPrice" value="${d.salePriceExtTax }"/></c:if>
							</td>
				        </tr>
			        </c:forEach>
			        <tr style="border-left-style: hidden;border-right-style: hidden;border-bottom-style: hidden;">
			                <td colspan="5" class="f_tda">
			                <ingenta-tag-v3:SplitTag first_ico="${ctx }/images/ico_left1.gif"
			                	last_ico="${ctx }/images/ico_right1.gif" 
			                	prev_ico="${ctx }/images/ico_left.gif" 
			                	next_ico="${ctx }/images/ico_right.gif" 
			                	method="post"
			                	formName="form"
			                	pageCount="${form.pageCount}" 
			                	count="${form.count}" 
			                	page="${form.curpage}" 
			                	url="${form.url}" 
			                	i18n="${sessionScope.lang}"/>
			                </td>
			               </tr>
                </table>  
          
    	</div>
    		<!-- 底部的版权信息 -->
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
