package com.example.demo;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.domain.Role;
import com.example.demo.pojo.domain.User;
import com.example.demo.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoginSystemApplicationTests {

	@Autowired
	UserService userService;

	@Test
	void testQueryUser() {
		System.out.println(userService.selectByUsername("masijun"));
	}

	/**
	 * 测试插入用户, 即一个接口insert()即可, 注意要加好roles
	 */
	@Test
	void  testInsertUser() {
		User user = new User("masijun", "19971003");
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("admin"));
		roles.add(new Role("root"));
		roles.add(new Role("guest"));
		user.setRoles(roles);
		userService.insert(user);
	}


}
