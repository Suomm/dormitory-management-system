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

package xyz.tran4f.dms.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 2021/1/16
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = "taskId")
@ApiModel(value = "查宿任务信息", description = "包含周次、宿舍信息、任务状态等详细信息。")
public class Task implements Serializable {

    private static final long serialVersionUID = -447370729695464083L;
    public static final String ICON = "layui-icon-set";

    @TableId(type = IdType.AUTO)
    private Integer taskId;
    private String name;
    private String grade;
    private Integer category;
    private Integer type;
    private Boolean menu;
    private Boolean complete;
    private Long total;
    private String menuIcon;
    private Integer parentId;

}