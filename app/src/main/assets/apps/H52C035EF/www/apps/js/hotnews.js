(function($) {
	$.init();
	$.plusReady(function() {
	    var settings = {
            KEY: 'afd68648ac1e4105938196aa9e8970d1'
        }
        mui.post('http://api.avatardata.cn/ActNews/LookUp',{
                key: settings["KEY"]
            },function(data){
                if(data.error_code == 0){
                    if(!!data.result){
                         for(var i = 0; i < data.result.length; i++){
                             document.getElementById('container').innerHTML += (data.result[i]+"</br>");
                         }
                    }
                }
            },'json'
        );
	});
})(mui);