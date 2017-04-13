(function($) {
	$.init();
	$.plusReady(function() {
         var args = plus.runtime.arguments;
         alert(args);
         var pagePath = getPagePath(args);
         if (!!pagePath) {
             window.location.href = pagePath;
         }
	});
})(mui);

function getPagePath(args){
    var appsPath = {
        "001": "apps/html/scan.html",
        "002": "apps/html/robot.html",
        "003": "apps/html/record.html",
        "004": "apps/html/news.html",
        "005": "apps/html/weather.html",
        "006": "apps/html/share.html",
    }
    var basePath = "file:///android_asset/apps/H52C035EF/www/";
    var subPath = appsPath[args];
    return basePath + subPath;
}
