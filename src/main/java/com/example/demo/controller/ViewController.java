package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

  @RequestMapping("/toLogin")
  public String toLogin() {
    return "user/login";
  }

  @RequestMapping("/toSignup")
  public String toSignup() {
    return "user/signup";
  }

  @RequestMapping({"/toIndex", "/" , "/index", "/index.html"})
  public String toIndex() {
    return "index";
  }

}
