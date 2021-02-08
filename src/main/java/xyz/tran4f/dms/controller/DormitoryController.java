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
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.service.DormitoryService;
import xyz.tran4f.dms.utils.WrapperUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 宿舍模块的具体业务流程控制。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Validated
@RestController
@Api(tags = "宿舍模块的程序接口")
@RequestMapping("/dormitory")
@Secured({"ROLE_MANAGER", "ROLE_ROOT"})
public class DormitoryController extends BaseController<DormitoryService> {

    /**
     * <p>
     * 返回数据库中所有宿舍楼的楼号信息。
     * </p>
     *
     * @return 所有宿舍楼的楼号
     */
    @GetMapping("buildings")
    @ApiOperation("获取所有宿舍楼的楼号信息")
    public List<Object> getBuildings() {
        return service.listObjs(Wrappers.lambdaQuery(Dormitory.class)
                .select(Dormitory::getBuilding)
                .groupBy(Dormitory::getBuilding));
    }

    /**
     * <p>
     * 分页显示宿舍信息，可以设置查询条件。
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
    @ApiOperation(value = "分页展示宿舍信息", notes = "获取宿舍所属年级、宿舍号、房间号等信息。")
    public Page<Dormitory> page(@Min(1) long current, @Min(1) long size, String params) {
        QueryWrapper<Dormitory> wrapper;
        if (params != null) {
            wrapper = WrapperUtils.allEq(JSON.parseObject(params, Dormitory.class));
        } else {
            wrapper = Wrappers.query();
        }
        wrapper.lambda().orderByAsc(Dormitory::getRoom);
        return service.page(new Page<>(current, size), wrapper);
    }

    /**
     * <p>
     * 保存或更新宿舍信息。
     * </p>
     *
     * @param dormitory 宿舍信息
     * @return {@code true} 更新或保存数据成功，{@code false} 更新或保存数据失败
     */
    @PostMapping("save")
    @ApiOperation("保存或更新宿舍信息")
    public boolean save(@ApiParam(value = "宿舍信息", required = true) @Validated Dormitory dormitory) {
        return service.saveOrUpdate(dormitory);
    }

    /**
     * <p>
     * 根据指定的房间号删除一条宿舍信息。
     * </p>
     *
     * @param room 房间号
     * @return {@code true} 删除宿舍信息成功，{@code false} 删除宿舍信息失败
     */
    @DeleteMapping("delete/{room}")
    @ApiOperation("根据指定的房间号删除一条宿舍信息")
    public boolean delete(@ApiParam(value = "房间号", required = true) @PathVariable String room) {
        return service.removeById(room);
    }

    /**
     * <p>
     * 根据房间号批量删除宿舍信息
     * </p>
     *
     * @param rooms 房间号
     * @return {@code true} 删除宿舍信息成功，{@code false} 删除宿舍信息失败
     */
    @DeleteMapping("deleteBatch")
    @ApiOperation("根据房间号批量删除宿舍信息")
    public boolean deleteBatch(@ApiParam(value = "宿舍信息", required = true)
                               @RequestBody @NotEmpty List<String> rooms) {
        return service.removeByIds(rooms);
    }

}
