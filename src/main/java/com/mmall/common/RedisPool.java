package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by rabbit on 2019/1/11.
 */
public class RedisPool {
    private static JedisPool jedisPool;
    private static int maxTotal = PropertiesUtil.getPropertyInInteger("redis.maxTotal", 20);
    private static int maxIdle = PropertiesUtil.getPropertyInInteger("redis.maxIdle", 10);
    private static int minIdle = PropertiesUtil.getPropertyInInteger("redis.minIdle", 5);
    private static boolean testOnBorrow = PropertiesUtil.getPropertyInBoolean("redis.testOnBorrow");
    private static boolean testOnReturn = PropertiesUtil.getPropertyInBoolean("redis.testOnReturn");
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static int redisPort = PropertiesUtil.getPropertyInInteger("redis.port");

    private static void configJedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort);
    }

    static{
        configJedisPool();
    }

    public static void returnResource(Jedis resource){
        jedisPool.returnResource(resource);
    }

    public static void returnBrokenResource(Jedis resource){
        jedisPool.returnBrokenResource(resource);
    }

    public static Jedis getResource(){
        return jedisPool.getResource();
    }

}
