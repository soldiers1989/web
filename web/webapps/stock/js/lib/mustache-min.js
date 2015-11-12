/*
 * mustache.js - Logic-less {{mustache}} templates with JavaScript
 * http://github.com/janl/mustache.js
 */
var Mustache;(function(a){if(typeof module!=="undefined"&&typeof module.exports!=="undefined"){module.exports=a}else{if(typeof define==="function"){define(a)}else{Mustache=a}}}((function(){var A={};A.name="mustache.js";A.version="0.5.2";A.tags=["{{","}}"];A.parse=o;A.clearCache=y;A.compile=h;A.compilePartial=k;A.render=z;A.Scanner=v;A.Context=s;A.Renderer=t;A.to_html=function(F,D,E,G){var C=z(F,D,E);if(typeof G==="function"){G(C)}else{return C}};var c=/\s*/;var m=/\s+/;var i=/\S/;var g=/\s*=/;var p=/\s*\}/;var u=/#|\^|\/|>|\{|&|=|!/;function q(D,C){return RegExp.prototype.test.call(D,C)}function f(C){return !q(i,C)}var j=Array.isArray||function(C){return Object.prototype.toString.call(C)==="[object Array]"};var l=/[\x00-\x2F\x3A-\x40\x5B-\x60\x7B-\xFF\u2028\u2029]/gm;function x(D){var C=D.replace(l,function(E){return"\\u"+("0000"+E.charCodeAt(0).toString(16)).slice(-4)});return'"'+C+'"'}function e(C){return C.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g,"\\$&")}var b={"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;","/":"&#x2F;"};function n(C){return String(C).replace(/[&<>"'\/]/g,function(D){return b[D]})}A.isWhitespace=f;A.isArray=j;A.quote=x;A.escapeRe=e;A.escapeHtml=n;function v(C){this.string=C;this.tail=C;this.pos=0}v.prototype.eos=function(){return this.tail===""};v.prototype.scan=function(D){var C=this.tail.match(D);if(C&&C.index===0){this.tail=this.tail.substring(C[0].length);this.pos+=C[0].length;return C[0]}return""};v.prototype.scanUntil=function(D){var C,E=this.tail.search(D);switch(E){case -1:C=this.tail;this.pos+=this.tail.length;this.tail="";break;case 0:C="";break;default:C=this.tail.substring(0,E);this.tail=this.tail.substring(E);this.pos+=E}return C};function s(C,D){this.view=C;this.parent=D;this.clearCache()}s.make=function(C){return(C instanceof s)?C:new s(C)};s.prototype.clearCache=function(){this._cache={}};s.prototype.push=function(C){return new s(C,this)};s.prototype.lookup=function(C){var F=this._cache[C];if(!F){if(C==="."){F=this.view}else{var E=this;while(E){if(C.indexOf(".")>0){var G=C.split("."),D=0;F=E.view;while(F&&D<G.length){F=F[G[D++]]}}else{F=E.view[C]}if(F!=null){break}E=E.parent}}this._cache[C]=F}if(typeof F==="function"){F=F.call(this.view)}return F};function t(){this.clearCache()}t.prototype.clearCache=function(){this._cache={};this._partialCache={}};t.prototype.compile=function(F,D){if(typeof F==="string"){F=o(F,D)}var E=d(F),C=this;return function(G){return E(s.make(G),C)}};t.prototype.compilePartial=function(D,E,C){this._partialCache[D]=this.compile(E,C);return this._partialCache[D]};t.prototype.render=function(E,C){var D=this._cache[E];if(!D){D=this.compile(E);this._cache[E]=D}return D(C)};t.prototype._section=function(C,D,K){var J=D.lookup(C);switch(typeof J){case"object":if(j(J)){var F="";for(var G=0,I=J.length;G<I;++G){F+=K(D.push(J[G]),this)}return F}return J?K(D.push(J),this):"";case"function":var E=K(D,this),L=this;var H=function(M){return L.render(M,D)};return J.call(D.view,E,H)||"";default:if(J){return K(D,this)}}return""};t.prototype._inverted=function(C,D,F){var E=D.lookup(C);if(E==null||E===false||(j(E)&&E.length===0)){return F(D,this)}return""};t.prototype._partial=function(C,D){var E=this._partialCache[C];if(E){return E(D)}return""};t.prototype._name=function(D,E,F){var G=E.lookup(D);if(typeof G==="function"){G=G.call(E.view)}var C=(G==null)?"":String(G);if(F){return n(C)}return C};function d(H,I){var D=['""'];var F,J,G;for(var E=0,C=H.length;E<C;++E){F=H[E];switch(F.type){case"#":case"^":J=(F.type==="#")?"_section":"_inverted";D.push("r."+J+"("+x(F.value)+", c, function (c, r) {\n  "+d(F.tokens,true)+"\n})");break;case"{":case"&":case"name":G=F.type==="name"?"true":"false";D.push("r._name("+x(F.value)+", c, "+G+")");break;case">":D.push("r._partial("+x(F.value)+", c)");break;case"text":D.push(x(F.value));break}}D="return "+D.join(" + ")+";";if(I){return D}return new Function("c, r",D)}function r(C){if(C.length===2){return[new RegExp(e(C[0])+"\\s*"),new RegExp("\\s*"+e(C[1]))]}throw new Error("Invalid tags: "+C.join(" "))}function B(H){var C=[];var G=C;var I=[];var E,F;for(var D=0;D<H.length;++D){E=H[D];switch(E.type){case"#":case"^":E.tokens=[];I.push(E);G.push(E);G=E.tokens;break;case"/":if(I.length===0){throw new Error("Unopened section: "+E.value)}F=I.pop();if(F.value!==E.value){throw new Error("Unclosed section: "+F.value)}if(I.length>0){G=I[I.length-1].tokens}else{G=C}break;default:G.push(E)}}F=I.pop();if(F){throw new Error("Unclosed section: "+F.value)}return C}function a(F){var C;for(var E=0;E<F.length;++E){var D=F[E];if(C&&C.type==="text"&&D.type==="text"){C.value+=D.value;F.splice(E--,1)}else{C=D}}}function o(N,P){P=P||A.tags;var O=r(P);var E=new v(N);var L=[],J=[],H=false,Q=false;var C=function(){if(H&&!Q){while(J.length){L.splice(J.pop(),1)}}else{J=[]}H=false;Q=false};var K,M,F;while(!E.eos()){M=E.scanUntil(O[0]);if(M){for(var G=0,I=M.length;G<I;++G){F=M.charAt(G);if(f(F)){J.push(L.length)}else{Q=true}L.push({type:"text",value:F});if(F==="\n"){C()}}}if(!E.scan(O[0])){break}H=true;K=E.scan(u)||"name";E.scan(c);if(K==="="){M=E.scanUntil(g);E.scan(g);E.scanUntil(O[1])}else{if(K==="{"){var D=new RegExp("\\s*"+e("}"+P[1]));M=E.scanUntil(D);E.scan(p);E.scanUntil(O[1])}else{M=E.scanUntil(O[1])}}if(!E.scan(O[1])){throw new Error("Unclosed tag at "+E.pos)}L.push({type:K,value:M});if(K==="name"||K==="{"||K==="&"){Q=true}if(K==="="){P=M.split(m);O=r(P)}}a(L);return B(L)}var w=new t();function y(){w.clearCache()}function h(D,C){return w.compile(D,C)}function k(D,E,C){return w.compilePartial(D,E,C)}function z(F,C,E){if(E){for(var D in E){k(D,E[D])}}return w.render(F,C)}return A}())));
