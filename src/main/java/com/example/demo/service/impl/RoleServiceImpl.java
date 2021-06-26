package com.example.demo.service.impl;

import com.example.demo.mapper.RoleMapper;
import com.example.demo.pojo.domain.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  RoleMapper roleMapper;

  @Override
  public Role selectByName(String name) {
    Role role = roleMapper.selectByName(name);
    if (role != null) {
      return role;
    } else {
      role = new Role();
      role.setName(name);
      roleMapper.insert(role);
      return selectByName(name);
    }
  }

  @Override
  public int insert(Role role) {
    Role temp = roleMapper.selectByName(role.getName());
    if (temp != null) {
      return 0;
    } else {
      return roleMapper.insert(role);
    }
  }
}
