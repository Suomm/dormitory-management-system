layui.use(['treetable', 'template'], function() {
    var $ = layui.jquery,
        layer = layui.layer,
        table = layui.table,
        treetable = layui.treetable,
        template = layui.template;

    parent.reloadTable = function () {
        treetable.render({
            treeColIndex: 1,
            treeSpid: -1,
            treeIdName: 'taskId',
            treePidName: 'parentId',
            toolbar: '#toolbarImpl',
            elem: '#currentTableId',
            url: '/task/list',
            page: false,
            cols: [[
                {type: 'numbers'},
                {field: 'name', minWidth: 200, title: '周次/宿舍楼'},
                {minWidth: 100, align: 'center', templet: function(task) {
                        if (task.complete) {
                            return '<span class="layui-badge layui-bg-green">完成</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-red">未完成</span>';
                        }
                    }, title: '进度'},
                {field: 'grade', minWidth: 100, align: 'center', title: '年级'},
                {field: 'total', minWidth: 80, align: 'center', title: '房间数'},
                {minWidth: 100, align: 'center', templet: function(task) {
                        if (task.menu) return '';
                        if (task.category === 0) {
                            return '女生宿舍';
                        } else {
                            return '男生宿舍';
                        }
                    }, title: '类型'},
                {minWidth: 100, align: 'center', templet: function(task) {
                        if (task.menu) return '';
                        if (task.type === 1) {
                            return '本科生';
                        } else {
                            return '研究生';
                        }
                    }, title: '类别'},
                {templet: '#currentTableBar', width: 200, align: 'center', title: '操作'}
            ]],
            done: function() {
                treetable.foldAll('#currentTableId');
            }
        });
    };
    parent.reloadTable();

    // 渲染表格
    table.on('toolbar(currentTableFilter)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
        switch (obj.event) {
            case 'expand':
                treetable.expandAll('#currentTableId');
                break;
            case 'fold':
                treetable.foldAll('#currentTableId');
                break;
            case 'set':
                template.show('page/add-edit/setting.html', '设置任务');
                break;
            case 'add':
                $.get("/task/get/week", function (rep) {
                    if (rep.code === -1) {
                        layer.msg(rep.msg, {icon: 5, shift: 6});
                    } else {
                        template.show('page/add-edit/task.html', '添加任务');
                        $('#week').append(rep.msg);
                    }
                });
                break;
        };
    });

    //监听工具条
    table.on('tool(currentTableFilter)', function(obj) {
        if (obj.event === 'delete') {
            layer.confirm('真的删除么', function(index) {
                $.ajax({
                    type: 'delete',
                    url: '/task/delete/' + obj.data.id,
                    success: function (status) {
                        if (status > 0) {
                            layer.msg("操作成功", {icon: 1});
                            parent.reloadTable();
                        } else {
                            layer.msg("操作失败", {icon: 5, shift: 6});
                        }
                    }
                });
                layer.close(index);
            });
        } else if (obj.event === 'details') {
            $.get("/task/details/" + obj.data.id, function (rep) {
                if (rep.code === -1) {
                    layer.msg(rep.msg, {icon: 5, shift: 6});
                } else {
                    template.show('page/manager/templet.html', '任务详情');
                    var $list = $('#list');
                    rep.data.forEach(function (v) {
                        $list.append(`<a href="${v}" class="layui-btn">${v.substring(v.lastIndexOf('/'), v.lastIndexOf('.') - 1)}</a>`)
                    });
                }
            });
        } else if (obj.event === 'reset') {
            layer.confirm('真的重置么', function(index) {
                $.ajax({
                    type: 'put',
                    url: '/task/rollback/' + obj.data.id,
                    success: function (status) {
                        if (status) {
                            layer.msg("操作成功", {icon: 1});
                            parent.reloadTable();
                        } else {
                            layer.msg("操作失败", {icon: 5, shift: 6});
                        }
                    }
                });
                layer.close(index);
            });
        }
    });
});
