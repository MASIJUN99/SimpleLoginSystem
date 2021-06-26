package com.example.demo.pojo.domain;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private Integer id;
  private String username;
  private String password;
  private Boolean enabled = true;                 // 是否可用
  private Boolean accountNonExpired = true;       // 是否过期
  private Boolean credentialsNonExpired = true;   // 许可是否过期
  private Boolean accountNonLocked = true;        // 是否锁定
  private List<Role> roles = null;
  private Date createTime = new Date();
  private Date updateTime = new Date();

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
