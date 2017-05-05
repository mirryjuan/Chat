(function($) {
	$.init();
	$.plusReady(function() {
//        var args = "005";
//        var resultStr = plus.android.invoke(window.activity, "getData", args);
//        alert(resultStr);
//        resultStr = JSON.parse(resultStr);
//        var data = resultStr.result;
//        alert(data.content);
          var weatherData = null;
          mui.post('http://api.avatardata.cn/Weather/Query',{
          		key:'8b205580e5f7409a8f881b1c1d1d6482',
          		cityName:'石家庄'
                },function(data){
          		//服务器返回响应，根据响应结果，分析是否登录成功；
          		    weatherData = data.result.data;
          	    },'json'
          );

          if(!!weatherData){
                for (var i = 0 ; i < weatherData.length; i++) {
                      var li = document.createElement('li');
                      li.className = 'mui-table-view-cell';

                      window.getTpl('weatherTemp').render(weatherData[i], function(html) {
                          table.insertBefore(li, table.firstChild);
                      });
                }
          }
	});
})(mui);
