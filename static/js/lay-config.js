/*
 * Copyright (C) 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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