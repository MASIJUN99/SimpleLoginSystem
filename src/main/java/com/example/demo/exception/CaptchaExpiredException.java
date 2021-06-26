package com.example.demo.exception;

import org.springframework.security.core.AuthenticationException;

public class CaptchaExpiredException extends AuthenticationException {

  public CaptchaExpiredException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public CaptchaExpiredException(String msg) {
    super(msg);
  }
}
