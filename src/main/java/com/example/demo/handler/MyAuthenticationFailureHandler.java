package com.example.demo.handler;

import com.example.demo.exception.CaptchaExpiredException;
import com.example.demo.exception.CaptchaWrongException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  public static final String NORMAL_ERROR_URL = "/toLogin?error";  // 校验错误
  public static final String CODE_ERROR_URL = "/toLogin?code";     // 验证码错误
  public static final String EXPIRED_ERROR_URL = "/toLogin?expired";     // 验证码过期

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

    if (exception instanceof CaptchaExpiredException){
      getRedirectStrategy().sendRedirect(request, response, EXPIRED_ERROR_URL);
    }
    else if (exception instanceof CaptchaWrongException) {
      getRedirectStrategy().sendRedirect(request, response, CODE_ERROR_URL);
    } else {
      getRedirectStrategy().sendRedirect(request, response, NORMAL_ERROR_URL);
    }
  }

}
