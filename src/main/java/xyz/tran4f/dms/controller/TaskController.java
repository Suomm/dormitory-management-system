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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.constant.RabbitConsts;
import xyz.tran4f.dms.model.Dormitory;
import xyz.tran4f.dms.model.Response;
import xyz.tran4f.dms.model.Task;
import xyz.tran4f.dms.service.TaskService;
import xyz.tran4f.dms.util.DateUtils;
import xyz.tran4f.dms.util.ServletUtils;
import xyz.tran4f.dms.util.TextUtils;
import xyz.tran4f.dms.util.WordUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static xyz.tran4f.dms.constant.RedisConsts.*;
import static xyz.tran4f.dms.constant.WebConsts.WEB_PORTFOLIO_ASSETS;
import static xyz.tran4f.dms.constant.WebConsts.WEB_PORTFOLIO_STORES;

/**
 * 任务模块的具体业务流程控制。
 *
 * @author 王帅
 * @since 1.0
 */
@Validated
@RestController
@RequestMapping("/task")
@Api(tags = "任务模块的程序接口")
public class TaskController extends BaseController<TaskService> {

    /**
     * 列出所有的任务信息（包括任务菜单）。
     *
     * @return 可被 Layuimini 解析的数据对象
     */
    @GetMapping("list")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation("列出所有的任务信息（包括任务菜单）")
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
     * 根据当前登陆用户的性别信息，展示当前未完成的所有具体任务。
     *
     * @return 任务列表
     */
    @GetMapping("show")
    @ApiOperation("向用户展示具体任务")
    public List<Task> show() {
        return service.lambdaQuery()
                .eq(Task::getMenu, false)
                .eq(Task::getComplete, false)
                .eq(Task::getCategory, ServletUtils.getUser().getGender())
                .list();
    }

    /**
     * 获取上一次制作的新闻稿内容。
     *
     * @return 新闻稿段落
     */
    @GetMapping("draft")
    @ApiOperation("获取上一次制作的新闻稿内容")
    public List<String> draft() {
        return redisUtils.get(KEY_DRAFT, new ArrayList<>());
    }

    /**
     * 设置任务开始日期。<b>注：</b>该日期所在周会被标识为第一周，并且会根据该日期计算查宿周次。
     *
     * @param date 日期
     */
    @PostMapping("set/date")
    @ApiOperation("设置任务开始日期")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public void setDate(@ApiParam(value = "起始日期", required = true, example = "2020-09-24")
                            @NotNull String date) {
        redisUtils.set(KEY_SEMESTER_BEGIN, DateUtils.parse(date));
    }

    /**
     * 获取任务开始日期。<b>注：</b>该日期所在周会被标识为第一周，并且会根据该日期计算查宿周次。
     *
     * @return 格式化日期字符串
     */
    @GetMapping("get/date")
    @ApiOperation("获取任务开始日期")
    public String getDate() {
        Long begin = redisUtils.get(KEY_SEMESTER_BEGIN);
        if (begin == null) { return DateUtils.now(); }
        return DateUtils.format(begin);
    }

    /**
     * 获取任务完成之后，处理上传图片的警告信息。
     *
     * @return 警告信息
     */
    @GetMapping("get/warns/{taskId}")
    @ApiOperation("获取处理任务数据时产生的警告")
    public List<String> getWarns(@PathVariable Integer taskId) {
        return redisUtils.pop(KEY_WARNINGS, taskId.toString());
    }

    /**
     * 保存新闻稿内容，并生成新闻稿文件。
     *
     * @param contents 段落内容
     * @throws IOException 发生 IO 异常
     */
    @PostMapping("publish")
    @ApiOperation("保存新闻稿内容，并生成新闻稿文件")
    @Secured({"ROLE_USER", "ROLE_MANAGER","ROLE_ROOT"})
    public void publish(@RequestBody @NotEmpty String[] contents) throws IOException {
        String week = redisUtils.orElseThrow(KEY_ACTIVE_WEEK, "TaskController.incorrectKey");
        Integer taskId = redisUtils.orElseThrow(KEY_TASK_ID, "TaskController.incorrectKey");
        redisUtils.set(KEY_DRAFT, Arrays.asList(contents));
        String name = "化学学院第" + week + "周宿舍文明检查周报";
        WordUtils.create(WEB_PORTFOLIO_STORES + taskId + "/" + name + ".docx",
                "【" + name + "】", contents);
    }

    /**
     * 根据任务开始日期计算周次并返回结果。
     *
     * @return 周次
     */
    @GetMapping("get/week")
    @ApiOperation("根据任务开始日期计算周次并返回结果")
    public Response getWeek() {
        Long begin = redisUtils.get(KEY_SEMESTER_BEGIN);
        if (begin == null) {
            return Response.error("请设置学期开始日期");
        }
        if (begin > System.currentTimeMillis()) {
            return Response.error("请在学期开始之后发布任务");
        }
        // 如果一个学期的周数大于指定周数
        int max = 25;
        int week = DateUtils.weekOfSemester(begin);
        if (week > max) {
            return Response.error("本学期的周数超过了最大周数");
        }
        return Response.ok(TextUtils.format(week));
    }

    /**
     * 保存任务信息。
     *
     * @param buildings 要检查的宿舍楼
     * @return 响应信息
     */
    @PostMapping("save")
    @ApiOperation("创建并保存任务信息")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public Response save(@RequestBody @NotEmpty List<String> buildings) {
        // 获取周次的响应信息
        Response response = getWeek();
        // 响应时发生错误
        if (response.getCode() == Response.ERROR) { return response; }
        // 删除缓存资源
        deleteCaches();
        // 取到周次信息
        String week = response.getMsg();
        // 保存任务信息并返回任务 ID 列表
        List<Integer> idList = service.create("第" + week + "周", buildings);
        // 设置当前周次
        redisUtils.set(KEY_ACTIVE_WEEK, week);
        // 获取任务菜单 ID
        Integer parentId = idList.remove(0);
        // 设置当前周任务菜单 ID
        redisUtils.set(KEY_TASK_ID, parentId);
        // 存放任务列表
        redisUtils.sSet(KEY_ACTIVE_TASK, idList.toArray());
        // 初始化查宿记录
        buildings.forEach(e -> redisUtils.hash(KEY_TASK_RECORD, service.notes(e)));
        // 创建历史记录文件夹
        if (!new File(WEB_PORTFOLIO_STORES.concat(parentId.toString())).mkdirs()) {
            return Response.error("创建历史记录文件夹失败");
        }
        return Response.ok("创建任务成功");
    }

    /**
     * 任务意外结束时回滚任务。
     *
     * @param taskId 任务 ID
     */
    @PutMapping("rollback/{taskId}")
    @ApiOperation("任务意外结束时回滚任务")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public void rollback(@PathVariable Integer taskId) {
        Integer parentId = service.findTask(taskId, false).getParentId();
        // 只有本周发布的任务才能回滚
        if (parentId.equals(redisUtils.get(KEY_TASK_ID))
            && service.rollback(taskId, parentId)) {
            redisUtils.set(KEY_TASK_ID, parentId);
            redisUtils.sSet(KEY_ACTIVE_TASK, taskId);
        }
    }

    /**
     * 根据宿舍楼信息获取宿舍保存的成绩。
     *
     * @param building 宿舍楼
     * @return 成绩信息
     */
    @GetMapping("/scores")
    @ApiOperation("获取宿舍的评分")
    public List<Dormitory> scores(@NotBlank String building) {
        List<Dormitory> values = redisUtils.values(KEY_TASK_RECORD);
        return values.stream()
                .filter(e -> building.equals(e.getBuilding()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 删除任务菜单和历史记录。
     *
     * @param taskId 任务 ID
     */
    @DeleteMapping("delete/{taskId}")
    @ApiOperation("删除任务菜单和历史记录")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public void delete(@PathVariable Integer taskId) {
        service.delete(taskId);
        String id = taskId.toString();
        redisUtils.remove(KEY_WARNINGS, id);
        // 如果删除当前周的任务，则一并删除当前周缓存
        if (taskId.equals(redisUtils.get(KEY_TASK_ID))) {
            deleteCaches();
        }
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_STORES.concat(id)));
    }

    /**
     * 删除上次任务的缓存文件。
     *
     * @since 1.1
     */
    private void deleteCaches() {
        // 删除上次的成绩记录
        redisUtils.delete(KEY_ACTIVE_WEEK, KEY_ACTIVE_TASK, KEY_TASK_RECORD, KEY_CLEAN, KEY_DIRTY);
        // 删除原有的临时资源文件夹
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_ASSETS));
    }

    /**
     * 任务完成之后，获取任务的相关完成资料。
     *
     * @param taskId 任务 ID
     * @return 详细信息
     */
    @GetMapping("details/{taskId}")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    @ApiOperation("任务完成之后，获取任务的相关完成资料")
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
     * 设置完成一项具体任务。
     *
     * @param taskId 任务 ID
     * @return {@code true} 操作成功，{@code false} 操作失败
     */
    @PostMapping("finish/{taskId}")
    @ApiOperation("根据任务 ID 结束具体查宿任务")
    @Secured({"ROLE_USER", "ROLE_MANAGER","ROLE_ROOT"})
    public boolean finish(@PathVariable Integer taskId) {
        service.findTask(taskId, false);
        redisUtils.sRemove(KEY_ACTIVE_TASK, taskId);
        int size = redisUtils.sSize(KEY_ACTIVE_TASK);
        if (size == 0) {
            // 更新父任务
            Integer parentId = redisUtils.get(KEY_TASK_ID);
            assert parentId != null;
            service.lambdaUpdate()
                    .eq(Task::getTaskId, parentId)
                    .set(Task::getComplete, true)
                    .update();
            rabbitTemplate.convertAndSend(RabbitConsts.QUEUE_TASK, parentId);
        }
        // 更新子任务
        return service.lambdaUpdate()
                .eq(Task::getTaskId, taskId)
                .set(Task::getComplete, true)
                .update();
    }

    /**
     * 记录查宿成绩。
     *
     * @param dormitories 成绩信息
     */
    @PostMapping("notes")
    @ApiOperation("记录宿舍成绩")
    @Secured({"ROLE_USER", "ROLE_MANAGER","ROLE_ROOT"})
    public void setNotes(@ApiParam(value = "成绩", required = true) @RequestBody @NotEmpty @Valid List<Dormitory> dormitories) {
        String week = redisUtils.orElseThrow(KEY_ACTIVE_WEEK, "TaskController.incorrectKey");
        // 设置当前时间
        Date date = new Date();
        // 获取所有宿舍的宿舍号
        Object[] hashKeys = dormitories.stream().map(Dormitory::getRoom).toArray();
        // 删除之前的脏乱（优秀）宿舍信息
        redisUtils.remove(KEY_CLEAN, hashKeys);
        redisUtils.remove(KEY_DIRTY, hashKeys);
        // 更新脏乱（优秀）宿舍信息
        redisUtils.hash(KEY_CLEAN, dormitories.stream()
                .filter(e -> e.getScore() >= 90)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build())
                .collect(Collectors.toMap(Dormitory::getRoom, Function.identity())));
        redisUtils.hash(KEY_DIRTY, dormitories.stream()
                .filter(e -> e.getScore() < 60)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build())
                .collect(Collectors.toMap(Dormitory::getRoom, Function.identity())));
        // 存入更新的数据
        redisUtils.hash(KEY_TASK_RECORD, dormitories.stream()
                .map(e -> e.setDate(date).setWeek(week))
                .collect(Collectors.toMap(Dormitory::getRoom, Function.identity())));
    }

    /**
     * 定时清理任务缓存记录文件。
     */
    @Scheduled(cron = "0 0 0 1 2,8 ?")
    public void clear() {
        service.remove(new QueryWrapper<>());
        redisUtils.delete(KEY_ACTIVE_WEEK, KEY_ACTIVE_TASK,
                KEY_TASK_ID, KEY_TASK_RECORD, KEY_SEMESTER_BEGIN,
                KEY_WARNINGS, KEY_CLEAN, KEY_DIRTY);
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_STORES));
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_ASSETS));
    }

}
