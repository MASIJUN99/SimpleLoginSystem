package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  // 这是json序列化的固定模板
  // 编写我们自己的RedisTemplate
  // 为了开发方便会使用<String, Object>泛型
  @Bean
  @SuppressWarnings("all")
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    // 配置我自己的序列化方式

    // 配置jackson序列化
    Jackson2JsonRedisSerializer<Object> Jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(DefaultTyping.NON_FINAL);
    Jackson2JsonRedisSerializer.setObjectMapper(om);
    // 配置String序列化
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    // key采用String的序列化方式
    template.setKeySerializer(stringRedisSerializer);
    // hash的key也采用String的序列化方式
    template.setHashKeySerializer(stringRedisSerializer);
    // value采用jackson的方式序列化
    template.setHashValueSerializer(Jackson2JsonRedisSerializer);
    // hash的value采用jackson的方式序列化
    template.setValueSerializer(Jackson2JsonRedisSerializer);
    // 配置
    template.afterPropertiesSet();

    return template;
  }


}
