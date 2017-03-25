(function($) {
	$.init();
	$.plusReady(function() {
         var args = JSON.parse(plus.runtime.arguments);
         var mUrl = console.log(args["url"]);
         mui.openWindow({
            url:mUrl;
         });
//        var mId = "004";
//        var pagePath = getPagePath(mId);
//        if (!!pagePath) {
//            window.location.href = pagePath;
//        } else {
//             document.write('待实现的资源页面:');
//        }
	});
})(mui);
//
//var appsPath = {
//    "001": "apps/html/scan.html",
//    "002": "apps/html/robot.html",
//    "003": "apps/html/record.html",
//    "004": "apps/html/news.html",
//    "005": "apps/html/weather.html",
//    "006": "apps/html/share.html",
//}
//
//function getPagePath(mId){
//    var basePath = "file:///android_asset/apps/H52C035EF/www/";
//    var subPath = appsPath[mId];
//    return basePath + subPath;
//}
