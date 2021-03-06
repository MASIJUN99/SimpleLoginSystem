package com.example.demo.config;

import com.example.demo.security.CustomUserService;
import com.example.demo.security.filter.CaptchaVerifyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  // 加载自己重写的UserDetailsService
  @Bean
  CustomUserService customUserService() {
    return new CustomUserService();
  }
  // 加载验证码过滤器
  @Autowired
  CaptchaVerifyFilter captchaVerifyFilter;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserService()).passwordEncoder(new BCryptPasswordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/captcha", "/jquery-3.6.0.js", "/verifyCode");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/toLogin").permitAll()
        .antMatchers("/signup", "/toSignup").permitAll()
        .antMatchers("/index", "/", "/toIndex", "/index.html").permitAll()
        .antMatchers("/root/**").hasRole("root");

    // 没有权限，回跳到登录页。
    http.formLogin()
        .loginPage("/toLogin?locked").loginProcessingUrl("/login")
        .usernameParameter("username").passwordParameter("password")  // 自定义用户数据的变量名
        .defaultSuccessUrl("/")
        .failureUrl("/toLogin?error");  // ForwardUrl会转发, 导致路由到login, 再次刷新会导致404, 于是不用forward

    // 开启注销
    http.logout().logoutSuccessUrl("/");

    // 关闭跨站攻击CSRF
    http.csrf().disable();

    // 开启记住我功能，在cookies里保存2周
    http.rememberMe().rememberMeParameter("remember");

    // 开启验证码验证过滤器
    http.addFilterBefore(captchaVerifyFilter, UsernamePasswordAuthenticationFilter.class);
  }



}
