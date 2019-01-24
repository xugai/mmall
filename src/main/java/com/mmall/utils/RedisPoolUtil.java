package com.mmall.utils;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created by rabbit on 2019/1/3.
 */
@Slf4j
public class RedisPoolUtil {

    public static void set(String key, String value){
        Jedis jedis = RedisPool.getResource();
        try {
            String result = jedis.set(key, value);
            RedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set %s error.", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
    }

    public static String get(String key){
        Jedis jedis = RedisPool.getResource();
        try {
            String result = jedis.get(key);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e) {
            log.error("get %s error.", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static void setex(String key, int timeout, String value){
        Jedis jedis = RedisPool.getResource();
        try {
            String result = jedis.setex(key, timeout, value);
            RedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set expire time for %s error.", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
    }

    public static void expire(String key, int timeout){
        Jedis jedis = RedisPool.getResource();
        try {
            Long result = jedis.expire(key, timeout);
            RedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set expire time for %s error.", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
    }
}
