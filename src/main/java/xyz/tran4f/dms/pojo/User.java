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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.tran4f.dms.validation.constraints.Id;
import xyz.tran4f.dms.validation.constraints.Password;
import xyz.tran4f.dms.validation.constraints.Type;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;

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
@EqualsAndHashCode(of = "id")
@ApiModel(value = "用户实体类", description = "封装学号、姓名、性别、密码、邮箱等用户信息")
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID = -5246550721989479832L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "学号", required = true)
    private String id;

    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String username;

    @NotBlank
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @Password
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Email
    @NotBlank
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    @Type
    @ApiModelProperty(value = "性别", required = true, example = "0")
    private Integer gender;

    @ApiModelProperty(hidden = true)
    private String role;

    @ApiModelProperty(hidden = true)
    private String grade;

    @Builder.Default
    @Getter(AccessLevel.NONE)
    @ApiModelProperty(hidden = true)
    private Boolean enabled = true;

    @Builder.Default
    @Getter(AccessLevel.NONE)
    @ApiModelProperty(hidden = true)
    private Boolean accountNonLocked = true;

    @Builder.Default
    @Getter(AccessLevel.NONE)
    @ApiModelProperty(hidden = true)
    private Boolean accountNonExpired = true;

    @Builder.Default
    @Getter(AccessLevel.NONE)
    @ApiModelProperty(hidden = true)
    private Boolean credentialsNonExpired = true;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
