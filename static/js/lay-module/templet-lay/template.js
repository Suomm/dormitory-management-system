layui.define(['jquery', 'layer', 'form', 'table', 'miniPage'], function(exports) {
	var	$ = layui.jquery,
		form  = layui.form,
	 	layer = layui.layer,
		table = layui.table,
		miniPage = layui.miniPage;
	// 选中行进行编辑的数据
	var templet = null;
	// 添加编辑操作打开的窗口
	var open = function(url, title) {
		var content = miniPage.getHrefContent(url);
		var openWH = miniPage.getOpenWidthHeight();
		layer.open({
			title: title,
			type: 1,
			shade: 0.2,
			maxmin: true,
			shadeClose: true,
			area: [openWH[0] + 'px', openWH[1] + 'px'],
			offset: [openWH[2] + 'px', openWH[3] + 'px'],
			content: content,
			end: function () {
				table.reload('currentTableId');
			}
		});
	};
	// 操作提示
	var tip = function (status) {
		status ? layer.msg("操作成功", {icon: 1}) : layer.msg("操作失败", {icon: 5, shift: 6});
	};
	// 模板对象
	var template = {
		// 渲染数据表格
		render: function(param) {
			param.page = true; // 开启数据表格分页
			param.cellMinWidth = 80; // 设置数据表格默认单元格宽度
			param.toolbar = 'default'; // 开启默认工具栏
			param.elem = '#currentTableId'; // 渲染的表格组件
			param.limits = [10, 15, 20, 25, 50, 100]; // 设置每页显示数据的数量
			param.request = {
				pageName: 'current', // 页码的参数名称，默认：page
				limitName: 'size' // 每页数据量的参数名，默认：limit
			};
			param.parseData = function(res){ // res 即为原始返回的数据
				return {
					"code": 0, //解析接口状态
					"msg": "", //解析提示文本
					"count": res.total, //解析数据长度
					"data": res.records //解析数据列表
				};
			};
			form.render(); // 渲染表单控件
			table.render(param); // 渲染数据表格
		},
		// 表单数据校验
		verify: function () {
			form.verify({
				id: [
					/^\d{2}3007\d{4}$/,
					'学号格式不正确'
				],
				password: [
					/^[\S]{6,32}$/,
					'密码必须6到32位，且不能出现空格'
				],
				building: [
					/^学生公寓\s?\d+\s?号楼$/,
					'宿舍楼格式不正确'
				],
				grade: [
					/^\d{4}\s?级$/,
					'年级格式不正确'
				]
			});
		},
		// 保存或更新数据
		save: function(url, parentIndex) {
			// 初始化表单
			form.render();
			// 表单赋值
			form.val('example', templet);
			// 赋值完毕置对象为空
			templet = null;
			// 监听提交
			form.on('submit(saveBtn)', function(data) {
				layer.load();
				$.post(url, data.field, function (status) {
					layer.closeAll('loading');
					tip(status);
					if (parentIndex) layer.close(parentIndex);
				});
				return false;
			});
		},
		// 添加编辑操作打开的窗口
		show: open,
		// 监听搜索操作
		search: function(param) {
			form.on('submit(data-search-btn)', function (data) {
			    var result = JSON.stringify(data.field);
			    //执行搜索重载
			    table.reload('currentTableId', {
			        page: {
			            curr: 1
			        },
					where: {
			        	params: result
					}
			    }, 'data');
			    return false;
			});
		},
		// 绑定通用事件
		binding: function(param) {
			//监听头工具栏事件
			table.on('toolbar(currentTableFilter)', function(obj) {
				var checkStatus = table.checkStatus(obj.config.id),
					data = checkStatus.data; //获取选中的数据
				switch (obj.event) {
					case 'add':
						open(param.url, param.add);
						break;
					case 'update':
						if (data.length === 0) {
							layer.msg('请选择一行');
						} else if (data.length > 1) {
							layer.msg('只能同时编辑一个');
						} else {
							templet = data[0];
							open(param.url, param.edit);
						}
						break;
					case 'delete':
						if (data.length === 0) {
							layer.msg('请选择一行');
						} else {
							layer.confirm('真的删除行么', function(index) {
								var request = param.removeAll(data);
								request.type = 'delete';
								request.contentType = "application/json";
								request.success = function(text, status, jqXHR) {
									tip(text);
									table.reload('currentTableId');
								}
								$.ajax(request);
								layer.close(index);
							});
						}
						break;
				};
			});
			table.on('tool(currentTableFilter)', function(obj) {
				if (obj.event === 'edit') {
					templet = obj.data;
					open(param.url, param.edit);
					return false;
				} else if (obj.event === 'delete') {
					layer.confirm('真的删除行么', function(index) {
						var request = param.remove(obj.data);
						request.type = 'delete';
						request.contentType = "application/json";
						request.success = function(text, status, jqXHR) {
							tip(text);
							table.reload('currentTableId');
						}
						$.ajax(request);
						layer.close(index);
					});
				}
			});
		}
	};
	//暴露接口
	exports('template', template);
});