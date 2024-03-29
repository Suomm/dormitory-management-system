/*
 * Copyright (C) 2020-2022 the original author or authors.
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

package cn.edu.tjnu.hxxy.dms.service;

import cn.edu.tjnu.hxxy.dms.exception.UnsupportedTaskException;
import cn.edu.tjnu.hxxy.dms.entity.Note;
import cn.edu.tjnu.hxxy.dms.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 任务相关操作的服务层接口。
 *
 * @author 王帅
 * @since 1.0
 */
public interface TaskService extends IService<Task> {

    /**
     * 通过任务 ID 查找具体任务。
     *
     * @param taskId 任务 ID
     * @param menu 是否为任务菜单
     * @return 任务信息
     * @throws UnsupportedTaskException 不存在该任务时抛出此异常
     */
    default Task findTask(Integer taskId, boolean menu) throws UnsupportedTaskException {
        return lambdaQuery()
                .eq(Task::getTaskId, taskId)
                .eq(Task::getMenu, menu)
                .oneOpt()
                .orElseThrow(() -> new UnsupportedTaskException("TaskService.incorrectKey"));
    }

    /**
     * 通过指定的当前周数和需要检查的宿舍楼，创建任务信息并写入数据库。生成任务菜单和具体任务，
     * 其中任务菜单是以当前周数为任务名称，具体任务是以需要检查的宿舍楼作为任务名称。任务菜单不
     * 能重复，即：如果存在以当前周数为任务名称的任务菜单则会抛出异常。
     *
     * @param week 当前周数
     * @param buildings 宿舍楼
     * @return 创建成功之后的所有任务 ID
     * @throws UnsupportedTaskException 存在相同名称的任务菜单
     */
    List<Integer> create(String week, List<String> buildings) throws UnsupportedTaskException;

    /**
     * 根据任务 ID 删除任务菜单及其子任务。
     *
     * @param taskId 任务 ID
     * @throws UnsupportedTaskException 如果 ID 对应的不是任务菜单抛出此异常
     */
    void delete(Integer taskId) throws UnsupportedTaskException;

    /**
     * 根据宿舍楼初始化所要检查的宿舍信息。
     *
     * @param building 要检查的宿舍楼
     * @return 该宿舍楼的宿舍信息
     */
    Map<String, Note> notes(String building);

    /**
     * 回滚具体任务
     *
     * @param taskId 任务 ID
     * @param parentId 父任务 ID
     * @return {@code true} 回滚成功，{@code false} 回滚失败
     */
    boolean rollback(Integer taskId, Integer parentId);

}
