$(document).ready(function(){
    $('.radio-wrap .radio').click(function () {
        if (!$(this).parent().parent().hasClass('radio-wrap-selected')) {
            $(this).parent().parent().addClass('radio-wrap-selected').siblings().removeClass('radio-wrap-selected');
        }
    });
    $('.custom-checkbox li').mouseenter(function () {
        if (!$(this).hasClass('checked')) {
            $(this).addClass('hover');
        }
    }).mouseleave(function () {
        $(this).removeClass('hover');
    }).click(function () {
        $(this).addClass('checked').siblings().removeClass('checked');
    });
    
    var tab = new JRJ.ui.TabSwitch({
        menuPre: "menua_",
        conPre: "cona_",
        total:2,
        onClass: "cur",
        eventType: 'click'
    });
});