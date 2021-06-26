package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface UserService {

  User selectByUsername(String username);

  int insert(User user);



}
