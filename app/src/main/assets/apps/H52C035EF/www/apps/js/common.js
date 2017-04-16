(function($) {
	$.init();
	$.plusReady(function() {
        window.activity = plus.android.runtimeMainActivity();
	});
})(mui);

window.onerror = dieError;
function dieError(msg, url, line) {
	var txt = "ERROR: " + msg + "\n";
	txt += "URL: " + url + "\n";
	txt += "LINE: " + line + "\n";
	alert(txt);
	return true;
}

var back = document.getElementsByClassName("back");
for(var i = 0; i < back.length ; i++){
	back[i].addEventListener('tap',function(){
		plus.android.invoke(window.activity,"backToActivity");
	});
}