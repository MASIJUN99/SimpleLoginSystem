package com.example.demo.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  /**
   * Dto 用于更改密码时, 不对用户权限进行读取
   */
  private Integer id;
  private String username;
  private String password;

}
