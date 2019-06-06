<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv='X-UA-Compatible' content='IE=edge' />
<title>易阅通</title>
<style type="text/css">
#box {
	width: 500px;
	height: 500px;
	position: relative;
}

.host {
	position: absolute;
	width: 100px;
	height: 50px;
	line-height: 50px;
	text-align: center;
	color: #000000;
	background-color: #eeeeee;
	border: #000000 1px solid;
	font-weight: bolder;
}

.guest {
	position: absolute;
	width: 80px;
	height: 40px;
	line-height: 40px;
	text-align: center;
	color: #999999;
	background-color: #FFFFFF;
	border: #000000 1px solid;
	cursor: pointer;
}

.relationship {
	position: absolute;
	width: 60px;
	height: 20px;
	color: #aaa;
	line-height: 20px;
	font-size: 12px;
	text-align: center;
}
</style>
<%@include file="/common/tools.jsp"%>
<script type="text/javascript" src="${ctx}/js/raphael-min.js"></script>
<script type="text/javascript" src="${ctx}/js/dracula_graffle.js"></script>
<script type="text/javascript" src="${ctx}/js/dracula_graph.js"></script>
<script type="text/javascript">
	window.onload = function() {
		getCount1();
		var width = 800; //定义宽度，也可以为 var width=$('#div').width()
		var height = 500; //定义高度
		var g = new Graph();
		//g.edgeFactory.template.style.directed = true; //设置链接之间是否有箭头
		//以下为定义任务关系
		var style = {
			fg : '#222222',
			bg : '#222222',
			directed : true
		};
		<c:forEach items="${relation}" var="p">
		g.addEdge("${p.separateCon.issn }", "${p.issueCon.issn }", style);
		</c:forEach>
		var layouter = new Graph.Layout.Spring(g);
		layouter.layout();

		// TODO - TypeError: i is null
		var renderer = new Graph.Renderer.Raphael('canvas', g, width, height);
		renderer.draw("${issn}");
	};
	
	function getCount1(){
		var testExp=/^\d+$/;
		$(".chapt_author_p3 ").each(function(i,item){
			var pagNubStr=$(item).text().trim();
			var numbers=pagNubStr.split("-");
			//console.info(numbers);
			var range=1;
			if(numbers){
				var spage=numbers[0].trim().match(testExp)?parseInt(numbers[0].trim()):numbers[0];
				if(numbers.length==2){
					var epage= numbers[1].trim().match(testExp)?parseInt(numbers[1].trim()):numbers[1];
					range=epage-spage+1;					
				}
				$(item).text(pagNubStr+" ("+range+")");
			}
		})
	}
</script>

<script type="text/javascript">
$(function(){

	initMenu();
	
	var currYear='${currYear}';
	if(currYear!='' && currYear !=null){
		$("#year_" + currYear).click();
		getIssuesObj(currYear, "0");
	}
		$("#selectAll").click(function(){
			if($(this).attr("checked")){
     			$("input[name='publicationsIds']").attr("checked",true);
     		}else{
     			
     			$("input[name='publicationsIds']").attr("checked",false);
     		}
		});		
	});
	

	//左侧条件查询
	function searchByCondition(type,value,a1,a2,a3,a4){
		document.formList.action="${ctx}/pages/publications/form/list";
		if(type=="searchValue"){
			$("#searchValue1").val(value);
			if(${sessionScope.selectType==1}){
	 			$("#lcense1").val("1");
	 			document.formList.action="${ctx}/index/searchLicense";
		 	}else{
		 		document.formList.action="${ctx}/index/search";
		 	}
		}else if(type=="type"){
			$("input[name='pubType']").val(value);
		}else if(type=="publisher"){
			$("input[name='publisher']").val(value); 
			$("input[name='publisherId']").val(a1);
		}else if(type=="pubDate"){
			$("input[name='pubDate']").val(value);
		}else if(type=="taxonomy"){
			$("#taxonomy1").val(value);
			$("#pCode1").val(a1);
			$("#code1").val(a2);
			$("#subParentId1").val(a3);
			$("#parentTaxonomy1").val(a4);
			document.formList.action="${ctx }/pages/subject/form/list";
		}else if(type=="taxonomyEn"){
			$("#taxonomyEn1").val(value);
			$("#pCode1").val(a1);
			$("#code1").val(a2);
			$("#subParentId1").val(a3);
			$("#parentTaxonomyEn1").val(a4);
			document.formList.action="${ctx }/pages/subject/form/list";
		}
		$("#page").val(0);
		$("#pageCount").val(10);
		$("#order1").val('');
		$("#lcense1").val('${sessionScope.selectType}');
		document.formList.submit();
	}
	//******************************弹出层--开始*********************************//
	/*
	 *弹出本页指定ID的内容于窗口
	 *id 指定的元素的id
	 *title: window弹出窗的标题
	 *width: 窗口的宽,height:窗口的高
	 */
	function showTipsWindown(title, id, width, height) {
		tipsWindown(title, "id:" + id, width, height, "true", "", "true", id);
	}
	function confirmTerm() {
		parent.closeWindown();
	}
	//弹出层调用
	function popTips() {
		showTipsWindown("<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang" />",
				'simTestContent', $(window).width()*0.6, $(window).height()*0.65);
	}
	//弹出层调用
	function popTips(pid) {
		art.dialog.open("${ctx}/pages/recommend/form/edit?pubid="+pid,{title:"<ingenta-tag:LanguageTag key="Page.Pop.Title.Recommend" sessionKey="lang"/>",top: 100,width: 520, height: 480,lock:true});
	}
	//获取资源弹出层调用
	function popTips2(downId) {
		art.dialog.open("${ctx}/pages/publications/form/getResource?pubid="+downId + "&downId=" + downId, {id : "getResourceId",title:"",top: 200,width: 340, height: 200,lock:true});
	}
	//在线阅读
	function viewPopTips(id,page,yon) {
		var url="";
		var tmp=window.open("about:blank","","fullscreen=1") ;
            tmp.moveTo(0,0)  ;
            tmp.resizeTo(screen.width-800,screen.height-100);
            //tmp.focus() ;
		if(page=='0'){
			url = "${ctx}/pages/view/form/view?id="+id;
		}else{
			url = "${ctx}/pages/view/form/view?id="+id+"&nextPage="+page;
		}
		//首先Ajax查询要阅读的路径
	if(yon=='2'){
		//window.location.href=url;
		 tmp.location=url;
	}else{
	$.ajax({
		type : "POST",
		async : false,
		url : "${ctx}/pages/publications/form/getUrl",
		data : {
			id : id,
			nextPage:page,
			r_ : new Date().getTime()
		},
		success : function(data) {
			var s = data.split(";");
			if (s[0] == "success") {
				if(s[1].indexOf('/pages/view/form/view')>=0){
				//	window.location.href=s[1];
				 tmp.location=s[1];
				}else{
					//window.location.href="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
					tmp.location="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
				}
			}else if(s[0] == "error"){
				art.dialog.tips(s[1],1,'error');
			}
		},
			error : function(data) {
				art.dialog.tips(data,1,'error');
			}
		});
		}
	}	
	
	//在线阅读弹出层调用
	function viewPopTips1(id,page) {
		var url="";
		var tmp=window.open("about:blank","","scrollbars=yes,resizable=yes,channelmode") ;          
            //tmp.focus() ;
		if(page=='0'){
			url = "${ctx}/pages/view/form/view?id="+id;
		}else{
			url = "${ctx}/pages/view/form/view?id="+id+"&nextPage="+page;
		}
		//首先Ajax查询要阅读的路径
		if('${form.obj.free}'=='2'||'${form.obj.oa}'=='2'){
		//window.location.href=url;
		 tmp.location=url;
	}else{
	$.ajax({
		type : "POST",
		async : false,
		url : "${ctx}/pages/publications/form/getUrl",
		data : {
			id : id,
			nextPage:page,
			r_ : new Date().getTime()
		},
		success : function(data) {
			var s = data.split(";");
			if (s[0] == "success") {
				if(s[1].indexOf('/pages/view/form/view')>=0){
					//window.location.href=s[1];
					tmp.location=s[1];
				}else{
					//window.location.href="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
					tmp.location="${ctx}/pages/view/form/view?id="+id+"&webUrl="+s[1];
				}
			}else if(s[0] == "error"){
				art.dialog.tips(s[1],1,'error');
			}
		},
			error : function(data) {
				art.dialog.tips(data,1,'error');
			}
		});
		}
	}
	
	
	//******************************弹出层--结束*********************************//
</script>
<script type="text/javascript">
  var pubtype;
  
function initMenu() {
	$('#menu ul').hide();
	$('#menu ul:first').show();
	var strst =${pubtype};
	$("#menu li a").bind("click", function() {
	
	if(strst==7){
		getIssuesObj(this); 
	}else{
	getIssues(this); 
	
	}
	
		var checkElement = $(this).siblings().next();
		if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
			$('#menu ul:visible').slideUp('normal');
// 			checkElement.slideDown('normal');
			return false;
		} else{
			$(checkElement).slideToggle('normal');
		}
	});
}
</script>
<script type="text/javascript">
		function addToCart(pid, ki,type) {
					var price = $("#price_" + pid).val();
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/cart/form/add",
						data : {
							pubId : pid,
							priceId : price,
							kind : ki,
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1);//location.reload();
								$("#cartCount").html("["+s[2]+"]");
								if(type=='1'){
									$("#add_"+pid).attr("class","ico ico_collection2");
									if($("#select_price_" + pid)!=null&&$("#select_price_" + pid)!=undefined ){
										$("#select_price_" + pid).css("display","none");
									}else{
										$("#price_" + pid).css("display","none");
									};
								}else{
									var pl = $("#options_ li").length;//价格长度
									//删除价格
									if(pl>1){//还有其他价格，隐藏现在购买的价格
										$("#price_"+pid+" option[value='"+price+"']").remove();
										//删除全部样式
										$("div[class='select_box']").remove();
										selects = document.getElementsByTagName('select');
										rSelects();
									}else{
										//没有价格了，隐藏购买按钮，隐藏价格
										$("#add_"+pid).css("display","none");
										$("#select_info_").css("display","none");
									}
									//$("#select_info_").css("display","none");
									//$("#selected_").css("display","none");
								}
							}else{
								$("#add_"+pid).attr("class","ico ico_collection2");
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				function addFavourites(pid) {
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/favourites/form/commit",
						data : {
							pubId : pid,
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1);//location.reload();
								$("#favourites_img_"+pid).attr("src","${ctx}/images/list_favourite.png");
								$("#favourites_"+pid).attr("class","ico ico_collection2");
								$("#favourites_"+pid).removeAttr("onclick");
								$("#favourites_"+pid).attr("title","<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
						//		$("#favourites_"+pid).html("<img id='favourites_${p.id }' src='${ctx }/images/ico/ico13-blank.png' class='vm' /><ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
								$("#favourites_"+pid).css("cursor","auto");								
								$("#favourites_check_"+pid).attr("checked",true);
							}else{
								art.dialog.tips(s[1],1,'error');
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				function recommendSubmit() {
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/recommend/form/submit",
						data : {
							pubid : $("#pubid").val(),
							note : $("#rnote").val(),
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1,'error');
								confirmTerm();
							}else{
								alert(s[1]);
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}
				
				function recommendSubmittwo() {
					$.ajax({
						type : "POST",
						url : "${ctx}/pages/recommend/form/submit",
						data : {
							pubid : $("#pubid").val(),
							note : $("#rnote").val(),
							r_ : new Date().getTime()
						},
						success : function(data) {
							var s = data.split(":");
							if (s[0] == "success") {
								art.dialog.tips(s[1],1,'error');
								confirmTerm();
							}else{
								alert(s[1]);
							}
						},
						error : function(data) {
							art.dialog.tips(data,1,'error');
						}
					});
				}

function GO(obj){
	document.formList.action="${ctx}/pages/publications/form/article/"+"${journal.id}?fp=3&pageCount="+$(obj).val();
	document.formList.submit();
}
function IssuesObj(url,objYear,vYear){
	var parObj=$(objYear).parent();
	var jID='${journal.id}';
	if(parObj.find("ul").length==0){
		
		var number=0;
		$.ajax({
			type : "POST",
			async : false,    
	        url: url,
	        data: {pubYear:vYear,
	        		parentId:jID,
	        		type:7,
	        		n:number},
	        success : function(data) { 
	            var json = eval(data.list);
	            pubtype=data.pubtype;
	            var htmlcode='<ul style="display: none;">';			            
	            if(json.length>0){
	            	var vol='&nbsp;&nbsp;&nbsp;&nbsp;<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />';
	            	vol=vol.replace('ume','');
	             	for(var i=0;i<json.length;i++){
	             		htmlcode +='<li class=""><a href="';
	             		htmlcode +='${ctx}/pages/publications/form/journaldetail/${journal.id}?issueId=';
	             		htmlcode += json[i].id;
	             		htmlcode += '">'+vol+'&nbsp;';
	             		htmlcode += json[i].volumeCode;
	             		htmlcode += '&nbsp;<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Issue" sessionKey="lang" />&nbsp;';
	             		htmlcode += json[i].issueCode;
	             		htmlcode += ',('+ vYear + '-' + json[i].month;
	             		htmlcode += ')</a></li>';
	             	}	             	
	             	htmlcode +='</ul>';
             	}
             	$(parObj).append(htmlcode);
            },  
            error : function(data) {  
              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
            }  		           
      });
	}
	
	
}
function IssuesObjNew(url,objYear,vYear){
	var parObj=$(objYear).parent();
	var jID='${journal.id}';
	if(parObj.find("ul").length==0){
		
		var number=0;
		$.ajax({
			type : "POST",
			async : false,    
	        url: url,
	        data: {pubYear:vYear,
	        		parentId:jID,
	        		type:7,
	        		n:number},
	        success : function(data) { 
	            var json = eval(data);
	            var htmlcode='<ul style="display: none;">';			            
	            if(json.length>0){
	            	var vol='&nbsp;&nbsp;&nbsp;&nbsp;<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />';
	            	vol=vol.replace('ume','');
	             	for(var i=0;i<json.length;i++){
	             		htmlcode +='<li class=""><a href="';
	             		htmlcode +='${ctx}/pages/publications/form/journaldetail/${journal.id}?issueId=';
	             		htmlcode += json[i].id;
	             		htmlcode += '">'+vol+'&nbsp;';
	             		htmlcode += json[i].volumeCode;
	             		htmlcode += '&nbsp;<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Issue" sessionKey="lang" />&nbsp;';
	             		htmlcode += json[i].issueCode;
	             		htmlcode += ',('+ vYear + '-' + json[i].month;
	             		htmlcode += ')</a></li>';
	             	}	             	
	             	htmlcode +='</ul>';
             	}
             	$(parObj).append(htmlcode);
            },  
            error : function(data) {  
              	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
            }  		           
      });
	}

}

function getIssues(objYear){
	
		var vYear=$(objYear).attr("id").replace("year_","");
		var	url="${ctx}/pages/publications/form/queryPubs";
		IssuesObj(url,objYear,vYear);

}

function getIssuesObj(objYear, i){
	var vYear;
	if ("0" == i) {
		vYear = objYear;
	} else {
		vYear=$(objYear).attr("id").replace("year_","");
	}
	
	var jID='${journal.id}';
	var parObj=$("#issue");
	$.ajax({
		type:"post",
		url: "${ctx}/pages/publications/form/issue",
		data:{
			pubYear:vYear,
      		parentId:jID,
      		type:7
		},
    /*   success : function(data) { 
     	 var json = eval(data.issueList);
          if(json.length>0){
        	  $(parObj).html("");
           	for(var i=0;i<json.length;i++){
      		   var htmlcodeNew;
           	    htmlcodeNew=' <div class="fl w330 mt2"> ';
          		htmlcodeNew+='<img src=" ';
           		htmlcodeNew+='${ctx}/pages/publications/form/cover?id=';
           		htmlcodeNew+= json[i].id;
           		htmlcodeNew+='"'+'width="95" height="129" onerror=" ';
           		htmlcodeNew+='this.src=\'${ctx}/images/noimg.jpg\' " class="fl mr20" ';
           		htmlcodeNew+=' />';
           		htmlcodeNew+='<br>'; 
           		
           		htmlcodeNew+='<p>';
           		htmlcodeNew+= '<a href=" ';
           		htmlcodeNew +='${ctx}/pages/publications/form/journaldetail/${journal.id}?issueId=';
           		htmlcodeNew +=json[i].id;
           		htmlcodeNew += '">'+json[i].title;
           		htmlcodeNew += '</a>';
           		htmlcodeNew+='</p>';
           	
           			
           		htmlcodeNew+=' <p>';
           		htmlcodeNew+='<a href="javascript:void(0) "  id="resource_div" class="ico ico_doin"  onclick="';
           		htmlcodeNew+=' viewPopTips1(\'';
           		htmlcodeNew +=json[i].id;
           		htmlcodeNew +='\',\'0\'); ';
           		htmlcodeNew+=' "> ';
           		htmlcodeNew+='<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />';
           		htmlcodeNew+='</a> ';
           		htmlcodeNew+='</p>';
           		
    
	           	htmlcodeNew +=' </div>';
       			$(parObj).append(htmlcodeNew);
           	}	             	
       	}
         
      },   */
     /*  error : function(data) {  
        	alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Subject.info.get.error'/>");
      }  		            */
      success : function(data) { 
       	$(parObj).html(data);
      }
	});

}

	var pubStr = "";
  	  var allIdStr = "";
      var pubIds=new Array(); 
      function getPid(){
      		pubStr="";
            $("input[name='publicationsIds']").each(function() {  
                if ($(this).attr("checked")) {  
                    pubIds.push($(this).val());
                    pubStr += $(this).val()+"@";
                }  
            });
      }
      function getSid(){
      		allIdStr = "";
            $("input[name='srcIds']").each(function() {  
                allIdStr += $(this).val()+"@";
            });
      }
      function batchSub(){
      		getSid();
      		getPid();
      		if(pubIds!=null && pubIds.length>0){
	          	$.ajax({
					type : "POST",
					url : "${ctx}/pages/favourites/form/batchCommit",
					data : {
					    srcIds : allIdStr,
						pubIds : pubStr,
						r_ : new Date().getTime()
					},
					success : function(data) {
						var s = data.split(":");
						if (s[0] == "success") {
							art.dialog.tips(s[1],1);//location.reload();
							
							var shows= new Array(); //定义一数组
							shows=allIdStr.split("@"); //字符分割      
							var hids= new Array();
							hids=pubStr.split("@");
							
							for (i=0;i<shows.length ;i++ ){
								if(shows[i]!=''){
								$("#favourites_img_"+shows[i]).attr("src","${ctx}/images/ico/list_unfavourite.png");
								$("#favourites_"+shows[i]).attr("onclick","addFavourites('"+ shows[i] +"')");
								$("#favourites_"+shows[i]).attr("title","<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />");
								$("#favourites_"+shows[i]).css("cursor","pointer");
								}
							}							
							for (i=0;i<hids.length ;i++ ){   
								if(hids[i]!=''){ 
								$("#favourites_img_"+hids[i]).attr("src","${ctx}/images/list_favourite.png"); 
								$("#favourites_"+hids[i]).removeAttr("onclick");
								$("#favourites_"+hids[i]).attr("title","<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />");
								$("#favourites_"+hids[i]).css("cursor","auto");
								}
							}
							
						}else{
							art.dialog.tips(s[1],1,'error');
						}
					},
					error : function(data) {
						art.dialog.tips(data,1,'error');
					}
				});
			}else{
				art.dialog.tips("<ingenta-tag:LanguageTag key="Pages.Favorite.Prompt.Not.Selected" sessionKey="lang" />",1,'error');
			}	
		}
</script>
<%@ include file="/common/ico.jsp"%>
</head>
<body id="uboxstyle">
	
		<jsp:include page="/pages/header/headerData" flush="true" />
		<!--定义01 mainContainer 内容区开始-->
		<div class="main personMain" style="margin-top: 15px;">
			
					<%-- <h1><ingenta-tag:LanguageTag key="Page.Publications.Journal.Article.List" sessionKey="lang" /></h1> --%>
					<!--列表内容开始-->
					<c:set var="objnews">${form.obj.latest!=null&&form.obj.latest>0 }</c:set>
					<c:set var="objoa">${form.obj.oa!=null&&form.obj.oa==2 }</c:set>
					<c:set var="objfree">${form.obj.free!=null&&form.obj.free==2 }</c:set>
					<c:set var="objcollection">${form.obj.inCollection!=null&&form.obj.inCollection>0 }</c:set>
					<c:set var="add1"
						value="${form.obj.priceList!=null&&fn:length(form.obj.priceList)>0&&form.obj.free!=2&&form.obj.oa!=2&&sessionScope.mainUser!=null && form.obj.subscribedUser<=0&&(form.obj.buyInDetail<=0&&form.obj.exLicense>=0)}" />
					<c:if test="${add1==false }">
						<c:set var="objadd" value="false" />
					</c:if>
					<c:if test="${add1==true &&form.obj.subscribedIp>0 }">
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id&&sessionScope.mainUser.level==2 }">
							<c:set var="objadd" value="false" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id &&sessionScope.mainUser.level!=2 }">
							<c:set var="objadd" value="true" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id!=sessionScope.institution.id}">
							<c:set var="objadd" value="true" />
						</c:if>
					</c:if>
					<c:if
						test="${add1==true &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0) }">
						<c:set var="objadd" value="true" />
					</c:if>
					<c:if test="${add1==false }">
						<c:set var="objadd" value="false" />
					</c:if>
	
					<c:set var="objfavourite"
						value="${sessionScope.mainUser!=null&&form.obj.favorite<=0 }" />
					<c:set var="objrecommand"
						value="${(form.obj.recommand>0||sessionScope.mainUser.institution!=null) &&(form.obj.subscribedIp==null||form.obj.subscribedIp<=0)&&(form.obj.free!=2&&form.obj.oa!=2)}" />
					<c:set var="objlicense">${(form.obj.subscribedIp!=null||form.obj.subscribedUser!=null)&&(form.obj.subscribedIp>0||form.obj.subscribedUser>0) }</c:set>

					<c:if test="${objlicense==true }">
						<div class="h2_list">
					</c:if>
					<c:if test="${objlicense==false }">
						<div class="h1_list">
					</c:if>
					<div class="oh f14">
						<div class="w90 fl mr10 tr">
							<span title="${form.obj.title}">
							<c:if test="${objlicense==true}">
								<img src="${ctx }/images/ico/ico_open.png" class="vm"/>
							</c:if>
							<c:if test="${objoa}">
								<img src="${ctx }/images/ico/o.png" class="vm"/>
							</c:if>
							<c:if test="${objfree}">
								<img src="${ctx }/images/ico/F.png" class="vm"/>
							</c:if>
							<c:if test="${objlicense==false && !objoa && !objfree }">
								<img src="${ctx }/images/ico/ico_close.png" class="vm"/>
							</c:if>
							<img src="${ctx }/images/ico/ico3.png" class="vm" />
							</span>
						</div>
						 <div class="fl w700 fb">
						 	<span class="blue fb f14">
						 		${fn:replace(fn:replace(fn:replace(form.obj.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}
						 		<c:if test="${form.obj.type!=2}">&nbsp(${form.obj.month}-${form.obj.year})</c:if>
						 	</span>
						 </div>
					</div>
					
					<div class="oh">
				        <div class="prodDetal">
				            <div class="oh">
				          		<div class="fl pridDetalCont">
				          		<c:if test="${form.obj.available==5 }">	
        							<div style="clear:both; padding:2px 0 10px 20px;"><img src="${ctx }/images/ico_20.png" style="vertical-align:middle; margin-right:8px;"/> <span style="color:red"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.available"/></span></div>
        						</c:if>
        					
        		<c:if test="${pcrlist1!=null||pcrlist3!=null||pcrlist5!=null }">
				<%-- <td valign="middle" class="tda"><ingenta-tag:LanguageTag key='Page.Publications.Journals.Relations' sessionKey='lang' /> ：&nbsp;</td> --%>
	            <td valign="middle" class="tdb">
	<%-- 				 <c:forEach items="${pcrlist1 }" var="pc1" varStatus="index">
	      				<c:if test="${pc1.mark=='12' }"><p>${pc1.occurTime}分刊为：<c:forEach items="${pcrlist }" var="pc" varStatus="index">${pc.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc.issueCon.id}">${pc.issueCon.title }</a></c:forEach></p></c:if>
	      				<c:if test="${pc1.mark=='1' }"><p>${pc1.occurTime}合刊为：<c:forEach items="${pcrlist }" var="pc" varStatus="index">${pc.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc.issueCon.id}">${pc.issueCon.title }</a></c:forEach></p></c:if>
	      				<c:if test="${pc1.mark=='3' }"><p>${pc1.occurTime}变更为：<c:forEach items="${pcrlist }" var="pc" varStatus="index">${pc.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc.issueCon.id}">${pc.issueCon.title }</a></c:forEach></p></c:if>
	      			</c:forEach> --%>
	      			<c:forEach items="${pcrlist3 }" var="pc3" varStatus="index">
	      				<c:if test="${pc3.mark=='21' }"><p class="ml50"><img src="${ctx}/images/ico/ico21.png" class="vm mr5" />本刊自${pc3.occurTime}年起，由<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.separateCon.id}">${pc2.separateCon.title }</a></c:forEach>合刊而成</p></c:if>
	      				<%-- <c:if test="${pc3.mark=='2' }"><p>${pc3.occurTime}由：<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.separateCon.id}">${pc2.separateCon.title }</a></c:forEach>分刊而来</p></c:if> --%>
	      				<%-- <c:if test="${pc3.mark=='31' }"><p>${pc3.occurTime}由：<c:forEach items="${pcrlist2 }" var="pc2" varStatus="index">${pc2.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc2.issueCon.id}">${pc2.issueCon.title }</a></c:forEach>变更而来</p></c:if> --%>
	      			</c:forEach>
	      			<c:forEach items="${pcrlist5 }" var="pc5" varStatus="index">
	      				<c:if test="${pc5.mark=='3' }"><p class="ml50"><%-- ${pc5.occurTime} --%>并正式更名为<c:forEach items="${pcrlist4 }" var="pc4" varStatus="index">${pc4.issueCon.code }<a href="${ctx }/pages/publications/form/article/${pc4.issueCon.id}">${pc4.issueCon.title }</a></c:forEach></p></c:if>
	      			</c:forEach>
	      			<%-- <c:forEach items="${pcrlist5 }" var="pc5" varStatus="index">
	      				<c:if test="${pc5.mark=='31' }"><p>${pc5.occurTime}由：<c:forEach items="${pcrlist4 }" var="pc4" varStatus="index">${pc4.separateCon.code }<a href="${ctx }/pages/publications/form/article/${pc4.separateCon.id}">${pc4.separateCon.title }</a></c:forEach>变更而来</p></c:if>
	      			</c:forEach> --%>
				</td>
				</c:if>
				</p>			
								 <c:if test="${form.obj.publications.id !=null && form.obj.publications.id !='' }">
				                    <p class="blockP">
				                    	<span class="w100 tr">刊名：</span>
				                        <span>
				                       
				                        ${form.obj.publications.title }
				                        </span>
				                    </p>
				                 </c:if>
				               
									<p class="blockP">
				                    	<span class="w100 tr"><ingenta-tag:LanguageTag key="Pages.publications.article.Label.publisher" sessionKey="lang" />：</span>
				                        <span><a href='${ctx }/index/search?type=2&searchsType=4&searchValue="${form.obj.publisher.name}"'>${form.obj.publisher.name }</a></span>
                    				</p>
                    				<c:if test="${form.obj.createOn!=null }">
                    				<p class="blockP">
				                    	<span class="w100 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.Year"/>：</span>
				                        <span><fmt:formatDate value="${form.obj.createOn }" pattern="yyyy"/></span>
				                    </p>
				                    </c:if>
				                    <p class="blockP">
				                    	<span class="w100 tr"><ingenta-tag:LanguageTag key="Page.Frame.Count.Lable.Total" sessionKey="lang" />：</span>
				                        <span>${artCount}</span>
				                    </p>
				                    <c:if test="${form.obj.eissn!=null}">
				                    <p class="blockP">
				                    	<span class="w100 tr">E-ISSN：</span>
				                        <span>${form.obj.eissn }</span>
				                    </p>
				                    </c:if>
									<p class="blockP">
											<span class="w100 tr">ISSN：</span>
											<span>${fn:split(form.obj.code,'|')[0]}</span>
									</p>
									<p class="blockP">
				                    	<span class="w100 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.CLC"/>：</span>
										<span>
										<c:set var="csName"></c:set>
										<c:set var="names"></c:set>					
										<c:forEach items="${form.obj.csList }" var="cs" varStatus="a">
										<c:set var="csName">${cs.subject.code }  <c:if test="${sessionScope.lang=='zh_CN' }">${cs.subject.name}</c:if><c:if test="${sessionScope.lang=='en_US' }">${cs.subject.nameEn }</c:if></c:set>
										<c:set var="names">${names }${csName }</c:set>
							            <c:if test="${fn:length(form.obj.csList)!=(a.index+1) }"><c:set var="names">${names };</c:set></c:if>
										</c:forEach>					
										${names }
										</span>
				                    </p>
				                    <c:if test="${form.obj.free!=2&&form.obj.oa!=2}">
							          	<p class="blockP">
							          		<span class="w100 tr"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.price"/>：</span>
							             	<span>${form.obj.lcurr}&nbsp;<fmt:formatNumber value="${form.obj.listPrice}" pattern="0.00" /></span>

							          	</p>
							          	</c:if>
				                    <p class="mt20">
									     <c:if test="${objadd }">
											<a href="#" id="add_${form.obj.id}" class="ico ico_cart" title="<ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang' />" onclick="addToCart('${form.obj.id}',1,'1')">
								        	  	<ingenta-tag:LanguageTag key='Page.Publications.Lable.Buy' sessionKey='lang' />
								          	</a>
										</c:if>
										 <c:if test="${sessionScope.mainUser!=null && !objadd }">
												<span><a href="#" id="add_${form.obj.id}"  class="ico ico_cart2"><ingenta-tag:LanguageTag key="Page.Publications.Lable.Buy" sessionKey="lang" /></a></span>
										</c:if>
										<c:if test="${objfavourite }">
										   	<a href="#" id="favourites_${form.obj.id}" class="ico ico_collection" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />" onclick="addFavourites('${form.obj.id}')">
													<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />
											</a>
										</c:if>
										<c:if test="${sessionScope.mainUser!=null && !objfavourite }">		
											<span><a href="#" id="favourites_${form.obj.id}"  class="ico ico_collection2"><ingenta-tag:LanguageTag key="Page.Index.Search.Button.Favourite" sessionKey="lang" /></a></span>
											</a>
										</c:if>
										<c:if test="${objrecommand}">
											<a href="#" id="recommand_${form.obj.id}" class="ico ico_recommed" title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' />" onclick="popTips('${form.obj.id}')">
													<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Recommend' sessionKey='lang' />
											</a>
										</c:if>
				                    </p>
				                    </div>
									
						<div class="fr">
							<a href="${ctx}/pages/publications/form/journaldetail/${form.obj.id}"
								title="${form.obj.title }"> <c:if test="${form.obj.cover!=null&&form.obj.cover!='' }">
										<img width="137" height="170"
											src="${ctx}/pages/publications/form/cover?t=2&id=${form.obj.id}"
											class="table_img"/>
									</c:if>
							</a>
						</div>
						
			
						</div>
						<c:if test="${form.obj.remark!=null && form.obj.remark!=''&& form.obj.remark!='[无简介]' }">
						<div class="mt10">
			                <h1 class="h1Tit borBot"><ingenta-tag:LanguageTag sessionKey="lang" key="Page.Publications.Journals.Abstract" /></h1>
			                <p class="fontFam">${fn:replace(fn:replace(fn:replace(fn:replace(form.obj.remark,"&lt;","<"),"&gt;",">"),"&amp;","&"),"<ul>","<ul class='patientia'>")}</p>
			            </div>
			            </c:if>
				<!--列表内容结束-->
				<!-- 章节开始 -->
			<c:if test="${artCount >0}">
			<div class="mt10">
                <h1 class="h1Tit borBot"><ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Menu" sessionKey="lang" /></h1>
			<div class="oh">
				
				<%-- <h1 class="chapt_title">
					<span><ingenta-tag:LanguageTag sessionKey="lang"
							key="Page.Publications.Journals.NewArticel" /></span>
				</h1> --%>
				<%-- <c:if test="${sessionScope.mainUser!=null}">
					<div style="margin-top:10px;">
						<span> <input type="checkbox" class="selectAll" value=""
							id="selectAll" /> <label for="selectAll"><ingenta-tag:LanguageTag
									sessionKey="lang" key="Global.Lable.Select.All" /></label>
						</span> <a class="a_gret" id="submit" onclick="batchSub()"><ingenta-tag:LanguageTag
								sessionKey="lang" key="Pages.Publication.Button.bulk.Favorite" /></a>
					</div>
				</c:if> --%>
				<div class="jourDiv">
					<ul id="menu" class="menu">
						<c:forEach items="${ylist}" var="v" varStatus="index">
							<li><a id="year_${v.year}" href="javascript:;">${v.year}</a></li>
						</c:forEach>
					</ul>
				</div>
				</c:if>
				<div class="jourCont">
				<c:forEach items="${list}" var="p" varStatus="index">
					<c:set var="license">${(p.subscribedIp!=null||p.subscribedUser!=null)&&(p.subscribedIp>0||p.subscribedUser>0) }</c:set>
					<c:set var="news">${p.latest!=null&&p.latest>0 }</c:set>
					<c:set var="oa">${p.oa!=null&&p.oa==2 }</c:set>
					<c:set var="free">${p.free!=null&&p.free==2 }</c:set>
					<c:set var="collection">${p.inCollection!=null&&p.inCollection>0 }</c:set>
					<c:set var="add1"
						value="${p.priceList!=null&&fn:length(p.priceList)>0&&p.free!=2&&p.oa!=2&&sessionScope.mainUser!=null && p.subscribedUser<=0&&(p.buyInDetail<=0&&p.exLicense>=0)}" />
					<c:if test="${add1==false }">
						<c:set var="add" value="false" />
					</c:if>
					<c:if test="${add1==true &&p.subscribedIp>0 }">
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id&&sessionScope.mainUser.level==2 }">
							<c:set var="add" value="false" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id==sessionScope.institution.id &&sessionScope.mainUser.level!=2 }">
							<c:set var="add" value="true" />
						</c:if>
						<c:if
							test="${sessionScope.mainUser.institution.id!=sessionScope.institution.id}">
							<c:set var="add" value="true" />
						</c:if>
					</c:if>
					<c:if
						test="${add1==true &&(p.subscribedIp==null||p.subscribedIp<=0) }">
						<c:set var="add" value="true" />
					</c:if>
					<c:if test="${add1==false }">
						<c:set var="add" value="false" />
					</c:if>
					<c:set var="favourite"
						value="${sessionScope.mainUser!=null&&(p.favorite==null||p.favorite<=0)}" />
					<c:set var="recommand"
						value="${p.recommand>0 &&(p.subscribedIp==null||p.subscribedIp<=0)&&(p.free!=2&&p.oa!=2)}" />
					 <c:set var="license1" value="${form.obj.subscribedIp>0||form.obj.subscribedUser>0||form.obj.free==2||form.obj.oa==2 }"/>
					<div class="block">
						
							<div class="fl w22 mt2"><img src="${ctx }/images/ico/ico5.png" width="13" height="13" /></div>
							<div class="fl w660">
							<p><a class="a_title" href="${ctx}/pages/publications/form/article/${p.id}">${fn:replace(fn:replace(fn:replace(p.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}</a></p>
						<p ><c:if test="${p.author !=null}">By <c:if test="${p.type!=2 }">${p.author }</c:if>
							<c:if test="${p.type==4 }"> in <a
										href="${ctx}/pages/publications/form/article/${p.publications.id}">${p.publications.title}</a>
								</c:if>(${fn:substring(p.pubDate,0,4) })</c:if>
							<c:if test="${p.type==2 }">Volume ${p.startVolume }-Volume ${p.endVolume }</c:if>
						</p>
						<a href="${ctx}/pages/publications/form/journaldetail/${form.obj.id}">
		            	${fn:replace(fn:replace(fn:replace(form.obj.title,"&lt;","<"),"&gt;",">"),"&amp;","&")}
		            	</a>,
						<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Volume" sessionKey="lang" />
		                ${p.volumeCode },
						<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.Issue" sessionKey="lang" />
						${p.issueCode }, 
						${p.year }-${p.month} ,
						<ingenta-tag:LanguageTag key="Pages.Publications.Journal.Lable.PageRange" sessionKey="lang" />
						<span class="chapt_author_p3">
							${p.startPage }-${p.endPage}<span></span>
						</span>
						<p>
				            <span><a href='${ctx }/index/search?type=2&searchsType=4&searchValue="${p.publisher.name}"'>${p.publisher.name }</a></span>
                    	</p>
                    	<p class="mt5">
                    		<input type="hidden" id="pubid" name="pubid" value="${p.id }" />
                    		<span class="mr20"><img src="${ctx }/images/ico/ico-det.png" width="16" height="16" class="vm mr5 mt1"/><a href="${ctx}/pages/publications/form/article/${p.id}" ><ingenta-tag:LanguageTag key="Pages.kournal.article.Abstract" sessionKey="lang"></ingenta-tag:LanguageTag></a></span>
                           
                            <span class="mr20">
                           <c:if test="${license1==false }">
							<span>
								<a href="#" class="blue" onclick="popTips2('${p.id}');"><img src="${ctx}/images/ico/ico15-blue.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
							</a>
							</span>
						</c:if>
						<c:if test="${license1==true}">
						<span>
							<%-- <a href="#" class="green"><img src="${ctx}/images/ico/ico15-green.png" class="vm" /> --%>
							<a href="#" class="green" onclick="popTips2('${p.id}');"><img src="${ctx}/images/ico/ico15-green.png" class="vm" />
							<ingenta-tag:LanguageTag key="Page.Publications.DetailList.Resource" sessionKey="lang" />
							</a>
							</span>
							</c:if>
							</span>
                            
                            
                            <c:if test="${!objrecommand }">
                            <span class="mr20"><a id="recommand_${p.id }" href="#" onclick="popTips('${p.id}')"><img src="${ctx}/images/ico/ico16-blue.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Index.Search.Link.Recommend" sessionKey="lang" />
							</a></span>
							</c:if>
							<c:if test="${objrecommand }">
                            <span class="mr20"><a id="recommand_${p.id }" href="#" onclick="popTips('${p.id}')"><img src="${ctx}/images/ico/ico16-blank.png" class="vm" />
								<ingenta-tag:LanguageTag key="Page.Index.Search.Link.Recommend" sessionKey="lang" />
							</a></span>
							</c:if>
                            <span>
	                            <%-- <c:if test="${sessionScope.mainUser!=null && !favourite }">
		                            <a href="#" id="favourites_${p.id}"  onclick="addFavourites('${p.id}');"><img src="${ctx}/images/ico/ico13-blue.png" class="vm" />
									<ingenta-tag:LanguageTag key="Page.Index.Search.Button.Favourite" sessionKey="lang" />	
									</a>
								</c:if> --%>     <!-- class="ico_link" -->
								<c:if test="${favourite }">
									<a href="#" id="favourites_${p.id }" class="ico_link"
										title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />"
										onclick="addFavourites('${p.id }')"> <img class="vm" id="favourites_img_${p.id}"
										src="<c:if test="${ctx!=''}">${ctx}</c:if><c:if test="${ctx==''}">${domain}</c:if>/images/ico/ico13-blue.png" />
										<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />
									</a>
								</c:if>
								
								<c:if test="${sessionScope.mainUser!=null && !favourite }">
									<a id="favourites_${p.id }" class="blank"
										title="<ingenta-tag:LanguageTag key='Page.Index.Search.Link.collected' sessionKey='lang' />">
										<img class="vm"
										src="<c:if test="${ctx!=''}">${ctx}</c:if><c:if test="${ctx==''}">${domain}</c:if>/images/ico/ico13-blank.png" />
										<ingenta-tag:LanguageTag key='Page.Index.Search.Link.Favorite' sessionKey='lang' />
									</a>
								</c:if>
							</span>
                        </p>
                        </div>
                        </div>
                      
                        <%-- 
						<p class="intro clearcss">
							${fn:replace(fn:replace(fn:replace(p.remark,"&lt;","<"),"&gt;",">"),"&amp;","&")}
						</p>
						 --%>
					
				</c:forEach>
				<jsp:include page="../pageTag/pageTag.jsp">
		        	<jsp:param value="${form }" name="form"/>
                </jsp:include>
					</div>
				</div>
				<%-- <div class="relation">
				<h1 class="chapt_title">
					<span><ingenta-tag:LanguageTag sessionKey="lang"
							key="Page.Publications.Journals.Relation" /></span>
				</h1>
				<div id="canvas"></div>
				<table width="95%" border="0" cellpadding="0" cellspacing="1"
					class="devil_table">
					<thead>
						<tr>
							<th width="5%" align="center"><ingenta-tag:LanguageTag
									sessionKey="lang" key="Pages.Content.Table.Lable.ISSN" /></th>
							<th width="20%" align="center"><ingenta-tag:LanguageTag
									sessionKey="lang" key="Pages.Content.Table.Lable.Title" /></th>
							<th width="5%" align="center"><ingenta-tag:LanguageTag
									sessionKey="lang" key="Pages.Content.Table.Lable.Type" /></th>
							<th width="12%" align="center"><ingenta-tag:LanguageTag
									sessionKey="lang" key="Pages.Content.Table.Lable.Publisher" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${plist}" var="p">
							<tr>
								<td align="center">${p.issn}</td>
								<td align="center">${p.title}</td>
								<td align="center"><c:if test="${p.type==1}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.EBook" />
									</c:if> <c:if test="${p.type==2}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Periodical" />
									</c:if> <c:if test="${p.type==3}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Chapter" />
									</c:if> <c:if test="${p.type==4}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Article" />
									</c:if> <c:if test="${p.type==5}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Database" />
									</c:if> <c:if test="${p.type==6}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Volume" />
									</c:if> <c:if test="${p.type==7}">
										<ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.content.type.Issue" />
									</c:if></td>
								<td align="center">${p.publisher.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div> --%>
			</div>
			
		</div>
	</div>
	</div>
	</div>
	
	
	<!--以下 提交查询Form 开始-->
	<form:form action="${form.url}" method="post" modelAttribute="form"
		commandName="form" name="formList" id="formList">
		<form:hidden path="searchsType" id="type1" />
		<form:hidden path="searchValue" id="searchValue1" />
		<form:hidden path="pubType" id="pubType1" />
		<form:hidden path="language" id="language1" />
		<form:hidden path="publisher" id="publisher1" />
		<form:hidden path="pubDate" id="pubDate1" />
		<form:hidden path="taxonomy" id="taxonomy1" />
		<form:hidden path="taxonomyEn" id="taxonomyEn1" />
		<form:hidden path="searchOrder" id="order1" />
		<form:hidden path="lcense" id="lcense1" />

		<form:hidden path="code" id="code1" />
		<form:hidden path="pCode" id="pCode1" />
		<form:hidden path="publisherId" id="publisherId1" />
		<form:hidden path="subParentId" id="subParentId1" />
		<form:hidden path="parentTaxonomy" id="parentTaxonomy1" />
		<form:hidden path="parentTaxonomyEn" id="parentTaxonomyEn1" />
	</form:form>
	<!--以上 提交查询Form 结束-->
	<!--定义01 mainContainer 内容区结束-->
	<!-- 底部的版权信息 -->
	<c:if test="${sessionScope.lang == 'zh_CN'}"><div id="footer_zh_CN"></div></c:if>
	<c:if test="${sessionScope.lang == 'en_US'}"><div id="footer_en_US"></div></c:if>
	</div>
</body>
</html>
