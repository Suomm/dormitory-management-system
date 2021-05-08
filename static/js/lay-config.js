window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

layui.config({
    base: rootPath,
    version: true
}).extend({
    "miniAdmin": "lay-module/layuimini/miniAdmin.min",
    "miniMenu": "lay-module/layuimini/miniMenu.min",
    "miniPage": "lay-module/layuimini/miniPage.min",
	"miniTheme": "lay-module/layuimini/miniTheme.min",
    "step": 'lay-module/step-lay/step.min',
    "treetable": 'lay-module/treetable-lay/treetable.min',
	"template": 'lay-module/templet-lay/template.min',
	"jquery.cookie": 'jq-module/jquery.cookie.min',
	"jquery.particleground": 'jq-module/jquery.particleground.min',
});

layui.use(['layer', 'jquery.cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer;
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader("X-XSRF-TOKEN", $.cookie('XSRF-TOKEN'));
        },
        error: function(xhr, status, error) {
            layer.closeAll('loading');
            switch (xhr.status) {
                case 400:
                    layer.msg(xhr.responseText, {icon: 5, shift: 6});
                    break;
                case 401:
                    layer.alert(xhr.responseText, {icon: 5}, function () {
                        window.location.href = "/login.html";
                    });
                    break;
                case 403:
                    layer.alert("您的权限不足", {icon: 5});
                    break;
                case 500:
                    // TODO 解决消息不显示问题
                    layer.alert(xhr.responseJSON.message, {icon: 5});
                    break;
            }
        }
    });
});