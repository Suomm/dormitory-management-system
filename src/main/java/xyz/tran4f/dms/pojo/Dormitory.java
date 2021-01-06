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

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 宿舍信息。
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
@ApiModel(value = "宿舍楼信息", description = "宿舍楼的相关信息，包括所属年级、楼号、房间号。")
public class Dormitory implements Serializable {

    private static final long serialVersionUID = -6765246421822725327L;

    @ExcelProperty(value = "年级")
    @ApiModelProperty(value = "所属年级", example = "2020级", required = true)
    private String grade;

    @ExcelProperty(value = "宿舍楼")
    @ApiModelProperty(value = "楼号", example = "学生公寓3号楼", required = true)
    private String building;

    @ExcelProperty(value = "房间号")
    @TableId(value = "room", type = IdType.INPUT)
    @ApiModelProperty(value = "房间号", example = "3-214", required = true)
    private String room;

}
