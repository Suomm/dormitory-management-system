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

package cn.edu.tjnu.hxxy.dms.entity;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import cn.edu.tjnu.hxxy.dms.validation.constraints.Building;
import cn.edu.tjnu.hxxy.dms.validation.constraints.Grade;
import cn.edu.tjnu.hxxy.dms.validation.constraints.Room;
import cn.edu.tjnu.hxxy.dms.validation.constraints.Type;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 检查宿舍的相关信息记录。
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
    @ApiModelProperty(value = "日期", example = "2020-12-12", hidden = true)
    private Date date;

    @Grade
    @ApiModelProperty(value = "所属年级", example = "2020级", required = true)
    private String grade;

    @Building
    @ApiModelProperty(value = "楼号", example = "学生公寓3号楼", required = true)
    private String building;

    @Room
    @ApiModelProperty(value = "房间号", example = "3-214", required = true)
    private String room;

    @Min(0)
    @Max(100)
    @NumberFormat("##")
    @ApiModelProperty(value = "分数", required = true, example = "60")
    private Integer score;

    @ApiModelProperty(value = "周次", example = "十一", hidden = true)
    private String week;

    @NotNull
    @ApiModelProperty(value = "备注", required = true)
    private String details;

    @Type
    @ApiModelProperty(value = "宿舍类型", example = "0", required = true, notes = "男生（女生）宿舍")
    private Integer type;

    @Override
    public int compareTo(Note o) {
        int res = this.getGrade().compareTo(o.getGrade());
        return res != 0 ? res : this.room.compareTo(o.room);
    }

}
