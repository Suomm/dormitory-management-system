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

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.exception.DatabaseException;
import xyz.tran4f.dms.exception.UserNotFoundException;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;
import xyz.tran4f.dms.validation.constraints.Grade;
import xyz.tran4f.dms.validation.constraints.Id;

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
@Secured({"ROLE_ROOT"})
@RestController("/manager")
@Api(tags = "管理员模块的程序接口")
public class AdminController extends BaseController<UserService> {

    @GetMapping("getAllGrades")
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含年级信息的数组"))
    @ApiOperation(value = "获取年级分组", notes = "年级信息展示在侧边栏上，方便按年级展示成员信息。")
    public List<Object> getAllGrades() {
        return service.getAllGrades();
    }

    @GetMapping("pageByGrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true, defaultValue = "1", example = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示数量", required = true, defaultValue = "1", example = "5"),
            @ApiImplicitParam(name = "grade", value = "年级", required = true, paramType = "query", defaultValue = "2020")
    })
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含分组信息的对象"))
    @ApiOperation(value = "用户分页操作", notes = "在每个年级分组中，分页展示用户信息")
    public IPage<User> pageByGrade(long current, long size, @Grade String grade) {
        return service.pageByGrade(current, size, grade);
    }

    @GetMapping("guestPage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true, defaultValue = "1", example = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示数量", required = true, defaultValue = "1", example = "5"),
            @ApiImplicitParam(name = "grade", value = "年级", required = true, paramType = "query", defaultValue = "2020")
    })
    @ApiOperation(value = "游客分页操作", notes = "分页显示流动志愿者信息")
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含分组信息的对象"))
    public IPage<User> guestPage(long current, long size, @Grade String grade) {
        return service.guestPage(current, size, grade);
    }

    @PutMapping("updateCredit/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "学号", required = true, defaultValue = "1", example = "1"),
            @ApiImplicitParam(name = "credit", value = "学分", required = true, paramType = "query", defaultValue = "1", example = "1"),
    })
    @ApiOperation(value = "更改学分", notes = "由管理员更改普通用户的学分")
    @ApiResponses(@ApiResponse(code = 200, message = "设置更改用户账户的学分信息"))
    public void updateCredit(@PathVariable @Id String id, Integer credit) {
        if (!service.updateCredit(id, credit)) {
            throw new DatabaseException();
        }
    }

    @GetMapping("getUser/{id}")
    @ApiOperation(value = "获取用户信息", notes = "用于展示用户的详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "返回用户数据库中的详细信息"))
    public User getUser(@ApiParam(value = "学号", required = true) @PathVariable @Id String id) {
        User user = service.getById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        user.setPassword("[protected]");
        return user;
    }

    @DeleteMapping("deleteUser/{id}")
    @ApiOperation(value = "获取用户信息", notes = "用于展示用户的详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "删除指定用户的请求响应成功"))
    public void deleteUser(@ApiParam(value = "学号", required = true) @PathVariable @Id String id) {
        if (!service.deleteUser(id)) {
            throw new DatabaseException();
        }
    }

    @PostMapping("addUser")
    @ApiResponses(@ApiResponse(code = 200, message = "请求响应成功重新刷新原界面"))
    @ApiOperation(value = "注册新用户", notes = "处理用户注册请求，将新用户信息写入数据库，并重新刷新原界面。")
    public void addUser(@ApiParam(value = "用户信息", required = true) @Validated User user) {
        user.setPassword("qybzb0123456789");
        service.register(user);
    }

}
