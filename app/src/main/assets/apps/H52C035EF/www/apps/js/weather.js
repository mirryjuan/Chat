(function($) {
	$.init();
	$.plusReady(function() {
//        var args = "005";
//        var resultStr = plus.android.invoke(window.activity, "getData", args);
//        alert(resultStr);
//        resultStr = JSON.parse(resultStr);
//        var data = resultStr.result;
//        alert(data.content);
          var settings = {//定义了两个常量
                KEY: '8b205580e5f7409a8f881b1c1d1d6482',
                CITY: '石家庄'
          }
          mui.post('http://api.avatardata.cn/Weather/Query',{
                    key: settings["KEY"],
                    cityName: settings["CITY"]
                },function(data){
          		//服务器返回响应，根据响应结果，分析是否登录成功；
          		    if(data.error_code == 0){
                        if(!!data.result){
                              document.getElementById('container').innerHTML = data.result.realtime.weather.info;
                        //                for (var i = 0 ; i < weatherData.length; i++) {
                        //                      var li = document.createElement('li');
                        //                      li.className = 'mui-table-view-cell';
                        //
                        //                      window.getTpl('weatherTemp').render(weatherData[i], function(html) {
                        //                          table.insertBefore(li, table.firstChild);
                        //                      });
                        //                }
                        }
          		    }
          	    },'json'
          );
	});
})(mui);
