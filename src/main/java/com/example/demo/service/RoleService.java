package com.example.demo.service;

import com.example.demo.pojo.domain.Role;

public interface RoleService {

  Role selectByName(String name);

  int insert(Role role);

}
