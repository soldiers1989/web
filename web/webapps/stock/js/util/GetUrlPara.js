 var QueryString = function() {
		var query_string = {};
		var query = window.location.search.substring(1);
		var vars = query.split("&");
		for (var i = 0; i < vars.length; i++) {
			var pair = vars[i].split("=");
			if (typeof query_string[pair[0]] === "undefined") {
				query_string[pair[0]] = pair[1];
			} else if (typeof query_string[pair[0]] === "string") {
				var arr = [ query_string[pair[0]], pair[1] ];
				query_string[pair[0]] = arr;
			} else {
				query_string[pair[0]].push(pair[1]);
			}
		}
		return query_string;
	}();
	function getParameter(parameter) {
		if (isEmpty(QueryString[parameter])) {
			return $.cookie('_' + parameter) || "";
		} else {
			if (QueryString[parameter] !== 'from') {
				$.cookie('_' + parameter, QueryString[parameter], {
					expires : 10,
					path : '/'
				});
			} else {
				$.cookie('_' + parameter, QueryString[parameter], {
					path : '/'
				});
			}
			return QueryString[parameter];
		}
	}
	function isEmpty(v, allowBlank) {
		return v === null || v === undefined || !v.length
				|| (!allowBlank ? v === '' : false);
	}
    function urlAppend(url, s) {
    	if (!isEmpty(s)) {
    		return url + (url.indexOf('&') === -1 ? (url.indexOf('?') == -1 ? '?':'&') : '&') + s;
    	}
    	return url;
    }