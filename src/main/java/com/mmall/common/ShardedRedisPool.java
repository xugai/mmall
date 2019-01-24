package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rabbit on 2019/1/1.
 */
public class ShardedRedisPool {
    private static ShardedJedisPool pool;
    private static int maxTotal = PropertiesUtil.getPropertyInInteger("redis.maxTotal", 20);
    private static int maxIdle = PropertiesUtil.getPropertyInInteger("redis.maxIdle", 10);
    private static int minIdle = PropertiesUtil.getPropertyInInteger("redis.minIdle", 5);
    private static boolean testOnBorrow = PropertiesUtil.getPropertyInBoolean("redis.testOnBorrow");
    private static boolean testOnReturn = PropertiesUtil.getPropertyInBoolean("redis.testOnReturn");

    // distributed redis cache config.
    private static String redis1_Ip = PropertiesUtil.getProperty("redis1.ip");
    private static int redis1_Port = PropertiesUtil.getPropertyInInteger("redis1.port");
    private static String redis2_Ip = PropertiesUtil.getProperty("redis2.ip");
    private static int redis2_Port = PropertiesUtil.getPropertyInInteger("redis2.port");

    private static void configJedisPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setBlockWhenExhausted(true);    //连接耗尽的时候是否阻塞, false不阻塞直接抛出异常; true则开始阻塞直到超时.

        JedisShardInfo jedisShardInfo1 = new JedisShardInfo(redis1_Ip, redis1_Port, 2 * 1000, 2);
        JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis2_Ip, redis2_Port, 2 * 1000);

        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(jedisShardInfo1);
        shards.add(jedisShardInfo2);

        /**
         * Hashing.MURMUR_HASH: 采用 consistent hashing 分布式算法.
         */
        pool = new ShardedJedisPool(poolConfig, shards, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        configJedisPool();
    }

    public static void returnResource(ShardedJedis resource){
        pool.returnResource(resource);
    }

    public static void returnBrokenResource(ShardedJedis resource){
        pool.returnBrokenResource(resource);
    }

    public static ShardedJedis getResource(){
        return pool.getResource();
    }

}
