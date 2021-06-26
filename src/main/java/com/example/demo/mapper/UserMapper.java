package com.example.demo.mapper;

import com.example.demo.pojo.domain.Role;
import com.example.demo.pojo.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

  User selectByUsername(String username);

  int insert(User user);

  int insertUserAndRoles(List<Role> roles, User user);

}
