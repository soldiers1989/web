var ChartUtil = function (settings) {

    this.settings = $.extend({
        swfUrl: 'flash/FCF_Pie3D.swf',
        chartId: 'chartId',
        chartDivId: 'chartdiv',
        width: 500,
        height: 500,
        noDataHtml:"",
        noDataCss:"",
        xmlUrl: 'data/Pie3D.xml'
    }, settings);

};

ChartUtil.prototype = {
    show: function () {

        var _this = this;

        function xmlToString(xmlData) {
            var xmlString;
            if (window.ActiveXObject) {
                xmlString = xmlData.xml;
            }
            else {
                xmlString = (new XMLSerializer()).serializeToString(xmlData);
            }
            return xmlString;
        }

        $.ajax({
            type: "GET",
            scriptCharset: 'utf-8',
            dataType: "xml",
            url: this.settings.xmlUrl,
            success: function (data) {
        			if(data.getElementsByTagName("nodata").length > 0 && data.getElementsByTagName("nodata")[0].firstChild.nodeValue ==1){
        				$("#"+_this.settings.chartDivId).html(_this.settings.noDataHtml);
        				$("#"+_this.settings.chartDivId).addClass(_this.settings.noDataCss);
        			}else{
        				var chart = new FusionCharts(_this.settings.swfUrl, _this.settings.chartId, _this.settings.width, _this.settings.height);
                        chart.setDataXML(encodeURI(xmlToString(data)));
                        chart.render(_this.settings.chartDivId);
        			}   
            }
        });
    }
};

