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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.RedisAttribute;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.pojo.Note;
import xyz.tran4f.dms.service.DormitoryService;
import xyz.tran4f.dms.utils.ExcelUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * 2021/1/5
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@RestController("/dormitory")
@Api(tags = "宿舍模块的程序接口")
@Secured({"ROLE_ROOT", "ROLE_MANAGER"})
public class DormitoryController extends BaseController<DormitoryService> {

    @GetMapping("buildings")
    @ApiOperation(value = "获取所有宿舍楼号")
    public List<Object> getBuildings() {
        return service.listObjs(Wrappers.lambdaQuery(Dormitory.class)
                .groupBy(Dormitory::getBuilding)
                .select(Dormitory::getBuilding));
    }

    @GetMapping("page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true, paramType = "query", example = "1"),
            @ApiImplicitParam(name = "size", value = "每页展示数量", required = true, paramType = "query", example = "1"),
            @ApiImplicitParam(name = "building", value = "宿舍楼号", required = true)
    })
    @ApiResponses(@ApiResponse(code = 200, message = "返回一个包含分组信息的对象"))
    @ApiOperation(value = "分页展示宿舍信息", notes = "获取宿舍所属年级、宿舍号、房间号的信息。")
    public Page<Dormitory> page(long current, long size, String building) {
        return service.page(new Page<>(current, size),
                Wrappers.lambdaQuery(Dormitory.class)
                        .eq(Dormitory::getBuilding, building)
                        .orderByAsc(Dormitory::getRoom));
    }

    @GetMapping("list")
    @ApiOperation(value = "根据指定的宿舍楼号获取所有房间信息")
    @ApiImplicitParam(name = "building", value = "宿舍楼号", required = true)
    public List<Dormitory> getAll(String building) {
        return service.list(Wrappers.lambdaQuery(Dormitory.class).eq(Dormitory::getBuilding, building));
    }

    @GetMapping("get/{room}")
    @ApiOperation(value = "根据指定的房间号获取房间信息")
    @ApiImplicitParam(name = "room", value = "房间号", required = true)
    public Dormitory get(@PathVariable String room) {
        return service.getById(room);
    }

    @PostMapping("save")
    @ApiOperation(value = "保存宿舍信息")
    @ApiImplicitParam(name = "dormitory", value = "宿舍信息", required = true)
    public void save(Dormitory dormitory) {
        service.save(dormitory);
    }

    @PostMapping("saveBatch")
    @ApiOperation(value = "批量修改插入宿舍信息")
    public void saveBatch(@ApiParam(value = "宿舍信息", required = true)
                          @RequestBody() List<Dormitory> dormitories) {
        service.saveOrUpdateBatch(dormitories);
    }

    @DeleteMapping("delete/{room}")
    @ApiOperation(value = "根据房间号删除一条宿舍信息")
    @ApiImplicitParam(name = "room", value = "房间号", required = true)
    public void delete(@PathVariable String room) {
        service.removeById(room);
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation(value = "根据房间号批量删除宿舍信息")
    public void deleteBatch(@ApiParam(value = "宿舍信息", required = true)
                            @RequestBody List<String> rooms) {
        service.removeByIds(rooms);
    }

    @PutMapping("update")
    @ApiOperation(value = "更新宿舍信息")
    @ApiImplicitParam(name = "dormitory", value = "宿舍信息", required = true)
    public boolean update(Dormitory dormitory) {
        return service.updateById(dormitory);
    }

    @PutMapping("updateBatch")
    @ApiOperation(value = "批量更新宿舍信息")
    public boolean updateBatch(@ApiParam(value = "宿舍信息", required = true)
                               @RequestBody List<Dormitory> dormitories) {
        return service.updateBatchById(dormitories);
    }

    @PostMapping("note")
    @ApiOperation(value = "记录宿舍成绩")
    public Long setNote(@ApiParam(value = "成绩", required = true) Note note) {
        return redisUtils.setAdd(RedisAttribute.KEY_RECORD, note);
    }

    @GetMapping("getNote")
    public Set<Object> getNote() {
        return redisUtils.setMembers(RedisAttribute.KEY_RECORD);
    }

    @PostMapping("createTable")
    public void createTable() {
        Set<Object> set = redisUtils.setUnion(RedisAttribute.KEY_RECORD);
        if (set == null) {
            throw new NullPointerException("set is null!");
        }
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ExcelUtils.writeWithTemplate("./tables/化学学院宿舍卫生检查表" + date + "（本科生）.xlsx", new TreeSet<>(set));
    }

    @GetMapping("test")
    public String test() {
        return new File("./").getAbsolutePath();
    }

}
