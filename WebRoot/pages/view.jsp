<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv='X-UA-Compatible' content='IE=edge' />
<meta name="viewport"
	content="initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width" />
<%@ include file="/common/tools.jsp"%>
<%@ include file="/common/ico.jsp"%>
<title>CNPIEC eReading: Introducing CNPIEC eReading</title>

<link href="${ctx}/css/reset.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/index.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/flexpaper/css/flexpaper.css" rel="stylesheet" type="text/css" />
<%-- jquery.mmenu START --%>	
<link href="${ctx }/js/mmenu_plugin/dist/css/demo.css" rel="stylesheet" type="text/css" />
<link href="${ctx }/js/mmenu_plugin/dist/css/jquery.mmenu.all.css"	 rel="stylesheet" type="text/css" />
<link href="${ctx }/js/mmenu_plugin/dist/css/extensions/jquery.mmenu.positioning.css" type="text/css" rel="stylesheet" />
 <%-- jquery.mmenu END   --%>	
	
<%-- 与上common冲突 --%>
<%-- <script type="text/javascript"
	src="${ctx}/flexpaper/flex_js/jquery.min.js"></script> --%>
	<script type="text/javascript" src="${ctx }/js/jquery-2.1.4.js"></script>
<%-- <script type="text/javascript" src="${ctx}/flexpaper/js/common.js"></script> --%>

<%-- jquery.mmenu --%>
<script type="text/javascript" src="${ctx }/js/mmenu_plugin/dist/js/jquery.mmenu.min.all.js"></script>

<script type="text/javascript"
	src="${ctx}/flexpaper/flex_js/jquery.extensions.min.js"></script>
<script type="text/javascript"
	src="${ctx}/flexpaper/flex_js/flexpaper.js"></script>
<script type="text/javascript"
	src="${ctx}/flexpaper/flex_js/flexpaper_handlers.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery-heartbeat.js"></script>
<%-- <script type="text/javascript" src="${ctx}/flexpaper/flex_js/FlexPaperViewer.js"></script> --%>
<script type="text/javascript" src="${ctx}/js/jquery.cookie.js"></script>
<script src="${ctx}/js/jquery.mobile-1.3.2.min.js"></script>
<%-- 导航栏不随页面滚动plugin --%>
<script type="text/javascript" src="${ctx}/js/posfixed.js"></script> 

<script type="text/javascript">

	$(function(){		
		$("#prePage").click(function(e){
			var curNumber=Number($("#curNumber").text());
			if(curNumber>1)
				aaaa(curNumber-1);
			else
				alert("没有了");
		});
		$("#nextPage").click(function(e){
			var curNumber=Number($("#curNumber").text());
			var allNumber=Number($("#allNumber").text());
			if(curNumber<allNumber)
				aaaa(curNumber+1);
			else
				alert("没有了");
		});
	});
	</script>
<script type="text/javascript">
	//<![data[
	var noteLength_yes = "<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.yes'/>";
	var noteLength_number = "<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.number'/>";
	var noteLength_no = "<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.no'/>";
	var delBut = "<ingenta-tag:LanguageTag sessionKey='lang' key='Global.Button.Delete'/>";
	var ctx = "<c:if test="${ctx!=''}">${ctx}</c:if><c:if test="${ctx==''}">${domain}</c:if>";
	var chapterText = "<ingenta-tag:LanguageTag sessionKey='lang' key='Page.view.Lable.Chapter'/>"; 
	$(function() { 
		//获取快捷工具
//		getQuickTools();
	 	//获取章节列表
		getTocList();
		$("#content").bind("input propertychange", function() { 
		strLenCalc($(this), 'checklen', 2000);
		}); 
		initMenu();
		//默认隐藏工具栏
		$('#switchBut').attr({'src':'${ctx}/images/black_ico/iconfont-show.png','title':'显示快捷工具'});
		$('#toolsBar').hide("slow");
	

		/*
		var onmousewheel = (function () {
            if (window.addEventListener) { 
				return function (el, sType, fn, capture) { 
					el.addEventListener(sType, fn, (capture)); 
				}; 
			} else if (window.attachEvent) { 
				return function (el, sType, fn, capture) { 
					el.attachEvent("on" + sType, fn); 
				}; 
			} else { 
				return function () { }; 
			} 
        })()
		, mousewheel = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel"; 
        function flashMousewhee() { 
            var o = getDocViewer() ; 
            if (!o) 
                return false; 
            onmousewheel(o, mousewheel, function (e) { 
                e =  e || window.event; 
                stopDefault(e); 
                var detail = (!!e.detail ? e.detail / -6 : e.wheelDelta / 120); 
				var d= {delta: detail};
                o.jsWheelZoom(d); 
            }, false); 
        }; 
        
        
        */
        /*function stopDefault(e) {

            if (e && e.preventDefault) {
                e.preventDefault();
            }
            else {
                window.event.returnValue = false;
            }
            return false;
        }; flashMousewhee(); 
        */
	
        
	});

	//获取章节列表
	 function getTocList(){
		var parObj=$("#chapter_label");
		$.ajax({
			type : "POST",
			async : false,    
	        url: "${ctx}/pages/view/form/tocList",
	        data : {
	        	id : '${form.id}',
	        	publicationsTitle : "${publicationsTitle}",
	        },
	        success : function(data) { //alert(data);
	        	if($.trim(data)!=''){//alert('a');
	            	$(parObj).html(data);
	            	$(parObj).css("text-align","left");
	            }else{//alert('b');
	            	//$("#chapter").hide();
	            }
	           }, 
	           error : function(data) {
	             	$(parObj).html(data);
	           }  
	     });
	} 
	
  function initMenu() {
  $('#menu ul').hide();
  $('#menu ul:first').show();
  $('#menu li a').click(
    function() {
      var checkElement = $(this).siblings().next();
      if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
        return false;
        }
      if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
        $('#menu ul').slideUp('normal');
        checkElement.slideDown('normal');
        return false;
        }
      }
    );
  }  
//检查笔记长度
function strLenCalc(obj, checklen, maxlen) { 
	var v = obj.val(), charlen = 0, maxlen = !maxlen ? 1000 : maxlen, curlen = maxlen, len = v.length;
	for(var i = 0; i < v.length; i++) {
		if(v.charCodeAt(i) < 0 || v.charCodeAt(i) > 255) { 
			curlen -= 1; 
		} 
	}
	if(curlen >= len) { 
		$("#checks").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.yes'/> <strong>"+Math.floor((curlen-len)/2)+"</strong> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.number'/>").css('color', ''); 
	} else { 
		$("#checks").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.no'/> <strong>"+Math.ceil((len-curlen)/2)+"</strong> <ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength.number'/>").css('color', '#FF0000'); 
	} 
}
//删除笔记
function deleteNote(id){
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/view/form/deleteNote",
        data: {
        	id:id,
			r_ : new Date().getTime()
         },
         success : function(data) {
           	var s = eval(data);
           	alert(s[0].info);
           	if(s[0].msg=='success'){
           		//删除
           		$("#nodes_"+id).css("display","none");
           		$("#isNote").val('');           	
           	}  
          },  
          error : function(data) {  
    			alert(data,1,'error');
    	  }  
	});
}
//添加笔记
function addNotes(){
	var content = $.trim($('#content').val());
	//alert($("#checks").css("color")=='rgb(255, 0, 0)');
	if(content==''){
		alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteNull'/>");
	}else if($("#checks").css("color")=='rgb(255, 0, 0)'){
		alert("<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Prompt.NoteLength'/>");
	}else{
		$.ajax({
				type : "POST",  
	            url: "${ctx}/pages/view/form/addNote",
	            data: {
	            	pageNum:$FlexPaper('documentViewer').getCurrPage(),
	            	sourceId:'${form.id}',
	            	noteContent:$('#content').val(),
	            	id:$('#isNote').val(),
					r_ : new Date().getTime()
	            },
	            success : function(data) {  
	              var s = eval(data);
	              if(s[0].success!=null&&s[0].success!=undefined){
		              alert(s[0].success);
		              $("#isNote").val(s[0].noteId);  
		              
		              var curNum = $FlexPaper('documentViewer').getCurrPage();
				      //dispaly:inline-block
				   	  var newp='<p id="nodes_'+ s[0].noteId +'"><span>'
				   	  +'<a style=\"margin: 0px;padding: 0px; border-bottom: none;background:none;\" onclick=\"$FlexPaper(\'documentViewer\').gotoPage(\''+ curNum +'\')\" >'+curNum+'__'+content+'</a></span><span>'
			              +'<a onclick=\"deleteNote(\''+ s[0].noteId +'\')" title=\"'+delBut+'\"><img style=\"margin-top: 16px; " src=\"${ctx}/images/cao.gif\"/></a></span></p>'; 
				   			var pArr=$("#noteList").find("p");
				   			if(pArr!=null && pArr.length>0){	   		
					   		for(var i=0;i<pArr.length;i++){
					   			var text=$(pArr[i]).text();
					   			var tArr= text.trim().split('__');
					   			if(curNum<tArr[0]){
					   				$(pArr[i]).before(newp);
					   				flag=1;
					   				break;
					   			}else if(curNum==tArr[0]){
					   				$(pArr[i]).html(newp);
					   				flag=1;
					   				break;
					   			}else if(i==pArr.length-1){
									$(pArr[i]).after(newp);	   			
					   			}	   			
					   		}	   		
					   	}else{
					   		$("#noteList").append(newp);
					   	}					
		          }else{
			          	if(s[0].error!=undefined){
			          		alert(s[0].error);
			          	}
		          }
            },  
            error : function(data) {  
              var s = eval(data);
              alert(s[0].error);
            }  
		});
	}
}
//添加标签
function addLabels(){
	var currPage = $("#curr").val();
	var currPage2=$FlexPaper('documentViewer').getCurrPage();
	if( currPage2!=0){
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/view/form/addLable",
        data: {
        	sourceId:$("#pubId11").val(),
        	pageNum:$FlexPaper('documentViewer').getCurrPage(),
			r_ : new Date().getTime()
        },
        success : function(data) {  
        	var s = data.split(":");
        	alert(s[1]);  
        	if(s[0]=="success"){
        		var curNum = $FlexPaper('documentViewer').getCurrPage();
        		$("#label1").attr("onclick","javascript:$FlexPaper('documentViewer').gotoPage("+curNum+")");
        		$("#label1").html("<ingenta-tag:LanguageTag sessionKey="lang" key='Pages.view.lable.lable1'/>："+curNum);
        	}
        },  
        error : function(data) {  
          alert(data);
        }  
	});
	}
}
//搜索
function aaaa(page){
	
	if($("#searchView").val()!=''){
		var pcont=15;
		page = page || 1;
		$.ajax({
 				type : "POST",  
            url: "${ctx}/pages/view/form/search",
            data: {
            	id:'${form.id}',
            	value:$("#searchView").val(),
            	curpage:page,
            	pageCount:pcont,
				r_ : new Date().getTime()
            },
            success : function(data) {
	            var s = eval(data);
	            var resultHtml ="<ingenta-tag:LanguageTag sessionKey='lang' key='Pages.view.Label.Result'/>：";
	            if(s[0].count==undefined){
	            	resultHtml += "0";
	            }else{
	            	resultHtml += s[0].count;
	            }
	            //搜索到的结果数量
	           	var allResultCount = s[0].count?s[0].count:0 ; 
	           	//总分页数
	           	var pageCount = s[0].count?Math.ceil(s[0].count / pcont):0 ; 

	         	var trim_search_val = $.trim($("#searchView").val());
	           	/** hr.yuan 屏蔽翻页功能   */
	            /** 
	            $("#searchText").html(resultHtml);
	            $("#curNumber").text(s[0].count?page:0);
	            $("#allNumber").text(s[0].count?Math.ceil(s[0].count / pcont):0); 	            
	            $("#search_result").show(); */
	            $('#search_list').show();
	            var ss = s[0].result;
		        page==1 && $("#search_list").html("<b style=\"font-size:10px;\">搜索到&nbsp;"+allResultCount+"&nbsp;记录</b><br>");
		        ss.length>0 && highlightSearchVal(1,trim_search_val);
		        //搜索到了内容
	            if(ss.length>0){
		            var listHtml = "";
		            $.each(ss,function(i,item ){
		            	listHtml += "<a style=\"font-size:13px;line-height:normal;height:auto;margin-bottom:5px;cursor:pointer\" onclick=\"highlightSearchVal("+ss[i].pageNumber+",'"+trim_search_val+"');\" >第<b style='color:#F38120'>"+ss[i].pageNumber+".</b>页&nbsp;</a><p style=\"font-size:12px\">"+ss[i].hlMap[0] + "</p>";
					}); 
					$("#search_list").append(listHtml); 
	            	$('#search_list').height("200px");
					
	            	if(page < pageCount){
	            		aaaa(page+1);
	            	}
	            	
	            }else{
	            	$('#search_list').height("25px");
	            }
		    },  
		    error : function(data) {  
		    }  
		});
	}
}

	//跳转页面、搜索内容高亮显示
	function highlightSearchVal(pageNum,searchVal){
		var evt = this.event || window.event ; 
		jumpToSearchPage(pageNum,searchVal,evt);
		setTimeout(function(event){
			jumpToSearchPage(pageNum,searchVal,evt);
			if($('.flexpaper_selected').length < 1 ){
				$FlexPaper('documentViewer').gotoPage(pageNum);
			}
				$('input.flexpaper_txtPageNumber').val(pageNum);
		},800);
	}


/* function getDocumentViewer(){
	var f = $FlexPaper('documentViewer') ;
	return f; 
} */

//拷贝
function copy(){
	//var pagenum = $FlexPaper('documentViewer').getCurrPage();
	/* $('.flexpaper_bttnTextSelect').removeAttr('style'); */
	/* $('.flexpaper_bttnTextSelect').css('display','block');
	$('.flexpaper_bttnTextSelect').show(); */
	//$('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_pressed');
	 $.ajax({
		type : "POST",  
        url: "${ctx}/pages/view/form/tocCopy",
        data: {
        	pubId:$("#pubId11").val(),
           	pageNum:$FlexPaper('documentViewer').getCurrPage(),
           	licenseId:$("#licenseId11").val(),
           	count:$("#pageCount11").val(),
           	readCount:$("#readCount11").val(),
        	isCopy:true,
        	readCount:'${form.readCount}',
			r_ : new Date().getTime()
        },
        success : function(data) {  
          var s = eval(data);
          if(s[0].isCopy=='true'){          	
          		//隐藏拷贝按钮
        	  $("#btn_copy").css("display","none");          	
          		//$FlexPaper('documentViewer').switchSelect(true);
          		//开启-关闭页面选中按钮
        	  $('.flexpaper_bttnTextSelect').removeAttr('style');
        	  $('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_pressed');
        	  $('.flexpaper_bttnTextSelect').click();          		
          } else{
          	alert(s[0].error);
          } 
          static_count = s[0].copyCount ; 
       	  $("#copyCount").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Content.View.now.copy.page'/>："+s[0].copyCount);
       		//currPage_num = $FlexPaper('documentViewer').getCurrPage() ; 
        },  
        error : function(data) {  
        	var s = eval(data);
            alert(s[0].error);
        }  
	}); 
}

//Token judge
function getToken(){
	/* var path = "${ctx}" + "/"; 
	var token = $.cookie("token",{"path" : path}); */
	//var path = "'${ctx}"+"/'" ; 
	/* var pathVal = "${ctx}/" ; 
	var token = $.cookie("token",{path:pathVal});

	if (token) {
		$.cookie("token",null,{path:pathVal});
		//$.cookie("token", token.substr(1));
		$.cookie("token",token.substr(1),{path:pathVal});

		return true ; 
	}
	return false ;  */
	var pathVal = "/" ; 
	var token = $.cookie("token");
	if (token) {
		$.cookie("token",null,{path:pathVal});
		$.cookie("token",token.substr(1),{path:pathVal});
		return true ; 
	}
	return false ; 
	

}

//打印
function ajaxPrint(){

	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/view/form/ajaxPrint",
        data: {
        	pubId:$("#pubId11").val(),
           	pageNum:$("#printStr").val(),
           	licenseId:$("#licenseId11").val(),
           	count:$("#pageCount11").val(),
			r_ : new Date().getTime()
        },
        success : function(data) {  
          var s = eval(data);
          if(s[0].isPrint=='true'){ 
        	  var print_page_num = s[0].printTask;
        	/*   console.log($FlexPaper('documentViewer')); */
        	  $FlexPaper('documentViewer').printPaper(print_page_num + ",");
        	 /*  $('div#modal-print').hide();
        	  $('input#toolbar_documentViewer_bttnPrintDialog_RangeSpecific').click();
        	  $('input#toolbar_documentViewer_bttnPrintDialogRangeText').val(print_page_num + ",");
        	  $('a#toolbar_documentViewer_bttnPrintdialogPrint').click(); */
          } else{
          	alert(s[0].error);
          } 
       	  $("#printCount").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Content.View.now.print.page'/>："+s[0].printCount);
        },  
        error : function(data) {  
          var s = eval(data);
          alert(s[0].error);
        }  
	});		
}
function closeReadWindow(){
	var tmp=window.close("about:blank","","fullscreen=1") ;
	//var tmp = window.open('','_self','');
	tmp.close();
    
	$.ajax({
		type : "POST",  
        url: "${ctx}/pages/complication/close",
        data: {
			r_ : new Date().getTime()
        },
        success : function(data) {
        	//history.back();//location.href="${ctx}"==""?"/":"${ctx}";
        	tmp.location=url; 	
        },  
        error : function(data) { 
        	//history.back();//location.href="${ctx}"==""?"/":"${ctx}";
        	tmp.location=url;
        }  
	});	
}
var waitTimes=0;
var tryTimes=0;
function waitReady(){
	var p=getUrlParam("nextPage");
	if(p && p>1){
		if(waitTimes<10){
			try{
				$FlexPaper('documentViewer').gotoPage(p);
				if(tryTimes<3){
					setTimeout(waitReady,500);
					tryTimes++;
				}
			}catch(e){
				waitTimes++;
				setTimeout(waitReady,500);
			}
		}
	}
}


function getUrlParam(name){
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r!=null) 
		return unescape(r[2]); 
	return null; //返回参数值
} 
//]]-->	 
</script>
<c:if
	test="${beatInterval!=null && beatInterval!='' && beatInterval!='0'}">
	<script type="text/javascript">
$(document).ready(function(){
	waitReady();
	$.jheartbeat.set({
		 url: "${ctx}/pages/view/form/beat?l=${form.licenseId }", // The URL that jHeartbeat will retrieve
		 delay: ${fn:trim(beatInterval)}000, // How often jHeartbeat should retrieve the URL
		 div_id: "test_div"}, // Where the data will be appended.
		 function (){
		  
		 });
	/* var dt=$("#documentViewer").offset().top; */
	/*  $(window).scroll(function(e){
		var s=$(document).scrollTop();
		if(s>dt){
			$("#documentViewer").css("position","fixed");
		}else{
			$("#documentViewer").css("position","");
		}
	});  */
});


function addDisplay(){
	
	setTimeout("hiddenSelect_but()",4500);
}

//隐藏/显示工具栏
function hideShowTools(){
	if($('#toolsBar').is(':hidden')){
		$('#switchBut').attr({'title':'隐藏快捷工具'});
		$('#toolsBar').show("slow");
	}else{
		$('#switchBut').attr({'title':'显示快捷工具'});
		$('#toolsBar').hide("slow");
	}
	
	
	
}
function hiddenSelect_but(){
	$("#toolbar_documentViewer_barSearchTools:last").hide();
	$('#header').append("<img id='switchBut' title='显示快捷工具' src='${ctx}/images/black_ico/tool_ico.png' style='width: 25px; height: 25px;float:right;margin: 7px 10px 0 0; position: absolute; right:5px;' onclick='javascript:hideShowTools();'/>");
	// ## 屏蔽右键菜单功能
	$("*.flexCont").bind("contextmenu",function(e){
			return false;
	});
}

  $(function() {
	   
	   $('nav#menu').mmenu({
		   extensions	: ['pageshadow'],
		   	 navbar 	 : {
			 title   : chapterText
			},
		   	 /* , 默认在左侧显示，打开之后显示在右侧*/
			offCanvas: {
          	  zposition : "front"
	       	} 
	   }); 
		// 点击之后关闭章节列表
		var API = $('nav#menu').data( "mmenu" );
		$("#chapter_label ").click(function(){
				API.close();	
		});
  }); 
	
</script>
</c:if>
<style type="text/css">
.search_word ul {
	background: #f8f8f8;
	border-left: 1px solid #bcbcbc;
	border-right: 1px solid #bcbcbc;
	border-bottom: 1px solid #bcbcbc;
	width: 86px;
	display: none;
	position: absolute;
	z-index: 5;
	margin-left: 380px;
	margin-top: 55px;
	margin-left: 380px\9; >
	margin-left /*IE5.5*/: -105px; >
	margin-top /*IE5.5*/: 53px;
}

*+html .search_word ul {
	margin-left: -103px;
	margin-top: 53px;
}

@media screen and (-webkit-min-device-pixel-ratio:0) {
	.search_word ul {
		margin-left: 380px;
	}
}

.search_help ul {
	margin-top: -7px;
	margin-left: 484px;
	margin-left: 484x\9;
	_margin-left: -93px;
	_margin-top: 56px;
}

*+html .search_help ul {
	margin-left: -92px;
	margin-top: 55px;
}

@media screen and (-webkit-min-device-pixel-ratio:0) {
	.search_help ul {
		margin-left: 484px;
	}
}

a {
	cursor: pointer;
}

.readlist {
	background: #fff none repeat scroll 0 0;
    border: 1px solid #ccc;
    border-radius: 8px;
    display: block;
    margin-left: 39%;
    margin-top: 40px;
    padding: 0 10px 10px;
    position: absolute;
    width: 53%;
    z-index: 1001;
}
div.readlist #menu li{
	background: none;
	background-color: white;
}
.write{
	color: #258FD4;
}





</style>
</head>
 <body style="overflow: hidden" onload="addDisplay();">		
 <c:if test="${webUrl==null||webUrl=='' }">
		<div>
			<!--  目录内容  START-->
			<div class="header" id="header">
				<a href="#menu" id="chapter"></a>
				
					<c:choose>
						<c:when test="${22 < fn:length(fn:replace(fn:replace(fn:replace(form.publicationsTitle, '&lt;', '<'), '&gt;', '>'), '&amp;', '&'))}">${fn:substring('PublicStaticLearnStudyWhaereSJAJadncgiqowqn', 0, 21)}...</c:when>
						<c:otherwise>${fn:replace(fn:replace(fn:replace(form.publicationsTitle,"&lt;","<"),"&gt;",">"),"&amp;","&")}</c:otherwise>
					</c:choose>
				<!-- ${fn:replace(fn:replace(fn:replace(form.publicationsTitle,"&lt;","<"),"&gt;",">"),"&amp;","&")}  -->
			</div>
			<nav id="menu" style="text-align: center; filter:alpha(Opacity=80);-moz-opacity:0.8;opacity: 0.8;">
				<ul class="hierarchy" id="chapter_label"
					style="text-align: center;">
					<img src="${ctx}/images/loading.gif" />
				</ul>				
			</nav>
		</div> 
		<!--  目录内容  END -->				
			
		<!--右侧内容开始 <右侧快捷工具栏> -->
		<div class="readlist" id="toolsBar" style="width: 53%; margin-left: 39%;  margin-top: 80px; z-index: 1001; position: absolute; display:block;">
					<c:if test="${form.id=='402880f1491210950149127d95020001' }">
						<h1>视频列表</h1>
						<ul class="hierarchy" style="text-align: left;"		>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-1.mp4')">视频1： 重建手术1</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-2.mp4')">视频2：软骨移植操作</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-3.mp4')">视频3：重建手术2</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-4.mp4')">视频4：膝盖骨加固操作</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-5.mp4')">视频5：自体软骨细胞</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-6.mp4')">视频6：肌腱切割操作</a></li>
						</ul>
					</c:if>
					<h1>
						<ingenta-tag:LanguageTag sessionKey='lang'
							key='Page.view.Lable.tools' />
					</h1>
					<ul id="menu">
						<li><a><span class="alph"><img
									src="${ctx }/images/blue_ico/ico_01.png"
									style="margin-top:13px; margin-left:-18px;" /></span> <span
								class="write"><ingenta-tag:LanguageTag sessionKey="lang"
										key="Pages.view.Label.Search" /></span></a>
							<ul class="tag">
								<li>
									<div>
										<p>
											<input class="a_input" type="text" id="searchView" /> <a
												style="cursor: pointer;" class="a_cxu" onclick="aaaa();">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Global.Button.Search" />
											</a>
										</p>
										<div></div>
										<br />
										<p id="search_result" class="book_b"
											style="padding-top: 10px;padding-bottom: 10px;display:none">
											<span id="searchText"></span><br /> <img id="prePage"
												style="cursor:pointer;float:none"
												src="${ctx }/images/pre.png" /> <span id="curNumber">0</span>
											/ <span id="allNumber"></span>&nbsp;&nbsp;&nbsp; <img
												id="nextPage" style="cursor:pointer;float:none"
												src="${ctx }/images/next.png" />

										</p>
										<div style="display:none;overflow-y:auto;height: 200px; border-color: red; border-width: 2px;" id="search_list">
								
										</div>
									</div>
								</li>
							</ul></li>
						<c:if test="${sessionScope.mainUser!=null }">
							<li><a><span class="alph"><img
										src="${ctx }/images/blue_ico/ico_04.png"
										style="margin-top:13px; margin-left:-18px;" /></span> <span
									class="write"><ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.view.Label.Record" /></span></a>
								<ul class="tag">
									<li>
										<div>
											<p class="book_b">
												<c:if
													test="${form.record!=null&&form.record.pages.number>0 }">
													<a id="label1"
														onclick="javascript:$FlexPaper('documentViewer').gotoPage('${form.record.pages.number}');"
														style="margin: 0px;padding: 0px;border-bottom: none;background: none;">
														<ingenta-tag:LanguageTag sessionKey="lang"
															key="Pages.view.lable.lable1" />：${form.record.pages.number }
													</a>
												</c:if>
												<c:if
													test="${form.record==null||form.record.pages.number==null || form.record.pages.number<=0 }">
													<a id="label1"
														style="margin: 0px;padding: 0px;border-bottom: none;background: none;">
														<ingenta-tag:LanguageTag sessionKey="lang"
															key="Pages.view.lable.nolable" />
													</a>
												</c:if>
											</p>
											<p class="book_a">
												<a onclick="addLabels();" class="a_gret"><ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.lable.button" /></a>
											</p>
										</div>
									</li>
								</ul></li>
							<li><a><span class="alph"> <input type="hidden"
										id="isNote" value="" /> <img src="${ctx }/images/blue_ico/ico_02.png"
										style="margin-top:13px; margin-left:-18px;" />
								</span> <span class="write"><ingenta-tag:LanguageTag
											sessionKey="lang" key="Pages.view.Label.Notes" /></span></a>
								<ul class="tag newTag">
									<li>
										<div>
											<div id="noteList">
												<c:forEach items="${form.noteList }" var="nlist"
													varStatus="index">
													<p id="nodes_${nlist.id }">
														<span> <a
															style="margin: 0px;padding: 0px; border-bottom: none;background:none;"
															onclick="javascript:$FlexPaper('documentViewer').gotoPage(${nlist.pages.number})">
																${nlist.pages.number }__${nlist.noteContent } </a>
														</span> <span> <a onclick="deleteNote('${nlist.id }')"><img
																style="margin-top: 16px;" src="${ctx}/images/cao.gif" /></a>
														</span>
													</p>
												</c:forEach>
											</div>
											<p class="book_cont" id="checks"></p>
											<p>
												<textarea name="content" id="content"></textarea>
											</p>
											<p class="book_a">
												<a name="save" style="display: inline;" class="a_gret"
													onclick="addNotes();"><ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.note.button" /></a>
											</p>
										</div>
									</li>
								</ul></li>
						</c:if>
					</ul>
				</div>
			
		<!-- 主阅读器 -->
		<div class="flexCont" style="height: 100%;" >	
							
		<div id="documentViewer" class="flexpaper_viewer"
			style="width:100%; height:100%; position: absolute; " >

		</div>

		<!--以下中间内容块开始-->
		<div class="big" style="display: none">
				
		<%-- <div class="main" style="padding-bottom:20px;">
			<c:if test="${webUrl!=null&&webUrl!='' }">
				<!-- IFream开始-->
				<iframe scrolling="yes" id="" height="800" width="830"
					src="${webUrl }"> </iframe>
				<!-- IFream结束 -->
			</c:if>
			<c:if test="${webUrl==null||webUrl=='' }">
				<!-- 左侧内容开始  -->
				<div class="read_content bg_wilte" id='left_div_info'
					style="height:800px;width:830px; float:left;">

					<div class="read_title">
						<div class="fl">
							<img src="${ctx }/images/logo_03.png" />
						</div>
						<div class="fl r_h1 pt8">
							<h1>${fn:replace(fn:replace(fn:replace(form.publicationsTitle,"&lt;","<"),"&gt;",">"),"&amp;","&")}</h1>
						</div>
						<div class="fl pt8">
							<a class="a_gret" onclick="closeReadWindow();"><ingenta-tag:LanguageTag sessionKey="lang" key="Pages.publications.article.Label.ClosePage"></ingenta-tag:LanguageTag></a>
						</div>
					</div>
					<c:if test="${available!=null&&available==5 }">
		    	<div><span style="color:red">主权声明</span></div>
		    	</c:if>
					<c:if test="${available!=null&&available==5 }">
						<div style="clear:both; padding:2px 0 10px 20px;">
							<img src="${ctx }/images/ico_20.png"
								style="vertical-align:middle; margin-right:8px;"> <span
								style="color:red"><ingenta-tag:LanguageTag
									sessionKey="lang"
									key="Pages.publications.article.Label.available" /></span>
						</div>
					</c:if> --%>
				
					<%-- 添加flexCont样式 --%>
					<!-- <div class="flexCont" >
						<div id="documentViewer" class="flexpaper_viewer"
							style="width:830px;height:867px; " ></div>
							style="width:830px;height:800px;"></div> 
					</div> -->
					<script type="text/javascript">

					function getDocumentUrl(document){
						var numPages = ${form.count};
						//当前浏览页码
						var url = "page?pubId={doc}&pageNum={page}";
						url = url.replace("{doc}",document);
						url = url.replace("{numPages}",numPages);
						
						return url;
					}
					
					function getDocQueryServiceUrl(document){
						return "swfsize.jsp?doc={doc}&page={page}".replace("{doc}",document);
					}
					var startDocument = "";
		
					function append_log(msg){
						$('#txt_eventlog').val(msg+'\n'+$('#txt_eventlog').val());
					}
		
					String.format = function() {
						var s = arguments[0];
						for (var i = 0; i < arguments.length - 1; i++) {
							var reg = new RegExp("\\{" + i + "\\}", "gm");
							s = s.replace(reg, arguments[i + 1]);
						}
						return s;
					};
		
					 var startDocument = "Paper";
					
					jQuery.get(
                            (!window.isTouchScreen) ? '${ctx}/pages/UI_flexpaper_desktop.html'
                                    : '${ctx}/pages/UI_flexpaper_mobile.html',
                            function(toolbarData) {
                                jQuery('#documentViewer')
                                        .FlexPaperViewer(
                                                {
                                                    config : {
                                                        IMGFiles 				: getDocumentUrl('${form.id}'),
                                                       	JSONFile 				: "json?pubId=${form.id}",
                                                        Scale                   : 0.6, 
                                                        ZoomTransition          : 'easeOut',
                                                        ZoomTime                : 0.5,
                                                        ZoomInterval            : 0.1,
                                                        FitPageOnLoad           : true, 
                                                        FitWidthOnLoad          : false, 
                                                        ProgressiveLoading      : false,
                                                        MinZoomSize             : 0.2,
                                                        MaxZoomSize             : 5,
                                                        SearchMatchAll          : false,
                                                        FullScreenAsMaxWindow   : false,

                                                        Toolbar                 : '',  
                                                        BottomToolbar           : '',
                                                        InitViewMode            : 'Portrait',
                                                        RenderingOrder          : 'html',
                                                        StartAtPage             : '',

                                                        ViewModeToolsVisible    : true,
                                                        ZoomToolsVisible        : true,
                                                        NavToolsVisible         : true,
                                                        CursorToolsVisible      : true,
                                                        SearchToolsVisible      : true,
                                                        EnableSearchAbstracts   : false,
                                                        localeChain             : '${sessionScope.lang}',
                                                        WMode : 'transparent', 
                                                        key : "@4516c44a3b7f5ec2893$cc3be1a9ff661cfdef6"
                                                    }
                                                });
                                
                                
                            });  
					
					
					
					
					/** var fp = new FlexPaperViewer(	
						 '${ctx}/flexpaper/FlexPaperViewer.swf',
						 'documentViewer', { config : {
						  *//* DOC : "page?pubId=${form.id}&format=swf&pageNum=1", */
						 /* DOC : /EPublishing/xxx.swf , */
						/**  DOC : getDocumentUrl('${form.id}'), */
						/*  Scale : 0.6,  */
					/**  ZoomTransition : 'easeOut',
						 ZoomTime : 0.5,
						 ZoomInterval : 0.2, */
						/*  FitPageOnLoad : true, */
					/** 	 FitWidthOnLoad : true,
						 PrintEnabled:false,
  						 SelectEnabled:false,

						 FullScreenAsMaxWindow : false,
						 ProgressiveLoading : false,
						 MinZoomSize : 0.2,
						 MaxZoomSize : 5,
						 SearchMatchAll : false,
						 InitViewMode : 'window',
						 
						 ViewModeToolsVisible : true,
						 ZoomToolsVisible : true,
						 NavToolsVisible : true,
						 CursorToolsVisible : true,
						 SearchToolsVisible : true,
  						 SearchString : '',
	  					 Reference : '',
	  					 SelectVisible : true,
  						 localeChain: '${sessionScope.lang}',
  						 WMode : 'transparent',
  						 key : '@4516c44a3b7f5ec2893$cc3be1a9ff661cfdef6'
					}}); */
					
					
						//判断当前的页面是否可以进行拷贝

					  jQuery('#documentViewer').bind('onCurrentPageChanged',function(e,pagenum){
						  var select_style = $('.flexpaper_bttnTextSelect').css("display")=="none"?true:false;
						  var copy_style = $("#btn_copy").css("display")=="none"?true:false;

						  $.ajax({
								type : "POST",  
						        url: "${ctx}/pages/view/form/tocCopy",
						        data: {
						        	pubId:$("#pubId11").val(),
						           	pageNum:pagenum ,
						           	licenseId:$("#licenseId11").val(),
						           	count:$("#pageCount11").val(),
						           	readCount:$("#readCount11").val(),
						        	isCopy:true,
						        	readCount:'${form.readCount}',
									r_ : new Date().getTime(),
									tag : "query"
						        },
						        success : function(data) {
						          var s = eval(data);
						          if(s[0].isCopy=='true' && s[0].flag=='true'){   
						          		  $('.flexpaper_bttnTextSelect').removeAttr('style');
							        	  $('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_pressed');
							        	  $('.flexpaper_bttnTextSelect').click();
							        	  $("#btn_copy").css("display","none");
						          } else{
						        	 // $('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_disabled');
						        	  $('.flexpaper_bttnHand').addClass('flexpaper_tbbutton_pressed');
						        	  $('.flexpaper_bttnTextSelect').removeClass('flexpaper_tbbutton_pressed');
						        	  $FlexPaper('documentViewer').setCurrentCursor("ArrowCursor");
						        	  $('.flexpaper_bttnTextSelect').css("display","none");
						        	  $("#btn_copy").css("display","inline");
						          } 
						       	  $("#copyCount").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Content.View.now.copy.page'/>："+s[0].copyCount);
						        },  
						        error : function(data) {  
						        	
						        }  
							}); 
						  
						 /*  
						  console.log(e);	
						  console.log(pagenum);
						   */
						  /** */
						  
						  
						 /*  
						 

			        	  if(current_page_num != currPage_num && copy_style){
							  $("#btn_copy").css("display","inline");
							  $('.flexpaper_bttnTextSelect').css("display","none");
							  return ; 
						  }
						 
						  var sessionScope_val = "${sessionScope.copyMap}"; 
						 
						  var page_val =current_page_num + "_" + $("#pubId11").val()+"="+$("#pubId11").val();
						  var sss = sessionScope_val[page_val] ; 
						  console.log(sss);
						   */
						  
						  /* 
						  if(page_val != null || page_val != ""){
							  $("#btn_copy").css("display","none");
							  $('.flexpaper_bttnTextSelect').removeAttr('style');
				        	  $('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_pressed');
				        	  $('.flexpaper_bttnTextSelect').click(); 
							  
						  }  */

						/*   if(current_page_num != currPage_num && copy_style){
							  $("#btn_copy").css("display","inline");
							  $('.flexpaper_bttnTextSelect').css("display","none");
							  return ; 
						  } */
						 						  
						/*   if(!select_style && !copy_style){
							  $.ajax({
									type : "POST",  
							        url: "${ctx}/pages/view/form/tocCopy",
							        data: {
							        	pubId:$("#pubId11").val(),
							           	pageNum: current_page_num,
							           	licenseId:$("#licenseId11").val(),
							           	count:$("#pageCount11").val(),
							           	readCount:$("#readCount11").val(),
							        	isCopy:true,
							        	readCount:'${form.readCount}',
										r_ : new Date().getTime()
							        },
							        success : function(data) {  
							          var s = eval(data);
							          if(s[0].isCopy=='true'){          	
							          		//开启-关闭页面选中按钮
							          		//var style = $('.flexpaper_bttnTextSelect').css("display");
							          		//if(style == ) 
							          		  $('.flexpaper_bttnTextSelect').removeAttr('style');
								        	  $('.flexpaper_bttnTextSelect').addClass('flexpaper_tbbutton_pressed');
								        	  $('.flexpaper_bttnTextSelect').click();          		
							          			
							          } else{
							        	  $('.flexpaper_bttnTextSelect').css("display","none");
							          } 
							          
							       	  $("#copyCount").html("<ingenta-tag:LanguageTag sessionKey='lang' key='Content.View.now.copy.page'/>："+s[0].copyCount);
							          currPage_num = current_page_num ; 
							          
							        },  
							        error : function(data) {  

							        }  
								}); 
						  } */
						  
					 }); 
					
				</script>
				<!-- </div> -->
				<!--左侧内容结束-->
				<%-- <!--右侧内容开始 -->
				<div class="readlist"
					style="padding-left: 10px; width: 184px; float:right; display: none">
					<c:if test="${form.id=='402880f1491210950149127d95020001' }">
						<h1>视频列表</h1>
						<ul class="hierarchy" style="text-align: left;">
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-1.mp4')">视频1： 重建手术1</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-2.mp4')">视频2：软骨移植操作</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-3.mp4')">视频3：重建手术2</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-4.mp4')">视频4：膝盖骨加固操作</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-5.mp4')">视频5：自体软骨细胞</a></li>
							<li class="hierarchy_li1"><a href="#login-modal"
								onclick="showVideo('video-45-6.mp4')">视频6：肌腱切割操作</a></li>
						</ul>
					</c:if>
					<h1>
						<ingenta-tag:LanguageTag sessionKey='lang'
							key='Page.view.Lable.tools' />
					</h1>
					<ul id="menu">
						<li><a><span class="alph"><img
									src="${ctx }/images/ico_04.gif"
									style="margin-top:13px; margin-left:-18px;" /></span> <span
								class="write"><ingenta-tag:LanguageTag sessionKey="lang"
										key="Pages.view.Label.Search" /></span></a>
							<ul class="tag">
								<li>
									<div>
										<p>
											<input class="a_input" type="text" id="searchView" /> <a
												style="cursor: pointer;" class="a_cxu" onclick="aaaa();">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Global.Button.Search" />
											</a>
										</p>
										<div></div>
										<br />
										<p id="search_result" class="book_b"
											style="padding-top: 10px;padding-bottom: 10px;display:none">
											<span id="searchText"></span><br /> <img id="prePage"
												style="cursor:pointer;float:none"
												src="${ctx }/images/pre.png" /> <span id="curNumber">0</span>
											/ <span id="allNumber"></span>&nbsp;&nbsp;&nbsp; <img
												id="nextPage" style="cursor:pointer;float:none"
												src="${ctx }/images/next.png" />

										</p>
										<p id="search_list"></p>
									</div>
								</li>
							</ul></li>
						<c:if test="${sessionScope.mainUser!=null }">
							<li><a><span class="alph"><img
										src="${ctx }/images/ico_01.gif"
										style="margin-top:13px; margin-left:-18px;" /></span> <span
									class="write"><ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.view.Label.Record" /></span></a>
								<ul class="tag">
									<li>
										<div>
											<p class="book_b">
												<c:if
													test="${form.record!=null&&form.record.pages.number>0 }">
													<a id="label1"
														onclick="javascript:$FlexPaper('documentViewer').gotoPage('${form.record.pages.number}');"
														style="margin: 0px;padding: 0px;border-bottom: none;background: none;">
														<ingenta-tag:LanguageTag sessionKey="lang"
															key="Pages.view.lable.lable1" />：${form.record.pages.number }
													</a>
												</c:if>
												<c:if
													test="${form.record==null||form.record.pages.number==null || form.record.pages.number<=0 }">
													<a id="label1"
														style="margin: 0px;padding: 0px;border-bottom: none;background: none;">
														<ingenta-tag:LanguageTag sessionKey="lang"
															key="Pages.view.lable.nolable" />
													</a>
												</c:if>
											</p>
											<p class="book_a">
												<a onclick="addLabels();" class="a_gret"><ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.lable.button" /></a>
											</p>
										</div>
									</li>
								</ul></li>
							<li><a><span class="alph"> <input type="hidden"
										id="isNote" value="" /> <img src="${ctx }/images/ico_02.gif"
										style="margin-top:13px; margin-left:-18px;" />
								</span> <span class="write"><ingenta-tag:LanguageTag
											sessionKey="lang" key="Pages.view.Label.Notes" /></span></a>
								<ul class="tag newTag">
									<li>
										<div>
											<div id="noteList">
												<c:forEach items="${form.noteList }" var="nlist"
													varStatus="index">
													<p id="nodes_${nlist.id }">
														<span> <a
															style="margin: 0px;padding: 0px; border-bottom: none;background:none;"
															onclick="javascript:$FlexPaper('documentViewer').gotoPage(${nlist.pages.number})">
																${nlist.pages.number }__${nlist.noteContent } </a>
														</span> <span> <a onclick="deleteNote('${nlist.id }')"><img
																style="margin-top: 16px;" src="${ctx}/images/cao.gif" /></a>
														</span>
													</p>
												</c:forEach>
											</div>
											<p class="book_cont" id="checks"></p>
											<p>
												<textarea name="content" id="content"></textarea>
											</p>
											<p class="book_a">
												<a name="save" style="display: inline;" class="a_gret"
													onclick="addNotes();"><ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.note.button" /></a>
											</p>
										</div>
									</li>
								</ul></li>
						</c:if>
						<c:if test="${form.readCount !=null && form.readCount>0}">
							<li><a><span class="alph"><img
										src="${ctx }/images/ico_03.gif"
										style="margin-top:13px; margin-left:-18px;" /></span> <span
									class="write"><ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.view.Label.Copy" /></span></a>
								<ul class="tag">
									<li>
										<div>
											<p class="book_b">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Pages.view.copy.Prompt" />
												：${form.readCount }
											</p>
											<p class="book_b" id="copyCount">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Content.View.now.copy.page" />
												：${copyCount}
											</p>
											<p id="copy_but" class="book_a">
												<a id="btn_copy" class="a_gret" onclick="copy()"
													style="margin-right: 4px;"> <ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.copy.Button" />
												</a>
											</p>
										</div>
									</li>
								</ul></li>
						</c:if>
						<c:if test="${form.downloadCount !=null && form.downloadCount>0}">
							<li><a><span class="alph"><img
										src="${ctx }/images/ico_05.gif"
										style="margin-top:13px; margin-left:-18px;" /></span> <span
									class="write"><ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.view.Label.Download" /></span></a>
								<ul class="tag">
									<li>
										<div>
											<p class="book_b">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Pages.view.download.Prompt" />
												：${form.downloadCount }
											</p>
											<p class="book_b" id="downloadCount">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Content.View.now.download.page" />
												：${downloadCount}
											</p>
											<p id="copy_but" class="book_a">
												<a class="a_gret" onclick="downloadaaaaa()" id="bbbbb">
													<ingenta-tag:LanguageTag sessionKey="lang"
														key="Global.Button.DownLoad" />
												</a>
												<c:if test="${pubType==4 && downloadPrecent==100}">
													<a class="a_gret"
														href="${ctx}/pages/view/form/download?pubId=${form.id}&isFull=true"
														id="downFull"> <ingenta-tag:LanguageTag
															sessionKey="lang" key="Pages.View.Button.DownLoad.Full" />
													</a>
												</c:if>
											</p>
										</div>
									</li>
								</ul></li>
						</c:if>
						<c:if test="${form.printCount !=null && form.printCount>0}">
							<li><a><span class="alph"><img
										src="${ctx }/images/ico_06.gif"
										style="margin-top:13px; margin-left:-18px;" /></span> <span
									class="write"><ingenta-tag:LanguageTag sessionKey="lang"
											key="Pages.view.Label.Print" /></span></a>
								<ul class="tag">
									<li>
										<div>
											<p class="book_b">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Pages.view.print.Prompt" />
												：${form.printCount }
											</p>
											<p class="book_b" id="printCount">
												<ingenta-tag:LanguageTag sessionKey="lang"
													key="Content.View.now.print.page" />
												：${printCount}
											</p>
											<p>
												<input class="a_input" type="text" id="printStr" /> <a
													class="a_cxu" onclick="ajaxPrint()" id="bt_print"> <ingenta-tag:LanguageTag
														sessionKey="lang" key="Pages.view.Label.Print" />
												</a>
											</p>
										</div>
									</li>
								</ul></li>
						</c:if>
					</ul>

					<c:if test="${form.type!=4}">
						<h1>
							<ingenta-tag:LanguageTag sessionKey='lang'
								key='Page.view.Lable.Chapter' />
						</h1>
						<ul class="hierarchy" id="chapter_label"
							style="text-align: center;">
							<img src="${ctx}/images/loading.gif" />
						</ul>
					</c:if>

					<c:if test="${form.type==4}">
					</c:if>

				</div> --%>




				<!--右侧内容结束 -->
		</div>
		<!--以上中间内容块结束-->
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
		<input type="hidden" id="pubId11" value="${form.id }" /> <input
			type="hidden" id="licenseId11" value="${form.licenseId }" /> <input
			type="hidden" id="pageCount11" value="${form.count }" /> <input
			type="hidden" id="readCount11" value="${form.readCount }" /> <input
			type="hidden" id="isCopy11" value="${form.isCopy }" />


	</div>
	
	</div>
</c:if>
</body>
</html>