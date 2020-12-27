/*
 * Copyright (C) 2020 Wang Shuai (suomm.macher@foxmail.com)
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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 王帅
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -5246550721989479832L;

    @NotNull()
    @Pattern(regexp  = "203007\\d{4}")
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @NotBlank()
    private String username;

    @NotBlank
    @Pattern(regexp  = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$")
    private String password;

    @Email
    @NotBlank()
    private String email;

    private String  role;
    private Integer gender;
    private Integer credit;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String validateCode;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Timestamp registerDate;

    public User(String id) {
        this.id = id;
    }

}
