package com.example.demo.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.pojo.domain.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

  @Autowired
  private UserService userService;

  @RequestMapping("/user/success")
  public String success() {
    return "index";
  }

  @RequestMapping("/user/register")
  @ResponseBody
  public String register(String username, String password) {
    User user = new User(username, password);
    int insert = userService.insert(user);
    String msg = "success";
    if (insert == 0) {
      msg = "fail";
    }
    JSONObject json = new JSONObject();
    json.put("msg", msg);
    return json.toJSONString();
  }

  @RequestMapping("/reg/verifyUsername")
  @ResponseBody
  public String verifyUsername(String username) {
    User user = userService.selectByUsername(username);
    String msg;
    JSONObject json = new JSONObject();
    if (user == null) {
      msg = "valid";
    } else {
      msg = "invalid";
    }
    json.put("msg", msg);
    return json.toJSONString();
  }

}
