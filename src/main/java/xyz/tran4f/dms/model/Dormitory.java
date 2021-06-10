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

package xyz.tran4f.dms.model;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import xyz.tran4f.dms.validation.constraints.Building;
import xyz.tran4f.dms.validation.constraints.Grade;
import xyz.tran4f.dms.validation.constraints.Room;
import xyz.tran4f.dms.validation.constraints.Type;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

/**
 * 宿舍的相关信息。
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
public class Dormitory implements Serializable, Comparable<Dormitory> {

    private static final long serialVersionUID = 5736959954440581339L;

    @Grade
    @ApiModelProperty(value = "所属年级", example = "2020级", required = true)
    private String grade;

    @Building
    @ApiModelProperty(value = "楼号", example = "学生公寓3号楼", required = true)
    private String building;

    @Room
    @TableId(value = "room", type = IdType.INPUT)
    @ApiModelProperty(value = "房间号", example = "3-214", required = true)
    private String room;

    @Type
    @ApiModelProperty(value = "宿舍类型", example = "0", required = true, notes = "男生（女生）宿舍")
    private Integer type;

    @Type
    @ApiModelProperty(value = "宿舍类型", example = "1", required = true, notes = "0 女生宿舍 1 男生宿舍")
    private Integer category;

    @TableField(exist = false)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "日期", example = "2020-12-12", hidden = true)
    private Date date;

    @Min(0)
    @Max(100)
    @NumberFormat("##")
    @TableField(exist = false)
    @ApiModelProperty(value = "分数", required = true, example = "60")
    private Integer score;

    @TableField(exist = false)
    @ApiModelProperty(value = "周次", example = "十一", hidden = true)
    private String week;

    @TableField(exist = false)
    @ApiModelProperty(value = "备注", required = true)
    private String details;


    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String buildingNo;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String roomNo;

    /**
     * 生成一些必要的属性。
     *
     * @since 1.1
     */
    public void generate() {
        if (this.details == null) {
            this.details = "";
        }
        this.buildingNo = room.substring(0, room.indexOf('-'));
        this.roomNo     = room.substring(room.lastIndexOf('-') + 1);
    }

    @Override
    public int compareTo(Dormitory o) {
        int res = this.grade.compareTo(o.grade);
        if (res != 0) {
            return res;
        }
        res = this.buildingNo.compareTo(o.buildingNo);
        return res != 0 ? res : this.roomNo.compareTo(o.roomNo);
    }

}
