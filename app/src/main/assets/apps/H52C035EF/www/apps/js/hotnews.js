(function($) {
	$.init();
	$.plusReady(function() {
	    var settings = {
            KEY: '3aad37fa8343b585cf3c9d0c55f51f05',
            TYPE: 'top'
        }
        mui.post('http://v.juhe.cn/toutiao/index',{
                type: settings["TYPE"],
                key: settings["KEY"]
            },function(data){
                if(data.error_code == 0){
                    var gettpl = document.getElementById('newsTemp').innerHTML;
                    if(!!data.result){
                         for(var i = 0; i < data.result.data.length; i++){
                             laytpl(gettpl).render(data.result.data[i], function(html){
                                 document.getElementById('container').appendChild(str2DOMElement(html));
                             });

                             var more = data.result.data[i].url+"_more";
                             document.getElementById(more).addEventListener('tap',function(c){
                                 startUrl(this.id);
                             },false);

                         }
                    }
                }
            },'json'
        );
	});
})(mui);

var $j = jQuery.noConflict();


function startUrl(mId){
    var pagePath = mId.split("_")[0];
    if (!!pagePath) {
        window.location.href = pagePath;
    }
}