(function($) {
	$.init();
	$.plusReady(function() {
          var settings = {
                KEY: '8b205580e5f7409a8f881b1c1d1d6482',
                CITY: '石家庄'
          }
          mui.post('http://api.avatardata.cn/Weather/Query',{
                    key: settings["KEY"],
                    cityName: settings["CITY"]
                },function(data){
          		    if(data.error_code == 0){
                        if(!!data.result){
//                            window.getTpl('weatherTemp').render(data.result, function(html) {
//                                document.getElementById('container').appendChild(str2DOMElement(html));
//                            });
                            var gettpl = document.getElementById('weatherTemp').innerHTML;
                            laytpl(gettpl).render(data.result, function(html){
                                document.getElementById('container').appendChild(str2DOMElement(html));
                            });
                        }
          		    }
          	    },'json'
          );
	});
})(mui);
