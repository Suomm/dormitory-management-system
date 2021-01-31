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

import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.pojo.Notice;
import xyz.tran4f.dms.utils.RedisUtils;

import java.util.Date;
import java.util.Set;

import static xyz.tran4f.dms.attribute.RedisAttribute.KEY_NOTICE;

/**
 * <p>
 * 公告模块的具体业务流程控制。
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

    /**
     * <p>
     * 获取所有公告内容。
     * </p>
     *
     * @return 所有公告
     */
    @GetMapping("list")
    @ApiOperation(value = "获取所有公告内容")
    public Set<Object> list() {
        return redisUtils.sMembers(KEY_NOTICE);
    }

    /**
     * <p>
     * 保存或更新公告信息。
     * </p>
     *
     * @param notice 公告信息
     * @return 如果操作成功则返回 {@code 1}，否则操作失败。
     */
    @PostMapping("save")
    @ApiOperation(value = "保存或更新公告信息")
    public Long save(Notice notice) {
        if (notice.getDate() == null) {
            notice.setDate(new Date());
        }
        return redisUtils.sSet(KEY_NOTICE, notice);
    }

    /**
     * <p>
     * 批量删除删除公告信息。
     * </p>
     *
     * @param notice 要删除的公告信息
     * @return 删除的记录条数
     */
    @DeleteMapping("delete")
    @ApiOperation(value = "批量删除删除公告信息")
    public Long delete(@RequestBody Notice[] notice) {
        return redisUtils.sRemove(KEY_NOTICE, notice);
    }

}
