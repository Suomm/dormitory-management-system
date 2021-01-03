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

package xyz.tran4f.dms.config;

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
import xyz.tran4f.dms.handler.SaveUserAuthenticationSuccessHandler;

import javax.sql.DataSource;

/**
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
        // 启动时创建一张表，这个参数到第二次启动时必须注释掉，因为已经创建了一张表
        // jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROOT > MANAGER > USER");
        return roleHierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SaveUserAuthenticationSuccessHandler authenticationSuccessHandler() {
        SaveUserAuthenticationSuccessHandler handler = new SaveUserAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/manager/dashboard.html");
        // handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    /**
     * 解决异常 UsernameNotFoundException 不显示问题。
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
        web.ignoring().antMatchers("/favicon.ico", "/webjars/**",
                "/js/**", "/css/**", "/img/**", "/font/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true);
        http.formLogin()
                .loginProcessingUrl("/user/login")
                .loginPage("/user/login.html")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/user/login.html?error=true");
        http.rememberMe()
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService);
        http.logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/login.html")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
        http.authorizeRequests()
                .antMatchers("/swagger-ui.html", "/v2/api-docs")
                .hasRole("ROOT")
                .antMatchers("/user/register.html", "/user/register")
                .permitAll()
                .antMatchers("/user/forget_password.html", "/user/forget_password/*")
                .permitAll()
                .antMatchers("/", "/index.html", "/user/login.html",
                        "/user/reset_password/*/*", "/user/get_captcha")
                .permitAll()
                .anyRequest().authenticated();
    }

}
