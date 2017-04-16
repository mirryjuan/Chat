mui.init({
	pullRefresh: {
		container: '#refreshContainer',
		down: {
			callback: pulldownRefresh
		}
	}
});
/**
 * 下拉刷新具体业务实现
 */
function pulldownRefresh() {
	setTimeout(function() {
		var table = document.body.querySelector('.mui-table-view');
		var cells = document.body.querySelectorAll('.mui-table-view-cell');
		for (var i = cells.length, len = i + 3; i < len; i++) {
			var li = document.createElement('li');
			li.className = 'mui-table-view-cell';
			li.innerHTML = '<a class="mui-navigate-right">Item ' + (i + 1) + '</a>';
			//下拉刷新，新纪录插到最前面；
			table.insertBefore(li, table.firstChild);
		}
//		for (var i = cells.length, len = data.length; i < len; i++) {
//			var li = document.createElement('li');
//			li.className = 'mui-table-view-cell';
//			
//			window.getTpl('newsTemp').render(data[i], function(html) {
//		    	table.insertBefore(li, table.firstChild);
//			});
//		
//		}
		mui('#refreshContainer').pullRefresh().endPulldownToRefresh(); //refresh completed
	}, 1500);
}