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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 管理员模块的具体业务流程控制。
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

    /**
     * <p>
     * 获取部门的邀请码，用于直接注册用户。
     * </p>
     *
     * @return 部门邀请码
     */
    @GetMapping("captcha")
    @ApiOperation("获取部门邀请码")
    public String captcha() {
        return redisUtils.get(RedisAttribute.KEY_CAPTCHA);
    }

    /**
     * <p>
     * 分页显示用户信息，可以设置查询条件。
     * </p>
     *
     * @param current 当前页
     * @param size 每页显示数量
     * @param params 查询参数
     * @return 数据分页对象
     */
    @GetMapping("page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码", required = true, example = "1", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页数据量", required = true, example = "10", paramType = "query"),
            @ApiImplicitParam(name = "params", value = "查询信息", paramType = "query")
    })
    @ApiOperation(value = "分页展示用户信息", notes = "获取用户所属年级、性别、学号、邮箱地址等信息。")
    public Page<User> page(@Min(1) long current, @Min(1) long size, String params) {
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

    /**
     * <p>
     * 保存或更新用户信息。
     * </p>
     *
     * @param user 用户信息
     * @return {@code true} 更新或保存数据成功，{@code false} 更新或保存数据失败
     */
    @PostMapping("save")
    @ApiOperation("保存用户信息")
    public boolean save(@ApiParam(value = "用户信息", required = true) @Validated User user) {
        return service.register(user.setPassword("123456"));
    }

    /**
     * <p>
     * 根据指定的学号删除一条用户信息。
     * </p>
     *
     * @param id 学号
     * @return {@code true} 删除用户信息成功，{@code false} 删除用户信息失败
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation("根据学号删除一条用户信息")
    public boolean delete(@ApiParam(value = "学号", required = true) @PathVariable String id) {
        return service.removeById(id);
    }

    /**
     * <p>
     * 根据学号批量删除用户信息
     * </p>
     *
     * @param ids 学号
     * @return {@code true} 删除用户信息成功，{@code false} 删除用户信息失败
     */
    @DeleteMapping("deleteBatch")
    @ApiOperation("根据学号批量删除用户信息")
    public boolean deleteBatch(@ApiParam(value = "用户信息", required = true)
                               @RequestBody @NotEmpty List<String> ids) {
        return service.removeByIds(ids);
    }

}
