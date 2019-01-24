package com.mmall.utils;

import com.mmall.common.ShardedRedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by rabbit on 2019/1/3.
 */
@Slf4j
public class ShardedRedisPoolUtil {

    public static void set(String key, String value){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            String result = jedis.set(key, value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
    }

    public static String get(String key){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            String result = jedis.get(key);
            ShardedRedisPool.returnResource(jedis);
            return result;
        } catch (Exception e) {
            log.error("get %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static String getSet(String key, String value){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            String result = jedis.getSet(key, value);
            ShardedRedisPool.returnResource(jedis);
            return result;
        } catch (Exception e) {
            log.error("get %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static Long del(String key){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            Long result = jedis.del(key);
            ShardedRedisPool.returnResource(jedis);
            return result;
        } catch (Exception e) {
            log.error("get %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static void setex(String key, int timeout, String value){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            String result = jedis.setex(key, timeout, value);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set expire time for %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
    }

    public static Long setnx(String key, String value){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            Long result = jedis.setnx(key, value);
            ShardedRedisPool.returnResource(jedis);
            return result;
        } catch (Exception e) {
            log.error("set expire time for %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
        return null;
    }

    public static void expire(String key, int timeout){
        ShardedJedis jedis = ShardedRedisPool.getResource();
        try {
            Long result = jedis.expire(key, timeout);
            ShardedRedisPool.returnResource(jedis);
        } catch (Exception e) {
            log.error("set expire time for %s error.", key, e);
            ShardedRedisPool.returnBrokenResource(jedis);
        }
    }
}
