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
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.RabbitAttribute;
import xyz.tran4f.dms.exception.TaskStatusException;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.pojo.Note;
import xyz.tran4f.dms.pojo.Task;
import xyz.tran4f.dms.service.TaskService;
import xyz.tran4f.dms.utils.ServletUtils;
import xyz.tran4f.dms.utils.WordUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.TASK_NONE;
import static xyz.tran4f.dms.attribute.RedisAttribute.*;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_ASSETS;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_STORES;

/**
 * <p>
 * 2021/1/21
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController<TaskService> {

    @GetMapping("list")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public Map<String, Object> list() {
        List<Task> tasks = service.list();
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", 0);
        map.put("msg", "请求成功");
        map.put("count", tasks.size());
        map.put("data", tasks);
        return map;
    }

    @GetMapping("show")
    public List<Task> show() {
        Integer gender = ServletUtils.getUser().getGender();
        return service.lambdaQuery()
                .eq(Task::getMenu, false)
                .eq(Task::getComplete, false)
                .list()
                .stream()
                .filter(t -> t.getCategory().equals(gender))
                .collect(Collectors.toList());
    }

    @GetMapping("draft")
    public List<String> draft() {
        return redisUtils.get(KEY_DRAFT, new ArrayList<>());
    }

    @PostMapping("publish")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public void publish(@RequestBody String[] contents) throws IOException {
        redisUtils.set(KEY_DRAFT, Arrays.asList(contents));
        String week = redisUtils.get(KEY_ACTIVE_WEEK);
        if (week == null) {
            throw new TaskStatusException(TASK_NONE);
        }
        String name = "化学学院第" + week + "周宿舍文明检查周报";
        WordUtils.create(WEB_PORTFOLIO_STORES + name + ".docx", "【" + name + "】", contents);
    }

    @PostMapping("save")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public boolean save(@RequestBody Map<String, Object> values) {
        @SuppressWarnings("unchecked")
        List<String> buildings = (List<String>) values.get("buildings");
        // 没有需要检查的宿舍楼
        if (buildings.size() == 0) {
            return false;
        }
        String name = (String) values.get("name");
        // 保存任务信息并返回任务 ID 列表
        List<Integer> idList = service.create(name, buildings);
        // 设置当前周次
        redisUtils.set(KEY_ACTIVE_WEEK, name);
        // 存放任务列表
        redisUtils.listPush(KEY_ACTIVE_TASK, idList.toArray());
        // 初始化查宿记录
        buildings.forEach(e -> {
            redisUtils.listPush(KEY_BUILDING_LIST, e);
            redisUtils.setAdd(PREFIX_TASK_RECORD + e, service.notes(e));
        });
        return true;
    }

    @PutMapping("rollback/{taskId}")
    public boolean rollback(@PathVariable Integer taskId) {
        service.notParentTask(taskId);
        return redisUtils.listPush(KEY_ACTIVE_TASK, taskId) &&
        service.lambdaUpdate().eq(Task::getTaskId, taskId).set(Task::getComplete, false).update();
    }

    @DeleteMapping("delete/{taskId}")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public int delete(@PathVariable Integer taskId) {
        service.notParentTask(taskId);
        // TODO 删除任务操作
        redisUtils.delete(Arrays.asList(KEY_ACTIVE_WEEK, KEY_ACTIVE_TASK, KEY_BUILDING_LIST));
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_ASSETS));
        FileUtils.deleteQuietly(new File(WEB_PORTFOLIO_STORES + taskId));
        return service.delete(taskId);
    }

    @GetMapping("details/{taskId}")
    @Secured({"ROLE_MANAGER","ROLE_ROOT"})
    public List<String> details(@PathVariable Integer taskId) {
        File source = new File(WEB_PORTFOLIO_STORES + taskId);
        // TODO 历史记录为空
        return FileUtils.listFiles(source, FileFileFilter.FILE, FalseFileFilter.FALSE)
                .stream()
                .map(e -> taskId + "/stores/" + e.getName())
                .collect(Collectors.toList());
    }

    @PostMapping("finish/{taskId}")
    public boolean finish(@PathVariable Integer taskId) {
        service.notParentTask(taskId);
        redisUtils.listRemove(KEY_ACTIVE_TASK, taskId);
        int size = redisUtils.listSize(KEY_ACTIVE_TASK);
        if (size == 1) {
            // 更新父任务
            Integer parentId = redisUtils.listFirst(KEY_ACTIVE_TASK);
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

    @PostMapping("notes")
    @ApiOperation(value = "记录宿舍成绩")
    public Long setNotes(@ApiParam(value = "成绩", required = true) @RequestBody List<Note> notes) {
        Date date = new Date();
        String week = redisUtils.get(KEY_ACTIVE_WEEK);
        String building = notes.get(0).getBuilding();
        Object[] values = notes.stream().map(e -> e.setDate(date).setWeek(week)).toArray();
        redisUtils.unmodifiableSet(PREFIX_CLEAN_SET + building, notes.stream().filter(e -> e.getScore() >= 90)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build()).toArray());
        redisUtils.unmodifiableSet(PREFIX_DIRTY_SET + building, notes.stream().filter(e -> e.getScore() < 60)
                .map(e -> Dormitory.builder().grade(e.getGrade()).room(e.getRoom()).build()).toArray());
        return redisUtils.setAdd(PREFIX_TASK_RECORD + building, values);
    }

}
