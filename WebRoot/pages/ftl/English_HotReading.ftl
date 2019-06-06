<#if (hotlist?? && 0 < hotlist?size)>
	<div class="featureContainer">
	    <div class="feature">
	        <div class="black h650">
	            <div id="botton-scroll3">
	                <ul class=featureUL>
	                  <li class="featureBox">
	                    <div class="oh">
	                    <#list hotlist as p>
	                        <div class="enBigBlock">
	                        	<a href="${request.contextPath}/pages/publications/form/article/${p.publications.id}">
	                        		<img src="<#if p.publications.cover??>${request.contextPath}/pages/publications/form/cover?t=2&id=${p.publications.id}<#else>${request.contextPath}/images/noimg.jpg</#if>"  width="167" height="240" onerror="this.src='${request.contextPath}/images/noimg.jpg'" class="imgbor"/>
	                           	</a>
	                            <p>
	                           		<a class="a_title" title="${p.publications.title}" href="${request.contextPath}/pages/publications/form/article/${p.publications.id}">
										<#if (22 < p.publications.title?length)>${p.publications.title[0..21]}...<#else>${p.publications.title}</#if>
									</a>
	                            </p>
	                       </div>
			           		 <#if (5 == p_index || 11 == p_index)></div></li><li class=featureBox><div class="oh"></#if>
								<#if (17 == p_index)></div></li></#if>
	                  		</#list>
                   </ul>
				</div>
			</div>
              <a class="prev3 enNext1" href="#">Previous</a>
              <a class="next3 enNext1" href="#">Next</a> 
	     </div>
    </div>    
</#if>    
