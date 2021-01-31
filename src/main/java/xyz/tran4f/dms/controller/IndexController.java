/*
 * Copyright (C) 2020-2021 the original author or authors.
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

package xyz.tran4f.dms.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import xyz.tran4f.dms.pojo.Menu;

import java.util.*;

/**
 * <p>
 * 加载 Layuimini 时所用到的 API。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@ApiIgnore
@RestController
public class IndexController {

    @GetMapping("/api/clear")
    public Map<String, Object> clear() {
        System.gc(); // 显式调用垃圾回收器
        Map<String, Object> ret = new HashMap<>();
        ret.put("code", 1);
        ret.put("msg", "服务端清理缓存成功");
        return ret;
    }

    @GetMapping("/api/init")
    public Map<String, Object> init() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> home = new HashMap<>();
        Map<String, Object> logo = new HashMap<>();
        List<Menu> menu = new ArrayList<>();

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .toArray()[0].toString();

        menu.add(Menu.builder().href("").title("操作").child(Arrays.asList(
                new Menu("宿舍检查", "fa fa-calendar", "page/user/inspect.html"),
                new Menu("周报制作", "fa fa-file-text", "page/user/publish.html"),
                new Menu("模板下载", "fa fa-cloud-download", "page/user/download.html")
        )).icon("fa fa-address-book").build());

        switch (role) {
            case "ROLE_MANAGER":
            case "ROLE_ROOT":
                menu.add(Menu.builder().href("").title("管理").child(Arrays.asList(
                        new Menu("用户管理", "fa fa-dot-circle-o", "page/manager/user.html"),
                        new Menu("宿舍管理", "fa fa-building", "page/manager/dormitory.html"),
                        new Menu("任务管理", "fa fa-window-maximize", "page/manager/task.html"),
                        new Menu("公告管理", "fa fa-tags", "page/manager/notice.html")
                )).icon("fa fa-lemon-o").build());
                break;
        }

        home.put("title", "首页");
        home.put("href", "welcome.html");

        logo.put("title", "权益保障部");
        logo.put("image", "images/logo.png");
        logo.put("href", "/");

        map.put("menuInfo", menu);
        map.put("logoInfo", logo);
        map.put("homeInfo", home);
        return map;
    }

}
