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

package cn.edu.tjnu.hxxy.dms.mapper;

import cn.edu.tjnu.hxxy.dms.entity.Task;
import cn.edu.tjnu.hxxy.dms.component.MybatisRedisCache;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * 对任务数据库进行操作的 Mapper 接口。
 *
 * @author 王帅
 * @since 1.0
 */
@Repository
@CacheNamespace(implementation = MybatisRedisCache.class)
public interface TaskMapper extends BaseMapper<Task> {
}
