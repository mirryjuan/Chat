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
                            var gettpl = document.getElementById('weatherTemp').innerHTML;
                            laytpl(gettpl).render(data.result, function(html){
                                document.getElementById('container').appendChild(str2DOMElement(html));
                            });

                            var charts = document.getElementById('weatherChart');
                            var weatherChart = echarts.init(charts);
                            var future = data.result.weather;

                            var weeks = [];
                            var tempHight = [];
                            var tempLow = [];
                            for(var i = 0; i < future.length; i++){
                                weeks.push("周"+ future[i].week);
                                tempHight.push(future[i].info.day[2]);
                                tempLow.push(future[i].info.night[2]);
                            }

                            var option = {
                                xAxis:  {
                                    type: 'category',
                                    axisLine: {
                                        show: false
                                    },
                                    axisTick: {
                                        show: false
                                    },
                                    boundaryGap: false,
                                    data: weeks
                                },
                                yAxis: {
                                    show: false
                                },
                                series: [
                                    {
                                        name:'最高气温',
                                        type:'line',
                                        data:tempHight,
                                        markPoint: {
                                            symbol: 'circle',
                                            symbolSize: 25,
                                            data: [
                                                {xAxis: 0, yAxis: tempHight[0]},
                                                {xAxis: 1, yAxis: tempHight[1]},
                                                {xAxis: 2, yAxis: tempHight[2]},
                                                {xAxis: 3, yAxis: tempHight[3]},
                                                {xAxis: 4, yAxis: tempHight[4]},
                                                {xAxis: 5, yAxis: tempHight[5]},
                                                {xAxis: 6, yAxis: tempHight[6]}
                                            ]
                                        }
                                    },
                                    {
                                        name:'最低气温',
                                        type:'line',
                                        data:tempLow,
                                        markPoint: {
                                            symbol: 'circle',
                                            symbolSize: 25,
                                            data: [
                                                {xAxis: 0, yAxis: tempLow[0]},
                                                {xAxis: 1, yAxis: tempLow[1]},
                                                {xAxis: 2, yAxis: tempLow[2]},
                                                {xAxis: 3, yAxis: tempLow[3]},
                                                {xAxis: 4, yAxis: tempLow[4]},
                                                {xAxis: 5, yAxis: tempLow[5]},
                                                {xAxis: 6, yAxis: tempLow[6]}
                                            ]
                                        }
                                    }
                                ]
                            };

                            weatherChart.setOption(option);
                        }
          		    }
          	    },'json'
          );
	});
})(mui);



