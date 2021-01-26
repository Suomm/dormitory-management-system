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

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.pojo.Notice;
import xyz.tran4f.dms.utils.RedisUtils;

import java.util.Date;
import java.util.Set;

import static xyz.tran4f.dms.attribute.RedisAttribute.KEY_NOTICE;

/**
 * <p>
 * 2021/1/20
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@RestController
@RequestMapping("/notice")
@Secured({"ROLE_MANAGER","ROLE_ROOT"})
public class NoticeController {

    private final RedisUtils redisUtils;

    public NoticeController(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @GetMapping("list")
    public Set<Object> list() {
        return redisUtils.setMembers(KEY_NOTICE);
    }

    @PostMapping("save")
    public Long save(Notice notice) {
        if (notice.getDate() == null) {
            notice.setDate(new Date());
        }
        return redisUtils.setAdd(KEY_NOTICE, notice);
    }

    @DeleteMapping("delete")
    public Long delete(@RequestBody Notice[] notice) {
        return redisUtils.setRemove(KEY_NOTICE, notice);
    }

}
