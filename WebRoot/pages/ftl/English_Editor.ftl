<#if (editorRecommendsList?? && 0 < editorRecommendsList?size)>
	<div class="featureContainer">
		<div class="feature">
			<div class="black h450">
				 <div id="botton-scroll1">
					<ul class=featureUL>
	                    <li class=featureBox>
		                    <div class="oh">
		                    <#list editorRecommendsList as p>
								<div class="enBlock" style="margin-bottom: 25px;">
		                          		<h1>
	                          				<p class="bookTit">
		                          				<a class="a_title" title="${p.publications.title}" href="${request.contextPath}/pages/publications/form/article/${p.publications.id}">
		                          					<#if (p.publications.title)?length lt 20>   
														${p.publications.title}
													<#else> 
     													${p.publications.title[0..21]}... 
													</#if>
		                          				</a>
		                          			</p>
		                          		</h1>
		                          		<a href="${request.contextPath}/pages/publications/form/article/${p.publications.id}">
		                              		<img  src="<#if p.publications.cover??>${request.contextPath}/pages/publications/form/cover?t=1&id=${p.publications.id}<#else>${request.contextPath}/images/noimg.jpg</#if>"  width="95" height="130"  class="fl mr10" onerror="this.src='${request.contextPath}/images/noimg.jpg'"/
										</a>
										<p>By ${p.publications.author }
										</p>
										<p class="mt20">${p.publications.publisher.name}</p>
	                          	</div>
								<#if (3 == p_index || 7 == p_index)></div></li><li class=featureBox><div class="oh"></#if>
								<#if (11 == p_index)></div></li></#if>
	                  		</#list>
                    </ul>
				</div>
			</div>
			<a class="prev1" href="javascript:;">Previous</a>
            <a class="next1" href="javascript:;">Next</a> 
		</div>
	</div>            
</#if>