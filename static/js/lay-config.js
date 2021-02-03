window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

layui.config({
    base: rootPath,
    version: true
}).extend({
    "miniAdmin": "lay-module/layuimini/miniAdmin",
    "miniMenu": "lay-module/layuimini/miniMenu",
    "miniPage": "lay-module/layuimini/miniPage",
	"miniTheme": "lay-module/layuimini/miniTheme",
    "step": 'lay-module/step-lay/step',
    "treetable": 'lay-module/treetable-lay/treetable',
	"template": 'lay-module/templet-lay/template',
	"jquery.cookie": 'jq-module/jquery.cookie',
	"jquery.particleground": 'jq-module/jquery.particleground',
});

layui.use('jquery.cookie', function () {
    var $ = layui.jquery;
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader("X-XSRF-TOKEN", $.cookie('XSRF-TOKEN'));
        },
        error: function(xhr, status, error) {
            switch (xhr.status) {
                case 400:
                    layer.msg(xhr.responseText, {icon: 5, shift: 6});
                    break;
                case 500:
                    layer.msg(xhr.responseJSON.message, {icon: 5, shift: 6});
                    break;
            }
        }
    });
});
