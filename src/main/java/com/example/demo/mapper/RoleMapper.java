package com.example.demo.mapper;

import com.example.demo.pojo.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RoleMapper {

  Role selectByName(String name);

  int insert(Role role);
}
