(function($) {
	$.init();
	$.plusReady(function() {
	    var settings = {
            KEY: 'ebc4607ee9e348ccb1bec4fa39c4699d',
            ROWS: 10
        }
        mui.post('http://api.avatardata.cn/Joke/NewstJoke',{
                key: settings["KEY"],
                rows: settings["ROWS"]
            },function(data){
                if(data.error_code == 0){
                    if(!!data.result){
                        for(var i = 0; i < settings["ROWS"]; i++){
                            document.getElementById('container').innerHTML += (data.result[i].content+"</br>");
                        }
                    }
                }
            },'json'
        );
	});
})(mui);