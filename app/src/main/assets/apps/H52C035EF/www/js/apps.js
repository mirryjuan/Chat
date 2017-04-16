(function($) {
	$.init();
	$.plusReady(function() {
         var args = plus.runtime.arguments;
//         var curTitle = getTitle(args);
//         document.getElementById('title').innerHTML = curTitle;

         var pagePath = getPagePath(args);

         if (!!pagePath) {
             $j("#container").load(pagePath);
         }
	});
})(mui);

var $j = jQuery.noConflict();

function getPagePath(args){
    var appsPath = {
        "001": "apps/html/scan.html",
        "002": "apps/html/robot.html",
        "003": "apps/html/record.html",
        "004": "apps/html/news.html",
        "005": "apps/html/weather.html"
    }
    var basePath = "file:///android_asset/apps/H52C035EF/www/";
    var subPath = appsPath[args];
    return basePath + subPath;
}


function getTitle(args){
    var title = {
        "001" : "扫一扫",
        "002" : "聊天机器人",
        "003" : "心情日志",
        "004" : "新闻",
        "005" : "天气"
    }
    return title[args];
}