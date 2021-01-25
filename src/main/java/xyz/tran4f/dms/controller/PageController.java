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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.tran4f.dms.utils.RedisUtils;

import static xyz.tran4f.dms.attribute.RedisAttribute.KEY_NOTICE;
import static xyz.tran4f.dms.attribute.RedisAttribute.PREFIX_TASK_RECORD;

/**
 * <p>
 * 2021/1/21
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Controller
public class PageController {

    private final RedisUtils redisUtils;

    public PageController(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @GetMapping("/welcome.html")
    public String welcome(Model model) {
        model.addAttribute("notices", redisUtils.setMembers(KEY_NOTICE));
        return "welcome";
    }

    @GetMapping("/page/user/note.html")
    public String note(String building, Model model) {
        model.addAttribute("notes", redisUtils.setMembers(PREFIX_TASK_RECORD + building));
        return "note";
    }

}
