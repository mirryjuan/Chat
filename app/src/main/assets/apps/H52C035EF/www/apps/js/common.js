window.onerror = dieError;
function dieError(msg, url, line) {
	var txt = "ERROR: " + msg + "\n";
	txt += "URL: " + url + "\n";
	txt += "LINE: " + line + "\n";
	alert(txt);
	return true;
}

