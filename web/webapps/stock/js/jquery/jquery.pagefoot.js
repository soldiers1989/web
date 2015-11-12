/*
******生成分页脚******
        
使用：
	$("#pagefoot").pagefoot({
	    pagesize:10,
	    count:500,
	    css:"",
	    previous:"<",
	    next:">",
	    paging:function(page){
			    alert("当前第"+page+"页");
		  }
	});
*/
(function($){
	$.pagefoot = {
    create:function(_this,s){
        var pageCount=0;
        //计算总页码
        pageCount = (s.count/s.pagesize<=0) ? 1 : Math.ceil(s.count/s.pagesize);
        s.current = (s.current > pageCount) ? pageCount : s.current;
        //循环生成页码
        var strPage="";
		var strJump='';
        //创建上一页
        if(s.current<=1){
        	  strPage += '<a class="pf-disabled" style="cursor:default">' + s.previous + '</a>';
        }else{
            strPage += '<a href='+(s.current-1)+'>' +s.previous+ '</a>';
        }
        //开始的页码
        var startP=1;
		    var anyMore;//页码左右显示最大页码数
		    //当前页前后显示的页码数
		    anyMore = parseInt(s.displaynum/2);
        //结束的页码
        var endP=(s.current + anyMore) > pageCount ? pageCount : s.current + anyMore;

        //可显示的码数(剩N个用来显示最后N页的页码)
        var pCount = s.pagesize -s.displaylastNum;
        if(s.current>s.displaynum){//页码数太多时，则隐藏多余的页码
            startP=s.current-anyMore;
            for(i=1;i<=s.displaylastNum;i++){
                strPage+='<a href="'+i+'">'+i+'</a>';
            }
            strPage+="...";
        }
        if(s.current+s.displaynum<=pageCount){//页码数太多时，则隐藏前面多余的页码
            endP=s.current+anyMore;
        }else{
            endP=pageCount;
        }
        for(i=startP;i<=endP;i++){
            if(s.current==i){
                strPage+='<a class="cur pf-disabled">'+i+'</a>';
            }else{
                strPage+='<a href="'+i+'">'+i+'</a>';
            }
        }
        if(s.current+s.displaynum<=pageCount){//页码数太多时，则隐藏后面多余的页码
            strPage+="...";
            for(i=pageCount-s.displaylastNum+1;i<=pageCount;i++){
                strPage+='<a href="'+i+'">'+i+'</a>';
            }
        }
        //创建下一页
        if(s.current>=pageCount){
            strPage += '<a class="pf-disabled" style="cursor:default">'+s.next+'</a>';
        }else{
            strPage+='<a href=' + (s.current+1) + '>' + s.next + '</a>';
        }
		if(pageCount > 1){
			strJump+='&nbsp;跳至&nbsp;<span class="p-txt" id="p-txt"><input type="text" class="txt"/><i class="ck">确定</i></span>&nbsp;页';
		}

        $(_this).empty().append('<span>'+strPage+'</span><span class="go-page">'+strJump+'</span>').find("a").click(function(){
            //得到翻页的页码
            if(this.className.indexOf("pf-disabled")>-1){return ;}
            var ln = this.href.lastIndexOf("/");
            var href = this.href;
            var page = parseInt(href.substring(ln+1,href.length));
            s.current=page; 
            //外部取消翻页时...
            if(!$.pagefoot.paging(s,s.paging)){
            	return false;
            }
            
            $.pagefoot.create(_this,s);
            //$(document).unbind('keydown.arrowto');
            
            return false;
        });
		$(_this).find('i.ck').click(function(){
			var page=$(this).siblings('input').val();
			if(page=='' || isNaN(page))return false;
			if(page > pageCount || page < 0)return false;
			s.current=page; 
            //外部取消翻页时...
            if(!$.pagefoot.paging(s,s.paging)){
            	return false;
            }
            
            $.pagefoot.create(_this,s);
			return false;
		});
		$(_this).find('#p-txt').on('click',function(){
			$(this).addClass('f-focus');
		});
        
        if(s.enableArrowKey){
        	$(document).unbind('keyup.arrowto').bind('keyup.arrowto', function (event){
    			//left
        		if(event.keyCode == 37){
        			$(_this).find('a').eq(0).trigger('click');
        		}
        		//right
        		if(event.keyCode == 39){
        			$(_this).find('a').eq($(_this).find('a').length-1).trigger('click');
        		}
        		$(_this).attr('data-istrigging', true);
        		
        	});
        }
        
        return this;
    },
    paging:function(s,callback){
      if(callback){
	       if(callback(s)==false){
	        	return false;
	       }
      }
      return true;
    }
	}
	
	$.fn.pagefoot= function(opt){
		var setting = {
			pagesize:10             //每页显示的页码数
			,count:0                //数据条数
			,css:""                 //分页脚css样式类
			,current:1              //当前页码
			,displaynum:10			    //中间显示页码数
			,displaylastNum:0		    //最后显示的页码数
			,previous:"<<上一页"    //上一页显示样式
			,next:"下一页>>"        //下一页显示样式
			,paging:null            //分页事件触发时callback函数
		};
		opt = opt || {};
		$.extend(setting , opt);
	  return this.each(function(){
	    $(this).addClass(setting.css);
	    $.pagefoot.create(this,setting);
	  });
	}
})(jQuery);