<#if (pubList?? && 0 < pubList?size)>
	<#list pubList as p>
		<div class="mb20 fontFam oh">
			<div class="w22 fl"><img class="vm" src="${request.contextPath}/images/ico/ico<#if (1 == p.type)>4<#elseif (2 == p.type)>3<#elseif (3 == p.type || 4 == p.type)>5<#else>1</#if>.png" /></div>
	        <div class="fl wid90">
				<h2><a class="a_title" href="javascript:window.location.href='${request.contextPath}/mobile/pages/publications/form/article/${p.id}'">${p.title}</a></h2>
				<p>
					<#if (p.author?? && "null" != p.author)><p>By <#list p.author?split(",") as x><a href='javascript:window.location.href="${request.contextPath}/index/search?type=2&isAccurate=1&searchValue="${x}""'>${x}</a>&nbsp;&nbsp;&nbsp;&nbsp;</#list></#if>
				</p>
				<p>
					<#if (p.publisher??)><a href='javascript:window.location.href="${request.contextPath}/index/search?type=2&searchsType=4&searchValue="${p.publisher}""'>${p.publisher}</a></#if>
					<#if (p.date??)>(${p.date[0..3]})</#if>
				</p>
				<#if (p.type==2 && p.startVolume?? && p.endVolume??)>
					<p>Vol ${p.startVolume} - Vol ${p.endVolume}</p>
				</#if>
			</div>
		</div>
		<#if (4 == p_index)><#break /></#if>
	</#list>
</#if>