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

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author 王帅
 * @since 1.0
 */
@Getter
public final class SecurityUser implements UserDetails {

    private static final long serialVersionUID = 9106013382537960231L;

    private final User user;
    private final Collection<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public SecurityUser(User user, Collection<GrantedAuthority> authorities) {
        this(user, true, true, true, true, authorities);
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
        return user.getUsername();
    }

    /**
     * Returns true if the supplied object is a User instance with the same username value.
     * In other words, the objects are equal if they have the same username, representing the same principal.
     *
     * @param obj 类型
     * @return true 相同用户名
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecurityUser) {
            return getUsername().equals(((SecurityUser) obj).getUsername());
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

}
