/*! Copyright (c) 2013 Brandon Aaron (http://brandon.aaron.sh)
 * Licensed under the MIT License (LICENSE.txt).
 *
 * Version 3.0.1
 *
 * Requires jQuery >= 1.2.6
 */
(function(n){typeof define=="function"&&define.amd?define(["jquery"],n):typeof exports=="object"?module.exports=n:n(jQuery)})(function(n){function t(n){return n&&n.constructor===Number?n+"px":n}n.fn.bgiframe=function(i){var r,u;return i=n.extend({top:"auto",left:"auto",width:"auto",height:"auto",opacity:!0,src:"javascript:false;",conditional:/MSIE 6\.0/.test(navigator.userAgent)},i),n.isFunction(i.conditional)||(r=i.conditional,i.conditional=function(){return r}),u=n('<iframe class="bgiframe"frameborder="0"tabindex="-1"src="'+i.src+'"style="display:block;position:absolute;z-index:-1;"/>'),this.each(function(){var r=n(this),f,e;i.conditional(this)!==!1&&(f=r.children("iframe.bgiframe"),e=f.length===0?u.clone():f,e.css({top:i.top=="auto"?(parseInt(r.css("borderTopWidth"),10)||0)*-1+"px":t(i.top),left:i.left=="auto"?(parseInt(r.css("borderLeftWidth"),10)||0)*-1+"px":t(i.left),width:i.width=="auto"?this.offsetWidth+"px":t(i.width),height:i.height=="auto"?this.offsetHeight+"px":t(i.height),opacity:i.opacity===!0?0:undefined}),f.length===0&&r.append(e))})};n.fn.bgIframe=n.fn.bgiframe});
/*
//# sourceMappingURL=jquery.bgiframe.min.js.map
*/