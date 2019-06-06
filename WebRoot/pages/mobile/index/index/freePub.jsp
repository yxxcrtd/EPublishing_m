<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/mobile/taglibs.jsp"%>
		<h1 class="underH1"><ingenta-tag:LanguageTag key="Global.Label.freePub" sessionKey="lang"/><span class="fr"><a href="javascript:window.location.href='${ctx}/mobile/pages/index/freePub?allfree=true'" style="font-size: 0.7em;">more>></a></span></h1>
		<c:if test="${list!=null&&fn:length(list)>0}">
		<c:forEach items="${list}" var="p" varStatus="index">
        <div class="mb20 fontFam oh">
        	<div class="w22 fl">
        		<c:if test="${p.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
				<c:if test="${p.type==2 || p.type==6|| p.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
				<c:if test="${p.type==4||p.type==3 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
        	</div>
            <div class="fl wid90">
                <h2><a data-ajax="false" href="${ctx}/mobile/pages/publications/form/article/${p.id}" >${fn:replace(fn:replace(fn:replace(p.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></h2>
                <p><c:if test="${not empty p.author}">
					<p>By <c:set var="authors" value="${fn:split(p.author,',')}" ></c:set>
					<a href='${request.contextPath}/index/search?type=2&searchsType=4&searchValue="${p.publisher}"'>
			                <c:forEach items="${authors}" var="a" >
			                <a href='${ctx }/index/search?type=2&isAccurate=1&searchValue="${a}"'>${a}</a>&nbsp;
			                </c:forEach></p>
			        </a>
				</c:if> </p>
                				<p>
					${p.publisher.name}
					<c:if test="${not empty fn:substring(p.pubDate,0,4)}">(${fn:substring(p.pubDate,0,4) })</c:if>
				</p>
				<c:if test="${p.type==2 && not empty p.startVolume && not empty p.endVolume}">
				<p>
				<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.endVolume }
				</p>
				</c:if>
    		</div>
		</div>
</c:forEach>
		</c:if>
		<c:if test="${list==null||fn:length(list)<=0 }">
				<ingenta-tag:LanguageTag key="Global.Label.Prompt.No.Product" sessionKey="lang"/>
		</c:if>