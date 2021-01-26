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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.RedisAttribute;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;
import xyz.tran4f.dms.utils.ServletUtils;
import xyz.tran4f.dms.utils.WrapperUtils;

import java.util.List;

/**
 * <p>
 * 提供管理员的有关操作。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Validated
@RestController
@RequestMapping("/manager")
@Api(tags = "管理员模块的程序接口")
@Secured({"ROLE_MANAGER","ROLE_ROOT"})
public class ManagerController extends BaseController<UserService> {

    @GetMapping("captcha")
    public String captcha() {
        return redisUtils.get(RedisAttribute.KEY_CAPTCHA);
    }

    @GetMapping("page")
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含分组信息的对象"))
    @ApiOperation(value = "分页展示用户信息", notes = "获取用户所属年级、性别、学号、邮箱地址等信息。")
    public Page<User> page(@ApiParam(value = "页码", required = true, example = "1") long current,
                           @ApiParam(value = "每页数据量", required = true, example = "10") long size,
                           @ApiParam(value = "查询信息") String params) {
        QueryWrapper<User> wrapper;
        if (params != null) {
            wrapper = WrapperUtils.allEq(JSON.parseObject(params, User.class));
        } else {
            wrapper = Wrappers.query();
        }
        String id = ServletUtils.getUser().getId();
        wrapper.lambda()
                .ne(User::getId, id)
                .ne(User::getRole, "ROLE_ROOT")
                .orderByAsc(User::getId);
        return service.page(new Page<>(current, size), wrapper);
    }

    @PostMapping("save")
    @ApiOperation(value = "保存宿舍信息")
    @ApiImplicitParam(name = "dormitory", value = "宿舍信息", required = true)
    public boolean save(User user) {
        user.setPassword("123456"); // 默认密码
        service.register(user);
        return true;
    }

    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "根据房间号删除一条宿舍信息")
    @ApiImplicitParam(name = "room", value = "房间号", required = true)
    public boolean delete(@PathVariable String id) {
        return service.removeById(id);
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation(value = "根据房间号批量删除宿舍信息")
    public boolean deleteBatch(@ApiParam(value = "宿舍信息", required = true)
                               @RequestBody List<String> ids) {
        return service.removeByIds(ids);
    }

}
