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

package cn.edu.tjnu.hxxy.dms.controller;

import cn.edu.tjnu.hxxy.dms.entity.Notice;
import cn.edu.tjnu.hxxy.dms.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

import static cn.edu.tjnu.hxxy.dms.constant.RedisConst.KEY_NOTICE;

/**
 * 公告模块的具体业务流程控制。
 *
 * @author 王帅
 * @since 1.0
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@Api(tags = "公告模块的程序接口")
public class NoticeController {

    private final RedisUtil redisUtil;

    /**
     * 获取所有公告内容。
     *
     * @return 所有公告
     */
    @GetMapping("list")
    @ApiOperation("获取所有公告内容")
    public List<Object> list() {
        return redisUtil.values(KEY_NOTICE);
    }

    /**
     * 保存或更新公告信息。
     *
     * @param notice 公告信息
     * @return 总是 {@code true}
     */
    @PostMapping("save")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation("保存或更新公告信息")
    public boolean save(@ApiParam(value = "公告信息", required = true) @Validated Notice notice) {
        if (notice.getDate() == null) {
            notice.setDate(new Date());
        }
        redisUtil.hash(KEY_NOTICE, notice.getDate().toString(), notice);
        return true;
    }

    /**
     * 批量删除删除公告信息。
     *
     * @param notice 要删除的公告信息
     * @return 删除的记录条数
     */
    @DeleteMapping("delete")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation("批量删除删除公告信息")
    public Long delete(@RequestBody @NotEmpty List<Notice> notice) {
        return redisUtil.remove(KEY_NOTICE, notice.stream().map(e -> e.getDate().toString()).toArray());
    }

}
