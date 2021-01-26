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

package xyz.tran4f.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tran4f.dms.exception.TaskStatusException;
import xyz.tran4f.dms.mapper.DormitoryMapper;
import xyz.tran4f.dms.mapper.TaskMapper;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.pojo.Note;
import xyz.tran4f.dms.pojo.Task;
import xyz.tran4f.dms.service.TaskService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.TASK_REPEAT;

/**
 * <p>
 * 2021/1/21
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final DormitoryMapper mapper;

    public TaskServiceImpl(DormitoryMapper mapper) {
        this.mapper = mapper;
    }

    public List<Integer> create(String name, List<String> buildings) {
        LambdaQueryWrapper<Task> query = Wrappers.lambdaQuery();
        if (baseMapper.selectCount(query.eq(Task::getName, name)) != 0) {
            throw new TaskStatusException(TASK_REPEAT);
        }

        List<Integer> idList = new ArrayList<>(buildings.size() + 1);
        Task parent = Task.builder()
                .menuIcon(Task.ICON)
                .menu(true)
                .name(name)
                .build();
        baseMapper.insert(parent);
        idList.add(parent.getTaskId());

        QueryWrapper<Dormitory> wrapper = Wrappers.query();
        wrapper.select("building", "grade", "category", "type", "count(1) as count");
        for (Iterator<String> itr = buildings.iterator(); itr.hasNext();) {
            wrapper.eq("building", itr.next()).or(itr.hasNext());
        }

        mapper.selectMaps(wrapper.groupBy("building")).forEach((map) -> {
            Task child = new Task();
            child.setParentId(parent.getTaskId());
            map.forEach((key, value) -> {
                switch (key) {
                    case "type":
                        child.setType((Integer) value);
                        break;
                    case "grade":
                        child.setGrade((String) value);
                        break;
                    case "count":
                        child.setTotal((Long) value);
                        break;
                    case "building":
                        child.setName((String) value);
                        break;
                    case "category":
                        child.setCategory((Integer) value);
                        break;
                }
            });
            baseMapper.insert(child);
            idList.add(child.getTaskId());
        });
        return idList;
    }

    public int delete(Integer taskId) {
        Task task = baseMapper.selectById(taskId);
        if (task.getMenu()) {
            LambdaQueryWrapper<Task> wrapper = Wrappers.lambdaQuery();
            List<Integer> collect = baseMapper.selectList(wrapper.eq(Task::getParentId, task.getTaskId()))
                    .stream().map(Task::getTaskId).collect(Collectors.toList());
            if (collect.size() > 0) {
                baseMapper.deleteBatchIds(collect);
            }
        }
        return baseMapper.deleteById(taskId);
    }

    public Object[] notes(String building) {
        return mapper.selectList(Wrappers.lambdaQuery(Dormitory.class)
                .eq(Dormitory::getBuilding, building)
                .orderByAsc(Dormitory::getRoom))
                .stream()
                .map(e -> Note.builder()
                .room(e.getRoom())
                .type(e.getType())
                .grade(e.getGrade())
                .building(e.getBuilding())
                .build()).toArray();
    }

    @Override
    public void notParentTask(Integer taskId) {
        // TODO 异常信息国际化
        if (lambdaQuery()
                .eq(Task::getTaskId, taskId)
                .oneOpt()
                .orElseThrow(() -> new TaskStatusException("找不到任务"))
                .getMenu()) {
            throw new TaskStatusException("不能修改父任务");
        }
    }

}
