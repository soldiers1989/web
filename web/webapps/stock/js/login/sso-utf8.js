/**
 * sso
 */
(function (window) {
    if (typeof JRJ === "undefined") {
        JRJ = {};
    }
    if (typeof JRJ.ssoHead === "undefined") {
        JRJ.ssoHead = {};
    }
    /**
	 * 格式化字符串
	 */
    String.prototype.format = function () {
        var args = arguments;
        if (args.length == 1 && Object.prototype.toString.apply(args[0]) === "[object Array]") {
            args = args[0];
        }
        return this.replace(/\{(\d+)\}/g, function (str, i) {
            var rep = args[i];
            return rep ? rep : "";
        });
    };
    window.DomReady = DomReady = (function () {
        // Everything that has to do with properly supporting our document ready event. Brought over from the most awesome jQuery. 
        var userAgent = navigator.userAgent.toLowerCase();
        // Figure out what browser is being used
        var browser = {
            version: (userAgent.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1],
            safari: /webkit/.test(userAgent),
            opera: /opera/.test(userAgent),
            msie: (/msie/.test(userAgent)) && (!/opera/.test(userAgent)),
            mozilla: (/mozilla/.test(userAgent)) && (!/(compatible|webkit)/.test(userAgent))
        };
        var readyBound = false;
        var isReady = false;
        var readyList = [];
        // Handle when the DOM is ready
        function domReady() {
            // Make sure that the DOM is not already loaded
            if (!isReady) {
                // Remember that the DOM is ready
                isReady = true;

                if (readyList) {
                    for (var fn = 0; fn < readyList.length; fn++) {
                        readyList[fn].call(window, []);
                    }

                    readyList = [];
                }
            }
        };
        // From Simon Willison. A safe way to fire onload w/o screwing up everyone else.
        function addLoadEvent(func) {
            var oldonload = window.onload;
            if (typeof window.onload != 'function') {
                window.onload = func;
            } else {
                window.onload = function () {
                    if (oldonload) {
                        oldonload();
                    }
                    func();
                }
            }
        };
        // does the heavy work of working through the browsers idiosyncracies (let's call them that) to hook onload.
        function bindReady() {
            if (readyBound) {
                return;
            }
            readyBound = true;
            // Mozilla, Opera (see further below for it) and webkit nightlies currently support this event
            if (document.addEventListener && !browser.opera) {
                // Use the handy event callback
                document.addEventListener("DOMContentLoaded", domReady, false);
            }
            // If IE is used and is not in a frame
            // Continually check to see if the document is ready
            if (browser.msie && window == top)
                (function () {
                    if (isReady)
                        return;
                    try {
                        // If IE is used, use the trick by Diego Perini
                        // http://javascript.nwbox.com/IEContentLoaded/
                        document.documentElement.doScroll("left");
                    } catch (error) {
                        setTimeout(arguments.callee, 0);
                        return;
                    }
                    // and execute any waiting functions
                    domReady();
                })();
            if (browser.opera) {
                document.addEventListener("DOMContentLoaded", function () {
                    if (isReady)
                        return;
                    for (var i = 0; i < document.styleSheets.length; i++)
                        if (document.styleSheets[i].disabled) {
                            setTimeout(arguments.callee, 0);
                            return;
                        }
                    // and execute any waiting functions
                    domReady();
                }, false);
            }
            if (browser.safari) {
                var numStyles;
                (function () {
                    if (isReady)
                        return;
                    if (document.readyState != "loaded" && document.readyState != "complete") {
                        setTimeout(arguments.callee, 0);
                        return;
                    }
                    if (numStyles === undefined) {
                        var links = document.getElementsByTagName("link");
                        for (var i = 0; i < links.length; i++) {
                            if (links[i].getAttribute('rel') == 'stylesheet') {
                                numStyles++;
                            }
                        }
                        var styles = document.getElementsByTagName("style");
                        numStyles += styles.length;
                    }
                    if (document.styleSheets.length != numStyles) {
                        setTimeout(arguments.callee, 0);
                        return;
                    }

                    // and execute any waiting functions
                    domReady();
                })();
            }
            // A fallback to window.onload, that will always work
            addLoadEvent(domReady);
        };
        bindReady();
        // This is the public function that people can use to hook up ready.
        return {
            ready: function (fn, args) {
                // Attach the listeners
                bindReady();
                // If the DOM is already ready
                if (isReady) {
                    // Execute the function immediately
                    fn.call(window, []);
                } else {
                    // Add the function to the wait list
                    readyList.push(function () {
                        return fn.call(window, []);
                    });
                }
            }
        };
    })(window);
    /**
	 * 辅助工具类
	 */
    JRJ.ssoHead.util = (function () {
        var toString = Object.prototype.toString;
        var head = document.getElementsByTagName("head")[0] || document.documentElement;
        return {
            /**
			 * 是否为空
			 */
            isEmpty: function (v, allowBlank) {
                return v === null || v === undefined || ((JRJ.ssoHead.util.isArray(v) && !v.length))
						|| (!allowBlank ? v === '' : false);
            },
            isObject: function (v) {
                return !!v && Object.prototype.toString.call(v) === '[object Object]';
            },
            /**
			 * Returns true if the passed value is a JavaScript array, otherwise
			 * false.
			 * 
			 * @param {Mixed}
			 *            value The value to test
			 * @return {Boolean}
			 */
            isArray: function (v) {
                return toString.apply(v) === '[object Array]';
            },
            /**
			 * Returns true if the passed value is not undefined.
			 * 
			 * @param {Mixed}
			 *            value The value to test
			 * @return {Boolean}
			 */
            isDefined: function (v) {
                return typeof v !== 'undefined';
            },
            /**
			 * 获取当前时间的毫秒
			 */
            time: function () {
                return +new Date();
            },
            /**
			 * Appends content to the query string of a URL, handling logic for
			 * whether to place a question mark or ampersand.
			 * 
			 * @param {String}
			 *            url The URL to append to.
			 * @param {String}
			 *            s The content to append to the URL.
			 * @return (String) The resulting URL
			 */
            urlAppend: function (url, s) {
                if (!JRJ.ssoHead.util.isEmpty(s)) {
                    return url + (url.indexOf('?') === -1 ? '?' : '&') + s;
                }
                return url;
            },
            /**
			 * 跨域请求js
			 */
            getScript: function (url, callback, nocache, scope, args) {
                var script = document.createElement("script");
                // 如果不指定（undefined）或者明确指定不需要缓存(fasle)
                nocache = JRJ.ssoHead.util.isDefined(nocache) ? nocache : true;
                script.src = nocache ? JRJ.ssoHead.util.urlAppend(url, '_dc=' + JRJ.ssoHead.util.time()) : url;
                script.onload = script.onreadystatechange = function () {
                    if (!script.readyState || "loaded" == script.readyState || "complete" == script.readyState) {
                        callback.apply(scope || window, args || []);
                        script.onload = script.onreadystatechange = null;
                        if (head && script.parentNode) {
                            head.removeChild(script);
                        }
                    }
                };
                head.insertBefore(script, head.firstChild);
            },
            /**
			 * 添加事件绑定
			 * 
			 * @param obj :
			 *            要绑定事件的元素
			 * @param type :
			 *            事件名称。不加 "on". 如 : "click" 而不是 "onclick".
			 * @param fn :
			 *            事件处理函数
			 */
            bind: function (obj, type, fn) {
                if (obj.attachEvent) {
                    obj['e' + type + fn] = fn;
                    obj[type + fn] = function () {
                        obj['e' + type + fn](window.event);
                    }
                    obj.attachEvent('on' + type, obj[type + fn]);
                } else
                    obj.addEventListener(type, fn, false);
            },
            /**
			 * 加入收藏夹
			 */
            addFavorite: function () {
                var url = location.href;
                var title = document.title;
                var message = "抱歉，此浏览器不支持收藏功能。\r\n请使用  Ctrl+D 快捷键添加。";
                try {
                    if (document.all) {
                        try {
                            window.external.AddFavorite(url, title);
                        } catch (ex1) {
                            try {
                                window.external.AddToFavoritesBar(url, title, 'slice');
                            } catch (ex2) {
                                alert(message);
                            }
                        }
                    } else if (window.sidebar) {
                        window.sidebar.addPanel(title, url, "");
                    } else {
                        alert(message);
                    }
                } catch (error) {
                    alert(message);
                }
            },
            /**
			 * 设为主页，chrome,opera,safari浏览器不支持
			 * 
			 * @param {string}
			 *            sURL 页面url
			 */
            setHomePage: function () {
                var url = location.href;
                try {
                    document.body.style.behavior = 'url(#default#homepage)';
                    document.body.setHomePage(url);
                } catch (e) {
                    if (window.netscape) {
                        try {
                            netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
                            var prefs = Components.classes['@mozilla.org/preferences-service;1']
									.getService(Components.interfaces.nsIPrefBranch);
                            prefs.setCharPref('browser.startup.homepage', url);
                        } catch (e) {
                            alert("请在浏览器地址栏输入“about:config”并回车，\r\n然后将[signed.applets.codebase_principal_support]设置为'true'。");
                        }
                    } else {
                        alert("抱歉，您所使用的浏览器无法完成操作。\r\n您需要手动将'" + url + "'设为首页。");
                    }
                }
            },
            /**
			 * MD5 (Message-Digest Algorithm) http://www.webtoolkit.info/
			 */
            MD5: function (string) {
                function RotateLeft(lValue, iShiftBits) {
                    return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
                }
                function AddUnsigned(lX, lY) {
                    var lX4, lY4, lX8, lY8, lResult;
                    lX8 = (lX & 0x80000000);
                    lY8 = (lY & 0x80000000);
                    lX4 = (lX & 0x40000000);
                    lY4 = (lY & 0x40000000);
                    lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
                    if (lX4 & lY4) {
                        return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
                    }
                    if (lX4 | lY4) {
                        if (lResult & 0x40000000) {
                            return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
                        } else {
                            return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
                        }
                    } else {
                        return (lResult ^ lX8 ^ lY8);
                    }
                }

                function F(x, y, z) {
                    return (x & y) | ((~x) & z);
                }
                function G(x, y, z) {
                    return (x & z) | (y & (~z));
                }
                function H(x, y, z) {
                    return (x ^ y ^ z);
                }
                function I(x, y, z) {
                    return (y ^ (x | (~z)));
                }

                function FF(a, b, c, d, x, s, ac) {
                    a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
                    return AddUnsigned(RotateLeft(a, s), b);
                };

                function GG(a, b, c, d, x, s, ac) {
                    a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
                    return AddUnsigned(RotateLeft(a, s), b);
                };

                function HH(a, b, c, d, x, s, ac) {
                    a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
                    return AddUnsigned(RotateLeft(a, s), b);
                };

                function II(a, b, c, d, x, s, ac) {
                    a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
                    return AddUnsigned(RotateLeft(a, s), b);
                };

                function ConvertToWordArray(string) {
                    var lWordCount;
                    var lMessageLength = string.length;
                    var lNumberOfWords_temp1 = lMessageLength + 8;
                    var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - (lNumberOfWords_temp1 % 64)) / 64;
                    var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
                    var lWordArray = Array(lNumberOfWords - 1);
                    var lBytePosition = 0;
                    var lByteCount = 0;
                    while (lByteCount < lMessageLength) {
                        lWordCount = (lByteCount - (lByteCount % 4)) / 4;
                        lBytePosition = (lByteCount % 4) * 8;
                        lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount) << lBytePosition));
                        lByteCount++;
                    }
                    lWordCount = (lByteCount - (lByteCount % 4)) / 4;
                    lBytePosition = (lByteCount % 4) * 8;
                    lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80 << lBytePosition);
                    lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
                    lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
                    return lWordArray;
                };

                function WordToHex(lValue) {
                    var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
                    for (lCount = 0; lCount <= 3; lCount++) {
                        lByte = (lValue >>> (lCount * 8)) & 255;
                        WordToHexValue_temp = "0" + lByte.toString(16);
                        WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length - 2, 2);
                    }
                    return WordToHexValue;
                };

                function Utf8Encode(string) {
                    string = string.replace(/\r\n/g, "\n");
                    var utftext = "";

                    for (var n = 0; n < string.length; n++) {

                        var c = string.charCodeAt(n);

                        if (c < 128) {
                            utftext += String.fromCharCode(c);
                        } else if ((c > 127) && (c < 2048)) {
                            utftext += String.fromCharCode((c >> 6) | 192);
                            utftext += String.fromCharCode((c & 63) | 128);
                        } else {
                            utftext += String.fromCharCode((c >> 12) | 224);
                            utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                            utftext += String.fromCharCode((c & 63) | 128);
                        }

                    }

                    return utftext;
                };

                var x = Array();
                var k, AA, BB, CC, DD, a, b, c, d;
                var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
                var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
                var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
                var S41 = 6, S42 = 10, S43 = 15, S44 = 21;

                string = Utf8Encode(string);

                x = ConvertToWordArray(string);

                a = 0x67452301;
                b = 0xEFCDAB89;
                c = 0x98BADCFE;
                d = 0x10325476;

                for (k = 0; k < x.length; k += 16) {
                    AA = a;
                    BB = b;
                    CC = c;
                    DD = d;
                    a = FF(a, b, c, d, x[k + 0], S11, 0xD76AA478);
                    d = FF(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
                    c = FF(c, d, a, b, x[k + 2], S13, 0x242070DB);
                    b = FF(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
                    a = FF(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
                    d = FF(d, a, b, c, x[k + 5], S12, 0x4787C62A);
                    c = FF(c, d, a, b, x[k + 6], S13, 0xA8304613);
                    b = FF(b, c, d, a, x[k + 7], S14, 0xFD469501);
                    a = FF(a, b, c, d, x[k + 8], S11, 0x698098D8);
                    d = FF(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
                    c = FF(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
                    b = FF(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
                    a = FF(a, b, c, d, x[k + 12], S11, 0x6B901122);
                    d = FF(d, a, b, c, x[k + 13], S12, 0xFD987193);
                    c = FF(c, d, a, b, x[k + 14], S13, 0xA679438E);
                    b = FF(b, c, d, a, x[k + 15], S14, 0x49B40821);
                    a = GG(a, b, c, d, x[k + 1], S21, 0xF61E2562);
                    d = GG(d, a, b, c, x[k + 6], S22, 0xC040B340);
                    c = GG(c, d, a, b, x[k + 11], S23, 0x265E5A51);
                    b = GG(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
                    a = GG(a, b, c, d, x[k + 5], S21, 0xD62F105D);
                    d = GG(d, a, b, c, x[k + 10], S22, 0x2441453);
                    c = GG(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
                    b = GG(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
                    a = GG(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
                    d = GG(d, a, b, c, x[k + 14], S22, 0xC33707D6);
                    c = GG(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
                    b = GG(b, c, d, a, x[k + 8], S24, 0x455A14ED);
                    a = GG(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
                    d = GG(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
                    c = GG(c, d, a, b, x[k + 7], S23, 0x676F02D9);
                    b = GG(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
                    a = HH(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
                    d = HH(d, a, b, c, x[k + 8], S32, 0x8771F681);
                    c = HH(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
                    b = HH(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
                    a = HH(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
                    d = HH(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
                    c = HH(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
                    b = HH(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
                    a = HH(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
                    d = HH(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
                    c = HH(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
                    b = HH(b, c, d, a, x[k + 6], S34, 0x4881D05);
                    a = HH(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
                    d = HH(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
                    c = HH(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
                    b = HH(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
                    a = II(a, b, c, d, x[k + 0], S41, 0xF4292244);
                    d = II(d, a, b, c, x[k + 7], S42, 0x432AFF97);
                    c = II(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
                    b = II(b, c, d, a, x[k + 5], S44, 0xFC93A039);
                    a = II(a, b, c, d, x[k + 12], S41, 0x655B59C3);
                    d = II(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
                    c = II(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
                    b = II(b, c, d, a, x[k + 1], S44, 0x85845DD1);
                    a = II(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
                    d = II(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
                    c = II(c, d, a, b, x[k + 6], S43, 0xA3014314);
                    b = II(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
                    a = II(a, b, c, d, x[k + 4], S41, 0xF7537E82);
                    d = II(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
                    c = II(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
                    b = II(b, c, d, a, x[k + 9], S44, 0xEB86D391);
                    a = AddUnsigned(a, AA);
                    b = AddUnsigned(b, BB);
                    c = AddUnsigned(c, CC);
                    d = AddUnsigned(d, DD);
                }
                var temp = WordToHex(a) + WordToHex(b) + WordToHex(c) + WordToHex(d);
                return temp.toLowerCase();
            }
        };
    })();
    /**
	 * 登录管理
	 */
    JRJ.ssoHead.loginManage = (function () {
        /**
		 * 存储监听事件
		 */
        var events = {};
        /**
		 * 观察者辅助类
		 */
        var Event = function (obj, name) {
            this.obj = obj;// 订阅者
            this.name = name;// 事件名称
            this.listeners = [];// 可以注册多个事件
        };
        var privateUserName = "";
        var privatePassword = "";
        /**
		 * 登录错误时的信息提示
		 */
        var msgBox = {
            "-1": "非法参数。",
            "0": "成功。",
            "1": "用户名或密码错误。",
            "5": "服务内部错误。"
        };
        var ssoLoginUrlPrefix = "https://sso.jrj.com.cn/sso/json/userSession.jsp";
        var ssoUserInfo = "https://sso.jrj.com.cn/sso/json/userInfo.jsp";
        var ssoLoginUrl = "";
        var ssoLoginScriptUrl = "http://i.jrj.com.cn/loginScript.jspa";
        var ssoMsgScriptUrl = "/stock/iservice/message/newMessagesCount.jsp?userID=";
        var welcomeMsg = ['<ul>',
            	            '<li><span class="mo0">您好，</span><span>&nbsp;</span></li>',
                            '<li><a class="username" href="http://i.jrj.com.cn/dispatchSpaceManage.jsp?uid={0}">{1}</a></li>',
                            '<li class="qx">',
                	            '<a href="http://i.jrj.com.cn/dispatchSpaceManage.jsp?uid={0}" class="pos btntop" id="home_lgt_lead">选择去向</a>',
                	            '<div id="userInfoBox" style="display: none;" class="userInfoBox"></div>',
                            '</li>',
                            '<li class="m01"><a href="javascript:;" id="ssoLogout" onclick="JRJ.ssoHead.loginManage.logout(function(){window.location.href=\'/stock/logout.jspa\';});">退出</a><span style="padding-lefT:5px;">|</span></li>',
                        '</ul>',
                        '<div class="nub" id="home_lgt_user_info"><a href="http://i.jrj.com.cn/app/msg/inbox.jspa" id="home_lgt_user_info_msg" class="home_lgt_user_info_msg">消息<b id="home_lgt_user_info_num"></b></a>',
                            '<div id="userInfo-msg-box" class="userInfo-msg-box" style="display:none;">',
                                '<ul>',
                                    '<li><span id="msg-mine" class="jrj-fr"></span><a href="http://i.jrj.com.cn/main/announcements.jspa" target="_blank">公告</a></li>',
                                    '<li><span id="msg-sys" class="jrj-fr"></span><a href="http://i.jrj.com.cn/main/systemMessages.jspa" target="_blank">系统消息</a></li>',
                                    '<li class="last"><span id="msg-site" class="jrj-fr"></span><a href="http://i.jrj.com.cn/main/inbox.jspa" target="_blank">站内信</a></li>',
                                '</ul>',
                            '</div>',
                        '</div>'].join("");
        var userInfoBox = ['<div class="bgnewbox"></div>',
                            '<div class="boxnew" id="boxnew">',
                    	        '<div class="topimgt">',
                        	        '<div class="img"><a href="http://i.jrj.com.cn/dispatchSpaceManage.jsp?uid={0}" onfocus="this.blur()"><img src="{2}" width="50" height="50" /></a></div>',
                                    '<div class="rightbox">',
                            	        '<p class="cent"><a href="http://i.jrj.com.cn/dispatchSpaceManage.jsp?uid={0}">{1}</a></p>',
                            	        '<p><a href="http://i.jrj.com.cn/app/manager/personalinfo.jspa">修改资料</a> <i>|</i> <a href="http://i.jrj.com.cn/app/manager/base.jspa">设置隐私</a> <i>|</i> <a href="http://i.jrj.com.cn/app/msg/inbox.jspa">消息({3})</a></p>',
                                    '</div>',
                                '</div>',
                                '<div class="listul">',
                        	        '<b></b>',
                        	        '<ul>',
                            	        '<li class="list01"><a href="http://i.jrj.com.cn/dispatchSpaceManage.jsp?uid={0}">我的金融界</a></li>',
                                        '<li class="list02"><a href="http://blog.jrj.com.cn/i,{4}.html">我的博客</a></li>',
                                        '<li class="list03"><a href="http://v.jrj.com.cn/">微视频</a></li>',
                                        '<li class="list04"><a href="http://zhibo.jrj.com.cn/">实盘直播</a></li>',
                                        '<li class="list05"><a href="http://sso.jrj.com.cn/sso/ssologin?ReturnURL=http://i.jrj.com.cn/app/stock/group/index.jspa">我的自选股</a></li>',
                                        '<li class="list06"><a href="http://win.jrj.com.cn/">投资广场</a></li>',
                                        '<li class="list09"><a href="http://pay.jrj.com.cn/coin/account.jspa">我的帐户</a></li>',
                                        '<li class="list07"><a href="http://pay.jrj.com.cn/coin/rechage.jspa">金币充值</a></li>',
                                    '</ul>',
                                '</div>	',
                            '</div>'].join("");
        var timerHide;

        Event.prototype = {
            /**
			 * 将事件函数，函数作用域以及函数执行时需要的参数加入监听器中
			 */
            addListener: function (fn, scope, options) {
                if (this.firing) { // 如果正在触发监听事件，则用slice方法创建一个与原对象一样的新对象，这样不会影响正在触发的监听方法链
                    this.listeners = this.listeners.slice(0);
                }
                this.listeners.push({
                    // 事件函数
                    fireFn: fn,
                    // 事件函数上下文
                    scope: scope,
                    // 事件函数的参数
                    options: options
                });
            },
            /**
			 * 执行订阅者订阅的事件
			 */
            fire: function () {
                // 获取fire的所有参数，转换为数组
                var args = Array.prototype.slice.call(arguments, 0, arguments.length),
				// 监听器中待执行事件的个数
				len = this.listeners.length,
				// 某个事件函数
				lis;
                if (len > 0) {
                    this.firing = true;// 标记当前事件正在被触发执行
                    // 遍历监听器中的所有事件并执行
                    for (var i = 0; i < len; i++) {
                        lis = this.listeners[i];
                        // 添加监听时设置的作用域具有最高优先级，其次是当前Event对象的目标对象，都没有就是window对象
                        // 这里的args为fire函数调用时传过来的参数,fire没有传递参数，则采用fireEvent的参数
                        var callArgs = args.length > 0 ? args : lis.options;
                        if (lis && lis.fireFn.apply(lis.scope || this.obj || window, callArgs) === false) {
                            // 如果当前事件函数返回false，则剩余的事件不在执行，返回false，并标记触发标记为false
                            return (this.firing = false);
                        }
                    }
                }
                this.firing = false;
                return true;
            }
        };
        return {
            /**
			 * 添加订阅者
			 * 
			 * @param eventName
			 *            事件名称
			 * @param fn
			 *            事件函数
			 * @param scope
			 *            事件函数的上下文
			 * @param options
			 *            需要传给事件函数的参数
			 */
            addListener: function (eventName, fn, scope, options) {
                // events[eventName]为Event对象的一个实例
                if (!JRJ.ssoHead.util.isDefined(events[eventName])) {
                    events[eventName] = new Event(this, eventName);
                }
                var len = arguments.length;
                var args = len > 3 ? Array.prototype.slice.call(arguments, 3, len) : [];
                events[eventName].addListener(fn, scope, args);
            },
            /**
			 * 发布者通知订阅者
			 * 
			 * @augments 第一个是事件名，之后是监听方法需要的参数（fire函数的参数）
			 * @return ret 进一步执行的标实
			 */
            fireEvent: function (eventName) {
                var args = arguments, ret = true;
                var eventObj = events[args[0]];
                if (JRJ.ssoHead.util.isObject(eventObj)) {
                    // fire返回true和false来确定是否继续
                    ret = eventObj.fire.apply(eventObj, Array.prototype.slice.call(args, 1, args.length));
                }
                return ret;
            },
            /**
			 * 获取dom节点，可缓存
			 */
            getDom: function (id) {
                return document.getElementById(id);
            },
            /**
			 * 用户名和密码校验
			 */
            check: function () {
                if (JRJ.ssoHead.util.isEmpty(this.getDom("LoginID").value)) {
                    alert("用户名不能为空");
                    this.getDom("LoginID").focus();
                    return false;
                } else if (JRJ.ssoHead.util.isEmpty(this.getDom("Passwd").value)) {
                    alert("密码不能为空");
                    this.getDom("Passwd").focus();
                    return false;
                }
                return true;
            },
            /**
			 * 隐藏元素
			 */
            hide: function (id) {
                JRJ.ssoHead.loginManage.getDom(id).style.display = "none";
                return this;
            },
            /**
			 * 显示元素
			 */
            show: function (id) {
                JRJ.ssoHead.loginManage.getDom(id).style.display = "block";
                return this;
            },
            /**
			 * 根据id设置innerHTML
			 */
            html: function (id, content) {
                JRJ.ssoHead.loginManage.getDom(id).innerHTML = content;
                return this;
            },
            /**
			 * 统计id
			 */
            getSpid: function () {
                return (document.domain.indexOf("jrj.com.cn") != -1 || document.domain.indexOf("jrj.com"))
						? document.domain.split(".")[0]
						: document.domain;
            },
            /**
			 * @param userInfo
			 *            {"userName":userJson.userName,"sso_uid":userJson.sso_uid,
			 *            "sso_userID":userJson.sso_userID} 登录后或者退出后更新页面显示状态
			 */
            updateState: function (userInfo) {
                if (JRJ.ssoHead.util.isDefined(userInfo)) {
                    JRJ.ssoHead.loginManage.hide("loginbefore").html("loginafter",
							welcomeMsg.format(userInfo["sso_userID"], userInfo["userName"])).show("loginafter").fireEvent(
							"afterlogin", userInfo);
                    //请求消息数接口
                    $.ajax({
                        url: ssoMsgScriptUrl + userInfo["sso_userID"],
                        //通用的接口，用jsonp跨域.
                        dataType: 'jsonp',
                        type: 'get',
                        cache: false,
                        timeout: 5000,
                        success: function (msgData) {
                            if (msgData['newAnnouncementCount'] >= 0) {
                                $('#msg-mine').text(msgData['newAnnouncementCount'] > 99 ? 99 : msgData['newAnnouncementCount']);
                            }
                            if (msgData['newSystemCount'] >= 0) {
                                $('#msg-sys').text(msgData['newSystemCount'] > 99 ? 99 : msgData['newSystemCount']);
                            }
                            if (msgData['newCommonCount'] >= 0) {
                                $('#msg-site').text(msgData['newCommonCount'] > 99 ? 99 : msgData['newCommonCount']);
                            }
                            var _total = msgData['newAnnouncementCount'] + msgData['newSystemCount'] + msgData['newCommonCount'];
                            if (_total > 0) {
                                $('#home_lgt_user_info_num').text(_total > 99 ? 99 : _total).show();
                            } else {
                                $('#home_lgt_user_info_num').hide();
                            }
                        },
                        complete: function () {
                        }
                    });

                } else {
                    JRJ.ssoHead.loginManage.hide("loginafter").html("loginafter", "").show("loginbefore").fireEvent('afterlogout');

                }
            },
            /**
			 *  显示登录后用户信息窗口
			 */
            showUserInfoBox: function () {

                JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("home_lgt_lead"), "mouseover", function () {
                    clearTimeout(timerHide);
                    JRJ.ssoHead.util.getScript(ssoLoginScriptUrl, function () {
                        JRJ.ssoHead.loginManage.getDom("userInfoBox").innerHTML = userInfoBox.format(jrj_aid, jrj_aname, jrj_hpic, mesCnt.toString(), jrj_sh);
                        JRJ.ssoHead.loginManage.getDom("userInfoBox").style.display = "";
                        JRJ.ssoHead.loginManage.getDom("home_lgt_lead").className = 'pos btntopcur';
                    });
                });

                JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("userInfoBox"), "mouseover", function () {
                    clearTimeout(timerHide);
                    JRJ.ssoHead.loginManage.getDom("userInfoBox").style.display = "";
                });
                JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("home_lgt_lead"), "mouseout", function () {
                    timerHide = window.setTimeout(function () {
                        try{
                            JRJ.ssoHead.loginManage.getDom("userInfoBox").style.display = "none";
                            JRJ.ssoHead.loginManage.getDom("home_lgt_lead").className = 'pos btntop';
                        } catch (e) { }
                    }, 1000);
                });
                JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("userInfoBox"), "mouseout", function () {
                    timerHide = window.setTimeout(function () {
                        try {
                            JRJ.ssoHead.loginManage.getDom("userInfoBox").style.display = "none";
                            JRJ.ssoHead.loginManage.getDom("home_lgt_lead").className = 'pos btntop';
                        } catch (e) { }
                    }, 1000);
                });

                //消息窗口
                $('#home_lgt_user_info_msg').mouseenter(function () {
                    clearTimeout(timerHide);
                    $('#userInfo-msg-box').show();
                    $('#home_lgt_user_info_msg').addClass('home_lgt_user_info_msg-hover');
                });
                $('#userInfo-msg-box').mouseenter(function () {
                    clearTimeout(timerHide);
                    $('#userInfo-msg-box').show();
                    $('#home_lgt_user_info_msg').addClass('home_lgt_user_info_msg-hover');
                }).mouseleave(function () {
                    $('#userInfo-msg-box').hide();
                    $('#home_lgt_user_info_msg').removeClass('home_lgt_user_info_msg-hover');
                });
                $('#userInfo-msg-box ul li').mouseenter(function () {
                    $(this).addClass('hover');
                }).mouseleave(function () {
                    $(this).removeClass('hover');
                });
            },
            /**
			 * 设置用户名和密码
			 */
            setValues: function (v1, v2) {
                privateUserName = v1;
                privatePassword = v2;
                return this;
            },
            /**
			 * 登录系统
			 */
            login: function (callback) {
                this.fireEvent('beforelogin');
                ssoLoginUrl = [ssoLoginUrlPrefix, "?LoginID=", privateUserName, "&Passwd=", JRJ.ssoHead.util.MD5(privatePassword)].join("");
                JRJ.ssoHead.util.getScript(JRJ.ssoHead.util.urlAppend(ssoLoginUrl, "spid=" + JRJ.ssoHead.loginManage.getSpid() + '&_dc=' + JRJ.ssoHead.util.time()), function () {
                    if (userJson.state == 0) {
                        JRJ.ssoHead.loginManage.updateState({
                            "userName": userJson.userName,
                            "sso_uid": userJson.sso_uid,
                            "sso_userID": userJson.sso_userID
                        });

                        JRJ.ssoHead.loginManage.showUserInfoBox();

                        if (typeof callback === "function") {
                            callback();
                        }
                    } else {
                        // 登录异常
                        alert(msgBox[userJson.state]);
                        JRJ.ssoHead.loginManage.fireEvent('loginerror');
                    }
                });
            },
            clearUserLoginInfo: function () {
                JRJ.ssoHead.loginManage.getDom("LoginID").value = "";
                JRJ.ssoHead.loginManage.getDom("Passwd").value = "";
            },
            /**
			 * 退出系统
			 */
            logout: function (callback) {
                var out = function (url) {
                    JRJ.ssoHead.util.getScript(url, function () {
                        JRJ.ssoHead.loginManage.updateState();
                        ssoLoginUrl = "";// 清空登录时url
                        //清空另一种登陆接口userInfo.jsp的全局值
                        if(typeof sso_userID != 'undefined' && typeof sso_uid != 'undefined' && typeof userName != 'undefined'){
                        	sso_userID='';
                            sso_uid='';
                            userName='';
                        }
                        
                        JRJ.ssoHead.loginManage.setValues("", "");
                        // 将登录框清空
                        JRJ.ssoHead.loginManage.clearUserLoginInfo();
                        if (typeof callback === "function") {
                            callback();
                        }
                    });
                }
                if (JRJ.ssoHead.util.isEmpty(ssoLoginUrl)) {// 用户刷新了页面导致ssoLoginUrl为空
                    JRJ.ssoHead.util.getScript(ssoUserInfo, function () {
                        out(JRJ.ssoHead.util.urlAppend(ssoLoginUrlPrefix, ["LoginID=", sso_userID, "&Passwd=",
										JRJ.ssoHead.util.MD5(userName), "&revoke=true&_dc=", JRJ.ssoHead.util.time()].join("")));
                    });
                } else {// 用户未刷新页面
                    out(JRJ.ssoHead.util.urlAppend(ssoLoginUrl, "revoke=true&_dc=" + JRJ.ssoHead.util.time()));
                }
            },
            /**
			 * 头部登录系统的入口，需要对用户名和密码进行校验
			 */
            entry: function () {
                if (JRJ.ssoHead.loginManage.check()) {
                    JRJ.ssoHead.loginManage.setValues(JRJ.ssoHead.loginManage.getDom("LoginID").value,
							JRJ.ssoHead.loginManage.getDom("Passwd").value).login();
                }
            },
            /**
			 * 判断用户是否已经登录
			 */
            init: function () {
                JRJ.ssoHead.util.getScript(ssoUserInfo, function () {
                    if (typeof sso_userID != 'undefined' && typeof userName != 'undefined' && !JRJ.ssoHead.util.isEmpty(sso_userID) && !JRJ.ssoHead.util.isEmpty(userName)) {
                        JRJ.ssoHead.loginManage.updateState({
                            "userName": window.userName,
                            "sso_uid": window.sso_uid,
                            "sso_userID": window.sso_userID
                        });

                        JRJ.ssoHead.loginManage.showUserInfoBox();

                    } else {
                        JRJ.ssoHead.loginManage.show("loginbefore");
                    }
                });
            },
            
            //jrj.sso.loginManage.isLogin()
            isLogin: function (){
            	if (typeof sso_userID != 'undefined' && !JRJ.ssoHead.util.isEmpty(sso_userID) && typeof userName != 'undefined' && !JRJ.ssoHead.util.isEmpty(userName)) {
            		return true;
            	}
            	if (typeof userJson != 'undefined' && userJson.userName && userJson.sso_uid) {
                    return true
                }
        		return false;
            }
        };
    })();
    JRJ.ssoHead.loginManage.on = JRJ.ssoHead.loginManage.addListener;// 简化调用
    DomReady.ready(function () {
        JRJ.ssoHead.loginManage.init();
        JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("Passwd"), "keypress", function (e) {
            e = (e) ? e : ((window.event) ? window.event : "");
            var keyCode = e.keyCode ? e.keyCode : e.which;
            if (keyCode == 13) {
                JRJ.ssoHead.loginManage.entry();
            }
        });
        JRJ.ssoHead.util.bind(JRJ.ssoHead.loginManage.getDom("ssoLogin"), "click", function () {
            JRJ.ssoHead.loginManage.entry();
        });
    });
})(window);
