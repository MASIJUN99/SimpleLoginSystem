package com.example.demo.security.filter;


import com.example.demo.exception.CaptchaWrongException;
import com.example.demo.handler.MyAuthenticationFailureHandler;
import com.example.demo.utils.RedisUtil;
import com.google.code.kaptcha.Constants;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 本想使用此方法进行验证码校验
 * 现在改为ajax, 此配置作废
 */
@Component
public class CaptchaVerifyFilter extends OncePerRequestFilter {

  @Autowired
  RedisUtil redisUtil;

  private AuthenticationFailureHandler handler = new MyAuthenticationFailureHandler();


  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String code = request.getParameter("vercode"); // get input code
//    String captcha = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
    String captcha = (String) redisUtil.get(Constants.KAPTCHA_SESSION_KEY); // get captcha actually

    // only filter the "/login" routing
    if(!request.getRequestURI().equals("/login")){
      filterChain.doFilter(request,response);
    } else {
      try {
        if (captcha == null) {
          throw new CaptchaWrongException("验证码过期");  // Expired code
        } else if (captcha.equals(code)){
          filterChain.doFilter(request, response);
        } else {
          throw new CaptchaWrongException("验证码错误");  // Wrong code
        }
      } catch (AuthenticationException e) {
        handler.onAuthenticationFailure(request, response, e);
      }
    }
  }
}
