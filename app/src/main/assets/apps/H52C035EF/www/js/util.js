// 模板缓存
window.tplCache = {};
// 获取模板对象
function getTpl(templeteId) {
	var templateContainer = document.getElementById('templateContainer');
	if (!templateContainer) {
		mui.ajax('../template.html', {
			async : false,
			dataType : 'text',// 服务器返回text格式数据
			success : function(data) {
				mui('body')[0].appendChild(str2DOMElement(data));
			}
		});
	}
	var result = window.tplCache[templeteId];
	if (!!result) {
		return result;
	}
	var templete = document.getElementById(templeteId).innerHTML;
	result = window.tplCache[templeteId] = laytpl(templete);
	return result;
}

// 字符串转dom对象
function str2DOMElement(html) {
	var frame = document.createElement('iframe');
	frame.style.display = 'none';
	document.body.appendChild(frame);
	frame.contentDocument.open();
	frame.contentDocument.write(html);
	frame.contentDocument.close();
	var el = frame.contentDocument.body.firstChild;
	document.body.removeChild(frame);
	return el;
}