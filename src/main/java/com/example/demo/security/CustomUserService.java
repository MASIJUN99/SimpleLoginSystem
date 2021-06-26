package com.example.demo.security;


import com.example.demo.pojo.domain.Role;
import com.example.demo.pojo.domain.User;
import com.example.demo.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserService implements UserDetailsService {

  @Autowired
  UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.selectByUsername(username);
    System.out.println(username + " try to login in");
    if (user == null) {
      throw new UsernameNotFoundException("Username " + username + " not exist!");
    }

    List<SimpleGrantedAuthority> auths = new ArrayList<>(); // 角色
    List<Role> roles = user.getRoles();
    String prefix = "ROLE_";
    for (Role role : roles) {
      auths.add(new SimpleGrantedAuthority(prefix + role.getName()));  // 把角色加进去
    }


    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(),
        user.getEnabled(), user.getAccountNonExpired(), user.getCredentialsNonExpired(), user.getAccountNonLocked(),
        auths
    );
  }
}
