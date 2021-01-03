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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * <p>
 * 安全用户对象，创建出来后有 Spring Security 管理，封装有 User 实体。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Getter
@Setter
@ToString
public final class SecurityUser implements UserDetails {

    private static final long serialVersionUID = 9106013382537960231L;

    private User user;
    private Collection<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public SecurityUser(User user, Collection<GrantedAuthority> authorities) {
        this(user, true, true, true, !user.getLocked(), authorities);
    }

    public SecurityUser(User user, boolean enabled, boolean accountNonExpired,
                        boolean credentialsNonExpired, boolean accountNonLocked,
                        Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.enabled = enabled;
        this.authorities = authorities;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    /**
     * <p>
     * 如果提供的对象是具有相同用户名的用户实例，则返回 {@code true} 值。换句话说，
     * 如果对象具有相同的用户名，表示相同的主体，那么它们是相等的。
     * </p>
     *
     * @param obj 比较的类型
     * @return {@code true} 具有相同用户名，{@code false} 不是同一个对象
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecurityUser) {
            return getUsername().equals(((SecurityUser) obj).getUsername());
        }
        return false;
    }

    /**
     * <p>
     * 返回 {@code username} 的哈希值。
     * </p>
     */
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

}
