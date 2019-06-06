$(document).ready(function(e) {
	$(".meaudown").mouseover(function(){
	  $(this).children("ul").css('display','block');
  });
    $(".meaudown").mouseleave(function(){
	  $(this).children("ul").css('display','none');
  });
  $(".onCur").mouseover(function(){
	  $(this).children("div.flexTake").css('display','block');
  });
	$(".onCur").mouseleave(function(){
	  $(this).children("div.flexTake").css('display','none');
  });
 /*epub js*/ 
  $curDiv=$(".flexRight");
	$(".imgCur").click(function(){
		 if($curDiv.is(":visible")){
			$curDiv.hide();                  
			$(this).html("<img src='images/ico/img/open.png' />"); 
			$(".flexpaper_viewer_wrap").css("width","930px").css("border-right","none");           	
		}else{
			$curDiv.show();                   		
			$(this).html("<img src='images/ico/img/close.png' />"); 
			$(".flexpaper_viewer_wrap").css("width","750px").css("border-right","1px solid #ccc");
		 }
		$FlexPaper("documentViewer").resize();
		$FlexPaper("documentViewer").fitWidth();
		return false;
	});
	$(".download").click(function(){
		$(".flexPop").css("display","block");
		return false;
	})
	$(".close").click(function(){
		$(".flexPop").css("display","none");
		return false;
	})
 /*epub js*/
/*鼠标移过某个按钮 高亮显示*/
	$(".prev,.next").hover(function(){
		$(this).fadeTo("show",0.7);
	},function(){
		$(this).fadeTo("show",0.1);
	})

	$("li.Stab").click(function(){
	$(this).addClass("StabSeleted").siblings().removeClass("StabSeleted");
	var nIndex=$(this).index();
	$(this).parent().siblings().removeClass("ScontentSelected").eq(nIndex).addClass("ScontentSelected");	
	});
	
	$newList=$(".dot").children("li:nth-child(n+5)");
	$newList.hide();
	var $togListA=$("span.newListA > a");
	$togListA.click(function(){
		 if($newList.is(":visible")){
				$newList.hide();                  
				$(this).html("<img src='images/ico/ico12.png' />");                 	
		}else{
				$newList.show();                   		
				$(this).html("<img src='images/ico/ico19.png' />");                
			}
		return false;
	})
	
	$aLis=$(".updownUl").children("li:nth-child(n+6)");
	$aLis.hide();
	var $toggleBtn = $('span.updownMore > a');
	$toggleBtn.click(function(){
		var $oLis=$(this).parent().siblings("ul").children("li:nth-child(n+6)");
		 if($oLis.is(":visible")){
				$oLis.hide();                  
				$(this).text("更多...");                 	
		}else{
				$oLis.show();                   		
				$(this).text("更少...");                
			}
		return false;					      	
	});
	
	
	function initMenu() {
	  $('#menu ul').hide();
  	  $('#menu ul:first').show();
	  $('#menu li a').click(
		function() {
		  var checkElement = $(this).next();
		  if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
			$('#menu ul:visible').slideUp('normal');
			checkElement.slideDown('normal');
			return false;
			}else{
				$(checkElement).slideToggle('normal');
			}
		  }
		);
	  }
	$(document).ready(function() {initMenu();});

	
})


$(document).ready(function(e) {
			
        });