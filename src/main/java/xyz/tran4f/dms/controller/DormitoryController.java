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
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.service.DormitoryService;
import xyz.tran4f.dms.utils.WrapperUtils;

import java.util.List;

/**
 * <p>
 * 2021/1/5
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@RestController()
@Api(tags = "宿舍模块的程序接口")
@RequestMapping("/dormitory")
@Secured({"ROLE_MANAGER", "ROLE_ROOT"})
public class DormitoryController extends BaseController<DormitoryService> {

    @GetMapping("buildings")
    @ApiOperation(value = "获取所有宿舍楼号")
    public List<Object> getBuildings() {
        return service.listObjs(Wrappers.lambdaQuery(Dormitory.class)
                .select(Dormitory::getBuilding)
                .groupBy(Dormitory::getBuilding));
    }

    @GetMapping("page")
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含分组信息的对象"))
    @ApiOperation(value = "分页展示宿舍信息", notes = "获取宿舍所属年级、宿舍号、房间号的信息。")
    public Page<Dormitory> page(@ApiParam(value = "页码", required = true, example = "1") long current,
                                @ApiParam(value = "每页数据量", required = true, example = "10") long size,
                                @ApiParam(value = "查询信息") String params) {
        QueryWrapper<Dormitory> wrapper;
        if (params != null) {
            wrapper = WrapperUtils.allEq(JSON.parseObject(params, Dormitory.class));
        } else {
            wrapper = Wrappers.query();
        }
        wrapper.lambda().orderByAsc(Dormitory::getRoom);
        return service.page(new Page<>(current, size), wrapper);
    }

    @PostMapping("save")
    @ApiOperation(value = "保存宿舍信息")
    @ApiImplicitParam(name = "dormitory", value = "宿舍信息", required = true)
    public boolean save(Dormitory dormitory) {
        return service.saveOrUpdate(dormitory);
    }

    @DeleteMapping("delete/{room}")
    @ApiOperation(value = "根据房间号删除一条宿舍信息")
    @ApiImplicitParam(name = "room", value = "房间号", required = true)
    public boolean delete(@PathVariable String room) {
        return service.removeById(room);
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation(value = "根据房间号批量删除宿舍信息")
    public boolean deleteBatch(@ApiParam(value = "宿舍信息", required = true)
                               @RequestBody List<String> rooms) {
        return service.removeByIds(rooms);
    }

}
