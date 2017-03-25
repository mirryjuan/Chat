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
		for (var i = cells.length, len = i  + 3; i < len; i++) {
			var li = document.createElement('li');
			li.className = 'mui-table-view-cell mui-media';
			li.innerHTML = '<a class="mui-navigate-right">Item ' + (i + 1) + '</a>';
			//下拉刷新，新纪录插到最前面；
			table.insertBefore(li, table.firstChild);
		}
//		
//		var createFragment = function(count) {
//			var fragment = document.createDocumentFragment();
//			var li;
//			for (var i = 0; i < count; i++) {
//				li = document.createElement('li');
//				li.className = 'mui-table-view-cell mui-media';
//				li.innerHTML = '<a class="mui-navigate-right"><img class="mui-media-object mui-pull-left" data-lazyload="http://www.dcloud.io/hellomui/images/' + (i % 5 + 1) + '.jpg?version=' + Math.random() * 1000 + '"><div class="mui-media-body">主标题<p class="mui-ellipsis">列表二级标题</p></div></a>';
//				fragment.appendChild(li);
//			}
//			return fragment;
//		};
//		(function($) {
//			var list = document.getElementById("list");
//			list.appendChild(createFragment(50));
//			$(document).imageLazyload({
//				placeholder: '../images/60x60.gif'
//			});
//		})(mui);
		
		
		
		
		
		
//		for (var i = cells.length, len = data.length; i < len; i++) {
//			var li = document.createElement('li');
//			li.className = 'mui-table-view-cell mui-media';
//			
//			window.getTpl('newsTemp').render(data[i], function(html) {
//		    	li.innerHTML = html;
//			});
//			table.insertBefore(li, table.firstChild);
//		}
		mui('#refreshContainer').pullRefresh().endPulldownToRefresh(); //refresh completed
	}, 1500);
}