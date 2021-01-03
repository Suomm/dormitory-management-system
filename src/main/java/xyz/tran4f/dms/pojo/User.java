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
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import xyz.tran4f.dms.validation.constraints.Gender;
import xyz.tran4f.dms.validation.constraints.Id;
import xyz.tran4f.dms.validation.constraints.Password;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 用户实体类，保存用户有关的信息。
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
@ApiModel(value = "用户实体类", description = "封装学号、姓名、性别、密码、邮箱等用户信息")
public class User implements Serializable {

    private static final long serialVersionUID = -5246550721989479832L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "学号", required = true)
    private String id; // 学号

    @NotBlank
    @ApiModelProperty(value = "姓名", required = true)
    private String username; // 姓名

    @Password
    @ApiModelProperty(value = "密码", required = true)
    private String password; // 密码

    @Email
    @NotBlank
    @ApiModelProperty(value = "邮箱", required = true)
    private String email; // 邮箱

    @Gender
    @ApiModelProperty(value = "性别", required = true, example = "0")
    private Integer gender; // 性别

    @ApiModelProperty(hidden = true)
    private String  role; // 角色
    @ApiModelProperty(hidden = true)
    private Integer credit; // 学分
    @ApiModelProperty(hidden = true)
    private Boolean locked; // 锁定

    @Contract(pure = true)
    public User(String id) {
        this.id = id;
    }

}
