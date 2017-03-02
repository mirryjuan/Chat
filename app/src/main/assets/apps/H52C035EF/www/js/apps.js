(function($) {
	$.init();
	$.plusReady(function() {
        var pagePath = getPagePath();
        if (!!pagePath) {
            window.location.href = pagePath;
        } else {
             document.write('待实现的资源页面:');
        }
	});
})(mui);

function getPagePath(){
    var basePath = 'file:///android_asset/apps/H52C035EF/www/';
    var subPath = 'apps/html/news.html';
    return basePath + subPath;
}
