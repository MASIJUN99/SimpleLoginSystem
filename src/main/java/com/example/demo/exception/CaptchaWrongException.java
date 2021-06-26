package com.example.demo.exception;

import org.springframework.security.core.AuthenticationException;

public class CaptchaWrongException extends AuthenticationException {

  public CaptchaWrongException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public CaptchaWrongException(String msg) {
    super(msg);
  }
}
