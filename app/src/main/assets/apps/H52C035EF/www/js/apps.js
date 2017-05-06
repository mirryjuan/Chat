(function($) {
	$.init();
	$.plusReady(function() {
	    window.activity = plus.android.runtimeMainActivity();

        var args = plus.runtime.arguments;
        var curTitle = getTitle(args);
        document.getElementById('title').innerHTML = curTitle;

        var back = document.getElementById('back');
        back.className = 'iconfont icon-back';
        back.addEventListener('tap',function(){
            plus.android.invoke(window.activity,"backToActivity");
 	 	});

//        var pagePath = getPagePath(args);
//        if (!!pagePath) {
//            $j("#container").load(pagePath);
//        }

        var jsPath = getJsPath(args);
        if(!!jsPath){
            $j.getScript(jsPath);
        }
	});
})(mui);

var $j = jQuery.noConflict();

function getJsPath(args){
    var jsPath = {
        "001": "apps/js/scan.js",
        "002": "apps/js/robot.js",
        "003": "apps/js/record.js",
        "004": "apps/js/hotnews.js",
        "005": "apps/js/weather.js",
        "006": "apps/js/joke.js"
    }
    var basePath = "file:///android_asset/apps/H52C035EF/www/";
    var subPath = jsPath[args];
    return basePath + subPath;
}

//function getPagePath(args){
//    var appsPath = {
//        "001": "apps/html/scan.html",
//        "002": "apps/html/robot.html",
//        "003": "apps/html/record.html",
//        "004": "apps/html/news.html",
//        "005": "apps/html/weather.html",
//        "006": "apps/html/joke.html"
//    }
//    var basePath = "file:///android_asset/apps/H52C035EF/www/";
//    var subPath = appsPath[args];
//    return basePath + subPath + " #content";
//}


function getTitle(args){
    var title = {
        "001" : "扫一扫",
        "002" : "聊天机器人",
        "003" : "心情日志",
        "004" : "新闻",
        "005" : "天气",
        "006" : "笑话大全"
    }
    return title[args];
}

window.onerror = dieError;
function dieError(msg, url, line) {
	var txt = "ERROR: " + msg + "\n";
	txt += "URL: " + url + "\n";
	txt += "LINE: " + line + "\n";
	alert(txt);
	return true;
}