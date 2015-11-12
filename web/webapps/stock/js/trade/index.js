/**
 * 
 */
$(document).ready(function(){    
    $(".prompt-close").click(function(){
		$(this).parent(".prompt-txt").fadeOut();
	});

	var totalWidth = 0;
        $('.assets').find('span').each(function () {
            totalWidth += $(this).outerWidth();
        });
        if ($('.assets').outerWidth() < totalWidth) {
            $('.assets').addClass('assets-s2').find('span:odd').addClass('noline');
        }
})

















