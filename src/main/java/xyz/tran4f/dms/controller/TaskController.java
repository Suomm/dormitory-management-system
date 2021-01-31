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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.RabbitAttribute;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.pojo.Note;
import xyz.tran4f.dms.pojo.Response;
import xyz.tran4f.dms.pojo.Task;
import xyz.tran4f.dms.service.TaskService;
import xyz.tran4f.dms.utils.DateUtils;
import xyz.tran4f.dms.utils.ServletUtils;
import xyz.tran4f.dms.utils.WordUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static xyz.tran4f.dms.attribute.RedisAttribute.*;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_ASSETS;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_STORES;

/**
 * <p>
 * 任务模块的具体业务流程控制。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/task")
@Api(tags = "任务模块的程序接口")
public class TaskController extends BaseController<TaskService> {

    /**
     * <p>
     * 列出所有的任务信息（包括任务菜单）。
     * </p>
     *
     * @return 可被 Layuimini 解析的数据对象
     */
    @GetMapping("list")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "列出所有的任务信息（包括任务菜单）")
    public Map<String, Object> list() {
        List<Task> tasks = service.list();
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", 0);
        map.put("msg", "请求成功");
        map.put("count", tasks.size());
        map.put("data", tasks);
        return map;
    }

    /**
     * <p>
     * 根据当前登陆用户的性别信息，展示当前未完成的所有具体任务。
     * </p>
     *
     * @return 任务列表
     */
    @GetMapping("show")
    @ApiOperation(value = "向用户展示具体任务")
    public List<Task> show() {
        return service.lambdaQuery()
                .eq(Task::getMenu, false)
                .eq(Task::getComplete, false)
                .eq(Task::getCategory, ServletUtils.getUser().getGender())
                .list();
    }

    /**
     * <p>
     * 获取上一次制作的新闻稿内容。
     * </p>
     *
     * @return 新闻稿段落
     */
    @GetMapping("draft")
    @ApiOperation(value = "获取上一次制作的新闻稿内容")
    public List<String> draft() {
        return redisUtils.get(KEY_DRAFT, new ArrayList<>());
    }

    /**
     * <p>
     * 设置任务开始日期。<b>注：</b>该日期所在周会被标识为第一周，并且会根据该日期计算查宿周次。
     * </p>
     *
     * @param date 日期
     */
    @PostMapping("set/date")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "设置任务开始日期")
    public void setDate(@ApiParam(value = "起始日期", required = true, example = "2020-09-24")
                            @NotNull String date) {
        redisUtils.set(KEY_SEMESTER_BEGIN, DateUtils.parse(date));
    }

    /**
     * <p>
     * 获取任务开始日期。<b>注：</b>该日期所在周会被标识为第一周，并且会根据该日期计算查宿周次。
     * </p>
     *
     * @return 格式化日期字符串
     */
    @GetMapping("get/date")
    @ApiOperation(value = "获取任务开始日期")
    public String getDate() {
        Long begin = redisUtils.get(KEY_SEMESTER_BEGIN);
        if (begin == null) { return DateUtils.now(); }
        return DateUtils.format(begin);
    }

    /**
     * <p>
     * 获取任务完成之后，处理上传图片的警告信息。
     * </p>
     *
     * @return 警告信息
     */
    @GetMapping("get/warns")
    @ApiOperation(value = "获取任务开始日期")
    public List<String> getWarns() {
        return redisUtils.orElseThrow(KEY_WARNINGS);
    }

    /**
     * <p>
     * 保存新闻稿内容，并生成新闻稿文件。
     * </p>
     *
     * @param contents 段落内容
     * @throws IOException 发生 IO 异常
     */
    @PostMapping("publish")
    @Secured({"ROLE_USER", "ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "保存新闻稿内容，并生成新闻稿文件")
    public void publish(@RequestBody @NotEmpty String[] contents) throws IOException {
        String week = redisUtils.orElseThrow(KEY_ACTIVE_WEEK);
        redisUtils.set(KEY_DRAFT, Arrays.asList(contents));
        String name = "化学学院第" + week + "周宿舍文明检查周报";
        WordUtils.create(WEB_PORTFOLIO_STORES + name + ".docx", "【" + name + "】", contents);
    }

    /**
     * <p>
     * 根据任务开始日期计算周次并返回结果。
     * </p>
     *
     * @return 周次
     */
    @GetMapping("get/week")
    @ApiOperation(value = "根据任务开始日期计算周次并返回结果")
    public Response getWeek() {
        Long begin = redisUtils.get(KEY_SEMESTER_BEGIN);
        if (begin == null) {
            return Response.error("请设置学期开始时间");
        }
        if (begin > System.currentTimeMillis()) {
            return Response.error("请在学期开始之后发布任务");
        }
        return Response.ok(DateUtils.weekOfSemester(begin));
    }

    /**
     * <p>
     * 保存任务信息。
     * </p>
     *
     * @param buildings 要检查的宿舍楼
     * @return 响应信息
     */
    @PostMapping("save")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "创建并保存任务信息")
    public Response save(@RequestBody @NotEmpty List<String> buildings) {
        // 获取周次的响应信息
        Response response = getWeek();
        // 响应时发生错误
        if (response.getCode() == Response.ERROR) { return response; }
        // 取到周次信息
        String week = response.getMsg();
        // 保存任务信息并返回任务 ID 列表
        List<Integer> idList = service.create(week, buildings);
        // 设置当前周次
        redisUtils.set(KEY_ACTIVE_WEEK, week);
        // 存放任务列表
        redisUtils.sSet(KEY_ACTIVE_TASK, idList.toArray());
        // 初始化查宿记录
        buildings.forEach(e -> {
            redisUtils.lPush(KEY_BUILDING_LIST, e);
            redisUtils.sSet(PREFIX_TASK_RECORD.concat(e), service.notes(e));
        });
        // 删除原有的临时资源文件夹
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_ASSETS));
        // 创建历史记录文件夹
        if (!new File(WEB_PORTFOLIO_STORES + idList.get(0)).mkdirs()) {
            log.warn("创建历史记录文件夹 {}/{}/ 失败", WEB_PORTFOLIO_STORES, idList.get(0));
        }
        return Response.ok("创建任务成功");
    }

    /**
     * <p>
     * 任务意外结束时回滚任务。
     * </p>
     *
     * @param taskId 任务 ID
     * @return 回滚数据条数
     */
    @PutMapping("rollback/{taskId}")
    @ApiOperation(value = "任务意外结束时回滚任务")
    public Long rollback(@PathVariable Integer taskId) {
        return redisUtils.sSet(KEY_ACTIVE_TASK, service.rollback(taskId));
    }

    /**
     * <p>
     * 删除任务菜单和历史记录。
     * </p>
     *
     * @param taskId 任务 ID
     */
    @DeleteMapping("delete/{taskId}")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "删除任务菜单和历史记录")
    public void delete(@PathVariable Integer taskId) {
        service.delete(taskId);
        redisUtils.delete(Arrays.asList(KEY_ACTIVE_WEEK, KEY_ACTIVE_TASK, KEY_BUILDING_LIST, KEY_WARNINGS));
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_STORES + taskId));
    }

    /**
     * <p>
     * 任务完成之后，获取任务的相关完成资料。
     * </p>
     *
     * @param taskId 任务 ID
     * @return 详细信息
     */
    @GetMapping("details/{taskId}")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation(value = "任务完成之后，获取任务的相关完成资料")
    public Response details(@PathVariable Integer taskId) {
        File source = new File(WEB_PORTFOLIO_STORES.concat(taskId.toString()));
        if (!source.exists()) {
            return Response.error("没有找到该任务的历史记录");
        }
        List<String> collect = FileUtils.listFiles(source, FileFileFilter.FILE, FalseFileFilter.FALSE)
                .stream()
                .map(e -> "stores/" + taskId + "/" + e.getName())
                .collect(Collectors.toList());
        if (collect.size() == 0) {
            return Response.error("任务还没有完成");
        }
        return Response.ok(collect);
    }

    /**
     * <p>
     * 设置完成一项具体任务。
     * </p>
     *
     * @param taskId 任务 ID
     * @return {@code true} 操作成功，{@code false} 操作失败
     */
    @PostMapping("finish/{taskId}")
    @ApiOperation(value = "设置完成一项具体任务")
    public boolean finish(@PathVariable Integer taskId) {
        service.findTask(taskId, false);
        redisUtils.sRemove(KEY_ACTIVE_TASK, taskId);
        int size = redisUtils.sSize(KEY_ACTIVE_TASK);
        if (size == 1) {
            // 更新父任务
            Integer parentId = redisUtils.sPop(KEY_ACTIVE_TASK);
            service.lambdaUpdate()
                    .eq(Task::getTaskId, parentId)
                    .set(Task::getComplete, true)
                    .update();
            rabbitTemplate.convertAndSend(RabbitAttribute.QUEUE_TASK, taskId);
        }
        // 更新子任务
        return service.lambdaUpdate()
                .eq(Task::getTaskId, taskId)
                .set(Task::getComplete, true)
                .update();
    }

    /**
     * <p>
     * 记录查宿成绩。
     * </p>
     *
     * @param notes 成绩信息
     * @return 更新数据条数
     */
    @PostMapping("notes")
    @ApiOperation(value = "记录宿舍成绩")
    public Long setNotes(@ApiParam(value = "成绩", required = true) @RequestBody @NotEmpty List<Note> notes) {
        String week = redisUtils.orElseThrow(KEY_ACTIVE_WEEK);
        Date date = new Date();
        String building = notes.get(0).getBuilding();
        Object[] values = notes.stream().map(e -> e.setDate(date).setWeek(week)).toArray();
        redisUtils.unmodifiableSet(PREFIX_CLEAN_SET.concat(building), notes.stream().filter(e -> e.getScore() >= 90)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build()).toArray());
        redisUtils.unmodifiableSet(PREFIX_DIRTY_SET.concat(building), notes.stream().filter(e -> e.getScore() < 60)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build()).toArray());
        return redisUtils.sSet(PREFIX_TASK_RECORD.concat(building), values);
    }

    /**
     * <p>
     * 定时清理任务缓存记录文件。
     * </p>
     */
    @Scheduled(cron = "0 0 0 1 2,8 ?")
    public void clear() {
        service.remove(new QueryWrapper<>());
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_STORES));
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_ASSETS));
        log.info("清理一个学期的历史记录文件成功");
    }

}
