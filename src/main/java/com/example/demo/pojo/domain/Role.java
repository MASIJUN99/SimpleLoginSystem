package com.example.demo.pojo.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

  private Integer id;
  private String name;
  private Date createTime = new Date();
  private Date updateTime = new Date();

  public Role(String name) {
    this.name = name;
  }
}
