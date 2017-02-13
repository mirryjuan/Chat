
document.getElementById('scan').addEventListener('click',scan);
document.getElementById('robot').addEventListener('click',getRobot);
document.getElementById('record').addEventListener('click',getRecord);
document.getElementById('news').addEventListener('click',getNews);
document.getElementById('weather').addEventListener('click',getWeather);
document.getElementById('share').addEventListener('click',shareApp);

function scan(){
	mui.openWindow({
		url:'apps/html/scan.html',
		id:'scan'
	});
}

function getRobot(){
	mui.openWindow({
		url:'apps/html/robot.html',
		id:'robot'
	});
}

function getRecord(){
	mui.openWindow({
		url:'apps/html/record.html',
		id:'record'
	});
}

function getNews(){
	mui.openWindow({
		url:'apps/html/news.html',
		id:'news'
	});
}

function getWeather(){
	mui.openWindow({
		url:'apps/html/weather.html',
		id:'weather'
	});
}

function shareApp(){
	mui.openWindow({
		url:'apps/html/share.html',
		id:'share'
	});
}

