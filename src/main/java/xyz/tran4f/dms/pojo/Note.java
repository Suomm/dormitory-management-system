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

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 2021/1/6
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
@EqualsAndHashCode(of = "room")
@ApiModel(value = "宿舍检查的成绩信息")
public class Note implements Serializable, Comparable<Note> {

    private static final long serialVersionUID = 5736959954440581339L;

    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "日期", example = "2020/12/12", required = true)
    private Date date;

    @ApiModelProperty(value = "所属年级", example = "2020级", required = true)
    private String grade;

    @ApiModelProperty(value = "楼号", example = "学生公寓3号楼", required = true)
    private String building;

    @ApiModelProperty(value = "房间号", example = "3-214", required = true)
    private String room;

    @NumberFormat("##")
    @ApiModelProperty(value = "分数", required = true, example = "60")
    private Integer score;

    @ApiModelProperty(value = "周次", required = true, example = "十一")
    private String week;

    @ApiModelProperty(value = "备注")
    private String details;

    @ExcelIgnore
    @ApiModelProperty(value = "宿舍类型")
    private Integer type;

    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull Note o) {
        return this.room.compareTo(o.room);
    }

}
