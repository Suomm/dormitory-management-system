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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tran4f.dms.exception.UnsupportedTaskException;
import xyz.tran4f.dms.mapper.TaskMapper;
import xyz.tran4f.dms.model.Dormitory;
import xyz.tran4f.dms.model.Task;
import xyz.tran4f.dms.service.DormitoryService;
import xyz.tran4f.dms.service.TaskService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务相关操作的服务接口实现。
 *
 * @author 王帅
 * @since 1.0
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final DormitoryService dormitoryService;

    public TaskServiceImpl(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> create(String name, List<String> buildings) {
        // 存在相同的任务菜单，抛出异常
        if (lambdaQuery().eq(Task::getName, name).count() != 0) {
            throw new UnsupportedTaskException("TaskServiceImpl.existed");
        }
        // 创建任务菜单
        List<Integer> idList = new ArrayList<>(buildings.size() + 1);
        Task parent = Task.builder()
                .menuIcon(Task.ICON)
                .menu(true)
                .name(name)
                .build();
        baseMapper.insert(parent);
        idList.add(parent.getTaskId());
        // 查询创建子任务需要的信息
        QueryWrapper<Dormitory> wrapper = Wrappers.query();
        wrapper.select("building", "grade", "category", "type", "count(1) as count");
        for (Iterator<String> itr = buildings.iterator(); itr.hasNext();) {
            wrapper.eq("building", itr.next()).or(itr.hasNext());
        }
        // 创建并插入子任务
        dormitoryService.listMaps(wrapper.groupBy("building")).forEach((map) -> {
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
                    default:
                        break;
                }
            });
            baseMapper.insert(child);
            idList.add(child.getTaskId());
        });
        return idList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer taskId) {
        // 根据任务号查找对应的任务
        Task task = findTask(taskId, true);
        // 查找并删除子任务
        List<Integer> collect = lambdaQuery()
                .eq(Task::getParentId, task.getTaskId())
                .list()
                .stream()
                .map(Task::getTaskId)
                .collect(Collectors.toList());
        if (collect.size() > 0) {
            baseMapper.deleteBatchIds(collect);
        }
        // 删除任务菜单
        baseMapper.deleteById(taskId);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Map<String, Dormitory> notes(String building) {
        return dormitoryService.lambdaQuery()
                .eq(Dormitory::getBuilding, building)
                .list()
                .stream()
                .map(e -> Dormitory.builder()
                    .room(e.getRoom())
                    .type(e.getType())
                    .grade(e.getGrade())
                    .building(e.getBuilding())
                    .build())
                .collect(Collectors.toMap(Dormitory::getRoom, Function.identity()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rollback(Integer taskId, Integer parentId) {
        return lambdaUpdate()
                .set(Task::getComplete, false)
                .in(Task::getTaskId, taskId, parentId)
                .update();
    }

}
