<div data-role="header">
	<div class="top">
		<div class="logo">
			<a href="/" data-ajax="false"><img src="/mobile/images/logo.png" width="80"></a>
		</div>
		<div class="topDiv" style="width: <#if (mainUser??)>23<#else>37</#if>%;">
			<input type="search" name="search" placeholder="搜索">
		</div>
		<div class="topClass">
			<a class="searCif classIco" href="/subject" data-ajax="false">分类法</a>
			<a class="searCif userIco" href="/mobile/pages/user/usercenter" data-ajax="false">个人中心</a>
			<#if (mainUser??)><a class="searCif cartIco" href="/mobile/pages/cart/form/list" data-ajax="false">购物车</a></#if>
		</div>
	</div>
</div>