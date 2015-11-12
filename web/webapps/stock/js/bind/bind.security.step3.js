$(function() {
	$('#buyStock').click(function() {
		var href = $(this).data('href');
		var new_href = decodeURI(href);
		window.location.replace(new_href);
	});
})