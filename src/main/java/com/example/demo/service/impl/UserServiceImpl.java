package com.example.demo.service.impl;

import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.domain.Role;
import com.example.demo.pojo.domain.User;
import com.example.demo.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserMapper userMapper;
  @Autowired
  RoleMapper roleMapper;
  @Bean
  BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public User selectByUsername(String username) {
    return userMapper.selectByUsername(username);
  }

  /**
   * 面向用户的接口, 在用户进来时判断是否已存在, 如不存在加密密码加入数据库
   * @param user 用户(密码非加密)
   * @return 成功与否
   */
  @Override
  public int insert(User user) {
    User temp = userMapper.selectByUsername(user.getUsername());
    if (temp != null) {
      return 0;
    } else {
      user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
      return insertUserAndRoles(user);
    }
  }

  /**
   * 完整的添加逻辑, 包括了角色
   * @param user 用户账号信息
   * @return 成功与否
   */
  private int insertUserAndRoles(User user) {
    List<Role> roles = user.getRoles();
    if (roles == null || roles.size() == 0) {
      return userMapper.insert(user);
    } else {
      for (Role role : roles) {
        Role temp = roleMapper.selectByName(role.getName());
        if (temp == null) {
          roleMapper.insert(role);
          temp = roleMapper.selectByName(role.getName());
        }
        role.setId(temp.getId());
      }
      userMapper.insert(user);
      user.setId(userMapper.selectByUsername(user.getUsername()).getId());
      return userMapper.insertUserAndRoles(roles, user);
    }
  }


}
