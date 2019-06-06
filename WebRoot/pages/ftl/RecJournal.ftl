<#if (list?? && 0 < list?size)>
<div class="featureContainer">
	<div class="feature">
		<div class="black h320">
			 <div id="botton-scroll2">
				<ul class=featureUL>
					<li class=featureBox>
						<!-- 一个滚动条内容开始 -->
						<div class="oh">
							<div class="block jourList">
							<#list list as p>
							 <#assign license=((p.publications.subscribedIp??||p.publications.subscribedUser??)&&(p.publications.subscribedIp>0||p.publications.subscribedUser>0))?string("true","false") />
							 <#assign oa=(p.publications.oa??&&p.publications.oa==2 )?string("true","false") />
					 		 <#assign free=(p.publications.free??&&p.publications.free==2 )?string("true","false")/>
								<div class="oh w350 fl" style="margin-bottom: 7px;">
                                    <div class="fl w40 mt2">
                                        <#if (license=='true'||oa=='true'||free=='true')><img src="${request.contextPath}/images/ico/ico_open.png" width="16" height="16" /></#if> 
                                        <#if (license=='false' && oa=='false' && free=='false')><img src="${request.contextPath}/images/ico/ico_close.png" width="16" height="16" /></#if> 
                                        <img src="${request.contextPath}/images/ico/ico3.png" width="13" height="13" />
                                    </div>
                                    <div class="fl w300">
                                        <p class="omit">
										<a class="a_title" title="${p.publications.title}" href="${request.contextPath}/pages/publications/form/article/${p.publications.id}">
														${p.publications.title}
						                </a>
										</p>
                                        <p class="omit"><a href='${request.contextPath}/index/search?type=2&isAccurate=1&searchValue="${p.publications.publisher.name }"'>${p.publications.publisher.name}</a></p>
										<p>
										<#if ( p.publications.startVolume?? &&p.publications.endVolume??)>
											Volume  ${p.publications.startVolume }  -  Volume  ${p.publications.endVolume }
										<#else> 
											</br>
										</#if>
										</p>
									</div>
                                 </div>
                               <#if (7 == p_index || 15 == p_index)></div></li><li class=featureBox><div class="block jourList" style="height:450px"></#if>
								<#if (23 == p_index)></div></li></#if>
	                  		</#list>
                   </ul>
				</div>
			</div>
		 <a class="prev2 chPrev1" href="javascript:void(0)">Previous</a>
         <a class="next2 chNext1" href="javascript:void(0)">Next</a> 
	</div>
		<!-- /feature -->
</div>
</#if>