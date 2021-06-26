package com.example.demo.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// 开发中的Redis的工具类
@Component
public class RedisUtil {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  /* ========================common======================== */

  /**
   * 指定key的缓存失效时间
   * @param key 键
   * @param time 时间（秒）
   * @return 是否成功
   */
  public boolean expire(String key, long time) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 获取指定key的缓存失效时间
   * @param key 键
   * @return 失效时间（秒），0表示永远有效
   */
  public long getExpire(String key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * 判断key是否存在
   * @param key 键
   * @return 是否存在
   */
  public boolean hasKey(String key) {
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 删除一个或多个key
   * @param key 键
   */
  public void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        for (String s : key) {
          redisTemplate.delete(s);
        }
      }
    } else {
      // 没有输入
    }
  }

  /* ========================String======================== */

  /**
   * 获取指定的值
   * @param key 键
   * @return 值
   */
  public Object get(String key) {
    return key == null ? null : redisTemplate.opsForValue().get(key);
  }

  /**
   * 存入键值
   * @param key 键
   * @param value 值
   * @return 是否成功
   */
  public boolean set(String key, Object value) {
    try {
      redisTemplate.opsForValue().set(key, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 存入键值及有效时间
   * @param key 键
   * @param value 值
   * @param time 时间（秒），0为永久
   * @return 是否成功
   */
  public boolean set(String key, Object value, long time) {
    try {
      if (time > 0) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        return true;
      } else {
        return set(key, value);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 递增
   * @param key 键
   * @param delta 递增值
   * @return
   */
  public long incr(String key, long delta) {
    if (delta < 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return redisTemplate.opsForValue().increment(key, delta);
  }
  public double incr(String key, double delta) {
    if (delta < 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return redisTemplate.opsForValue().increment(key, delta);
  }

  /**
   * 递减
   * @param key 键
   * @param delta 递减值
   * @return
   */
  public long decr(String key, long delta) {
    if (delta > 0) {
      throw new RuntimeException("递减因子必须小于0");
    }
    return redisTemplate.opsForValue().increment(key, -delta);
  }
  public double decr(String key, double delta) {
    if (delta > 0) {
      throw new RuntimeException("递减因子必须小于0");
    }
    return redisTemplate.opsForValue().increment(key, -delta);
  }

  /* =========================Map========================= */

  /**
   * Hash get
   * @param key 键 非空
   * @param item 项 非空
   * @return get结果
   */
  public Object hGet(String key, String item) {
    return redisTemplate.opsForHash().get(key, item);
  }

  /**
   * Hash set
   * @param key 键
   * @param item 项
   * @param value 值
   * @return 是否成功
   */
  public boolean hSet(String key, String item, Object value) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Hash set
   * @param key 键
   * @param item 项
   * @param value 值
   * @param time 有效时间（秒），会覆盖原有的有效时间
   * @return 是否成功
   */
  public boolean hSet(String key, String item, Object value, long time) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 获取hashKey所有的键值
   * @param key 键
   * @return 多个键值
   */
  public Map<Object, Object> hMapGet(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  /**
   * HashSet
   * @param key 键
   * @param map 对应的多个键值
   * @return 是否成功
   */
  public boolean hMapSet(String key, Map<String, Object> map) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * HashSet
   * @param key 键
   * @param map 对应的多个键值
   * @param time 设置有效时间
   * @return 是否成功
   */
  public boolean hMapSet(String key, Map<String, Object> map, long time) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 删除HashSet中的项对应的值
   * @param key 键
   * @param item 项
   */
  public void hDel(String key, Object... item) {
    redisTemplate.opsForHash().delete(key, item);
  }

  /**
   * 是否存在某项
   * @param key 键
   * @param item 项
   * @return 是否存在
   */
  public boolean hHasKey(String key, String item) {
    return redisTemplate.opsForHash().hasKey(key, item);
  }

  /**
   * hash 递增，若不存在会新建后返回
   * @param key 键
   * @param item 项
   * @param delta 递增值
   * @return
   */
  public double hIncr(String key, String item, long delta) {
    if (delta < 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return redisTemplate.opsForHash().increment(key, item, delta);
  }
  public double hIncr(String key, String item, double delta) {
    if (delta < 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return redisTemplate.opsForHash().increment(key, item, delta);
  }


  /**
   * hash 递减，若不存在会新建后返回
   * @param key 键
   * @param item 项
   * @param delta 递减值
   * @return
   */
  public double hDecr(String key, String item, long delta) {
    if (delta > 0) {
      throw new RuntimeException("递减因子必须小于0");
    }
    return redisTemplate.opsForHash().increment(key, item, -delta);
  }
  public double hDecr(String key, String item, double delta) {
    if (delta > 0) {
      throw new RuntimeException("递减因子必须小于0");
    }
    return redisTemplate.opsForHash().increment(key, item, -delta);
  }


  /* =========================Set========================= */

  /**
   * 根据key获取Set
   * @param key 键
   * @return 得到的Set
   */
  public Set<Object> sGet(String key) {
    try {
      return redisTemplate.opsForSet().members(key);
    } catch (RuntimeException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 查找Set中是否存在这个值
   * @param key 键
   * @param value 值
   * @return 是否存在
   */
  public boolean sHasKey(String key, Object value) {
    try {
      return redisTemplate.opsForSet().isMember(key, value);
    } catch (RuntimeException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将值放入Set
   * @param key 键
   * @param value 值
   * @return 0为失败
   */
  public long sSet(String key, Object... value) {
    try {
      return redisTemplate.opsForSet().add(key, value);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 将值放入Set
   * @param key 键
   * @param time 时间（秒）会覆盖原有的有效时间
   * @param value 值
   * @return 成功数量
   */
  public long sSet(String key, long time, Object... value) {
    try {
      Long count = redisTemplate.opsForSet().add(key, value);
      if (time > 0) {
        expire(key, time);
      }
      return count;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 获取Set大小
   * @param key 键
   * @return 大小
   */
  public long sSize(String key) {
    try {
      return redisTemplate.opsForSet().size(key);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 删除Set中的值
   * @param key 键
   * @param value 值
   * @return 成功个数
   */
  public long sRemove(String key, Object... value) {
    try {
      return redisTemplate.opsForSet().remove(key, value);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
  public long sRm(String key, Object... value) {
    return sRemove(key, value);
  }

  /* =========================List========================= */

  /**
   * 获取List中的内容
   * @param key 键
   * @param start 起始
   * @param end 结束
   * @return 内容
   */
  public List<Object> lGet(String key, long start, long end) {
    try {
      return redisTemplate.opsForList().range(key, start, end);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public List<Object> lGet(String key) {
    return lGet(key, 0, -1);
  }

  /**
   * 获取List大小
   * @param key 键
   * @return 大小
   */
  public long lSize(String key) {
    try {
      return redisTemplate.opsForList().size(key);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 获取List指定索引值
   * @param key 键
   * @param index 索引
   * @return 值
   */
  public Object lGetIndex(String key, long index) {
    try {
      return redisTemplate.opsForList().index(key, index);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 放入List
   * @param key 键
   * @param value 值
   * @return 是否成功
   */
  public boolean lSet(String key, Object value) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  public boolean lSet(String key, List<Object> value) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }


  /**
   * 放入List
   * @param key 键
   * @param value 值
   * @param time 有效时间（秒）
   * @return 是否成功
   */
  public boolean lSet(String key, Object value, long time) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  public boolean lSet(String key, List<Object> value, long time) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 更新List中索引的值
   * @param key 键
   * @param index 索引
   * @param value 值
   * @return 是否成功
   */
  public boolean lUpdateIndex(String key, long index, Object value) {
    try {
      redisTemplate.opsForList().set(key, index, value);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 移除count个value
   * @param key 键
   * @param count 数量
   * @param value 值
   * @return 成功移除的数量
   */
  public long lRemove(String key, long count, Object value) {
    try {
      long rm = redisTemplate.opsForList().remove(key, count, value);
      return rm;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }


}
