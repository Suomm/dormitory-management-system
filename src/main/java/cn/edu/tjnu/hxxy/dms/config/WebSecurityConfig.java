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

package cn.edu.tjnu.hxxy.dms.config;

import cn.edu.tjnu.hxxy.dms.event.AjaxAuthenticationFailureHandler;
import cn.edu.tjnu.hxxy.dms.event.AuthenticationExpiredHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

/**
 * Spring Security 配置类。
 *
 * @author 王帅
 * @since 1.0
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(DataSource dataSource,
                             @Qualifier("userManager") UserDetailsService userDetailsService) {
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROOT > MANAGER > USER > GUEST");
        return roleHierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 解决 {@code UsernameNotFoundException} 不显示问题。
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) {
        // 前端资源过滤
        web.ignoring().antMatchers("/css/**", "/images/**", "/js/**", "/lib/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.sessionManagement()
                .maximumSessions(1)
                .expiredSessionStrategy(new AuthenticationExpiredHandler());
        http.formLogin()
                .loginProcessingUrl("/user/login")
                .loginPage("/login.html")
                .failureHandler(new AjaxAuthenticationFailureHandler());
        http.rememberMe()
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService);
        http.logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/login.html")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
        http.authorizeRequests()
                // Swagger2 文档只能 ROOT 权限用户访问
                .antMatchers("/swagger-ui.html", "/v2/api-docs").hasRole("ROOT")
                // 拒绝所有 /page/** 的请求
                .antMatchers("/page/**").denyAll()
                // 放行登陆之前的页面与请求
                .antMatchers("/login.html", "/register.html", "/user/register",
                        "/forget-password.html", "/user/forget-password/*",
                        "/reset-password.html", "/user/reset-password/*",
                        "/user/getCaptcha", "/user/checkup").permitAll()
                // 管理员与根用户能访问到的页面
                .antMatchers("/admin/**", "/edit/**").hasAnyRole("MANAGER", "ROOT")
                // 用户页面和首页需要认证
                .antMatchers("/user/**", "/", "/index.html").authenticated()
                // 其他请求一律认证
                .anyRequest().authenticated();
    }

}
