# Spring Security整合captcha登录

## Sql

```sql
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`                      INTEGER PRIMARY KEY AUTO_INCREMENT,
    `username`                VARCHAR(50) NOT NULL,
    `password`                VARCHAR(100) NOT NULL,
    `enabled`                 BOOLEAN DEFAULT TRUE,
    `account_non_expired`     BOOLEAN DEFAULT TRUE,
    `credentials_non_expired` BOOLEAN DEFAULT TRUE,
    `account_non_locked`      BOOLEAN DEFAULT TRUE,
    `create_time`             DATE,
    `update_time`             DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          INTEGER PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(30) NOT NULL,
    `create_time` DATE,
    `update_time` DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `role_user`;
CREATE TABLE `role_user`
(
    `id`          INTEGER PRIMARY KEY AUTO_INCREMENT,
    `user_id`     INTEGER REFERENCES `user` (id),
    `role_id`     INTEGER REFERENCES `role` (id),
    `create_time` DATE,
    `update_time` DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

建表逻辑如上。

在测试类有一个添加用户的方法，先运行一下，会自动添加不存在的用户组（role），记得在运行前修改一下yaml中的数据库连接地址。

说白了就是三个表，一个存放所有用户组role，一个存放用户组role和用户user的关系，一个存放用户user。

多表在查询和插入的时候都会略麻烦，用到了`LEFT JOIN`

## Captcha

验证码方面使用了google的本地验证码依赖，maven依赖如下

```xml
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>
```

接下来就是生成验证码，我们在config中添加`CaptchaConfig`配置类，东西都是死的，直接拿着用就行。

在编写好配置类后，我们写一个`Controller`来使用这个Captcha生成器。

写好之后，我们就可以这样调用这个captcha，注意数据是通过字节传过去的。

```html
<img src='/captcha'>
```

这里的部分基本也是死的，我在下面还添加了一个用来Ajax验证的controller。其中用到了ali的FasjJson，关于这个依赖也不多说，我的用法也比较简单（真好用）。

## SpringSecurity

这里我是听狂神的课学会的，但是狂神没有提过怎么从数据库验证用户信息，这里我提供一个方法，就是重写`UserDetailsService`，这里注意一点，这个类不能加注解来自动装配，因为内部已经有了一个了，我们只能靠配置类来加载。

### SpringSecurity实现数据库查找用户信息

我们首先写好正常的`SecurityConfig`，我这里就不多讲代码，注释很明白了。

这个时候，我们需要账号通过数据库内容进行验证，那么我们就要走`UserDetailsService`，这个类是什么意思呢？

这个类直译过来就是`用户细节服务`，我们观察他的返回值，是一个`org.springframework.security.core.userdetails.User`，打开源码，看到他继承了两个类，一个是`UserDetails`一个是`CredentialsContainer`，分别翻译一下，就是`用户细节`和`资格容器`，其实已经很明白了，这个User，不是一般的User，他是一个拥有**用户细节**和**资格**的**容器**。

![image-20210627001827370](.\imgs\zh\image-20210627001827370.png)

观察我们重写的`UserDetailsService`，他只有一个方法，且需要重写，是`UserDetails loadUserByUsername(String var1);`其实已经很明了了，输入一个用户名，返回一个用户细节，那么我们就可以通过这个方法，把我们的`用户细节`都加载好，返回到上面的`User(Details)`类。

![image-20210627001859178](.\imgs\zh\image-20210627001859178.png)

观察返回的`User`的方法，都是`get`方法，所以我们只能通过构造函数入手，有两个构造函数，一个参数多，一个参数少，仔细看，其实不变的参数有三个`username`、`password`、`authorities`。那个多的，里面有各种账户的状态，也很好理解，自己翻译一下英文就行。（少参数的构造就是默认全true的多参数构造，一个道理）

![image-20210627001956262](C:\Users\Administrator\Documents\Java\CodeBase\LoginSystem\notebooks\imgs\zh\image-20210627001956262.png)

这个时候，我们的思路就有了，说白了就是实现一个`UserDetailsService`然后重写`loadUserByUsername`方法，通过输入的username在数据库中找用户信息嘛。这里也不赘述了，无非就是mybatis在获得用户角色的时候用到了动态sql，mybatis不熟练的朋友可以好好复习一下了~

### SpringSecurity实现过滤器（Filter）

我们用了用户信息，就可以顺利登陆了。

等等！

我们的验证码没验证啊！

但是重写的方法只能输入一个参数啊，这可怎么办？

这个时候就要说到SpringSecurity的**过滤器链**，SpringSecurity有一个**过滤器链**，这就是SpringSecurity的核心之一。

我们需要自己写一个过滤器，然后添加到这个链里，实现我们在验证码正确之前，不查找用户信息，更保证了安全也保证了数据库服务器的压力。

细心的朋友已经发现我们的SpringSecurity配置类里多了一点东西，就是：

```java
// 开启验证码验证过滤器
http.addFilterBefore(captchaVerifyFilter, UsernamePasswordAuthenticationFilter.class);
```

这里有四个添加点，一个是After一个是Before一个是At，还有一个是直接添加，这四个的区别可以去搜一下其他资料，说的非常清楚了，关于过滤器链的执行顺序，添加位置。

我们在进行账号密码验证这个过滤器前添加一个验证码的过滤器，来实现验证码不过，不进行账号密码验证的目的。

过滤器思路也很简单，我们在进行`/login`路由（登录）的时候，开启这个过滤器，来验证我们的验证码输入是否正确，这里我要提一点，我对于验证码验证的时候都是在Redis里的，同时设置了5分钟的过期时间。

>  这里有一个弊端，就是多用户登录的时候，下一个用户会覆盖上一个用户的验证码，我们可以使用UUID作为保存验证码的key，这里我就没做，因为想实现其实很简单。

具体实现可以看一下源码，不难。

## Mybatis

其实这个项目除了SpringSecurity最耗时，就是Mybatis耗时，因为多表查询必然带来sql语句的复杂，在UserMapper里包含了<resultMap>和动态sql语句，对于这方面不熟悉或者不熟练的朋友可以去看看，查漏补缺。

## 小结

其他的也没什么好说的，我文笔也不咋样，欢迎大家参考并提出意见，我的微信masijun99，也欢迎大家加QQ群交流学习心得和困难：673215016（V哥的群）

