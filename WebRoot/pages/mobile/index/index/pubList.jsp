<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

		<h1 class="underH1">
             <ingenta-tag:LanguageTag key="Global.Label.New_Resources" sessionKey="lang"/>
        </h1>
		<c:if test="${list!=null&&fn:length(list)>0 }">
		<c:forEach items="${list}" var="p" varStatus="index">
		<!--  
		<c:set var="license">${(p.subscribedIp!=null||p.subscribedUser!=null)&&(p.subscribedIp>0||p.subscribedUser>0) }</c:set>
		-->
		<c:set var="license" value="${form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2 }"/>
		<c:set var="oa">${p.oa!=null&&p.oa==2 }</c:set>
		<c:set var="free">${p.free!=null&&p.free==2 }</c:set>
		<c:set var="i" value="${i+1}"></c:set>
			<c:if test="${5 > i}">
				<div class="mb20 fontFam oh">
        			<div class="w22 fl">
	                    <c:if test="${p.type==1}"><img width="13" height="13" src="${ctx}/images/ico/ico4.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Book" sessionKey="lang" />" /></c:if>
				        <c:if test="${p.type==2 || p.type==6|| p.type==7}"><img width="13" height="13" src="${ctx}/images/ico/ico3.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Journal" sessionKey="lang" />" /></c:if>
				        <c:if test="${p.type==4 }"><img width="13" height="13" src="${ctx}/images/ico/ico5.png" title="<ingenta-tag:LanguageTag key="Pages.Index.Lable.Article" sessionKey="lang" />" /></c:if>
                </div>
                    <div class="fl wid90">
	                    <h2>
	                    <a class="a_title" data-ajax="false"  href="${ctx}/mobile/pages/publications/form/article/${p.id}">
	                		${fn:replace(fn:replace(fn:replace(p.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}
	                	</a>
		                </h2>
				<c:if test="${not empty p.author}">
					<p>
						By
						<c:set var="authors" value="${fn:split(p.author,',')}"></c:set>
						<c:forEach items="${authors}" var="a">
							${a}&nbsp;
			                </c:forEach>
					</p>
				</c:if>
				<c:if test="${not empty p.publisher.name}">
							<p>${p.publisher.name}<c:if
									test="${not empty fn:substring(p.pubDate,0,4)}">(${fn:substring(p.pubDate,0,4) })</c:if>
							</p>
						</c:if>
						<c:if test="${p.type==2  && not empty p.startVolume && not empty p.endVolume}">
							<p>
							<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.startVolume }-<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" /> ${p.endVolume }
							</p>
						</c:if>
				</div>
                </div>
			</c:if>
		</c:forEach>
		</c:if>
		<c:if test="${list==null||fn:length(list)<=0 }">
		<ingenta-tag:LanguageTag key="Global.Label.Prompt.No.Product" sessionKey="lang"/>
</c:if>